/*----------------------------------------------------------------------------------------------------------------------------------
 * PACKAGE  : org.freeware.ant.taskdefs
 * FILE     : SecureProperty.java
 * CREATED  : 10-Jul-2014 8:11:12 pm
 * AUTHOR   : Prasad P. Khandekar
 * COPYRIGHT: Copyright (c) 2008, Fundtech INDIA Ltd.
 *--------------------------------------------------------------------------------------------------------------------------------*/
package org.freeware.ant.taskdefs;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.PropertyHelper;
import org.apache.tools.ant.Task;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.jasypt.encryption.pbe.config.EnvironmentStringPBEConfig;

/**
 * <p>A task similar to ant's built in <code>property</code> task with additional encryption support.</p>
 * @author Prasad P. Khandekar
 * @version $Id$
 */
public class SecureProperty extends Task
{
	private int _intMode = CryptMode.toMode("ENCRYPT");
	private int _intAlgorithm = CryptAlgorithm.toAlgorithm("PBEWITHMD5ANDDES");

	private String _strName;
	private String _strValue;
	private String _strPassword;

	/**
	 * The sets the name of the property to be added 
	 * @param name the name of the proeprty to be added
	 */
	public final void setName(String name)
	{
		_strName = name;
	}

	/**
	 * @param value the value to set
	 */
	public final void setValue(String value)
	{
		_strValue = value;
	}

	/**
	 * Sets the encryption algorithm to be used, ignored if mode is set to <code>NONE</code>. Supported algorithms are
	 * <ul>
	 *     <li>PBEWITHMD5ANDDES</li>
	 *     <li>PBEWITHMD5ANDTRIPLEDES</li>
	 *     <li>PBEWITHSHA1ANDDESEDE</li>
	 *     <li>PBEWITHSHA1ANDRC2_40</li>
	 * </ul>
	 * @param algorithm the algorithm to set
	 */
	public final void setAlgorithm(CryptAlgorithm algorithm)
	{
		_intAlgorithm = CryptAlgorithm.toAlgorithm(algorithm.getValue());
	}

	/**
	 * Sets the password to be used for encryption.
	 * @param password the password for encryption
	 */
	public final void setPassword(String password)
	{
		_strPassword = password;
	}

	/**
	 * Sets the mode of operation. Supported values are
	 * <ul>
	 *     <li>ENCRYPT - Encryption mode</li>
	 *     <li>DECRYPT - Decryption mode</li>
	 *     <li>NONE - Plain text mode</li>
	 * </ul>
	 * @param strMode
	 */
	public final void setMode(String strMode)
	{
		_intMode = CryptMode.toMode(strMode);
	}

	/* (non-Javadoc)
	 * @see org.apache.tools.ant.Task#execute()
	 */
	public void execute() throws BuildException
	{
		String strRet = null;
		PropertyHelper ph = null;
    	StandardPBEStringEncryptor svc = null;
    	EnvironmentStringPBEConfig cfg = null;

		try
		{
			if (null == _strName || null == _strValue)
				throw new BuildException("You must specify the name and  value attributes!");

			if (_intMode != CryptMode.OPERATION_NONE && null == _strPassword)
				throw new BuildException("You must specify password for encryption or decryption!");

			if (_intMode != CryptMode.OPERATION_NONE)
			{
				svc = new StandardPBEStringEncryptor();
	    		cfg = new EnvironmentStringPBEConfig();
	    		cfg.setAlgorithm(CryptAlgorithm.toValue(_intAlgorithm));
	    		cfg.setPassword(_strPassword);
	    		svc.setConfig(cfg);
			}

			if (_intMode == CryptMode.OPERATION_ENCRYPT)
				strRet = svc.encrypt(_strValue);
			else if (_intMode == CryptMode.OPERATION_DECRYPT)
				strRet = svc.decrypt(_strValue);
			else
				strRet = _strValue;

			ph = PropertyHelper.getPropertyHelper(getProject());
			ph.setProperty(_strName, strRet, true);
		}
		finally
		{
			svc = null;
			ph = null;
			strRet = null;
		}
	}
}
