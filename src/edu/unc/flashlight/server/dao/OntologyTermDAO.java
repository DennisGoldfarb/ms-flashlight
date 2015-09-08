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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.Query;
import org.hibernate.Session;

import edu.unc.flashlight.shared.model.OntologyRelationType;
import edu.unc.flashlight.shared.model.OntologyTerm;
import edu.unc.flashlight.shared.model.OntologyType;
import edu.unc.flashlight.shared.model.Pair;

public class OntologyTermDAO extends GenericDAO<OntologyTerm> {

	public OntologyTermDAO(Session session) {
		super(session);
	}
	
	public Set<OntologyTerm> getAllByType(final String type) {
		Query q = session.getNamedQuery("OntologyTerm.all,byType");
		q.setString("name", type);
		Set<OntologyTerm> result = new HashSet<OntologyTerm>(q.list());
		return result;
	}
	
	public OntologyTerm getByID(final String id) {
		return (OntologyTerm) session.get(OntologyTerm.class, id);
	}
	
	public List<Pair<String,String>> getOntologyNetworkForGenes(long id_a, long id_b, String type) {
		Query q = session.getNamedQuery("OntologyType.byName");
		q.setString("name",type);
		OntologyType ontologyType = (OntologyType) q.uniqueResult();
		q = session.getNamedQuery("OntologyRelationType.byName");
		q.setString("name","is_a");
		OntologyRelationType ontologyRelationType = (OntologyRelationType) q.uniqueResult();
		
		q = session.getNamedQuery("OntologyTerm.getNetworkForGenes");
		q.setLong("id_a", id_a);
		q.setLong("id_b", id_b);
		q.setLong("type", ontologyType.getId());
		q.setLong("relation_type", ontologyRelationType.getId());
		List<Pair<String,String>> network = new ArrayList<Pair<String,String>>();
		List<Object[]> result = q.list();
		for (Object[] row : result) {
			network.add(new Pair<String,String>(row[0].toString(),row[1].toString()));
		}
		return network;
	}

}
