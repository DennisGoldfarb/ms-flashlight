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

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.ui.DecoratedPopupPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.SimplePanel;

public class MySpaceTree extends SimplePanel implements MouseOutLabelHandler, MouseOverLabelHandler, ClickLabelHandler {
	ToolTip tooltip;
	
	public MySpaceTree() {
		SpaceTree spacetree = new SpaceTree("myspacetree", config("myspacetree"));
		spacetree.addClickLabelHandler(this);
		spacetree.addMouseOverLabelHandler(this);
		spacetree.addMouseOutLabelHandler(this);
		setWidget(spacetree);
		DeferredCommand.addCommand(new Command() {
			public void execute() {
				loadAndDisplay("myspacetree", null);
			}
		});
	}



	private native JavaScriptObject config(String name) /*-{
    return {
    orientation: 'left',
    duration: 333,
    fps: 25,
    levelDistance: 32,
    levelsToShow: 5,
    Node: {
        width: 160,
        height: 20,
        type: 'rectangle',
        color: '#7aa',
        overridable: true },
    Edge: {
        type: 'bezier',
        overridable: true },
    onCreateLabel: function(label, node) {
        label.id = node.id;
        if (0 < node.data.childcount && !node.selected) {
        	label.innerHTML = node.name + "  " + node.data.childcount;
        	node.data.$color = "#aa7";
        } else {
        	label.innerHTML = node.name;
        	delete node.data.$color;
        }
        var forwardEvent = function(event) {
            jitWrapperForwardEvent(name, event || $wnd.event, label, node); };
        label.onclick = function(event) {
            jitWrappedObject(name).onClick(node.id);
            forwardEvent(event); };
        label.oncontextmenu = forwardEvent;
        label.onmouseover = forwardEvent;
        label.onmouseout = forwardEvent;
        var style = label.style;
        style.display = 'block';
        style.width = '160px';
        style.height ='20px';
        style.cursor = 'pointer';
        style.color = '#222';
        style.fontSize = '0.7em';
        style.textAlign= 'left';
        style.paddingTop = '2px';
        style.paddingLeft = '2px';
    },
    onBeforePlotNode: function(node){
        if (node.selected) {
            node.data.$color = "#ff7";
        } else {
            delete node.data.$color;
        }
    },
    onBeforePlotLine: function(adj){
        if (adj.nodeFrom.selected && adj.nodeTo.selected) {
            adj.data.$color = "#44d";
            adj.data.$lineWidth = 1.2;
        } else {
            delete adj.data.$color;
            delete adj.data.$lineWidth;
        }
    }
    }
}-*/;

	private native void loadAndDisplay(String name, JavaScriptObject json)  /*-{
    var jit = jitWrappedObject(name);
    jit.loadJSON(json);
    jit.compute();
    jit.onClick(jit.root);
    jitWrapperOnClick(name, null, json);
}-*/;

	@Override
	public void onClickLabel(ClickLabelEvent event) {
		JitWidget jit = (JitWidget) event.getSource();
		//.dump("node", jit.getClickedNode());
	}

	@Override
	public void onRightClickLabel(ClickLabelEvent event) {
		JitWidget jit = (JitWidget) event.getSource();
		//dump("label", jit.getClickedLabel());
	}

	@Override
	public void onMouseOverLabel(MouseOverLabelEvent event) {
		JitWidget jit = (JitWidget) event.getSource();
		JSONObject json = new JSONObject(jit.getClickedNode());
		PopupPanel popup= new DecoratedPopupPanel();
		StringBuilder b = new StringBuilder();
		for (String k : json.keySet()) {
			b.append(k);
			b.append(" : ");
			b.append(json.get(k).isString());
			b.append("");
		}
		popup.setWidth("200px");
		popup.setWidget(new HTML(b.toString()));
		int x = event.getNativeEvent().getClientX();
		int y = event.getNativeEvent().getClientY();
		tooltip = new ToolTip(2000, popup, x, y);
	}

	@Override
	public void onMouseOutLabel(MouseOutLabelEvent event) {
		tooltip.cancel();
	}

	//@Override
	public void onValueChange(ValueChangeEvent<String> event) {
		String token = event.getValue();
		String name = token.substring(0, token.indexOf(";"));
		String nodeid = token.substring(token.indexOf(";") + 1);
		clickNode(name, nodeid);
	};

	private native void clickNode(String name, String nodeid) /*-{
    var jit = jitWrappedObject(name);
    jit.onClick(nodeid);
    jit.controller.onAfterCompute();
}-*/;
}
