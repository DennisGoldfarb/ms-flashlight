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
package edu.unc.flashlight.client.ui.widget;

import com.google.gwt.cell.client.ClickableTextCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.SimplePager;
import com.google.gwt.user.cellview.client.SimplePager.TextLocation;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.view.client.AsyncDataProvider;
import com.google.gwt.view.client.HasData;
import com.google.gwt.view.client.Range;

import edu.unc.flashlight.client.Flashlight;
import edu.unc.flashlight.client.FlashlightConstants;
import edu.unc.flashlight.client.FlashlightMessages;
import edu.unc.flashlight.client.command.ServerOp;
import edu.unc.flashlight.client.ui.widget.popup.CoexpressionPopup;
import edu.unc.flashlight.client.ui.widget.popup.IsKnownPopup;
import edu.unc.flashlight.client.ui.widget.popup.OntologyPopup;
import edu.unc.flashlight.shared.model.GenePair;
import edu.unc.flashlight.shared.model.UserResult;
import edu.unc.flashlight.shared.model.table.PageResults;
import edu.unc.flashlight.shared.model.table.PaginationParameters;

public class ExperimentResultTable extends Composite {
	private static String EMTPY = "";

	CellTable<UserResult> cellTable;
	private SimplePager pager;
	private AsyncDataProvider<UserResult> dataProvider;
	private NumberFormat sciFormat = NumberFormat.getFormat("0.00E0");
	private NumberFormat doubleFormat = NumberFormat.getFormat("#.###");
	private boolean isBrowse;
	private String searchText = "";
	private Label emptyTableWidget;
	private final int numRows = 50;
	private FlashlightConstants constants = FlashlightConstants.INSTANCE;
	private FlashlightMessages messages = FlashlightMessages.INSTANCE;

	public ExperimentResultTable(boolean isBrowse) {
		this.isBrowse = isBrowse;
		VerticalPanel panel = new VerticalPanel();
		initWidget(panel);

		cellTable = new CellTable<UserResult>();
		SimplePager.Resources pagerResources = GWT.create(SimplePager.Resources.class);
		pager = new SimplePager(TextLocation.CENTER, pagerResources, false, 0, true);
		pager.setDisplay(cellTable);
		pager.setPageSize(numRows);

		dataProvider = new AsyncDataProvider<UserResult>() {
			protected void onRangeChanged(HasData<UserResult> display) {
				int start = display.getVisibleRange().getStart();
				int length = display.getVisibleRange().getLength();
				updateTable(start, length, searchText);
			}
		};
		dataProvider.addDataDisplay(cellTable);

		new ServerOp<String>() {
			public void onSuccess(String result) {
				if (result == null || result.equals(""))
					initTableColumns(constants.resultsCol_msScore());
				else
					initTableColumns(result);
			}
			public void onFailure(final Throwable e) {
				initTableColumns(constants.resultsCol_msScore());
			}
			public void begin() {
				Flashlight.userResultService.getClassificationAlgorithm(this);
			}
		}.begin();
		

		panel.setCellHorizontalAlignment(pager, HasHorizontalAlignment.ALIGN_CENTER);

		panel.add(cellTable);
		panel.add(pager);
		emptyTableWidget = new Label(constants.table_noResults());
		emptyTableWidget.setStylePrimaryName("emptyTableLabel");
	}
	
	private void updateEmpty() {
		if (searchText.equals("")) {
			emptyTableWidget.setText(constants.table_noResults());
		} else {
			emptyTableWidget.setText(messages.results_noResultsQuery(searchText));
		}
	}

	public void updateTable(final int start, final int length, final String sym) {
		new ServerOp<PageResults<UserResult>>() {
			public void onSuccess(PageResults<UserResult> result) {
				dataProvider.updateRowData(start, result.getResults());
				dataProvider.updateRowCount(((Long)result.getSize()).intValue(), true);
				if (((Long)result.getSize()).intValue() == 0) {
					updateEmpty();
					cellTable.setEmptyTableWidget(emptyTableWidget);
				}
			}
			public void begin() {
				if (isBrowse) {
					if (sym.equals("")) {
						Flashlight.userResultService.getBrowseData(new PaginationParameters(start,length), this);
					} else {
						Flashlight.userResultService.getBrowseDataSearch(new PaginationParameters(start,length), sym, this);
					}
				} else {
					if (sym.equals("")) {
						Flashlight.userResultService.getData(new PaginationParameters(start,length), this);
					} else {
						Flashlight.userResultService.getDataSearch(new PaginationParameters(start,length), sym, this);
					}
				}
			}
		}.begin();
	}
	
