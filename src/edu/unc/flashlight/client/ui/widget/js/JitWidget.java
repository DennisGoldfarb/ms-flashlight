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
package edu.unc.flashlight.client.ui.widget.js;

import java.util.LinkedHashMap;
import java.util.Map;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Widget; 

public abstract class JitWidget extends Widget implements ResizeHandler, HasClickLabelHandlers,
HasMouseOverLabelHandlers, HasMouseOutLabelHandlers {

	public JitWidget(String name, JavaScriptObject config) {
		this.name = name;
		this.config = config;
		final Element domelement = DOM.createElement("div");
		DOM.setElementProperty(domelement, "id", name);
		setElement(domelement);
		backgroundColor = 0 < DOM.getStyleAttribute(domelement, "backgroundColor").length() ? DOM
				.getStyleAttribute(getElement(), "backgroundColor") : defaultBackgroundColor;
				DOM.setStyleAttribute(domelement, "backgroundColor", backgroundColor);
				jitgwtwrappers.put(name, this);
	}

	public String getName() {
		return name;
	}

	public JavaScriptObject getClickedNode() {
		return node;
	}

	public JavaScriptObject getClickedLabel() {
		return label;
	}

	@Override
	protected void onAttach() {
		setSize("100%", "100%");
		JavaScriptObject canvas = initCanvas(name, getOffsetWidth(), getOffsetHeight(), backgroundColor);
		JavaScriptObject jit = init(canvas, config);
		jitwrappedobjects.put(name, jit);
		super.onAttach();
	}

	@Override
	public void onResize(ResizeEvent event) {
		double width = event.getWidth();
		double height = event.getHeight();
		double percentagex = width / oldWidth;
		double percentagey = height / oldHeight;
		double w = getOffsetWidth() * percentagex;
		double h = getOffsetHeight() * percentagey;
		setPixelSize((int) w, (int) h);
		resize(name, w, h);
	}

	protected abstract JavaScriptObject init(JavaScriptObject canvas, JavaScriptObject config);

	private final native JavaScriptObject initCanvas(String name, double w, double h, String bcolor) /*-{
    return new $wnd.$jit.ForceDirected(name + '_canvas', {
       'injectInto': name,
       'width': w,
       'height': h,
       'backgroundColor': bcolor});
}-*/;

	private final native void resize(String name, double w, double h) /*-{
    var jit = jitWrappedObject(name);
    jit.canvas.resize(w, h);
    jit.refresh();
    jit.controller.onAfterCompute();
}-*/;

	public final static void resizeAll(ResizeEvent event) {
		for (JitWidget jit : jitgwtwrappers.values()) {
			jit.onResize(event);
		}
		oldWidth = event.getWidth();
		oldHeight = event.getHeight();
	}

	@Override
	public HandlerRegistration addClickLabelHandler(ClickLabelHandler handler) {
		return addHandler(handler, ClickLabelEvent.getType());
	}

	@Override
	public HandlerRegistration addMouseOverLabelHandler(MouseOverLabelHandler handler) {
		return addHandler(handler, MouseOverLabelEvent.getType());
	}

	@Override
	public HandlerRegistration addMouseOutLabelHandler(MouseOutLabelHandler handler) {
		return addHandler(handler, MouseOutLabelEvent.getType());
	}

	private final void setClickedNode(JavaScriptObject node) {
		this.node = node;
	}

	private final void setClickedLabel(JavaScriptObject label) {
		this.label = label;
	}

	@SuppressWarnings("unused")
	private final static JavaScriptObject getWrappedObject(String name) {
		return jitwrappedobjects.get(name);
	}

	@SuppressWarnings("unused")
	private final static void forwardEvent(String name, JavaScriptObject event, JavaScriptObject label,
			JavaScriptObject node) {
		NativeEvent received = (NativeEvent) (null == event ? null : event.cast());
		Document document = Document.get();
		JitWidget jit = jitgwtwrappers.get(name);
		jit.setClickedLabel(label);
		jit.setClickedNode(node);
		String type = null == received ? "click" : received.getType();
		if ("click".equals(type)) {
			JSONObject nodeobject = new JSONObject(node);
			String nodeid = nodeobject.get("id").toString();
			nodeid = nodeid.substring(1, nodeid.length() - 1);
			History.newItem(name + ";" + nodeid);
			ClickLabelEvent.fire(jit, false);
		} else if ("contextmenu".equals(type)) {
			ClickLabelEvent.fire(jit, true);
		} else if ("mouseover".equals(type)) {
			MouseOverLabelEvent.fire(jit, received);
		} else if ("mouseout".equals(type)) {
			MouseOutLabelEvent.fire(jit, received);
		} else {
			GWT.log("not handled: " + type, null);
		}
	}

	private final static native void exportToJavaScript() /*-{
    jitWrappedObject = @edu.unc.flashlight.client.ui.widget.js.JitWidget::getWrappedObject(Ljava/lang/String;);
    jitWrapperForwardEvent = @edu.unc.flashlight.client.ui.widget.js.JitWidget::forwardEvent(Ljava/lang/String;Lcom/google/gwt/core/client/JavaScriptObject;Lcom/google/gwt/core/client/JavaScriptObject;Lcom/google/gwt/core/client/JavaScriptObject;);
}-*/;

	static {
		exportToJavaScript();
		oldWidth = Window.getClientWidth();
		oldHeight = Window.getClientHeight();
	}

	private final String name;
	private final JavaScriptObject config;
	private final String backgroundColor;
	private JavaScriptObject node;
	private JavaScriptObject label;
	private static final String defaultBackgroundColor = "#fff";
	private static final Map<String, JavaScriptObject> jitwrappedobjects = new LinkedHashMap<String, JavaScriptObject>();
	private static final Map<String, JitWidget> jitgwtwrappers = new LinkedHashMap<String, JitWidget>();
	private static double oldWidth;
	private static double oldHeight;

}
