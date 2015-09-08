package edu.unc.flashlight.server.ms;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import edu.unc.flashlight.server.rf.GenePairScore;
import edu.unc.flashlight.server.util.FileUtil;
import edu.unc.flashlight.shared.GenericCommand;
import edu.unc.flashlight.shared.model.SAINT.SaintParameters;
import edu.unc.flashlight.shared.util.Constants;
import edu.unc.flashlight.shared.util.DeepCopy;

public class SAINT extends MassSpecScorer {
	private String folderName;
	private SaintParameters params;
	

	public SAINT(APMSControlDataset data, String sid, SaintParameters saintParameters) {
		super(data);
		this.folderName = "SAINT_"+sid;
		this.params = saintParameters;
	}
	
	private GenePairScore[] calculateRawScores(APMSDataset data, GenePairScore[] result, int itr) {
		String itrFolderName = folderName + "_" + String.valueOf(itr);
		new File(FileUtil.createPath(itrFolderName)).mkdirs();
		writeData(data, itrFolderName);
		execSAINT(itrFolderName);
		parseResults(result, itrFolderName);
		clean(itrFolderName);
		return result;
	}

	public ClassifierResults calculateScores(GenericCommand<Double> updateProgress) {
		updateProgress.execute(.01);
		ClassifierResults results = new ClassifierResults();
		results.scores = calculateRawScores(data, data.getSpokeInteractions(),0);
		updateProgress.execute(.02);
		results.permutationScores = new HashMap<Object,List<Double>>();
		
		/*int numItr = 10;
			
		for (int k = 0; k < data.getBaits().length; k++) {
			try {
				String bait = data.getBaits()[k];
				results.permutationScores.put(bait, new GenePairScore[numItr][]);		
				// Generate new APMSControlDataset with only the controls and this bait
				APMSControlDataset baitControl = createBaitSubset((APMSControlDataset) data,bait);
				// Generate new APMSDataset with remaining experiments
				APMSDataset remaining = createBaitExcludedSubset(data,bait);
				// Setup permutation generators
				MSControlPermutationGenerator baitControlPermGen = new MSControlPermutationGenerator((APMSControlDataset) baitControl);
				MSPermutationGenerator2 remainingPermGen = new MSPermutationGenerator2(remaining);
				
				for (int i = 0; i < numItr; i++) {		
					// Permute controls and specific bait
					APMSControlDataset baitControlPermData = baitControlPermGen.permute(data.getNumReplicates(bait));
					// Permute remaining experiments
//					APMSDataset remainingPermData = remainingPermGen.permute(data.getNumReplicates(bait));
					// Merge data sets
//					APMSControlDataset permData = APMSControlDataset.mergeDatasets(baitControlPermData, remainingPermData);
					// Score bait-control interactions only
					results.permutationScores.get(bait)[i] = calculateRawScores(baitControlPermData, data.getSpokeInteractionsForBait(bait));
				}
				updateProgress.execute(.02 + ((k+1)*.97/(data.getBaits().length)));
			} catch (Exception e) {
				int x = 1;
				System.err.println("FUCKED UP! " +e.getMessage());
			}
		}
		normalizeIndividualData(results);
		*/
		
		
		/*
		int numItr = 10;
		for (int j = 0; j < data.getReplicateCounts().length; j++) {
			int rep = data.getReplicateCounts()[j];
			results.permutationScores.put(rep, new GenePairScore[numItr*data.getBaits().length][]);
			for (int k = 0; k < data.getBaits().length; k++) {
				try {
					String bait = data.getBaits()[k];
					// Generate new APMSControlDataset with only the controls and this bait
					APMSControlDataset baitControl = createBaitSubset((APMSControlDataset) data,bait);
					// Generate new APMSDataset with remaining experiments
					APMSDataset remaining = createBaitExcludedSubset(data,bait);
					// Setup permutation generators
					MSControlPermutationGenerator baitControlPermGen = new MSControlPermutationGenerator((APMSControlDataset) baitControl);
					MSPermutationGenerator2 remainingPermGen = new MSPermutationGenerator2(remaining);
					
					for (int i = 0; i < numItr; i++) {		
						// Permute controls and specific bait
						APMSControlDataset baitControlPermData = baitControlPermGen.permute(rep);
						// Permute remaining experiments
						APMSDataset remainingPermData = remainingPermGen.permute(rep);
						// Merge data sets
						APMSControlDataset permData = APMSControlDataset.mergeDatasets(baitControlPermData, remainingPermData);
						// Score bait-control interactions only
						results.permutationScores.get(rep)[(k*numItr)+i] = calculateRawScores(permData, baitControlPermData.getSpokeInteractions());
					}
					updateProgress.execute(.02 + (((data.getBaits().length*j)+k+1)*.97/(data.getBaits().length*data.getReplicateCounts().length)));
				} catch (Exception e) {
					int x = 1;
					System.err.println("FUCKED UP! " +e.getMessage());
				}
			}
		}
		normalizeData(results);
		*/
		
		
		/***** FULL PERMUTATIONS PER BAIT *****/
		/*for (int i = 0; i < data.getBaits().length; i++) {	
			try {
				String bait = data.getBaits()[i];
				// Generate new APMSControlDataset with only the controls and this bait	
				MSControlPermutationGenerator2 permGen = new MSControlPermutationGenerator2((APMSControlDataset) data, bait);
				
				results.permutationScores.put(bait, new GenePairScore[permGen.getNumComb()][]);
				// Permute all permutations
				for (int j = 0; j < permGen.getNumComb(); j++) {
					APMSControlDataset permData = permGen.permuteNext(bait);	
					GenePairScore[] tmpScores = calculateRawScores(permData, data.getSpokeInteractionsForBait(bait));
					results.permutationScores.get(bait)[j] = tmpScores;
				}
				updateProgress.execute(.01 + ((i+1)*.89/(data.getBaits().length)));
			} catch (Exception e) {
				int x = 1;
			}
		}
		normalizeIndividualData(results);*/

		/***** RANDOM PERMUTATIONS WITH CONTROLS *****/
		
		MSControlPermutationGenerator permGen = new MSControlPermutationGenerator(data);
		int numDesiredPermutationInteractions = results.scores.length*invFDR*cov;
		int totalSteps = data.getReplicateCounts().length*numDesiredPermutationInteractions;
		// Continue sampling until all replicate counts are filled.
		int numComplete = 0;
		for (int j = 0; j < data.getReplicateCounts().length; j++) {
			int rep = data.getReplicateCounts()[j];
			results.permutationScores.put(rep, Collections.synchronizedList(new ArrayList<Double>((int) (numDesiredPermutationInteractions*1.2))));
		}
		
		while (numComplete < data.getReplicateCounts().length) {
			ExecutorService executor = Executors.newFixedThreadPool(NUM_THREADS);
			for (int k = 0; k < NUM_THREADS; k++) {
				Runnable worker = new ScoringLoop(k,permGen,(int) (numDesiredPermutationInteractions * 1.2),results);
				executor.execute(worker);
			}
			executor.shutdown();
			while (!executor.isTerminated()) {}
			numComplete = 0;
			int currentStep = 0;
			for (int rep : data.getReplicateCounts()) {
				currentStep += Math.min(results.permutationScores.get(rep).size(), numDesiredPermutationInteractions);
				if (results.permutationScores.get(rep).size() >= numDesiredPermutationInteractions) {
					numComplete++;
				}
			} 
			updateProgress.execute(.02 + (currentStep*.97/totalSteps));
		}
		
		/*
		int totalSteps = data.getReplicateCounts().length*results.scores.length*invFDR*cov;
		int currentStep = 0;
		int lastStep = 0;
		
		results.permutationScores = new HashMap<Object,List<GenePairScore>>();
		for (int j = 0; j < data.getReplicateCounts().length; j++) {
			int rep = data.getReplicateCounts()[j];
			//int numDesiredPermutationInteractions = data.getNumberInteractionsForReplicate(rep)*invFDR*cov;
			int numDesiredPermutationInteractions = results.scores.length*invFDR*cov;
			int numPermutationInteractions = 0;
			
			results.permutationScores.put(rep, Collections.synchronizedList(new ArrayList<GenePairScore>((int) (numDesiredPermutationInteractions*1.2))));
			while (numPermutationInteractions < numDesiredPermutationInteractions) {	
				ExecutorService executor = Executors.newFixedThreadPool(NUM_THREADS);
				for (int k = 0; k < NUM_THREADS; k++) {
					Runnable worker = new ScoringLoop(k,permGen,rep,results);
					executor.execute(worker);
				}
				executor.shutdown();
				while (!executor.isTerminated()) {}
				numPermutationInteractions = results.permutationScores.get(rep).size();
				currentStep += Math.min(numPermutationInteractions,numDesiredPermutationInteractions)-currentStep+lastStep;
				updateProgress.execute(.02 + (currentStep*.97/totalSteps));
			}
			lastStep = currentStep;
		}*/
		normalizeData(results);
		thresholdData(results.scores);
		
		updateProgress.execute(1.0);
		return results;
	}
	
