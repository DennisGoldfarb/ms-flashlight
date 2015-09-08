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

import com.google.gwt.event.dom.client.HasMouseDownHandlers;
import com.google.gwt.event.dom.client.HasMouseMoveHandlers;
import com.google.gwt.event.dom.client.HasMouseOutHandlers;
import com.google.gwt.event.dom.client.HasMouseUpHandlers;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Window;

import edu.unc.flashlight.client.Flashlight;


public class DraggablePopup extends ModalPopup implements MouseDownHandler, MouseMoveHandler,
										MouseUpHandler, MouseOutHandler,
										HasMouseDownHandlers, HasMouseUpHandlers, 
										HasMouseMoveHandlers, HasMouseOutHandlers {
	private boolean dragging;
	private int dragStartX, dragStartY;
	
	public DraggablePopup(String title) {
		this();
		this.setTitle(title);
	}
	
	public DraggablePopup() {
		super(false, false, false);
		this.addStyleDependentName("shadow");
		this.getTitleWidget().addMouseDownHandler(this);
		this.getTitleWidget().addMouseMoveHandler(this);
		this.getTitleWidget().addMouseUpHandler(this);	
		this.getTitleWidget().addMouseOutHandler(this);
		this.addMouseDownHandler(new MouseDownHandler() {
			@Override
			public void onMouseDown(MouseDownEvent event) {
				DOM.setStyleAttribute(DraggablePopup.this.getElement(), "zIndex", String.valueOf(1+Flashlight.getMaxZ()));
				Flashlight.setMaxZ(1+Flashlight.getMaxZ());
			}
			
		});
	}

	public void startDrag(MouseDownEvent event) {
		this.addStyleDependentName("dragging");
		dragging = true;
		dragStartX = event.getRelativeX(this.getElement());
		dragStartY = event.getRelativeY(this.getElement());
		event.preventDefault();
	}
	
	public void endDrag() {
		this.removeStyleDependentName("dragging");
		dragging = false;
	}
	
	@Override
	public void onMouseDown(MouseDownEvent event) {	
		startDrag(event);
	}

	@Override
	public void onMouseMove(MouseMoveEvent event) {
		if (dragging) {			
			int absX = Window.getScrollLeft() + event.getClientX();
			int absY = Window.getScrollTop() + event.getClientY();
			setPopupPosition(absX - dragStartX, absY - dragStartY);
		}
	}

	@Override
	public void onMouseUp(MouseUpEvent event) {
		endDrag();
	}
	
	@Override
	public void onMouseOut(MouseOutEvent event) {
		endDrag();
	}

	@Override
	public HandlerRegistration addMouseDownHandler(MouseDownHandler handler) {
		return addDomHandler(handler, MouseDownEvent.getType());
	}

	@Override
	public HandlerRegistration addMouseMoveHandler(MouseMoveHandler handler) {
		return addDomHandler(handler, MouseMoveEvent.getType());
	}

	@Override
	public HandlerRegistration addMouseUpHandler(MouseUpHandler handler) {
		return addDomHandler(handler, MouseUpEvent.getType());
	}

	@Override
	public HandlerRegistration addMouseOutHandler(MouseOutHandler handler) {
		return addDomHandler(handler, MouseOutEvent.getType());
	}

}
