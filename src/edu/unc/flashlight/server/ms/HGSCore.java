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
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.math3.special.Gamma;

import edu.unc.flashlight.server.rf.GenePairScore;
import edu.unc.flashlight.shared.GenericCommand;

public class HGSCore extends MassSpecScorer {

	private class FormattedData {
		public Map<String,Map<String,Integer>> prey2prey2k;
		public Map<Long, Map<String, Float>> exp2prey2NSAF;
		public Long N;
		public ConcurrentMap<String,Long> prey2M;
		public Map<Long,Integer> exp2index;
		
		public FormattedData(APMSControlDataset data) {
			prey2prey2k = new HashMap<String,Map<String,Integer>>();
			exp2prey2NSAF = new HashMap<Long,Map<String,Float>>();
			N = 0L;
			prey2M = new ConcurrentHashMap<String,Long>();
			exp2index = new HashMap<Long,Integer>();
			formatData(data);
		}
		
		private void formatData(APMSControlDataset data) {
			createNSAF(data);
			scaleNSAF(data);
			createMatrix(data);
		}
		
		
		private void createMatrix(APMSControlDataset data) {
			int expIndex = 0;
			N = 0L;
			for (Long exp : data.exp2bait.keySet()) {
				exp2index.put(exp,expIndex);
				
				String[] prey = data.exp2prey2sc.get(exp).keySet().toArray(new String[data.exp2prey2sc.get(exp).keySet().size()]);
				for (int preyIndex = 0; preyIndex < prey.length; preyIndex++) {
					String prey1 = prey[preyIndex];
					int NSAF_a = data.exp2prey2sc.get(exp).get(prey1);
					for (int preyIndex2 = preyIndex; preyIndex2 < prey.length; preyIndex2++) {
						String prey2 = prey[preyIndex2];
						int NSAF_b = data.exp2prey2sc.get(exp).get(prey2);
						int NSAF = Math.min(NSAF_a, NSAF_b);

						N += NSAF;
						if (!prey2M.containsKey(prey2)) prey2M.put(prey2,0L);
						if (!prey2M.containsKey(prey1)) prey2M.put(prey1,0L);
						if (prey1 == prey2) {
							prey2M.put(prey2,NSAF + prey2M.get(prey2));
						}
						else {
							prey2M.put(prey2,NSAF + prey2M.get(prey2));
							prey2M.put(prey1,NSAF + prey2M.get(prey1));
						}
						if (data.containsBait(prey1)) {
							if (!prey2prey2k.containsKey(prey1)) prey2prey2k.put(prey1, new HashMap<String,Integer>());
							if (!prey2prey2k.get(prey1).containsKey(prey2)) prey2prey2k.get(prey1).put(prey2, 0);
							prey2prey2k.get(prey1).put(prey2, NSAF + prey2prey2k.get(prey1).get(prey2));
						}
						if (data.containsBait(prey2)) {
							if (!prey2prey2k.containsKey(prey2)) prey2prey2k.put(prey2, new HashMap<String,Integer>());
							if (!prey2prey2k.get(prey2).containsKey(prey1)) prey2prey2k.get(prey2).put(prey1, 0);
							prey2prey2k.get(prey2).put(prey1, NSAF + prey2prey2k.get(prey2).get(prey1));
						}
					}
				}
				expIndex++;
			}
			
			for (Long exp : data.c_exp2bait.keySet()) {
				exp2index.put(exp,expIndex);
				
				String[] prey = data.c_exp2prey2sc.get(exp).keySet().toArray(new String[data.c_exp2prey2sc.get(exp).keySet().size()]);
				for (int preyIndex = 0; preyIndex < prey.length; preyIndex++) {
					String prey1 = prey[preyIndex];
					int NSAF_a = data.c_exp2prey2sc.get(exp).get(prey1);
					for (int preyIndex2 = preyIndex; preyIndex2 < prey.length; preyIndex2++) {
						String prey2 = prey[preyIndex2];
						int NSAF_b = data.c_exp2prey2sc.get(exp).get(prey2);
						int NSAF = Math.min(NSAF_a, NSAF_b);

						N += NSAF;
						if (!prey2M.containsKey(prey2)) prey2M.put(prey2,0L);
						if (!prey2M.containsKey(prey1)) prey2M.put(prey1,0L);
						if (prey1 == prey2) {
							prey2M.put(prey2,NSAF + prey2M.get(prey2));
						}
						else {
							prey2M.put(prey2,NSAF + prey2M.get(prey2));
							prey2M.put(prey1,NSAF + prey2M.get(prey1));
						}
						if (data.containsBait(prey1)) {
							if (!prey2prey2k.containsKey(prey1)) prey2prey2k.put(prey1, new HashMap<String,Integer>());
							if (!prey2prey2k.get(prey1).containsKey(prey2)) prey2prey2k.get(prey1).put(prey2, 0);
							prey2prey2k.get(prey1).put(prey2, NSAF + prey2prey2k.get(prey1).get(prey2));
						}
						if (data.containsBait(prey2)) {
							if (!prey2prey2k.containsKey(prey2)) prey2prey2k.put(prey2, new HashMap<String,Integer>());
							if (!prey2prey2k.get(prey2).containsKey(prey1)) prey2prey2k.get(prey2).put(prey1, 0);
							prey2prey2k.get(prey2).put(prey1, NSAF + prey2prey2k.get(prey2).get(prey1));
						}
					}
				}
				expIndex++;
			}
		}
		
