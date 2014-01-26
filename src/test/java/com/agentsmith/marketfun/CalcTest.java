package com.agentsmith.marketfun;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.agentsmith.marketfun.Calc.calcSimpleMovingAvg;
import static com.agentsmith.marketfun.Calc.setEMAsFor;
import static com.agentsmith.marketfun.Calc.setSMAsFor;
import static org.junit.Assert.assertEquals;

/**
 * Unit tests for Calc.
 * <p/>
 * User: rmarquez
 * Date: 1/26/2014
 * Time: 00:15
 */
public class CalcTest
{
    @Test
    public void testCalcSimpleMovingAvg() throws Exception
    {
        List<Bar> bars = new ArrayList<>(10);
        bars.add(new Bar("GOOG", new Date(), -1.0, -1.0, -1.0, 22.29, -1));
        bars.add(new Bar("GOOG", new Date(), -1.0, -1.0, -1.0, 22.24, -1));
        bars.add(new Bar("GOOG", new Date(), -1.0, -1.0, -1.0, 22.43, -1));
        bars.add(new Bar("GOOG", new Date(), -1.0, -1.0, -1.0, 22.23, -1));
        bars.add(new Bar("GOOG", new Date(), -1.0, -1.0, -1.0, 22.13, -1));
        bars.add(new Bar("GOOG", new Date(), -1.0, -1.0, -1.0, 22.18, -1));
        bars.add(new Bar("GOOG", new Date(), -1.0, -1.0, -1.0, 22.17, -1));
        bars.add(new Bar("GOOG", new Date(), -1.0, -1.0, -1.0, 22.08, -1));
        bars.add(new Bar("GOOG", new Date(), -1.0, -1.0, -1.0, 22.19, -1));
        bars.add(new Bar("GOOG", new Date(), -1.0, -1.0, -1.0, 22.27, -1));

        double SMA = calcSimpleMovingAvg(bars, 10);
        assertEquals("Should have been equal!", 22.22, SMA, 0.05);
    }

