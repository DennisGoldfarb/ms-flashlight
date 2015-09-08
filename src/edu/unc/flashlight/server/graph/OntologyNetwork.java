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
package edu.unc.flashlight.server.graph;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import edu.unc.flashlight.shared.model.GeneAnnotation;
import edu.unc.flashlight.shared.model.Pair;

public class OntologyNetwork {
	
	public static String network2JSON(List<Pair<String,String>> net, Set<GeneAnnotation> a1, Set<GeneAnnotation> a2) {
		String json = "{";
		String nodes_json = "\"nodes\":[";
		String edges_json = "\"edges\":[";
		Set<String> nodes = new HashSet<String>();
		Set<String> a1_strings = new HashSet<String>();
		Set<String> a2_strings = new HashSet<String>();
		for (GeneAnnotation ga : a1) {
			a1_strings.add(ga.getOntologyTerm().getName());
		}
		for (GeneAnnotation ga : a2) {
			a2_strings.add(ga.getOntologyTerm().getName());
		}
		if (net.size() == 0 && a1_strings.size() > 0) {
			nodes_json += addNode(a1_strings.iterator().next(),nodes);
			edges_json += ",";
		}
		for (Pair<String,String> edge : net) {
			nodes_json += addNode(edge.getFirst(),nodes);
			nodes_json += addNode(edge.getSecond(),nodes);
			edges_json += addEdge(edge);
		}
		nodes_json = nodes_json.substring(0, nodes_json.length()-1) + "],";
		edges_json = edges_json.substring(0, edges_json.length()-1) + "]";
		json += nodes_json + edges_json + "}";
		return json;
	}
	
	public static String addEdge(Pair<String,String> edge) {
		return "{\"s\":\"" + edge.getFirst() + "\",\"d\":\"" + edge.getSecond() + "\",\"t\":1},";
	}
	
