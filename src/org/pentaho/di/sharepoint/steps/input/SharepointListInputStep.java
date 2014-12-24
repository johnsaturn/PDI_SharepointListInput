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


import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.exception.KettlePluginException;
import org.pentaho.di.core.row.RowDataUtil;
import org.pentaho.di.core.row.RowMeta;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.core.row.ValueMetaInterface;
import org.pentaho.di.core.row.value.ValueMetaFactory;
import org.pentaho.di.sharepoint.connector.SharepointConnection;
import org.pentaho.di.trans.Trans;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.BaseStep;
import org.pentaho.di.trans.step.StepDataInterface;
import org.pentaho.di.trans.step.StepInterface;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.step.StepMetaInterface;
import org.pentaho.di.trans.steps.jsoninput.JsonInput;
import org.pentaho.di.trans.steps.jsoninput.JsonInputField;

/**
 * This class is used to read values from a Sharepoint list using the ODATA URL
 * 
 */
public class SharepointListInputStep extends JsonInput  {

	protected int currentRow=0;
	/**
	 * The constructor should simply pass on its arguments to the parent class.
	 * 
	 * @param s 				step description
	 * @param stepDataInterface	step data class
	 * @param c					step copy
	 * @param t					transformation description
	 * @param dis				transformation executing
	 */
	public SharepointListInputStep(StepMeta s, StepDataInterface stepDataInterface, int c, TransMeta t, Trans dis) {
		super(s, stepDataInterface, c, t, dis);
	}
	
	/**
	 * This method is called by PDI during transformation startup. 
	 * 
	 * It should initialize required for step execution
	 *  
	 * 
	 * It is mandatory that super.init() is called to ensure correct behavior.
	 * 
	 * @param smi 	step meta interface implementation, containing the step settings
	 * @param sdi	step data interface implementation, used to store runtime information
	 * 
	 * @return true if initialization completed successfully, false if there was an error preventing the step from working. 
	 *  
	 */
	public boolean init(StepMetaInterface smi, StepDataInterface sdi) {
		// Casting to step-specific implementation classes is safe
		SharepointListInputMeta meta = (SharepointListInputMeta) smi;
		BaseSharepointStepData data = (BaseSharepointStepData) sdi;

		
		boolean success= super.init(meta, data);
		

		//Get List
		SharepointConnection sc=new SharepointConnection(meta.getSite(),meta.getUsername(),meta.getPassword(),meta.getDomain());
		String list=meta.getList();
		
		//In case just the name of the list is set
		if(list.toUpperCase().indexOf("/_API/WEB/LISTS/")>0 &&  list.toUpperCase().indexOf("/ITEMS")==-1)
				list=meta.getList()+"/items";
			
		//Set the reference to the list
		data.currentURL=list;
		data.connection=sc;
			 
			
		return success;
	}	
	
 
	/**
	 * Retrieves the next Resultset if any
	 * @param data
	 * @return
	 * @throws IOException
	 */
	@SuppressWarnings("unchecked")
	protected boolean getNextResultset(BaseSharepointStepData data) throws IOException{
			

		if(data.currentURL!=null){
			
			//Retrieve the List
			data.JsonPayload= data.connection.getList(data.currentURL);
			
			if(data.JsonPayload==null){
				this.logError("Error while getting Sharepoint Payload." +data.connection.getLastHttpStatus());
				data.JsonPayload=new JSONObject();
				data.JsonPayload.put("results", new JSONArray());
				data.processedRows=0;
			} else{
				//Set reference to the next (if any)
				if(data.JsonPayload.containsKey("__next")){
					data.currentURL=(String)data.JsonPayload.get("__next");
					
					
				}
				else{
					//Last Item to be processe
					data.currentURL=null; //No more lists
				}
				//Reset current processed row
				data.processedRows=0;
				return true;
				
				
			}
		}
		return false;
		
	}
	
