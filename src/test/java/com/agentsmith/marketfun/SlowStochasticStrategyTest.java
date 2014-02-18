package com.agentsmith.marketfun;

import com.agentsmith.marketfun.strategy.SlowStochasticsStrategy;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertTrue;

/**
 * Unit tests for SlowStochasticStrategy.
 * <p/>
 * User: rmarquez
 * Date: 1/17/2014
 * Time: 00:27
 */
public class SlowStochasticStrategyTest
{
    @Test
    public void testSlowStoch() throws Exception
    {
        TechnicalsFinderOptions options = new TechnicalsFinderOptions();
        options.maxSlowStochK = 100.0;
        options.slowStochKPeriods = 5;
        options.slowStochDPeriods = 3;

        SlowStochasticsStrategy slowStochStrategy = new SlowStochasticsStrategy(options);

        List<Bar> bars = new ArrayList<>();
        bars.add(new Bar("BLAH", new Date(), -1.0, 23.6, 23.0, 23.5, 1000000)); // current close = 23.5
        bars.add(new Bar("BLAH", new Date(), -1.0, 23.5, 20.0, 23.4, 1000000)); // lowest low = 20.0
        bars.add(new Bar("BLAH", new Date(), -1.0, 24.0, 23.0, 23.3, 1000000)); // highest high = 24.0
        bars.add(new Bar("BLAH", new Date(), -1.0, 23.3, 23.0, 23.2, 1000000));
        bars.add(new Bar("BLAH", new Date(), -1.0, 23.2, 23.0, 23.1, 1000000));
        bars.add(new Bar("BLAH", new Date(), -1.0, 23.1, 23.0, 23.0, 1000000));

        // Add extra bars just so we pass the validation part that checks
        // whether we have enough bars to start the calculation.
        bars.add(new Bar("BLAH", new Date(), -1.0, 23.1, 23.0, 23.0, 1000000));
        bars.add(new Bar("BLAH", new Date(), -1.0, 23.1, 23.0, 23.0, 1000000));
        bars.add(new Bar("BLAH", new Date(), -1.0, 23.1, 23.0, 23.0, 1000000));
        bars.add(new Bar("BLAH", new Date(), -1.0, 23.1, 23.0, 23.0, 1000000));
        bars.add(new Bar("BLAH", new Date(), -1.0, 23.1, 23.0, 23.0, 1000000));
        bars.add(new Bar("BLAH", new Date(), -1.0, 23.1, 23.0, 23.0, 1000000));

        assertTrue(slowStochStrategy.isOpportunity("BLAH", bars));
    }
}
