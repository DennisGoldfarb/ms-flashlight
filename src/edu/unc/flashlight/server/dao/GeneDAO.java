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
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.Query;
import org.hibernate.Session;

import edu.unc.flashlight.server.util.FileUtil;
import edu.unc.flashlight.shared.GenericCommand;
import edu.unc.flashlight.shared.model.Gene;
import edu.unc.flashlight.shared.model.OntologyType;
import edu.unc.flashlight.shared.model.UserResult;
import edu.unc.flashlight.shared.model.table.PaginationParameters;

public class GeneDAO extends GenericDAO<Gene>{	

	public GeneDAO(Session session) {
		super(session);
	}

	public Gene getGene(Long gene_id) {
		return (Gene) session.get(Gene.class, gene_id);
	}

	public Map<String, Set<Long>> convertToGeneID(Collection<String> input, Long taxID,
			String sid, GenericCommand<Double> updateProgress) {
		Query q = session.createSQLQuery("create temporary table id_temp (id VARCHAR(50))");
		q.executeUpdate();
		String filename =  FileUtil.createPath("id_mapping_temp"+sid);
		try{
			// Create file 
			FileWriter fstream = new FileWriter(filename, false);
			BufferedWriter out = new BufferedWriter(fstream);
			for (String s : input) out.write(s+"\n");
			out.close();
		}catch (Exception e){
			System.err.println("Error: " + e.getMessage());
		}

		session.createSQLQuery("load data local infile '"+filename+"' into table id_temp").executeUpdate();
		Map<String, Set<Long>> returnVal = new HashMap<String, Set<Long>>();
		try {
			FileUtil.deleteFile(filename);
			updateProgress.execute(.20);

			List<Long> q_ids = getGeneIDbyGeneID();
			Set<String> complete = new HashSet<String>();
			for (Long l : q_ids) {
				if (!returnVal.containsKey(l.toString())) returnVal.put(l.toString(), new HashSet<Long>());
				returnVal.get(l.toString()).add(l);
				complete.add(l.toString());
			}
			updateProgress.execute(.40);

			List<Object[]> q_ids2 = getGeneIDbyOfficialSymbol(taxID);
			for (Object[] row : q_ids2) {
				if (!complete.contains((String)row[0])) {
					if (!returnVal.containsKey(row[0])) returnVal.put((String)row[0], new HashSet<Long>());
					returnVal.get(row[0]).add((Long)row[1]);
					complete.add((String)row[0]);
				}
			}
			updateProgress.execute(.60);

			Set<String> completeAlias = new HashSet<String>();
			q_ids2 = getGeneIDbyAlias(taxID);
			for (Object[] row : q_ids2) {
				if (!complete.contains((String)row[0])) {
					if (!returnVal.containsKey(row[0])) returnVal.put((String)row[0], new HashSet<Long>());
					returnVal.get(row[0]).add((Long)row[1]);
					completeAlias.add((String)row[0]);
				}
			}
			updateProgress.execute(.80);

			q_ids2 = getGeneIDbySequenceID();
			for (Object[] row : q_ids2) {
				if (!complete.contains((String)row[0]) && !completeAlias.contains((String)row[0])) {
					if (!returnVal.containsKey(row[0])) returnVal.put((String)row[0], new HashSet<Long>());
					returnVal.get(row[0]).add((Long)row[1]);
				}
			}
		} catch (Exception e) {

		} finally {
			session.createSQLQuery("drop table id_temp").executeUpdate();	
		}
		return returnVal;
	}

	public Map<String,Integer> getLengthsForUser(long userID) {
		Query q = session.getNamedQuery("getLengthsByUser");
		q.setLong("id", userID);
		List<Object[]> result = q.list();
		Map<String,Integer> gene2length = new HashMap<String,Integer>();

		for (Object[] row : result) {
			String gene_id = row[0].toString();
			Integer length = (Integer) row[1];
			gene2length.put(gene_id,length);
		}
		return gene2length;
	}

	public Collection<Gene> getAll() {
		Query q = session.getNamedQuery("Gene.all");
		q.setMaxResults(10);
		Collection<Gene> result = new ArrayList<Gene>(q.list());
		return result;
	}

	public List<Object[]> getGeneIDbySequenceID() {
		Query q = session.getNamedQuery("GeneID.bySequenceID");
		List<Object[]> ids = q.list();
		return ids;
	}

	public List<Object[]> getGeneIDbyAlias(Long tax_id) {
		Query q = session.getNamedQuery("GeneID.byAlias");
		q.setLong("tax_id", tax_id);
		List<Object[]> ids = q.list();
		return ids;
	}

	public List<Object[]> getGeneIDbyOfficialSymbol(Long tax_id) {
		Query q = session.getNamedQuery("GeneID.byOfficialSymbol");
		q.setLong("tax_id", tax_id);
		List<Object[]> ids = q.list();
		return ids;
	}

	public List<Long> getGeneIDbyGeneID() {
		Query q = session.getNamedQuery("GeneID.byGeneID");
		List<Long> ids = (List<Long>) q.list();
		return ids;
	}

	public Gene getGeneWithAnnotations(Long gene_id) {
		Query q = session.getNamedQuery("Gene.byGeneID.Annotations");
		q.setLong("gene_id", gene_id);
		Gene g = (Gene) q.uniqueResult();
		return g;
	}
	
	public List<String> getSymbolSuggestions(String query, Long uid) {
		Query q = session.getNamedQuery("GeneSymbol.bySymbolQuery");
		q.setString("query", query+"%");
		q.setLong("uid", uid);

		q.setFirstResult(0);
		q.setMaxResults(10);
		List<String> ret = (List<String>) q.list();
		return ret;
	}

	
	public Map<Long,String> getOfficialSymbols(Collection<Long> ids, String sid) {
		Query q = session.createSQLQuery("create temporary table indistinguishable_temp (id LONG)");
		q.executeUpdate();
		Map<Long, String> returnVal = new HashMap<Long, String>();
		String filename =  FileUtil.createPath("indistinguishable_temp"+sid);
		try{
			// Create file 
			FileWriter fstream = new FileWriter(filename, false);
			BufferedWriter out = new BufferedWriter(fstream);
			for (Long s : ids) out.write(s+"\n");
			out.close();
			session.createSQLQuery("load data local infile '"+filename+"' into table indistinguishable_temp").executeUpdate();
			FileUtil.deleteFile(filename);
			q = session.getNamedQuery("GeneSymbols.byGeneIDs");
			List<Object[]> map = q.list();
			for (Object[] row : map) {
				returnVal.put((Long)row[0], (String)row[1]);
			}
		}catch (Exception e){
			System.err.println("Error: " + e.getMessage());
		}
		finally {
			session.createSQLQuery("drop table indistinguishable_temp").executeUpdate();	
		}
		
		return returnVal;
	}
}
