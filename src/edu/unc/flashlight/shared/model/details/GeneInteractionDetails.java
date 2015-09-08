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

import edu.unc.flashlight.shared.model.GeneAnnotation;
import edu.unc.flashlight.shared.model.OntologyTerm;

public class GeneInteractionDetails implements Serializable{
	private static final long serialVersionUID = 1L;
	
	private String symbolA;
	private String symbolB;
	private Boolean isLowThroughput;
	
	public GeneInteractionDetails() {
		
	}
	
	public GeneInteractionDetails(String symbolA, String symbolB, Boolean isLow) {
		this.symbolA = symbolA;
		this.symbolB = symbolB;
		this.isLowThroughput = isLow;
	}
	
	public void setIsLowThroughput(Boolean isLow) {
		this.isLowThroughput = isLow;
	}
	
	public Boolean getIsLowThroughput() {
		return isLowThroughput;
	}
	
	public void setSymbolA(String symbolA) {
		this.symbolA = symbolA;
	}
	
	public String getSymbolA() {
		return symbolA;
	}
	
	public void setSymbolB(String symbolB) {
		this.symbolB = symbolB;
	}
	
	public String getSymbolB() {
		return symbolB;
	}
	
	public boolean equals(final Object o) {
        if (o instanceof GeneInteractionDetails) {
            	final boolean one = ((GeneInteractionDetails) o).getSymbolA().equals(symbolA);
                final boolean two = ((GeneInteractionDetails) o).getSymbolB().equals(symbolB);
                final boolean three = ((GeneInteractionDetails) o).getSymbolA().equals(symbolB);
                final boolean four = ((GeneInteractionDetails) o).getSymbolB().equals(symbolA);
                return (one && two) || (three && four);
        } 
        return false;
	}
		
	public int hashCode() {
		if (symbolA != null && symbolB != null)
			return symbolA.hashCode() + symbolB.hashCode();
		return super.hashCode();
	}

	public String toString() {
		return symbolA + " " + symbolB + " " + isLowThroughput.toString();
	}
}
