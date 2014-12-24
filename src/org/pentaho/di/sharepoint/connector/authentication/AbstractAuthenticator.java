package org.pentaho.di.sharepoint.connector.authentication;

import java.net.Authenticator;

/***
 * 
 * Defines an Abstract authenticator that uses username, domain and password
 * 
 */
public abstract class AbstractAuthenticator extends Authenticator {

	String domain;
	String user;
	String password;

	protected AbstractAuthenticator(String user, String password) {
		super();
		this.domain = null;
		this.user = user;
		this.password = password;
	}

	protected AbstractAuthenticator(String domain, String user, String password) {
		super();
		this.domain = domain;
		this.user = user;
		this.password = password;
	}

	public String getPassword() {
		return password;
	}

	public String getDomain() {
		return domain;
	}

	public String getUser() {
		return user;
	}

	public String getUserName() {
		String userName = user;
		if (domain != null && !domain.isEmpty()) {
			userName = domain + "\\" + user;
		}
		return userName;
	}
}