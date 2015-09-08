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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import edu.unc.flashlight.shared.GenericCommand;
import edu.unc.flashlight.shared.model.Pair;
import edu.unc.flashlight.shared.util.Conversion;

public class PE { /*extends MassSpecScorer {
	private Map<Long,Map<Long,Set<Long>>> ap2bait2exp;
	private Map<Long,Map<Long,Integer>> ap2prey2count;
	private Map<Long,Integer> ap2totalPreyCount;
	private Map<Long,Integer> ap2distinctPreyCount;
	private Map<Long,Integer> ap2pairCount;
	private Map<Long,Map<Long,Map<Long,Integer>>> ap2prey2prey2count;
	private double r = 0;
	private double n_pseudo = 0;
	
	public PE(Map<Long, Long> exp2bait, Map<Long, Map<Long, Integer>> exp2prey2sc, Map<Long, Set<Long>> ap2exp, final List<String> hashes) {
		super(exp2bait, exp2prey2sc, ap2exp, hashes);
		ap2bait2exp = new HashMap<Long,Map<Long,Set<Long>>>();
		ap2prey2count = new HashMap<Long,Map<Long,Integer>>();
		ap2totalPreyCount= new HashMap<Long,Integer>();
		ap2distinctPreyCount= new HashMap<Long,Integer>();
		ap2prey2prey2count = new HashMap<Long,Map<Long,Map<Long,Integer>>>();
		ap2pairCount= new HashMap<Long,Integer>();
	}

	public double[] calculateScores(GenericCommand<Double> updateProgress) {
		formatData();
		//updateProgress.execute(.05);
		scoreData();
		//updateProgress.execute(.1);
		normalizeData();
		return scores;
	}
	
	private class ScoringLoop implements Runnable {
		private int index;
		private long ap;
		public ScoringLoop(int i,long ap) {
			index = i;
			this.ap = ap;
		}
		public void run() {
			Object[] hashes = ap2pair2score.get(ap).keySet().toArray();
			Map<String, Double> ap_pair2score = ap2pair2score.get(ap);
			for (int i = index; i < hashes.length; i+=NUM_THREADS) {
				String hash = hashes[i].toString();
				Pair<Long,Long> ids = Conversion.undoGenePairHash(hash);
				double score = 0D;
				Long g1 = ids.getFirst();
				Long g2 = ids.getSecond();
				//if (ap2prey2prey2count.get(ap).containsKey(g1) && ap2prey2prey2count.get(ap).get(g1).containsKey(g2)) score = calc_SocioAffinityIndex(g1,g2,ap);
				ap_pair2score.put(hash, score);
			}
		}
	}
	
	public void scoreData() {
		for (Long ap : ap2pair2score.keySet()) {
			ExecutorService executor = Executors.newFixedThreadPool(6);
			
			for (int i = 0; i < NUM_THREADS; i++) {
				Runnable worker = new ScoringLoop(i,ap);
				executor.execute(worker);
			}
			executor.shutdown();
			while (!executor.isTerminated()) {
				
			}
		}
	}
	
	private double calc_PE(Long bait, Long prey, Long ap) {
		double s1 = S(bait,prey,ap);
		double s2 = S(prey,bait,ap);
		double m = M(bait,prey,ap);
		if (s1 != s1) s1 = 0;
		if (s2 != s2) s2 = 0;
		if (m != m) m = 0;
		return s1+s2+m;
	}
	
	private double F(Long prey, Long ap) {
		double num = ap2prey2count.get(ap).get(prey) + n_pseudo;
		double denom = ap2totalPreyCount.get(ap) + (ap2distinctPreyCount.get(ap) * n_pseudo);
		return num/denom;
	}
	
	private double S(Long bait, Long prey, Long ap) {
		double s = 0;
		for (Long exp : ap2bait2exp.get(ap).get(bait)) {
			if (exp2prey2sc.get(exp).containsKey(prey)) {
				double p = 1 - Math.exp(exp2prey2sc.get(exp).keySet().size() * ap2bait2exp.get(ap).get(bait).size() * F(prey,ap));
				s += Math.log10((r+(1+r)*p)/p);
			} else {
				s += Math.log10(1-r);
			}
		}
		return s;
	}
	
	private double M(Long bait, Long prey, Long ap) {
		double m = 0;
		if (ap2prey2prey2count.get(ap).containsKey(bait) && ap2prey2prey2count.get(ap).get(bait).containsKey(prey)) {
			for (int i = 0; i < ap2prey2prey2count.get(ap).get(bait).get(prey); i++) {
				double p = 1 - Math.exp(F(bait,ap) * F(prey,ap) * ap2pairCount.get(ap));
				m += Math.log10((r+(1-r)*p)/p);
			}
		}
		return m;
	}
	
	private void formatData() {
		
	}*/
}
