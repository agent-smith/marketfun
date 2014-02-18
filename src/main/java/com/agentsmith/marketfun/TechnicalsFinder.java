package com.agentsmith.marketfun;

import com.agentsmith.marketfun.strategy.OpportunityStrategy;
import com.agentsmith.marketfun.strategy.OpportunityStrategyContext;
import com.beust.jcommander.JCommander;
import org.apache.commons.lang3.time.StopWatch;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.agentsmith.marketfun.SymbolFinder.findSymbols;
import static com.agentsmith.marketfun.Util.emailOpportunitiesIfNec;
import static com.agentsmith.marketfun.Util.errToUser;
import static com.agentsmith.marketfun.Util.outToUser;
import static com.agentsmith.marketfun.Util.prettyPrintResults;
import static com.agentsmith.marketfun.Util.waitForAllThreadsToComplete;
import static com.agentsmith.marketfun.strategy.StrategyFactory.createStrategies;
import static java.lang.Double.parseDouble;
import static java.lang.Integer.parseInt;

/**
 * Finds trade opportunities based on some set of technical analysis strategies.
 * <p/>
 * User: rmarquez
 * Date: 11/20/2013
 * Time: 21:44
 */
public class TechnicalsFinder
{
    public static final String HISTORICAL_DATA_FOR_SYMBOL_URL_PREFIX = "http://ichart.finance.yahoo.com/table.csv?s=";

    // SimpleDateFormat is not thread-safe, so use ThreadLocal to regulate access to static member.
    private static final ThreadLocal<DateFormat> YAHOO_DATE_FORMAT = new ThreadLocal<DateFormat>()
    {
        @Override
        protected DateFormat initialValue()
        {
            return new SimpleDateFormat("yyyy-MM-dd");
        }
    };

    private final TechnicalsFinderOptions options;

    public TechnicalsFinder(TechnicalsFinderOptions options)
    {
        this.options = options;
    }

    public static void main(String[] args) throws Exception
    {
        TechnicalsFinderOptions options = createOptionsFrom(args);

        TechnicalsFinder techFinder = new TechnicalsFinder(options);

        // TODO: Find a way to make a weighted result, instead of all or nothing opportunity.
        OpportunityStrategyContext opportunityStrategyCtx = getOpportunityStrategyContext(options);

        Set<String> opportunities = new TreeSet<>(techFinder.findOpportunities(opportunityStrategyCtx));

        String resultStr = prettyPrintResults(options, opportunities);
        outToUser(options, resultStr);

        emailOpportunitiesIfNec(options, resultStr);
    }

    private static TechnicalsFinderOptions createOptionsFrom(String... args)
    {
        TechnicalsFinderOptions options = new TechnicalsFinderOptions();
        new JCommander(options, args);
        return options;
    }

    private static OpportunityStrategyContext getOpportunityStrategyContext(TechnicalsFinderOptions options)
    {
        OpportunityStrategyContext opportunityStrategyCtx = new OpportunityStrategyContext(options);

        StringBuilder strategyNamesSB = new StringBuilder();

        Set<OpportunityStrategy> strategies = createStrategies(options);
        for (OpportunityStrategy nextStrategy : strategies)
        {
            opportunityStrategyCtx.addStrategy(nextStrategy);
            strategyNamesSB.append(nextStrategy.getClass().getSimpleName()).append(", ");
        }

        String strategyNames = strategyNamesSB.toString().substring(0, strategyNamesSB.length() - 2);
        outToUser(options, "Added " + strategies.size() + " strategies: " + strategyNames);
        return opportunityStrategyCtx;
    }

