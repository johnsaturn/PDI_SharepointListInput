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

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.*;
import org.eclipse.swt.events.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.Props;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.row.RowMeta;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.core.row.ValueMeta;
import org.pentaho.di.core.row.ValueMetaInterface;
import org.pentaho.di.core.row.value.ValueMetaFactory;
import org.pentaho.di.i18n.BaseMessages;
import org.pentaho.di.sharepoint.connector.SharepointConnection;
import org.pentaho.di.trans.Trans;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.TransPreviewFactory;
import org.pentaho.di.ui.core.dialog.EnterNumberDialog;
import org.pentaho.di.ui.core.dialog.EnterTextDialog;
import org.pentaho.di.ui.core.dialog.ErrorDialog;
import org.pentaho.di.ui.core.dialog.PreviewRowsDialog;
import org.pentaho.di.ui.core.widget.*;
import org.pentaho.di.ui.trans.dialog.TransPreviewProgressDialog;
import org.pentaho.di.ui.trans.step.BaseStepDialog;
import org.pentaho.di.trans.step.BaseStepMeta;
import org.pentaho.di.trans.step.StepDialogInterface;
import org.pentaho.di.trans.steps.jsoninput.JsonInputField;

/**
 * This class is part of the demo step plug-in implementation.
 * It demonstrates the basics of developing a plug-in step for PDI. 
 * 
 * The demo step adds a new string field to the row stream and sets its
 * value to "Hello World!". The user may select the name of the new field.
 *   
 * This class is the implementation of StepDialogInterface.
 * Classes implementing this interface need to:
 * 
 * - build and open a SWT dialog displaying the step's settings (stored in the step's meta object)
 * - write back any changes the user makes to the step's meta object
 * - report whether the user changed any settings when confirming the dialog 
 * 
 */
public class SharepointListInputDialog   extends BaseStepDialog implements StepDialogInterface {


	/**
	 *	The PKG member is used when looking up internationalized strings.
	 *	The properties file with localized keys is expected to reside in 
	 *	{the package of the class specified}/messages/messages_{locale}.properties   
	 */
	private static Class<?> PKG = SharepointListInputMeta.class; // for i18n purposes

	// this is the object the stores the step's settings
	// the dialog reads the settings from it when opening
	// the dialog writes the settings to it when confirmed 
	private SharepointListInputMeta input;


	/**
	 * The constructor should simply invoke super() and save the incoming meta
	 * object to a local variable, so it can conveniently read and write settings
	 * from/to it.
	 * 
	 * @param parent 	the SWT shell to open the dialog in
	 * @param in		the meta object holding the step's settings
	 * @param transMeta	transformation description
	 * @param sname		the step name
	 */
	public SharepointListInputDialog(Shell parent, Object in, TransMeta transMeta, String sname) {
		super(parent, (BaseStepMeta) in, transMeta, sname);
		input = (SharepointListInputMeta) in;

	}


	private Button wbGetFields;

	private CTabFolder wTabFolder;
	private FormData fdTabFolder;

	private CTabItem wFileTab, wFieldsTab;

	private Composite wFileComp, wContentComp, wFieldsComp;
	private FormData fdFileComp, fdContentComp, fdFieldsComp;

	private Label wlFilename,wlODataURL,wlUsername,wlPassword,wlDomain;


	private TextVar wFilename,wODataURL,wUsername,wPassword,wDomain;
	private FormData fdlFilename,fdlODataURL,fdlUsername,fdlPassword,fdlDomain, fdFilename,fdODataURL,fdFilename3,fdPassword,fdDomain;

	private FormData fdlFieldValue, fdlSourceStreamField;
	private FormData fdFieldValue, fdSourceStreamField;
	private FormData fdOutputField,  fdConf;
	private Label wlSourceField, wlSourceStreamField;
	private CCombo wFieldValue;
	private Button wSourceStreamField;


	private TableView wFields;
	private FormData fdFields;

	private Group wOutputField;
	private Group wConf;





	private int middle;
	private int margin;
	private ModifyListener lsMod;



