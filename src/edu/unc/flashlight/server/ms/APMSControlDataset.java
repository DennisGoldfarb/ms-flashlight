package edu.unc.flashlight.server.ms;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import edu.unc.flashlight.shared.util.Constants;
import edu.unc.flashlight.shared.util.Conversion;
import edu.unc.flashlight.shared.util.DeepCopy;

public class APMSControlDataset extends APMSDataset implements Serializable {
	private static final long serialVersionUID = 324L;
	String CONTROL_PREFIX = "CONTROL_";
	Map<Long,String> c_exp2bait;
	Map<String,Set<Long>> c_bait2exp;
	Map<Long,Map<String,Integer>> c_exp2prey2sc;
	
	public APMSControlDataset() {
		super();
		c_exp2bait = new HashMap<Long,String>();
		c_exp2prey2sc = new HashMap<Long,Map<String,Integer>>();
		c_bait2exp = new HashMap<String,Set<Long>>();
	}
	
	public APMSControlDataset(Map<String,Integer> prey2length) {
		this.prey2length = prey2length;
		exp2bait = new HashMap<Long,String>();
		exp2prey2sc = new HashMap<Long,Map<String,Integer>>();
		bait2exp = new HashMap<String,Set<Long>>();
		c_exp2bait = new HashMap<Long,String>();
		c_exp2prey2sc = new HashMap<Long,Map<String,Integer>>();
		c_bait2exp = new HashMap<String,Set<Long>>();
	}
	
	public APMSControlDataset(APMSDataset data) {
		prey2length = (Map<String,Integer>) DeepCopy.copy(data.prey2length);
		exp2bait = (Map<Long,String>)  DeepCopy.copy(data.exp2bait);
		exp2prey2sc = (Map<Long,Map<String,Integer>>) DeepCopy.copy(data.exp2prey2sc);
		bait2exp = (Map<String,Set<Long>>)  DeepCopy.copy(data.bait2exp);
		c_exp2bait = new HashMap<Long,String>();
		c_exp2prey2sc = new HashMap<Long,Map<String,Integer>>();
		c_bait2exp = new HashMap<String,Set<Long>>();
	}
	
	public APMSControlDataset(Map<Long,String> exp2bait, Map<Long,Map<String,Integer>> exp2prey2sc, Map<String,Integer> prey2length,
			Map<Long,String> c_exp2bait, Map<Long,Map<String,Integer>> c_exp2prey2sc, Set<Long> expsForScoring) {
		this.exp2bait = exp2bait;
		this.exp2prey2sc = exp2prey2sc;
		this.prey2length = prey2length;
		this.bait2exp = Conversion.invertMap(exp2bait);
		this.expsForScoring = expsForScoring;
		this.c_exp2bait = c_exp2bait;
		this.c_exp2prey2sc = c_exp2prey2sc;
		this.c_bait2exp = Conversion.invertMap(c_exp2bait);
		fillMissingLength();
	}
	
	public void fillMissingLength() {
		for (Long exp : exp2prey2sc.keySet()) {
			for (String prey : exp2prey2sc.get(exp).keySet()) {
				if (!prey2length.containsKey(prey)) {
					prey2length.put(prey,Constants.AVERAGE_PROTEIN_LENGTH);
				}
			}
		}
		for (Long exp : c_exp2prey2sc.keySet()) {
			for (String prey : c_exp2prey2sc.get(exp).keySet()) {
				if (!prey2length.containsKey(prey)) {
					prey2length.put(prey,Constants.AVERAGE_PROTEIN_LENGTH);
				}
			}
		}
	}
	
	public String[] getPrey() {
		Set<String> allPrey = new HashSet<String>();
		for (Long exp : exp2prey2sc.keySet()) {
			allPrey.addAll(exp2prey2sc.get(exp).keySet());
		}
		for (Long exp : c_exp2prey2sc.keySet()) {
			allPrey.addAll(c_exp2prey2sc.get(exp).keySet());
		}
		String[] prey = new String[allPrey.size()];
		return allPrey.toArray(prey);
	}
	
