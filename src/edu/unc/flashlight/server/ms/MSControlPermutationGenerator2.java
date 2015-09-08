package edu.unc.flashlight.server.ms;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.math3.special.Gamma;
import org.paukov.combinatorics.Factory;
import org.paukov.combinatorics.Generator;
import org.paukov.combinatorics.ICombinatoricsVector;


public class MSControlPermutationGenerator2 {
	// Original data
	private APMSControlDataset data;
	private Generator<Long> gen;
	private Iterator<ICombinatoricsVector<Long>> itr;
	
	public MSControlPermutationGenerator2(APMSControlDataset data, String bait) {
		this.data = data;
		List<Long> shuffledExps = new ArrayList<Long>();
		shuffledExps.addAll(data.bait2exp.get(bait));
		shuffledExps.addAll(data.c_exp2bait.keySet());
		ICombinatoricsVector<Long> initialVector = Factory.createVector(shuffledExps);
		gen = Factory.createSimpleCombinationGenerator(initialVector, data.c_exp2bait.size());
		itr = gen.createIterator();
	}
	
	public int getNumComb() {
		int n = gen.getOriginalVector().getSize();
		int r = data.c_exp2bait.size();
		return (int) Math.round(Math.exp(comb(n,r)));
	}
	
	private double comb(int n, int k) {
		return Gamma.logGamma(n+1) - Gamma.logGamma(k+1) - Gamma.logGamma(n-k+1);
	}
	
	public void getNext() {
		
	}
	
	public APMSControlDataset permuteNext(String bait) {
		APMSControlDataset permutedData = new APMSControlDataset(data.prey2length);
		ICombinatoricsVector<Long> exps = itr.next();
			
		for (Long exp : exps) {
			if (data.exp2prey2sc.containsKey(exp)) permutedData.c_exp2prey2sc.put(exp, data.exp2prey2sc.get(exp));
			else permutedData.c_exp2prey2sc.put(exp, data.c_exp2prey2sc.get(exp));
			//permutedData.c_exp.add(exp);
		}
		
		for (Long exp : data.exp2bait.keySet()) {
			if (!permutedData.c_exp2prey2sc.containsKey(exp)) {
				permutedData.exp2bait.put(exp, data.exp2bait.get(exp));
				permutedData.exp2prey2sc.put(exp, data.exp2prey2sc.get(exp));
			}
		}
		
		for (Long exp : data.c_exp2prey2sc.keySet()) {
			if (!permutedData.c_exp2prey2sc.containsKey(exp)) {
				permutedData.exp2bait.put(exp, bait);
				permutedData.exp2prey2sc.put(exp, data.c_exp2prey2sc.get(exp));
			}
		}
		
		permutedData.setupBait2exp();
		return permutedData;
	};
}
