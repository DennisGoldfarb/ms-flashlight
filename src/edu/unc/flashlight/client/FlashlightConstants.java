package edu.unc.flashlight.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.ConstantsWithLookup;

import edu.unc.flashlight.client.resource.FlashlightClientBundle;
import edu.unc.flashlight.shared.util.Constants;

public interface FlashlightConstants extends ConstantsWithLookup {
	public static final FlashlightConstants INSTANCE =  GWT.create(FlashlightConstants.class);
	
	/** Menu **/
	@DefaultStringValue("Home")
	String menu_home();
	@DefaultStringValue("My Data")
	String menu_myData();
	@DefaultStringValue("Upload")
	String menu_upload();
	@DefaultStringValue("Browse")
	String menu_browse();
	@DefaultStringValue("About")
	String menu_about();
	@DefaultStringValue("Feedback")
	String menu_feedback();

	/** Algorithms **/
	@DefaultStringValue("HGSCore")
	String hgscore();
	@DefaultStringValue("CompPASS")
	String comppass();
	@DefaultStringValue("SAINT")
	String saint();

	/** UploadView **/
	@DefaultStringValue("Select APMS interaction scoring algorithm")
	String uploadView_selectAPMSAlgorithm();
	@DefaultStringValue("Logistic Regression Parameters:")
	String uploadView_chooseRFParams();
	@DefaultStringValue("Use indirect evidence")
	String uploadView_useRandomForest();
	@DefaultStringValue("Select public datasets to use with APMS scoring (Optional).")
	String uploadView_selectAPMSDatasets();
	@DefaultStringValue("Data not inserted. Please fix all errors and upload again.")
	String uploadView_uploadFail();
	@DefaultStringValue("Upload Complete!")
	String uploadView_uploadSuccess();
	@DefaultStringValue("View Data")
	String uploadView_viewData();
	@DefaultStringValue("Download Unmapped Data")
	String uploadView_unmappedData();
	@DefaultStringValue("Uploading..")
	String uploadView_uploading();
	@DefaultStringValue("Parsing..")
	String uploadView_parsing();
	@DefaultStringValue("Mapping identifiers..")
	String uploadView_mapping();
	@DefaultStringValue("Calculating APMS scores and permutations..")
	String uploadView_MS_scoring();
	//@DefaultStringValue("Calculating logistic regression scores and permutations..")
	@DefaultStringValue("Calculating logistic regression scores..")
	String uploadView_classifying();
	@DefaultStringValue("Updating Database")
	String uploadView_updating();
	
	/** UploadView - SAINT **/
	@DefaultStringValue("SAINT Parameters")
	String uploadView_saintParams();
	@DefaultStringValue("Number of virtual controls")
	String uploadView_saintVirtualControls();
	@DefaultStringValue("Number of replicates:")
	String uploadView_saintNumReplicates();
	
	/** SAINT Validation **/
	@DefaultStringValue("Please enter an integer between " + Constants.SAINT_VIRTUAL_CONTROLS_MIN + " and " + Constants.SAINT_VIRTUAL_CONTROLS_MAX)
	String valid_saintVirtualControls();
	@DefaultStringValue("Please enter an integer between " + Constants.SAINT_NUM_REPLICATES_MIN + " and " + Constants.SAINT_NUM_REPLICATES_MAX)
	String valid_saintNumReplicates();

	/** Datasets Table **/
	@DefaultStringValue("Dataset")
	String datasetsCol_dataset();
	@DefaultStringValue("AP Method")
	String datasetsCol_APMethod();
	@DefaultStringValue("Lab")
	String datasetsCol_lab();
	@DefaultStringValue("Pubmed")
	String datasetsCol_pubmed();
	@DefaultStringValue("# Exps")
	String datasetsCol_numExps();
	@DefaultStringValue("# Ctrl")
	String datasetsCol_numCtrls();
	@DefaultStringValue("# Interactions")
	String datasetsCol_numInteractions();
	@DefaultStringValue("No public datasets available.")
	String datasets_noResults();

	/** MyDataView **/
	@DefaultStringValue("Delete Data")
	String myDataView_deleteData();
	@DefaultStringValue("Export Data")
	String myDataView_exportData();
	@DefaultStringValue("False Discovery Rate")
	String myDataView_fdr();
	@DefaultStringValue("Gene Symbol")
	String myDataView_geneSymbol();
	@DefaultStringValue("Search")
	String myDataView_search();

	/** Results Table **/
	@DefaultStringValue("Bait")
	String resultsCol_bait();
	@DefaultStringValue("Prey")
	String resultsCol_prey();
	@DefaultStringValue("Classifier")
	String resultsCol_classifier();
	@DefaultStringValue("Classifier pval")
	String resultsCol_classifierPValue();
	@DefaultStringValue("msScore")
	String resultsCol_msScore();
	@DefaultStringValue("pval")
	String resultsCol_pValue();
	@DefaultStringValue("BP")
	String resultsCol_bp();
	@DefaultStringValue("CC")
	String resultsCol_cc();
	@DefaultStringValue("Hsa phen")
	String resultsCol_hsaPhen();
	@DefaultStringValue("Mmu phen")
	String resultsCol_mmuPhen();
	@DefaultStringValue("Disease")
	String resultsCol_disease();
	@DefaultStringValue("CXP Hsa")
	String resultsCol_cxpHsa();
	@DefaultStringValue("CXP Mmu")
	String resultsCol_cxpMmu();
	@DefaultStringValue("CXP Rno")
	String resultsCol_cxpRno();
	@DefaultStringValue("CXP Gga")
	String resultsCol_cxpGga();
	@DefaultStringValue("CXP Dre")
	String resultsCol_cxpDre();
	@DefaultStringValue("CXP Mcc")
	String resultsCol_cxpMcc();
	@DefaultStringValue("CXP Cel")
	String resultsCol_cxpCel();
	@DefaultStringValue("Domain")
	String resultsCol_domain();
	@DefaultStringValue("Homo int")
	String resultsCol_homoInt();
	@DefaultStringValue("Known?")
	String resultsCol_known();

	/** Table defaults **/
	@DefaultStringValue("No results found.")
	String table_noResults();
	
	/** Upload Table **/
	@DefaultStringValue("Results")
	String uploadCol_results();
	@DefaultStringValue("Mapped")
	String uploadCol_mapped();
	@DefaultStringValue("Not Mapped")
	String uploadCol_notMapped();
	@DefaultStringValue("Baits")
	String uploadCol_baits();
	@DefaultStringValue("Prey")
	String uploadCol_prey();
	@DefaultStringValue("Interactions")
	String uploadCol_interactions();
	@DefaultStringValue("Unique Interactions")
	String uploadCol_uniqueInteractions();
	@DefaultStringValue("Experiments")
	String uploadCol_experiments();
	@DefaultStringValue("Controls")
	String uploadCol_controls();
}
