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
import com.google.gwt.dom.client.Element;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Widget;

public class ForceDirected extends Widget {
	Element div;
	
	private JavaScriptObject jso;
	private JSONValue data;

	public ForceDirected(JSONValue data) {
		div = DOM.createDiv();
		div.setId("gwt-jit-container");
		
		setElement(div);
		setStyleName("gwt-jit-container");
		this.data = data;
	}
	
	@Override
	public void onAttach() {
		jso = createJso(div);
		initNodes();
		setData(jso, ((JSONArray) data).getJavaScriptObject());
		super.onAttach();
	}

	protected native void setData(JavaScriptObject o, JavaScriptObject data) /*-{
		o.loadJSON(data);
		o.refresh();
	}-*/;
	
	
	
	protected native JavaScriptObject createJso(Element parent) /*-{
		
		var labelType, useGradients, nativeTextSupport, animate;
		
		var ua = navigator.userAgent,
      iStuff = ua.match(/iPhone/i) || ua.match(/iPad/i),
      typeOfCanvas = typeof HTMLCanvasElement,
      nativeCanvasSupport = (typeOfCanvas == 'object' || typeOfCanvas == 'function'),
      textSupport = nativeCanvasSupport 
        && (typeof document.createElement('canvas').getContext('2d').fillText == 'function');
	  //I'm setting this based on the fact that ExCanvas provides text support for IE
	  //and that as of today iPhone/iPad current text support is lame
	  labelType = (!nativeCanvasSupport || (textSupport && !iStuff))? 'Native' : 'HTML';
	  nativeTextSupport = labelType == 'Native';
	  useGradients = nativeCanvasSupport;
	  animate = !(iStuff || !nativeCanvasSupport);
  
	    var fd = new $wnd.$jit.ForceDirected({
    	//id of the visualization container
    	injectInto: parent.id,
	    //Enable zooming and panning
	    //by scrolling and DnD
	    Navigation: {
	      enable: false,
	      //Enable panning events only if we're dragging the empty
	      //canvas (and not a node).
	      panning: 'avoid nodes',
	      zooming: 30 //zoom speed. higher is more sensible
	    },
	    // Change node and edge styles such as
	    // color and width.
	    // These properties are also set per node
	    // with dollar prefixed data-properties in the
	    // JSON structure.
	    Node: {
	      overridable: true
	    },
	    Edge: {
	      overridable: true,
	      color: '#23A4FF',
	      lineWidth: 0.4
	    },
	    //Native canvas text styling
	    Label: {
	      type: 'HTML', //Native or HTML
	      size: 10,
	      style: 'bold',
	    },
	    //Add Tips
	    Tips: {
	      enable: true,
	      onShow: function(tip, node) {
	        //count connections
	        var count = 0;
	        node.eachAdjacency(function() { count++; });
	        //display node info in tooltip
	        tip.innerHTML = "<div class=\"tip-title\">" + node.name + "</div>"
	          + "<div class=\"tip-text\"><b>connections:</b> " + count + "</div>";
	      }
	    },
	    // Add node events
	    Events: {
	      enable: true,
	      type: 'Native',
	      //Change cursor style when hovering a node
	      onMouseEnter: function() {
	        fd.canvas.getElement().style.cursor = 'move';
	      },
	      onMouseLeave: function() {
	        fd.canvas.getElement().style.cursor = '';
	      },
	      //Update node positions when dragged
	      onDragMove: function(node, eventInfo, e) {
	          var pos = eventInfo.getPos();
	          node.pos.setc(pos.x-10, pos.y-10);
	          fd.plot();
	      },
	      //Implement the same handler for touchscreens
	      onTouchMove: function(node, eventInfo, e) {
	        $jit.util.event.stop(e); //stop default touchmove event
	        fd.onDragMove(node, eventInfo, e);
	      },
	      //Add also a click handler to nodes
	      onClick: function(node) {
	        if(!node) return;
	        //$wnd.alert("clicked! " + node.id);
	      }
	    },
	    //Number of iterations for the FD algorithm
	    iterations: 200,
	    //Edge length
	    levelDistance: 100,
	    // Add text to the labels. This method is only triggered
	    // on label creation and only for DOM labels (not native canvas ones).
	    onCreateLabel: function(domElement, node){
	      domElement.innerHTML = node.name;
	      var style = domElement.style;
	      style.fontSize = "0.8em";
	      style.color = "#555";
	    },
	    // Change node styles when DOM labels are placed
	    // or moved.
	    onPlaceLabel: function(domElement, node){
	      var style = domElement.style;
	      var left = parseInt(style.left);
	      var top = parseInt(style.top);
	      var w = domElement.offsetWidth;
	      style.left = (left - w / 2) + 'px';
	      style.top = (top + 10) + 'px';
	      style.display = '';
	    }
	  });
	  return fd;
	}-*/;
	
