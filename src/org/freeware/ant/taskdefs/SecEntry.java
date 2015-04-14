/*----------------------------------------------------------------------------------------------------------------------------------
 * PACKAGE  : org.freeware.ant.taskdefs
 * FILE     : SecEntry.java
 * CREATED  : 15-Jul-2014 8:23:30 pm
 * AUTHOR   : Prasad P. Khandekar
 * COPYRIGHT: Copyright (c) 2008, Fundtech INDIA Ltd.
 *--------------------------------------------------------------------------------------------------------------------------------*/
package org.freeware.ant.taskdefs;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Properties;

import org.apache.tools.ant.BuildException;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;

/**
 * Instance of this class represents nested elements of a task <code>secpropfile</code>.
 */
public class SecEntry
{
    private static final int DEFAULT_INT_VALUE = 0;
    private static final String DEFAULT_DATE_VALUE = "now";
    private static final String DEFAULT_STRING_VALUE = "";

    private int _intType = EntryType.STRING_TYPE;
    private int _intOperation = Operation.EQUALS_OPER;
    private int _intField = Calendar.DATE;
    private String _strKey = null;
    private String _strValue = null;
    private String _strDefaultValue = null;
    private String _strNewValue = null;
    private String _strPattern = null;

    /**
     * Name of the property name/value pair
     * @param value the key.
     */
    public void setKey(String value)
    {
        _strKey = value;
    }

    /**
     * Value to set (=), to add (+) or subtract (-)
     * @param value the value.
     */
    public void setValue(String value)
    {
        _strValue = value;
    }

    /**
     * operation to apply.
     * &quot;+&quot; or &quot;=&quot;
     *(default) for all datatypes; &quot;-&quot; for date and int only)\.
     * @param value the operation enumerated value.
     */
    public void setOperation(Operation value)
    {
        _intOperation = Operation.toOperation(value.getValue());
    }

    /**
     * Regard the value as : int, date or string (default)
     * @param value the type enumerated value.
     */
    public void setType(EntryType value)
    {
        _intType = EntryType.toType(value.getValue());
    }

    /**
     * Initial value to set for a property if it is not
     * already defined in the property file.
     * For type date, an additional keyword is allowed: &quot;now&quot;
     * @param value the default value.
     */
    public void setDefault(String value)
    {
        _strDefaultValue = value;
    }

    /**
     * For int and date type only. If present, Values will
     * be parsed and formatted accordingly.
     * @param value the pattern to use.
     */
    public void setPattern(String value)
    {
        _strPattern = value;
    }

    /**
     * The unit of the value to be applied to date +/- operations. Valid Values are:
     * <ul>
     *     <li>millisecond</li>
     *     <li>second</li>
     *     <li>minute</li>
     *     <li>hour</li>
     *     <li>day (default)</li>
     *     <li>week</li>
     *     <li>month</li>
     *     <li>year</li>
     * </ul>
     * This only applies to date types using a +/- operation.
     * @param unit the unit enumerated value.
     * @since Ant 1.5
     */
    public void setUnit(Unit unit)
    {
        _intField = unit.getCalendarField();
    }

    /**
     * Apply the nested element to the properties.
     * @param props the properties to apply the entry on.
     * @throws BuildException if there is an error.
     */
    protected void executeOn(Properties props, StandardPBEStringEncryptor svc, int intMode, boolean skipEmpty) throws BuildException
    {
    	String oldValue = null;
		String strTemp = null;

        checkParameters();

        if (_intOperation == Operation.DELETE_OPER)
        {
            props.remove(_strKey);
            return;
        }

        // type may be null because it wasn't set
        strTemp = props.getProperty(_strKey);
        if (intMode == CryptMode.OPERATION_DECRYPT && strTemp != null && strTemp.startsWith("ENC("))
			oldValue = svc.decrypt(strTemp.substring(4, strTemp.length() - 1));
        else if (!skipEmpty)
        	throw new BuildException("Empty values are not allowed, properety " + _strKey);
        else
        	oldValue = strTemp;

        try
        {
            if (_intType == EntryType.INTEGER_TYPE)
                executeInteger(oldValue);
            else if (_intType == EntryType.DATE_TYPE)
                executeDate(oldValue);
            else if (_intType == EntryType.STRING_TYPE)
                executeString(oldValue);
            else
                throw new BuildException("Unknown operation type: " + _intType);
        }
        catch (NullPointerException npe)
        {
            // Default to string type
            // which means do nothing
            npe.printStackTrace();
        }

        if (_strNewValue == null) _strNewValue = "";

        // Insert as a string by default
        if (intMode == CryptMode.OPERATION_ENCRYPT && _strNewValue.length() > 0)
        {
        	strTemp = svc.encrypt(_strNewValue);
        	props.put(_strKey, String.format("ENC(%1s)", strTemp));
        }
        else if (!skipEmpty)
			throw new BuildException("Empty values are not allowed, properety " + _strKey);
		else
        	props.put(_strKey, _strNewValue);
    }

