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

import org.json.simple.JSONObject;
import org.pentaho.di.core.exception.KettleException;

import org.pentaho.di.core.row.RowMeta;

import org.pentaho.di.sharepoint.connector.SharepointConnection;

import org.pentaho.di.trans.steps.jsoninput.JsonInputData;

/**
 * 
 * This class handles the storage for row processing for this package's steps.
 * 
 * @author JohnSaturn
 */
public class BaseSharepointStepData extends JsonInputData {

	/**
	 * Holds a reference to the Payload
	 */
	protected JSONObject JsonPayload = null;

	/**
	 * Holds a reference of the current read record
	 */
	protected int processedRows = 0;

	/**
	 * Holds a reference to the current URI
	 * 
	 */
	protected String currentURL = "";

	/**
	 * Holds a reference to the connection used with this JSONInputData
	 */
	protected SharepointConnection connection;

	protected RowMeta meta;

	public BaseSharepointStepData() {
		super();
	}

	/**
	 * Resets the JSONReader
	 * 
	 * @throws KettleException
	 */
	public void resetJsonReader(SharepointListInputMeta meta)
			throws KettleException {
		boolean ignoreMissingPath = true;
/*
		// Respect previous setting
		if (this.jsonReader != null) {
			ignoreMissingPath = this.jsonReader.isIgnoreMissingPath();
		}
		// Override with our implementation
		this.jsonReader = new JsonSharepointReader(meta.getSite(),
				meta.getUsername(), meta.getPassword(), meta.getDomain());
		this.jsonReader.SetIgnoreMissingPath(ignoreMissingPath);

		*/
		
	}

}
