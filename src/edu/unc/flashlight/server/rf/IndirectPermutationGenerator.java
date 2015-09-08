package edu.unc.flashlight.server.rf;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import edu.unc.flashlight.shared.util.Conversion;

public class IndirectPermutationGenerator {
	private List<List<Double>> featureIndex2binProbabilities;
	private List<List<Double>> featureIndex2binMinimums;
	private List<List<Double>> featureIndex2binMaximums;
	private int[] indices = {0,1,2,3,4,5,7,8,9,11,15};
	private int numBins;
	
	private Random randomGenerator = new Random();
	
	public IndirectPermutationGenerator(List<List<Double>> featureIndex2binProbabilities, List<Double> featureIndex2minimums, 
			List<Double> featureIndex2maximums) {
		this.featureIndex2binProbabilities = featureIndex2binProbabilities;
		
		numBins = featureIndex2binProbabilities.get(0).size();
		featureIndex2binMinimums = new ArrayList<List<Double>>();
		featureIndex2binMaximums = new ArrayList<List<Double>>();
		
		for (int i = 0; i < featureIndex2minimums.size(); i++) {
			featureIndex2binMinimums.add(new ArrayList<Double>());
			featureIndex2binMaximums.add(new ArrayList<Double>());
			double step_size = (featureIndex2maximums.get(i) - featureIndex2minimums.get(i)) / numBins;
			for (int j = 0; j < numBins; j++) {
				double binMin = featureIndex2minimums.get(i) + (step_size * j);
				double binMax = featureIndex2minimums.get(i) + (step_size * (j+1));
				featureIndex2binMinimums.get(i).add(binMin);
				featureIndex2binMaximums.get(i).add(binMax);
			}
		}
	}
	
	public Double[] sampleFeatures() {
		Double[] sampledFeatures = new Double[indices.length];
		for (int i = 0; i < indices.length; i++) {
			int index = indices[i];
			double randomNum = randomGenerator.nextDouble();
			int randomBin = Math.min(1000,45+Conversion.binaryInsertionSearch(featureIndex2binProbabilities.get(index), randomNum));
			double range = featureIndex2binMaximums.get(index).get(randomBin) - featureIndex2binMinimums.get(index).get(randomBin);
			double randomValue = (randomGenerator.nextDouble()*range) + featureIndex2binMinimums.get(index).get(randomBin);	
			sampledFeatures[i] = randomValue;
		}
		return sampledFeatures;
	}
}
