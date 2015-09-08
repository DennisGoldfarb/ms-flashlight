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
package edu.unc.flashlight.shared.util;

public class Constants {
	public static final String COXPRESDB_URL = "http://coxpresdb.jp/cgi-bin/coexpression_viewer.cgi";
	public static final String COXPRESDB_IMG_URL = "http://coxpresdb.jp/data/coexpression_detail/";
	public static final String NOT_MAPPED_FILE = "notmapped";
	public static final String DOWNLOAD_SERVLET = "downloadServlet";
	public static final String SAINT_PATH ="/Users/Dennis/Research/SAINTexpress_v3.1.0/bin";
	//public static final String SAINT_PATH ="/Users/Dennis/Research/SAINT_v2.3.4/SAINT_v2.3.4/bin";
	public static final String FILES_PATH = "/Users/Dennis/spotlite_files/";
	public static final String SEP = "/";

	public static final String SAINT_CONTROL_EXEC ="saint-spc-ctrl";
	public static final String SAINT_NO_CONTROL_EXEC ="SAINTexpress-spc";
	//public static final String SAINT_NO_CONTROL_EXEC ="saint-spc-noctrl";
	public static final String SAINT_REFORMAT_EXEC ="saint-reformat";
	public static final String SAINT_PREY_FILE_SUFFIX = "SAINT_prey.csv";
	public static final String SAINT_BAIT_FILE_SUFFIX = "SAINT_bait.csv";
	public static final String SAINT_INTERACTION_FILE_SUFFIX = "SAINT_interaction.csv";
	//public static final String SAINT_RESULTS_SUFFIX = "RESULT/unique_interactions";
	public static final String SAINT_RESULTS_SUFFIX = "list.txt";
	public static final int SAINT_VIRTUAL_CONTROLS = 10;
	public static final int SAINT_VIRTUAL_CONTROLS_MAX = 100;
	public static final int SAINT_VIRTUAL_CONTROLS_MIN = 1;
	public static final int SAINT_NUM_REPLICATES = 100;
	public static final int SAINT_NUM_REPLICATES_MAX = 100;
	public static final int SAINT_NUM_REPLICATES_MIN = 1;
	
	public static final Integer AVERAGE_PROTEIN_LENGTH = 568;
	public static enum MS_ALGORITHMS {HGSCore, CompPASS, SAINT};
	public static final String SAINT_DELIMITER = "\t";
	public static final String SPOTLITE_DELIMITER = "\t";
	public static final int PARSER_PROGRESS_UPDATE_STEP = 5000;

}
