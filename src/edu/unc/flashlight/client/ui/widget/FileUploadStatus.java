/*******************************************************************************
 * Copyright 2012 The University of North Carolina at Chapel Hill.
 *  All Rights Reserved.
 * 
 *  Permission to use, copy, modify OR distribute this software and its
 *  documentation for educational, research and non-profit purposes, without
 *  fee, and without a written agreement is hereby granted, provided that the
 *  above copyright notice and the following three paragraphs appear in all
 *  copies.
 * 
 *  IN NO EVENT SHALL THE UNIVERSITY OF NORTH CAROLINA AT CHAPEL HILL BE
 *  LIABLE TO ANY PARTY FOR DIRECT, INDIRECT, SPECIAL, INCIDENTAL, OR
 *  CONSEQUENTIAL DAMAGES, INCLUDING LOST PROFITS, ARISING OUT OF THE
 *  USE OF THIS SOFTWARE AND ITS DOCUMENTATION, EVEN IF THE UNIVERSITY
 *  OF NORTH CAROLINA HAVE BEEN ADVISED OF THE POSSIBILITY OF SUCH
 *  DAMAGES.
 * 
 *  THE UNIVERSITY OF NORTH CAROLINA SPECIFICALLY DISCLAIM ANY
 *  WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
 *  MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE.  THE SOFTWARE
 *  PROVIDED HEREUNDER IS ON AN "AS IS" BASIS, AND THE UNIVERSITY OF
 *  NORTH CAROLINA HAS NO OBLIGATIONS TO PROVIDE MAINTENANCE, SUPPORT,
 *  UPDATES, ENHANCEMENTS, OR MODIFICATIONS.
 * 
 *  The authors may be contacted via:
 * 
 *  US Mail:           Dennis Goldfarb
 *                     Wei Wang
 * 
 *                     Department of Computer Science
 *                       Sitterson Hall, CB #3175
 *                       University of N. Carolina
 *                       Chapel Hill, NC 27599-3175
 * 
 *                     Ben Major
 * 
 *                     Department of Cell Biology and Physiology 
 *                       Lineberger Comprehensive Cancer Center
 *                       University of N. Carolina
 *                       Chapel Hill, NC 27599-3175
 * 
 *  Email:             dennisg@cs.unc.edu
 *                     weiwang@cs.unc.edu
 *                     ben_major@med.unc.edu
 * 
 *  Web:               www.unc.edu/~dennisg/
 ******************************************************************************/
package edu.unc.flashlight.client.ui.widget;

import gwtupload.client.IUploadStatus;
import gwtupload.client.BaseUploadStatus.BasicProgressBar;
import gwtupload.client.IUploadStatus.CancelBehavior;

import java.util.Set;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Widget;

public class FileUploadStatus implements IUploadStatus {

	private ProgressBar prg;
	 private UploadStatusConstants i18nStrs = GWT.create(UploadStatusConstants.class);
	  private IUploadStatus.Status status = Status.UNINITIALIZED;

	public FileUploadStatus() {
		prg = new ProgressBar();
	}

	public void setProgressBar(ProgressBar prg){
		this.prg = prg;
	}

	@Override
	public Widget getWidget() {
		return prg;
	}

	@Override
	public void setFileName(String name) {

	}

	@Override
	public void setError(String error) {
		setStatus(IUploadStatus.Status.ERROR);
		if (error != null && error.length() > 0)
			Window.alert(error);
	}

	@Override
	public void setVisible(boolean v) {
		prg.setVisible(v);
	}

	@Override
	public void setProgress(int a, int b) {
		prg.setProgress(100*(a/(double)b));
	}

	@Override
	public IUploadStatus newInstance() {
		FileUploadStatus newStatus = new FileUploadStatus();
		newStatus.setProgressBar(prg);
		return newStatus;		
	}

	@Override
	public HandlerRegistration addCancelHandler(UploadCancelHandler handler) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Status getStatus() {
		return status;
	}

	@Override
	public void setCancelConfiguration(Set<CancelBehavior> config) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setI18Constants(UploadStatusConstants strs) {
		this.i18nStrs = strs;
	}

	@Override
	public void setStatus(Status stat) {
		this.status = stat;
		 switch (stat) {
	      case CHANGED: case QUEUED:
	        updateStatusPanel(false, i18nStrs.uploadStatusQueued());
	        break;
	      case SUBMITING:
	        updateStatusPanel(false, i18nStrs.uploadStatusSubmitting());
	        break;
	      case INPROGRESS:
	        updateStatusPanel(true, i18nStrs.uploadStatusInProgress());
	        break;
	      case SUCCESS: case REPEATED: 
	        updateStatusPanel(true, i18nStrs.uploadStatusSuccess());
	        prg.setProgress(100);
	        break;
	      case INVALID:
	        break;
	      case CANCELING:
	        updateStatusPanel(false, i18nStrs.uploadStatusCanceling());
	        break;
	      case CANCELED:
	        updateStatusPanel(false, i18nStrs.uploadStatusCanceled());
	        break;
	      case ERROR:
	        updateStatusPanel(false, i18nStrs.uploadStatusError());
	        break;
	      case DELETED:
	        updateStatusPanel(false, i18nStrs.uploadStatusDeleted());
	        break;
	    }
	}
	
	protected void updateStatusPanel(boolean showProgress, String message) {
	    if (prg != null) {
	      prg.setVisible(showProgress);
	    }
	  }

	@Override
	public void setStatusChangedHandler(UploadStatusChangedHandler handler) {
		// TODO Auto-generated method stub

	}	

}
