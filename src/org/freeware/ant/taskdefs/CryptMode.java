/*----------------------------------------------------------------------------------------------------------------------------------
 * PACKAGE  : org.freeware.ant.taskdefs
 * FILE     : CryptMode.java
 * CREATED  : 15-Jul-2014 8:12:05 pm
 * AUTHOR   : Prasad P. Khandekar
 * COPYRIGHT: Copyright (c) 2008, Fundtech INDIA Ltd.
 *--------------------------------------------------------------------------------------------------------------------------------*/
package org.freeware.ant.taskdefs;

import org.apache.tools.ant.types.EnumeratedAttribute;

/**
 * <p>An enumeration defining the modes of operation.</p>
 * @author Prasad P. Khandekar
 * @version $Id$
 */
public class CryptMode extends EnumeratedAttribute
{
	public static final int OPERATION_ENCRYPT = 0;
	public static final int OPERATION_DECRYPT = 1;
	public static final int OPERATION_NONE    = 2;

	private static final String[] MODES = {"ENCRYPT", "DECRYPT", "NONE"};

    /** {@inheritDoc}. */
    public String[] getValues()
    {
        return MODES;
    }

    public static int toMode(String mode)
    {
    	if ("ENCRYPT".equalsIgnoreCase(mode))
    		return OPERATION_ENCRYPT;
    	else if ("DECRYPT".equalsIgnoreCase(mode))
    		return OPERATION_DECRYPT;

    	return OPERATION_NONE;
    }
}
