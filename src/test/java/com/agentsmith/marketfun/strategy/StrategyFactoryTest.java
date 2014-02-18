package com.agentsmith.marketfun.strategy;

import com.agentsmith.marketfun.TechnicalsFinderOptions;
import org.junit.Test;
import org.reflections.Reflections;

import java.util.Set;

import static java.lang.reflect.Modifier.isAbstract;
import static org.junit.Assert.*;

/**
 * Unit tests for StrategyFactory.
 * <p/>
 * User: rmarquez
 * Date: 2/17/14
 * Time: 2:59 PM
 */
public class StrategyFactoryTest
{
    @Test
    public void createStrategies() throws Exception
    {
        TechnicalsFinderOptions options = new TechnicalsFinderOptions();
        options.strategies.add("BollingerBand");
        options.strategies.add("MovingAverages");
        options.strategies.add("SlowStochastics");

        Set<OpportunityStrategy> strategies = StrategyFactory.createStrategies(options);
        for (OpportunityStrategy nextStrategy : strategies)
        {
            Strategy strategyAnnotation = nextStrategy.getClass().getAnnotation(Strategy.class);
            assertTrue(options.strategies.contains(strategyAnnotation.value()));
        }
    }

    @Test
    public void createAllStrategies() throws Exception
    {
        // By not specifying any strategy, the default behavior should be to create ALL of them
        Set<OpportunityStrategy> strategies = StrategyFactory.createStrategies(new TechnicalsFinderOptions());

        Reflections reflections = new Reflections(StrategyFactory.STRATEGIES_PACKAGE);
        Set<Class<? extends OpportunityStrategy>> strategySubTypes = reflections.getSubTypesOf(OpportunityStrategy.class);
        for (Class<? extends OpportunityStrategy> nextStrategyClass : strategySubTypes)
        {
            if (!isAbstract(nextStrategyClass.getModifiers()))
            {
                assertStrategiesContains(strategies, nextStrategyClass);
            }
        }
    }

    private void assertStrategiesContains(Set<OpportunityStrategy> strategies,
                                          Class<? extends OpportunityStrategy> strategyClass)
    {
        for (OpportunityStrategy nextStrategy : strategies)
        {
            Class<? extends OpportunityStrategy> nextStrategyClass = nextStrategy.getClass();
            if (nextStrategyClass  == strategyClass)
            {
                return;
            }
        }
        fail();
    }
}
