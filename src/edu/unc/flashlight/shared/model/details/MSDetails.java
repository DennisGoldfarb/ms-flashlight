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
package edu.unc.flashlight.shared.model.details;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

import edu.unc.flashlight.shared.model.Pair;
import edu.unc.flashlight.shared.model.UserResult;

public class MSDetails implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private OrgDataTable dataTable;
	private List<UserResult> hits;
	private Set<Long> bait_ids;
	private List<Pair<String,String>> network;
	
	public MSDetails() {
		
	}
	
	public MSDetails(List<UserResult> hits, OrgDataTable dataTable, Set<Long> bait_ids, List<Pair<String,String>> network) {
		this.hits = hits;
		this.dataTable = dataTable;
		this.bait_ids = bait_ids;
		this.network = network;
	}
	
	public void setHits(List<UserResult> hits) {
		this.hits = hits;
	}
	
	public List<UserResult> getHits() {
		return hits;
	}
	
	public void setDataTable(OrgDataTable dataTable) {
		this.dataTable = dataTable;
	}
	
	public OrgDataTable getDataTable() {
		return dataTable;
	}
	
	public void setBaitIds(Set<Long> bait_ids) {
		this.bait_ids = bait_ids;
	}
	
	public Set<Long> getBaitIds() {
		return bait_ids;
	}
	
	public void setNetwork(List<Pair<String,String>> network) {
		this.network = network;
	}
	
	public List<Pair<String,String>> getNetwork() {
		return network;
	}
}
