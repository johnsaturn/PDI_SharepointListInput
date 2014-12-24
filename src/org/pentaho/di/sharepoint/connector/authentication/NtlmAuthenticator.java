package org.pentaho.di.sharepoint.connector.authentication;

import java.net.PasswordAuthentication;

 

/**
 * Implements NTLM Authentication used by Sharepoint 2013
*
 * 
 */
public class NtlmAuthenticator extends AbstractAuthenticator {

	public NtlmAuthenticator(String user, String password) {
		super(user, password);
	}

	public NtlmAuthenticator(String domain, String user, String password) {
		super(domain, user, password);
	}

	@Override
	public PasswordAuthentication getPasswordAuthentication() {
		return new PasswordAuthentication(getUserName(), password.toCharArray());
	}
}