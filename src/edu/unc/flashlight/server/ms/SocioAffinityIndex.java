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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import edu.unc.flashlight.shared.GenericCommand;
import edu.unc.flashlight.shared.model.Pair;
import edu.unc.flashlight.shared.util.Conversion;

public class SocioAffinityIndex {/*extends MassSpecScorer {
	private Map<Long,Integer> ap2totalPreyCount;
	private Map<Long,Map<Long,Integer>> ap2bait2preyCount;
	private Map<Long,Map<Long,Set<Long>>> ap2bait2exp;
	private Map<Long,Map<Long,Integer>> ap2prey2count;
	private Map<Long,Map<Long,Map<Long,Pair<Integer,Integer>>>> ap2prey2prey2count;
	private int bigSum;
	
	public SocioAffinityIndex(Map<Long, Long> exp2bait, Map<Long, Map<Long, Integer>> exp2prey2sc, 
			Map<Long, Set<Long>> ap2exp, final List<String> hashes) {
		super(exp2bait, exp2prey2sc, ap2exp, hashes);
		ap2totalPreyCount= new HashMap<Long,Integer>();
		ap2bait2preyCount = new HashMap<Long,Map<Long,Integer>>();
		ap2bait2exp = new HashMap<Long,Map<Long,Set<Long>>>();
		ap2prey2count = new HashMap<Long,Map<Long,Integer>>();
		ap2prey2prey2count = new HashMap<Long,Map<Long,Map<Long,Pair<Integer,Integer>>>>();
		bigSum = 0;
	}

	public double[] calculateScores(GenericCommand<Double> updateProgress) {
		formatData();
		updateProgress.execute(.05);
		scoreData();
		updateProgress.execute(.1);
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
				if (ap2prey2prey2count.get(ap).containsKey(g1) && ap2prey2prey2count.get(ap).get(g1).containsKey(g2)) score = calc_SocioAffinityIndex(g1,g2,ap);
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
	
	private double calc_SocioAffinityIndex(Long bait, Long prey, Long ap) {
		double s1 = S(bait,prey,ap);
		double s2 = S(prey,bait,ap);
		double m = M(bait,prey,ap);
		if (s1 != s1) s1 = 0;
		if (s2 != s2) s2 = 0;
		if (m != m) m = 0;
		return s1+s2+m;
	}
	
	public void formatData() {	
		for (Long ap : ap2exp.keySet()) {
			ap2pair2score.put(ap, new HashMap<String,Double>());
			ap2bait2preyCount.put(ap, new HashMap<Long,Integer>());
			ap2bait2exp.put(ap, new HashMap<Long,Set<Long>>());
			ap2prey2count.put(ap, new HashMap<Long,Integer>());
			ap2prey2prey2count.put(ap, new HashMap<Long,Map<Long,Pair<Integer,Integer>>>());
			ap2totalPreyCount.put(ap, 0);
			for (Long exp : ap2exp.get(ap)) {
				Long bait = exp2bait.get(exp);
				if (!ap2bait2preyCount.containsKey(bait))  {
					ap2bait2preyCount.get(ap).put(bait, 0);
					ap2bait2exp.get(ap).put(bait, new HashSet<Long>());
				}
				if (!ap2prey2prey2count.get(ap).containsKey(bait)) ap2prey2prey2count.get(ap).put(bait, new HashMap<Long,Pair<Integer,Integer>>());
				ap2bait2preyCount.get(ap).put(bait, ap2bait2preyCount.get(ap).get(bait) + exp2prey2sc.get(exp).size());
				ap2bait2exp.get(ap).get(bait).add(exp);
				ap2totalPreyCount.put(ap,ap2totalPreyCount.get(ap) + exp2prey2sc.get(exp).size());
				for (Long prey : exp2prey2sc.get(exp).keySet()) {
					String hash = Conversion.doGenePairHash(bait, prey);
//					if (!pair2score.containsKey(hash) && !prey.equals(bait)) pair2score.put(hash, null);
					if (!ap2pair2score.get(ap).containsKey(hash) && !prey.equals(bait)) ap2pair2score.get(ap).put(hash, 0D);
					if (!ap2prey2count.get(ap).containsKey(prey)) {
						ap2prey2count.get(ap).put(prey, 0);
						ap2prey2prey2count.get(ap).put(prey, new HashMap<Long,Pair<Integer,Integer>>());
					}
					ap2prey2count.get(ap).put(prey, ap2prey2count.get(ap).get(prey)+1);
				}
			}
			for (Long exp : ap2exp.get(ap)) {
				Long bait = exp2bait.get(exp);
				Map<Long,Pair<Integer,Integer>> prey2count = ap2prey2prey2count.get(ap).get(bait);
				for (Long prey : exp2prey2sc.get(exp).keySet()) {
					Map<Long,Pair<Integer,Integer>> otherPrey2count = ap2prey2prey2count.get(ap).get(prey);
					
					if (!prey2count.containsKey(prey)) prey2count.put(prey, new Pair<Integer,Integer>(0,0));
					prey2count.get(prey).setFirst(prey2count.get(prey).getFirst()+1);
										
					for (Long prey2 : ap2bait2exp.get(ap).keySet()) {
						if (exp2prey2sc.get(exp).containsKey(prey2)) {
							if (!otherPrey2count.containsKey(prey2)) otherPrey2count.put(prey2, new Pair<Integer,Integer>(0,0));
							if (prey != bait && prey2 != bait) otherPrey2count.get(prey2).setSecond(otherPrey2count.get(prey2).getSecond()+1);
						}
					}
				}
				bigSum += ((double)exp2prey2sc.get(exp).size()-1 * (exp2prey2sc.get(exp).size()-2))/2;
			}
		}
	}
	
	private double M(Long prey1, Long prey2, Long ap) {
		double odds =  (double)ap2prey2prey2count.get(ap).get(prey1).get(prey2).getSecond() / 
				(getPreyFrac(ap,prey1) * getPreyFrac(ap,prey2) * (double)bigSum);
		return Math.max(0,Math.log(odds));
	}
	
	private double S(Long bait, Long prey, Long ap) {
		if (!ap2bait2preyCount.get(ap).containsKey(bait)) return 0;
		if (!ap2prey2prey2count.get(ap).get(bait).containsKey(prey)) return 0;
		double odds = (double)ap2prey2prey2count.get(ap).get(bait).get(prey).getFirst() / 
				(getBaitFrac(ap,bait) * ap2exp.get(ap).size() * getPreyFrac(ap,prey) * ap2bait2preyCount.get(ap).get(bait) );
		return Math.max(0,Math.log(odds));
	}
	
	private double getPreyFrac(Long ap, Long prey) {
		if (!ap2prey2count.get(ap).containsKey(prey)) return 0D;
		return (double) ap2prey2count.get(ap).get(prey) / ap2totalPreyCount.get(ap);
	}
	
	private double getBaitFrac(Long ap, Long bait) {
		if (!ap2bait2exp.get(ap).containsKey(bait)) return 0D;
		return (double) ap2bait2exp.get(ap).get(bait).size() / ap2exp.get(ap).size();
	}
*/
}
