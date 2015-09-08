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

import edu.unc.flashlight.client.Flashlight;
import edu.unc.flashlight.client.ui.event.FileUploadCompleteEvent;
import edu.unc.flashlight.client.ui.event.FileUploadStartEvent;
import edu.unc.flashlight.shared.model.SAINT.SaintParameters;
import edu.unc.flashlight.shared.util.Constants.MS_ALGORITHMS;
import gwtupload.client.IFileInput.FileInputType;
import gwtupload.client.IUploadStatus;
import gwtupload.client.ModalUploadStatus;
import gwtupload.client.Uploader;
import gwtupload.client.IUploadStatus.Status;
import gwtupload.client.IUploader.UploaderConstants;
import gwtupload.client.SingleUploader;

import java.util.Set;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiConstructor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Focusable;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.HasEnabled;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.Widget;

public class SingleUploadWidget extends Uploader implements HasClickHandlers {

	private Widget submitButton;
	private boolean isFirstSuccess = false;
	private boolean isParsing = false;
	private long role = -1;
	private MS_ALGORITHMS alg = MS_ALGORITHMS.CompPASS;
	private Set<Long> selectedIds;
	private boolean useRandomForest = true;
	private SaintParameters saintParameters = new SaintParameters();
	private HandlerRegistration clickHandlerRegistration;

	public SingleUploadWidget() {
		this(FileInputType.BROWSER_INPUT);
	}

	public HandlerRegistration addClickHandler(ClickHandler handler)  {
		clickHandlerRegistration.removeHandler();
		return submitButton.addHandler(handler, ClickEvent.getType());
	}

	public void setRole(long role) {
		this.role = role;
	}

	public void setMSAlgorithm(MS_ALGORITHMS alg) {
		this.alg = alg;
	}

	public void setSelectedIds(Set<Long> selectedIds) {
		this.selectedIds = selectedIds;
	}

	@UiConstructor
	public SingleUploadWidget(FileInputType type) {
		this(type, new FileUploadStatus());

	}

	public SingleUploadWidget(FileInputType type, IUploadStatus status) {
		this(type, status, new Button());
	}

	public SingleUploadWidget(FileInputType type, IUploadStatus status, Widget submitButton) {
		this(type, status, submitButton, null);
	}

	public SingleUploadWidget(FileInputType type, IUploadStatus status, Widget submitButton, FormPanel form) {
		super(type, form);
		if (status == null) status = new ModalUploadStatus();
		super.setStatusWidget(status);

		submitButton.addStyleName("submit");
		this.submitButton = submitButton;
		final Uploader thisInstance = this;

		clickHandlerRegistration = ((HasClickHandlers)submitButton).addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				thisInstance.submit();
			}
		});

		((HasText)submitButton).setText(I18N_CONSTANTS.uploaderSend());
		// The user could have attached the button anywhere in the page.
		if (!submitButton.isAttached()) super.add(submitButton);

		setButtonEnabled(false);
		getFileInput().addChangeHandler(new ChangeHandler() {
			public void onChange(ChangeEvent event) {
				getFileInput().setText(getBasename());
				setButtonEnabled(true);
			}	
		});
	}

	@Override
	protected void onFinishUpload() {
		if (getStatus() == Status.SUCCESS) {
			isFirstSuccess = !isFirstSuccess;
			if (isFirstSuccess) {		
				Flashlight.eventBus.fireEvent(new FileUploadCompleteEvent(getBasename(), this, role, alg, selectedIds, useRandomForest, saintParameters));
			}
		} else {
			isFirstSuccess = false;
		}
		super.onFinishUpload();
		if (getStatus() == Status.REPEATED) {
			getStatusWidget().setError(getI18NConstants().uploaderAlreadyDone());
		}
		getStatusWidget().setStatus(Status.UNINITIALIZED);
		reuse();
		assignNewNameToFileInput();
		if (submitButton != null) {
			setEnabledButton(true);
			submitButton.removeStyleName("changed");
		}
		if (autoSubmit) {
			getFileInput().setText(i18nStrs.uploaderBrowse());
		}
	}

	private void resetGUI() {
		this.getFileInput().setText(i18nStrs.uploaderBrowse());
		setButtonEnabled(false);
	}

	@Override
	protected void onStartUpload() {
		isFirstSuccess = false;
		Flashlight.eventBus.fireEvent(new FileUploadStartEvent(role, alg, selectedIds));
		resetGUI();
		super.onStartUpload();
		if (submitButton != null) {
			setEnabledButton(false);
			submitButton.removeStyleName("changed");
		}
	}
	
	@Override
	  public void setI18Constants(UploaderConstants strs) {
		    super.setI18Constants(strs);
		    if (submitButton != null && submitButton instanceof HasText) {
		      ((HasText)submitButton).setText(strs.uploaderSend());
		    }
		  }

	@Override
	protected void onChangeInput() {
		super.onChangeInput();
		if (submitButton != null) {
			submitButton.addStyleName("changed");
			if (submitButton instanceof Focusable) {
				((Focusable)submitButton).setFocus(true);
			}
		}
	}

	public void setButtonEnabled(boolean enabled) {
		if (!(enabled && isParsing))
			((Button)submitButton).setVisible(enabled);
	}

	private void setEnabledButton(boolean b) {
		if (submitButton != null) {
			// HasEnabled is only available after gwt-2.1.x
			if (submitButton instanceof HasEnabled) {
				((HasEnabled)submitButton).setEnabled(b);
			} else if (submitButton instanceof Button) {
				((Button)submitButton).setEnabled(b);
			}
		}
	}

	public void setStatusWidget(IUploadStatus status) {
		super.setStatusWidget(status);
	}

	public void setProgressBar(ProgressBar prg) {
		((FileUploadStatus) super.getStatusWidget()).setProgressBar(prg);
	}

	public void setIsParsing(boolean isParsing) {
		this.isParsing = isParsing;
	}

	public void setUseRandomForest(boolean useRandomForest) {
		this.useRandomForest = useRandomForest;
	}

	public boolean getUseRandomForest() {
		return useRandomForest;
	}

	public void setSaintParameters(SaintParameters saintParameters) {
		this.saintParameters = saintParameters;
	}

	public SaintParameters getSaintParameters() {
		return saintParameters;
	}
}
