package edu.unc.flashlight.server.ms;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.unc.flashlight.server.rf.GenePairScore;
import edu.unc.flashlight.shared.util.Constants;
import edu.unc.flashlight.shared.util.Conversion;
import edu.unc.flashlight.shared.util.DeepCopy;

public class APMSDataset implements Serializable {
	private static final long serialVersionUID = 324L;
	// Raw Data
	protected Map<Long,String> exp2bait;
	protected Map<Long,Map<String,Integer>> exp2prey2sc;
	protected Map<String,Set<Long>> bait2exp;
	protected Map<String,Integer> prey2length;
	protected Set<Long> expsForScoring;
	
	// Statistics
	protected Integer[] replicateCounts;
	protected Integer maxRep;
	protected Double averageNumProteins;
	protected Map<String,Double> bait2averageNumProteins;
	protected Double stdNumProteins;
	protected Double averageNumTSC;
	protected Double averageReplicateOverlap;
	
	public APMSDataset() {
		exp2bait = new HashMap<Long,String>();
		exp2prey2sc = new HashMap<Long,Map<String,Integer>>();
		bait2exp = new HashMap<String,Set<Long>>();
		prey2length = new HashMap<String,Integer>();
	}
	
	public APMSDataset(APMSDataset data) {
		prey2length = (Map<String,Integer>) DeepCopy.copy(data.prey2length);
		exp2bait = (Map<Long,String>)  DeepCopy.copy(data.exp2bait);
		exp2prey2sc = (Map<Long,Map<String,Integer>>) DeepCopy.copy(data.exp2prey2sc);
		bait2exp = (Map<String,Set<Long>>)  DeepCopy.copy(data.bait2exp);
		fillMissingLength();
	}
	
	public APMSDataset(Map<String,Integer> prey2length) {
		this.prey2length = prey2length;
		exp2bait = new HashMap<Long,String>();
		exp2prey2sc = new HashMap<Long,Map<String,Integer>>();
		bait2exp = new HashMap<String,Set<Long>>();
		fillMissingLength();
	}
	
	public APMSDataset (Map<Long,String> exp2bait, Map<Long,Map<String,Integer>> exp2prey2sc, Map<String,Integer> prey2length, Set<Long> expsForScoring) {
		this.exp2bait = exp2bait;
		this.exp2prey2sc = exp2prey2sc;
		this.prey2length = prey2length;
		this.bait2exp = Conversion.invertMap(exp2bait);
		this.expsForScoring = expsForScoring;
		fillMissingLength();
	}
	
	public void setupBait2exp() {
		bait2exp = Conversion.invertMap(exp2bait);
	}
	
	public void fillMissingLength() {
		for (Long exp : exp2prey2sc.keySet()) {
			for (String prey : exp2prey2sc.get(exp).keySet()) {
				if (!prey2length.containsKey(prey)) {
					prey2length.put(prey,Constants.AVERAGE_PROTEIN_LENGTH);
				}
			}
		}
	}
	
	public int getNumBaits() {
		return bait2exp.size();
	}
	
	public int getNumExperiments() {
		return exp2bait.size();
	}
	
	public int getNumReplicates(String bait) {
		return bait2exp.get(bait).size();
	}
	
	public Integer[] getReplicateCounts() {
		if (replicateCounts == null) {
			Set<Integer> numReplicates = new HashSet<Integer>();
			for (String bait : bait2exp.keySet()) {
				numReplicates.add(getNumReplicates(bait));
			}
			replicateCounts = new Integer[numReplicates.size()];
			numReplicates.toArray(replicateCounts);
		}
		return replicateCounts;
	}
	
	public int getNumberInteractionsForReplicate(int rep) {
		int tot = 0;
		for (String bait : bait2exp.keySet()) {
			if (getNumReplicates(bait) == rep) {
				tot += getSpokeInteractionsForBait(bait).length;
			}
		}
		return tot;
	}
	
