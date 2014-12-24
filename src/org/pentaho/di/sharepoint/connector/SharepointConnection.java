/**
 * 
 */
package org.pentaho.di.sharepoint.connector;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.simple.*;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.sharepoint.connector.authentication.NtlmAuthenticator;

/**
 * Defines an HTTP Client that consumes Sharepoint 2013 OData (REST) Interface
 * providing the required NTLM authentication and security token handling. This
 * class redefines the Default Authenticator for each instance
 * 
 * @author JoseJonathan.Puertos
 * 
 */
public class SharepointConnection implements Serializable {

	/**
	 * Retrieves the current Digest used by this Connection while communicating
	 * to Sharepoint
	 * 
	 * @return
	 */
	public String getDigest() {
		return digest;
	}

	/**
	 * Sets the token to be used when sending requests to Sharepoint
	 * 
	 * @param digest
	 */
	public void setDigest(String digest) {
		this.digest = digest;
	}

	/**
	 * Retrieve the Last Http status code
	 * 
	 * @return
	 */
	public int getLastHttpStatus() {
		return lastHttpStatus;
	}

	private String digest;
	protected String siteURL;
	protected NtlmAuthenticator credentials;
	protected int lastHttpStatus;

	/**
	 * Creates a new Sharepoint REST Http Client using the given parameters
	 * 
	 * @param siteURL
	 *            The URL of the base site (or SubSite). Example:
	 *            https://mysite.com
	 * @param username
	 *            NTLM Username without domain. Example: myuser
	 * @param password
	 *            NTLM Password. Example: mypa$$w0rd
	 * @param domain
	 *            Domain. Example: domain
	 * @throws IOException
	 */
	public SharepointConnection(String siteURL, String username,
			String password, String domain) {

		// Set URL To use as endpoint of the site
		this.siteURL = siteURL;

		// Define Default NTLM Authentication.
		credentials = new NtlmAuthenticator(domain, username, password);

	}

	/**
	 * Resets the default Authenticator with the credentials for this connection
	 * and requests Sharepoint a new digest
	 * 
	 * @throws IOException
	 *             If there was an exception when trying to retrieve the Digest
	 */
	public void renewDigest() throws IOException {

		// Set Authentication
		Authenticator.setDefault(credentials);

		// Try to retrieve the token
		String contextURL = siteURL + "/_api/contextinfo";
		try {
			JSONObject obj = (JSONObject) JSONValue.parse(executeURL(
					contextURL, "POST", null, false, false));
			digest = ((JSONObject) ((JSONObject) obj.get("d"))
					.get("GetContextWebInformation")).get("FormDigestValue")
					.toString();
		} catch (IOException ex) {
			throw new IOException(
					String.format(
							"Error While getting token from %s with the given credentials.)",
							contextURL), ex);
		}
	}

	/**
	 * Retrieves the items of a Sharepoint List based on a given OData URL
	 * 
	 * @param url
	 *            URL To the List. Example :
	 *            https://mysite.com/_api/web/lists/getByTitle('MyList')/items
	 * @return A JSON object containing the results of the request
	 * @throws IOException
	 *             If there was an exception when trying to get the list
	 */

	public JSONObject getList(String url) throws IOException {
		return (JSONObject) ((JSONObject) ((JSONObject) JSONValue
				.parse(getURLContents(url))).get("d"));
	}

	/**
	 * Executes a GET request for a JSON encoded resource on the given URL and
	 * returns the contents as text
	 * 
	 * @param url
	 *            . Url to execute such as https://mysite.com/_api/Web
	 * @return Contents of the resource
	 * @throws IOException
	 *             If there was an exception when trying to get the list
	 */
	public String getURLContents(String url) throws IOException {
		return getURLContents(url, "GET", null);
	}

	/**
	 * Executes an HTTP request with the provided information and returns the
	 * result as text
	 * 
	 * @param url
	 *            The URL to execute.
	 * @param method
	 *            HTTP Verb (POST,GET,DELETE)
	 * @param contents
	 *            For POST requests payload to send
	 * @return Contents of the response
	 * @throws IOException
	 *             If there was an exception when trying to get the list
	 */
	public String getURLContents(String url, String method, String contents)
			throws IOException {
		
		//For Multiple Tenancy and support multiple authentications synchronised access
		synchronized(SharepointConnection.class){
		return executeURL(url, method, contents, false, true);
		}
	}

