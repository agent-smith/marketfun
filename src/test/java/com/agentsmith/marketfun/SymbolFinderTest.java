package com.agentsmith.marketfun;

import org.junit.Test;

import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertTrue;

/**
 * Unit tests for UrlSymbolReader.
 * <p/>
 * User: rmarquez
 * Date: 12/7/13
 * Time: 11:41 AM
 */
public class SymbolFinderTest
{
    @Test
    public void testReadAllSymbols_NASDAQ() throws Exception
    {
        Set<String> allSymbolsOnNYSE = SymbolFinder.findAllSymbols("NYSE", "NASDAQ");
        assertTrue("Should have found symbol: ABT", allSymbolsOnNYSE.contains("ABT"));
        assertTrue("Should have found symbol: CS", allSymbolsOnNYSE.contains("CS"));
        assertTrue("Should have found symbol: WAG", allSymbolsOnNYSE.contains("WAG"));
    }
}
