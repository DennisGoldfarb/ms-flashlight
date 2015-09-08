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
package edu.unc.flashlight.server.rf;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import edu.unc.flashlight.server.ms.ClassifierResults;
import edu.unc.flashlight.server.ms.MassSpecScorer;
import edu.unc.flashlight.shared.GenericCommand;
import edu.unc.flashlight.shared.model.UserFDR;
import edu.unc.flashlight.shared.util.Constants.MS_ALGORITHMS;

public class SpotliteClassifier {
	/** BP, CC, MR_human, MR_mouse, MR_worm, MR_chicken, MR_rat, MR_fish, MR_monkey, domain, homo_int, intercept **/
	private LogisticRegressionModel IndirectModel = new LogisticRegressionModel(new double[] {	5.116e-02, 9.402e-02, 1.227e-02, 
																								1.346e-02, 9.840e-04, 2.652e-02, 
																								1.614e-03, 4.222e-03, 9.795e-03, 
																								3.218e-04, 6.369e-04	}, 
																								-3.646);
	
	private EnumMap<MS_ALGORITHMS, LogisticRegressionModel> algorithm2model = new EnumMap<MS_ALGORITHMS, LogisticRegressionModel>(MS_ALGORITHMS.class) {
		private static final long serialVersionUID = 1L;
		{
			/** MS p value, indirect logistic regression probability, intercept **/
        	put(MS_ALGORITHMS.HGSCore, new LogisticRegressionModel(new double[] {0.5061303, 0.2300247}, -2.370868));
        	put(MS_ALGORITHMS.CompPASS, new LogisticRegressionModel(new double[] {0.3480232, 0.2303997}, -2.370398));
        	put(MS_ALGORITHMS.SAINT, new LogisticRegressionModel(new double[] {0.496303, 0.197159}, -2.6798262));
        }
    };
	
	private static double[] featureMeans = {	0.6349977861745764,0.6129523612704467,0.05777415150037,
												0.050774688957400796,0.09920133605827236,0.0942092601889469,
												0.03997958269139492,0.03751714463965891,0.08823182076447027,
												0.000537177375254343,0.0006263421184722231};	

	private static double[] featureVariances = {	0.0765366964872,0.0829674400695,0.00724264994587,
													0.00491337520467,0.00284840079075,0.0111691917767,
													0.0063789779929,0.00421575033773,0.0125767256463,
													7.75718060533e-06, 3.27763231021e-05};
	
	private GenePairScore[] msScores;
	List<Double> classifierPermutationScores;
	private Double prog = 0D;
	private MS_ALGORITHMS alg;
	private Random ran;

	public SpotliteClassifier(MS_ALGORITHMS alg) {
		this.alg = alg;
		ran = new Random();
	}
	
	private Double[] scale(Double[] instance) {
		for (int i = 0; i < instance.length; i++) {
			instance[i] = (instance[i]-featureMeans[i])/featureVariances[i];
		}
		return instance;
	}
	
	private Map<String,Double[]> scaleAllFeatures(Map<String,Double[]> features) {
		for (String hash : features.keySet()) {
			features.put(hash, scale(features.get(hash)));
		}
		return features;
	}
	
	private Map<String,Double[]> fillMissingValues(Map<String,Double[]> features) {
		for (GenePairScore gps : msScores) {
			String hash = gps.getGenePairHash();
			if (!features.containsKey(hash)) {
				features.put(hash, new Double[featureMeans.length]);
			}
			for (int j = 0; j < features.get(hash).length; j++) {
				if (features.get(hash)[j] == null) {
					features.get(hash)[j] = featureMeans[j];
				}
			}
		}
		return features;
	}
	
