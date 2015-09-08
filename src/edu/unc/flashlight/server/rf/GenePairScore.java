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
package edu.unc.flashlight.server.rf;

import edu.unc.flashlight.shared.util.Conversion;


public class GenePairScore implements Comparable<GenePairScore>{
	private double msScore = Double.NEGATIVE_INFINITY;
	private double msPValue = Double.NEGATIVE_INFINITY;
	private double classifier = Double.NEGATIVE_INFINITY;
	private double classifierPValue = Double.NEGATIVE_INFINITY;
	private String baitNiceName;
	private String preyNiceName;
	
	public GenePairScore(final GenePairScore c) {
		this(c.getBaitNiceName(),c.getPreyNiceName());
	}
	
	public GenePairScore(final String baitNiceName, final String preyNiceName) {
		this.baitNiceName = baitNiceName;
		this.preyNiceName = preyNiceName;
	}
	
	public Double getMsScore() {
		return msScore;
	}
	
	public void setMsScore(final double msScore) {
		this.msScore = msScore;
	}

	public Double getClassifier() {
		return classifier;
	}
	
	public void setClassifier(final double classifier) {
		this.classifier = classifier;
	}
	
	public Double getMsPValue() {
		return msPValue;
	}
	
	public void setMsPValue(final double msPValue) {
		this.msPValue = msPValue;
	}

	public Double getClassifierPValue() {
		return classifierPValue;
	}
	
	public void setClassifierPValue(final double classifierPValue) {
		this.classifierPValue = classifierPValue;
	}
	
	public String getGenePairHash() {
		if (!Conversion.isInt(baitNiceName) || !Conversion.isInt(preyNiceName))
			return null;
		return Conversion.doGenePairHash(baitNiceName, preyNiceName);
	}
	
	public String getBaitNiceName() {
		return baitNiceName;
	}
	
	public String getPreyNiceName() {
		return preyNiceName;
	}
	
	@Override
	public int compareTo(GenePairScore arg0) {
		if (getClassifier() == Double.NEGATIVE_INFINITY) return getMsScore().compareTo(arg0.getMsScore());
		return getClassifier().compareTo((arg0).getClassifier());
	}
	
	@Override
	public String toString() {
		return String.valueOf(getMsScore());
	}
}

