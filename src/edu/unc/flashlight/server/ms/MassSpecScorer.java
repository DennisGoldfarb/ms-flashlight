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
package edu.unc.flashlight.server.ms;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.unc.flashlight.server.rf.GenePairScore;
import edu.unc.flashlight.shared.GenericCommand;
import edu.unc.flashlight.shared.model.UserFDR;
import edu.unc.flashlight.shared.util.Conversion;

public abstract class MassSpecScorer {
	protected APMSControlDataset data;
	protected List<GenePairScore[]> permutations;
	protected int cov = 4;
	protected int invFDR = 20;
	public static int NUM_THREADS = 6;
	protected double maxVal = Double.NEGATIVE_INFINITY;
	
	public MassSpecScorer(APMSControlDataset data) {
		this.data = data;
		permutations = new ArrayList<GenePairScore[]>();
	}
	
	public abstract ClassifierResults calculateScores(GenericCommand<Double> updateProgress);
	
	protected void normalizeDataSingle(ClassifierResults results) {
		List<GenePairScore> sortedScores = Arrays.asList(results.scores);
		Collections.sort(sortedScores);
		List<Double> sortedPermScores = results.permutationScores.get(results.permutationScores.keySet().iterator().next());
		Collections.sort(sortedPermScores);
		double threshold=0; 
		int j=sortedPermScores.size()-1;
		
		for (int i = sortedScores.size()-1; i >= 0; i--) {
			GenePairScore gps = sortedScores.get(i);
			threshold = gps.getMsScore();

			while (j>=0 && sortedPermScores.get(j) >= threshold) {
				j--;
			}
			
			double pval = (sortedPermScores.size() - j)/(sortedPermScores.size()+1.0);
			
			gps.setMsPValue(pval);
		}
		
		int i = sortedPermScores.size()-1;
		while (i >= 0) {
			int k = 1;
			double pval = (sortedPermScores.size() - i)/(sortedPermScores.size()+1.0);
			while (i-k >= 0 && sortedPermScores.get(i-k) == sortedPermScores.get(i)) {
				sortedPermScores.set(i-k, pval);
				k++;
			}	
			sortedPermScores.set(i, pval);
			i-=k;
		}
	}
	
	protected void normalizeData(ClassifierResults results) {		
		Map<Integer,List<GenePairScore>> rep2gps = new HashMap<Integer,List<GenePairScore>>();
		for (GenePairScore gps : results.scores) {
			int numRep = data.getNumReplicates(gps.getBaitNiceName());
			if (!rep2gps.containsKey(numRep)) rep2gps.put(numRep, new ArrayList<GenePairScore>());
			rep2gps.get(numRep).add(gps);
		}
		
		for (Object numRep : results.permutationScores.keySet()) {
			List<GenePairScore> sortedScores = rep2gps.get(numRep);
			Collections.sort(sortedScores);

			List<Double> sortedPermScores = results.permutationScores.get(numRep);
			Collections.sort(sortedPermScores);
						
			double threshold=0; 
			int j=sortedPermScores.size()-1;
			
			for (int i = sortedScores.size()-1; i >= 0; i--) {
				GenePairScore gps = sortedScores.get(i);
				threshold = gps.getMsScore();

				while (j>=0 && sortedPermScores.get(j) >= threshold) {
					j--;
				}
				
				double pval = (sortedPermScores.size() - j)/(sortedPermScores.size()+1.0);
				
				gps.setMsPValue(pval);
			}
			
			int i = sortedPermScores.size()-1;
			while (i >= 0) {
				int k = 1;
				double pval = (sortedPermScores.size() - i)/(sortedPermScores.size()+1.0);
				while (i-k >= 0 && sortedPermScores.get(i-k) == sortedPermScores.get(i)) {
					sortedPermScores.set(i-k, pval);
					k++;
				}	
				sortedPermScores.set(i, pval);
				i-=k;
			}
		}
	}
	
	public static double getPValue(List<Double> sortedPermScores, double target) {
		int j = Conversion.binaryInsertionSearch(sortedPermScores, target);		
		double pval = (sortedPermScores.size() - j)/(sortedPermScores.size()+1.0);		
		return pval;
	}
	
	public List<GenePairScore[]> getPermutations() {
		return permutations;
	}
	
	public static List<UserFDR> calculateFDR(GenePairScore[] scores) {
		List<Double> pvalues = new ArrayList<Double>();
		for (GenePairScore gps : scores) {
			pvalues.add(gps.getMsPValue());
		}
		List<UserFDR> fdrs = new ArrayList<UserFDR>();
		Collections.sort(pvalues);
		double qvalue = 0;
		double prev_qvalue = 0;
		double[] selected_fdrs = new double[]{.01, .05, .1, .2};
		int score_size = pvalues.size();
		
		for (double alpha : selected_fdrs) {
			for (int i = 0; i < score_size-1; i++) {
				if (pvalues.get(i) >= ((i+1)*alpha)/score_size && pvalues.get(i) != pvalues.get(i+1) ) {
					UserFDR userFDR = new UserFDR(alpha, pvalues.get(i),i+1);
					fdrs.add(userFDR);
					break;
				}
			}
		}
		
		/*
		for (int i = 0; i < score_size; i++) {
			qvalue = (pvalues.get(i)*score_size)/(i+1);
			if ((int)(qvalue*1000) > (int)(prev_qvalue*1000)) {
				UserFDR userFDR = new UserFDR(qvalue, pvalues.get(i),i+1);
				fdrs.add(userFDR);
				prev_qvalue = qvalue;
			}
		}*/
		return fdrs;
	}
	
}