	@Override
	public String open() {
		Shell parent = getParent();
		Display display = parent.getDisplay();

		shell = new Shell( parent, SWT.DIALOG_TRIM | SWT.RESIZE | SWT.MAX | SWT.MIN );
		props.setLook( shell );
		setShellImage( shell, input );

		lsMod = new ModifyListener() {
			@Override
			public void modifyText( ModifyEvent e ) {
				input.setChanged();
			}
		};
		changed = input.hasChanged();

		FormLayout formLayout = new FormLayout();
		formLayout.marginWidth = Const.FORM_MARGIN;
		formLayout.marginHeight = Const.FORM_MARGIN;

		shell.setLayout( formLayout );
		shell.setText( BaseMessages.getString( PKG, "SharepointListInputDialog.DialogTitle" ) );

		middle = props.getMiddlePct();
		margin = Const.MARGIN;

		// Stepname line
		wlStepname = new Label( shell, SWT.RIGHT );
		wlStepname.setText( BaseMessages.getString( PKG, "System.Label.StepName" ) );
		props.setLook( wlStepname );
		fdlStepname = new FormData();
		fdlStepname.left = new FormAttachment( 0, 0 );
		fdlStepname.top = new FormAttachment( 0, margin );
		fdlStepname.right = new FormAttachment( middle, -margin );
		wlStepname.setLayoutData( fdlStepname );
		wStepname = new Text( shell, SWT.SINGLE | SWT.LEFT | SWT.BORDER );
		wStepname.setText( stepname );
		props.setLook( wStepname );
		wStepname.addModifyListener( lsMod );
		fdStepname = new FormData();
		fdStepname.left = new FormAttachment( middle, 0 );
		fdStepname.top = new FormAttachment( 0, margin );
		fdStepname.right = new FormAttachment( 100, 0 );
		wStepname.setLayoutData( fdStepname );

		wTabFolder = new CTabFolder( shell, SWT.BORDER );
		props.setLook( wTabFolder, Props.WIDGET_STYLE_TAB );

		// ////////////////////////
		// START OF FILE TAB ///
		// ////////////////////////
		wFileTab = new CTabItem( wTabFolder, SWT.NONE );
		wFileTab.setText( BaseMessages.getString( PKG, "SharepointListInputDialog.File.Tab" ) );

		wFileComp = new Composite( wTabFolder, SWT.NONE );
		props.setLook( wFileComp );

		FormLayout fileLayout = new FormLayout();
		fileLayout.marginWidth = 3;
		fileLayout.marginHeight = 3;
		wFileComp.setLayout( fileLayout );




		// ///////////////////////////////
		// START OF Output Field GROUP //
		// ///////////////////////////////

		wOutputField = new Group( wFileComp, SWT.SHADOW_NONE );
		props.setLook( wOutputField );
		wOutputField.setText( BaseMessages.getString( PKG, "SharepointListInputDialog.wOutputField.Label" ) );

		FormLayout outputfieldgroupLayout = new FormLayout();
		outputfieldgroupLayout.marginWidth = 10;
		outputfieldgroupLayout.marginHeight = 10;
		wOutputField.setLayout( outputfieldgroupLayout );

		// Is source string defined in a Field
		wlSourceStreamField = new Label( wOutputField, SWT.RIGHT );
		wlSourceStreamField.setText( BaseMessages.getString( PKG, "SharepointListInputDialog.wlSourceStreamField.Label" ) );
		props.setLook( wlSourceStreamField );
		fdlSourceStreamField = new FormData();
		fdlSourceStreamField.left = new FormAttachment( 0, -margin );
		fdlSourceStreamField.top = new FormAttachment( 0, margin );
		fdlSourceStreamField.right = new FormAttachment( middle, -2 * margin );
		wlSourceStreamField.setLayoutData( fdlSourceStreamField );

		wSourceStreamField = new Button( wOutputField, SWT.CHECK );
		props.setLook( wSourceStreamField );
		wSourceStreamField
		.setToolTipText( BaseMessages.getString( PKG, "SharepointListInputDialog.wSourceStreamField.Tooltip" ) );
		fdSourceStreamField = new FormData();
		fdSourceStreamField.left = new FormAttachment( middle, -margin );
		fdSourceStreamField.top = new FormAttachment( 0, margin );
		wSourceStreamField.setLayoutData( fdSourceStreamField );
		SelectionAdapter lsstream = new SelectionAdapter() {
			@Override
			public void widgetSelected( SelectionEvent arg0 ) {
				ActiveStreamField();
				input.setChanged();
			}
		};
		wSourceStreamField.addSelectionListener( lsstream );



		// If source string defined in a Field
		wlSourceField = new Label( wOutputField, SWT.RIGHT );
		wlSourceField.setText( BaseMessages.getString( PKG, "SharepointListInputDialog.wlSourceField.Label" ) );
		props.setLook( wlSourceField );
		fdlFieldValue = new FormData();
		fdlFieldValue.left = new FormAttachment( 0, -margin );
		fdlFieldValue.top = new FormAttachment( wSourceStreamField, margin );	 
		fdlFieldValue.right = new FormAttachment( middle, -2 * margin );
		wlSourceField.setLayoutData( fdlFieldValue );

		wFieldValue = new CCombo( wOutputField, SWT.BORDER | SWT.READ_ONLY );
		wFieldValue.setEditable( true );
		props.setLook( wFieldValue );
		wFieldValue.addModifyListener( lsMod );
		fdFieldValue = new FormData();
		fdFieldValue.left = new FormAttachment( middle, -margin );
		fdFieldValue.top = new FormAttachment( wSourceStreamField, margin );	 
		fdFieldValue.right = new FormAttachment( 100, -margin );
		wFieldValue.setLayoutData( fdFieldValue );
		wFieldValue.addFocusListener( new FocusListener() {
			@Override
			public void focusLost( org.eclipse.swt.events.FocusEvent e ) {
			}

			@Override
			public void focusGained( org.eclipse.swt.events.FocusEvent e ) {
				org.eclipse.swt.graphics.Cursor busy = new org.eclipse.swt.graphics.Cursor( shell.getDisplay(), SWT.CURSOR_WAIT );
				shell.setCursor( busy );
				setSourceStreamField();
				shell.setCursor( null );
				busy.dispose();
			}
		} );

		fdOutputField = new FormData();
		fdOutputField.left = new FormAttachment( 0, margin );
		fdOutputField.top =  new FormAttachment( wSourceStreamField, margin );//new FormAttachment( wFilenameList, margin );
		fdOutputField.right = new FormAttachment( 100, -margin );
		wOutputField.setLayoutData( fdOutputField );

		// ///////////////////////////////////////////////////////////
		// / END OF Output Field GROUP
		// ///////////////////////////////////////////////////////////

		// Filename line
		wlFilename = new Label( wFileComp, SWT.RIGHT );
		wlFilename.setText( BaseMessages.getString( PKG, "SharepointListInputDialog.Filename.Label" ) );
		props.setLook( wlFilename );
		fdlFilename = new FormData();
		fdlFilename.left = new FormAttachment( 0, 0 );
		fdlFilename.top = new FormAttachment( wOutputField, margin );
		fdlFilename.right = new FormAttachment( middle, -margin );
		wlFilename.setLayoutData( fdlFilename );

		wFilename = new TextVar( transMeta, wFileComp, SWT.SINGLE | SWT.LEFT | SWT.BORDER );
		props.setLook( wFilename );	    
		wFilename.addModifyListener( lsMod );
		fdFilename = new FormData();
		fdFilename.left = new FormAttachment( middle, margin );
		fdFilename.right = new FormAttachment( 100, -margin );
		fdFilename.top = new FormAttachment( wOutputField, margin );	    
		wFilename.setLayoutData( fdFilename );


		//New Added fields	    


		//--OData List Endpoint
		wlODataURL = new Label( wFileComp, SWT.RIGHT );
		wlODataURL.setText( BaseMessages.getString( PKG, "SharepointListInputDialog.Filename.Label2" ) );
		props.setLook( wlODataURL );
		fdlODataURL = new FormData();
		fdlODataURL.left = new FormAttachment( 0, 0 );
		fdlODataURL.top = new FormAttachment( wFilename, margin );
		fdlODataURL.right = new FormAttachment( middle, -margin );
		wlODataURL.setLayoutData( fdlODataURL );

		wODataURL = new TextVar( transMeta, wFileComp, SWT.SINGLE | SWT.LEFT | SWT.BORDER );
		props.setLook( wODataURL );	    
		wODataURL.addModifyListener( lsMod );
		fdODataURL = new FormData();
		fdODataURL.left = new FormAttachment( middle, margin );
		// fdFilename.right = new FormAttachment( wlFilename, -margin );
		fdODataURL.right = new FormAttachment( 100, -margin );
		fdODataURL.top = new FormAttachment( wFilename, margin );	    
		wODataURL.setLayoutData( fdODataURL );	


		//--Username
		wlUsername = new Label( wFileComp, SWT.RIGHT );
		wlUsername.setText( BaseMessages.getString( PKG, "SharepointListInputDialog.Filename.Label3" ) );
		props.setLook( wlUsername );
		fdlUsername = new FormData();
		fdlUsername.left = new FormAttachment( 0, 0 );
		fdlUsername.top = new FormAttachment( wODataURL, margin );
		fdlUsername.right = new FormAttachment( middle, -margin );
		wlUsername.setLayoutData( fdlUsername );

		wUsername = new TextVar( transMeta, wFileComp, SWT.SINGLE | SWT.LEFT | SWT.BORDER );
		props.setLook( wUsername );	    
		wUsername.addModifyListener( lsMod );
		fdFilename3 = new FormData();
		fdFilename3.left = new FormAttachment( middle, margin );
		// fdFilename.right = new FormAttachment( wlFilename, -margin );
		fdFilename3.right = new FormAttachment( 100, -margin );
		fdFilename3.top = new FormAttachment( wODataURL, margin );	    
		wUsername.setLayoutData( fdFilename3 );	

		//--Password
		wlPassword = new Label( wFileComp, SWT.RIGHT );
		wlPassword.setText( BaseMessages.getString( PKG, "SharepointListInputDialog.Filename.Label4" ) );
		props.setLook( wlPassword );
		fdlPassword = new FormData();
		fdlPassword.left = new FormAttachment( 0, 0 );
		fdlPassword.top = new FormAttachment( wUsername, margin );
		fdlPassword.right = new FormAttachment( middle, -margin );
		wlPassword.setLayoutData( fdlPassword );

		wPassword = new TextVar( transMeta, wFileComp, SWT.SINGLE | SWT.LEFT | SWT.BORDER | SWT.PASSWORD);
		props.setLook( wPassword );	    
		wPassword.addModifyListener( lsMod );
		fdPassword = new FormData();
		fdPassword.left = new FormAttachment( middle, margin );
		// fdFilename.right = new FormAttachment( wlFilename, -margin );
		fdPassword.right = new FormAttachment( 100, -margin );
		fdPassword.top = new FormAttachment( wUsername, margin );	    
		wPassword.setLayoutData( fdPassword );	

		//--Domain
		wlDomain = new Label( wFileComp, SWT.RIGHT );
		wlDomain.setText( BaseMessages.getString( PKG, "SharepointListInputDialog.Filename.Label5" ) );
		props.setLook( wlDomain );
		fdlDomain = new FormData();
		fdlDomain.left = new FormAttachment( 0, 0 );
		fdlDomain.top = new FormAttachment( wPassword, margin );
		fdlDomain.right = new FormAttachment( middle, -margin );
		wlDomain.setLayoutData( fdlDomain );

		wDomain = new TextVar( transMeta, wFileComp, SWT.SINGLE | SWT.LEFT | SWT.BORDER );
		props.setLook( wDomain );	    
		wDomain.addModifyListener( lsMod );
		fdDomain = new FormData();
		fdDomain.left = new FormAttachment( middle, margin );
		// fdFilename.right = new FormAttachment( wlFilename, -margin );
		fdDomain.right = new FormAttachment( 100, -margin );
		fdDomain.top = new FormAttachment( wPassword, margin );	    
		wDomain.setLayoutData( fdDomain );		    

		
		fdFileComp = new FormData();
		fdFileComp.left = new FormAttachment( 0, 0 );
		fdFileComp.top = new FormAttachment( 0, 0 );
		fdFileComp.right = new FormAttachment( 100, 0 );
		fdFileComp.bottom = new FormAttachment( 100, 0 );
		wFileComp.setLayoutData( fdFileComp );

		wFileComp.layout();
		wFileTab.setControl( wFileComp );

		// ///////////////////////////////////////////////////////////
		// / END OF FILE TAB
		// ///////////////////////////////////////////////////////////

		// ////////////////////////
		// START OF CONTENT TAB///
		// /
		//wContentTab = new CTabItem( wTabFolder, SWT.NONE );
		// wContentTab.setText( BaseMessages.getString( PKG, "SharepointListInputDialog.Content.Tab" ) );

		FormLayout contentLayout = new FormLayout();
		contentLayout.marginWidth = 3;
		contentLayout.marginHeight = 3;

		wContentComp = new Composite( wTabFolder, SWT.NONE );
		props.setLook( wContentComp );   
		wContentComp.setLayout( contentLayout );
		wContentComp.setVisible(false);


		// ///////////////////////////////
		// START OF Conf Field GROUP //
		// ///////////////////////////////

		wConf = new Group( wContentComp, SWT.SHADOW_NONE );
		props.setLook( wConf );
		wConf.setText( BaseMessages.getString( PKG, "SharepointListInputDialog.wConf.Label" ) );

		FormLayout ConfgroupLayout = new FormLayout();
		ConfgroupLayout.marginWidth = 10;
		ConfgroupLayout.marginHeight = 10;
		wConf.setLayout( ConfgroupLayout );



		fdConf = new FormData();
		fdConf.left = new FormAttachment( 0, margin );
		fdConf.top = new FormAttachment( 0, margin );
		fdConf.right = new FormAttachment( 100, -margin );
		wConf.setLayoutData( fdConf );

		// ///////////////////////////////////////////////////////////
		// / END OF Conf Field GROUP
		// ///////////////////////////////////////////////////////////

		// ///////////////////////////////
		// START OF Additional Fields GROUP //
		// ///////////////////////////////
		// ///////////////////////////////////////////////////////////
		// / END OF AddFileResult GROUP
		// ///////////////////////////////////////////////////////////

		fdContentComp = new FormData();
		fdContentComp.left = new FormAttachment( 0, 0 );
		fdContentComp.top = new FormAttachment( 0, 0 );
		fdContentComp.right = new FormAttachment( 100, 0 );
		fdContentComp.bottom = new FormAttachment( 100, 0 );
		wContentComp.setLayoutData( fdContentComp );
		wContentComp.layout();
		//wContentTab.setControl( wContentComp );

		// ///////////////////////////////////////////////////////////
		// / END OF CONTENT TAB
		// ///////////////////////////////////////////////////////////

		// Fields tab...
		//
		wFieldsTab = new CTabItem( wTabFolder, SWT.NONE );
		wFieldsTab.setText( BaseMessages.getString( PKG, "SharepointListInputDialog.Fields.Tab" ) );

		FormLayout fieldsLayout = new FormLayout();
		fieldsLayout.marginWidth = Const.FORM_MARGIN;
		fieldsLayout.marginHeight = Const.FORM_MARGIN;

		wFieldsComp = new Composite( wTabFolder, SWT.NONE );
		wFieldsComp.setLayout( fieldsLayout );
		props.setLook( wFieldsComp );

		wbGetFields = new Button( wFieldsComp, SWT.PUSH | SWT.CENTER );
		props.setLook( wbGetFields );
		wbGetFields.setText( BaseMessages.getString( PKG, "SharepointListInputDialog.GetFields.Button" ) );
		setButtonPositions( new Button[] { wbGetFields }, margin, null );	    

		wbGetFields.addSelectionListener( new SelectionAdapter() {
			@Override
			public void widgetSelected( SelectionEvent arg0 ) {
				getFields();
			}
		} );

		final int FieldsRows = input.getInputFields().length;

		ColumnInfo[] colinf =
				new ColumnInfo[] {
				new ColumnInfo(
						BaseMessages.getString( PKG, "SharepointListInputDialog.FieldsTable.Name.Column" ),
						ColumnInfo.COLUMN_TYPE_TEXT, false ),
						new ColumnInfo(
								BaseMessages.getString( PKG, "SharepointListInputDialog.FieldsTable.Path.Column" ),
								ColumnInfo.COLUMN_TYPE_TEXT, false ),
								new ColumnInfo(
										BaseMessages.getString( PKG, "SharepointListInputDialog.FieldsTable.Type.Column" ),
										ColumnInfo.COLUMN_TYPE_CCOMBO, ValueMeta.getTypes(), true ),
										new ColumnInfo(
												BaseMessages.getString( PKG, "SharepointListInputDialog.FieldsTable.Format.Column" ),
												ColumnInfo.COLUMN_TYPE_CCOMBO, Const.getConversionFormats() ),
												new ColumnInfo(
														BaseMessages.getString( PKG, "SharepointListInputDialog.FieldsTable.Length.Column" ),
														ColumnInfo.COLUMN_TYPE_TEXT, false ),
														new ColumnInfo(
																BaseMessages.getString( PKG, "SharepointListInputDialog.FieldsTable.Precision.Column" ),
																ColumnInfo.COLUMN_TYPE_TEXT, false ),
																new ColumnInfo(
																		BaseMessages.getString( PKG, "SharepointListInputDialog.FieldsTable.Currency.Column" ),
																		ColumnInfo.COLUMN_TYPE_TEXT, false ),
																		new ColumnInfo(
																				BaseMessages.getString( PKG, "SharepointListInputDialog.FieldsTable.Decimal.Column" ),
																				ColumnInfo.COLUMN_TYPE_TEXT, false ),
																				new ColumnInfo(
																						BaseMessages.getString( PKG, "SharepointListInputDialog.FieldsTable.Group.Column" ),
																						ColumnInfo.COLUMN_TYPE_TEXT, false ),
																						new ColumnInfo(
																								BaseMessages.getString( PKG, "SharepointListInputDialog.FieldsTable.TrimType.Column" ),
																								ColumnInfo.COLUMN_TYPE_CCOMBO, JsonInputField.trimTypeDesc, true ),
																								new ColumnInfo(
																										BaseMessages.getString( PKG, "SharepointListInputDialog.FieldsTable.Repeat.Column" ),
																										ColumnInfo.COLUMN_TYPE_CCOMBO, new String[] {
																											BaseMessages.getString( PKG, "System.Combo.Yes" ),
																											BaseMessages.getString( PKG, "System.Combo.No" ) }, true ),

		};

		colinf[0].setUsingVariables( true );
		colinf[0].setToolTip( BaseMessages.getString( PKG, "SharepointListInputDialog.FieldsTable.Name.Column.Tooltip" ) );
		colinf[1].setUsingVariables( true );
		colinf[1].setToolTip( BaseMessages.getString( PKG, "SharepointListInputDialog.FieldsTable.Path.Column.Tooltip" ) );

		wFields =
				new TableView( transMeta, wFieldsComp, SWT.FULL_SELECTION | SWT.MULTI, colinf, FieldsRows, lsMod, props );

		fdFields = new FormData();
		fdFields.left = new FormAttachment( 0, 0 );
		fdFields.top = new FormAttachment( 0, 0 );
		fdFields.right = new FormAttachment( 100, 0 );
		fdFields.bottom = new FormAttachment( 100, -margin );
		wFields.setLayoutData( fdFields );

		fdFieldsComp = new FormData();
		fdFieldsComp.left = new FormAttachment( 0, 0 );
		fdFieldsComp.top = new FormAttachment( 0, 0 );
		fdFieldsComp.right = new FormAttachment( 100, 0 );
		fdFieldsComp.bottom = new FormAttachment( 100, 0 );
		wFieldsComp.setLayoutData( fdFieldsComp );

		wFieldsComp.layout();
		wFieldsTab.setControl( wFieldsComp );



		fdTabFolder = new FormData();
		fdTabFolder.left = new FormAttachment( 0, 0 );
		fdTabFolder.top = new FormAttachment( wStepname, margin );
		fdTabFolder.right = new FormAttachment( 100, 0 );
		fdTabFolder.bottom = new FormAttachment( 100, -50 );
		wTabFolder.setLayoutData( fdTabFolder );

		wOK = new Button( shell, SWT.PUSH );
		wOK.setText( BaseMessages.getString( PKG, "System.Button.OK" ) );

		wPreview = new Button( shell, SWT.PUSH );
		wPreview.setText( BaseMessages.getString( PKG, "SharepointListInputDialog.Button.PreviewRows" ) );

		wCancel = new Button( shell, SWT.PUSH );
		wCancel.setText( BaseMessages.getString( PKG, "System.Button.Cancel" ) );

		setButtonPositions( new Button[] { wOK, wPreview, wCancel }, margin, wTabFolder );

		// Add listeners
		lsOK = new Listener() {
			@Override
			public void handleEvent( Event e ) {
				ok();
			}
		};
		lsPreview = new Listener() {
			@Override
			public void handleEvent( Event e ) {
				preview();
			}
		};
		lsCancel = new Listener() {
			@Override
			public void handleEvent( Event e ) {
				cancel();
			}
		};

		wOK.addListener( SWT.Selection, lsOK );
		wPreview.addListener( SWT.Selection, lsPreview );
		wCancel.addListener( SWT.Selection, lsCancel );

		lsDef = new SelectionAdapter() {
			@Override
			public void widgetDefaultSelected( SelectionEvent e ) {
				ok();
			}
		};

		wStepname.addSelectionListener( lsDef );

		// Whenever something changes, set the tooltip to the expanded version of the filename:
		wFilename.addModifyListener( new ModifyListener() {
			@Override
			public void modifyText( ModifyEvent e ) {
				wFilename.setToolTipText( wFilename.getText() );
			}
		} );


		// Detect X or ALT-F4 or something that kills this window...
		shell.addShellListener( new ShellAdapter() {
			@Override
			public void shellClosed( ShellEvent e ) {
				cancel();
			}
		} );

		wTabFolder.setSelection( 0 );

		// Set the shell size, based upon previous time...
		setSize();
		getData( input );
		ActiveStreamField();

		input.setChanged( changed );
		wFields.optWidth( true );

		shell.open();
		while ( !shell.isDisposed() ) {
			if ( !display.readAndDispatch() ) {
				display.sleep();
			}
		}
		return stepname;
	}

