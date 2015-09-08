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
package edu.unc.flashlight.server.dao;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.Query;
import org.hibernate.Session;

import edu.unc.flashlight.shared.model.GeneInteraction;

public class GeneInteractionDAO extends GenericDAO<GeneInteraction> {

	public GeneInteractionDAO(Session session) {
		super(session);
	}
	
	public List<GeneInteraction> getDirect(Set<Long> ids) {
		Query q = session.getNamedQuery("GeneInteraction.directByIds");
		q.setParameterList("ids", ids);
		List<GeneInteraction> result = q.list();
		return result;
	}
	
	public List<GeneInteraction> getNetwork(Set<Long> ids) {
		Query q = session.getNamedQuery("GeneInteraction.networkBetweenIds");
		q.setParameterList("ids", ids);
		List<GeneInteraction> result = q.list();
		return result;
	}

	public Set<GeneInteraction> getAll() {
		Query q = session.getNamedQuery("GeneInteraction.all");
		Set<GeneInteraction> result = new HashSet<GeneInteraction>(q.list());
		return result;
	}
	
	public Set<GeneInteraction> getAllLow() {
		Query q = session.getNamedQuery("GeneInteraction.all_low");
		Set<GeneInteraction> result = new HashSet<GeneInteraction>(q.list());
		return result;
	}
	
	public Set<GeneInteraction> getByExperimentalSystem(Long experimental_system_id) {
		Query q = session.getNamedQuery("GeneInteraction.byExperimentalSystem");
		q.setLong("id", experimental_system_id);
		Set<GeneInteraction> result = new HashSet<GeneInteraction>(q.list());
		return result;
	}

	public List<GeneInteraction> getGeneInteractions(Long gene_id_a, Long gene_id_b) {
		Query q = session.getNamedQuery("GeneInteraction.byGeneIds");
		q.setLong("id_a", gene_id_a);
		q.setLong("id_b", gene_id_b);
		List<GeneInteraction> result = q.list();
		return result;
	}
}