	/**
	 * This method is called by PDI once the step is done processing. 
	 * 
	 * The dispose() method is the counterpart to init() and should release any resources
	 * acquired for step execution like file handles or database connections.
	 * 
	 * The meta and data implementations passed in can safely be cast
	 * to the step's respective implementations. 
	 * 
	 * It is mandatory that super.dispose() is called to ensure correct behavior.
	 * 
	 * @param smi 	step meta interface implementation, containing the step settings
	 * @param sdi	step data interface implementation, used to store runtime information
	 */
	public void dispose(StepMetaInterface smi, StepDataInterface sdi) {

		// Casting to step-specific implementation classes is safe
		SharepointListInputMeta meta = (SharepointListInputMeta) smi;
		BaseSharepointStepData data = (BaseSharepointStepData) sdi;		
		super.dispose(meta, data);
	}
	

	@Override
	public boolean processRow(StepMetaInterface smi, StepDataInterface sdi)
			throws KettleException {
		
		SharepointListInputMeta meta=(SharepointListInputMeta)smi;
		BaseSharepointStepData data = (BaseSharepointStepData) sdi;		
		
		//Check that we have at least something in the payload
		if(data.JsonPayload==null){
			try {
				getNextResultset(data);
			} catch (IOException e) {
				throw new  KettleException(e);
			}
		}
		
		 
		//Get the results
		JSONArray results=(JSONArray) data.JsonPayload.get("results");
		
		;
		//Check if last row of the current set has been processed
		if(data.processedRows >=results.size()){
		
			//If it does, then try to move to the next one..
			try {
				if (!getNextResultset(data)){
					setOutputDone();
					return false;				
				}else{
					//Set it again
					results=(JSONArray) data.JsonPayload.get("results");
				}
			} catch (IOException e) {
				throw new  KettleException(e);
			}

		}
		
		
		//Get Metadata
		if(data.outputRowMeta==null){
			data.outputRowMeta = getRowMetaFromMeta(meta);
			meta.getFields( data.outputRowMeta, getStepname(), null, null, this );
			
		}

			
		//Add to output
		putRow(data.outputRowMeta, getRowDataAt(currentRow,data,meta));
		
		//Keep reading
		data.processedRows++;
		return true;
	}
	
	protected RowMeta getRowMetaFromMeta(SharepointListInputMeta meta) throws KettlePluginException{
		RowMeta rm=new RowMeta();
		
		JsonInputField[] fields=  meta.getInputFields();
		for(int i=0;i<fields.length;i++){
			JsonInputField field=fields[i];
			ValueMetaInterface vmi=ValueMetaFactory.createValueMeta( field.getName(), field.getType() );
			rm.addValueMeta(i, vmi);
		}
		
		return rm;
	}
	
	protected static SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	protected Object[] getRowDataAt(int index,BaseSharepointStepData data,SharepointListInputMeta meta){
		JsonInputField[] fields=  meta.getInputFields();
		Object[] outputRowData =  new Object[fields.length];
		 
		JSONArray results=(JSONArray) data.JsonPayload.get("results");
		
		 //Get Object
		 JSONObject obj=(JSONObject)results.get(index);
 
		 for(int i=0;i<fields.length;i++){
			 JsonInputField field=fields[i];
			 Object value=obj.get(field.getPath());
			 
			 //Apply Fix for Numbers
			 if(value!=null && field.getType()== ValueMetaInterface.TYPE_NUMBER){
				 value=  Double.parseDouble(value.toString());
			 }
			 
			 //Apply Fix for Dates
			 if(value!=null && field.getType()== ValueMetaInterface.TYPE_DATE){
				 value=  value.toString().replace("T", " ").replace("Z","");
				 try {
					value= sdf.parseObject(value.toString());
				} catch (ParseException e) {

				} 
				 
			 }			 
			 
			 outputRowData[i]=value;
			 
		 }
		 		 		 
		 return outputRowData;
		  
	}
}