    @Test
    public void testSet10DayEMAs() throws Exception
    {
        int N_DAYS = 10;

        List<Bar> bars = new ArrayList<>(30);
        bars.add(0,  new Bar("GOOG", new Date(), -1.0, -1.0, -1.0, 22.17, -1));
        bars.add(1,  new Bar("GOOG", new Date(), -1.0, -1.0, -1.0, 22.40, -1));
        bars.add(2,  new Bar("GOOG", new Date(), -1.0, -1.0, -1.0, 23.10, -1));
        bars.add(3,  new Bar("GOOG", new Date(), -1.0, -1.0, -1.0, 22.68, -1));
        bars.add(4,  new Bar("GOOG", new Date(), -1.0, -1.0, -1.0, 23.33, -1));
        bars.add(5,  new Bar("GOOG", new Date(), -1.0, -1.0, -1.0, 23.10, -1));
        bars.add(6,  new Bar("GOOG", new Date(), -1.0, -1.0, -1.0, 23.19, -1));
        bars.add(7,  new Bar("GOOG", new Date(), -1.0, -1.0, -1.0, 23.65, -1));
        bars.add(8,  new Bar("GOOG", new Date(), -1.0, -1.0, -1.0, 23.87, -1));
        bars.add(9,  new Bar("GOOG", new Date(), -1.0, -1.0, -1.0, 23.82, -1));
        bars.add(10, new Bar("GOOG", new Date(), -1.0, -1.0, -1.0, 23.63, -1));
        bars.add(11, new Bar("GOOG", new Date(), -1.0, -1.0, -1.0, 23.95, -1));
        bars.add(12, new Bar("GOOG", new Date(), -1.0, -1.0, -1.0, 23.83, -1));
        bars.add(13, new Bar("GOOG", new Date(), -1.0, -1.0, -1.0, 23.75, -1));
        bars.add(14, new Bar("GOOG", new Date(), -1.0, -1.0, -1.0, 24.05, -1));
        bars.add(15, new Bar("GOOG", new Date(), -1.0, -1.0, -1.0, 23.36, -1));
        bars.add(16, new Bar("GOOG", new Date(), -1.0, -1.0, -1.0, 22.61, -1));
        bars.add(17, new Bar("GOOG", new Date(), -1.0, -1.0, -1.0, 22.38, -1));
        bars.add(18, new Bar("GOOG", new Date(), -1.0, -1.0, -1.0, 22.39, -1));
        bars.add(19, new Bar("GOOG", new Date(), -1.0, -1.0, -1.0, 22.15, -1));
        bars.add(20, new Bar("GOOG", new Date(), -1.0, -1.0, -1.0, 22.29, -1));
        bars.add(21, new Bar("GOOG", new Date(), -1.0, -1.0, -1.0, 22.24, -1));
        bars.add(22, new Bar("GOOG", new Date(), -1.0, -1.0, -1.0, 22.43, -1));
        bars.add(23, new Bar("GOOG", new Date(), -1.0, -1.0, -1.0, 22.23, -1));
        bars.add(24, new Bar("GOOG", new Date(), -1.0, -1.0, -1.0, 22.13, -1));
        bars.add(25, new Bar("GOOG", new Date(), -1.0, -1.0, -1.0, 22.18, -1));
        bars.add(26, new Bar("GOOG", new Date(), -1.0, -1.0, -1.0, 22.17, -1));
        bars.add(27, new Bar("GOOG", new Date(), -1.0, -1.0, -1.0, 22.08, -1));
        bars.add(28, new Bar("GOOG", new Date(), -1.0, -1.0, -1.0, 22.19, -1));
        bars.add(29, new Bar("GOOG", new Date(), -1.0, -1.0, -1.0, 22.27, -1));

        // These 3 bars should not be considered if the range of nDays is being honored.
        bars.add(30, new Bar("GOOG", new Date(), -1.0, -1.0, -1.0, 50.00, -1));
        bars.add(31, new Bar("GOOG", new Date(), -1.0, -1.0, -1.0, 60.00, -1));
        bars.add(32, new Bar("GOOG", new Date(), -1.0, -1.0, -1.0, 70.00, -1));


        setEMAsFor(bars, N_DAYS);

        assertEquals("Should have been equal!", 22.22, bars.get(20).EMAs.get(N_DAYS), 0.05);
        assertEquals("Should have been equal!", 22.21, bars.get(19).EMAs.get(N_DAYS), 0.05);
        assertEquals("Should have been equal!", 22.24, bars.get(18).EMAs.get(N_DAYS), 0.05);
        assertEquals("Should have been equal!", 22.27, bars.get(17).EMAs.get(N_DAYS), 0.05);
        assertEquals("Should have been equal!", 22.33, bars.get(16).EMAs.get(N_DAYS), 0.05);
        assertEquals("Should have been equal!", 22.52, bars.get(15).EMAs.get(N_DAYS), 0.05);
        assertEquals("Should have been equal!", 22.80, bars.get(14).EMAs.get(N_DAYS), 0.05);
        assertEquals("Should have been equal!", 22.97, bars.get(13).EMAs.get(N_DAYS), 0.05);
        assertEquals("Should have been equal!", 23.13, bars.get(12).EMAs.get(N_DAYS), 0.05);
        assertEquals("Should have been equal!", 23.28, bars.get(11).EMAs.get(N_DAYS), 0.05);
        assertEquals("Should have been equal!", 23.34, bars.get(10).EMAs.get(N_DAYS), 0.05);
        assertEquals("Should have been equal!", 23.43, bars.get(9).EMAs.get(N_DAYS), 0.05);
        assertEquals("Should have been equal!", 23.51, bars.get(8).EMAs.get(N_DAYS), 0.05);
        assertEquals("Should have been equal!", 23.54, bars.get(7).EMAs.get(N_DAYS), 0.05);
        assertEquals("Should have been equal!", 23.47, bars.get(6).EMAs.get(N_DAYS), 0.05);
        assertEquals("Should have been equal!", 23.40, bars.get(5).EMAs.get(N_DAYS), 0.05);
        assertEquals("Should have been equal!", 23.39, bars.get(4).EMAs.get(N_DAYS), 0.05);
        assertEquals("Should have been equal!", 23.26, bars.get(3).EMAs.get(N_DAYS), 0.05);
        assertEquals("Should have been equal!", 23.23, bars.get(2).EMAs.get(N_DAYS), 0.05);
        assertEquals("Should have been equal!", 23.08, bars.get(1).EMAs.get(N_DAYS), 0.05);
        assertEquals("Should have been equal!", 22.92, bars.get(0).EMAs.get(N_DAYS), 0.05);
    }