		private void scaleNSAF(APMSControlDataset data) {
			double min_NSAF = getMinNSAF(data);
			for (Long exp : data.exp2bait.keySet()) {
				for (String prey : exp2prey2NSAF.get(exp).keySet()) {
					double NSAF = exp2prey2NSAF.get(exp).get(prey);
					data.exp2prey2sc.get(exp).put(prey, (int)Math.sqrt(NSAF/min_NSAF));
				}
			}
			for (Long exp : data.c_exp2bait.keySet()) {
				for (String prey : exp2prey2NSAF.get(exp).keySet()) {
					double NSAF = exp2prey2NSAF.get(exp).get(prey);
					data.c_exp2prey2sc.get(exp).put(prey, (int)Math.sqrt(NSAF/min_NSAF));
				}
			}
		}
		
		private double getMinNSAF(APMSControlDataset data) {
			double min_NSAF = 1e100;
			for (Long exp : data.exp2bait.keySet()) {
				for (String prey : exp2prey2NSAF.get(exp).keySet()) {
					double NSAF = exp2prey2NSAF.get(exp).get(prey);
					if (NSAF < min_NSAF) min_NSAF = NSAF;
				}
			}
			for (Long exp : data.c_exp2bait.keySet()) {
				for (String prey : exp2prey2NSAF.get(exp).keySet()) {
					double NSAF = exp2prey2NSAF.get(exp).get(prey);
					if (NSAF < min_NSAF) min_NSAF = NSAF;
				}
			}
			return min_NSAF;
		}
		
		private void createNSAF(APMSControlDataset data) {	
			for (Long exp : data.exp2bait.keySet()) {
				exp2prey2NSAF.put(exp, new HashMap<String,Float>());

				float tot = 0f;
				for (String prey : data.exp2prey2sc.get(exp).keySet()) {
					int sc = data.exp2prey2sc.get(exp).get(prey);
					tot += (float) sc / data.prey2length.get(prey);
				}
				for (String prey : data.exp2prey2sc.get(exp).keySet()) {
					float nsc = (float) data.exp2prey2sc.get(exp).get(prey) / data.prey2length.get(prey);
					exp2prey2NSAF.get(exp).put(prey, nsc/tot);
				}
			}
			for (Long exp : data.c_exp2bait.keySet()) {
				exp2prey2NSAF.put(exp, new HashMap<String,Float>());

				float tot = 0f;
				for (String prey : data.c_exp2prey2sc.get(exp).keySet()) {
					int sc = data.c_exp2prey2sc.get(exp).get(prey);
					tot += (float) sc / data.prey2length.get(prey);
				}
				for (String prey : data.c_exp2prey2sc.get(exp).keySet()) {
					float nsc = (float) data.c_exp2prey2sc.get(exp).get(prey) / data.prey2length.get(prey);
					exp2prey2NSAF.get(exp).put(prey, nsc/tot);
				}
			}
		}
	}

	public HGSCore(APMSControlDataset data) {
		super(data);		
	}
	
	public GenePairScore[] calculateRawScores(FormattedData formattedData, GenePairScore[] result) {
		for (GenePairScore gps : result) {
			gps.setMsScore(calc_HGSCore(formattedData,gps.getBaitNiceName(),gps.getPreyNiceName()));
		}
		return result;
	}

