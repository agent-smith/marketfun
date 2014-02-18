package com.agentsmith.marketfun.strategy;

import com.agentsmith.marketfun.TechnicalsFinderOptions;

/**
 * Abstract OpportunityStrategy.
 * <p/>
 * User: rmarquez
 * Date: 2/17/14
 * Time: 1:00 PM
 */
public abstract class AbstractOpportunityStrategy implements OpportunityStrategy
{
    protected final TechnicalsFinderOptions options;

    public AbstractOpportunityStrategy(TechnicalsFinderOptions options)
    {
        this.options = options;
    }
}
