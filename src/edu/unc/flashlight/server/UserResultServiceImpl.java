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
package edu.unc.flashlight.server;

import java.util.List;

import edu.unc.flashlight.client.service.UserResultService;
import edu.unc.flashlight.server.dao.DaoCommand;
import edu.unc.flashlight.server.dao.DaoManager;
import edu.unc.flashlight.shared.exception.FlashlightException;
import edu.unc.flashlight.shared.model.ClassificationAlgorithm;
import edu.unc.flashlight.shared.model.UserFDR;
import edu.unc.flashlight.shared.model.UserResult;
import edu.unc.flashlight.shared.model.details.MSDetails;
import edu.unc.flashlight.shared.model.table.PageResults;
import edu.unc.flashlight.shared.model.table.PaginationParameters;

public class UserResultServiceImpl extends HibernateServlet implements UserResultService{
	private static final long serialVersionUID = 1L;
	
	public MSDetails getMSDetails(final long prey_id) throws FlashlightException {
		DaoCommand<MSDetails> daoCommand = new DaoCommand<MSDetails>() {
			public MSDetails execute(DaoManager<MSDetails> manager) {
				
				/*List<UserResult> results = manager.getUserResultDAO().getByPrey(prey_id, getCurrentUserID());
				Gene g = manager.getGeneDAO().getGene(prey_id);
				OrgDataTable data = new OrgDataTable();
				
				Map<Integer, Double> bins = new HashMap<Integer, Double>();
				
				List<Long> bait_ids = new ArrayList<Long>();
				// get max abundance
				double max_abundance = 0;
				for (UserResult er : results) {
					bait_ids.add(er.getExperiment().getBait().getId());
					double abundance = er.getAbundance();
					if (abundance > max_abundance) max_abundance = abundance;
				}
				
				int step_size = 1;
				if (Math.ceil(max_abundance/10) > step_size) step_size = (int) Math.ceil(max_abundance/10);

				for (int i = 0; i <= max_abundance; i+=step_size) {
					bins.put(i, 0D);
				}
				
				for (UserResult er : results) {
					double abundance = er.getAbundance();
					int bin = ((int)(abundance/step_size))*step_size;
					bins.put(bin,bins.get(bin)+1);
				}
				
				ArrayList<Integer> sortedKeys = new ArrayList<Integer>(bins.keySet());
				Collections.sort(sortedKeys);
				for (Integer i : sortedKeys) {
					data.addRow(i.toString(), bins.get(i).toString());
				}
				
				List<DefaultEdge> uniquenessNetwork = db_network.getUniqunessNetwork(bait_ids, manager.getGeneDAO());
				List<Pair<String,String>> return_network = new ArrayList<Pair<String,String>>();
				
				for (DefaultEdge e : uniquenessNetwork) {
					String[] edges = e.toString().replaceAll("\\(|\\)", "").split(" : ");
					return_network.add(new Pair<String,String>(edges[0], edges[1]));
				}
				*/
				return null; //new UserDetails(results, data, new HashSet<Long>(bait_ids), return_network);
			}
		};
		return new DaoManager<MSDetails>(getSessionFactory()){}.execute(daoCommand);
	}
	
	public Void deleteData() throws FlashlightException {
		DaoCommand<Void> daoCommand = new DaoCommand<Void>() {
			public Void execute(DaoManager<Void> manager) {				
				manager.getUserResultDAO().deleteDataForUser(getCurrentUserID());
				manager.getExperimentDataDAO().deleteDataForUser(getCurrentUserID());	
				manager.getExperimentDAO().deleteDataForUser(getCurrentUserID());	
				manager.getUserFDRDAO().deleteDataForUser(getCurrentUserID());	
				return null;
			}
		};
		new DaoManager<Void>(getSessionFactory()){}.execute(daoCommand);
		return null;
	}

	public PageResults<UserResult> getData(final PaginationParameters p) throws Exception {
		DaoCommand<PageResults<UserResult>> daoCommand = new DaoCommand<PageResults<UserResult>>() {
			public PageResults<UserResult> execute(DaoManager<PageResults<UserResult>> manager) {
				
				List<UserResult> results = manager.getUserResultDAO().getData(p, getCurrentUserID());
				long size = manager.getUserResultDAO().getDataSize(getCurrentUserID());
				
				return new PageResults<UserResult>(results, size);
			}
		};
		return new DaoManager<PageResults<UserResult>>(getSessionFactory()){}.execute(daoCommand);
	}
	
	public PageResults<UserResult> getDataSearch(final PaginationParameters p, final String sym) throws Exception {
		DaoCommand<PageResults<UserResult>> daoCommand = new DaoCommand<PageResults<UserResult>>() {
			public PageResults<UserResult> execute(DaoManager<PageResults<UserResult>> manager) {
				
				long size = manager.getUserResultDAO().getDataSearchSize(getCurrentUserID(), sym);
				List<UserResult> results = manager.getUserResultDAO().getDataSearch(p, getCurrentUserID(), sym);			
				
				return new PageResults<UserResult>(results, size);
			}
		};
		return new DaoManager<PageResults<UserResult>>(getSessionFactory()){}.execute(daoCommand);
	}
	
	public PageResults<UserResult> getBrowseDataSearch(final PaginationParameters p, final String sym) throws Exception {
		DaoCommand<PageResults<UserResult>> daoCommand = new DaoCommand<PageResults<UserResult>>() {
			public PageResults<UserResult> execute(DaoManager<PageResults<UserResult>> manager) {
				
				List<UserResult> results = manager.getUserResultDAO().getBrowseDataSearch(p, getCurrentUserID(), sym);
				long size = manager.getUserResultDAO().getBrowseDataSearchSize(getCurrentUserID(), sym);
				
				return new PageResults<UserResult>(results, size);
			}
		};
		return new DaoManager<PageResults<UserResult>>(getSessionFactory()){}.execute(daoCommand);
	}
	
	public PageResults<UserResult> getBrowseData(final PaginationParameters p) throws Exception {
		DaoCommand<PageResults<UserResult>> daoCommand = new DaoCommand<PageResults<UserResult>>() {
			public PageResults<UserResult> execute(DaoManager<PageResults<UserResult>> manager) {
				
				List<UserResult> results = manager.getUserResultDAO().getBrowseData(p, getCurrentUserID());
				long size = manager.getUserResultDAO().getBrowseDataSize(getCurrentUserID());
				
				return new PageResults<UserResult>(results, size);
			}
		};
		return new DaoManager<PageResults<UserResult>>(getSessionFactory()){}.execute(daoCommand);
	}
	
	public List<UserFDR> getFDRForUser() throws FlashlightException {
		DaoCommand<List<UserFDR>> daoCommand = new DaoCommand<List<UserFDR>>() {
			public List<UserFDR> execute(DaoManager<List<UserFDR>> manager) {
				List<UserFDR> results = manager.getUserFDRDAO().getFDR(getCurrentUserID());
				return results;
			}
		};
		return new DaoManager<List<UserFDR>>(getSessionFactory()){}.execute(daoCommand);
	}

	public String getClassificationAlgorithm() throws FlashlightException {
		DaoCommand<String> daoCommand = new DaoCommand<String>() {
			public String execute(DaoManager<String> manager) {	
				return manager.getUserResultDAO().getClassificationAlgorithmForUser(getCurrentUserID());
			}
		};
		return new DaoManager<String>(getSessionFactory()){}.execute(daoCommand);
	}
}
