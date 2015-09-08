package edu.unc.flashlight.server.ms;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;


public class MSPermutationGenerator2 {
	// Original data
	private APMSDataset data;
	private String[] baits;
	// Original data statistics
	private Map<String,Double> globalBait2ASC;
	private Map<String,Double> globalPrey2ASC;
	private Map<String,Double> globalBait2AverageNumProtein;
	private Map<String,Double> globalBait2baitASC;
	
	private Map<String,Double> prey2ASC;
	private double globalTotalSpectralCount = 0;
	private double globalTotalBaitSpectralCount = 0; 
	private int totalSpectralCount = 0;
	// Other
	private Random generator = new Random();
	
	public MSPermutationGenerator2(APMSDataset data) {
		this.data = data;
		calculateDatasetStats();
	}
	
	private void calculateDatasetStats() {
		baits = data.getBaits();
		globalPrey2ASC = new HashMap<String,Double>();
		globalBait2ASC = new HashMap<String,Double>();
		globalBait2baitASC = new HashMap<String,Double>();
		globalBait2AverageNumProtein = data.getBait2AverageNumProteins();
		
		for (String prey : data.getPrey()) {
			double ASC = data.getNormalizedAverageSpectralCount(prey);
			if (ASC > 0) {
				globalPrey2ASC.put(prey, ASC);
			}
		}
		
		for (String bait : data.bait2exp.keySet()) {
			double baitASC = data.getBaitASCTot(bait);
			globalBait2ASC.put(bait, baitASC);
			globalBait2baitASC.put(bait,data.getBaitASC(bait));
			globalTotalBaitSpectralCount+=data.getBaitASC(bait);
			globalTotalSpectralCount+=baitASC;
		}

	}
	
	public APMSControlDataset permute(int rep) {
		APMSControlDataset permutedData = new APMSControlDataset(data.prey2length);
		
		prey2ASC = new HashMap<String,Double>(globalPrey2ASC);
		Map<Long,Integer> exp2NumProtein = new HashMap<Long,Integer>();
		Map<Long,Double> exp2TSC = new HashMap<Long,Double>();
		Map<String,Integer> prey2numExp = new HashMap<String,Integer>();
		int totalNumberProteins = 0;
		totalSpectralCount = (int) (globalTotalSpectralCount * rep);
		
		for (String prey : prey2ASC.keySet()) {
			prey2ASC.put(prey, prey2ASC.get(prey)*rep);
			prey2numExp.put(prey,baits.length*rep);
		}
		
		Long exp = 1l;
		for (String bait : baits) {
			for (int i = 0; i < rep; i++,exp++) {
				permutedData.exp2prey2sc.put(exp, new HashMap<String,Integer>());
				permutedData.exp2bait.put(exp,bait);
				//exp2TSC.put(exp, globalBait2ASC.get(bait));
				//exp2NumProtein.put(exp, globalBait2AverageNumProtein.get(bait).intValue());
				//totalNumberProteins+= globalBait2AverageNumProtein.get(bait).intValue();
				exp2TSC.put(exp,data.getAverageNumTSC());
				exp2NumProtein.put(exp, (int) data.getAverageNumProteins());
				totalNumberProteins+= (int) data.getAverageNumProteins();
			}
		}
		
		for (String prey : prey2ASC.keySet()) {
			Long randomExp = sampleExp(permutedData.exp2prey2sc,exp2NumProtein,prey);
			permutedData.exp2prey2sc.get(randomExp).put(prey,1);
			
			prey2numExp.put(prey, prey2numExp.get(prey)-1);
			prey2ASC.put(prey, prey2ASC.get(prey)-1);
			exp2TSC.put(randomExp, exp2TSC.get(randomExp)-1);
			
			totalSpectralCount--;
			totalNumberProteins--;	
		}
		
		while (totalNumberProteins > 0) {
			String randomPrey = samplePrey(prey2ASC, prey2numExp, totalSpectralCount);		
			Long randomExp = sampleExp(permutedData.exp2prey2sc,exp2NumProtein,randomPrey);		
			permutedData.exp2prey2sc.get(randomExp).put(randomPrey,1);
			
			prey2numExp.put(randomPrey, prey2numExp.get(randomPrey)-1);
			exp2TSC.put(randomExp, exp2TSC.get(randomExp)-1);
			
			totalSpectralCount--;
			totalNumberProteins--;
			
			if (exp2NumProtein.get(randomExp) == 0) {
				checkFilled(permutedData.exp2prey2sc, randomExp, prey2numExp);
			}						
		}
		
		for (String prey : prey2ASC.keySet()) {
			double remainingSC = prey2ASC.get(prey);
			for (int i = 0; i < remainingSC; i++) {
				Long randomExp = sampleExpSC(permutedData.exp2prey2sc,exp2TSC,prey);
				prey2ASC.put(prey, prey2ASC.get(prey)-1);
				if (randomExp != -1l) {
					permutedData.exp2prey2sc.get(randomExp).put(prey,permutedData.exp2prey2sc.get(randomExp).get(prey)+1);
				}
				totalSpectralCount--;
			}
		}
		
		for (Long curExp : permutedData.exp2prey2sc.keySet()) {
			String bait = permutedData.exp2bait.get(curExp);
			int sc = globalBait2baitASC.get(bait).intValue();
			if (sc > 0) {
				permutedData.exp2prey2sc.get(curExp).put(bait, globalBait2baitASC.get(bait).intValue());
			}
		}
		/*
		double totalBaitSc = globalTotalBaitSpectralCount * rep;
		for (Long curExp : permutedData.exp2prey2sc.keySet()) {
			String bait = permutedData.exp2bait.get(curExp);
			permutedData.exp2prey2sc.get(curExp).put(bait, 1);
		}
		
		List<Long> exps = new ArrayList<Long>(permutedData.exp2prey2sc.keySet());
		for (int i = 0; i < totalBaitSc; i++) {
			Long randomExp = exps.get(generator.nextInt(exps.size()));
			String bait = permutedData.exp2bait.get(randomExp);
			permutedData.exp2prey2sc.get(randomExp).put(bait, permutedData.exp2prey2sc.get(randomExp).get(bait)+1);
		}*/
		
		permutedData.setupBait2exp();
		permutedData.fillMissingLength();
		return permutedData;
	};
	
