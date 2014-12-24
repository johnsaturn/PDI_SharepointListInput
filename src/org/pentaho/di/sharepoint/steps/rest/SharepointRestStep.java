/**
 * 
 */
package org.pentaho.di.sharepoint.steps.rest;

import java.io.IOException;
import java.util.Arrays;

import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.exception.KettlePluginException;
import org.pentaho.di.core.row.RowDataUtil;
import org.pentaho.di.core.row.RowMeta;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.core.row.ValueMetaInterface;
import org.pentaho.di.core.row.value.ValueMetaFactory;
import org.pentaho.di.sharepoint.connector.SharepointConnection;
import org.pentaho.di.sharepoint.steps.input.SharepointListInputMeta;
import org.pentaho.di.trans.Trans;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.StepDataInterface;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.step.StepMetaInterface;
import org.pentaho.di.trans.steps.jsoninput.JsonInputField;
import org.pentaho.di.trans.steps.rest.Rest;
import org.pentaho.di.trans.steps.rest.RestData;
import org.pentaho.di.trans.steps.rest.RestMeta;

import javax.ws.rs.core.MediaType;

/**
 * @author josejonathan.puertos
 * 
 */
public class SharepointRestStep extends Rest {

	protected static String SHAREPOINT_TOKEN_FIELD = "__SP_TOKEN_";

	RestMeta meta;
	RestData data;

	protected String digest = "";

	/**
	 * @param stepMeta
	 * @param stepDataInterface
	 * @param copyNr
	 * @param transMeta
	 * @param trans
	 */
	public SharepointRestStep(StepMeta stepMeta,
			StepDataInterface stepDataInterface, int copyNr,
			TransMeta transMeta, Trans trans) {
		super(stepMeta, stepDataInterface, copyNr, transMeta, trans);

	}

	/**
	 * Retrieve sthe Digest
	 * 
	 * @return
	 * @throws KettleException
	 * @throws IOException
	 */
	protected String getDigest(RestMeta meta) throws IOException,
			KettleException {

		String site, user, password, domain;

		// Deduce properties from metadata
		user = meta.getHttpLogin().substring(
				meta.getHttpLogin().indexOf('\\') + 1);
		domain = meta.getHttpLogin().substring(0,
				meta.getHttpLogin().indexOf('\\'));
		password = meta.getHttpPassword();
		if (meta.isUrlInField()) {
			site = environmentSubstitute(meta.getUrlField());
		} else {
			site = meta.getUrl();
		}
		// From Site take up to the _api
		site = site.substring(0, site.indexOf("/_api/"));

		SharepointConnection sc = new SharepointConnection(site, user,
				password, domain);
		return sc.getDigest();
	}

	/**
	 * Overrides the regular initialisation done by the REST Client
	 */
	@Override
	public boolean init(StepMetaInterface smi, StepDataInterface sdi) {
		boolean result;

		// Cast to original...
		meta = (RestMeta) smi;
		data = (RestData) sdi;

		// Set the Digest Header..
		String[] headerNames=  meta.getHeaderName();
		Arrays.sort(headerNames); ;

		try {
			if (java.util.Arrays.binarySearch(headerNames,
					"X-RequestDigest") < 0) {
				digest = getDigest(meta);
				// Regenerate headers
				headerNames = java.util.Arrays.copyOf(
						meta.getHeaderName(), meta.getHeaderName().length + 1);
				String[] headerValues = java.util.Arrays
						.copyOf(meta.getHeaderField(),
								meta.getHeaderField().length + 1);

				headerNames[headerNames.length - 1] = "X-RequestDigest";
				headerValues[headerValues.length - 1] = SHAREPOINT_TOKEN_FIELD;

				// Set
				meta.setHeaderName(headerNames);
				meta.setHeaderField(headerValues);
			}
		} catch (Exception e) {
			this.logError(e.getMessage());
			return false;
		}

		// Change configuration done by the parent to ODATA
		data.mediaType = new MediaType("application/json", "odata=verbose");

		// Create Sharepoint Connection Object and get digest
		result = super.init(smi, sdi);

		return result;
	}

	/**
	 * Overrides this method to have the token
	 */
	@Override
	public Object[] getRow() throws KettleException {

		// Get the row from parent
		Object[] row = super.getRow();
		RowMetaInterface ri = getInputRowMeta();

				
	 

		if(ri!=null && row!=null){
				int index = ri.indexOfValue(SHAREPOINT_TOKEN_FIELD);
				row[index]=digest;
			}
			
		


		return row;
	}
	
	/**
	 * Helps validate we have the required Token in the request
	 * @param ri
	 * @return
	 */
	protected RowMetaInterface ensureDigestToken(RowMetaInterface ri){
		
		if (ri != null) {

			//Sort Headers
			String[] headerNames=  ri.getFieldNames();
			Arrays.sort(headerNames); 
			
			if (java.util.Arrays.binarySearch(headerNames,
					SHAREPOINT_TOKEN_FIELD) < 0) {
				 
				 
					try {
						ValueMetaInterface stringMeta = ValueMetaFactory
								.createValueMeta(SHAREPOINT_TOKEN_FIELD,
										ValueMetaInterface.TYPE_STRING);
						ri.addValueMeta(stringMeta);
					} catch (KettlePluginException e) {
						// TODO Auto-generated catch block
						this.logError(e.getMessage());
					}

			}

		}

		return ri;

	}

	@Override
	public  RowMetaInterface getInputRowMeta() {
		return ensureDigestToken(super.getInputRowMeta());
	};

	/**
	 * Override and prefill the added field
	 */
	@Override
	public boolean processRow(StepMetaInterface smi, StepDataInterface sdi)
			throws KettleException {
		RestMeta m = (RestMeta) smi;
		RestData d = (RestData) sdi;


		d.inputRowMeta=ensureDigestToken(d.inputRowMeta);
		
		// TODO Auto-generated method stub
		return super.processRow(smi, sdi);
	} 

}
