package com.agentsmith.marketfun;

import com.agentsmith.marketfun.strategy.OpportunityStrategy;
import com.beust.jcommander.IParameterValidator;
import com.beust.jcommander.ParameterException;

import java.util.Map;

import static com.agentsmith.marketfun.strategy.StrategyFactory.findAllStrategies;

/**
 * Validator for email addresses.
 * <p/>
 * User: rmarquez
 * Date: 1/12/14
 * Time: 3:45 PM
 */
public class StrategyValidator implements IParameterValidator
{
    public void validate(String name, String value) throws ParameterException
    {
        Map<String, Class<OpportunityStrategy>> strategiesMap = findAllStrategies();

        String[] strategyNames = value.split(",");
        for (String nextStrategyName : strategyNames)
        {
            if (!strategiesMap.containsKey(nextStrategyName))
            {
                throw new ParameterException("\"" + value + "\" is not a valid strategy.  " +
                                                     "Must be one of the following: " + strategiesMap.keySet());
            }
        }
    }
}
