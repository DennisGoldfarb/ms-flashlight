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
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiFactory;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

import edu.unc.flashlight.client.ui.view.BrowseView;
import edu.unc.flashlight.client.ui.widget.AsyncSuggestOracle;
import edu.unc.flashlight.client.ui.widget.ExperimentResultTable;

public class BrowseViewImpl extends Composite implements BrowseView {
	
	private static BrowseViewImplUiBinder uiBinder = GWT.create(BrowseViewImplUiBinder.class);
	
	interface BrowseViewImplUiBinder extends UiBinder<Widget, BrowseViewImpl> {}
	
	private Presenter presenter;
	private final AsyncSuggestOracle mySuggestions = new AsyncSuggestOracle();
	
	@UiField ExperimentResultTable dataTable;
	@UiField Button searchButton;
	@UiField (provided = true) SuggestBox searchBox;
	
	public BrowseViewImpl() {
		searchBox = new SuggestBox(mySuggestions);
		searchBox.getTextBox().addKeyUpHandler(new KeyUpHandler() {
			public void onKeyUp(KeyUpEvent e) {
				if (e.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
					updateTable(searchBox.getText());
				}
			}			
		});
		initWidget(uiBinder.createAndBindUi(this));
	}
	
	public void updateTable(String sym) {
		dataTable.setSearchText(sym);
		dataTable.refreshTable();
	}
	
	public Widget asWidget() {
		return this;
	}
	
	public void setPresenter(Presenter presenter) {
		this.presenter = presenter;
	}
	
	@UiFactory ExperimentResultTable makeExperimentResultTable() { // method name is insignificant
		return new ExperimentResultTable(true);
	}
	
	@UiHandler("searchButton")
	void onSearchButtonClicked(ClickEvent e) {
		updateTable(searchBox.getText());
	}
	
	@UiHandler("searchBox")
	void onKeyDown(KeyDownEvent e) {
	  if (e.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
		  updateTable(searchBox.getText());
	  }
	}
}
