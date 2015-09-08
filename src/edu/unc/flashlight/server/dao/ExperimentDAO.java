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

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.Query;
import org.hibernate.Session;

import edu.unc.flashlight.server.util.FileUtil;
import edu.unc.flashlight.shared.model.Experiment;

public class ExperimentDAO extends GenericDAO<Experiment> {

	public ExperimentDAO(Session session) {
		super(session);
	}
	
	public Experiment save(Experiment inst) {
		if (inst.getBait() == null || inst.getBait().getId() == null || inst.getBait().getId() < 1) {
			inst.setBaitNiceName(inst.getBaitUploadId());
		} else {
			inst.setBaitNiceName(inst.getBait().getId().toString());
		}
		session.saveOrUpdate(inst);
		return inst;
	}
	
	public void deleteDataForUser(Long userID) {
		Query q = session.getNamedQuery("Experiment.deleteByUser");
		q.setLong("id", userID);
		q.executeUpdate();
	}
	
	public void deleteDataForOldUsers(Timestamp now) {
		Query q = session.getNamedQuery("Experiment.deleteByOldUsers");
		q.setTimestamp("now", now);
		q.executeUpdate();
	}
	
	public Map<Long,String> getExp2Bait(Long userId, Long expType) {
		Query q = session.getNamedQuery("Experiment.baitsByUser");
		q.setLong("user_id",userId);
		q.setLong("exp_type", expType);
		List<Object[]> result = q.list();
		Map<Long,String> exp2bait = new HashMap<Long,String>();
		
		for (Object[] row : result) {
			Long exp = ((BigInteger) row[0]).longValue();
			String bait = (row[1] == null) ?  (String) row[2] : row[1].toString();
			exp2bait.put(exp,bait);
		}
		return exp2bait;
	}
	
	public List<Long> getExp(Long userId, Long expType) {
		Query q = session.getNamedQuery("Experiment.expByUser");
		q.setLong("user_id",userId);
		q.setLong("exp_type", expType);
		List<Long> exps = new ArrayList<Long>();
		List<BigInteger> bad_exps = q.list();
		for (BigInteger id : bad_exps) {
			exps.add(id.longValue());
		}
		return exps;
	}

	public List<String> getDuplicateExperiments(long userID, Collection<String> expNames, String sid) {
		Query q = session.createSQLQuery("create temporary table exp_temp (id VARCHAR(50))");
		q.executeUpdate();
		
		String filename = FileUtil.createPath("exp_mapping_temp"+sid);
		try{
			FileWriter fstream = new FileWriter(filename, false);
			BufferedWriter out = new BufferedWriter(fstream);
			for (String s : expNames) out.write(s+"\n");
					out.close();
		}catch (Exception e){
			System.err.println("Error: " + e.getMessage());
		}

		session.createSQLQuery("load data local infile '"+filename+"' into table exp_temp").executeUpdate();
		FileUtil.deleteFile(filename);
		
		List<String> duplicates = getExpNameByExpName(userID);
		session.createSQLQuery("drop table exp_temp").executeUpdate();
		return duplicates;
	}
	
	public List<String> getExpNameByExpName(Long userID) {
		Query q = session.getNamedQuery("ExperimentName.byExperimentName");
		q.setLong("id", userID);
		List<String> names = q.list();
		return names;
	}
	
	public long getNumMappedBaits(Long userID) {
		Query q = session.getNamedQuery("Experiment.numMappedBaits");
		q.setLong("user_id", userID);
		return ((BigInteger) q.uniqueResult()).longValue();
	}
}