	private void setSourceStreamField() {
		try {
			String value = wFieldValue.getText();
			wFieldValue.removeAll();

			RowMetaInterface r = transMeta.getPrevStepFields( stepname );
			if ( r != null ) {
				wFieldValue.setItems( r.getFieldNames() );
			}
			if ( value != null ) {
				wFieldValue.setText( value );
			}
		} catch ( KettleException ke ) {
			new ErrorDialog(
					shell, BaseMessages.getString( PKG, "SharepointListInputDialog.FailedToGetFields.DialogTitle" ), BaseMessages
					.getString( PKG, "SharepointListInputDialog.FailedToGetFields.DialogMessage" ), ke );
		}
	}

	private void ActiveStreamField() {
		wlSourceField.setEnabled( wSourceStreamField.getSelection() );
		wFieldValue.setEnabled( wSourceStreamField.getSelection() );

		wlFilename.setEnabled( !wSourceStreamField.getSelection() );

		wFilename.setEnabled( !wSourceStreamField.getSelection() );


		wPreview.setEnabled( !wSourceStreamField.getSelection() );
	}



	/**
	 * Read the data from the TextFileInputMeta object and show it in this dialog.
	 *
	 * @param in
	 *          The TextFileInputMeta object to obtain the data from.
	 */
	public void getData( SharepointListInputMeta in ) {




		if ( in.getFieldValue() != null ) {
			wFieldValue.setText( in.getFieldValue() );
		}



		if ( isDebug() ) {
			logDebug( BaseMessages.getString( PKG, "SharepointListInputDialog.Log.GettingFieldsInfo" ) );
		}
		for ( int i = 0; i < in.getInputFields().length; i++ ) {
			JsonInputField field = in.getInputFields()[i];

			if ( field != null ) {
				TableItem item = wFields.table.getItem( i );
				String name = field.getName();
				String xpath = field.getPath();
				String type = field.getTypeDesc();
				String format = field.getFormat();
				String length = "" + field.getLength();
				String prec = "" + field.getPrecision();
				String curr = field.getCurrencySymbol();
				String group = field.getGroupSymbol();
				String decim = field.getDecimalSymbol();
				String trim = field.getTrimTypeDesc();
				String rep =
						field.isRepeated() ? BaseMessages.getString( PKG, "System.Combo.Yes" ) : BaseMessages.getString(
								PKG, "System.Combo.No" );

						if ( name != null ) {
							item.setText( 1, name );
						}
						if ( xpath != null ) {
							item.setText( 2, xpath );
						}
						if ( type != null ) {
							item.setText( 3, type );
						}
						if ( format != null ) {
							item.setText( 4, format );
						}
						if ( length != null && !"-1".equals( length ) ) {
							item.setText( 5, length );
						}
						if ( prec != null && !"-1".equals( prec ) ) {
							item.setText( 6, prec );
						}
						if ( curr != null ) {
							item.setText( 7, curr );
						}
						if ( decim != null ) {
							item.setText( 8, decim );
						}
						if ( group != null ) {
							item.setText( 9, group );
						}
						if ( trim != null ) {
							item.setText( 10, trim );
						}
						if ( rep != null ) {
							item.setText( 11, rep );
						}

			}
		}

		wFields.removeEmptyRows();
		wFields.setRowNums();
		wFields.optWidth( true );


		//-----------------------------------------
		//Sharepoint Properties.
		//-----------------------------------------
		if(in.getDomain()!=null){
			wDomain.setText(in.getDomain());
		}

		if(in.getPassword()!=null){
			wPassword.setText(in.getPassword());
		}

		if(in.getUsername()!=null){
			wUsername.setText(in.getUsername());
		}
		if(in.getList()!=null){
			wODataURL.setText(in.getList());
		}
		if(in.getSite()!=null){
			wFilename.setText(in.getSite());
		} 



		wStepname.selectAll();
		wStepname.setFocus();
	}

