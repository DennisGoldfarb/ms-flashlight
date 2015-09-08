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
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import edu.unc.flashlight.server.rf.GenePairScore;
import edu.unc.flashlight.shared.GenericCommand;
import edu.unc.flashlight.shared.model.Pair;

public class CompPASS extends MassSpecScorer {


	private class FormattedData {
		public Map<String,Map<String,Pair<Double,Integer>>> bait2prey2tsc_repro = new HashMap<String,Map<String,Pair<Double,Integer>>>();
		
		public FormattedData(APMSControlDataset data) {
			formatData(data);
		}
		
		private void fillPrey2TSC(Map<Long,String> exp2bait, Map<String,Set<Long>> bait2exp, Map<Long,Map<String,Integer>> exp2prey2sc) {
			for (Long exp : exp2bait.keySet()) {
				String bait = exp2bait.get(exp);
				if (!bait2exp.containsKey(bait)) bait2exp.put(bait, new HashSet<Long>());
				bait2exp.get(bait).add(exp);
				if (!bait2prey2tsc_repro.containsKey(bait)) bait2prey2tsc_repro.put(bait, new HashMap<String,Pair<Double,Integer>>());
				Map<String,Integer> prey2sc = exp2prey2sc.get(exp);
				Map<String,Pair<Double,Integer>> prey2tsc = bait2prey2tsc_repro.get(bait);
				for (String prey : prey2sc.keySet()) {
					if (!prey2tsc.containsKey(prey)) prey2tsc.put(prey, new Pair<Double,Integer>(0.0,0));
					prey2tsc.get(prey).setFirst(prey2tsc.get(prey).getFirst() + prey2sc.get(prey));
					prey2tsc.get(prey).setSecond(prey2tsc.get(prey).getSecond()+1);
				}
			}
		}
		
		private void formatData(APMSControlDataset data) {			
			fillPrey2TSC(data.exp2bait,data.bait2exp,data.exp2prey2sc);
			fillPrey2TSC(data.c_exp2bait,data.c_bait2exp,data.c_exp2prey2sc);

			for (String bait : bait2prey2tsc_repro.keySet()) {
				Map<String,Pair<Double,Integer>> prey2tsc = bait2prey2tsc_repro.get(bait);
				for (String prey : prey2tsc.keySet()) {
					prey2tsc.get(prey).setFirst(prey2tsc.get(prey).getFirst() / prey2tsc.get(prey).getSecond());
				}
			}
		}
	}	
	
	public CompPASS(APMSControlDataset data) {
		super(data);
	}

