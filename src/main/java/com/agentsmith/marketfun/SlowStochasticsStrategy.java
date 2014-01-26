package com.agentsmith.marketfun;

import java.util.ArrayList;
import java.util.List;

import static com.agentsmith.marketfun.Calc.findHighestHigh;
import static com.agentsmith.marketfun.Calc.findLowestLow;
import static com.agentsmith.marketfun.Util.errToUser;

/**
 * Calculates Slow Stochastics for a set of bars and determines whether the %K is in an upward trend, realtive to the %D,
 * but where the %K is not above an upper bound.  By default, the upper bound is 25 (because anything below 20 that is
 * found should be considered interesting), but you might want to consider opportunities with an upper bound of say 55.
 * <p/>
 * Reference for how to calculate the %K and %D, for Stochastics: http://www.great-trades.com/Help/stochastics.htm
 * <p/>
 * User: rmarquez
 * Date: 1/16/2014
 * Time: 22:14
 */
public class SlowStochasticsStrategy implements OpportunityStrategy
{
    private final TechnicalsFinderOptions options;

    public SlowStochasticsStrategy(TechnicalsFinderOptions options)
    {
        this.options = options;
    }

    @Override
    public boolean isOpportunity(String symbol, List<Bar> bars)
    {
        int nBarsNeededForCalc = (options.slowStochKPeriods * 2) + options.slowStochDPeriods - 1;

        if (bars.size() < nBarsNeededForCalc)
        {
            errToUser(options, "\nNot enough data to calculate %K for the Slow Stochastic.\n");
            return false;
        }

        // Regular Stochastic Calculations

        List<Double> stochPercentKs = new ArrayList<>(nBarsNeededForCalc);
        for (int i = 0; i < options.slowStochKPeriods; i++)
        {
            double nextPercentK = calcStochPercentK(bars.subList(i, options.slowStochKPeriods + i));
            //errToUser(options, "[" + symbol + "] reg stoch %K[" + i + "] = " + nextPercentK);
            stochPercentKs.add(nextPercentK);
        }

        List<Double> stochPercentDs = new ArrayList<>(options.slowStochDPeriods);
        for (int i = 0; i < options.slowStochDPeriods; i++)
        {
            double nextPercentD = calcStochPercentD(stochPercentKs.subList(i, stochPercentKs.size()));
            //errToUser(options, "[" + symbol + "] reg stoch %D[" + i + "] = " + nextPercentD);
            stochPercentDs.add(nextPercentD);
        }

        // Slow Stochastic Calculations

        double slowStochPercentK = stochPercentDs.get(0);
        errToUser(options, "\n[" + symbol + "] SLOW STOCH %K = " + slowStochPercentK);

        double slowStochPercentDSum = 0.0;
        for (int i = 0; i < options.slowStochDPeriods; i++)
        {
            slowStochPercentDSum += stochPercentDs.get(i);
        }

        double slowStochPercentD = slowStochPercentDSum / options.slowStochDPeriods;
        errToUser(options, "[" + symbol + "] SLOW STOCH %D = " + slowStochPercentD);

        return (slowStochPercentK < options.maxSlowStochK)
                && (slowStochPercentK > slowStochPercentD);
    }

    @Override
    public StrategyWeight getWeight()
    {
        return StrategyWeight.HIGHEST;
    }

    private double calcStochPercentK(List<Bar> kBars)
    {
        double closeOfCurrBar = kBars.get(0).close;

        double highestHigh = findHighestHigh(kBars);
        double lowestLow = findLowestLow(kBars);

        return 100 * ((closeOfCurrBar - lowestLow) / (highestHigh - lowestLow));
    }

    private double calcStochPercentD(List<Double> stochPercentKs)
    {
        double stochPercentKSum = 0.0;
        for (int i = 0; i < options.slowStochDPeriods; i++)
        {
            stochPercentKSum += stochPercentKs.get(i);
        }

        return stochPercentKSum / options.slowStochDPeriods;
    }
}
