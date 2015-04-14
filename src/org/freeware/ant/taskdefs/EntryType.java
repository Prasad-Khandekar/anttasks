/*----------------------------------------------------------------------------------------------------------------------------------
 * PACKAGE  : org.freeware.ant.taskdefs
 * FILE     : EntryType.java
 * CREATED  : 15-Jul-2014 8:25:19 pm
 * AUTHOR   : Prasad P. Khandekar
 * COPYRIGHT: Copyright (c) 2008, Fundtech INDIA Ltd.
 *--------------------------------------------------------------------------------------------------------------------------------*/
package org.freeware.ant.taskdefs;

import org.apache.tools.ant.types.EnumeratedAttribute;

/**
 * Enumerated attribute with the values "int", "date" and "string".
 */
public class EntryType extends EnumeratedAttribute
{
    // Property types
    public static final int INTEGER_TYPE =     0;
    public static final int DATE_TYPE =        1;
    public static final int STRING_TYPE =      2;

    /** {@inheritDoc} */
    public String[] getValues()
    {
        return new String[] {"int", "date", "string"};
    }

    /**
     * Convert string to index.
     * @param type the string to convert.
     * @return the index.
     */
    public static int toType(String type)
    {
        if ("int".equals(type))
            return INTEGER_TYPE;
        else if ("date".equals(type))
            return DATE_TYPE;

        return STRING_TYPE;
    }
}