	private void thresholdData(GenePairScore[] results) {
		for (GenePairScore gps : results) {
			gps.setMsScore(Math.min(1, gps.getMsScore()));
		}
	}
	
	private class ScoringLoop implements Runnable {
		private int numDesiredPermutationInteractions;
		private int index;
		private MSControlPermutationGenerator permGen;
		private ClassifierResults results;
		public ScoringLoop(int i, MSControlPermutationGenerator permGen, int numDesiredPermutationInteractions, ClassifierResults results) {
			this.permGen = permGen;
			this.results = results;
			index = i;
			this.numDesiredPermutationInteractions = numDesiredPermutationInteractions;
		}
		public void run() {
			APMSControlDataset permData = permGen.permute();
			GenePairScore[] scores = calculateRawScores(permData, permData.getSpokeInteractions(),index);
			for (GenePairScore gps : scores) {
				int rep = permData.getNumReplicates(gps.getBaitNiceName());
				if (results.permutationScores.get(rep).size() < numDesiredPermutationInteractions) {
					results.permutationScores.get(rep).add(gps.getMsScore());
				}
			}
		}
	}
	
	private APMSDataset createBaitExcludedSubset(APMSDataset data, String bait) {
		APMSDataset tmp = new APMSDataset(data);
		for (String curBait : data.bait2exp.keySet()) {
			if (!curBait.equals(bait)) {
				for (Long exp : data.bait2exp.get(curBait)) {
					tmp.exp2bait.put(exp, curBait);
					tmp.exp2prey2sc.put(exp, data.exp2prey2sc.get(exp));
					for (String prey : data.exp2prey2sc.get(exp).keySet()) {
						if (!tmp.prey2length.containsKey(prey)) {
							tmp.prey2length.put(prey, data.prey2length.get(prey));
						}
					}
					if (!tmp.bait2exp.containsKey(curBait)) tmp.bait2exp.put(curBait, new HashSet<Long>());
					tmp.bait2exp.get(curBait).add(exp);
				}
			}
		}
		return tmp;
	}
	
