package org.pentaho.di.sharepoint.steps.rest;

import org.eclipse.swt.widgets.Shell;
import org.pentaho.di.core.annotations.Step;
import org.pentaho.di.sharepoint.steps.input.SharepointListInputDialog;
import org.pentaho.di.sharepoint.steps.input.SharepointListInputStep;
import org.pentaho.di.trans.Trans;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.StepDataInterface;
import org.pentaho.di.trans.step.StepDialogInterface;
import org.pentaho.di.trans.step.StepInterface;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.step.StepMetaInterface;
import org.pentaho.di.trans.steps.rest.RestMeta;

//@Step( description="Executes REST calls to Sharepoint", name="Microsoft Sharepoint REST Client", image="icon.png",id="SharepointRestClient", categoryDescription="Lookup")
public class SharepointRestMeta extends RestMeta {

	public SharepointRestMeta() {
		super();
	}

	
	@Override
	public StepInterface getStep(StepMeta stepMeta, StepDataInterface stepDataInterface, int cnr, TransMeta transMeta, Trans disp) {
		return new SharepointRestStep(stepMeta, stepDataInterface, cnr, transMeta, disp);
	}
	
	public StepDialogInterface getDialog(Shell shell, StepMetaInterface meta, TransMeta transMeta, String name) {
		return new  org.pentaho.di.ui.trans.steps.rest.RestDialog(shell, meta, transMeta, name);
	}
}