    @Test
    public void testSet10DaySMAs() throws Exception
    {
        int N_DAYS = 10;

        List<Bar> bars = new ArrayList<>(30);
        bars.add(0,  new Bar("GOOG", new Date(), -1.0, -1.0, -1.0, 22.17, -1));
        bars.add(1,  new Bar("GOOG", new Date(), -1.0, -1.0, -1.0, 22.40, -1));
        bars.add(2,  new Bar("GOOG", new Date(), -1.0, -1.0, -1.0, 23.10, -1));
        bars.add(3,  new Bar("GOOG", new Date(), -1.0, -1.0, -1.0, 22.68, -1));
        bars.add(4,  new Bar("GOOG", new Date(), -1.0, -1.0, -1.0, 23.33, -1));
        bars.add(5,  new Bar("GOOG", new Date(), -1.0, -1.0, -1.0, 23.10, -1));
        bars.add(6,  new Bar("GOOG", new Date(), -1.0, -1.0, -1.0, 23.19, -1));
        bars.add(7,  new Bar("GOOG", new Date(), -1.0, -1.0, -1.0, 23.65, -1));
        bars.add(8,  new Bar("GOOG", new Date(), -1.0, -1.0, -1.0, 23.87, -1));
        bars.add(9,  new Bar("GOOG", new Date(), -1.0, -1.0, -1.0, 23.82, -1));
        bars.add(10, new Bar("GOOG", new Date(), -1.0, -1.0, -1.0, 23.63, -1));
        bars.add(11, new Bar("GOOG", new Date(), -1.0, -1.0, -1.0, 23.95, -1));
        bars.add(12, new Bar("GOOG", new Date(), -1.0, -1.0, -1.0, 23.83, -1));
        bars.add(13, new Bar("GOOG", new Date(), -1.0, -1.0, -1.0, 23.75, -1));
        bars.add(14, new Bar("GOOG", new Date(), -1.0, -1.0, -1.0, 24.05, -1));
        bars.add(15, new Bar("GOOG", new Date(), -1.0, -1.0, -1.0, 23.36, -1));
        bars.add(16, new Bar("GOOG", new Date(), -1.0, -1.0, -1.0, 22.61, -1));
        bars.add(17, new Bar("GOOG", new Date(), -1.0, -1.0, -1.0, 22.38, -1));
        bars.add(18, new Bar("GOOG", new Date(), -1.0, -1.0, -1.0, 22.39, -1));
        bars.add(19, new Bar("GOOG", new Date(), -1.0, -1.0, -1.0, 22.15, -1));

        // These 3 bars should not be considered if the range of nDays is being honored.
        bars.add(20, new Bar("GOOG", new Date(), -1.0, -1.0, -1.0, 50.00, -1));
        bars.add(21, new Bar("GOOG", new Date(), -1.0, -1.0, -1.0, 60.00, -1));
        bars.add(22, new Bar("GOOG", new Date(), -1.0, -1.0, -1.0, 70.00, -1));


        setSMAsFor(bars, N_DAYS);

        assertEquals("Should have been equal!", 23.38, bars.get(9).SMAs.get(N_DAYS), 0.05);
        assertEquals("Should have been equal!", 23.53, bars.get(8).SMAs.get(N_DAYS), 0.05);
        assertEquals("Should have been equal!", 23.65, bars.get(7).SMAs.get(N_DAYS), 0.05);
        assertEquals("Should have been equal!", 23.71, bars.get(6).SMAs.get(N_DAYS), 0.05);
        assertEquals("Should have been equal!", 23.69, bars.get(5).SMAs.get(N_DAYS), 0.05);
        assertEquals("Should have been equal!", 23.61, bars.get(4).SMAs.get(N_DAYS), 0.05);
        assertEquals("Should have been equal!", 23.51, bars.get(3).SMAs.get(N_DAYS), 0.05);
        assertEquals("Should have been equal!", 23.43, bars.get(2).SMAs.get(N_DAYS), 0.05);
        assertEquals("Should have been equal!", 23.28, bars.get(1).SMAs.get(N_DAYS), 0.05);
        assertEquals("Should have been equal!", 23.13, bars.get(0).SMAs.get(N_DAYS), 0.05);
    }
}
