package com.agentsmith.marketfun.old;

import com.agentsmith.marketfun.Bar;
import org.apache.commons.net.PrintCommandListener;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * // TODO: impl me!
 * <p/>
 * User: rmarquez
 * Date: 11/20/13
 * Time: 9:44 PM
 */
public class SymbolFetcherBB
{
    public static final String FTP_HOST = "ftp.nasdaqtrader.com";
    public static final String FTP_FILE_PATH = "SymbolDirectory/nasdaqlisted.txt";
    public static final String FTP_URL = "ftp://" + FTP_HOST + "/" + FTP_FILE_PATH;
    public static final String FTP_USER = "anonymous";
    public static final String FTP_PASS = "agent.smith.more@gmail.com";

    public static final String SYMBOLS_FILE_PATH = "/Users/rmarquez/tmp/nasdaqlisted.txt";

    public static final String HISTORICAL_DATA_FOR_SYMBOL_URL_PREFIX = "http://ichart.finance.yahoo.com/table.csv?s=";

    private static final int N_DAY_MOVING_AVG = 50;

    public static List<String> fetchSymbols() throws IOException
    {
//        return Arrays.asList("AAPL", "GOOG");

        FTPClient ftp = new FTPClient();
        ftp.addProtocolCommandListener(new PrintCommandListener(new PrintWriter(System.out)));
        ftp.connect(FTP_HOST);
        int reply = ftp.getReplyCode();
        if (!FTPReply.isPositiveCompletion(reply))
        {
            ftp.disconnect();
            throw new RuntimeException("Exception in connecting to FTP Server");
        }

        boolean success = ftp.login(FTP_USER, FTP_PASS);
        success &= ftp.setFileType(FTP.ASCII_FILE_TYPE);
        ftp.enterLocalPassiveMode();
        try (FileOutputStream fos = new FileOutputStream(SYMBOLS_FILE_PATH))
        {
            success &= ftp.retrieveFile(FTP_FILE_PATH, fos);
        }

        if (!success)
        {
            throw new RuntimeException("Problem encountered when trying to download file via FTP.");
        }

        if (ftp.isConnected())
        {
            try
            {
                ftp.logout();
                ftp.disconnect();
            } catch (IOException f)
            {
                // do nothing since file is already downloaded at this point
            }
        }

        /*
            The symbols file should contain approx. 2700 lines in this format:
            Symbol|Security Name|Market Category|Test Issue|Financial Status|Round Lot Size
            AAIT|iShares MSCI All Country Asia Information Technology Index Fund|G|N|N|100
            AAME|Atlantic American Corporation - Common Stock|G|N|N|100
        */

        List<String> symbols = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(SYMBOLS_FILE_PATH)))
        {
            br.readLine(); // skip the first line

            String line;
            while ((line = br.readLine()) != null)
            {
                if (line.startsWith("File Creation Time"))
                {
                    break;
                }

                symbols.add(line.split("\\|")[0]);
            }
        }

        return symbols;
    }

    public static final SimpleDateFormat YAHOO_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    // Bollinger recommends the following settings:
    // 10 day period => 1.9
    // 20 day period => 2.0
    // 50 day period => 2.1
    public static final double N_BOLLINGER_BAND_DEVIATIONS = 1.9;

    public static void main(String[] args) throws Exception
    {
        final int nPeriods = Integer.parseInt(args[0]);
        if (nPeriods < 1)
        {
            throw new RuntimeException("First arg (number of bars) must be greater than 0.");
        }

        System.out.println("[BEGIN] Downloading symbols from: " + FTP_URL);
        final List<String> symbols = fetchSymbols();
        System.out.println("[END] Downloading symbols from: " + FTP_URL);

        final List<String> skippedSymbols = new ArrayList<>();
        final List<String> symbolsBreakingAboveMiddleBand = new ArrayList<>();
        final List<String> symbolsBreakingAboveUpperBand = new ArrayList<>();

        ExecutorService executors = Executors.newFixedThreadPool(20);

        final CountDownLatch latch = new CountDownLatch(symbols.size());

        for (int i = 0; i < symbols.size(); i++)
        {
            final int finalI = i;
            executors.submit(new Runnable()
            {
                @Override
                public void run()
                {
                    String nextSymbol = symbols.get(finalI);

                    URL url;
                    try
                    {
                        url = new URL(HISTORICAL_DATA_FOR_SYMBOL_URL_PREFIX + nextSymbol);
                    }
                    catch (MalformedURLException e)
                    {
                        skippedSymbols.add(nextSymbol);
                        System.err.println("Problem while reading line for symbol '" + nextSymbol + "', so skipping.");
                        return;
                    }

                    System.out.println("[BEGIN-" + finalI + "] Fetching historical data for symbol: " + nextSymbol);

                    /*
                        A CSV file for each symbol is downloaded in this format:
                        Date,Open,High,Low,Close,Volume,Adj Close
                        2013-11-21,31.14,31.18,31.14,31.18,900,31.18
                        2013-11-20,31.39,31.40,31.18,31.18,1900,31.18
                    */

                    // TODO: http://www.great-trades.com/Help/bollinger%20bands%20calculation.htm

                    double sumVolume = 0;

                    try (BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream(), "UTF-8")))
                    {
                        br.readLine(); // skip the header

                        List<Bar> bars = new ArrayList<>();

                        int lineNum = 1;
                        String line;
                        while ((line = br.readLine()) != null && (lineNum - 1 < N_DAY_MOVING_AVG))
                        {
                            String[] lineArr = line.split(",");
                            String dateStr = lineArr[0];
                            if (!is2013Or2014(dateStr))
                            {
                                break;
                            }
                            Date date;
                            try
                            {
                                date = YAHOO_DATE_FORMAT.parse(dateStr);

                            }
                            catch (ParseException e)
                            {
                                skippedSymbols.add(nextSymbol);
                                System.err.println("Problem with date format '" + dateStr + "' while reading line " +
                                                           "for symbol '" + nextSymbol + "', so skipping.");
                                continue;
                            }


                            double open = Double.parseDouble(lineArr[1]);
                            double high = Double.parseDouble(lineArr[2]);
                            double low = Double.parseDouble(lineArr[3]);
                            double close = Double.parseDouble(lineArr[4]);
                            int volume = Integer.parseInt(lineArr[5]);

                            Bar bar = new Bar(nextSymbol, date, open, high, low, close, volume);
                            bars.add(bar);
                            System.out.println("   Line " + lineNum + ": " + bar);

                            sumVolume += volume;

                            lineNum++;
                        }

                        if (lineNum < nPeriods)
                        {
                            System.err.println("Not enough historical data found in order to determine trading " +
                                               "opportunity for symbol: " + nextSymbol);
                            return;
                        }

                        double avgVolume = sumVolume / N_DAY_MOVING_AVG;
                        System.out.println(N_DAY_MOVING_AVG + " Day Avg Volume = " + avgVolume);


                        double movingAvg = calcMovingAvg(bars, nPeriods);
                        double deviation = calcDeviation(bars, movingAvg, nPeriods);


                        double upperBollingerBand = movingAvg + (N_BOLLINGER_BAND_DEVIATIONS * deviation);
                        System.out.println("upperBollingerBand = " + upperBollingerBand);

                        System.out.println("middleBollingerBand = " + movingAvg);

                        double lowerBollingerBand = movingAvg - (N_BOLLINGER_BAND_DEVIATIONS * deviation);
                        System.out.println("lowerBollingerBand = " + lowerBollingerBand);

                        if (bars.size() < nPeriods)
                        {
                            System.err.println("Not enough historical data found in order to determine trading " +
                                               "opportunity for symbol: " + nextSymbol);
                            return;
                        }

                        Bar yesterdayBar = bars.get(0);

                        List<Bar> lastNPeriodsBeforeYesterday = bars.subList(1, nPeriods+1);

                        double epsilonPercent;
                        if (upperBollingerBand - lowerBollingerBand <= 0.5)
                        {
                            epsilonPercent = 0.0;
                        }
                        else if (upperBollingerBand - lowerBollingerBand <= 1)
                        {
                            epsilonPercent = 0.25;
                        }
                        else if (upperBollingerBand - lowerBollingerBand <= 2)
                        {
                            epsilonPercent = 0.5;
                        }
                        else if (upperBollingerBand - lowerBollingerBand <= 3)
                        {
                            epsilonPercent = 0.75;
                        }
                        else if (upperBollingerBand - lowerBollingerBand <= 5)
                        {
                            epsilonPercent = 1.0;
                        }
                        else
                        {
                            epsilonPercent = 1.5;
                        }

                        if (yesterdayBar.volume > avgVolume
                            && yesterdayBar.close > upperBollingerBand
                            && betweenBoundsForLastNPeriods(lastNPeriodsBeforeYesterday,
                                                            movingAvg,
                                                            upperBollingerBand,
                                                            epsilonPercent))
                        {
                            System.out.println(nextSymbol + " BROKE OUT ABOVE UPPER BAND!!!");
                            symbolsBreakingAboveUpperBand.add(nextSymbol);
                        }
                        else if (yesterdayBar.volume > avgVolume
                                 && yesterdayBar.close > movingAvg
                                 && betweenBoundsForLastNPeriods(lastNPeriodsBeforeYesterday,
                                                                 lowerBollingerBand,
                                                                 movingAvg,
                                                                 epsilonPercent))
                        {
                            System.out.println(nextSymbol + " BROKE OUT ABOVE MIDDLE BAND!!!");
                            symbolsBreakingAboveMiddleBand.add(nextSymbol);
                        }


                        System.out.println("Yesterday's volume > Avg Volume?: " + (yesterdayBar.volume > avgVolume));

                    }
                    catch (Throwable t)
                    {
                        skippedSymbols.add(nextSymbol);
                        System.err.println("Problem while reading line for symbol '" + nextSymbol + "', so skipping.");
                    }
                    finally
                    {
                        latch.countDown();
                    }

                    System.out.println("[END-" + finalI + "] Fetching historical data for symbol: " + nextSymbol);
                }

                private double calcMovingAvg(List<Bar> bars, int nPeriods)
                {
                    double sumCloses = 0D;
                    for (int i = 0; i < nPeriods; i++)
                    {
                        Bar nextBar = bars.get(i);
                        sumCloses += nextBar.close;
                    }
                    return sumCloses / nPeriods;
                }

                private double calcDeviation(List<Bar> bars, double movingAvg, int nPeriods)
                {
                    double sumCloseMinusMvgAvgSquared = 0D;
                    for (int i = 0; i < nPeriods; i++)
                    {
                        Bar nextBar = bars.get(i);
                        sumCloseMinusMvgAvgSquared += Math.pow(nextBar.close - movingAvg, 2);
                    }

                    return Math.sqrt(sumCloseMinusMvgAvgSquared / nPeriods);
                }

                private boolean betweenBoundsForLastNPeriods(List<Bar> bars,
                                                             double lowerBound,
                                                             double upperBound,
                                                             double epsilonPercent)
                {
                    for (Bar nextBar : bars)
                    {
                        double lowerBoundWithEpsilon = lowerBound - (lowerBound * epsilonPercent/100);
                        double upperBoundWithEpsilon = upperBound + (upperBound * epsilonPercent/100);

                        if (nextBar.close <= lowerBoundWithEpsilon || nextBar.close >= upperBoundWithEpsilon
                            || nextBar.open <= lowerBoundWithEpsilon || nextBar.open >= upperBoundWithEpsilon)
                        {
                            return false;
                        }
                    }
                    return true;
                }
            });
        }

        latch.await();

        if (!skippedSymbols.isEmpty())
        {
            System.err.println("Skipped the following symbols: " + skippedSymbols);
        }

        if (!symbolsBreakingAboveMiddleBand.isEmpty())
        {
            System.out.println("Broke out above middle band: " + symbolsBreakingAboveMiddleBand);
        }

        if (!symbolsBreakingAboveUpperBand.isEmpty())
        {
            System.out.println("Broke out above upper band: " + symbolsBreakingAboveUpperBand);
        }

        executors.shutdown();
    }

    private static boolean is2013Or2014(String date)
    {
        return date.contains("2013") || date.contains("-13")
                || date.contains("2014") || date.contains("-14");
    }
}
