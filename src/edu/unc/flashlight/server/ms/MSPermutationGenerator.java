package edu.unc.flashlight.server.ms;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.apache.commons.math3.distribution.PoissonDistribution;


public class MSPermutationGenerator {
	/*
	// Original data
	private APMSDataset data;
	private String[] baits;
	// Permuted data
	// Original data statistics
	private Map<String,Integer> globalPrey2TSC;
	private Map<String,Integer> prey2index;
	private int globalTotalSpectralCount = 0;
	private Map<String,Integer> prey2TSC;
	private int totalSpectralCount = 0;
	// Other
	private Random generator = new Random();
	private PoissonDistribution poissonGenerator;
	private PoissonDistribution TSCGenerator;
	
	public MSPermutationGenerator(APMSDataset data) {
		this.data = data;
		calculateDatasetStats();
		poissonGenerator = new PoissonDistribution(data.getAverageNumProteins());
		TSCGenerator = new PoissonDistribution(data.getAverageNumTSC());
	}
	
	private void calculateDatasetStats() {
		baits = data.getBaits();
		globalPrey2TSC = new HashMap<String,Integer>();
		
		for (String prey : data.getPrey()) {
			globalPrey2TSC.put(prey, data.getNormalizedAverageSpectralCount(prey));
		}
		
		prey2index = new HashMap<String,Integer>();
		
		int i=0;
		for (String prey : globalPrey2TSC.keySet()) {
			prey2index.put(prey,i);
			globalTotalSpectralCount+=globalPrey2TSC.get(prey);
			i++;
		}
	}
	
	public void permuteReplicate(APMSDataset permutedData, long exp, long first_exp) {
		/*int averageNumProteins = permutedData.exp2prey2sc.get(first_exp).size();
		/*averageNumTSC = 0;
		for (String prey : exp2prey2sc.get(exp).keySet()) {
			averageNumTSC += exp2prey2sc.get(exp).get(prey);
		}
		int replicatePreySize = (int) ((2-data.getAverageReplicateOverlap()) * averageNumProteins);
		int averageNumTSC = data.getAverageNumTSC();//TSCGenerator.sample();//(int) (data.getAverageNumTSC() * (averageNumProteins/(float)data.getAverageNumProteins()));
		
		List<String> tmpExpPrey = new ArrayList<String>();
		List<Integer> tmpExpTSC = new ArrayList<Integer>();
		int total=0, i=0, last_count=0, randomNum, randomIndex;
		
		// take first mock run
		Set<String> sampledPrey = new HashSet<String>();
		for (String prey : permutedData.exp2prey2sc.get(first_exp).keySet()) {
			tmpExpPrey.add(prey);
			tmpExpTSC.add(permutedData.exp2prey2sc.get(first_exp).get(prey) + last_count);
			last_count = tmpExpTSC.get(i);
			i++;
			sampledPrey.add(prey);
		}
		
		
		// randomly sample others
		while (i < replicatePreySize) {
			randomNum = generator.nextInt(TSCArr.size()-1);
			String prey = preyArr.get(randomNum);
			if (!sampledPrey.contains(prey)) {
				tmpExpPrey.add(prey);
				tmpExpTSC.add((int) (Math.ceil(prey2TSC.get(prey)/(double)(data.getMaxReplicateCount()*data.exp2prey2sc.size())) + last_count));
				last_count = tmpExpTSC.get(i);
				i++;
				sampledPrey.add(prey);
				permutedData.exp2prey2sc.get(exp).put(prey, 1);
				total++;
			}
		}
		
		/*String bait = permutedData.exp2bait.get(exp);
		if (prey2TSC.containsKey(bait)) {
			permutedData.exp2prey2sc.get(exp).put(bait, 1);
			total++;
		}
		
		while (permutedData.exp2prey2sc.get(exp).size() < averageNumProteins && total < averageNumTSC) {
			randomNum = generator.nextInt(tmpExpTSC.get(tmpExpTSC.size()-1));
			randomIndex = binarySearch(randomNum,tmpExpTSC);
			String prey = tmpExpPrey.get(randomIndex);	
			if (!permutedData.exp2prey2sc.get(exp).containsKey(prey)) {
				permutedData.exp2prey2sc.get(exp).put(prey, 0);
			}
			permutedData.exp2prey2sc.get(exp).put(prey,permutedData.exp2prey2sc.get(exp).get(prey)+1);
			total++;
		}

		while(total < averageNumTSC) {
			randomNum = generator.nextInt(tmpExpTSC.get(tmpExpTSC.size()-1));
			randomIndex = binarySearch(randomNum,tmpExpTSC);
			String prey = tmpExpPrey.get(randomIndex);	
			if (!permutedData.exp2prey2sc.get(exp).containsKey(prey)) {
				continue;
				//permutedData.exp2prey2sc.get(exp).put(prey, 0);
			}
			permutedData.exp2prey2sc.get(exp).put(prey,permutedData.exp2prey2sc.get(exp).get(prey)+1);
			total++;
		}*
	}
	
	public void permuteFirst(APMSDataset permutedData,long exp) {
		String bait = permutedData.exp2bait.get(exp);
		
		//int averageNumProteins = Math.max(100,(int) Math.round(generator.nextGaussian() * data.getStdNumProteins() + data.getAverageNumProteins()));
		int averageNumProteins = data.getAverageNumProteins();//poissonGenerator.sample();
		int averageNumTSC = data.getAverageNumTSC();//TSCGenerator.sample();//(int) (data.getAverageNumTSC() * (averageNumProteins/(float)data.getAverageNumProteins()));
		int total = 0;
		Integer tmpTotalSpectralCount = 0;
				
		/*if (prey2TSC.containsKey(bait)) {
			permuted_exp2prey2sc.get(exp).put(bait, 1); 
			expPrey[i] = bait;
			/*for (String bait2 : bait2exp.keySet()) {
				for (Long exp2 : bait2exp.get(bait2)) {
					expTSC[i] += exp2prey2sc.get(exp2).get(bait2)*((float)bait2exp.get(bait2).size())/maxRep;
				}
			}
			expTSC[i] /=  bait2exp.size();*
			expTSC[i] = prey2TSC.get(bait) + last_count;
			last_count = expTSC[i];
			i++; total++;
		}*
		
		while (permutedData.exp2prey2sc.get(exp).size() < averageNumProteins) {			
			String prey = sampleOne(prey2TSC.keySet());
			if (!permutedData.exp2prey2sc.get(exp).containsKey(prey)) {
				permutedData.exp2prey2sc.get(exp).put(prey, 0);
			} 
			permutedData.exp2prey2sc.get(exp).put(prey,permutedData.exp2prey2sc.get(exp).get(prey)+1);
			total++;
		}
		
		for (String prey : permutedData.exp2prey2sc.get(exp).keySet()) {
			tmpTotalSpectralCount += prey2TSC.get(prey);
		}
		
		for (;total < averageNumTSC && tmpTotalSpectralCount > 0; total++) {
			String prey = sampleOne(permutedData.exp2prey2sc.get(exp).keySet(), tmpTotalSpectralCount);//Math.max(tmpTotalSpectralCount,permutedData.exp2prey2sc.get(exp).keySet().size()));	
			permutedData.exp2prey2sc.get(exp).put(prey,permutedData.exp2prey2sc.get(exp).get(prey)+1);
			tmpTotalSpectralCount--;
		}
	}
	
	public APMSDataset permute(int rep) {
		APMSDataset permutedData = new APMSDataset(data.prey2length);
		
		Long exp = 1l;
		Long first_exp = 1l;
		totalSpectralCount = globalTotalSpectralCount*rep;
		prey2TSC = new HashMap<String,Integer>(globalPrey2TSC);
		for (String prey : prey2TSC.keySet()) {
			prey2TSC.put(prey, prey2TSC.get(prey)*rep);
		}
		for (String bait : baits) {
			permutedData.exp2prey2sc.put(exp, new HashMap<String,Integer>());
			permutedData.exp2bait.put(exp,bait);
			permuteFirst(permutedData,exp);
			first_exp = exp;
			exp++;
			for (int i = 1; i < rep; i++,exp++) {
				permutedData.exp2prey2sc.put(exp, new HashMap<String,Integer>());
				permutedData.exp2bait.put(exp,bait);
				permuteFirst(permutedData,exp);
				//permuteReplicate(permutedData,exp,first_exp);
			}
		}
		return permutedData;
	};
	
	private String sampleOne(Set<String> targets, int totalSpectralCount) {
		String prey = sampleOneHelper(targets,totalSpectralCount);
		removeSC(prey);
		return prey;
	}
	
	private String sampleOne(Set<String> targets) {
		String prey = sampleOneHelper(targets,totalSpectralCount);
		removeSC(prey);
		return prey;
	}
	
	private String sampleOneHelper(Set<String> targets, int totalSpectralCount) {
		int randomNum = generator.nextInt(totalSpectralCount);
		int lo = 0;
		for (String prey : targets) {
			int hi = prey2TSC.get(prey) + lo;
			if (randomNum < hi) {
				return prey;
			}
			lo = hi;
		}		
		return "";
	}
	
	private void removeSC(String prey) {
		//int sc = prey2TSC.get(prey);
		//if (sc == 1) {
	
		//}
		//else {
			prey2TSC.put(prey, prey2TSC.get(prey)-1);
			totalSpectralCount--;
		//}
	}
	
	private int binarySearch(int target, List<Integer> arr) {
		int hi = arr.size()-1;
		int lo = 0;
		int mid = (hi+lo)/2;
		int val = 0;
		while (hi >= lo) {	
			mid = (hi+lo)/2;
			val = arr.get(mid);
			
			if (val == target) return mid;
			if (target > val) {
				lo = mid+1;
			} else {
				hi = mid-1;
			}
		}
		if (target < val) return mid;
		return mid+1;
	}*/
}
