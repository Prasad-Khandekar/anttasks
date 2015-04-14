/*----------------------------------------------------------------------------------------------------------------------------------
 * PACKAGE  : org.freeware.ant.taskdefs
 * FILE     : SecurePropertyFile.java
 * CREATED  : 10-Jul-2014 8:11:12 pm
 * AUTHOR   : Prasad P. Khandekar
 * COPYRIGHT: Copyright (c) 2008, Fundtech INDIA Ltd.
 *--------------------------------------------------------------------------------------------------------------------------------*/
package org.freeware.ant.taskdefs;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.Properties;
import java.util.Vector;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.util.FileUtils;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.jasypt.encryption.pbe.config.EnvironmentStringPBEConfig;

/**
 * <p>A task similar to ant's built in <code>propertyfile</code> task with additional encryption.
 * <h3>Sample Usage</h3>
 * <pre style="padding:2px;margin:0px;border:1px dotted #0A246A;background-color:white;font-family:Consolas,monospace;">
 * &lt;secpropfile file="${basedir}/config/secprops.properties" mode="ENCRYPT" password="XFb3T4Zy"&gt;
 *     &lt;secentry key="DB_PASSWORD" value="${db.password}" operation="=" type="string"/&gt;
 *     &lt;secentry key="LOGIN_ATTEMPTS" value="${login.attempts}" operation="=" type="int"/&gt;
 *     &lt;secentry key="EXPIRES_ON" value="12" default="now" operation="+" type="date" unit="month" pattern="yyyyMMdd"/&gt;
 * &lt;/secpropfile&gt;</pre>
 * </p>
 * @author Prasad P. Khandekar
 * @version $Id$
 */
public class SecurePropertyFile extends Task
{
	private int _intAlgorithm = CryptAlgorithm.toAlgorithm("PBEWITHMD5ANDDES");
	private int _intMode = CryptMode.OPERATION_NONE;
	private boolean _blnSkipEmpty = false;

	private File _filProps;
	private String _strPassword;
	private String _strComment;

	private Properties _props;
	private Vector<SecEntry> entries = new Vector<SecEntry>();

	/**
	 * optional header comment for the file
	 * @param strVal the comment to set
	 */
	public final void setComment(String strVal)
	{
		_strComment = strVal;
	}

	/**
	 * The encryption algorithm to be used, default is '<code>PBEWITHMD5ANDDES</code>'
	 * @param algorithm the algorithm to set
	 */
	public final void setAlgorithm(CryptAlgorithm algorithm)
	{
		_intAlgorithm = CryptAlgorithm.toAlgorithm(algorithm.getValue());
	}

	/**
	 * The encryption password to be used
	 * @param password the password to set
	 */
	public final void setPassword(String password)
	{
		_strPassword = password;
	}

    /**
     * Location of the property file to be edited; required.
     * @param file the property file.
     */
    public void setFile(File file)
    {
        _filProps = file;
    }

    /**
     * Sets the mode of operation, valid values are
     * <ul>
     *     <li>NONE - store as is</li>
     *     <li>ENCRYPT - Encrypt property values</li>
     *     <li>DECRYPT - Decrypt property values</li>
     * </ul>
     * The default mode is NONE
     * @param strMode
     */
    public void setMode(String strMode)
    {
    	_intMode = CryptMode.toMode(strMode);
    }

	/**
	 * A flag to determine whether to skip empty values or flag an error. When set to tru an empty value will result in an error.
	 * @param skip the flag to skip empty values
	 */
	public void setSkipEmptyValues(boolean skip)
	{
		_blnSkipEmpty = skip;
	}

	/* (non-Javadoc)
	 * @see org.apache.tools.ant.Task#execute()
	 */
	public void execute() throws BuildException
	{
        checkParameters();
        readFile();
        executeOperation();
        writeFile();
	}

