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

import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

import edu.unc.flashlight.shared.util.Constants;
import edu.unc.flashlight.shared.util.Conversion;

public class DownloadServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	public void doGet(HttpServletRequest request, HttpServletResponse response)
	        throws ServletException, IOException {

	   if (request.getParameter("filename") != null) notMapped(request,response);
	   else if (request.getParameter("eu") != null) export(request, response, request.getParameter("eu"));
	}
	
	private void export(HttpServletRequest request, HttpServletResponse response, String type) throws ServletException, IOException {
	    Long id = (Long) request.getSession().getAttribute(HibernateServlet.USER_ID_ATT);
	    if (id == null) return;
		response.setContentType("application/force-download");
	    response.setHeader("Content-Disposition", "attachment; fileName=spotlite_export.csv");

		Session s = HibernateUtil.getSessionFactory().getCurrentSession();
		Transaction t = s.beginTransaction();

		Query q;
		if (type.equalsIgnoreCase("Distinct")) {
			q = s.getNamedQuery("getClassificationAlgorithm.byUser");
			q.setLong("uid", id);
			String classificationAlgorithmName = (String) q.setMaxResults(1).uniqueResult();
			q = s.getNamedQuery("getDataPerExp");
			q.setLong("id", id);
			
			List<Object[]> ret = (List<Object[]>) q.list();
			OutputStream out2 = response.getOutputStream();
			PrintWriter out = new PrintWriter(out2);
			
			Map<String,Object[]> hash2features = new HashMap<String,Object[]>();
			
			for (Object[] row: ret) {
				String bait = row[2] == null ? row[0].toString() :  row[2].toString();
				String prey = row[5] == null  ? row[3].toString() :  row[5].toString();
				String hash = Conversion.doGenePairHash(bait, prey);
				
				if (hash2features.containsKey(hash)) {
					
					if(Double.parseDouble(hash2features.get(hash)[7].toString()) > Double.parseDouble(row[7].toString())) {
						hash2features.put(hash, row);
					} else if (Double.parseDouble(hash2features.get(hash)[9].toString()) > Double.parseDouble(row[9].toString())) {
						hash2features.put(hash, row);
					}
					
				} else {
					hash2features.put(hash, row);
				}
			}
			
			
			out.write("Bait Uploaded Identifier,Bait Official Symbol,Bait GeneID,Prey Uploaded Identifier,Prey Official Symbol,Prey GeneID,Classifier Score,Classifier P Value,"+classificationAlgorithmName+","+classificationAlgorithmName+" P Value, Exp SC,Control SC,BP,CC,CXP Hsa,CXP Mmu,CXP Mcc,CXP Cfa,CXP Rno,CXP Dre,Domain,Homo Int,Hsa phen,Mmu phen,Disease,Known\n");
			Set<Integer> no_display_cols = new HashSet<Integer>(Arrays.asList(new Integer[] {12}));
		    for (Object[] row : hash2features.values()) {
		    	String outLine = "";
		    	for (int i = 0; i < row.length-1; i++) {
		    		if (!no_display_cols.contains(i)) {
		    			outLine += row[i] + ",";
		    		}
		    	}
		    	outLine += row[row.length-1] + "\n";
		    	out.write(outLine.replace("null", ""));
		    }
		    out.flush();
			t.commit();
			
		} else {
			q = s.getNamedQuery("getClassificationAlgorithm.byUser");
			q.setLong("uid", id);
			String classificationAlgorithmName = (String) q.setMaxResults(1).uniqueResult();
			
			q = s.getNamedQuery("getDataPerExp");
			q.setLong("id", id);
			
			List<Object[]> ret = (List<Object[]>) q.list();
			OutputStream out2 = response.getOutputStream();
			PrintWriter out = new PrintWriter(out2);
			
			out.write("Bait Uploaded Identifier,Bait Official Symbol,Bait GeneID,Prey Uploaded Identifier,Prey Official Symbol,Prey GeneID,Classifier Score,Classifier P Value,"+classificationAlgorithmName+","+classificationAlgorithmName+" P Value, Exp SC,Control SC,BP,CC,CXP Hsa,CXP Mmu,CXP Mcc,CXP Cfa,CXP Rno,CXP Dre,Domain,Homo Int,Hsa phen,Mmu phen,Disease,Known\n");
			Set<Integer> no_display_cols = new HashSet<Integer>(Arrays.asList(new Integer[] {12}));
		    for (Object[] row : ret) {
		    	String outLine = "";
		    	for (int i = 0; i < row.length-1; i++) {
		    		if (!no_display_cols.contains(i)) {
		    			outLine += row[i] + ",";
		    		}
		    	}
		    	outLine += row[row.length-1] + "\n";
		    	out.write(outLine.replace("null", ""));
		    }
		    out.flush();
			t.commit();
		}
		
		
	}
	
	private void notMapped(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
		String filename = request.getParameter("filename");
	    response.setContentType("application/force-download");
	    response.setHeader("Content-Disposition", "attachment; fileName=" + Constants.NOT_MAPPED_FILE + ".txt");
	    
	    OutputStream out = response.getOutputStream();
	    FileInputStream fIn = new FileInputStream(filename);
	    byte[] buffer = new byte[4096];
	    int length;
	    while ((length = fIn.read(buffer)) > 0){
	        out.write(buffer, 0, length);
	    }
	    fIn.close();
	    out.flush();
	}
}
