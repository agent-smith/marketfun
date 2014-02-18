package com.agentsmith.marketfun;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.converters.ISO8601DateConverter;
import com.beust.jcommander.validators.PositiveInteger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * All Options for running the TechnicalsFinder application.
 * <p/>
 * User: rmarquez
 * Date: 12/8/2013
 * Time: 20:28
 */
public class TechnicalsFinderOptions
{
    @Parameter(names = { "-strategies" },
               description = "Comma separated set of specific strategies to run (if not specified, then all " +
                             "strategies are run).",
               validateWith = StrategyValidator.class)
    public List<String> strategies = new ArrayList<>();

    @Parameter(names = { "-emailAddress", "-emailAddr", "-email" },
               description = "The email address to send possible opportunities to.",
               validateWith = MyEmailValidator.class)
    public String emailAddress;

    @Parameter(names = { "-exchanges", "-e" },
               description = "Comma separated set of exchanges that symbols will be downloaded from.")
    public List<String> exchanges = Arrays.asList("NYSE", "NASDAQ");

    @Parameter(names = { "-symbols", "-s" },
               description = "Comma separated set of specific symbols to analyze (instead of downloading and analyzing all symbols).")
    public List<String> symbols = new ArrayList<>();

    @Parameter(names = { "-numTrendPeriods", "-tp" },
               description = "Number of periods that should be considered for analyzing trends.  Defaults to 10.",
               validateWith = PositiveInteger.class)
    public Integer numTrendPeriods = 10;

    @Parameter(names = { "-startDate", "-sd" },
               description = "Date (yyyy-MM-dd) to start looking back in time for a trend.",
               converter = ISO8601DateConverter.class)
    public Date startDate;

    @Parameter(names = { "-bbPeriods", "-bbp" },
               description = "Number of periods used to calculate the Bollinger Bands for.",
               validateWith = PositiveInteger.class)
    public Integer bollingerBandPeriods = 20;

    @Parameter(names = { "-bbDeviation", "-bbd" },
               description = "Deviation used to calculate the Bollinger Bands.  " +
                             "Bollinger recommends the following settings: " +
                             "10 day period => 1.9; " +
                             "20 day period => 2.0; " +
                             "50 day period => 2.1")
    public Double bollingerBandDeviation = 2.0;

    @Parameter(names = { "-sskPeriods", "-ssk" },
               description = "Number of periods used to calculate the %K for the Slow Stochastic.  Default is 14",
               validateWith = PositiveInteger.class)
    public Integer slowStochKPeriods = 14;

    @Parameter(names = { "-ssdPeriods", "-ssd" },
               description = "Number of periods used to calculate the %D for the Slow Stochastic.  Default is 3.",
               validateWith = PositiveInteger.class)
    public Integer slowStochDPeriods = 3;

    @Parameter(names = { "-maxSSK", "-mssk" },
               description = "The maximum %K for the Slow Stochastic for it to be considered.  Default is 55.")
    public Double maxSlowStochK = 55.0;

    @Parameter(names = { "-maxPriceFluxAngle", "-mpf" },
               description = "The maximum angle that the high and low prices can change in numTrendPeriods.  The " +
                             "closer the angle is to 100, the more opportunities will be found, because it allows " +
                             "for greater fluctuations in high and low prices.")
    public Double maxPriceFluxAngle = 100.0;

    @Parameter(names = { "-epsilonPercentOfPrice", "-epp" },
               description = "A percentage that can be forgiven when looking for a trend over the past numTrendPeriods." +
                             "For example, if we're looking for prices within a certain range, and -epp 0.05 is " +
                             "provided, then any price within ((price * 0.05 / 100) - lower_bound) " +
                             "or ((price * 0.05 / 100) + upper_bound) will also be considered.")
    public Double epsilonPercentOfPrice = 0.0;

    @Parameter(names = { "-minPrice", "-mp" },
               description = "The minimum price the asset should be for it to be considered.")
    public Double minPrice = 0.0;

    @Parameter(names = { "-minVolume", "-mv" },
               description = "The minimum volume the asset should currently be trading at for it to be considered." +
                             "Defaults to 500K")
    public Double minVolume = 500000.0;

    @Parameter(names = { "-log", "-verbose" }, description = "Level of verbosity")
    public Integer verbose = 1;

    @Parameter(names = "-debug", description = "Debug mode")
    public boolean debug = false;

    @Override
    public String toString()
    {
        return "TechnicalsFinderOptions{" +
                "\n\temailAddress='" + emailAddress + '\'' +
                "\n\texchanges=" + exchanges +
                "\n\tstrategies=" + strategies +
                "\n\tsymbols=" + symbols +
                "\n\tnumTrendPeriods=" + numTrendPeriods +
                "\n\tstartDate=" + startDate +
                "\n\tbollingerBandPeriods=" + bollingerBandPeriods +
                "\n\tbollingerBandDeviation=" + bollingerBandDeviation +
                "\n\tslowStochKPeriods=" + slowStochKPeriods +
                "\n\tslowStochDPeriods=" + slowStochDPeriods +
                "\n\tmaxSlowStochK=" + maxSlowStochK +
                "\n\tmaxPriceFluxAngle=" + maxPriceFluxAngle +
                "\n\tepsilonPercentOfPrice=" + epsilonPercentOfPrice +
                "\n\tminPrice=" + minPrice +
                "\n\tminVolume=" + minVolume +
                "\n\tverbose=" + verbose +
                "\n\tdebug=" + debug +
                "\n}";
    }
}
