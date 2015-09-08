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
package edu.unc.flashlight.client.ui.view.impl;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.Widget;

import edu.unc.flashlight.client.Flashlight;
import edu.unc.flashlight.client.command.ServerOp;
import edu.unc.flashlight.client.ui.view.WelcomeView;
import edu.unc.flashlight.client.ui.view.WelcomeView.Presenter;
import edu.unc.flashlight.shared.model.upload.UploadResult;

public class WelcomeViewImpl extends Composite implements WelcomeView {
	
	private static WelcomeViewImplUiBinder uiBinder = GWT.create(WelcomeViewImplUiBinder.class);
	
	interface WelcomeViewImplUiBinder extends UiBinder<Widget, WelcomeViewImpl> {}
	
	private Presenter presenter;
	
	@UiField PushButton uploadButton;
	@UiField PushButton browseButton;
	@UiField PushButton aboutButton;
	
	public WelcomeViewImpl() {
		initWidget(uiBinder.createAndBindUi(this));
	}
	
	@UiHandler("browseButton")
	void onBrowseButtonClicked(ClickEvent e) {
		presenter.onBrowseButtonClicked();
	}
	
	@UiHandler("uploadButton")
	void onUploadButtonClicked(ClickEvent e) {
		presenter.onUploadButtonClicked();
	}
	
	@UiHandler("aboutButton")
	void onAboutButtonClicked(ClickEvent e) {
		presenter.onAboutButtonClicked();
	}
	
	public Widget asWidget() {
		return this;
	}
	
	public void setPresenter(Presenter presenter) {
		this.presenter = presenter;
	}
}
