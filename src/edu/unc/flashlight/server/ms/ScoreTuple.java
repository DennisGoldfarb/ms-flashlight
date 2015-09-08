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
package edu.unc.flashlight.server.ms;

public class ScoreTuple {
	private double unique;
	private double abundanceZ;
	private double abundance;
	private double repro;
	private double finalScore;
	
	public ScoreTuple(double abundance, double unique, double abundanceZ, double repro, double finalScore) {
		this.unique = unique;
		this.abundance = abundance;
		this.repro = repro;
		this.finalScore = finalScore;
		this.abundanceZ = abundanceZ;
	}
	
	public void setUnique(final double unique) {
		this.unique = unique;
	}
	
	public double getUnique() {
		return unique;
	}
	
	public void setAbundance(final double abundance) {
		this.abundance = abundance;
	}
	
	public double getAbundance() {
		return abundance;
	}
	
	public void setAbundanceZ(final double abundanceZ) {
		this.abundanceZ = abundanceZ;
	}
	
	public double getAbundanceZ() {
		return abundanceZ;
	}
	
	public void setRepro(final double repro) {
		this.repro = repro;
	}
	
	public double getRepro() {
		return repro;
	}
	
	public void setFinalScore(final double finalScore) {
		this.finalScore = finalScore;
	}
	
	public double getFinalScore() {
		return finalScore;
	}
}
