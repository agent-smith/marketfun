package com.agentsmith.marketfun.strategy;

import com.agentsmith.marketfun.Bar;
import com.agentsmith.marketfun.StrategyWeight;
import com.agentsmith.marketfun.TechnicalsFinderOptions;

import java.util.List;

import static com.agentsmith.marketfun.Calc.setEMAsFor;
import static com.agentsmith.marketfun.Calc.setSMAsFor;
import static com.agentsmith.marketfun.Util.errToUser;

/**
 *
 * <p/>
 * User: rmarquez
 * Date: 1/21/2014
 * Time: 23:48
 */
@Strategy("MovingAverages")
public class MovingAveragesStrategy extends AbstractOpportunityStrategy
{
    public MovingAveragesStrategy(TechnicalsFinderOptions options)
    {
        super(options);
    }

    @Override
    public boolean isOpportunity(String symbol, List<Bar> bars)
    {
        try
        {
            setSMAsFor(bars, 20);
            setSMAsFor(bars, 50);

            setEMAsFor(bars, 20);
        }
        catch (Throwable t)
        {
            errToUser(options, MovingAveragesStrategy.class.getSimpleName() + " skipping \"" + symbol + "\" since " +
                    "there was an issue calculating SMAs. " + t.getMessage());
            return false;
        }

        Bar currBar = bars.get(0);

        return (currBar.EMAs.get(20) > currBar.SMAs.get(20))
                && (currBar.EMAs.get(20) > currBar.SMAs.get(50))
                && (currBar.SMAs.get(20) > currBar.SMAs.get(50));
//                && (currBar.close > currBar.SMAs.get(50));
    }

    @Override
    public StrategyWeight getWeight()
    {
        return StrategyWeight.WEAKEST; // TODO: change after implement
    }
}
