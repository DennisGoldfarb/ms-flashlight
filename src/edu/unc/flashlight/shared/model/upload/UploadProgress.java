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

public class UploadProgress implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private double parsingProgress = 0;
	private double mappingProgress = 0;
	private double MSScoreProgress = 0;
	private double classifierProgress = 0;
	private double updateProgress = 0;
	
	public UploadProgress() {
		
	}
	
	public void setParsingProgress(double parsingProgress) {
		this.parsingProgress = parsingProgress;
	}
	
	public double getParsingProgress() {
		return parsingProgress;
	}
	
	public void setMappingProgress(double mappingProgress) {
		this.mappingProgress = mappingProgress;
	}
	
	public double getMappingProgress() {
		return mappingProgress;
	}
	
	public void setMSScoreProgress(double MSScoreProgress) {
		this.MSScoreProgress = MSScoreProgress;
	}
	
	public double getMSScoreProgress() {
		return MSScoreProgress;
	}
	
	public void setClassifierProgress(double classifierProgress) {
		this.classifierProgress = classifierProgress;
	}
	
	public double getClassifierProgress() {
		return classifierProgress;
	}
	
	public void setUpdateProgress(double updateProgress) {
		this.updateProgress = updateProgress;
	}
	
	public double getUpdateProgress() {
		return updateProgress;
	}
}
