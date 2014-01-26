package com.agentsmith.marketfun;

import java.util.List;

/**
 *
 * <p/>
 * User: rmarquez
 * Date: 1/21/2014
 * Time: 23:48
 */
public class MACDStrategy implements OpportunityStrategy
{
    private final TechnicalsFinderOptions options;

    public MACDStrategy(TechnicalsFinderOptions options)
    {
        this.options = options;
    }

    @Override
    public boolean isOpportunity(String symbol, List<Bar> bars)
    {
        return false; // TODO: impl me!
    }

    @Override
    public StrategyWeight getWeight()
    {
        return StrategyWeight.WEAKEST; // TODO: change after implement
    }
}
