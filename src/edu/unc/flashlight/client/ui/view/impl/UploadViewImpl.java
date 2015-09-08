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
package edu.unc.flashlight.client.ui.view.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

import edu.unc.flashlight.client.ui.validation.FlashlightValidationMessages;
import edu.unc.flashlight.client.ui.view.UploadView;
import edu.unc.flashlight.client.ui.widget.ProgressBar;
import edu.unc.flashlight.client.ui.widget.ProgressBar.TextFormatter;
import edu.unc.flashlight.client.ui.widget.SingleUploadWidget;
import edu.unc.flashlight.client.ui.widget.UploadErrorTable;
import edu.unc.flashlight.client.ui.widget.UserTable;
import edu.unc.flashlight.client.ui.widget.popup.ValidationPopupDescription;
import edu.unc.flashlight.shared.model.SAINT.SaintParameters;
import edu.unc.flashlight.shared.model.upload.UploadResultCount;
import edu.unc.flashlight.shared.util.Constants;
import edu.unc.flashlight.shared.util.Constants.MS_ALGORITHMS;
import eu.maydu.gwt.validation.client.DefaultValidationProcessor;
import eu.maydu.gwt.validation.client.ValidationProcessor;
import eu.maydu.gwt.validation.client.actions.StyleAction;
import eu.maydu.gwt.validation.client.validators.numeric.DoubleValidator;
import eu.maydu.gwt.validation.client.validators.numeric.IntegerValidator;


public class UploadViewImpl extends Composite implements UploadView {
	
	private static UploadViewImplUiBinder uiBinder = GWT.create(UploadViewImplUiBinder.class);
	
	interface UploadViewImplUiBinder extends UiBinder<Widget, UploadViewImpl> {}
	
	private Presenter presenter;
	private List<Element> rows;
	private String NotMappedFilename;
	private ValidationProcessor validator;
	private ValidationPopupDescription popupDescription;
	
	//@UiField UserTable userTable;
	@UiField RadioButton HGSCore;
	@UiField RadioButton CompPASS;
	@UiField RadioButton SAINT;
	@UiField CheckBox useRandomForest;
	@UiField SingleUploadWidget uploadField;
	@UiField ProgressBar progressParse;
	@UiField ProgressBar progressMap;
	@UiField ProgressBar progressMSScore;
	@UiField ProgressBar progressClassifier;
	@UiField ProgressBar progressUpdate;
	@UiField ProgressBar progressUpload;
	@UiField HTMLPanel progressPanel;
	@UiField HTMLPanel errorPanel;
	@UiField HTMLPanel resultPanel;
	@UiField HTMLPanel uploadFieldPanel;
	@UiField UploadErrorTable errorTable;
	
	@UiField Label mappedBaits;
	@UiField Label notMappedBaits;
	@UiField Label mappedPreys;
	@UiField Label notMappedPreys;
	@UiField Label mappedExperiments;
	@UiField Label notMappedExperiments;
	@UiField Label mappedInteractions;
	@UiField Label notMappedInteractions;
	@UiField Label mappedUniqueInteractions;
	@UiField Label notMappedUniqueInteractions;
	@UiField Label mappedControls;
	@UiField HTMLPanel SAINTexplanationPanel;
	
	// SAINT parameters
	@UiField TextBox SAINTvirtualControls;
	@UiField TextBox SAINTnumReplicates;
	
	@UiField Button viewDataButton;
	@UiField Button downloadNotMappedButton;
	
	public UploadViewImpl() {
		initWidget(uiBinder.createAndBindUi(this));
		TextFormatter textFormatter = new TextFormatter() {
			protected String getText(ProgressBar bar, double curProgress) {
				return bar.getTitle() + " " + (int) (100 * bar.getPercent()) + "%";
			}
		};
		SAINTvirtualControls.setText(String.valueOf(Constants.SAINT_VIRTUAL_CONTROLS));
		SAINTnumReplicates.setText(String.valueOf(Constants.SAINT_NUM_REPLICATES));
	
		progressParse.setTextFormatter(textFormatter);
		progressMap.setTextFormatter(textFormatter);
		progressMSScore.setTextFormatter(textFormatter);
		progressClassifier.setTextFormatter(textFormatter);
		progressUpdate.setTextFormatter(textFormatter);
		progressUpload.setTextFormatter(textFormatter);
		rows = new ArrayList<Element>();
		
		FlashlightValidationMessages validationMessages = new FlashlightValidationMessages();
		popupDescription = new ValidationPopupDescription(validationMessages);
		
		validator = new DefaultValidationProcessor(validationMessages);
		validator.addValidators("SAINTvirtualControls", new IntegerValidator(SAINTvirtualControls,Constants.SAINT_VIRTUAL_CONTROLS_MIN,Constants.SAINT_VIRTUAL_CONTROLS_MAX)
			.addActionForFailure(new StyleAction("validationFailedBorder")));
		popupDescription.addDescription("valid_saintVirtualControls", SAINTvirtualControls);
		
		validator.addValidators("SAINTnumReplicates", new IntegerValidator(SAINTnumReplicates,Constants.SAINT_NUM_REPLICATES_MIN,Constants.SAINT_NUM_REPLICATES_MAX)
			.addActionForFailure(new StyleAction("validationFailedBorder")));
		popupDescription.addDescription("valid_saintNumReplicates", SAINTnumReplicates);
	}
	
	
	public Widget asWidget() {
		return this;
	}
	