    /**
     * Handle operations for type <code>date</code>.
     *
     * @param oldValue the current value read from the property file or <code>null</code> if the <code>key</code> was
     * not contained in the property file.
     */
    private void executeDate(String oldValue) throws BuildException
    {
    	int offset = 0;
    	String curVal = null;
    	Calendar cal = null;
    	DateFormat fmt = null;

        cal = Calendar.getInstance();

        if (_strPattern == null) _strPattern = "yyyy/MM/dd HH:mm";

        fmt = new SimpleDateFormat(_strPattern);

        curVal = getCurrentValue(oldValue);
        if (curVal == null) curVal = DEFAULT_DATE_VALUE;


        if ("now".equals(curVal))
        	cal.setTime(new Date());
        else
        {
            try
        	{
            	cal.setTime(fmt.parse(curVal));
            }
            catch (ParseException pe)
            {
                // swallow
            }
        }

        if (_intOperation != Operation.EQUALS_OPER)
        {
            try
            {
                offset = Integer.parseInt(_strValue);
                if (_intOperation == Operation.DECREMENT_OPER)
                    offset = -1 * offset;
            }
            catch (NumberFormatException e)
            {
                throw new BuildException("Value not an integer on " + _strKey);
            }
            cal.add(_intField, offset);
        }

        _strNewValue = fmt.format(cal.getTime());
    }


    /**
     * Handle operations for type <code>int</code>.
     *
     * @param oldValue the current value read from the property file or <code>null</code> if the <code>key</code> was
     * not contained in the property file.
     */
    private void executeInteger(String oldValue) throws BuildException
    {
        int currentValue = DEFAULT_INT_VALUE;
        int newV  = DEFAULT_INT_VALUE;
        int opValue = 1;
        String curval = null;
        DecimalFormat fmt = null;

        fmt = (_strPattern != null) ? new DecimalFormat(_strPattern) : new DecimalFormat();
        try
        {
            curval = getCurrentValue(oldValue);
            if (curval != null)
                currentValue = fmt.parse(curval).intValue();
            else
                currentValue = 0;
        }
        catch (NumberFormatException nfe)
        {
            // swallow
        }
        catch (ParseException pe)
        {
            // swallow
        }

        if (_intOperation == Operation.EQUALS_OPER)
            newV = currentValue;
        else
        {
            if (_strValue != null)
            {
                try
                {
                    opValue = fmt.parse(_strValue).intValue();
                }
                catch (NumberFormatException nfe)
                {
                    // swallow
                }
                catch (ParseException pe)
                {
                    // swallow
                }
            }

            if (_intOperation == Operation.INCREMENT_OPER)
                newV = currentValue + opValue;
            else if (_intOperation == Operation.DECREMENT_OPER)
                newV = currentValue - opValue;
        }

        _strNewValue = fmt.format(newV);
    }

    /**
     * Handle operations for type <code>string</code>.
     *
     * @param oldValue the current value read from the property file or <code>null</code> if the <code>key</code> was
     * not contained in the property file.
     */
    private void executeString(String oldValue) throws BuildException
    {
        String newV  = DEFAULT_STRING_VALUE;
        String curVal = null;

        curVal = getCurrentValue(oldValue);
        if (curVal == null) curVal = DEFAULT_STRING_VALUE;

        if (_intOperation == Operation.EQUALS_OPER)
            newV = curVal;
        else if (_intOperation == Operation.INCREMENT_OPER)
            newV = curVal + _strValue;

        _strNewValue = newV;
    }

    /**
     * Check if parameter combinations can be supported
     * TODO: make sure the 'unit' attribute is only specified on date fields
     */
    private void checkParameters() throws BuildException
    {
        if (_intType == EntryType.STRING_TYPE && _intOperation == Operation.DECREMENT_OPER)
            throw new BuildException("- is not supported for string properties (key:" + _strKey + ")");

        if (_strValue == null && _strDefaultValue == null  && _intOperation != Operation.DELETE_OPER)
            throw new BuildException("\"value\" and/or \"default\" attribute must be specified (key:" + _strKey + ")");

        if (_strKey == null)
            throw new BuildException("key is mandatory");

        if (_intType == EntryType.STRING_TYPE && _strPattern != null)
            throw new BuildException("pattern is not supported for string properties (key:" + _strKey + ")");
    }

    private String getCurrentValue(String oldValue)
    {
        String ret = null;
        if (_intOperation == Operation.EQUALS_OPER)
        {
            // If only value is specified, the property is set to it
            // regardless of its previous value.
            if (_strValue != null && _strDefaultValue == null) ret = _strValue;

            // If only default is specified and the property previously
            // existed in the property file, it is unchanged.
            if (_strValue == null && _strDefaultValue != null && oldValue != null) ret = oldValue;

            // If only default is specified and the property did not
            // exist in the property file, the property is set to default.
            if (_strValue == null && _strDefaultValue != null && oldValue == null) ret = _strDefaultValue;

            // If value and default are both specified and the property
            // previously existed in the property file, the property
            // is set to value.
            if (_strValue != null && _strDefaultValue != null && oldValue != null) ret = _strValue;

            // If value and default are both specified and the property
            // did not exist in the property file, the property is set
            // to default.
            if (_strValue != null && _strDefaultValue != null && oldValue == null) ret = _strDefaultValue;
        }
        else
            ret = (oldValue == null) ? _strDefaultValue : oldValue;

        return ret;
    }
}