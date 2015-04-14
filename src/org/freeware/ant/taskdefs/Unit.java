/*----------------------------------------------------------------------------------------------------------------------------------
 * PACKAGE  : org.freeware.ant.taskdefs
 * FILE     : Unit.java
 * CREATED  : 15-Jul-2014 8:23:49 pm
 * AUTHOR   : Prasad P. Khandekar
 * COPYRIGHT: Copyright (c) 2008, Fundtech INDIA Ltd.
 *--------------------------------------------------------------------------------------------------------------------------------*/
package org.freeware.ant.taskdefs;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import org.apache.tools.ant.types.EnumeratedAttribute;

/**
 * Borrowed from Tstamp
 * @todo share all this time stuff across many tasks as a datetime datatype
 * @since Ant 1.5
 */
public class Unit extends EnumeratedAttribute
{
    private static final String MILLISECOND = "millisecond";
    private static final String SECOND = "second";
    private static final String MINUTE = "minute";
    private static final String HOUR = "hour";
    private static final String DAY = "day";
    private static final String WEEK = "week";
    private static final String MONTH = "month";
    private static final String YEAR = "year";

    private static final String[] UNITS = {MILLISECOND, SECOND, MINUTE, HOUR, DAY, WEEK, MONTH, YEAR };

    private Map<String, Integer> calendarFields = new HashMap<String, Integer>();

    /** no arg constructor */
    public Unit()
    {
        calendarFields.put(MILLISECOND, new Integer(Calendar.MILLISECOND));
        calendarFields.put(SECOND, new Integer(Calendar.SECOND));
        calendarFields.put(MINUTE, new Integer(Calendar.MINUTE));
        calendarFields.put(HOUR, new Integer(Calendar.HOUR_OF_DAY));
        calendarFields.put(DAY, new Integer(Calendar.DATE));
        calendarFields.put(WEEK, new Integer(Calendar.WEEK_OF_YEAR));
        calendarFields.put(MONTH, new Integer(Calendar.MONTH));
        calendarFields.put(YEAR, new Integer(Calendar.YEAR));
    }

    /**
     * Convert the value to a Calendar field index.
     * @return the calander value.
     */
    public int getCalendarField()
    {
        String key = getValue().toLowerCase();
        Integer i = (Integer) calendarFields.get(key);
        return i.intValue();
    }

    /** {@inheritDoc}. */
    public String[] getValues()
    {
        return UNITS;
    }
}