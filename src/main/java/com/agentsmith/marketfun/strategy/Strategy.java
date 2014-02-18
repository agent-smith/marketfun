package com.agentsmith.marketfun.strategy;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates that the class is a valid Strategy used to look for opportunities in market data.
 * <p/>
 * User: rmarquez
 * Date: 2/17/14
 * Time: 1:53 PM
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Strategy
{
    String value();
}