	public ClassifierResults calculateScores(GenericCommand<Double> updateProgress) {
		updateProgress.execute(.01);
		ClassifierResults results = new ClassifierResults();
		results.scores = calculateRawScores(new FormattedData(data),data.getSpokeInteractions());
		
		updateProgress.execute(.02);
		MSControlPermutationGenerator permGen = new MSControlPermutationGenerator(data);
		int numDesiredPermutationInteractions = results.scores.length*invFDR*cov;
		int totalSteps = numDesiredPermutationInteractions;
		results.permutationScores = new HashMap<Object,List<Double>>();
		int i = 0;
		
		int rep = data.getReplicateCounts()[0];
		results.permutationScores.put(rep, Collections.synchronizedList(new ArrayList<Double>((int) (numDesiredPermutationInteractions*1.2))));
		
		int numPermutationInteractions = 0;
		while (numPermutationInteractions < numDesiredPermutationInteractions) {	
			ExecutorService executor = Executors.newFixedThreadPool(NUM_THREADS);
			for (int k = 0; k < NUM_THREADS; k++) {
				Runnable worker = new ScoringLoop(i,permGen,data.getReplicateCounts(),results);
				executor.execute(worker);
			}
			executor.shutdown();
			while (!executor.isTerminated()) {}
			numPermutationInteractions = results.permutationScores.values().iterator().next().size();
			int currentStep = Math.min(numPermutationInteractions, numDesiredPermutationInteractions); 
			updateProgress.execute(.02 + (currentStep*.97/totalSteps));
		}
		
		normalizeDataSingle(results);
		updateProgress.execute(1.0);
		return results;
	}
	
	private long calc_m(FormattedData data, String g1) {
		if (data.prey2M.containsKey(g1)) return data.prey2M.get(g1);
		return 0;
	}
	
	private long calc_k(FormattedData data, String g1, String g2) {
		if (data.prey2prey2k.containsKey(g1) && data.prey2prey2k.get(g1).containsKey(g2)) 
			return data.prey2prey2k.get(g1).get(g2);
		else if (data.prey2prey2k.containsKey(g2) && data.prey2prey2k.get(g2).containsKey(g1)) 
			return data.prey2prey2k.get(g2).get(g1);
		return 0;
	}
	
	private double comb(Long n, Long k) {
		return Gamma.logGamma(n+1) - Gamma.logGamma(k+1) - Gamma.logGamma(n-k+1);
	}
	
	private double calc_hygeo(Long x, Long k, Long n, Long m, Long N) {
		return Math.exp(comb(n,x) + comb(N-n,m-x) - comb(N,m));
	}
	
	private double calc_full_hygeo(Long k, Long n, Long m, Long N) {
		double full_hygeo = 0D;
		long zero_k = (long) (1+(((m*n)-N)/(double)(N+1)));
		long max_k = Math.min(m,n)+1;
		double tmp_hygeo = 0;
		double part_hygeo = 0;
		for (long x = k; x < max_k; x++) {
			part_hygeo = calc_hygeo(x,k,n,m,N);
			tmp_hygeo = full_hygeo + part_hygeo;
			if (part_hygeo < 1e-17 && x <= zero_k) return 1;
			if (tmp_hygeo == full_hygeo && x >= zero_k) break;
			full_hygeo = tmp_hygeo;
		}
		full_hygeo = Math.max(Math.min(1, full_hygeo),0);
		return full_hygeo;
	}
	
	private double calc_HGSCore(FormattedData data, String g1, String g2) {
		long k = calc_k(data,g1,g2);
		long n = calc_m(data,g1);
		long m = calc_m(data,g2);
		double HGSCore = calc_full_hygeo(k,n,m,data.N);
		if (HGSCore == 0){
			HGSCore = 1e-323;
		}
//		return HGSCore;
		return -1*Math.log(HGSCore);
		
		
	}
	
	private class ScoringLoop implements Runnable {
		private int index;
		private MSControlPermutationGenerator permGen;
		private ClassifierResults results;
		private Integer[] replicateCounts;
		public ScoringLoop(int i, MSControlPermutationGenerator permGen, Integer[] replicateCounts, ClassifierResults results) {
			this.permGen = permGen;
			this.results = results;
			index = i;
			this.replicateCounts = replicateCounts;
		}
		public void run() {
			APMSControlDataset permData = permGen.permute();
			FormattedData formatedPermData = new FormattedData(permData);
			GenePairScore[] scores = calculateRawScores(formatedPermData, permData.getSpokeInteractions());
			int rep = (Integer) results.permutationScores.keySet().iterator().next();
			for (GenePairScore gps : scores) {
				results.permutationScores.get(rep).add(gps.getMsScore());
			}
		}
	}
	
	
}
