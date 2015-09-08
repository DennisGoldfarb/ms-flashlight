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

import java.util.HashMap;
import java.util.Map;

import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.user.client.ui.SimplePanel;

import edu.unc.flashlight.client.ui.widget.js.ForceDirected;
import edu.unc.flashlight.shared.model.Pair;
import edu.unc.flashlight.shared.model.details.GeneInteractionDetails;
import edu.unc.flashlight.shared.model.details.InteractionDetails;
import edu.unc.flashlight.shared.model.details.MSDetails;

public class ProteinInteractionView extends SimplePanel {
	private ForceDirected canvas;
	
	public ProteinInteractionView(MSDetails details) {
		JSONArray array = new JSONArray();
		Map<String, JSONArray> adjacencies = new HashMap<String, JSONArray>();
		Map<String, Integer> nodes = new HashMap<String, Integer>();
		
		int size = 0;
		for (Pair<String,String> d : details.getNetwork()) {
			JSONObject input;
			if (!adjacencies.containsKey(d.getFirst())) {
				adjacencies.put(d.getFirst(), new JSONArray());
				nodes.put(d.getFirst(), size);
				size++;			
				input =  createNodeData(d.getFirst(), d.getFirst(), details.getBaitIds().contains(Long.parseLong(d.getFirst())));
			} else {
				input = (JSONObject) array.get(nodes.get(d.getFirst()));
			}
				
			if (!adjacencies.containsKey(d.getSecond())) {
				adjacencies.put(d.getSecond(), new JSONArray());
				nodes.put(d.getSecond(), size);
				size++;
				JSONObject inputB = createNodeData(d.getSecond(), d.getSecond(), details.getBaitIds().contains(Long.parseLong(d.getSecond())));
				inputB.put("adjacencies", new JSONArray());
				array.set(nodes.get(d.getSecond()), inputB);
			}
			
			JSONArray adj = adjacencies.get(d.getFirst());
			
			JSONObject adj_tmp = new JSONObject();
			adj_tmp.put("nodeTo",  new JSONString(d.getSecond()));
			adj_tmp.put("nodeFrom",  new JSONString(d.getFirst()));
			
			adj_tmp.put("data", createEdgeData(true, true));
			
			adj.set(adj.size(), adj_tmp);
			
			input.put("adjacencies", adj);
			
			array.set(nodes.get(d.getFirst()), input);
		}
		canvas = new ForceDirected(array);
		this.setWidget(canvas);
	}

	public ProteinInteractionView(InteractionDetails details) {
		JSONArray array = new JSONArray();
		Map<String, JSONArray> adjacencies = new HashMap<String, JSONArray>();
		Map<String, Integer> nodes = new HashMap<String, Integer>();
		
		int size = 0;
		for (GeneInteractionDetails d : details.getNetwork()) {
			JSONObject input;
			if (!adjacencies.containsKey(d.getSymbolA())) {
				adjacencies.put(d.getSymbolA(), new JSONArray());
				nodes.put(d.getSymbolA(), size);
				size++;			
				input =  createNodeData(d.getSymbolA(), d.getSymbolA(), isInteractor(details, d.getSymbolA()));
			} else {
				input = (JSONObject) array.get(nodes.get(d.getSymbolA()));
			}
				
			if (!adjacencies.containsKey(d.getSymbolB())) {
				adjacencies.put(d.getSymbolB(), new JSONArray());
				nodes.put(d.getSymbolB(), size);
				size++;
				JSONObject inputB = createNodeData(d.getSymbolB(), d.getSymbolB(), isInteractor(details, d.getSymbolB()));
				inputB.put("adjacencies", new JSONArray());
				array.set(nodes.get(d.getSymbolB()), inputB);
			}
			
			JSONArray adj = adjacencies.get(d.getSymbolA());
			
			JSONObject adj_tmp = new JSONObject();
			adj_tmp.put("nodeTo",  new JSONString(d.getSymbolB()));
			adj_tmp.put("nodeFrom",  new JSONString(d.getSymbolA()));
			
			adj_tmp.put("data", createEdgeData(d.getIsLowThroughput(), isDirect(details,d.getSymbolA(),d.getSymbolB())));
			
			adj.set(adj.size(), adj_tmp);
			
			input.put("adjacencies", adj);
			
			array.set(nodes.get(d.getSymbolA()), input);
		}
		canvas = new ForceDirected(array);
		this.setWidget(canvas);
	}
	
	public boolean isDirect(InteractionDetails details, String v1, String v2) {
		return (isInteractor(details, v1) || isInteractor(details, v2)) ;
	}
	
	public boolean isInteractor(InteractionDetails details, String v1) {
		String g1 = details.getInteractions().get(0).getGeneA().getOfficialSymbol();
		String g2 = details.getInteractions().get(0).getGeneB().getOfficialSymbol();
		return (g1.equals(v1) || g2.equals(v1)) ;
	}
	
	public JSONObject createEdgeData(boolean isLow, boolean isDirect) {
		String color = "#56A0D3";
		String lineWidth = "1";
		if (isLow) color = "#FBB917";
		if (isDirect) lineWidth = "3";

		return createEdgeData(color, lineWidth);
	}
	
	public JSONObject createEdgeData(String color, String lineWidth) {
		JSONObject data = new JSONObject();
		data.put("$color",  new JSONString(color));
		data.put("$lineWidth",  new JSONString(lineWidth));
		return data;
	}
	
	public JSONObject createNodeData(String id, String label, boolean isInteractor) {
		if (isInteractor) return createNodeData(id, label, "orange_node");
		return createNodeData(id, label, "green_node");
	}
	
	public JSONObject createNodeData(String id, String label, String type) {
		JSONObject node = new JSONObject();
		node.put("id",  new JSONString(id));
		node.put("name",  new JSONString(label));
		
		JSONObject data = new JSONObject();
		data.put("$type",  new JSONString(type));
		node.put("data", data);
		return node;
	}
}
