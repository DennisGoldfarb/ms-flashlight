package edu.unc.flashlight.server.parser;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.unc.flashlight.server.util.FileUtil;
import edu.unc.flashlight.shared.GenericCommand;
import edu.unc.flashlight.shared.exception.upload.ExpectedPositiveIntegerException;
import edu.unc.flashlight.shared.exception.upload.ExperimentAlreadyExistsException;
import edu.unc.flashlight.shared.exception.upload.InconsistentFieldValueException;
import edu.unc.flashlight.shared.exception.upload.UploadIOException;
import edu.unc.flashlight.shared.model.Experiment;
import edu.unc.flashlight.shared.model.Gene;
import edu.unc.flashlight.shared.model.upload.UploadError;
import edu.unc.flashlight.shared.model.upload.UploadResult;
import edu.unc.flashlight.shared.model.upload.UploadRow;
import edu.unc.flashlight.shared.util.Constants;
import edu.unc.flashlight.shared.util.Conversion;
import edu.unc.flashlight.shared.validation.DatabaseObjectConstraints;

public abstract class Parser {
	
	protected Map<String,ExperimentInfo> exp2info;
	protected Map<String,Experiment> name2experiment;
	protected List<UploadRow> uploadRows;	
	protected Set<String> ids;
	
	protected DatabaseObjectConstraints doc;
	protected UploadResult uploadResult;
	protected BufferedReader br;
	
	protected GenericCommand<Double> updateProgress;
	protected int totalRows = 0;
	
	public Parser(String fileOnServer, DatabaseObjectConstraints doc, GenericCommand<Double> updateProgress) {
		uploadRows = new ArrayList<UploadRow>();
		exp2info = new HashMap<String, ExperimentInfo>();
		name2experiment = new HashMap<String, Experiment>();
		ids = new HashSet<String>();
		uploadResult = new UploadResult();
		this.updateProgress = updateProgress;
		this.doc = doc;
		try {
			totalRows = FileUtil.count(fileOnServer);
			br = new BufferedReader(new InputStreamReader(new FileInputStream(fileOnServer)));	
		} catch (IOException e) {
			uploadResult.addError(new UploadError(0,0,"",new UploadIOException(e)));
		}
	}
	
	public abstract UploadResult parse();
	
	public UploadResult parse(String header) {
		int row = 1;
		try {
			String line = br.readLine();
			if (header.equalsIgnoreCase(line.trim())) {
				line = br.readLine();
			}
			
			while (line != null) {
					parseRow(row, line);
					row++;
					line = br.readLine();
					if (row % Constants.PARSER_PROGRESS_UPDATE_STEP == 0) updateProgress.execute(Double.valueOf(row)/totalRows);
			}
			updateProgress.execute(1D);
		} catch (IOException e) {
			if (uploadResult.getErrors().size() < 100) {
				uploadResult.addError(new UploadError(row, 0, "", new UploadIOException(e)));
			}
		}

		return uploadResult;
	}
	
	protected abstract void parseRow(int row, String rowData);
	
	protected boolean addError(UploadError uploadError) {
		uploadResult.addError(uploadError);
		return false;
	}
	
	protected int parseSpectralCount(String scString, int row, int col, Boolean validRow) {
		int sc = -1;
		try {
			sc = Integer.parseInt(scString);
			if (sc < 0) {
				validRow = addError(new UploadError(row, col, scString,
					new ExpectedPositiveIntegerException()));
			}
		} catch (Exception e) {
			validRow = addError(new UploadError(row, col, scString, 
					new ExpectedPositiveIntegerException()));
		}
		return sc;
	}
	
	protected void addExperiment(String expName, String baitID, String preyID, String type, 
			int row, int sc, UploadRow newResult) {
		exp2info.put(expName, new ExperimentInfo(type,baitID,row));
		ids.add(baitID);
		ids.add(preyID);
		Experiment newExp = name2experiment.get(expName);
		if (newExp == null) {
			newExp = new Experiment();
			newExp.setExperimentRole(doc.expRoleConstraint.valueInCollectionByString("Private"));
			newExp.setExperimentType(doc.expTypeConstraint.valueInCollectionByString(type));
			newExp.setName(expName);
			Gene baitGene = new Gene();
			baitGene.setOfficialSymbol(baitID);
			newExp.setBait(baitGene);
			newExp.setBaitUploadId(baitID);
			name2experiment.put(expName, newExp);
		}
		newResult.preyID = preyID;
		newResult.sc = sc;
		newResult.experimentID = expName;
		newResult.baitID = baitID;
		uploadRows.add(newResult);
	}
	
	protected void checkExperimentConsistency(ExperimentInfo tmp, String type, int row, int colExp, String colExpLabel, 
			int colBait, String colBaitLabel, String expName, String baitID, Boolean validRow) {
		if (tmp != null) {
			if (!type.equals(tmp.type)) {
				validRow = addError(new UploadError(row, colExp, type, 
						new InconsistentFieldValueException(colExpLabel, tmp.row, tmp.type, expName)));
			}
			if (!baitID.equals(tmp.bait)) {
				validRow = addError(new UploadError(row, colBait, baitID, 
						new InconsistentFieldValueException(colBaitLabel, tmp.row, tmp.bait, expName)));
			}
		}
	}
	
	public abstract UploadResult addErrorsForDuplicateExperiments(Collection<String> duplicates);
	
	public UploadResult addErrorsForDuplicateExperiments(Collection<String> duplicates, int col) {
		Map<Integer, String> row2exp= new HashMap<Integer, String>();
		List<Integer> sortedRows = new ArrayList<Integer>();
		for (String dup : duplicates) {
			ExperimentInfo expInfo = exp2info.get(dup);
			row2exp.put(expInfo.row, dup);
			sortedRows.add(expInfo.row);
		}
		Collections.sort(sortedRows);
		for (Integer row : sortedRows) {			
			addError(new UploadError(row, col, row2exp.get(row),
					new ExperimentAlreadyExistsException()));
		}
		return uploadResult;
	}
	
	protected class ExperimentInfo {
		public int row;
		public String type;
		public String bait;
		
		public ExperimentInfo(String type, String bait, int row) {
			this.type = type;
			this.bait = bait;
			this.row = row;
		}
	}	
	
	public UploadResult getUploadResult() {
		return uploadResult;
	}
	
	public Set<String> getIds() {
		return ids;
	}
	
	public Map<String,ExperimentInfo> getExp2Info() {
		return exp2info;
	}
	
	public Map<String,Experiment> getName2Experiment() {
		return name2experiment;
	}
	
	public List<UploadRow> getUploadRows() {
		return uploadRows;
	}
	
	
}
