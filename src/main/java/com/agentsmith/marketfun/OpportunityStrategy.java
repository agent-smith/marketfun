package com.agentsmith.marketfun;

import java.util.List;

/**
 * Implement this for each strategy that searches for an opportunity within the given span of bars.
 * <p/>
 * User: rmarquez
 * Date: 12/15/2013
 * Time: 00:04
 */
public interface OpportunityStrategy
{
    boolean isOpportunity(String symbol, List<Bar> bars);
}