    /**
     * The entry nested element.
     * @return an entry nested element to be configured.
     */
    public SecEntry createSecEntry()
    {
        SecEntry e = new SecEntry();
        entries.addElement(e);
        return e;
    }

    /**
     * Helper method to take actions on individual entries.
     * @throws BuildException if unable to perform the specified operation (+,-,=,del)
     */
    private void executeOperation() throws BuildException
    {
    	SecEntry entry = null;
    	StandardPBEStringEncryptor svc = null;
    	EnvironmentStringPBEConfig cfg = null;

    	if (_intMode != CryptMode.OPERATION_NONE)
    	{
    		svc = new StandardPBEStringEncryptor();
    		cfg = new EnvironmentStringPBEConfig();
    		cfg.setAlgorithm(CryptAlgorithm.toValue(_intAlgorithm));
    		cfg.setPassword(_strPassword);
    		svc.setConfig(cfg);
    	}

        for (Enumeration<SecEntry> e = entries.elements(); e.hasMoreElements();)
        {
            entry = (SecEntry) e.nextElement();
            entry.executeOn(_props, svc, _intMode, _blnSkipEmpty);
        }
        svc = null;
    }

    /**
     * Helper method to validate the arguments supplied to this instance of task
     * @throws BuildException if properties file reference is not supplied or password not supplied for encrypt or decrypt operation
     */
    private void checkParameters() throws BuildException
    {
        if (!checkParam(_filProps))
            throw new BuildException("file token must not be null.", getLocation());

        if (_intMode != CryptMode.OPERATION_NONE && _strPassword == null)
        	throw new BuildException("Password must be supplied in encryption or decryption mode", getLocation());
    }

    /**
     * Helper method to check whether the properties file reference is supplied or not
     * @param param The properties file reference
     * @return true if properties file reference is supplied, false otherwise
     */
    private boolean checkParam(File param)
    {
        return (param != null);
    }

    /**
     * Helper method to write the properties file
     * @throws BuildException if unable to write the file
     */
    private void writeFile() throws BuildException
    {
    	OutputStream os = null;
    	ByteArrayOutputStream baos = null;

        // Write to RAM first, as an OOME could otherwise produce a truncated file:
        try
        {
        	baos = new ByteArrayOutputStream();
            _props.store(baos, _strComment);
        }
        catch (IOException x)
        {
        	// should not happen
            throw new BuildException(x, getLocation());
        }

        try
        {
            os = new FileOutputStream(_filProps);
            try
            {
                try
                {
                    os.write(baos.toByteArray());
                }
                finally
                {
                    os.close();
                }
            }
            catch (IOException x)
            {
            	// possibly corrupt
                FileUtils.getFileUtils().tryHardToDelete(_filProps);
                throw x;
            }
        }
        catch (IOException x)
        {
        	// opening, writing, or closing
            throw new BuildException(x, getLocation());
        }
    }

    /**
     * Helper method to load the properties file
     * @throws BuildException if unable to read the properties file.
     */
    private void readFile() throws BuildException
    {
    	FileInputStream fis = null;
    	FileOutputStream out = null;
    	BufferedInputStream bis = null;

        try
        {
        	_props = new Properties();
            if (_filProps.exists())
            {
                log("Updating property file: " + _filProps.getAbsolutePath());
                try
                {
                    fis = new FileInputStream(_filProps);
                    bis = new BufferedInputStream(fis);
                    _props.load(bis);
                }
                finally
                {
                    if (fis != null)
                    {
                        fis.close();
                    }
                }
            }
            else
            {
                log("Creating new property file: " + _filProps.getAbsolutePath());
                try
                {
                    out = new FileOutputStream(_filProps.getAbsolutePath());
                    out.flush();
                }
                finally
                {
                    if (out != null)
                    {
                        out.close();
                    }
                }
            }
        }
        catch (IOException ioe)
        {
            throw new BuildException(ioe.toString());
        }
    }
}