	protected native void initNodes() /*-{
		var render_img = function(node, canvas, img_path) {
		  	var width = node.getData('width'),  
			height = node.getData('height'),  
			posX = node.pos.getc(true).x-10,
			posY = node.pos.getc(true).y-10,
			radius = 10
			
			var ctx = canvas.getCtx();
			var img = new Image();
			
			img.onload = function() {
				ctx.drawImage(img, posX, posY, radius*2, radius*2);
			};
			img.src = img_path;
		  }
		
		$wnd.$jit.ForceDirected.Plot.NodeTypes.implement({  
		
		'cyan_node': {  
		'render': function(node, canvas) {  
			render_img(node, canvas, "images/Cyan.png");
		},
		'contains': function(node, pos) {
			return this.nodeHelper.circle.contains({ x: node.pos.getc(true).x-10, y: node.pos.getc(true).y-10 }, pos, 20);	
		}
		}, 
		
		'orange_node': {  
		'render': function(node, canvas) {  
			render_img(node, canvas, "images/Orange.png");		
		},
		'contains': function(node, pos) {
			return this.nodeHelper.circle.contains({ x: node.pos.getc(true).x+10, y: node.pos.getc(true).y+10 }, pos, 10);			
		}
		}, 
		
		'red_node': {  
		'render': function(node, canvas) {  
			render_img(node, canvas, "images/Red.png");
		},
		'contains': function(node, pos) {
			return this.nodeHelper.circle.contains({ x: node.pos.getc(true).x-10, y: node.pos.getc(true).y-10 }, pos, 20);	
		}
		}, 
		
		'brown_node': {  
		'render': function(node, canvas) {  
			render_img(node, canvas, "images/Brown.png");
		},
		'contains': function(node, pos) {
			return this.nodeHelper.circle.contains({ x: node.pos.getc(true).x-10, y: node.pos.getc(true).y-10 }, pos, 20);	
		}
		}, 
		
		'blue_node': {  
		'render': function(node, canvas) {  
			render_img(node, canvas, "images/Blue.png");
		},
		'contains': function(node, pos) {
			return this.nodeHelper.circle.contains({ x: node.pos.getc(true).x-10, y: node.pos.getc(true).y-10 }, pos, 20);	
		}
		}, 
		
		'green_node': {  
		'render': function(node, canvas) {  
			render_img(node, canvas, "images/Green.png");
		},
		'contains': function(node, pos) {
			return this.nodeHelper.circle.contains({ x: node.pos.getc(true).x+10, y: node.pos.getc(true).y+10 }, pos, 10);	
		}
		}, 
		
		'gray_node': {  
		'render': function(node, canvas) {  
			render_img(node, canvas, "images/Gray.png");
		},
		'contains': function(node, pos) {
			return this.nodeHelper.circle.contains({ x: node.pos.getc(true).x-10, y: node.pos.getc(true).y-10 }, pos, 20);	
		}
		}, 
		
		'black_node': {  
		'render': function(node, canvas) {  
			render_img(node, canvas, "images/Black.png");
		},
		'contains': function(node, pos) {
			return this.nodeHelper.circle.contains({ x: node.pos.getc(true).x-10, y: node.pos.getc(true).y-10 }, pos, 20);	
		}
		}
		
		
		
	
		})
	}-*/;
}