	public static String addNode(String node, Set<String> nodes) {
		if (!nodes.contains(node)) {
			nodes.add(node);
			return "{\"n\":\"" + node + "\".\"g\":" + "1},";
		}
		return "";
	}
/*	private Map<String, OntologyTerm> ontology_map;
	private OntologyTerm root;
	
	public OntologyNetwork() {
		ontology_map = new HashMap<String, OntologyTerm>();
	}
	
	public OntologyNetwork(Collection<OntologyTerm> terms) {
		this();
		addTerms(terms);
	}
	
	public void addTerms(Collection<OntologyTerm> terms) {
		for (OntologyTerm ot : terms) {
			ontology_map.put(ot.getId(), ot);
			if (isRoot(ot)) root = ot;
		}	
	}
	
	public Map<String, OntologyTerm> getOntologyMap() {
		return ontology_map;
	}
	
	public Set<OntologyTerm> getPathRecursive(OntologyTerm t, Set<OntologyTerm> path) {
		t = ontology_map.get(t.getId());
		for (OntologyTerm p: t.getParents()) {
			path.addAll(getPathRecursive(ontology_map.get(p.getId()), path));
		}
		path.add(t);
		return path;
	}
	
	public List<OntologyTerm> getPathToTerm(OntologyTerm t, OntologyTerm target, List<OntologyTerm> path) {
		t = ontology_map.get(t.getId());
		if (t.equals(target)) return path;
		if (isRoot(t)) return null;
		List<List<OntologyTerm>> paths = new ArrayList<List<OntologyTerm>>();
		for (OntologyTerm p: t.getParents()) {
			if (!p.equals(target)) {
				List<OntologyTerm> temp_path = new ArrayList<OntologyTerm>();
				temp_path.addAll(path);
				temp_path.add(ontology_map.get(p.getId()));
				paths.add(getPathToTerm(ontology_map.get(p.getId()), target, temp_path));
			} else {
				paths.add(path);
			}
		}
		List<OntologyTerm> shortestPath = null;
		for (List<OntologyTerm> next_path : paths) {
			if (shortestPath == null || (next_path != null && next_path.size() < shortestPath.size())) {
				shortestPath = next_path;
			}
		}
		return shortestPath;
	}
	
	public BestParentDetails getBestParent(OntologyTerm t1, OntologyTerm t2) {
		Set<OntologyTerm> t1_paths = getPathRecursive(t1, new HashSet<OntologyTerm>());
		Set<OntologyTerm> t2_paths = getPathRecursive(t2, new HashSet<OntologyTerm>());
		
		Set<OntologyTerm> t1_paths_temp = new HashSet<OntologyTerm>();
		t1_paths_temp.addAll(t1_paths);
		t1_paths_temp.retainAll(t2_paths);
		
		OntologyTerm bestParent = null;
		for (OntologyTerm parent : t1_paths_temp) {
			if (bestParent == null || parent.getIc() > bestParent.getIc()) {
				bestParent = parent;
			}
		}	
		
		Set<OntologyTerm> parent_path =  getPathRecursive(bestParent, new HashSet<OntologyTerm>());
		
		t1_paths.removeAll(parent_path);
		t2_paths.removeAll(parent_path);
		
		BestParentDetails details = new BestParentDetails(getPathToTerm(t1, bestParent, new ArrayList<OntologyTerm>()), 
				getPathToTerm(t2, bestParent, new ArrayList<OntologyTerm>()), bestParent);
		return details;
	}
	
	public OntologyDetails getOntologyScore(Collection<GeneAnnotation> terms_a, Collection<GeneAnnotation> terms_b, OntologyDetails details) {
		OntologyTerm bestParent = root;
		OntologyTerm child_a = root;
		OntologyTerm child_b = root;
		BestParentDetails bestParentDetails = getBestParent(root,root);
		for (GeneAnnotation t1 : terms_a) {
			for (GeneAnnotation t2 : terms_b) {
				BestParentDetails parentDetails = getBestParent(t1.getOntologyTerm(), t2.getOntologyTerm());
				if (bestParent == null || parentDetails.term.getIc() > bestParent.getIc() || 
						( parentDetails.term.getIc() == bestParent.getIc() && 
							(child_a.getIc() <= t1.getOntologyTerm().getIc() && child_b.getIc() <= t2.getOntologyTerm().getIc()))) {
					bestParent = parentDetails.term;
					bestParentDetails = parentDetails;
					child_a = t1.getOntologyTerm();
					child_b = t2.getOntologyTerm();
				}
			}
		}
		details.setChildA(child_a);
		details.setChildB(child_b);
		details.setParent(bestParent);
		
		OrgDataTable data = new OrgDataTable();

	    data = createTable(ontology_map.get(child_a.getId()), ontology_map.get(child_b.getId()), bestParentDetails);
	    
	    details.setDataTable(data);
		return details;
	}
	
	private OrgDataTable createTable(OntologyTerm child_a, OntologyTerm child_b, BestParentDetails parentDetails) {
		OrgDataTable data = new OrgDataTable();
		
		if (child_a.equals(child_b) && child_a.equals(parentDetails.term)) {// Only one node
			if (isRoot(child_a)) {
				data = displayChildren(data, child_a, 2, child_a, child_b, parentDetails.term);
			} else if (isLeaf(child_a)){
				data = displayParent(data, child_a, 2, child_a, child_b, parentDetails.term);			
			} else {
				data = displayParent(data, child_a, 1, child_a, child_b, parentDetails.term);
				data = displayChildren(data, child_a, 1, child_a, child_b, parentDetails.term);
			}
		} else if (child_a.equals(parentDetails.term) || child_b.equals(parentDetails.term)) { // Two nodes
			OntologyTerm real_child = (child_a.equals(parentDetails.term)) ? child_b : child_a;
			List<OntologyTerm> real_path = (child_a.equals(parentDetails.term)) ? parentDetails.path1 : parentDetails.path2;
			if (real_path.size() == 0) {
				if (isRoot(parentDetails.term)) {
					data = displayChildren(data, real_child, 1, child_a, child_b, parentDetails.term);
					data = addTerm(data, real_child, parentDetails.term, getColor(parentDetails.term, child_a, child_b, real_child), "Red");
				} else {
					data = displayParent(data, parentDetails.term, 1, child_a, child_b, parentDetails.term);
					data = addTerm(data, real_child, parentDetails.term, getColor(parentDetails.term, child_a, child_b, real_child), "Red");
				}
			} else if (real_path.size() == 1) {
				data = addTerm(data, real_path.get(0), parentDetails.term, "", "Red");
			} else {
				data = addCountTerm(data, real_child, real_path.size(), parentDetails.term, getColor(parentDetails.term, child_a, child_b, real_child));
			}
		} else { // Three nodes
			if (parentDetails.path1.size() == 0 && parentDetails.path2.size() == 0) {
				if (isRoot(parentDetails.term)) {
					data = displayChildren(data, child_a, 1, child_a, child_b, parentDetails.term);
					data = displayChildren(data, child_b, 1, child_a, child_b, parentDetails.term);
				} else {
					data = displayParent(data, parentDetails.term, 1, child_a, child_b, parentDetails.term);
				}
			}
			if (parentDetails.path1.size() == 1) {
				data = addTerm(data, child_a, parentDetails.path1.get(0), getColor(parentDetails.term, child_a, child_b, child_a), "");
				data = addTerm(data, parentDetails.path1.get(0), parentDetails.term, "", "Red");
			} else if (parentDetails.path1.size() > 1) {
				data = addCountTerm(data, child_a, parentDetails.path1.size(), parentDetails.term, "#56A0D3");
			} else {
				data= addTerm(data, child_a, parentDetails.term, getColor(parentDetails.term, child_a, child_b, child_a), "Red");
			}
			if (parentDetails.path2.size() == 1) {
				data = addTerm(data, child_b, parentDetails.path2.get(0), getColor(parentDetails.term, child_a, child_b, child_b), "");
				data = addTerm(data, parentDetails.path2.get(0), parentDetails.term, "", "Red");
			} else if (parentDetails.path2.size() > 1) {
				data = addCountTerm(data, child_b, parentDetails.path2.size(), parentDetails.term, "#25B52B");
			} else {
				data= addTerm(data, child_b, parentDetails.term, getColor(parentDetails.term, child_a, child_b, child_b), "Red");
			}
		}
		return data;
	}
	
	private OrgDataTable addCountTerm(OrgDataTable data, OntologyTerm t, int count, OntologyTerm p, String color) {
		data.addRow(getOrgCountTermHTML(count), getOrgTermHTML(p, "Red"));
		data.addRow(getOrgTermHTML(t, color), getOrgCountTermHTML(count));
		return data;
	}
	
	private OrgDataTable addChildCountTerm(OrgDataTable data, int count, OntologyTerm p, String color) {
		data.addRow(getOrgChildCountTermHTML(count), getOrgTermHTML(p, color));
		return data;
	}
	
	private OrgDataTable addTerm(OrgDataTable data, OntologyTerm term, OntologyTerm parent, String color, String p_color) {
		data.addRow(getOrgTermHTML(term, color), getOrgTermHTML(parent, p_color));
		return data;
	}
	
	private boolean isLeaf(OntologyTerm term) {
		for (OntologyTerm t : term.getChildren()) {
			if (t.getIc() > 0) return false;
		}
		return true;
	}
	
	private boolean isRoot(OntologyTerm term) {
		return term.getParents().size() == 0;
	}
	
	private OrgDataTable displayParent(OrgDataTable data, OntologyTerm child, int depth, OntologyTerm c1, OntologyTerm c2, OntologyTerm p) {
		if (depth > 0) {
			OntologyTerm parent = child.getParents().iterator().next();
			data = addTerm(data, child, parent, getColor(p, c1, c2, child), getColor(p, c1, c2, parent));
			data = displayParent(data, parent, depth-1, c1, c2, p);
		}
		return data;
	}
	
	private OrgDataTable displayChildren(OrgDataTable data, OntologyTerm parent, int depth, OntologyTerm c1, OntologyTerm c2, OntologyTerm p) {
		if (depth > 0) {
			int count = 1;
			int totCount = 0;
			for (OntologyTerm t : parent.getChildren()) {
				if (t.getIc() > 0) totCount++;
			}
			for (OntologyTerm t : parent.getChildren()) {
				if (t.getIc() > 0) {
					if (count < 3 || (count == 3 && totCount == 3) ) {
						data = addTerm(data, t, parent, getColor(p, c1, c2, t), getColor(p, c1, c2, parent));
						data = displayParent(data, t, depth-1, c1, c2, p);
					}
					count++;
				}
			}
			if (count > 3 || (count == 3 && totCount == 3)) {
				data = addChildCountTerm(data, count-2, parent, getColor(p, c1, c2, parent));
			}
		}
		return data;
	}
	
	private String getColor(OntologyTerm p, OntologyTerm c1, OntologyTerm c2, OntologyTerm t) {
		if (p.equals(t)) return "Red";
		if (c1.equals(t)) return "#56A0D3";
		if (c2.equals(t)) return "#25B52B";
		return "";
	}
	
	private String getOrgTermHTML(OntologyTerm term, String color) {
		String fontWeight = "regular";
		if (!color.equals("")) fontWeight="bold";
		return "<div style='font-weight:"+fontWeight+"'>"+term.getName() + "<br/><font color=\""+ color + "\"> IC = " + term.getIc().toString() + "<br/>Count = " + term.getCount() + "</font></div>";
	}
	
	
	private String getOrgCountTermHTML(int count) {
		return String.valueOf(count) + " terms in between.";
	}
	
	private String getOrgChildCountTermHTML(int count) {
		return String.valueOf(count) + " more child term" + (count > 1 ? "s." : ".");
	}
	
	private class BestParentDetails {
		public List<OntologyTerm> path1;
		public List<OntologyTerm> path2;
		public OntologyTerm term;
		
		public BestParentDetails(List<OntologyTerm> path1, List<OntologyTerm> path2, OntologyTerm parent) {
			this.path1 = path1;
			this.path2 = path2;
			this.term = parent;
		}
	}*/
}
