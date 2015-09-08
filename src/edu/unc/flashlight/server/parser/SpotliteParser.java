/*******************************************************************************
 * Copyright 2012 The University of North Carolina at Chapel Hill.
 *  All Rights Reserved.
 * 
 *  Permission to use, copy, modify OR distribute this software and its
 *  documentation for educational, research and non-profit purposes, without
 *  fee, and without a written agreement is hereby granted, provided that the
 *  above copyright notice and the following three paragraphs appear in all
 *  copies.
 * 
 *  IN NO EVENT SHALL THE UNIVERSITY OF NORTH CAROLINA AT CHAPEL HILL BE
 *  LIABLE TO ANY PARTY FOR DIRECT, INDIRECT, SPECIAL, INCIDENTAL, OR
 *  CONSEQUENTIAL DAMAGES, INCLUDING LOST PROFITS, ARISING OUT OF THE
 *  USE OF THIS SOFTWARE AND ITS DOCUMENTATION, EVEN IF THE UNIVERSITY
 *  OF NORTH CAROLINA HAVE BEEN ADVISED OF THE POSSIBILITY OF SUCH
 *  DAMAGES.
 * 
 *  THE UNIVERSITY OF NORTH CAROLINA SPECIFICALLY DISCLAIM ANY
 *  WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
 *  MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE.  THE SOFTWARE
 *  PROVIDED HEREUNDER IS ON AN "AS IS" BASIS, AND THE UNIVERSITY OF
 *  NORTH CAROLINA HAS NO OBLIGATIONS TO PROVIDE MAINTENANCE, SUPPORT,
 *  UPDATES, ENHANCEMENTS, OR MODIFICATIONS.
 * 
 *  The authors may be contacted via:
 * 
 *  US Mail:           Dennis Goldfarb
 *                     Wei Wang
 * 
 *                     Department of Computer Science
 *                       Sitterson Hall, CB #3175
 *                       University of N. Carolina
 *                       Chapel Hill, NC 27599-3175
 * 
 *                     Ben Major
 * 
 *                     Department of Cell Biology and Physiology 
 *                       Lineberger Comprehensive Cancer Center
 *                       University of N. Carolina
 *                       Chapel Hill, NC 27599-3175
 * 
 *  Email:             dennisg@cs.unc.edu
 *                     weiwang@cs.unc.edu
 *                     ben_major@med.unc.edu
 * 
 *  Web:               www.unc.edu/~dennisg/
 ******************************************************************************/
package edu.unc.flashlight.server.parser;

import java.util.Collection;

import edu.unc.flashlight.shared.GenericCommand;
import edu.unc.flashlight.shared.exception.upload.ColumnCountException;
import edu.unc.flashlight.shared.exception.upload.InvalidFieldException;
import edu.unc.flashlight.shared.model.upload.UploadError;
import edu.unc.flashlight.shared.model.upload.UploadResult;
import edu.unc.flashlight.shared.model.upload.UploadRow;
import edu.unc.flashlight.shared.util.Constants;
import edu.unc.flashlight.shared.validation.DatabaseObjectConstraints;

public class SpotliteParser extends Parser{
	public static enum Columns {
		EXP ("Experiment ID"), 
		TYPE ("Experiment Type"),  
		BAIT ("Bait"), 
		PREY ("Prey"), 
		SC ("Spectral Count");
		
		private final String label;
		
		Columns(String s) {
			label = s;
		}
		public String getLabel() {
			return label;
		}
	};
	
	public SpotliteParser(String fileOnServer, DatabaseObjectConstraints doc, GenericCommand<Double> updateProgress) {
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
		String[] splitRow = rowData.split(Constants.SPOTLITE_DELIMITER);
		if (splitRow.length != Columns.values().length) {
			uploadResult.addError(new UploadError(row, 0, "",new ColumnCountException(Columns.values().length, splitRow.length)));
			return;
		} 
		String exp = splitRow[Columns.EXP.ordinal()];
		String type = splitRow[Columns.TYPE.ordinal()].trim();
		String baitID = splitRow[Columns.BAIT.ordinal()].trim();
		String preyID = splitRow[Columns.PREY.ordinal()].trim();
		String scString = splitRow[Columns.SC.ordinal()].trim();
		int sc = parseSpectralCount(scString, row, Columns.SC.ordinal(), validRow);
		
		if (exp == "") { 
			validRow = addError(new UploadError(row, Columns.EXP.ordinal(), exp, 
				new InvalidFieldException(Columns.EXP.getLabel())));
		}
		if (baitID == "") {
			validRow = addError(new UploadError(row, Columns.BAIT.ordinal(), baitID, 
				new InvalidFieldException(Columns.BAIT.getLabel())));
		}
		if (preyID == "") {
			validRow = addError(new UploadError(row, Columns.PREY.ordinal(), preyID, 
				new InvalidFieldException(Columns.PREY.getLabel())));
		}
		if (doc.expTypeConstraint.valueInCollectionByString(type) == null) {
			validRow = addError(new UploadError(row, Columns.TYPE.ordinal(), type, 
					new InvalidFieldException(Columns.TYPE.getLabel(), doc.expTypeConstraint.getOptionNames())));
		}
		
		ExperimentInfo tmp = exp2info.get(exp);
		
		checkExperimentConsistency(tmp, type, row, Columns.TYPE.ordinal(), Columns.TYPE.getLabel(), 
				Columns.BAIT.ordinal(), Columns.BAIT.getLabel(), exp, baitID, validRow);
		
		if (validRow && sc != 0) {
			
			addExperiment(exp, baitID, preyID, type, row, sc, new UploadRow());
		}
	}
	
	public UploadResult addErrorsForDuplicateExperiments(Collection<String> duplicates) {
		return addErrorsForDuplicateExperiments(duplicates, Columns.EXP.ordinal());
	}
	
}
