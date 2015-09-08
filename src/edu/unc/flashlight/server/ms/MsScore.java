package edu.unc.flashlight.server.ms;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.unc.flashlight.shared.GenericCommand;

public class MsScore { /*extends MassSpecScorer {
	double[] HGSCore;
	double[] CompPASS;
	double[] control;
	private Map<Long,Set<Long>> bait2exp;
	private static double b0 = -5.577;//-8.1931901;
	private static double b1 = 0.005154;//0.0040110;
	private static double b2 = 0.1301;//0.1548544;
	private static double b3 = 2.507;//3.0495931;
	
	public MsScore(Map<Long, Long> exp2bait, Map<Long, Map<Long, Integer>> exp2prey2sc, Map<Long, Set<Long>> ap2exp, 
			final List<String> hashes, double[] HGSCore, double[] CompPASS, double[] control) {
		super(exp2bait, exp2prey2sc, ap2exp, hashes);
		this.HGSCore = HGSCore;
		this.CompPASS = CompPASS;
		this.control = control;
		bait2exp = new HashMap<Long,Set<Long>>();
	}

	public double[] calculateScores(GenericCommand<Double> updateProgress) {
		formatData();
		//updateProgress.execute(.05);
		//scoreData();
		//updateProgress.execute(.1);
		
		for (Long ap : ap2exp.keySet()) {
			ap2pair2score.put(ap, new HashMap<String,Double>());
			for (int i = 0; i < hashes.size(); i++) {
				Double exp = Math.exp(b0 + (b1*bait2exp.size()*HGSCore[i]) + (b2*CompPASS[i]) + (b3*control[i]));
				Double score = exp/(exp+1);
				
				//Double score = control[i] + Math.sqrt(bait2exp.size()-1) * (HGSCore[i] + CompPASS[i]);
				ap2pair2score.get(ap).put(hashes.get(i),score);
			}		
		}
		for (Long ap : ap2pair2score.keySet()) {
			for (int i = 0; i < hashes.size(); i++) {
				String hash = hashes.get(i);
				if (ap2pair2score.get(ap).containsKey(hash)) {
					double val = ap2pair2score.get(ap).get(hash);
					scores[i] = Math.max(val, scores[i]);
				}
			}
		}

//		normalizeData();
		return scores;
	}
	
	private void formatData() {
		for (Long ap : ap2exp.keySet()){
			for (Long exp : ap2exp.get(ap)) {
				Long bait = exp2bait.get(exp);
				if (!bait2exp.containsKey(bait)) bait2exp.put(bait, new HashSet<Long>());
				bait2exp.get(bait).add(exp);
			}
		}
	}*/
}
