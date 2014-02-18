package com.agentsmith.marketfun.strategy;

import com.agentsmith.marketfun.Bar;
import com.agentsmith.marketfun.StrategyWeight;
import com.agentsmith.marketfun.TechnicalsFinderOptions;

import java.util.List;

/**
 *
 * <p/>
 * User: rmarquez
 * Date: 1/21/2014
 * Time: 23:48
 */
@Strategy("MACD")
public class MACDStrategy extends AbstractOpportunityStrategy
{
    public MACDStrategy(TechnicalsFinderOptions options)
    {
        super(options);
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
