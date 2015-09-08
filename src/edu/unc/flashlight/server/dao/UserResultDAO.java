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
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.hibernate.Query;
import org.hibernate.Session;

import edu.unc.flashlight.server.rf.GenePairScore;
import edu.unc.flashlight.server.util.FileUtil;
import edu.unc.flashlight.shared.model.ExperimentData;
import edu.unc.flashlight.shared.model.Pair;
import edu.unc.flashlight.shared.model.UserResult;
import edu.unc.flashlight.shared.model.table.PaginationParameters;
import edu.unc.flashlight.shared.util.Constants.MS_ALGORITHMS;
import edu.unc.flashlight.shared.util.Conversion;

public class UserResultDAO extends GenericDAO<UserResult> {

	public UserResultDAO(Session session) {
		super(session);
	}
	
	public UserResult save(UserResult inst) {
		if (inst.getPreyGene() == null || inst.getPreyGene().getId() == null || inst.getPreyGene().getId() < 1) {
			inst.setPreyNiceName(inst.getPreyUploadId());
		} else {
			inst.setPreyNiceName(inst.getPreyGene().getId().toString());
		}
		if (inst.getBaitGene() == null || inst.getBaitGene().getId() == null || inst.getBaitGene().getId() < 1) {
			inst.setBaitNiceName(inst.getBaitUploadId());
		} else {
			inst.setBaitNiceName(inst.getBaitGene().getId().toString());
		}
		session.saveOrUpdate(inst);
		return inst;
	}
	
	public String getClassificationAlgorithmForUser(Long userID) {
		Query q = session.getNamedQuery("getClassificationAlgorithm.byUser");
		q.setLong("uid", userID);
		return (String) q.setMaxResults(1).uniqueResult();
	}
	
	public void deleteDataForOldUsers(Timestamp now) {
		Query q = session.getNamedQuery("UserResult.deleteByOldUsers");
		q.setTimestamp("now", now);
		q.executeUpdate();
	}
	
	public void deleteDataForUser(Long userID) {
		Query q = session.getNamedQuery("UserResult.deleteByUser");
		q.setLong("id", userID);
		q.executeUpdate();
	}
	
	public void bulkInsert(GenePairScore[] scores, String sid, Long userID, int classification_algorithm_id) {
		String filename = FileUtil.createPath("insert_user_results"+sid);
		try{
			// Create file 
			FileWriter fstream = new FileWriter(filename, false);
			BufferedWriter out = new BufferedWriter(fstream);
			for (GenePairScore s : scores) {
				String g1 = s.getBaitNiceName();
				String g2 = s.getPreyNiceName();
				String gph = s.getGenePairHash() == null ? "\\N" : s.getGenePairHash();
						
				out.write(userID.toString() + "\t" + g1 + "\t" + g2 + "\t" + String.valueOf(s.getMsScore()) + "\t" + String.valueOf(s.getMsPValue()) + "\t" +
						String.valueOf(s.getClassifier()) + "\t" + String.valueOf(s.getClassifierPValue()) + "\t" + gph + "\t" + String.valueOf(classification_algorithm_id) + "\n");
			}
			out.close();
			session.createSQLQuery("load data local infile '"+filename+"' into table user_results (user_id, bait_nice_name, prey_nice_name, ms_score, ms_p_value, classifier_score, classifier_p_value, gene_pair_hash, classification_algorithm_id)").executeUpdate();
			updateIds(userID);
		}catch (Exception e){
			System.err.println("Error: " + e.getMessage());
		} finally {
			FileUtil.deleteFile(filename);
		}
	}
	
	private void updateBaitGeneIds(Long userID) {
		Query q = session.getNamedQuery("UserResult.updateBaitGeneIds");
		q.setLong("user_id", userID);
		q.executeUpdate();
	}
	
	private void updatePreyGeneIds(Long userID) {
		Query q = session.getNamedQuery("UserResult.updatePreyGeneIds");
		q.setLong("user_id", userID);
		q.executeUpdate();
	}
	
	public void updateGeneIds(Long userID) {
		updateBaitGeneIds(userID);
		updatePreyGeneIds(userID);
	}
	
	private void updateBaitUploadIds(Long userID) {
		Query q = session.getNamedQuery("UserResult.updateBaitUploadIds");
		q.setLong("user_id", userID);
		q.executeUpdate();
	}
	
	private void updatePreyUploadIds(Long userID) {
		Query q = session.getNamedQuery("UserResult.updatePreyUploadIds");
		q.setLong("user_id", userID);
		q.executeUpdate();
	}
	
	public void updateUploadIds(Long userID) {
		updateBaitUploadIds(userID);
		updatePreyUploadIds(userID);
	}
	
	public void updateIds(Long userID) {
		updateGeneIds(userID);
		updateUploadIds(userID);
	}
	
	public List<UserResult> getBrowseDataSearch(PaginationParameters p, Long userID, String sym) {
		Query q = session.getNamedQuery("getBrowseDataSearch");
		q.setString("sym", sym);
		q.setFirstResult(p.getFirstResult());
		q.setMaxResults(p.getMaxResults());
		List<UserResult> ret = (List<UserResult>) q.list();
		return ret;
	}
	
	public List<UserResult> getDataSearch(PaginationParameters p, Long userID, String sym) {
		Query q = session.getNamedQuery("getDataSearch");
		q.setLong("id", userID);
		q.setString("sym", sym);
		q.setFirstResult(p.getFirstResult());
		q.setMaxResults(p.getMaxResults());
		List<UserResult> ret = (List<UserResult>) q.list();
		return ret;
	}
	
	public List<UserResult> getBrowseData(PaginationParameters p, Long userID) {
		Query q = session.getNamedQuery("getBrowseData");
		q.setFirstResult(p.getFirstResult());
		q.setMaxResults(p.getMaxResults());
		List<UserResult> ret = (List<UserResult>) q.list();
		return ret;
	}
	
	public List<UserResult> getData(PaginationParameters p, Long userID) {
		Query q = session.getNamedQuery("getData");
		q.setLong("id", userID);
		q.setFirstResult(p.getFirstResult());
		q.setMaxResults(p.getMaxResults());
		List<UserResult> ret = (List<UserResult>) q.list();
		return ret;
	}
	
	public long getBrowseDataSearchSize(Long userID, String sym) {
		Query q = session.getNamedQuery("getBrowseDataSearch.size");
		q.setString("sym", sym);
		return (Long) q.uniqueResult();
	}
	
	public long getDataSearchSize(Long userID, String sym) {
		Query q = session.getNamedQuery("getDataSearch.size");
		q.setString("sym", sym);
		q.setLong("id", userID);
		return (Long) q.uniqueResult();
	}
	
	public long getBrowseDataSize(Long userID) {
		Query q = session.getNamedQuery("getBrowseData.size");
		return (Long) q.uniqueResult();
	}
	
	public long getDataSize(Long userID) {
		Query q = session.getNamedQuery("getData.size");
		q.setLong("id", userID);
		return (Long) q.uniqueResult();
	}
}
