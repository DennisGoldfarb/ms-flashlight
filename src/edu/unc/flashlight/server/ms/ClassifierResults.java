package edu.unc.flashlight.server.ms;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;

import edu.unc.flashlight.server.rf.GenePairScore;

public class ClassifierResults {
	public GenePairScore[] scores;
	public Map<Object,List<Double>> permutationScores;
	
	public ClassifierResults() {
	}
	
	public ClassifierResults(GenePairScore[] scores) {
		this.scores = scores;
	}
	
	public ClassifierResults(GenePairScore[] scores, ConcurrentMap<Object,List<Double>> permutationScores) {
		this.scores = scores;
		this.permutationScores = permutationScores;
	}
}
