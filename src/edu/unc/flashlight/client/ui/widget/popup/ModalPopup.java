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
package edu.unc.flashlight.client.ui.widget.popup;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.SimplePanel;

import edu.unc.flashlight.client.Flashlight;

public class ModalPopup extends PopupPanel implements ClickHandler{	
	private FlowPanel fp = new FlowPanel();
	private SimplePanel titleContainer = new SimplePanel();
	private SimplePanel contentPanel = new SimplePanel();
	private HTML title = new HTML("Popup");
	private PushButton closeButton = new PushButton();
	
	public ModalPopup() {
		this(false, true, true);
	}
	
	public void center(boolean showIfClosed) {
		if (!showIfClosed && !this.isShowing()) return;
		if (!this.isShowing()) {
			DOM.setStyleAttribute(this.getElement(), "zIndex", String.valueOf(1+Flashlight.getMaxZ()));
			Flashlight.addPopup(Integer.valueOf(DOM.getStyleAttribute(this.getElement(), "zIndex")));
		}
		super.center();
		int numPopups = Flashlight.getNumPopups();	
	    if (!this.isGlassEnabled()) {
	    	int left = (Window.getClientWidth() - getOffsetWidth()) >> 1;
    	    int top = (Window.getClientHeight() - getOffsetHeight()) >> 1;
    	    left = Math.max(Window.getScrollLeft() + left, 0);
    	    top = Math.max(Window.getScrollTop() + top, 0);
			this.setPopupPosition(left + 30*numPopups, top + 30*numPopups);
	    }
	}
	
	public void hide() {
		if (this.isShowing()) Flashlight.removePopup();
		super.hide();
	}
	
	public ModalPopup(boolean autoHide, boolean modal, boolean glass) {
		this(autoHide,modal,glass,true);
	}	
	
	public ModalPopup(boolean autoHide, boolean modal, boolean glass, boolean close) {
		super(autoHide,modal);
		this.setGlassEnabled(glass);
		this.setStyleName("modal");
//		this.addStyleName("modal");
		fp.setStyleName("main");
		closeButton.setStylePrimaryName("modal-close");
		titleContainer.add(title);
		titleContainer.setStyleName("title");
		contentPanel.setStyleName("modal-content");
		fp.add(titleContainer);
		if (close) fp.add(closeButton);
		fp.add(contentPanel);
		
		closeButton.addClickHandler(this);
		
		this.setWidget(fp);
	}
	
	public ModalPopup(String title, boolean autoHide, boolean modal, boolean glass) {
		this(autoHide, modal, glass);
		setTitle(title);
	}
	
	public ModalPopup(String title) {
		this(title,false,true,true);
	}
	
	public void setTitle(String title) {
		this.title.setHTML(title);
	}
	
	public void setContentWidget(IsWidget w) {
		contentPanel.setWidget(w);
	}

	@Override
	public void onClick(ClickEvent event) {
		this.hide();
	}
	
	protected HTML getTitleWidget() {
		return title;
	}
}