	public int getControlPreySC(String prey) {
		int tot = 0;
		for (Long exp : c_exp2bait.keySet()) {
			if (c_exp2prey2sc.get(exp).containsKey(prey)) {
				tot += c_exp2prey2sc.get(exp).get(prey);
			}
		}
		return tot;
	}
	
	public int getControlTotalSC(Long exp) {
		int tot = 0;
		for (String prey : c_exp2prey2sc.get(exp).keySet()) {
			tot += c_exp2prey2sc.get(exp).get(prey);
		}
		return tot;
	}
	
	public Long[] getControlExps() {
		Long[] exps = new Long[c_exp2bait.size()];
		return c_exp2bait.keySet().toArray(exps);
	}
	
	private void calculateStatistics() {
		int count = 0;
		averageNumProteins=0d;averageNumTSC=0d;averageReplicateOverlap=0d;stdNumProteins=0d;
		
		bait2averageNumProteins = new HashMap<String,Double>();
		for (Long exp : exp2prey2sc.keySet()) {
			String bait = exp2bait.get(exp);
			if (!bait2averageNumProteins.containsKey(bait)) bait2averageNumProteins.put(bait,0d);

			for (String prey : exp2prey2sc.get(exp).keySet()) {
				if (prey.equals(bait)) continue;/***************/
				int sc = exp2prey2sc.get(exp).get(prey);
				averageNumTSC += sc;
				averageNumProteins++;
				bait2averageNumProteins.put(bait, bait2averageNumProteins.get(bait)+1);
			}
		}
		for (Long exp : c_exp2prey2sc.keySet()) {
			for (String prey : c_exp2prey2sc.get(exp).keySet()) {
				int sc = c_exp2prey2sc.get(exp).get(prey);
				averageNumTSC += sc;
				averageNumProteins++;
			}
		}
		averageNumProteins /= (exp2prey2sc.size() + c_exp2prey2sc.size());
		averageNumTSC /= (exp2prey2sc.size() + c_exp2prey2sc.size());
		
		for (String bait : bait2exp.keySet()) {
			bait2averageNumProteins.put(bait, bait2averageNumProteins.get(bait)/bait2exp.get(bait).size());
		}
	}
	
	public Map<String,Double> getBait2AverageNumProteins() {
		if (bait2averageNumProteins == null) calculateStatistics();
		return bait2averageNumProteins;
	}
	
	public double getAverageNumProteins() {
		if (averageNumProteins == null) calculateStatistics();
		return averageNumProteins;
	}
	
	public double getAverageNumTSC() {
		if (averageNumTSC == null) calculateStatistics();
		return averageNumTSC;
	}
	
	public int getCtlTSC(Long exp) {
		int tsc = 0;
		for (String prey : c_exp2prey2sc.get(exp).keySet()) {
			tsc += c_exp2prey2sc.get(exp).get(prey);
		}
		return tsc;
	}
	
	public long getMaxExpId() {
		long maxExpId = 0l;
		for (Long exp : exp2bait.keySet()) {
			maxExpId = Math.max(exp, maxExpId);
		}
		for (Long exp : c_exp2bait.keySet()) {
			maxExpId = Math.max(exp, maxExpId);
		}
		return maxExpId;
	}
	
	public static APMSControlDataset mergeDatasets(APMSControlDataset d1, APMSDataset d2) {
		APMSControlDataset merged = new APMSControlDataset(d2);
		long maxExp = merged.getMaxExpId()+1;
		
		for (Long exp : d1.exp2bait.keySet()) {
			String bait = d1.exp2bait.get(exp);
			merged.exp2bait.put(maxExp, bait);
			if (!merged.bait2exp.containsKey(bait)) merged.bait2exp.put(bait, new HashSet<Long>());
			merged.bait2exp.get(bait).add(maxExp);
			merged.exp2prey2sc.put(maxExp, d1.exp2prey2sc.get(exp));
			maxExp++;
		}
		
		for (Long exp : d1.c_exp2bait.keySet()) {
			merged.c_exp2bait.put(maxExp,d1.c_exp2bait.get(exp));
			merged.c_exp2prey2sc.put(maxExp, d1.c_exp2prey2sc.get(exp));
			maxExp++;
		}
		
		return merged;
	}

}
