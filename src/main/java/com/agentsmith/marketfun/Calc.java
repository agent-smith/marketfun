package com.agentsmith.marketfun;

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

    public static double calcMovingAvg(List<Bar> bars)
    {
        double sumCloses = 0.0;
        for (Bar nextBar : bars)
        {
            sumCloses += nextBar.close;
        }
        return sumCloses / bars.size();
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