	private void cancel() {
		stepname = null;
		input.setChanged( changed );
		dispose();
	}

	private void ok() {
		try {
			getInfo( input );
		} catch ( KettleException e ) {
			new ErrorDialog(
					shell, BaseMessages.getString( PKG, "SharepointListInputDialog.ErrorParsingData.DialogTitle" ), BaseMessages
					.getString( PKG, "SharepointListInputDialog.ErrorParsingData.DialogMessage" ), e );
		}
		dispose();
	}

	private void getInfo( SharepointListInputMeta in ) throws KettleException {
		stepname = wStepname.getText(); // return value


		in.setReadUrl( true);
		in.setInFields( wSourceStreamField.getSelection() );
		in.setIsAFile( false);
		in.setFieldValue( wFieldValue.getText() );

		int nrFields = wFields.nrNonEmpty();

		in.allocate( 1, nrFields ); 

		for ( int i = 0; i < nrFields; i++ ) {
			JsonInputField field = new JsonInputField();

			TableItem item = wFields.getNonEmpty( i );

			field.setName( item.getText( 1 ) );
			field.setPath( item.getText( 2 ) );
			field.setType( ValueMeta.getType( item.getText( 3 ) ) );
			field.setFormat( item.getText( 4 ) );
			field.setLength( Const.toInt( item.getText( 5 ), -1 ) );
			field.setPrecision( Const.toInt( item.getText( 6 ), -1 ) );
			field.setCurrencySymbol( item.getText( 7 ) );
			field.setDecimalSymbol( item.getText( 8 ) );
			field.setGroupSymbol( item.getText( 9 ) );
			field.setTrimType( JsonInputField.getTrimTypeByDesc( item.getText( 10 ) ) );
			field.setRepeated( BaseMessages.getString( PKG, "System.Combo.Yes" ).equalsIgnoreCase( item.getText( 11 ) ) );

			//CHECKSTYLE:Indentation:OFF
			in.getInputFields()[i] = field;
		}


		//-----------------------------------------
		//Sharepoint Properties.
		//-----------------------------------------
		in.setDomain( wDomain.getText());
		in.setPassword(wPassword.getText());
		in.setUsername(wUsername.getText());
		in.setList(wODataURL.getText());
		in.setSite(wFilename.getText());
		in.setFileName(new String[]{in.getList()});
		in.setReadUrl(true);





	}

