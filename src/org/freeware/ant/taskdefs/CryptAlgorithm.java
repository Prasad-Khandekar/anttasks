/*----------------------------------------------------------------------------------------------------------------------------------
 * PACKAGE  : org.freeware.ant.taskdefs
 * FILE     : CryptAlgorithm.java
 * CREATED  : 15-Jul-2014 8:10:23 pm
 * AUTHOR   : Prasad P. Khandekar
 * COPYRIGHT: Copyright (c) 2008, Fundtech INDIA Ltd.
 *--------------------------------------------------------------------------------------------------------------------------------*/
package org.freeware.ant.taskdefs;

import org.apache.tools.ant.types.EnumeratedAttribute;

/**
 * <p>The enumeration defininf the supported encryption algorithms by the secure property tasks.</p>
 * @author Prasad P. Khandekar
 * @version $Id$
 */
public class CryptAlgorithm extends EnumeratedAttribute
{
	private static final int ALGO_PBEWITHMD5ANDDES       = 0;
	private static final int ALGO_PBEWITHMD5ANDTRIPLEDES = 1;
	private static final int ALGO_PBEWITHSHA1ANDDESEDE   = 2;
	private static final int ALGO_PBEWITHSHA1ANDRC2_40   = 3;

	private static final String[] ALGORITHMS = {"PBEWITHMD5ANDDES", "PBEWITHMD5ANDTRIPLEDES", "PBEWITHSHA1ANDDESEDE",
													"PBEWITHSHA1ANDRC2_40"};

    /** {@inheritDoc}. */
    public String[] getValues()
    {
        return ALGORITHMS;
    }

    public static int toAlgorithm(String algo)
    {
    	if ("PBEWITHMD5ANDTRIPLEDES".equalsIgnoreCase(algo))
    		return ALGO_PBEWITHMD5ANDTRIPLEDES;
    	else if ("PBEWITHSHA1ANDDESEDE".equalsIgnoreCase(algo))
    		return ALGO_PBEWITHSHA1ANDDESEDE;
    	else if ("PBEWITHSHA1ANDRC2_40".equalsIgnoreCase(algo))
    		return ALGO_PBEWITHSHA1ANDRC2_40;

    	return ALGO_PBEWITHMD5ANDDES;
    }

    public static String toValue(int algoId)
    {
    	if (algoId > -1 & algoId < ALGORITHMS.length)
    		return ALGORITHMS[algoId];

    	return null;
    }
}
