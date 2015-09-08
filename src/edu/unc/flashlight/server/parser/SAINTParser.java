package edu.unc.flashlight.server.parser;

import java.util.Collection;

import edu.unc.flashlight.server.parser.SpotliteParser.Columns;
import edu.unc.flashlight.server.rf.GenePairScore;
import edu.unc.flashlight.shared.GenericCommand;
import edu.unc.flashlight.shared.exception.upload.ColumnCountException;
import edu.unc.flashlight.shared.exception.upload.InvalidFieldException;
import edu.unc.flashlight.shared.model.upload.UploadError;
import edu.unc.flashlight.shared.model.upload.UploadResult;
import edu.unc.flashlight.shared.model.upload.UploadRow;
import edu.unc.flashlight.shared.model.upload.UploadRowSAINT;
import edu.unc.flashlight.shared.util.Constants;
import edu.unc.flashlight.shared.validation.DatabaseObjectConstraints;

public class SAINTParser extends Parser {
	private GenePairScore[] scores;
	private static String WITHIN_COLUMN_DELIMITER = "\\|";
	private boolean isControl;
	private static enum Columns {
		BAIT ("Bait"), PREY ("Prey"), PREYGENE ("PreyGene"), IP ("IP"), SPEC ("Spec"), SPECSUM ("SpecSum"), NUMREP ("NumRep"), 
		PROB ("Prob"), IPROB ("iProb"), CTRLCOUNTS ("ctrlCounts"), AVGP ("AvgP"), MAXP ("MaxP");
		
		private final String label;
		
		Columns(String s) {
			label = s;
		}
		public String getLabel() {
			return label;
		}
	};
	
	public SAINTParser(String fileOnServer, DatabaseObjectConstraints doc, GenericCommand<Double> updateProgress) {
		super(fileOnServer,doc,updateProgress);
	}
	
	public UploadResult parse() {
		String header = "";
		for (int i = 0; i < Columns.values().length-1; i++) {
			header += Columns.values()[i].getLabel() + "\t";
		}
		header += Columns.values()[Columns.values().length-1].getLabel();
		return parse(header);
	}
	
	public void parseRow(int row, String rowData) {
		boolean validRow = true;
		boolean startValidRow = true;
		String[] splitRow = rowData.split(Constants.SPOTLITE_DELIMITER);
		if (splitRow.length == Columns.values().length) {
			isControl = true;
		} else if (splitRow.length == Columns.values().length - 1) {
			isControl = false;
		} else {
			uploadResult.addError(new UploadError(row, 0, "", new ColumnCountException(Columns.values().length, splitRow.length)));
			return;
		}
		String[] exps = splitRow[Columns.IP.ordinal()].trim().split(WITHIN_COLUMN_DELIMITER);
		String[] scExps = splitRow[Columns.SPEC.ordinal()].trim().split(WITHIN_COLUMN_DELIMITER);
		String[] scControlExps = isControl ? splitRow[Columns.CTRLCOUNTS.ordinal()].trim().split(WITHIN_COLUMN_DELIMITER) : null;
		String baitID = splitRow[Columns.BAIT.ordinal()].trim();
		String preyID = splitRow[Columns.PREY.ordinal()].trim();
		String prob = splitRow[Columns.PROB.ordinal()].trim();
		String type;
		
		if (baitID == "") {
			validRow = addError(new UploadError(row, Columns.BAIT.ordinal(), baitID, 
				new InvalidFieldException(Columns.BAIT.getLabel())));
		}
		if (preyID == "") {
			validRow = addError(new UploadError(row, Columns.PREY.ordinal(), preyID, 
				new InvalidFieldException(Columns.PREY.getLabel())));
		}
		startValidRow = validRow;
		
		for (int i = 0; i < exps.length; i++) {
			validRow = startValidRow;
			type = "T";
			String expName = exps[i];
			int sc = parseSpectralCount(scExps[i], row, Columns.SPEC.ordinal(), validRow);
			validRow = (validRow && sc > 0);
			if (expName == "") { 
				validRow = addError(new UploadError(row, Columns.IP.ordinal(), expName, 
					new InvalidFieldException(Columns.IP.getLabel())));
			}
			ExperimentInfo tmp = exp2info.get(expName);
			
			checkExperimentConsistency(tmp, type, row, Columns.IP.ordinal(), Columns.IP.getLabel(), 
					Columns.BAIT.ordinal(), Columns.BAIT.getLabel(), expName, baitID, validRow);
			
			if (validRow) {
				UploadRowSAINT uploadRow = new UploadRowSAINT();
				uploadRow.prob = Double.parseDouble(prob);
				uploadRow.baitID = baitID;
				addExperiment(expName, baitID, preyID, type, row, sc, uploadRow);
			}
		}
		
		if (isControl) {
			for (int i = 0; i < scControlExps.length; i++) {
				validRow = startValidRow;
				baitID = "";
				type = "C";
				int sc = parseSpectralCount(scControlExps[i], row, Columns.CTRLCOUNTS.ordinal(), validRow);
				validRow = (validRow && sc > 0);
				String controlExpName = "Control_"+i;
				ExperimentInfo tmp = exp2info.get(controlExpName);
				
				checkExperimentConsistency(tmp, type, row, Columns.IP.ordinal(), Columns.IP.getLabel(), 
						Columns.BAIT.ordinal(), Columns.BAIT.getLabel(), controlExpName, baitID, validRow);
				
				if (validRow) {
					addExperiment(controlExpName, baitID, preyID, type, row, sc, new UploadRow());
				}
			}
		}
	}
	
	public UploadResult addErrorsForDuplicateExperiments(Collection<String> duplicates) {
		return addErrorsForDuplicateExperiments(duplicates, Columns.IP.ordinal());
	}
}