	private APMSControlDataset createBaitSubset(APMSControlDataset data, String bait) {
		APMSControlDataset tmp = new APMSControlDataset();
		for (Long exp : data.bait2exp.get(bait)) {
			tmp.exp2bait.put(exp, bait);
			tmp.exp2prey2sc.put(exp, data.exp2prey2sc.get(exp));
			for (String prey : data.exp2prey2sc.get(exp).keySet()) {
				if (!tmp.prey2length.containsKey(prey)) {
					tmp.prey2length.put(prey, data.prey2length.get(prey));
				}
			}
			if (!tmp.bait2exp.containsKey(bait)) tmp.bait2exp.put(bait, new HashSet<Long>());
			tmp.bait2exp.get(bait).add(exp);
		}
		tmp.c_exp2bait = (Map<Long,String>) DeepCopy.copy(data.c_exp2bait);
		tmp.c_exp2prey2sc = (Map<Long,Map<String,Integer>>) DeepCopy.copy(data.c_exp2prey2sc);
		return tmp;
	}
	
	public void parseResults(GenePairScore[] scores, String folderName) {
		try {
			String output_filename = FileUtil.createPath(folderName+Constants.SEP+Constants.SAINT_RESULTS_SUFFIX);
			FileReader fstream = new FileReader(output_filename);
			BufferedReader in = new BufferedReader(fstream);
			String line;
			in.readLine();
			Map<String,Map<String,Double>> bait2prey2saint = new HashMap<String,Map<String,Double>>();
			while ((line = in.readLine()) != null) {
				String[] lineArray = line.split("\t");
				String bait = lineArray[0];
				String prey = lineArray[1];
				String prob = lineArray[12];
				if (!bait2prey2saint.containsKey(bait)) bait2prey2saint.put(bait, new HashMap<String,Double>());
				
				//double saintScore = new BigDecimal(prob).setScale(5, BigDecimal.ROUND_HALF_UP).doubleValue();
				double saintScore = Double.parseDouble(prob);
				if (saintScore == 1) {
					saintScore += Double.parseDouble(lineArray[13]);
				}
				bait2prey2saint.get(bait).put(prey, saintScore);
			}
			for (GenePairScore gps : scores) {
				String bait = gps.getBaitNiceName();
				String prey = gps.getPreyNiceName();
				if (bait2prey2saint.containsKey(bait) && bait2prey2saint.get(bait).containsKey(prey)) {
					gps.setMsScore(Math.max(bait2prey2saint.get(bait).get(prey),gps.getMsScore()));
				}
			}
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
	}
	
	public void execSAINT(String folderName) {
		try {
			//String prey_filename = FileUtil.createPath(folderName+Constants.SEP+"prey.new");
			//String bait_filename = FileUtil.createPath(folderName+Constants.SEP+"bait.new");
			//String interaction_filename = FileUtil.createPath(folderName+Constants.SEP+"interaction.new");
			String prey_filename = FileUtil.createPath(folderName+Constants.SEP+Constants.SAINT_PREY_FILE_SUFFIX);
			String bait_filename = FileUtil.createPath(folderName+Constants.SEP+Constants.SAINT_BAIT_FILE_SUFFIX);
			String interaction_filename = FileUtil.createPath(folderName+Constants.SEP+Constants.SAINT_INTERACTION_FILE_SUFFIX);
			ProcessBuilder builder;
			
			builder = new ProcessBuilder(Constants.SAINT_PATH + Constants.SEP + Constants.SAINT_NO_CONTROL_EXEC,
					"-L"+String.valueOf(params.getVirtualControls()),"-R"+String.valueOf(params.getNumReplicates()),
					interaction_filename,prey_filename,bait_filename);
			

			builder.directory(new File(FileUtil.createPath(folderName)));
			builder.redirectErrorStream(true);
			Process p = builder.start();
			p.waitFor();
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
	}
	
	public void writePrey(APMSDataset data, String folderName) throws IOException {
		String prey_filename = FileUtil.createPath(folderName+Constants.SEP+Constants.SAINT_PREY_FILE_SUFFIX);
		FileWriter fstream = new FileWriter(prey_filename, false);
		BufferedWriter out = new BufferedWriter(fstream);
		for (String prey : data.getPrey()) {
			if (!data.prey2length.containsKey(prey)) data.prey2length.put(prey, Constants.AVERAGE_PROTEIN_LENGTH);
			out.write(prey.toString() + "\t" + data.prey2length.get(prey).toString() + "\t" + prey.toString() + "\n");
		}
		out.close();
	}
	
	public void writeBait(APMSDataset data, String folderName) throws IOException {
		String bait_filename = FileUtil.createPath(folderName+Constants.SEP+Constants.SAINT_BAIT_FILE_SUFFIX);
		FileWriter fstream = new FileWriter(bait_filename, false);
		BufferedWriter out = new BufferedWriter(fstream);
		for (Long exp : data.exp2bait.keySet()) {
			out.write(exp.toString() + "\t" + data.exp2bait.get(exp).toString() + "\tT\n");
		}
		if (data instanceof APMSControlDataset) {
			for (Long exp : ((APMSControlDataset) data).c_exp2prey2sc.keySet()) {
				out.write(exp.toString() + "\t" + exp.toString() + "\tC\n");
			}
		}
		out.close();
	}
	
	public void writeInteractions(APMSDataset data, String folderName) throws IOException {
		String interaction_filename = FileUtil.createPath(folderName+Constants.SEP+Constants.SAINT_INTERACTION_FILE_SUFFIX);
		FileWriter fstream = new FileWriter(interaction_filename, false);
		BufferedWriter out = new BufferedWriter(fstream);
		for (Long exp : data.exp2bait.keySet()) {
			String bait = data.exp2bait.get(exp).toString();
			for (String prey : data.exp2prey2sc.get(exp).keySet()) {
				out.write(exp.toString() + "\t" + bait + "\t" + prey.toString() + "\t" + data.exp2prey2sc.get(exp).get(prey).toString() + "\n");
			}
		}
		if (data instanceof APMSControlDataset) {
			for (Long exp : ((APMSControlDataset) data).c_exp2bait.keySet()) {
				String bait = ((APMSControlDataset) data).c_exp2bait.get(exp);
				for (String prey : ((APMSControlDataset) data).c_exp2prey2sc.get(exp).keySet()) {
//					if (data.prey2length.containsKey(prey)) {
						out.write(exp.toString() + "\t" + bait + "\t" + prey.toString() + "\t" + ((APMSControlDataset) data).c_exp2prey2sc.get(exp).get(prey).toString() + "\n");
//					}
				}
			}
		}
		out.close();
	}
	
	public void writeData(APMSDataset data,String folderName) {
		try {
			writePrey(data,folderName);
			writeBait(data,folderName);
			writeInteractions(data,folderName);
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
	}
	
	public void clean(String folderName) {
		FileUtil.deleteFolder(FileUtil.createPath(folderName));
	}

}
