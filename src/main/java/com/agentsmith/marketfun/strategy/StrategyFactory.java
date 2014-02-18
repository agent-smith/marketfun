package com.agentsmith.marketfun.strategy;

import com.agentsmith.marketfun.TechnicalsFinderOptions;
import org.reflections.Reflections;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Creates strategies.
 * <p/>
 * User: rmarquez
 * Date: 2/17/14
 * Time: 12:45 PM
 */
public class StrategyFactory
{
    public static final String STRATEGIES_PACKAGE = "com.agentsmith.marketfun.strategy";
    public static final Set<String> STRATEGY_NAMES = findAllStrategies().keySet();

    private StrategyFactory()
    {
        // static class
    }

    public static Set<OpportunityStrategy> createStrategies(TechnicalsFinderOptions options)
    {
        Map<String, Class<OpportunityStrategy>> strategiesMap = findAllStrategies();

        Set<OpportunityStrategy> strategies = new HashSet<>();

        if (options.strategies.size() == 0)
        {
            for (String nextStrategyName : strategiesMap.keySet())
            {
                OpportunityStrategy strategy = createStrategy(strategiesMap.get(nextStrategyName), options);
                strategies.add(strategy);
            }
        }
        else
        {
            for (String nextStrategyName : options.strategies)
            {
                if (strategiesMap.containsKey(nextStrategyName))
                {
                    OpportunityStrategy strategy = createStrategy(strategiesMap.get(nextStrategyName), options);
                    strategies.add(strategy);
                }
            }
        }

        return strategies;
    }

    /**
     *
     * @return Map of strategy names to strategy classes (e.g. "BollingerBand"->BollingerBand.class
     */
    public static Map<String, Class<OpportunityStrategy>> findAllStrategies()
    {
        Map<String, Class<OpportunityStrategy>> strategyNameToClassMap = new HashMap<>();

        Reflections reflections = new Reflections(STRATEGIES_PACKAGE);

        Set<Class<? extends OpportunityStrategy>> strategySubTypes = reflections.getSubTypesOf(OpportunityStrategy.class);

        for (Class<?> nextStrategyClass : reflections.getTypesAnnotatedWith(Strategy.class))
        {
            //noinspection SuspiciousMethodCalls
            if (strategySubTypes.contains(nextStrategyClass))
            {
                Strategy strategyAnnotation = nextStrategyClass.getAnnotation(Strategy.class);
                //noinspection unchecked
                strategyNameToClassMap.put(strategyAnnotation.value(), (Class<OpportunityStrategy>) nextStrategyClass);
            }
        }

        return strategyNameToClassMap;
    }

    public static OpportunityStrategy createStrategy(Class<OpportunityStrategy> strategyClass,
                                                     TechnicalsFinderOptions options)
    {
        try
        {
            return strategyClass.getConstructor(TechnicalsFinderOptions.class).newInstance(options);
        }
        catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e)
        {
            throw new RuntimeException("Could not create strategy class: " + strategyClass.getSimpleName(), e);
        }
    }
}
