package com.agentsmith.marketfun;

import java.util.Collections;
import java.util.List;

import static java.lang.Math.atan2;
import static java.lang.Math.toDegrees;

/**
 * Calculator utilities.
 * <p/>
 * User: rmarquez
 * Date: 12/15/2013
 * Time: 00:03
 */
public class Calc
{
    public static double midpoint(double lo, double hi)
    {
        return lo + ((hi - lo) / 2);
    }

    public static double calcAngleOfLineBetweenTwoPoints(Point p1, Point p2)
    {
        return toDegrees(atan2(p2.y - p1.y, p2.x - p1.x));
    }

    public static double calcAvgVol(List<Bar> bars)
    {
        double sumVolume = 0.0;
        for (Bar nextBar : bars)
        {
            sumVolume += nextBar.volume;
        }

        return sumVolume / bars.size();
    }

    public static double calcSimpleMovingAvg(List<Bar> bars)
    {
        double sumCloses = 0.0;
        for (Bar nextBar : bars)
        {
            sumCloses += nextBar.close;
        }
        return sumCloses / bars.size();
    }

    // Assumes bars.get(0) is the current day.
    public static double calcSimpleMovingAvg(List<Bar> bars, int nDays)
    {
        double sumCloses = 0.0;

        // Natural ordering of bars is index 0 contains the current day
        // work our way from the current day, back nDays
        for (int i = 0; i < nDays; i++)
        {
            Bar nextBar = bars.get(i);
            sumCloses += nextBar.close;
        }
        return sumCloses / nDays;
    }

    // Assumes bars.get(0) is the current day.
    public static void setSMAsFor(List<Bar> bars, int nDays)
    {
        if (bars.size() < nDays * 2)
        {
            throw new RuntimeException("Need at least 2 times the amount of bars to calculate the " + nDays + "-day " +
                                       "SMA.");
        }

        for (int i = 0; i < nDays; i++)
        {
            Bar currBar = bars.get(i);
            currBar.SMAs.put(nDays, calcSimpleMovingAvg(bars.subList(i, i + nDays), nDays));
        }
    }

    // Assumes bars.get(0) is the current day.
    // Reference for calculating Exponential Moving Average (EMA): http://www.great-trades.com/Help/ma.htm
    public static void setEMAsFor(List<Bar> bars, int nDays)
    {
        if (bars.size() < nDays * 3)
        {
            throw new RuntimeException("Need at least 3 times the amount of bars to calculate the " + nDays + "-day " +
                                       "EMA.  This is because we have to calculate the " + nDays + "-day SMA for the " +
                                       "first data point, and then another " + nDays + "-day EMA before calculating " +
                                       "the " + nDays + "-day EMA from the current day.");
        }

        // Want to work from the past forward to the current day
        List<Bar> workingSetOfBars = bars.subList(0, nDays * 3);
        Collections.reverse(workingSetOfBars);

        Bar nthDayBar = workingSetOfBars.get(nDays - 1);
        if (!nthDayBar.SMAs.containsKey(nDays))
        {
            nthDayBar.SMAs.put(nDays, calcSimpleMovingAvg(workingSetOfBars, nDays));
        }

        nthDayBar.EMAs.put(nDays, nthDayBar.SMAs.get(nDays));

        double expPercentage = 2.0 / (nDays + 1);

        for (int i = nDays; i < workingSetOfBars.size(); i++)
        {
            Bar currBar = workingSetOfBars.get(i);
            Bar prevBar = workingSetOfBars.get(i-1);
            double currEMA = (currBar.close * expPercentage) + (prevBar.EMAs.get(nDays) * (1 - expPercentage));
            currBar.EMAs.put(nDays, currEMA);
        }

        Collections.reverse(workingSetOfBars);
    }

    public static double calcDeviation(List<Bar> bars, double movingAvg)
    {
        double sumCloseMinusMvgAvgSquared = 0D;
        for (Bar nextBar : bars)
        {
            sumCloseMinusMvgAvgSquared += Math.pow(nextBar.close - movingAvg, 2);
        }

        return Math.sqrt(sumCloseMinusMvgAvgSquared / bars.size());
    }

    public static double findHighestHigh(List<Bar> bars)
    {
        double maxHigh = Integer.MIN_VALUE;
        for (Bar nextBar : bars)
        {
            maxHigh = Math.max(maxHigh, nextBar.high);
        }
        return maxHigh;
    }

    public static double findLowestLow(List<Bar> bars)
    {
        double lowestLow = Integer.MAX_VALUE;
        for (Bar nextBar : bars)
        {
            lowestLow = Math.min(lowestLow, nextBar.low);
        }
        return lowestLow;
    }

}
