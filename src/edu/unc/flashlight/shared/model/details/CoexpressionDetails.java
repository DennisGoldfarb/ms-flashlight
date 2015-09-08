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
package edu.unc.flashlight.shared.model.details;

import java.io.Serializable;

import edu.unc.flashlight.shared.model.Gene;

public class CoexpressionDetails implements Serializable{
	private static final long serialVersionUID = 1L;

	private Gene geneA;
	private Gene geneB;
	private Double pcc;
	private Boolean hasImage;
	
	public CoexpressionDetails() {
		
	}
	
	public CoexpressionDetails(Gene geneA, Gene geneB, Double pcc) {
		this(geneA, geneB, pcc, true);
	}
	
	public CoexpressionDetails(Gene geneA, Gene geneB, Double pcc, Boolean hasImage) {
		this.geneA = geneA;
		this.geneB = geneB;
		this.hasImage = hasImage;
		this.pcc = pcc;
	}
	
	public void setGeneA(Gene geneA) {
		this.geneA = geneA;
	}
	
	public Gene getGeneA() {
		return geneA;
	}
	
	public void setGeneB(Gene geneB) {
		this.geneB = geneB;
	}
	
	public Gene getGeneB() {
		return geneB;
	}
	
	public void setPcc(Double pcc) {
		this.pcc = pcc;
	}
	
	public Double getPcc() {
		return pcc;
	}
	
	public void setHasImage(Boolean hasImage) {
		this.hasImage = hasImage;
	}
	
	public Boolean getHasImage() {
		return hasImage;
	}
}