	public void refreshTable() {
		cellTable.setVisibleRangeAndClearData(new Range(0,numRows), true);
	}
	
	public void setSearchText(String sym) {
		searchText = sym;
	}
	

	private void initTableColumns(String classificationAlgorithmName) {
		Column<UserResult, String> datasetColumn = new Column<UserResult, String>(new TextCell()) {
			public String getValue(UserResult object) {
				return String.valueOf(object.getUser().getUsername());
			}
		};
		
		Column<UserResult, String> baitColumn = new Column<UserResult, String>(new ClickableTextCell()) {
			public String getValue(UserResult object) {
				return String.valueOf(object.getBaitDisplayName());
			}
		};
		
		baitColumn.setFieldUpdater(new FieldUpdater<UserResult, String>() {
			public void update(int index, final UserResult object, String value) {
				if (object.getBaitGene() != null)
					com.google.gwt.user.client.Window.open("http://www.ncbi.nlm.nih.gov/sites/entrez?db=gene&amp;cmd=Retrieve&amp;dopt=full_report&amp;list_uids="+object.getBaitGene().getId().toString(), "_blank", "");	
			}
		});
		

		Column<UserResult, String> preyColumn = new Column<UserResult, String>(new ClickableTextCell()) {
			public String getValue(UserResult object) {
				return String.valueOf(object.getPreyDisplayName());
			}
		};
		
		preyColumn.setFieldUpdater(new FieldUpdater<UserResult, String>() {
			public void update(int index, final UserResult object, String value) {
				if (object.getPreyGene() != null)
					com.google.gwt.user.client.Window.open("http://www.ncbi.nlm.nih.gov/sites/entrez?db=gene&amp;cmd=Retrieve&amp;dopt=full_report&amp;list_uids="+object.getPreyGene().getId().toString(), "_blank", "");	
			}
		});


		Column<UserResult, String> classifierScoreColumn = new Column<UserResult, String>(new ClickableTextCell()) {
			public String getValue(UserResult object) {
				return doubleFormat.format(object.getClassifierScore());
			}
		};
		
		Column<UserResult, String> classifierPValueColumn = new Column<UserResult, String>(new ClickableTextCell()) {
			public String getValue(UserResult object) {
				return sciFormat.format(object.getClassifierPValue());
			}
		};

		Column<UserResult, String> MsScoreColumn = new Column<UserResult, String>(new ClickableTextCell()) {
			public String getValue(UserResult object) {
				return doubleFormat.format(object.getMsScore());
			}
		};
		
		Column<UserResult, String> MsPValueColumn = new Column<UserResult, String>(new ClickableTextCell()) {
			public String getValue(UserResult object) {
				return sciFormat.format(object.getMsPValue());
			}
		};

		Column<UserResult, String> BPScoreColumn = new Column<UserResult, String>(new ClickableTextCell()) {
			public String getValue(UserResult object) {
				GenePair gp = object.getGenePair();
				return gp == null || gp.getBpScore() == null ? EMTPY : doubleFormat.format(gp.getBpScore());
			}
		};
		BPScoreColumn.setFieldUpdater(new FieldUpdater<UserResult, String>() {
			public void update(int index, final UserResult object, String value) {
				displayOntologyViewer(object, "biological_process", "Biological Process");			
			}
		});


		Column<UserResult, String> CCScoreColumn = new Column<UserResult, String>(new ClickableTextCell()) {
			public String getValue(UserResult object) {
				GenePair gp = object.getGenePair();
				return gp == null || gp.getCcScore() == null ? EMTPY : doubleFormat.format(gp.getCcScore());
			}
		};
		CCScoreColumn.setFieldUpdater(new FieldUpdater<UserResult, String>() {
			public void update(int index, final UserResult object, String value) {
				displayOntologyViewer(object, "cellular_component", "Cellular Component");			
			}
		});


		Column<UserResult, String> CXPHsaScoreColumn = new Column<UserResult, String>(new ClickableTextCell()) {
			public String getValue(UserResult object) {
				GenePair gp = object.getGenePair();
				return gp == null || gp.getCoxpHumanScore() == null ? EMTPY : doubleFormat.format(gp.getCoxpHumanScore());
			}
		};
		CXPHsaScoreColumn.setFieldUpdater(new FieldUpdater<UserResult, String>() {
			public void update(int index, final UserResult object, String value) {
				generateCoexpressionImage(object, 9606L, "Hsa");
			}
		});


		Column<UserResult, String> CXPMmuScoreColumn = new Column<UserResult, String>(new ClickableTextCell()) {
			public String getValue(UserResult object) {
				GenePair gp = object.getGenePair();
				return gp == null || gp.getCoxpMouseScore() == null ? EMTPY : doubleFormat.format(gp.getCoxpMouseScore());
			}
		};
		CXPMmuScoreColumn.setFieldUpdater(new FieldUpdater<UserResult, String>() {
			public void update(int index, final UserResult object, String value) {
				generateCoexpressionImage(object, 10090L, "Mmu");
			}
		});


		Column<UserResult, String> CXPMccScoreColumn = new Column<UserResult, String>(new ClickableTextCell()) {
			public String getValue(UserResult object) {
				GenePair gp = object.getGenePair();
				return gp == null || gp.getCoxpMonkeyScore() == null ? EMTPY : doubleFormat.format(gp.getCoxpMonkeyScore());
			}
		};
		CXPMccScoreColumn.setFieldUpdater(new FieldUpdater<UserResult, String>() {
			public void update(int index, final UserResult object, String value) {
				generateCoexpressionImage(object, 9544L, "Mcc");
			}
		});


		Column<UserResult, String> CXPDreScoreColumn = new Column<UserResult, String>(new ClickableTextCell()) {
			public String getValue(UserResult object) {
				GenePair gp = object.getGenePair();
				return gp == null || gp.getCoxpFishScore() == null ? EMTPY : doubleFormat.format(gp.getCoxpFishScore());
			}
		};
		CXPDreScoreColumn.setFieldUpdater(new FieldUpdater<UserResult, String>() {
			public void update(int index, final UserResult object, String value) {
				generateCoexpressionImage(object, 7955L, "Dre");
			}
		});
		
		Column<UserResult, String> CXPCelScoreColumn = new Column<UserResult, String>(new ClickableTextCell()) {
			public String getValue(UserResult object) {
				GenePair gp = object.getGenePair();
				return gp == null || gp.getCoxpWormScore() == null ? EMTPY : doubleFormat.format(gp.getCoxpWormScore());
			}
		};
		CXPCelScoreColumn.setFieldUpdater(new FieldUpdater<UserResult, String>() {
			public void update(int index, final UserResult object, String value) {
				generateCoexpressionImage(object, 6293L, "Cel");
			}
		});
		

		Column<UserResult, String> CXPRatScoreColumn = new Column<UserResult, String>(new ClickableTextCell()) {
			public String getValue(UserResult object) {
				GenePair gp = object.getGenePair();
				return gp == null || gp.getCoxpRatScore() == null ? EMTPY : doubleFormat.format(gp.getCoxpRatScore());
			}
		};
		CXPRatScoreColumn.setFieldUpdater(new FieldUpdater<UserResult, String>() {
			public void update(int index, final UserResult object, String value) {
				generateCoexpressionImage(object, 10116L, "Rno");
			}
		});
		

		Column<UserResult, String> CXPGgaScoreColumn = new Column<UserResult, String>(new ClickableTextCell()) {
			public String getValue(UserResult object) {
				GenePair gp = object.getGenePair();
				return gp == null || gp.getCoxpChickenScore() == null ? EMTPY : doubleFormat.format(gp.getCoxpChickenScore());
			}
		};
		CXPGgaScoreColumn.setFieldUpdater(new FieldUpdater<UserResult, String>() {
			public void update(int index, final UserResult object, String value) {
				generateCoexpressionImage(object, 9031L, "Gga");
			}
		});
		

		Column<UserResult, String> DomainScoreColumn = new Column<UserResult, String>(new TextCell()) {
			public String getValue(UserResult object) {
				GenePair gp = object.getGenePair();
				return gp == null || gp.getDomainScore() == null ? EMTPY : doubleFormat.format(gp.getDomainScore());
			}
		};
		

		Column<UserResult, String> HumanIntScoreColumn = new Column<UserResult, String>(new TextCell()) {
			public String getValue(UserResult object) {
				GenePair gp = object.getGenePair();
				return gp == null || gp.getHumanIntScore() == null ? EMTPY : doubleFormat.format(gp.getHumanIntScore());
			}
		};
		

		Column<UserResult, String> HumanPhenScoreColumn = new Column<UserResult, String>(new ClickableTextCell()) {
			public String getValue(UserResult object) {
				GenePair gp = object.getGenePair();
				return gp == null || gp.getHumanPhenScore() == null ? EMTPY : doubleFormat.format(gp.getHumanPhenScore());
			}
		};
		HumanPhenScoreColumn.setFieldUpdater(new FieldUpdater<UserResult, String>() {
			public void update(int index, final UserResult object, String value) {
				displayOntologyViewer(object, "human_phenotype", "Human Phenotype");			
			}
		});
		

		Column<UserResult, String> MousePhenScoreColumn = new Column<UserResult, String>(new ClickableTextCell()) {
			public String getValue(UserResult object) {
				GenePair gp = object.getGenePair();
				return gp == null || gp.getMousePhenScore() == null ? EMTPY : doubleFormat.format(gp.getMousePhenScore());
			}
		};
		MousePhenScoreColumn.setFieldUpdater(new FieldUpdater<UserResult, String>() {
			public void update(int index, final UserResult object, String value) {
				displayOntologyViewer(object, "mouse_phenotype", "Mouse Phenotype");		
			}
		});
		
		Column<UserResult, String> DiseaseScoreColumn = new Column<UserResult, String>(new ClickableTextCell()) {
			public String getValue(UserResult object) {
				GenePair gp = object.getGenePair();
				return gp == null || gp.getDiseaseScore() == null ? EMTPY : doubleFormat.format(gp.getDiseaseScore());
			}
		};
		DiseaseScoreColumn.setFieldUpdater(new FieldUpdater<UserResult, String>() {
			public void update(int index, final UserResult object, String value) {
				displayOntologyViewer(object, "disease_ontology", "Human Disease");			
			}
		});
		

		Column<UserResult, String> IsknownColumn = new Column<UserResult, String>(new ClickableTextCell()) {
			public String getValue(UserResult object) {
				GenePair gp = object.getGenePair();
				return gp == null || gp.getIsKnown() == null ? EMTPY : gp.getIsKnown();
			}
		};
		IsknownColumn.setFieldUpdater(new FieldUpdater<UserResult, String>() {
			public void update(int index, final UserResult object, String value) {
				new IsKnownPopup(object.getBaitGene(),object.getPreyGene()).showPopup();
			}
		});
		
		
		if (isBrowse) {
			cellTable.addColumn(datasetColumn, constants.datasetsCol_dataset());
		}
		cellTable.addColumn(baitColumn, constants.resultsCol_bait());
		cellTable.addColumn(preyColumn, constants.resultsCol_prey());
		cellTable.addColumn(classifierScoreColumn, constants.resultsCol_classifier());
		//cellTable.addColumn(classifierPValueColumn, constants.resultsCol_classifierPValue());
		cellTable.addColumn(MsScoreColumn,classificationAlgorithmName);
		cellTable.addColumn(MsPValueColumn,classificationAlgorithmName+" "+constants.resultsCol_pValue());
		cellTable.addColumn(BPScoreColumn, constants.resultsCol_bp());
		cellTable.addColumn(CCScoreColumn, constants.resultsCol_cc());
		cellTable.addColumn(CXPHsaScoreColumn, constants.resultsCol_cxpHsa());
		cellTable.addColumn(CXPMmuScoreColumn, constants.resultsCol_cxpMmu());
		cellTable.addColumn(CXPCelScoreColumn, constants.resultsCol_cxpCel());	
		cellTable.addColumn(CXPGgaScoreColumn, constants.resultsCol_cxpGga());
		cellTable.addColumn(CXPRatScoreColumn, constants.resultsCol_cxpRno());
		cellTable.addColumn(CXPDreScoreColumn, constants.resultsCol_cxpDre());
		cellTable.addColumn(CXPMccScoreColumn, constants.resultsCol_cxpMcc());
		cellTable.addColumn(DomainScoreColumn, constants.resultsCol_domain());
		cellTable.addColumn(HumanIntScoreColumn, constants.resultsCol_homoInt());
		cellTable.addColumn(HumanPhenScoreColumn, constants.resultsCol_hsaPhen());
		cellTable.addColumn(MousePhenScoreColumn, constants.resultsCol_mmuPhen());
		cellTable.addColumn(DiseaseScoreColumn, constants.resultsCol_disease());
		cellTable.addColumn(IsknownColumn, constants.resultsCol_known());
	}
	
	public void generateCoexpressionImage(final UserResult object, final Long taxID, String abr) {
		final CoexpressionPopup panel = new CoexpressionPopup(object.getBaitGene(), 
				object.getPreyGene(), taxID, abr);
		panel.showPopup();
	}
	
	public void displayOntologyViewer(final UserResult object, final String ontology_type_name, String abr) {
		final OntologyPopup panel = new OntologyPopup(object.getBaitGene(), 
				object.getPreyGene(), abr, ontology_type_name);
		panel.showPopup();
	}
}