	public ClassifierResults calculateScores(GenericCommand<Double> updateProgress) {
		updateProgress.execute(.01);
		ClassifierResults results = new ClassifierResults();
		results.scores = calculateRawScores(new FormattedData(data), data.getSpokeInteractions());
		
		updateProgress.execute(.02);
		
		int numDesiredPermutationInteractions = results.scores.length*invFDR*cov;
		int totalSteps = data.getReplicateCounts().length*numDesiredPermutationInteractions;
		results.permutationScores = new HashMap<Object,List<Double>>();
		MSControlPermutationGenerator permGen = new MSControlPermutationGenerator(data);
		int overfill = 2;
		
		// Continue sampling until all replicate counts are filled.
		int numComplete = 0;
		try {
			for (int j = 0; j < data.getReplicateCounts().length; j++) {
				int rep = data.getReplicateCounts()[j];
				results.permutationScores.put(rep, Collections.synchronizedList(new ArrayList<Double>((int) (numDesiredPermutationInteractions*1.2))));
			}
			
			while (numComplete < data.getReplicateCounts().length) {
				ExecutorService executor = Executors.newFixedThreadPool(NUM_THREADS);
				for (int k = 0; k < NUM_THREADS; k++) {
					Runnable worker = new ScoringLoop(permGen,results,(int) (numDesiredPermutationInteractions*overfill));
					executor.execute(worker);
				}
				executor.shutdown();
				while (!executor.isTerminated()) {}
				numComplete = 0;
				int currentStep = 0;
				for (int rep : data.getReplicateCounts()) {
					currentStep += Math.min(results.permutationScores.get(rep).size(), numDesiredPermutationInteractions);
					if (results.permutationScores.get(rep).size() >= numDesiredPermutationInteractions) {
						numComplete++;
					}
				} 
				updateProgress.execute(.02 + (currentStep*.97/totalSteps));
			}
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
		
		
		
		/*
		for (int j = 0; j < data.getReplicateCounts().length; j++) {
			int numPermutationInteractions = 0;
			int rep = data.getReplicateCounts()[j];
			rep = 15;
			results.permutationScores.put(rep, Collections.synchronizedList(new ArrayList<GenePairScore>((int) (numDesiredPermutationInteractions*1.2))));
			while (numPermutationInteractions < numDesiredPermutationInteractions) {	
				ExecutorService executor = Executors.newFixedThreadPool(NUM_THREADS);
				for (int k = 0; k < NUM_THREADS; k++) {
					Runnable worker = new ScoringLoop(permGen,rep,results);
					executor.execute(worker);
				}
				executor.shutdown();
				while (!executor.isTerminated()) {}
				numPermutationInteractions = results.permutationScores.get(rep).size();
				int currentStep = Math.min(numPermutationInteractions, numDesiredPermutationInteractions) + (j*numDesiredPermutationInteractions); 
				updateProgress.execute(.02 + (currentStep*.97/totalSteps));
			}
		}
		*/
		normalizeData(results);
		updateProgress.execute(1.0);
		return results;
	}
	
	private GenePairScore[] calculateRawScores(FormattedData formattedData, GenePairScore[] result) {
		for (GenePairScore gps : result) {
			gps.setMsScore(calc_compPASS(formattedData,gps.getBaitNiceName(),gps.getPreyNiceName()));
		}
		return result;
		
		/*ExecutorService executor = Executors.newFixedThreadPool(NUM_THREADS);
		
		for (int i = 0; i < NUM_THREADS; i++) {
			Runnable worker = new ScoringLoop(formattedData,i,result);
			executor.execute(worker);
		}
		executor.shutdown();
		while (!executor.isTerminated()) {}
		return result;*/
	}
	
	private class ScoringLoop implements Runnable {
		private int numDesiredPermutationInteractions;
		private MSControlPermutationGenerator permGen;
		private ClassifierResults results;
		public ScoringLoop(MSControlPermutationGenerator permGen, ClassifierResults results, int numDesiredPermutationInteractions) {
			this.permGen = permGen;
			this.results = results;
			this.numDesiredPermutationInteractions = numDesiredPermutationInteractions;
		}
		public void run() {
			for (int i = 0; i < 10; i++) {
				APMSControlDataset permData = permGen.permute();
				FormattedData formatedPermData = new FormattedData(permData);
				GenePairScore[] scores = calculateRawScores(formatedPermData, permData.getSpokeInteractions());
				for (GenePairScore gps : scores) {
					int rep = permData.getNumReplicates(gps.getBaitNiceName());
					if (results.permutationScores.get(rep).size() < numDesiredPermutationInteractions) {
						results.permutationScores.get(rep).add(gps.getMsScore());
					}
				}
			}
		}
	}
	
	/*private class ScoringLoop implements Runnable {
		private int rep;
		private MSControlPermutationGenerator permGen;
		private ClassifierResults results;
		public ScoringLoop(MSControlPermutationGenerator permGen, int rep, ClassifierResults results) {
			this.permGen = permGen;
			this.results = results;
			this.rep = rep;
		}
		public void run() {
			
			APMSControlDataset permData = permGen.permute(rep);
			FormattedData formatedPermData = new FormattedData(permData);
			results.permutationScores.get(rep).addAll(Arrays.asList(calculateRawScores(formatedPermData, permData.getSpokeInteractions())));
		}
	}*/
	
	private double calc_compPASS(FormattedData data, String bait, String prey) {
		try {
			double x = data.bait2prey2tsc_repro.get(bait).get(prey).getFirst();
			int p = data.bait2prey2tsc_repro.get(bait).get(prey).getSecond();
			int k = data.bait2prey2tsc_repro.size();
			int f = getFreq(data,prey);
			double a = getAvg(data,prey);
			double o = getStd(data,prey,a);
			//int n = data.bait2exp.get(bait).size();
			if (data.bait2prey2tsc_repro.containsKey(prey)) k--;
			return calc_WD_score(x,p,k,f,a,o);
		} catch (Exception e) {
			return 0d;
		}
	}
	
	private double calc_WD_score(double x,int p,int k,int f,double a,double o) throws Exception {
		double O = Math.max(1.0, o/a);
		//double toLog = (p/Math.sqrt(n))+1;
		//double wd = Math.sqrt(Math.pow(O*k/f , Math.log10(toLog)/Math.log10(2))*x);
		return Math.sqrt(Math.pow(O*k/f , p)*x);
	}
	
	private double getStd(FormattedData data, String prey, double avg) throws Exception{
		double std = 0D;
		int denom = 0;
		for (String bait : data.bait2prey2tsc_repro.keySet()) {
			if (data.bait2prey2tsc_repro.get(bait).containsKey(prey) && !prey.equals(bait)) {
				double ASC = data.bait2prey2tsc_repro.get(bait).get(prey).getFirst();
				double diff = Math.pow(ASC - avg,2);
				std += diff;
				denom++;
			} 
		}
		std /= denom;
		std = Math.sqrt(std);
		return std;
	}
	
	private double getAvg(FormattedData data, String prey) throws Exception{
		double avg = 0D;
		int denom = 0;
		for (String bait : data.bait2prey2tsc_repro.keySet()) {
			if (data.bait2prey2tsc_repro.get(bait).containsKey(prey) && !prey.equals(bait)) {
				double ASC = data.bait2prey2tsc_repro.get(bait).get(prey).getFirst();
				avg+=ASC;
				denom++;
			}
		}
		avg /= denom;
		return avg;
	}
	
	private int getFreq(FormattedData data, String prey) throws Exception{
		int count = 0;
		for (String bait : data.bait2prey2tsc_repro.keySet()) {
			if (data.bait2prey2tsc_repro.get(bait).containsKey(prey) && !prey.equals(bait)) {
				count++;
			}
		}
		return count;
	}

}