	public void setPresenter(Presenter presenter) {
		this.presenter = presenter;
		presenter.bind();
	}
	
	public ProgressBar getProgressParse() {
		return progressParse;
	}

	public ProgressBar getProgressMap() {
		return progressMap;
	}

	public ProgressBar getProgressMSScore() {
		return progressMSScore;
	}

	public ProgressBar getProgressClassifier() {
		return progressClassifier;
	}

	public ProgressBar getProgressUpdate() {
		return progressUpdate;
	}
	
	public ProgressBar getProgressUpload() {
		return progressUpload;
	}
	
	public SingleUploadWidget getUploadWidget() {
		return uploadField;
	}
	
	public HTMLPanel getProgressPanel() {
		return progressPanel;
	}
	
	public HTMLPanel getErrorPanel() {
		return errorPanel;
	}
	
	public HTMLPanel getResultPanel() {
		return resultPanel;
	}
	
	public HTMLPanel getUploadFieldPanel() {
		return uploadFieldPanel;
	}
	
	public UploadErrorTable getErrorTable() {
		return errorTable;
	}
	
	public void UpdateCountWidgets(UploadResultCount resultCount, String NotMappedFilename) {
		mappedBaits.setText(String.valueOf(resultCount.getNumBaits()));
		notMappedBaits.setText(String.valueOf(resultCount.getNumBaitsNotMapped()));
		mappedPreys.setText(String.valueOf(resultCount.getNumPreys()));
		notMappedPreys.setText(String.valueOf(resultCount.getNumPreysNotMapped()));
		mappedExperiments.setText(String.valueOf(resultCount.getNumExperiments()));
		notMappedExperiments.setText(String.valueOf(resultCount.getNumExperimentsNotMapped()));
		mappedInteractions.setText(String.valueOf(resultCount.getNumInteractions()));
		notMappedInteractions.setText(String.valueOf(resultCount.getNumInteractionsNotMapped()));
		mappedUniqueInteractions.setText(String.valueOf(resultCount.getNumUniqueInteractions()));
		notMappedUniqueInteractions.setText(String.valueOf(resultCount.getNumUniqueInteractionsNotMapped()));
		mappedControls.setText(String.valueOf(resultCount.getNumControls()));
		this.NotMappedFilename = NotMappedFilename;
	}
	
	@UiHandler("viewDataButton")
	void onViewDataButtonClicked(ClickEvent e) {
		presenter.onViewDataButtonClicked();
	}
	
	@UiHandler("downloadNotMappedButton")
	void onDownloadNotMappedButtonClicked(ClickEvent e) {
		Window.open(GWT.getModuleBaseURL() + Constants.DOWNLOAD_SERVLET +"?filename=" + NotMappedFilename, "_blank", ""); 
	}
	
	@UiHandler(value={"SAINT","HGSCore","CompPASS"})
	void onSAINTSelectionChanged(ClickEvent e) {
		SAINTexplanationPanel.setVisible(SAINT.getValue());
	}
	
	@UiHandler("uploadField")
	void onUploadButtonClicked(ClickEvent e) {
		if (getMSAlgorithm() == MS_ALGORITHMS.SAINT && !validator.validate()) {
			
		}
		else {
			uploadField.submit();
		}
	}

	public boolean getUseRandomForest() {
		return useRandomForest.getValue();
	}
	
	public MS_ALGORITHMS getMSAlgorithm() {
		if (HGSCore.getValue()) {
			return MS_ALGORITHMS.HGSCore;
		} else if (CompPASS.getValue()) {
			return MS_ALGORITHMS.CompPASS;
		} else if (SAINT.getValue()) {
			return MS_ALGORITHMS.SAINT;
		}
		return MS_ALGORITHMS.HGSCore;
	}
	
	/*public Set<Long> getSelectedIds() {
		return userTable.getSelectedIds();
	}*/
	
	public SaintParameters getSaintParameters() {
		SaintParameters saintParams = new SaintParameters();
		saintParams.setVirtualControls(Integer.parseInt(SAINTvirtualControls.getValue()));
		saintParams.setNumReplicates(Integer.parseInt(SAINTnumReplicates.getValue()));
		return saintParams;
	}
}