    private List<String> findOpportunities(final OpportunityStrategyContext opportunityStrategyCtx)
    {
        final Set<String> symbols = findSymbols(options);

        final List<String> symbolsNotFound = new ArrayList<>();
        final List<String> skippedSymbols = new ArrayList<>();
        final List<String> foundOpportunities = new ArrayList<>();

        ExecutorService executors = Executors.newFixedThreadPool(20);

        final CountDownLatch latch = new CountDownLatch(symbols.size());

        final Iterator<String> symbolIter = symbols.iterator();

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        for (int i = 0; i < symbols.size(); i++)
        {
            final int _i = i;

            executors.submit(new Runnable() {
                /**
                 */
                @Override
                public void run()
                {
                    try
                    {
                        String nextSymbol = symbolIter.next();

                        URL url = getUrlFor(nextSymbol);
                        if (url == null)
                        {
                            return;
                        }

                        if (symbols.size() < 100 || _i % 100 == 0)
                        {
                            outToUser(options,
                                      "\n[BEGIN-" + _i + "] Fetching historical data for symbol: " + nextSymbol);
                        }

                        List<Bar> bars = new ArrayList<>();
                        if (!populateBars(nextSymbol, url, bars))
                        {
                            return;
                        }

                        try
                        {
                            if (bars.get(0).close > options.minPrice)
                            {
                                if (opportunityStrategyCtx.isOpportunity(nextSymbol, bars))
                                {
                                    foundOpportunities.add(nextSymbol);
                                }
                            }
                        }
                        catch (Throwable t)
                        {
                            skippedSymbols.add(nextSymbol);
                            errToUser(options, "\nProblem while determining whether '" + nextSymbol +
                                    "' is a trading opportunity, so skipping. Error was: " +
                                    t.getMessage() + "\n");
                        }

                        if (_i % 100 == 0)
                        {
                            outToUser(options, "[END-" + _i + "] Fetching historical data for symbol: " + nextSymbol);
                        }
                    }
                    finally
                    {
                        latch.countDown();
                    }
                }

                /**
                 */
                private boolean populateBars(String nextSymbol, URL url, List<Bar> bars)
                {
                    /*
                        A CSV file for each symbol is downloaded in this format:
                        Date,Open,High,Low,Close,Volume,Adj Close
                        2013-11-21,31.14,31.18,31.14,31.18,900,31.18
                        2013-11-20,31.39,31.40,31.18,31.18,1900,31.18
                    */

                    try (BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream())))
                    {
                        br.readLine(); // skip the header

                        int lineNum = 1;
                        String line;
                        while ((line = br.readLine()) != null
                                && (lineNum - 1 < 1000))  // TODO: figure out what's a reasonable amount of data to collect without getting too crazy.
//                                && (lineNum - 1 < options.bollingerBandPeriods * 2))
                        {
                            String[] lineArr = line.split(",");
                            String dateStr = lineArr[0].trim();
                            Date date;
                            try
                            {
                                date = YAHOO_DATE_FORMAT.get().parse(dateStr);

                                if (options.startDate != null)
                                {
                                    if (date.compareTo(options.startDate) > 0)
                                    {
                                        continue;
                                    }
                                }
                            }
                            catch (Throwable t)
                            {
                                skippedSymbols.add(nextSymbol);
                                errToUser(options, "\nProblem with date format '" + dateStr + "' while reading " +
                                                   "line for symbol '" + nextSymbol + "', so skipping.\n");
                                return false;
                            }

                            Bar bar = createBar(nextSymbol, lineArr, date);
                            bars.add(bar);
                            //System.out.println("   Line " + lineNum + ": " + bar);

                            lineNum++;
                        }

                        if (lineNum < options.numTrendPeriods
                            || bars.size() < options.bollingerBandPeriods * 2)
                        {
                            errToUser(options, "\nNot enough historical data found in order to determine trading " +
                                               "opportunity for symbol: " + nextSymbol + "\n");
                            return false;
                        }
                    }
                    catch (Throwable t)
                    {
                        String errMsg = t.getMessage();

                        if (errMsg.contains(HISTORICAL_DATA_FOR_SYMBOL_URL_PREFIX))
                        {
                            symbolsNotFound.add(nextSymbol);
                            errToUser(options, "\nSymbol '" + nextSymbol + "' not provided by Yahoo! Finance, so skipping.\n");
                        }
                        else
                        {
                            skippedSymbols.add(nextSymbol);
                            errToUser(options, "\nProblem while reading line for symbol '" + nextSymbol +
                                               "', so skipping. Error was: " + errMsg + "\n");
                        }

                        return false;
                    }

                    return true;
                }

                /**
                 */
                private URL getUrlFor(String symbol)
                {
                    URL url;
                    try
                    {
                        url = new URL(HISTORICAL_DATA_FOR_SYMBOL_URL_PREFIX + symbol);
                    }
                    catch (MalformedURLException e)
                    {
                        skippedSymbols.add(symbol);
                        errToUser(options, "\nProblem while creating URL for symbol '" + symbol +
                                "', so skipping. Error was: " + e.getMessage() + "\n");
                        return null;
                    }
                    return url;
                }

                /**
                 */
                private Bar createBar(String nextSymbol, String[] lineArr, Date date)
                {
                    return new Bar(nextSymbol,
                                   date,
                                   parseDouble(lineArr[1]),
                                   parseDouble(lineArr[2]),
                                   parseDouble(lineArr[3]),
                                   parseDouble(lineArr[4]),
                                   parseInt(lineArr[5]));
                }
            });
        }

        waitForAllThreadsToComplete(latch, executors);

        stopWatch.stop();
        outToUser(options, "\nTime taken to find " + foundOpportunities.size() + " opportunities out of " +
                           symbols.size() + " total instruments: " + stopWatch.toString());

        if (options.debug)
        {
            if (!symbolsNotFound.isEmpty())
            {
                errToUser(options, "\nSymbols not found on Yahoo! Finance: " + symbolsNotFound);
            }

            if (!skippedSymbols.isEmpty())
            {
                errToUser(options, "\nSkipped the following symbols due to unknown error: " + skippedSymbols);
            }
        }

        return foundOpportunities;
    }
}