	public int getMaxReplicateCount() {
		if (maxRep == null) {
			maxRep = 0;
			for (int repCount : getReplicateCounts()) {
				maxRep = Math.max(repCount, maxRep);
			}
		}
		return maxRep;
	}
	
	private void calculateStatistics() {
		int count = 0;
		averageNumProteins=0d;averageNumTSC=0d;averageReplicateOverlap=0d;stdNumProteins=0d;
		
		Set<String> preys = new HashSet<String>();
		bait2averageNumProteins = new HashMap<String,Double>();

		for (Long exp : exp2prey2sc.keySet()) {
			String bait = exp2bait.get(exp);
			if (!bait2averageNumProteins.containsKey(bait)) bait2averageNumProteins.put(bait,0d);
			
			for (String prey : exp2prey2sc.get(exp).keySet()) {
				preys.add(prey);
				if (prey.equals(bait)) continue;/***************/
				int sc = exp2prey2sc.get(exp).get(prey);
				averageNumTSC += sc;
				averageNumProteins++;
				bait2averageNumProteins.put(bait, bait2averageNumProteins.get(bait)+1);
			}
			//averageNumProteins += exp2prey2sc.get(exp).size();
			for (Long exp2 : exp2prey2sc.keySet()) {
				if (exp2bait.get(exp2).equals(bait) && exp2 != exp) {
					Set<String> union = new HashSet<String>(exp2prey2sc.get(exp).keySet());
					Set<String> inter = new HashSet<String>(exp2prey2sc.get(exp).keySet());
					union.remove(bait);
					inter.remove(bait);
					inter.retainAll(exp2prey2sc.get(exp2).keySet());
					averageReplicateOverlap += inter.size()/(float)union.size();
					count++;
				}
			}
		}
		averageReplicateOverlap /= (float)count;
		averageNumProteins /= exp2prey2sc.size();
		averageNumTSC /= exp2prey2sc.size();
		
		for (Long exp : exp2prey2sc.keySet()) {
			String bait = exp2bait.get(exp);
			int numProt = 0;
			for (String prey : exp2prey2sc.get(exp).keySet()) {
				if (prey.equals(bait)) continue;
				numProt++;
			}
			stdNumProteins += Math.pow((numProt-averageNumProteins),2);
		}
		stdNumProteins /= exp2prey2sc.size();
		stdNumProteins = Math.sqrt(stdNumProteins);
		
		for (String bait : bait2exp.keySet()) {
			bait2averageNumProteins.put(bait, bait2averageNumProteins.get(bait)/bait2exp.get(bait).size());
		}
	}
	
	public double getNormalizedAverageSpectralCount(String prey) {
		double tot = 0;
		for (Long exp : exp2prey2sc.keySet()) {
			String bait = exp2bait.get(exp);
			if (!prey.equals(bait)) {
				if (exp2prey2sc.get(exp).containsKey(prey)) {
					int sc = exp2prey2sc.get(exp).get(prey);
					//double scale = getMaxReplicateCount() / (double) getNumReplicates(bait);
					tot += sc / (double) getNumReplicates(bait);
				}
			}
		}
		return tot;
	}
	
	public double getPreyTSC(String p) {
		double tot = 0d;
		for (Long exp : exp2prey2sc.keySet()) {
			String bait = exp2bait.get(exp);
			if (!bait.equals(p))  {
				if (exp2prey2sc.get(exp).containsKey(p)) {
					tot += exp2prey2sc.get(exp).get(p);
				}
			}
		}
		return tot;
	}
	
	public double getBaitASCTot(String bait) {
		double tot = 0d;
		for (Long exp : bait2exp.get(bait)) {
			for (String prey : exp2prey2sc.get(exp).keySet()) {
				if (!bait.equals(prey)) {
					tot += exp2prey2sc.get(exp).get(prey);
				}
			}
		}
		return tot/bait2exp.get(bait).size();
	}
	
