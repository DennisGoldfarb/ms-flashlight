package edu.unc.flashlight.server.ms;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.unc.flashlight.shared.util.Conversion;
import edu.unc.flashlight.shared.util.XORShiftRandom;


public class MSControlPermutationGenerator {
	// Original data
	private APMSControlDataset data;
	private String[] baits;
	// Original data statistics
	private Map<String,Double> globalBait2ASC;
	private Map<String,Double> globalPrey2ASC;
	private Map<String,Double> globalBait2AverageNumProtein;
	private Map<String,Double> globalBait2baitASC;
	private double globalTotalSpectralCount = 0;
	// Other
	//private Random generator = new Random();
	private XORShiftRandom generator = new XORShiftRandom();
	
	
	public MSControlPermutationGenerator(APMSControlDataset data) {
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
			globalPrey2ASC.put(prey, data.getNormalizedAverageSpectralCount(prey));
		}
		
		for (String bait : data.bait2exp.keySet()) {
			double baitASC = data.getBaitASCTot(bait);
			globalBait2ASC.put(bait, baitASC);
			globalBait2baitASC.put(bait,data.getBaitASC(bait));
			globalTotalSpectralCount+=baitASC;
		}
	}
	
	public APMSControlDataset permute(int rep) {
		APMSControlDataset permutedData = new APMSControlDataset(data.prey2length);
		
		Map<String,Double> prey2ASC = new HashMap<String,Double>();
		Map<Long,Integer> exp2NumProtein = new HashMap<Long,Integer>();
		Map<Long,Double> exp2TSC = new HashMap<Long,Double>();

		int totalNumberExperiments = (rep * baits.length) + data.c_exp2bait.keySet().size();
		int totalNumberProteins = totalNumberExperiments * (int) data.getAverageNumProteins() ;
		int totalSpectralCount = (int) (globalTotalSpectralCount * rep);
		
		for (String prey : globalPrey2ASC.keySet()) {
			prey2ASC.put(prey, (globalPrey2ASC.get(prey)*rep) + data.getControlPreySC(prey));
		}
		
		for (Long controlExp : data.c_exp2bait.keySet()) {
			totalSpectralCount+=data.getControlTotalSC(controlExp);
		}
		
		Long exp = 1l;
		for (String bait : baits) {
			for (int i = 0; i < rep; i++,exp++) {
				permutedData.exp2prey2sc.put(exp, new HashMap<String,Integer>());
				permutedData.exp2bait.put(exp,bait);
				exp2TSC.put(exp,data.getAverageNumTSC());
				exp2NumProtein.put(exp, (int) data.getAverageNumProteins());
			}
		}
		
		for (long controlExp : data.c_exp2bait.keySet()) {
			permutedData.c_exp2bait.put(exp,data.c_exp2bait.get(controlExp));
			permutedData.c_exp2prey2sc.put(exp, new HashMap<String,Integer>());
			exp2TSC.put(exp,data.getAverageNumTSC());
			exp2NumProtein.put(exp, (int) data.getAverageNumProteins());
			exp++;
		}
		
		return permuteImpl(permutedData,exp2NumProtein,totalNumberProteins,prey2ASC,exp2TSC,totalSpectralCount);
	};
	
	
	public APMSControlDataset permute() {
		APMSControlDataset permutedData = new APMSControlDataset(data.prey2length);
		
		Map<String,Double> prey2ASC = new HashMap<String,Double>();
		Map<Long,Integer> exp2NumProtein = new HashMap<Long,Integer>();
		Map<Long,Double> exp2TSC = new HashMap<Long,Double>();

		int totalNumberProteins = 0;
		int totalSpectralCount=0;
			
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
		
		for (long controlExp : data.c_exp2bait.keySet()) {
			permutedData.c_exp2bait.put(exp,data.c_exp2bait.get(controlExp));
			permutedData.c_exp2prey2sc.put(exp, new HashMap<String,Integer>());
			exp2TSC.put(exp,data.getAverageNumTSC());
			exp2NumProtein.put(exp, (int) data.getAverageNumProteins());
			totalNumberProteins+= (int) data.getAverageNumProteins();
			exp++;
		}
		
		for (String prey : globalPrey2ASC.keySet()) {
			prey2ASC.put(prey,data.getPreyTSC(prey) + data.getControlPreySC(prey));
			totalSpectralCount += prey2ASC.get(prey);
		}
		
		return permuteImpl(permutedData,exp2NumProtein,totalNumberProteins,prey2ASC,exp2TSC,totalSpectralCount);
	};
	
	private void createPreyByASC(Long curExp, Map<String,Double> prey2ASC, Map<String,Short> prey2index, Set<String> preys, ArrayList<Short> preyByASC) {
		int newSize = calcTotPrey(preys, prey2ASC);
		preyByASC.ensureCapacity(newSize);
		
		int lastIndex = 0, i = 0, nextIndex = 0, ncopies;
		short val;
		for (String prey : preys) {
			ncopies = (int) Math.ceil(prey2ASC.get(prey));
 			val = prey2index.get(prey);
 			nextIndex = lastIndex+ncopies;
 			for (i=lastIndex; i < nextIndex; i++) {
 				if (i < preyByASC.size()) preyByASC.set(i, val);
 				else preyByASC.add(val);
 			}
 			lastIndex = i;
		}
		preyByASC.subList(newSize, preyByASC.size()).clear();
	}
	
	private int calcTotPrey(Set<String> preys, Map<String,Double> prey2ASC) {
		int tot = 0;
		//int ncopies;
		for (String prey : preys) {
			tot += (int) Math.ceil(prey2ASC.get(prey));
			//ncopies = (int) Math.ceil(prey2ASC.get(prey));
			//if (ncopies > 0) tot += ncopies;
		}
		return tot;
	}
	
	private APMSControlDataset permuteImpl2(APMSControlDataset permutedData, Map<Long,Integer> exp2NumProtein, int totalNumberProteins,
			Map<String,Double> prey2ASC, Map<Long,Double> exp2TSC, int totalSpectralCount) {
		
		Map<String,Short> prey2index = new HashMap<String,Short>();
		short startIndex = Short.MIN_VALUE;
		for (String prey : prey2ASC.keySet()) {
			prey2index.put(prey,startIndex);
			startIndex++;
		}
		Map<Short,String> index2prey = (Map<Short,String>) Conversion.invertOne2OneMap(prey2index);
		
		List<Short> allsc = new ArrayList<Short>(totalSpectralCount);
		for (String prey : prey2ASC.keySet()) {
 			int ncopies = (int) Math.ceil(prey2ASC.get(prey));
 			if (ncopies > 0) {
	 			short val = prey2index.get(prey);
	 			allsc.addAll(Collections.nCopies(ncopies,val));
 			}
 		}
		Collections.shuffle(allsc, generator);
		int i=0;
		for (Long exp : permutedData.exp2prey2sc.keySet()) {
			for (int j = 0; j < exp2TSC.get(exp); j++,i++) {
				String prey = index2prey.get(allsc.get(i));
				if (!permutedData.exp2prey2sc.get(exp).containsKey(prey)) permutedData.exp2prey2sc.get(exp).put(prey, 0);
				permutedData.exp2prey2sc.get(exp).put(prey,permutedData.exp2prey2sc.get(exp).get(prey)+1);
			}
		}
		for (Long exp : permutedData.c_exp2prey2sc.keySet()) {
			for (int j = 0; j < exp2TSC.get(exp); j++,i++) {
				String prey = index2prey.get(allsc.get(i));
				if (!permutedData.c_exp2prey2sc.get(exp).containsKey(prey)) permutedData.c_exp2prey2sc.get(exp).put(prey, 0);
				permutedData.c_exp2prey2sc.get(exp).put(prey,permutedData.c_exp2prey2sc.get(exp).get(prey)+1);
			}
		}
		
		permutedData.setupBait2exp();		
		permutedData.fillMissingLength();
		return permutedData;
	}
	
	private APMSControlDataset permuteImpl(APMSControlDataset permutedData, Map<Long,Integer> exp2NumProtein, int totalNumberProteins,
			Map<String,Double> prey2ASC, Map<Long,Double> exp2TSC, int totalSpectralCount) {
		/**** STEP 1: Assign each protein to an experiment based on experiment # protein ****/
		List<Short> expByNumProtein = new ArrayList<Short>(totalNumberProteins);
		for (Long tmpExp : exp2NumProtein.keySet()) {
			for (int j = 0; j < exp2NumProtein.get(tmpExp); j++) {
				expByNumProtein.add(tmpExp.shortValue());
			}
		}
		Collections.shuffle(expByNumProtein, generator);
		int i = 0;
		for (String prey : prey2ASC.keySet()) {
			Long randomExp = expByNumProtein.get(i % expByNumProtein.size()).longValue();
			if (randomExp < 0) {
				randomExp = Short.MAX_VALUE + (randomExp - Short.MIN_VALUE);
			}
			if (permutedData.exp2prey2sc.containsKey(randomExp)) permutedData.exp2prey2sc.get(randomExp).put(prey,1);
			else permutedData.c_exp2prey2sc.get(randomExp).put(prey,1);
			
			prey2ASC.put(prey, prey2ASC.get(prey)-1);
			exp2TSC.put(randomExp, exp2TSC.get(randomExp)-1);
			
			totalSpectralCount--;
			totalNumberProteins--;
			i++;
		}
		
		/**** STEP 2: Shuffle the spectral counts and add the protein to the next exp ****/
		Map<String,Short> prey2index = new HashMap<String,Short>();
		short startIndex = Short.MIN_VALUE;
		for (String prey : prey2ASC.keySet()) {
			prey2index.put(prey,startIndex);
			startIndex++;
		}
		Map<Short,String> index2prey = (Map<Short,String>) Conversion.invertOne2OneMap(prey2index);
		
		List<Short> preyBySC = new ArrayList<Short>(totalSpectralCount);
 		
 		for (String prey : prey2ASC.keySet()) {
 			int ncopies = (int) Math.ceil(prey2ASC.get(prey));
 			if (ncopies > 0) {
 				short val = prey2index.get(prey);
	 			preyBySC.addAll(Collections.nCopies(ncopies,val));
 			}
 		}
 		Collections.shuffle(preyBySC, generator);
 		
 		/*for (Long curExp : permutedData.exp2prey2sc.keySet()) {
			String bait = permutedData.exp2bait.get(curExp);
			int sc = globalBait2baitASC.get(bait).intValue();
			if (sc > 0) {
				permutedData.exp2prey2sc.get(curExp).put(bait, 1);
				totalNumberProteins--;
				prey2ASC.put(bait, prey2ASC.get(bait)-1);
				exp2TSC.put(curExp, exp2TSC.get(curExp)-1);
			}
 		}*/
 		
 		int ii = 0;
 		while (totalNumberProteins > 0) {
 			String randomPrey = index2prey.get(preyBySC.get(ii));
 			Long randomExp = (long) expByNumProtein.get(i);
 			if (randomExp < 0) {
				randomExp = Short.MAX_VALUE + (randomExp - Short.MIN_VALUE);
			}
 			
 			if (permutedData.exp2prey2sc.containsKey(randomExp)) {
 				if (!permutedData.exp2prey2sc.get(randomExp).containsKey(randomPrey)) {
 					permutedData.exp2prey2sc.get(randomExp).put(randomPrey,1);
 					totalNumberProteins--; i++;
 					prey2ASC.put(randomPrey, prey2ASC.get(randomPrey)-1);
 					exp2TSC.put(randomExp, exp2TSC.get(randomExp)-1);
 				}
			} else {
				if (!permutedData.c_exp2prey2sc.get(randomExp).containsKey(randomPrey)) {
					permutedData.c_exp2prey2sc.get(randomExp).put(randomPrey,1);
					totalNumberProteins--; i++;
					prey2ASC.put(randomPrey, prey2ASC.get(randomPrey)-1);
					exp2TSC.put(randomExp, exp2TSC.get(randomExp)-1);
				}
			}
 			ii = (ii+1) % preyBySC.size();
 		} 		
 		
 		/**** STEP 3: For each experiment, make list of prey by SC remaining, shuffle and fill exp ****/
 		
 		ArrayList<Short> preyByASC = new ArrayList<Short>();
 		Map<String,Integer> prey2sc;
 		int expTSC, preyASCsize, index;
 		for (Long curExp : exp2TSC.keySet()) {
 			if (permutedData.exp2prey2sc.containsKey(curExp)) prey2sc = permutedData.exp2prey2sc.get(curExp);		
 			else prey2sc = permutedData.c_exp2prey2sc.get(curExp);

 			createPreyByASC(curExp, prey2ASC, prey2index, prey2sc.keySet(),preyByASC);
 			expTSC = (int) Math.ceil(exp2TSC.get(curExp));
			preyASCsize = preyByASC.size();
			for (int j = 0; j < expTSC; j++) {
				index = j % preyASCsize;
				if (index == 0) Collections.shuffle(preyByASC, generator);
				String prey = index2prey.get(preyByASC.get(index));
				prey2sc.put(prey,prey2sc.get(prey)+1);
				prey2ASC.put(prey, Math.max(1,prey2ASC.get(prey)-1));
				
			} 			
 		}
		
 		/** PERMUTE BAIT SPECTRAL COUNTS **/
		totalSpectralCount = 0;
 		Map<Integer,Long> index2exp = new HashMap<Integer,Long>();
 		startIndex = 0;
		for (Long curExp : permutedData.exp2prey2sc.keySet()) {
			String bait = permutedData.exp2bait.get(curExp);
			int sc = globalBait2baitASC.get(bait).intValue();
			if (sc > 0) {
				permutedData.exp2prey2sc.get(curExp).put(bait, globalBait2baitASC.get(bait).intValue());
			}
			/*permutedData.exp2prey2sc.get(curExp).put(bait, 1);
			totalSpectralCount += sc-1;
			index2exp.put((int)startIndex, curExp);
			startIndex++;*/
		}
		
		/*for (i = 0; i < totalSpectralCount; i++) {
			index = generator.nextInt(index2exp.size());
			long curExp = index2exp.get(index);
			String bait = permutedData.exp2bait.get(curExp);
			permutedData.exp2prey2sc.get(curExp).put(bait, permutedData.exp2prey2sc.get(curExp).get(bait)+1);
		}*/
 		
 		
		
		permutedData.setupBait2exp();		
		permutedData.fillMissingLength();
		
		return permutedData;
	}
	
	public APMSControlDataset permute2() {
		APMSControlDataset permutedData = new APMSControlDataset(data.prey2length);
		
		Map<String,Double> prey2ASC = new HashMap<String,Double>();
		Map<Long,Double> exp2TSC = new HashMap<Long,Double>();
		int totalSpectralCount = 0;
			
		for (long exp : data.exp2bait.keySet()) {
			String bait = data.exp2bait.get(exp);
			permutedData.exp2prey2sc.put(exp, new HashMap<String,Integer>());
			permutedData.exp2bait.put(exp,bait);
			exp2TSC.put(exp,(double) data.getExpTSC(exp));
		}
		
		for (long exp : data.c_exp2bait.keySet()) {
			permutedData.c_exp2bait.put(exp,data.c_exp2bait.get(exp));
			permutedData.c_exp2prey2sc.put(exp, new HashMap<String,Integer>());
			exp2TSC.put(exp,(double) data.getCtlTSC(exp));
		}
		
		for (String prey : globalPrey2ASC.keySet()) {
			prey2ASC.put(prey,data.getPreyTSC(prey) + data.getControlPreySC(prey));
			totalSpectralCount += prey2ASC.get(prey);
		}
		
		return permuteImpl(permutedData,prey2ASC,exp2TSC, totalSpectralCount);
	};
	
	public APMSControlDataset permuteImpl(APMSControlDataset permutedData, Map<String,Double> prey2ASC, Map<Long,Double> exp2TSC, int totalSpectralCount) {
		Map<String,Short> prey2index = new HashMap<String,Short>();
		short startIndex = Short.MIN_VALUE;
		for (String prey : prey2ASC.keySet()) {
			prey2index.put(prey,startIndex);
			startIndex++;
		}
		Map<Short,String> index2prey = (Map<Short,String>) Conversion.invertOne2OneMap(prey2index);
		
		List<Short> preyBySC = new ArrayList<Short>(totalSpectralCount);
 		
 		for (String prey : prey2ASC.keySet()) {
 			int ncopies = (int) Math.ceil(prey2ASC.get(prey));
 			if (ncopies > 0) {
 				short val = prey2index.get(prey);
	 			preyBySC.addAll(Collections.nCopies(ncopies,val));
 			}
 		}
 		Collections.shuffle(preyBySC, generator);
 		
 		int index = 0;
 		for (Long exp : exp2TSC.keySet()) {
 			for (int i = 0; i < exp2TSC.get(exp); i++) {
 				Short preyIndex = preyBySC.get(index);
 				String prey = index2prey.get(preyIndex);
 				if (permutedData.exp2prey2sc.containsKey(exp)) {
	 				if (!permutedData.exp2prey2sc.get(exp).containsKey(prey)) {
	 					permutedData.exp2prey2sc.get(exp).put(prey,0);
	 				}
	 				permutedData.exp2prey2sc.get(exp).put(prey,permutedData.exp2prey2sc.get(exp).get(prey)+1);
 				} else {
 					if (!permutedData.c_exp2prey2sc.get(exp).containsKey(prey)) {
	 					permutedData.c_exp2prey2sc.get(exp).put(prey,0);
	 				}
	 				permutedData.c_exp2prey2sc.get(exp).put(prey,permutedData.c_exp2prey2sc.get(exp).get(prey)+1);
 				}
 				index = ((index + 1) % preyBySC.size());
 			}
 		}
		
		for (Long curExp : permutedData.exp2prey2sc.keySet()) {
			String bait = permutedData.exp2bait.get(curExp);
			int sc = globalBait2baitASC.get(bait).intValue();
			if (sc > 0) {
				permutedData.exp2prey2sc.get(curExp).put(bait, globalBait2baitASC.get(bait).intValue());
			}
		}
 		
 		permutedData.setupBait2exp();		
		permutedData.fillMissingLength();
 		
		return permutedData;
	}
}