	/**
	 * Executes an HTTP request with the provided information and returns the
	 * result as text. This method has part of the code synchronized on the
	 * class in order to support multiple Sharepoints' domains
	 * 
	 * @param url
	 *            The URL to execute.
	 * @param method
	 *            HTTP Verb (POST,GET,DELETE)
	 * @param contents
	 *            For POST requests payload to send
	 * @param useMerge
	 *            For POST applies Merge logic adding the X-HTTP-Method header
	 * @param requireDigest
	 *            Specificy if it should use the Digest. Most of them should be
	 *            TRUE
	 * @return Contents of the response
	 * @throws IOException
	 *             If there was an exception when trying to get the list
	 */
	public String executeURL(String url, String method, String contents,
			boolean useMerge, boolean requiresDigest) throws IOException {

		StringBuilder sb = new StringBuilder();

		if (contents == null)
			contents = "";

		if (method == null)
			method = "GET";
		else
			method = method.toUpperCase();

		HttpURLConnection conn = null;
		URL urlRequest = new URL(url);

		conn = (HttpURLConnection) urlRequest.openConnection();
		conn.setRequestMethod(method);
		conn.setRequestProperty("Accept", "application/json; odata=verbose");
		conn.setRequestProperty("Content-Type",
				"application/json; odata=verbose");
		if (useMerge) {
			conn.setRequestProperty("X-HTTP-Method", "MERGE");
			// Avoid optimistic lock and overwrite
			conn.setRequestProperty("If-Match", "*");

		}

		// Verify if we require to have a digest
		if (requiresDigest) {
			if (getDigest() == null)
				renewDigest();
			conn.setRequestProperty("X-RequestDigest", getDigest());
		}

		conn.setDoOutput(true);
		if (method.equals("POST")) {
			conn.setDoInput(true);
			conn.setRequestProperty("Content-Length", "" + contents.length());
			DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
			wr.writeBytes(contents);
			wr.flush();
			wr.close();
		}

		// Force the authenticator for multiple connections
		Authenticator.setDefault(credentials);

		InputStream is;
		boolean isError = false;

 
		conn.connect();

			try {
				is = conn.getInputStream();
			} catch (Exception ex) {
				isError = true;
				this.lastHttpStatus = conn.getResponseCode();
				is = conn.getErrorStream();

				throw new IOException(ex);
			}
 
		byte[] b = new byte[1024];
		int bytesRead = 0;
		while ((bytesRead = is.read(b)) > 0) {
			sb.append(new String(b, 0, bytesRead));
		}
		is.close();

		if (isError) {
			System.out.println("Generated by " + contents);
			System.out.println(sb.toString());
		}
		conn.disconnect();

		this.lastHttpStatus = conn.getResponseCode();
		return sb.toString();
	}

	/**
	 * Encodes parameters to be used in an HTTP/HTTPS URL call
	 * 
	 * @param input
	 * @param string
	 * @return
	 */
	private static String encodeParam(String input) {
		StringBuilder resultStr = new StringBuilder();
		for (char ch : input.toCharArray()) {
			if (isUnsafe(ch)) {
				resultStr.append('%');
				resultStr.append(toHex(ch / 16));
				resultStr.append(toHex(ch % 16));
			} else {
				resultStr.append(ch);
			}
		}
		return resultStr.toString();
	}

	/**
	 * Gets the Hex Code for a Character
	 * 
	 * @param ch
	 * @return
	 */
	private static char toHex(int ch) {
		return (char) (ch < 10 ? '0' + ch : 'A' + ch - 10);
	}

	/**
	 * Identifies if a character is unsafe for HTTP
	 * 
	 * @param ch
	 * @return
	 */
	private static boolean isUnsafe(char ch) {
		if (ch > 128 || ch < 0)
			return true;
		return " %$&+,/:;=?@<>#%".indexOf(ch) >= 0;
	}

}
