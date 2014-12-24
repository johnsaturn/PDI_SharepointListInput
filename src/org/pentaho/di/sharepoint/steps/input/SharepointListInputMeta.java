/*******************************************************************************
 *
 * Pentaho Data Integration
 *
 * Copyright (C) 2002-2012 by Pentaho : http://www.pentaho.com
 *
 *******************************************************************************
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 ******************************************************************************/

package org.pentaho.di.sharepoint.steps.input;

import java.util.List;
import java.util.Map;

import org.eclipse.swt.widgets.Shell;
import org.pentaho.di.core.Counter;
import org.pentaho.di.core.annotations.Step;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.core.encryption.Encr;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.exception.KettleXMLException;
import org.pentaho.di.core.fileinput.FileInputList;
import org.pentaho.di.core.variables.VariableSpace;
import org.pentaho.di.core.xml.XMLHandler;
import org.pentaho.di.i18n.BaseMessages;
import org.pentaho.di.repository.ObjectId;
import org.pentaho.di.repository.Repository;
import org.pentaho.di.trans.Trans;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.StepDataInterface;
import org.pentaho.di.trans.step.StepDialogInterface;
import org.pentaho.di.trans.step.StepInterface;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.step.StepMetaInterface;
import org.w3c.dom.Node;

/**
*
*	This class overrides
*
 */
@Step( description="Retrieves items from a Sharepoint List", name="Microsoft Sharepoint List Input", image="icon.png",id="SharepointListInput", categoryDescription="Input")
public class SharepointListInputMeta extends org.pentaho.di.trans.steps.jsoninput.JsonInputMeta  {

	/**
	 *	The PKG member is used when looking up internationalized strings.
	 *	The properties file with localized keys is expected to reside in 
	 *	{the package of the class specified}/messages/messages_{locale}.properties   
	 */
	private static Class<?> PKG = SharepointListInputMeta.class; // for i18n purposes
	

	private String username, password,domain,site,list;
	
	/**
	 * Constructor should call super() to make sure the base class has a chance to initialize properly.
	 */
	public SharepointListInputMeta() {
		super(); 
	}
	
	/**
	 * Called by Spoon to get a new instance of the SWT dialog for the step.
	 * A standard implementation passing the arguments to the constructor of the step dialog is recommended.
	 * 
	 * @param shell		an SWT Shell
	 * @param meta 		description of the step 
	 * @param transMeta	description of the the transformation 
	 * @param name		the name of the step
	 * @return 			new instance of a dialog for this step 
	 */
	public StepDialogInterface getDialog(Shell shell, StepMetaInterface meta, TransMeta transMeta, String name) {
		return new SharepointListInputDialog(shell, meta, transMeta, name);
	}

	/**
	 * Called by PDI to get a new instance of the step implementation. 
	 * A standard implementation passing the arguments to the constructor of the step class is recommended.
	 * 
	 * @param stepMeta				description of the step
	 * @param stepDataInterface		instance of a step data class
	 * @param cnr					copy number
	 * @param transMeta				description of the transformation
	 * @param disp					runtime implementation of the transformation
	 * @return						the new instance of a step implementation 
	 */
	public StepInterface getStep(StepMeta stepMeta, StepDataInterface stepDataInterface, int cnr, TransMeta transMeta, Trans disp) {
		return new SharepointListInputStep(stepMeta, stepDataInterface, cnr, transMeta, disp);
	}

	/**
	 * Called by PDI to get a new instance of the step data class.
	 */
	public StepDataInterface getStepData() {
		return new BaseSharepointStepData();
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getSite() {
		return site;
	}

	public void setSite(String site) {
		this.site = site;
	}	
	public String getList() {
		return list;
	}

	public void setList(String list) {
		this.list = list;
	}
	
	private String jsonValue;
	
	@Override public String getFieldValue() {
		return jsonValue;
		
	};
	public void setFieldValue(String value){
		jsonValue=value;
	}

	@Override public void loadXML(Node stepnode, java.util.List<DatabaseMeta> databases, java.util.Map<String,Counter> counters) throws KettleXMLException {
		
		
		
		try{
			
			setList( XMLHandler.getTagValue(stepnode, "list"));
			setSite( XMLHandler.getTagValue(stepnode, "site"));
			setPassword( Encr.decryptPasswordOptionallyEncrypted(XMLHandler.getTagValue(stepnode, "password")));
			setUsername( XMLHandler.getTagValue(stepnode, "username"));
			setDomain( XMLHandler.getTagValue(stepnode, "domain"));
			
			
		}catch(Exception e){
			 throw new KettleXMLException( BaseMessages.getString( PKG, "JsonInputMeta.Exception.ErrorLoadingXML", e
				        .toString() ) );
		}
		
		super.loadXML(stepnode, databases, counters);
	};
	
	
	
	@Override
	public String getXML() {
		//Retrieve  parent's XML
		String xml=super.getXML();
				 
		StringBuffer retval=new StringBuffer(400);
		StringBuffer preval=new StringBuffer(xml);
		int idx=preval.indexOf("</readurl>")+10;
		 retval.append( "    " ).append( XMLHandler.addTagValue( "site",  getSite() ) );
		 retval.append( "    " ).append( XMLHandler.addTagValue( "password",  Encr.encryptPasswordIfNotUsingVariables(getPassword()) ));
		 retval.append( "    " ).append( XMLHandler.addTagValue( "username", getUsername()) );
		 retval.append( "    " ).append( XMLHandler.addTagValue( "list",   getList() ) );
		 retval.append( "    " ).append( XMLHandler.addTagValue( "domain",  getDomain() ) );
		 
		 preval.insert(idx, retval);
		 
		return preval.toString();
	}


	@Override
	public void readRep(Repository rep, ObjectId id_step,
			List<DatabaseMeta> databases, Map<String, Counter> arg3)
			throws KettleException {
		// Call Super
		super.readRep(rep, id_step, databases, arg3);
		
		//Set Sharepoint's attributes
		this.setSite(rep.getStepAttributeString(id_step, "site"));
		this.setPassword(Encr.decryptPasswordOptionallyEncrypted(rep.getStepAttributeString(id_step, "password")));
		this.setUsername(rep.getStepAttributeString(id_step, "username"));
		this.setPassword(rep.getStepAttributeString(id_step, "list"));
		this.setDomain(rep.getStepAttributeString(id_step, "domain"));
		
		
	}
	@Override
	public void saveRep(Repository rep, ObjectId id_transformation, ObjectId id_step)
			throws KettleException {
		// Save Repo
		super.saveRep(rep, id_transformation, id_step);
		
		//Set Sharepoint's attributes
		rep.saveStepAttribute(id_transformation, id_step, "site", getSite());
		rep.saveStepAttribute(id_transformation, id_step, "password", Encr.encryptPasswordIfNotUsingVariables(getPassword()));
		rep.saveStepAttribute(id_transformation, id_step, "username", getUsername());
		rep.saveStepAttribute(id_transformation, id_step, "list", getList());
		rep.saveStepAttribute(id_transformation, id_step, "domain", getDomain());
		
	}

 
	
}