	// Preview the data
	private void preview() {
		try {
			SharepointListInputMeta oneMeta = new SharepointListInputMeta();
			getInfo( oneMeta );

			TransMeta previewMeta =
					TransPreviewFactory.generatePreviewTransformation( transMeta, oneMeta, wStepname.getText() );

			EnterNumberDialog numberDialog = new EnterNumberDialog( shell, props.getDefaultPreviewSize(),
					BaseMessages.getString( PKG, "SharepointListInputDialog.NumberRows.DialogTitle" ),
					BaseMessages.getString( PKG, "SharepointListInputDialog.NumberRows.DialogMessage" ) );

			int previewSize = numberDialog.open();
			if ( previewSize > 0 ) {
				input.setChanged();
				TransPreviewProgressDialog progressDialog =
						new TransPreviewProgressDialog(
								shell, previewMeta, new String[] { wStepname.getText() }, new int[] { previewSize } );
				progressDialog.open();

				if ( !progressDialog.isCancelled() ) {
					Trans trans = progressDialog.getTrans();
					String loggingText = progressDialog.getLoggingText();

					if ( trans.getResult() != null && trans.getResult().getNrErrors() > 0 ) {
						EnterTextDialog etd =
								new EnterTextDialog(
										shell, BaseMessages.getString( PKG, "System.Dialog.PreviewError.Title" ), BaseMessages
										.getString( PKG, "System.Dialog.PreviewError.Message" ), loggingText, true );
						etd.setReadOnly();
						etd.open();
					}
					PreviewRowsDialog prd =
							new PreviewRowsDialog(
									shell, transMeta, SWT.NONE, wStepname.getText(), progressDialog.getPreviewRowsMeta( wStepname
											.getText() ), progressDialog.getPreviewRows( wStepname.getText() ), loggingText );
					prd.open();
				}

			}

		} catch ( KettleException e ) {
			new ErrorDialog(
					shell, BaseMessages.getString( PKG, "SharepointListInputDialog.ErrorPreviewingData.DialogTitle" ), BaseMessages
					.getString( PKG, "SharepointListInputDialog.ErrorPreviewingData.DialogMessage" ), e );
		}
	}