	public GenePairScore[] scoreData(Map<String,Double[]> features, ClassifierResults results, IndirectPermutationGenerator permGen, 
			GenericCommand<Double> updateProgress) {
		try {
			prog = 0D;
			this.msScores = results.scores;
			features = fillMissingValues(features);
			features = scaleAllFeatures(features);
			
			int permutationSize = 0;
			for (Object key : results.permutationScores.keySet()) {
				permutationSize += results.permutationScores.get(key).size();
			}
			
			classifierPermutationScores = new ArrayList<Double>(permutationSize);
			while (classifierPermutationScores.size() < permutationSize) classifierPermutationScores.add(0D);
			
			int runningSum = 0;
			for (Object key : results.permutationScores.keySet()) {
				ExecutorService executor = Executors.newFixedThreadPool(MassSpecScorer.NUM_THREADS);				
				for (int i = 0; i < MassSpecScorer.NUM_THREADS; i++) {
					Runnable worker = new PermutationLoop(permGen, results.permutationScores.get(key), i, permutationSize, runningSum, features);
					executor.execute(worker);
				}
				executor.shutdown();
				while (!executor.isTerminated()) {
					updateProgress.execute(prog);
				}
				runningSum += results.permutationScores.get(key).size();
			}			
			
			Collections.sort(classifierPermutationScores);
			
			ExecutorService executor = Executors.newFixedThreadPool(MassSpecScorer.NUM_THREADS);
			for (int i = 0; i < MassSpecScorer.NUM_THREADS; i++) {
				Runnable worker = new ScoringLoop(features, i);
				executor.execute(worker);
			}
			executor.shutdown();
			while (!executor.isTerminated()) {
				updateProgress.execute(prog);
			}

		} catch (Exception e) {
			System.out.println("ERROR: " + e.getMessage());
		}
		
		return msScores;
	}
	
	private double calculateClassifierScore(Double[] featureVector, double msScore) {
		double score = IndirectModel.dotProduct(featureVector);
		score = algorithm2model.get(alg).calculateLogisticProbability(new Double[] {msScore,score} );
		return score;
	}
	
	private class PermutationLoop implements Runnable {
		private int index;
		private IndirectPermutationGenerator permGen;
		private List<Double> permutationScores;
		private int tot;
		private int runningSum;
		private List<Double[]> features;
		
		public PermutationLoop(IndirectPermutationGenerator permGen, List<Double> permutationScores, int i, int tot, int runningSum, Map<String,Double[]> features) {
			index = i;
			this.permGen = permGen;
			this.permutationScores = permutationScores;
			this.tot = tot;
			this.runningSum = runningSum;
			this.features = new ArrayList<Double[]>(features.values());
		}
		public void run() {
			try {
				for (int i = index;  i < permutationScores.size(); i+=MassSpecScorer.NUM_THREADS) {
					Double msPValue = permutationScores.get(i);
					Double[] randomFeatures = scale(permGen.sampleFeatures());
					//Double[] randomFeatures = features.get(r.nextInt(features.size()-1));
					Double classifier_score = calculateClassifierScore(randomFeatures, -Math.log(msPValue));
					//Double classifier_score = algorithm2model.get(alg).calculateLogisticProbability(new Double[] {-Math.log(msPValue),permGen.sampleFeatures()} );
					classifierPermutationScores.set(i,classifier_score);
					
					if ((index == 0) && i % (tot/5000) == 0) {
						prog = (.99* ((i+runningSum)/(float)tot));
					}
				}
			} catch (Exception e) {
				System.err.println(e.getMessage());
			}

		}
	}
	
	private class ScoringLoop implements Runnable {
		private int index;
		private Map<String,Double[]> features;
		public ScoringLoop(Map<String,Double[]> features, int i) {
			index = i;
			this.features = features;
		}
		public void run() {
			try {
				for (int i = index;  i < msScores.length; i+=MassSpecScorer.NUM_THREADS) {	
					double score = calculateClassifierScore(features.get(msScores[i].getGenePairHash()), -Math.log(msScores[i].getMsPValue()));
					double pvalue = MassSpecScorer.getPValue(classifierPermutationScores, score);
					
					msScores[i].setClassifier(score);
					msScores[i].setClassifierPValue(pvalue);
					
				}
			} catch (Exception e) {
				System.err.println(e.getMessage());
			}
		}
	}
}
