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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Query;
import org.hibernate.Session;

import edu.unc.flashlight.server.rf.GenePairScore;
import edu.unc.flashlight.server.rf.IndirectPermutationGenerator;
import edu.unc.flashlight.server.util.FileUtil;
import edu.unc.flashlight.shared.model.Gene;
import edu.unc.flashlight.shared.model.GenePair;
import edu.unc.flashlight.shared.model.details.CoexpressionDetails;

public class GenePairDAO extends GenericDAO<GenePair> {

	public GenePairDAO(Session session) {
		super(session);
	}
	
	public IndirectPermutationGenerator getPermutationGenerator(Long userID) {
		List<List<Double>> featureIndex2binProbabilities = new ArrayList<List<Double>>();
		List<Double> featureIndex2minimums = new ArrayList<Double>();
		List<Double> featureIndex2maximums = new ArrayList<Double>();
		
		Query q = session.getNamedQuery("GenePair.getFeatureRanges");
		List<Object[]> result = q.list();
		for (Object[] row : result) {
			Double min = (Double) row[0];
			Double max = (Double) row[1];
			featureIndex2minimums.add(min);
			featureIndex2maximums.add(max);
		}
		
		q = session.getNamedQuery("GenePair.getFeatureBins");
		q.setLong("user_id", userID);
		result = q.list();
		
		for (Object[] row : result) {
			int featureIndex = (Integer) row[0];
			Double binCount = (Double) row[1];
			
			if (featureIndex2binProbabilities.size()-1 < featureIndex) {
				featureIndex2binProbabilities.add(new ArrayList<Double>());
			}
			
			Double previous = 0D;
			int size = featureIndex2binProbabilities.get(featureIndex).size();
			if (size > 0) {
				previous = featureIndex2binProbabilities.get(featureIndex).get(size-1);
			}
			featureIndex2binProbabilities.get(featureIndex).add(binCount+previous);
		}
		
		for (List<Double> binProbabilities : featureIndex2binProbabilities) {
			for (int i = 0; i < binProbabilities.size(); i++) {
				binProbabilities.set(i, binProbabilities.get(i) / binProbabilities.get(binProbabilities.size()-1));
			}
		}	
		
		return new IndirectPermutationGenerator(featureIndex2binProbabilities, featureIndex2minimums, featureIndex2maximums);
	}
	
	public Map<String,Double[]> getFeatureScores(String sid, GenePairScore[] input) {
		Query q = session.createSQLQuery("create temporary table hash_temp (id VARCHAR(50), ms_score DOUBLE)");
		q.executeUpdate();
		String filename = FileUtil.createPath("hash_mapping_temp"+sid);
		try{
			FileWriter fstream = new FileWriter(filename, false);
			BufferedWriter out = new BufferedWriter(fstream);
			for (GenePairScore gps : input) {
				if (gps.getGenePairHash() != null) {
					out.write(gps.getGenePairHash()+"\t"+gps.getMsScore()+"\n");
				}
			}
			out.close();
		}catch (Exception e){
			System.err.println("Error: " + e.getMessage());
		}

		session.createSQLQuery("load data local infile '"+filename+"' into table hash_temp").executeUpdate();
		Map<String,Double[]> scores = new HashMap<String,Double[]>();
		//FileUtil.deleteFile(filename);
		try {
			q = session.getNamedQuery("GenePair.getScores");	
			for (Object o : q.list()) {
				Object[] oldRow = (Object[]) o;
				Double[] row = new Double[oldRow.length-1];
				for (int i = 1; i < oldRow.length; i++) {
					row[i-1] = (Double) oldRow[i];
				}
 				scores.put((String) oldRow[0],row);
			}
		} catch (Exception e) {
			System.err.println(e.getMessage());
		} finally {
			session.createSQLQuery("drop table hash_temp").executeUpdate();	
		}
		return scores;
	}
	
	public CoexpressionDetails getBestCoexpressionHomolog(final long geneIDa,final long geneIDb, final long taxID) {
		Query q = session.getNamedQuery("getBestCoexpressionHomolog");
		q.setLong("gene_id_a", geneIDa);
		q.setLong("gene_id_b", geneIDb); 
		q.setLong("tax_id", taxID);
		
		Object[] ids = (Object[]) q.uniqueResult();
		if (ids == null) return null;
		
		Gene g1 = (Gene) session.getNamedQuery("Gene.byId.Filled").setLong("id", ((BigInteger)ids[0]).longValue()).uniqueResult();
		Gene g2 = (Gene) session.getNamedQuery("Gene.byId.Filled").setLong("id", ((BigInteger)ids[1]).longValue()).uniqueResult();
		Double pcc = (Double)ids[2];
		
		return new CoexpressionDetails(g1, g2, pcc);
	}
	public CoexpressionDetails getBestCoexpressionHuman(final long geneIDa,final long geneIDb) {
		Query q = session.getNamedQuery("getBestCoexpressionHuman");
		q.setLong("gene_id_a", geneIDa);
		q.setLong("gene_id_b", geneIDb); 
		
		Double pcc = (Double) q.uniqueResult();
		if (pcc == null) return null;
		
		Gene g1 = (Gene) session.getNamedQuery("Gene.byId.Filled").setLong("id", geneIDa).uniqueResult();
		Gene g2 = (Gene) session.getNamedQuery("Gene.byId.Filled").setLong("id", geneIDb).uniqueResult();
		
		return new CoexpressionDetails(g1,g2,pcc);
	}
	
	public GenePair getByHash(final String genePairHash) {
		Query q = session.getNamedQuery("GenePair.byHash");
		q.setString("genePairHash", genePairHash);
		
		return (GenePair) q.uniqueResult();
	}
}	