	public double getBaitASC(String bait) {
		double tot = 0d;
		for (Long exp : bait2exp.get(bait)) {
			if (exp2prey2sc.get(exp).containsKey(bait)) {
				tot += exp2prey2sc.get(exp).get(bait);
			}
		}
		return tot/bait2exp.get(bait).size();
	}
	
	public Map<String,Double> getBait2AverageNumProteins() {
		if (bait2averageNumProteins == null) calculateStatistics();
		return bait2averageNumProteins;
	}
	
	public double getAverageNumProteins() {
		if (averageNumProteins == null) calculateStatistics();
		return averageNumProteins;
	}
	
	public double getStdNumProteins() {
		if (stdNumProteins == null) calculateStatistics();
		return stdNumProteins;
	}
	
	public double getAverageNumTSC() {
		if (averageNumTSC == null) calculateStatistics();
		return averageNumTSC;
	}
	
	public double getAverageReplicateOverlap() {
		if (averageReplicateOverlap == null) calculateStatistics();
		return averageReplicateOverlap;
	}
	
	public int getExpTSC(Long exp) {
		int tsc = 0;
		for (String prey : exp2prey2sc.get(exp).keySet()) {
			tsc += exp2prey2sc.get(exp).get(prey);
		}
		return tsc;
	}
	
	public String[] getBaits() {
		String[] baits = new String[bait2exp.keySet().size()];
		return bait2exp.keySet().toArray(baits);
	}
	
	public boolean containsBait(String bait) {
		return bait2exp.keySet().contains(bait);
	}
	
	public String[] getPrey() {
		Set<String> allPrey = new HashSet<String>();
		for (Long exp : exp2prey2sc.keySet()) {
			allPrey.addAll(exp2prey2sc.get(exp).keySet());
		}
		String[] prey = new String[allPrey.size()];
		return allPrey.toArray(prey);
	}
	
	private void setupExpsForScoring() {
		if (expsForScoring == null) {
			expsForScoring = exp2bait.keySet();
		}
	}
	
	public GenePairScore[] getSpokeInteractionsForBait(String bait) {
		setupExpsForScoring();
		List<GenePairScore> tmpScores = new ArrayList<GenePairScore>();
		Set<String> preyForBait = new HashSet<String>();
		for (Long exp : bait2exp.get(bait)) {
			if (expsForScoring.contains(exp)) {
				for (String prey : exp2prey2sc.get(exp).keySet()) {
					if (!bait.equals(prey) && !preyForBait.contains(prey)) {
						tmpScores.add(new GenePairScore(bait, prey));
						preyForBait.add(prey);
					}
				}
			}
		}
		return tmpScores.toArray(new GenePairScore[tmpScores.size()]);
	}
	
	public GenePairScore[] getSpokeInteractions() {
		setupExpsForScoring();
		List<GenePairScore> tmpScores = new ArrayList<GenePairScore>();
		for (String bait : bait2exp.keySet()) {
			tmpScores.addAll(Arrays.asList(getSpokeInteractionsForBait(bait)));
		}
		return tmpScores.toArray(new GenePairScore[tmpScores.size()]);
	}
	
	public GenePairScore[] getMatrixInteractions() {
		setupExpsForScoring();
		List<GenePairScore> tmpScores = new ArrayList<GenePairScore>();
		Set<String> hashes = new HashSet<String>();
		for (Long exp : expsForScoring) {
			for (String prey : exp2prey2sc.get(exp).keySet()) {
				for (String prey2 : exp2prey2sc.get(exp).keySet()) {
					if (prey.compareTo(prey2) > 0) {
						tmpScores.add(new GenePairScore(prey, prey2));
						hashes.add(Conversion.doGenePairHash(prey, prey2));
					}
				}
			}
		}
		return tmpScores.toArray(new GenePairScore[tmpScores.size()]);
	}
}
