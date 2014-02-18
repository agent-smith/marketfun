package com.agentsmith.marketfun.strategy;

import com.agentsmith.marketfun.Bar;
import com.agentsmith.marketfun.TechnicalsFinderOptions;

import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import static com.agentsmith.marketfun.Util.isStable;
import static com.agentsmith.marketfun.Util.outToUser;
import static java.lang.String.format;

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

    private final Set<OpportunityStrategy> strategies;

    public OpportunityStrategyContext(TechnicalsFinderOptions options)
    {
        this(options, new TreeSet<>(new Comparator<OpportunityStrategy>() {
            @Override
            public int compare(OpportunityStrategy o1, OpportunityStrategy o2)
            {
                return o2.getWeight().ordinal() - o1.getWeight().ordinal();
            }
        }));
    }

    public OpportunityStrategyContext(TechnicalsFinderOptions options, Set<OpportunityStrategy> strategies)
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
            outToUser(options, "Found possible opportunity for \"" + symbol + "\" using " +
                               nextStrategy.getClass().getSimpleName());
        }

        boolean isStable = isStable(options, bars);

        outToUser(options,
                  format("All strategies passed and found opportunity for %s%s the configured maxPriceFluxAngle: %s",
                         symbol,
                         isStable ? " and falls below" : " but it does not fall below", options.maxPriceFluxAngle));

        return isStable;
    }
}
