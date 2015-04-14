/*----------------------------------------------------------------------------------------------------------------------------------
 * PACKAGE  : org.freeware.ant.taskdefs
 * FILE     : Operation.java
 * CREATED  : 15-Jul-2014 8:25:09 pm
 * AUTHOR   : Prasad P. Khandekar
 * COPYRIGHT: Copyright (c) 2008, Fundtech INDIA Ltd.
 *--------------------------------------------------------------------------------------------------------------------------------*/
package org.freeware.ant.taskdefs;

import org.apache.tools.ant.types.EnumeratedAttribute;

/**
 * Enumerated attribute with the values "+", "-", "="
 */
public class Operation extends EnumeratedAttribute
{
    // Property type operations
    public static final int INCREMENT_OPER =   0;
    public static final int DECREMENT_OPER =   1;
    public static final int EQUALS_OPER =      2;
    public static final int DELETE_OPER =      3;

    /** {@inheritDoc}. */
    public String[] getValues()
    {
        return new String[] {"+", "-", "=", "del"};
    }

    /**
     * Convert string to index.
     * @param oper the string to convert.
     * @return the index.
     */
    public static int toOperation(String oper)
    {
        if ("+".equals(oper))
            return INCREMENT_OPER;
        else if ("-".equals(oper))
            return DECREMENT_OPER;
        else if ("del".equals(oper))
            return DELETE_OPER;

        return EQUALS_OPER;
    }
}