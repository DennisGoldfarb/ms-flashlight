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

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.Query;
import org.hibernate.Session;

import edu.unc.flashlight.server.rf.GenePairScore;
import edu.unc.flashlight.shared.model.Experiment;
import edu.unc.flashlight.shared.model.ExperimentData;

public class ExperimentDataDAO extends GenericDAO<ExperimentData> {

	public ExperimentDataDAO(Session session) {
		super(session);
	}
	
	public ExperimentData save(ExperimentData inst) {
		if (inst.getPrey() == null || inst.getPrey().getId() == null || inst.getPrey().getId() < 1) {
			inst.setPreyNiceName(inst.getPreyUploadId());
		} else {
			inst.setPreyNiceName(inst.getPrey().getId().toString());
		}
		session.saveOrUpdate(inst);
		return inst;
	}
	
	public void deleteDataForUser(Long userID) {
		Query q = session.getNamedQuery("ExperimentData.deleteByUser");
		q.setLong("id", userID);
		q.executeUpdate();
	}
	
	public void deleteDataForOldUsers(Timestamp now) {
		Query q = session.getNamedQuery("ExperimentData.deleteByOldUsers");
		q.setTimestamp("now", now);
		q.executeUpdate();
	}
	
	public void bulkInsert(String filename) {
		session.createSQLQuery("LOAD DATA LOCAL INFILE :file INTO TABLE experiment_data CHARACTER SET latin1 FIELDS TERMINATED BY '\\t' LINES TERMINATED BY '\\n' (experiment_id, prey_gene_id, prey_upload_id, prey_nice_name, spectral_count, gene_pair_hash);")
	    .setString("file", filename)
	    .executeUpdate();
		//session.createSQLQuery("load data local infile '"+filename+"' into table experiment_data " +
		//		"(experiment_id, prey_gene_id, prey_upload_id, spectral_count, gene_pair_hash)").executeUpdate();
	}
	
	public boolean checkForRandomInsertionBug(long uid) {
		return (((BigInteger) session.createSQLQuery("select count(1) from experiment_data ed inner join experiments e on e.experiment_id = ed.experiment_id where e.user_id = :uid")
		.setLong("uid", uid)
		.uniqueResult()).intValue() > 0);
	}
	
	public GenePairScore[] getHashesForUser(Long userID, Long expType) {
		Query q = session.getNamedQuery("ExperimentData.getHashes");
		q.setLong("id", userID);
		q.setLong("exp_type", expType);
		List<Object[]> ret = q.list();
		
		GenePairScore[] ids = new GenePairScore[ret.size()];
		for (int i = 0; i < ret.size(); i++) {
			Object[] row = ret.get(i);
			String g1 = row[1] == null ? row[3].toString() : row[1].toString();
			String g2 = row[2] == null ? row[4].toString() : row[2].toString();
			ids[i] = new GenePairScore(g1,g2);
		}	
		return ids;
	}
	
	public long getNumInteractions(Long userId) {
		Query q = session.getNamedQuery("getNumInteractions");
		q.setLong("user_id", userId);
		return ((BigInteger) q.uniqueResult()).longValue();
	}
	
	public Map<Long,Map<String,Integer>> getExp2Prey2SC(Long userId, long expType) {
		Query q = session.getNamedQuery("getExp2Prey2SC");
		q.setLong("user_id",userId);
		q.setLong("exp_type", expType);
		List<Object[]> result = q.list();
		
		Map<Long,Map<String,Integer>> exp2prey2sc = new HashMap<Long,Map<String,Integer>>();
		
		for (Object[] row : result) {
			Long exp = ((BigInteger) row[0]).longValue();
			String prey = (row[1] == null) ? (String) row[3] : row[1].toString();
			Integer sc = (Integer) row[2];
			
			if (!exp2prey2sc.containsKey(exp)) exp2prey2sc.put(exp, new HashMap<String,Integer>());
			Map<String,Integer> expMap = exp2prey2sc.get(exp);
			if (!expMap.containsKey(prey)) expMap.put(prey, sc);
			else expMap.put(prey, expMap.get(prey)+sc);
		}
		return exp2prey2sc;
	}
}
