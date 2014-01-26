package com.agentsmith.marketfun;

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
public class MovingAveragesStrategy implements OpportunityStrategy
{
    private final TechnicalsFinderOptions options;

    public MovingAveragesStrategy(TechnicalsFinderOptions options)
    {
        this.options = options;
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
