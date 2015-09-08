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
package edu.unc.flashlight.shared.model.upload;

import java.io.Serializable;

public class UploadResultCount implements Serializable {
	private static final long serialVersionUID = 1L;
	
	public int numBaits = 0;//
	public int numPreys = 0;//
	public int numExperiments = 0;//
	public int numControls = 0;
	public int numInteractions = 0;//
	public int numUniqueInteractions = 0;//
	public int numPreysNotMapped = 0;//
	public int numBaitsNotMapped = 0;//
	public int numExperimentsNotMapped = 0;//
	public int numInteractionsNotMapped = 0;//
	public int numUniqueInteractionsNotMapped = 0;//
	
	public UploadResultCount() {
		
	}
	
	public UploadResultCount(int numBaits, int numPreys, int numExperiments, int numControls, int numInteractions,
			int numUniqueInteractions) {
		this.numBaits = numBaits;
		this.numPreys = numPreys;
		this.numExperiments = numExperiments;
		this.numControls = numControls;
		this.numInteractions = numInteractions;
		this.numUniqueInteractions = numUniqueInteractions;
	}
	
	public int getNumBaits() {
		return numBaits;
	}
	
	public void setNumBaits(final int numBaits) {
		this.numBaits = numBaits;
	}
	
	public int getNumPreys() {
		return numPreys;
	}
	
	public void setNumPreys(final int numPreys) {
		this.numPreys = numPreys;
	}
	
	public int getNumExperiments() {
		return numExperiments;
	}
	
	public void setNumExperiments(final int numExperiments) {
		this.numExperiments = numExperiments;
	}
	
	public int getNumControls() {
		return numControls;
	}
	
	public void setNumControls(final int numControls) {
		this.numControls = numControls;
	}
	
	public int getNumInteractions() {
		return numInteractions;
	}
	
	public void setNumInteractions(final int numInteractions) {
		this.numInteractions = numInteractions;
	}
	
	public int getNumUniqueInteractions() {
		return numUniqueInteractions;
	}
	
	public void setNumUniqueInteractions(final int numUniqueInteractions) {
		this.numUniqueInteractions = numUniqueInteractions;
	}
	
	public int getNumBaitsNotMapped() {
		return numBaitsNotMapped;
	}
	
	public void setNumBaitsNotMapped(final int numBaitsNotMapped) {
		this.numBaitsNotMapped = numBaitsNotMapped;
	}
	
	public int getNumPreysNotMapped() {
		return numPreysNotMapped;
	}
	
	public void setNumPreysNotMapped(final int numPreysNotMapped) {
		this.numPreysNotMapped = numPreysNotMapped;
	}
	
	public int getNumInteractionsNotMapped() {
		return numInteractionsNotMapped;
	}
	
	public void setNumInteractionsNotMapped(final int numInteractionsNotMapped) {
		this.numInteractionsNotMapped = numInteractionsNotMapped;
	}
	
	public int getNumUniqueInteractionsNotMapped() {
		return numUniqueInteractionsNotMapped;
	}
	
	public void setNumUniqueInteractionsNotMapped(final int numUniqueInteractionsNotMapped) {
		this.numUniqueInteractionsNotMapped = numUniqueInteractionsNotMapped;
	}
	
	public int getNumExperimentsNotMapped() {
		return numExperimentsNotMapped;
	}
	
	public void setNumExperimentsNotMapped(final int numExperimentsNotMapped) {
		this.numExperimentsNotMapped = numExperimentsNotMapped;
	}
	

}
