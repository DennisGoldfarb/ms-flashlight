package edu.unc.flashlight.server.rf;

public class LogisticRegressionModel {
	private double[] weights;
	private double intercept;
	
	public LogisticRegressionModel(double[] weights, double intercept) {
		this.weights = weights;
		this.intercept = intercept;
	}
	
	public double dotProduct(Double[] instance) {
		double prod = intercept;
		for (int i = 0; i < instance.length; i++) {
			prod += weights[i] * instance[i];
		}
		return prod;
	}
	
	public double calculateLogisticProbability(Double[] instance){
		double prod = dotProduct(instance);
		return 1/(1+Math.exp(-prod));
	}
}