	private void checkFilled(Map<Long,Map<String,Integer>> exp2prey2sc, Long randomExp, Map<String,Integer> prey2numExp) {
		for (String prey : prey2numExp.keySet()) {
			if (!exp2prey2sc.get(randomExp).containsKey(prey)) {
				prey2numExp.put(prey, prey2numExp.get(prey)-1);
			}
		}
		
	}
	
	private Long sampleExpSC(Map<Long,Map<String,Integer>> exp2prey2sc, Map<Long,Double> exp2TSC, String prey) {
		Long randomExp = 0l;
		int randomMax = 0;
		for (Long exp : exp2prey2sc.keySet()) {
			if (exp2prey2sc.get(exp).containsKey(prey)) {
				randomMax += exp2TSC.get(exp);
			}
		}
		
		if (randomMax < 1) {
			int x = 1;
			return -1l;
		}
		int randomNum = generator.nextInt(randomMax);
		randomExp = findExpSC(exp2TSC, exp2prey2sc, prey, randomNum);
		return randomExp;
	}
	
	private Long findExpSC(Map<Long,Double> exp2TSC, Map<Long,Map<String,Integer>> exp2prey2sc, String prey, int randomNum) {
		double lo = 0;
		double hi = 0;
		for (Long exp : exp2TSC.keySet()) {
			if (exp2prey2sc.get(exp).containsKey(prey)) {
				hi = exp2TSC.get(exp) + lo;
				if (randomNum < hi) {
					exp2TSC.put(exp, Math.max(1,exp2TSC.get(exp)-1));
					return exp;
				}
				lo = hi;
			}
		}
		return -1l;
	}	
	
	private Long sampleExp(Map<Long,Map<String,Integer>> exp2prey2sc, Map<Long,Integer> exp2NumProtein, String prey) {
		Long randomExp = 0l;
		int randomMax = 0;
		for (Long exp : exp2prey2sc.keySet()) {
			if (!exp2prey2sc.get(exp).containsKey(prey)) {
				randomMax += exp2NumProtein.get(exp);
			}
		}
		
		if (randomMax < 1) {
			int x = 1;
		}
		int randomNum = generator.nextInt(randomMax);
		randomExp = findExp(exp2NumProtein, exp2prey2sc, prey, randomNum);
		return randomExp;
	}
	
	private Long findExp(Map<Long,Integer> exp2NumProtein, Map<Long,Map<String,Integer>> exp2prey2sc, String prey, int randomNum) {
		double lo = 0;
		double hi = 0;
		for (Long exp : exp2NumProtein.keySet()) {
			if (!exp2prey2sc.get(exp).containsKey(prey)) {
				hi = exp2NumProtein.get(exp) + lo;
				if (randomNum < hi) {
					exp2NumProtein.put(exp, exp2NumProtein.get(exp)-1);
					return exp;
				}
				lo = hi;
			}
		}
		return -1l;
	}
	
	private String samplePrey(Map<String,Double> prey2ASC, Map<String,Integer> prey2numExp, int randomMax) {
		if (randomMax < 1) {
			int x = 1;
		}
		String randomPrey = "";
		do {
			int randomNum = generator.nextInt(randomMax);
			randomPrey = findPrey(prey2ASC, randomNum);	
		} while (prey2numExp.get(randomPrey) <= 0);
		prey2ASC.put(randomPrey, prey2ASC.get(randomPrey)-1);
		return randomPrey;
	}
	