	/**
	 * Get the list of fields in the Excel workbook and put the result in the fields table view.
	 */
	public void getFields() {
		RowMetaInterface fields = new RowMeta();

		SharepointListInputMeta info = new SharepointListInputMeta();


		int clearFields = SWT.YES;
		if ( wFields.nrNonEmpty() > 0 ) {
			MessageBox messageBox = new MessageBox( shell, SWT.YES | SWT.NO | SWT.CANCEL | SWT.ICON_QUESTION );
			messageBox.setMessage( BaseMessages.getString( PKG, "SharepointListInputDialog.ClearFieldList.DialogMessage" ) );
			messageBox.setText( BaseMessages.getString( PKG, "SharepointListInputDialog.ClearFieldList.DialogTitle" ) );
			clearFields = messageBox.open();
			if ( clearFields == SWT.CANCEL ) {
				return;
			}
		}
		//Set Sharepoint objects
		SharepointConnection sp;
		JSONArray result=null;
		try{
			getInfo( info );
			//Create Connection..
			sp= new  SharepointConnection(info.getSite(), info.getUsername(), info.getPassword(), info.getDomain());

			//Since this is just a preview just take the first 10 records
			String list=info.getList();
			int qIndex=list.indexOf("?");
			if (qIndex>0){
				list=list.substring(0,qIndex);
			}	    
			result=(JSONArray) sp.getList(list+"/fields").get("results");


		}catch(Exception e){
			new ErrorDialog( shell, BaseMessages.getString( PKG, "System.Dialog.Error.Title" ), BaseMessages
					.getString( PKG, "SharepointListInputDialog.ErrorReadingFile2.DialogMessage", info.getList(), e
							.toString() ), e );

		}
		//Parse the List Values..

		try{
			for (int i=0;i<result.size();i++) {
				try {
					//Read Definition
					JSONObject f=(JSONObject)result.get(i);
					int type=Integer.parseInt(f.get("FieldTypeKind").toString());


					//Set PDI properties
					String fieldname =f.get("StaticName").toString();
					int fieldtype = ValueMetaInterface.TYPE_STRING;


					//Map Types http://msdn.microsoft.com/en-us/library/microsoft.sharepoint.client.fieldtype(v=office.15).aspx

					switch(type){

					case 1: 
						fieldtype=ValueMetaInterface.TYPE_INTEGER;
						break;
					case 4:
						fieldtype=ValueMetaInterface.TYPE_DATE;
						break;
					case 8:
						fieldtype=ValueMetaInterface.TYPE_BOOLEAN;
						break;
					case 9:
					case 10:
						fieldtype=ValueMetaInterface.TYPE_NUMBER;
						break;
					}


					ValueMetaInterface field = ValueMetaFactory.createValueMeta( fieldname, fieldtype );
					fields.addValueMeta( field );
				}
				catch ( ArrayIndexOutOfBoundsException aioobe ) {	               

				}
			}

		}


		catch ( Exception e ) {
			new ErrorDialog( shell, BaseMessages.getString( PKG, "System.Dialog.Error.Title" ), BaseMessages
					.getString( PKG, "SharepointListInputDialog.ErrorReadingFile2.DialogMessage", result.toJSONString(), e
							.toString() ), e );
		}


		if ( fields.size() > 0 ) {
			if ( clearFields == SWT.YES ) {
				wFields.clearAll( false );
			}
			for ( int j = 0; j < fields.size(); j++ ) {
				ValueMetaInterface field = fields.getValueMeta( j );
				String mask="";
				if(field.getType()==ValueMetaInterface.TYPE_DATE){
					mask="yyyy-MM-dd HH:mm:ss";
				}
				wFields.add( new String[] { field.getName(),  field.getName(),field.getTypeDesc(), mask, "none", "N" } );
			}
			wFields.removeEmptyRows();
			wFields.setRowNums();
			wFields.optWidth( true );
			input.setChanged();
		} else {
			MessageBox mb = new MessageBox( shell, SWT.OK | SWT.ICON_WARNING );
			mb.setMessage( BaseMessages.getString( PKG, "SharepointListInputDialog.UnableToFindFields.DialogMessage" ) );
			mb.setText( BaseMessages.getString( PKG, "SharepointListInputDialog.UnableToFindFields.DialogTitle" ) );
			mb.open();
		}

	}
}