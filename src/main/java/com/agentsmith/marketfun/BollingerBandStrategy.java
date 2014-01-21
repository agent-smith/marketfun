package com.agentsmith.marketfun;

import java.util.List;

import static com.agentsmith.marketfun.Calc.*;
import static com.agentsmith.marketfun.Util.betweenInclusive;
import static com.agentsmith.marketfun.Util.errToUser;
import static com.agentsmith.marketfun.Util.outToUser;

/**
 * Calculates Bollinger Bands for a set of bars and determines whether a symbol was trading within the upper/lower
 * band for a given period.
 * <p/>
 * Reference for how to calculate the Bollinger Bands: http://www.great-trades.com/Help/bollinger%20bands%20calculation.htm
 * <p/>
 * User: rmarquez
 * Date: 12/15/2013
 * Time: 00:10
 */
public class BollingerBandStrategy implements OpportunityStrategy
{
    private final TechnicalsFinderOptions options;

    public BollingerBandStrategy(TechnicalsFinderOptions options)
    {
        this.options = options;
    }

    @Override
    public boolean isOpportunity(String symbol, List<Bar> bars)
    {
        setBollingerBandsFor(options.bollingerBandPeriods, options.bollingerBandDeviation, bars);

        Bar prevDayBar = bars.get(0);
        List<Bar> lastNBarsBeforePrevDay = bars.subList(1, options.numTrendPeriods + 1);

        double avgVolume = calcAvgVol(bars);

        StringBuilder resultsSB = new StringBuilder(symbol);
        try
        {
            if (meetsVolumeConditions(prevDayBar, avgVolume)
                && brokeOutAboveUpperBand(prevDayBar))
            {
                return isBetweenBollingerBands(true, lastNBarsBeforePrevDay, bars, resultsSB);
            }
            else if (meetsVolumeConditions(prevDayBar, avgVolume)
                     && brokeOutAboveMiddleBand(prevDayBar))
            {
                return isBetweenBollingerBands(false, lastNBarsBeforePrevDay, bars, resultsSB);
            }
        }
        finally
        {
            if (options.debug)
            {
                String results = resultsSB.toString();
                if (!results.equals(symbol))
                {
                    if (results.contains("was NOT trading sideways"))
                    {
                        errToUser(options, "\n" + results);
                    }
                    else if (results.contains("WAS trading sideways"))
                    {
                        outToUser(options, "\n" + results);
                    }
                }
            }
        }
        return false;
    }

    private boolean isBetweenBollingerBands(boolean evaluatingUpperBand,
                                            List<Bar> lastNBarsBeforePrevDay,
                                            List<Bar> bars,
                                            StringBuilder resultsSB)
    {
        resultsSB.append(" BROKE OUT ABOVE ").append(evaluatingUpperBand ? "__UPPER__" : "__MIDDLE__")
                .append(" BAND, with following data:\n")
                .append("\t").append(bars.subList(0, options.numTrendPeriods));

        if (betweenBollingerBands(lastNBarsBeforePrevDay, evaluatingUpperBand))
        {
            resultsSB.append("\n\t... and WAS trading sideways for ").append(options.numTrendPeriods)
                    .append(" periods!!!!!!!!!!!!!!!!!!!!");
            return true;
        }
        else
        {
            resultsSB.append("\n\t... but was NOT trading sideways for ").append(options.numTrendPeriods)
                    .append(" periods.");
            return false;
        }
    }

    public void setBollingerBandsFor(int bollingerBandPeriods,
                                     double bollingerBandDeviation,
                                     List<Bar> bars)
    {
        for (int i = 0; i < bollingerBandPeriods; i++)
        {
            double movingAvg = calcMovingAvg(bars.subList(i, i + bollingerBandPeriods));
            double deviation = calcDeviation(bars.subList(i, i + bollingerBandPeriods), movingAvg);

            Bar nextBar = bars.get(i);
            nextBar.upperBollingerBand = movingAvg + (bollingerBandDeviation * deviation);
            nextBar.middleBollingerBand = movingAvg;
            nextBar.lowerBollingerBand = movingAvg - (bollingerBandDeviation * deviation);
        }
    }

    private boolean meetsVolumeConditions(Bar bar, double avgVolume)
    {
        return (bar.volume >= options.minVolume) && (bar.volume > avgVolume);
    }

    private boolean brokeOutAboveUpperBand(Bar bar)
    {
        return bar.open > bar.middleBollingerBand
               && bar.open <= bar.upperBollingerBand
               && bar.close > bar.upperBollingerBand;
    }

    private boolean brokeOutAboveMiddleBand(Bar bar)
    {
        return bar.open > bar.lowerBollingerBand
               && bar.open <= bar.middleBollingerBand
               && bar.close > bar.middleBollingerBand;
    }

    public boolean betweenBollingerBands(List<Bar> bars, boolean evaluatingUpperBand)
    {
        if (bars.size() < 2)
        {
            throw new RuntimeException("Not enough data to determine if some bars are between Bollinger Bands.");
        }

        for (Bar nextBar : bars)
        {
            if (nextBar.upperBollingerBand < 0
                    || nextBar.middleBollingerBand < 0
                    || nextBar.lowerBollingerBand < 0)
            {
                throw new RuntimeException("The following bar does not have one of the " +
                                                   "Bollinger Bands set: " + nextBar);
            }

            double upperBound = evaluatingUpperBand ? nextBar.upperBollingerBand : nextBar.middleBollingerBand;
            double lowerBound = evaluatingUpperBand ? nextBar.middleBollingerBand : nextBar.lowerBollingerBand;

            double epsilon = nextBar.close * options.epsilonPercentOfPrice / 100;

            double upperBoundWithEpsilon = upperBound + epsilon;
            double lowerBoundWithEpsilon = lowerBound - epsilon;

            if (!betweenInclusive(lowerBoundWithEpsilon, nextBar.open, upperBoundWithEpsilon)
                    || !betweenInclusive(lowerBoundWithEpsilon, nextBar.close, upperBoundWithEpsilon))
            {
                return false;
            }
        }
        return true;
    }
}
