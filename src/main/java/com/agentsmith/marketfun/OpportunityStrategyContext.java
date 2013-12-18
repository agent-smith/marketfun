package com.agentsmith.marketfun;

import java.util.ArrayList;
import java.util.List;

import static com.agentsmith.marketfun.Util.isStable;

/**
 * Provides a way to add multiple strategies and check if there's an opportunity to trade a given symbol, based on
 * executing all of the strategies and having _all_ of them return a positive result.
 * <p/>
 * If one strategy does not return a positive result, then the entire context is considered negative and
 * no opportunity exists.
 * <p/>
 * User: rmarquez
 * Date: 12/15/2013
 * Time: 00:11
 */
public class OpportunityStrategyContext
{
    private final TechnicalsFinderOptions options;

    private final List<OpportunityStrategy> strategies;

    public OpportunityStrategyContext(TechnicalsFinderOptions options)
    {
        this(options, new ArrayList<OpportunityStrategy>());
    }

    public OpportunityStrategyContext(TechnicalsFinderOptions options,
                                      List<OpportunityStrategy> strategies)
    {
        this.options = options;
        this.strategies = strategies;
    }

    public void addStrategy(OpportunityStrategy strategy)
    {
        strategies.add(strategy);
    }

    public boolean isOpportunity(String symbol, List<Bar> bars)
    {
        for (OpportunityStrategy nextStrategy : strategies)
        {
            if (!nextStrategy.isOpportunity(symbol, bars))
            {
                return false;
            }
        }
        return isStable(options, bars);
    }
}
