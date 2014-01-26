package com.agentsmith.marketfun;

/**
 * Contains the in-order weights so that they can be compared when determining which strategy to use first when
 * identifying opportunities.
 * <p/>
 * User: rmarquez
 * Date: 1/25/2014
 * Time: 13:21
 */
public enum StrategyWeight
{
    // Never use this one, because if we ever used this to multiply by some constant to get a weight relative to some
    // other strategy, then it would still be 0!
    WILL_NEVER_BE_USED_SINCE_ORDINAL_IS_ZERO,

    WEAKEST,
    WEAKER,
    WEAK,

    MID_LOW,
    MID,
    MID_HIGH,

    HIGH,
    HIGHER,
    HIGHEST;
}