	private String findPrey(Map<String,Double> prey2ASC, int randomNum) {
		double lo = 0;
		double hi = 0;
		for (String prey : prey2ASC.keySet()) {
			if (prey2ASC.get(prey) > 0) {
				hi = prey2ASC.get(prey) + lo;
				if (randomNum < hi) {				
					return prey;
				}
				lo = hi;
			}
		}
		return "";
	}
	
	
	public APMSDataset permute() {
		APMSDataset permutedData = new APMSDataset(data.prey2length);
		
		prey2ASC = new HashMap<String,Double>();
		Map<Long,Integer> exp2NumProtein = new HashMap<Long,Integer>();
		Map<Long,Double> exp2TSC = new HashMap<Long,Double>();
		Map<String,Integer> prey2numExp = new HashMap<String,Integer>();
		int totalNumberProteins = 0;
		totalSpectralCount=0;
			
		Long exp = 1l;
		for (String bait : baits) {
			for (int i = 0; i < data.getNumReplicates(bait); i++,exp++) {
				permutedData.exp2prey2sc.put(exp, new HashMap<String,Integer>());
				permutedData.exp2bait.put(exp,bait);
				exp2TSC.put(exp,data.getAverageNumTSC());
				exp2NumProtein.put(exp, (int) data.getAverageNumProteins());
				totalNumberProteins+= (int) data.getAverageNumProteins();
			}
			
		}
		
		for (String prey : globalPrey2ASC.keySet()) {
			prey2ASC.put(prey,data.getPreyTSC(prey));
			totalSpectralCount += prey2ASC.get(prey);
			prey2numExp.put(prey,exp.intValue()-1);
		}
		
		
		for (String prey : prey2ASC.keySet()) {
			Long randomExp = sampleExp(permutedData.exp2prey2sc,exp2NumProtein,prey);
			permutedData.exp2prey2sc.get(randomExp).put(prey,1);
			
			prey2numExp.put(prey, prey2numExp.get(prey)-1);
			prey2ASC.put(prey, prey2ASC.get(prey)-1);
			exp2TSC.put(randomExp, exp2TSC.get(randomExp)-1);
			
			totalSpectralCount--;
			totalNumberProteins--;	
		}
		
		while (totalNumberProteins > 0) {
			String randomPrey = samplePrey(prey2ASC, prey2numExp, totalSpectralCount);		
			Long randomExp = sampleExp(permutedData.exp2prey2sc,exp2NumProtein,randomPrey);		
			permutedData.exp2prey2sc.get(randomExp).put(randomPrey,1);
			
			prey2numExp.put(randomPrey, prey2numExp.get(randomPrey)-1);
			exp2TSC.put(randomExp, exp2TSC.get(randomExp)-1);
			
			totalSpectralCount--;
			totalNumberProteins--;
			
			if (exp2NumProtein.get(randomExp) == 0) {
				checkFilled(permutedData.exp2prey2sc, randomExp, prey2numExp);
			}						
		}
		
		for (String prey : prey2ASC.keySet()) {
			double remainingSC = prey2ASC.get(prey);
			for (int i = 0; i < remainingSC; i++) {
				Long randomExp = sampleExpSC(permutedData.exp2prey2sc,exp2TSC,prey);
				prey2ASC.put(prey, prey2ASC.get(prey)-1);
				if (randomExp != -1l) {
					permutedData.exp2prey2sc.get(randomExp).put(prey,permutedData.exp2prey2sc.get(randomExp).get(prey)+1);
				}
				totalSpectralCount--;
			}
		}
		
		for (Long curExp : permutedData.exp2prey2sc.keySet()) {
			String bait = permutedData.exp2bait.get(curExp);
			int sc = globalBait2baitASC.get(bait).intValue();
			if (sc > 0) {
				permutedData.exp2prey2sc.get(curExp).put(bait, globalBait2baitASC.get(bait).intValue());
			}
		}
		/*
		double totalBaitSc = globalTotalBaitSpectralCount * rep;
		for (Long curExp : permutedData.exp2prey2sc.keySet()) {
			String bait = permutedData.exp2bait.get(curExp);
			permutedData.exp2prey2sc.get(curExp).put(bait, 1);
		}
		
		List<Long> exps = new ArrayList<Long>(permutedData.exp2prey2sc.keySet());
		for (int i = 0; i < totalBaitSc; i++) {
			Long randomExp = exps.get(generator.nextInt(exps.size()));
			String bait = permutedData.exp2bait.get(randomExp);
			permutedData.exp2prey2sc.get(randomExp).put(bait, permutedData.exp2prey2sc.get(randomExp).get(bait)+1);
		}*/
		
		permutedData.setupBait2exp();
		permutedData.fillMissingLength();
		return permutedData;
	};
}
