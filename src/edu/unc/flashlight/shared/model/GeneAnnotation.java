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
package edu.unc.flashlight.shared.model;

import java.io.Serializable;

import net.sf.gilead.pojo.gwt.LightEntity;

public class GeneAnnotation extends LightEntity implements Serializable{

	private static final long serialVersionUID = 1L;
	
	//private Long id;
	private Long pubmed;
	private OntologyTerm ontologyTerm;
	private OntologyQualifier ontologyQualifier;
	
	public GeneAnnotation() {}
	
	/*public void setId(Long id) {
		this.id = id;
	}
	
	public Long getId() {
		return id;
	}*/
	
	public void setPubmed(Long pubmed) {
		this.pubmed = pubmed;
	}
	
	public Long getPubmed() {
		return pubmed;
	}
	
	public void setOntologyTerm(OntologyTerm t) {
		this.ontologyTerm = t;
	}
	
	public OntologyTerm getOntologyTerm() {
		return ontologyTerm;
	}
	
	public void setOntologyQualifier(OntologyQualifier t) {
		this.ontologyQualifier = t;
	}
	
	public OntologyQualifier getOntologyQualifier() {
		return ontologyQualifier;
	}
	
	public boolean equals(final Object o) {
            if (o instanceof GeneAnnotation) {
                final boolean one = ((GeneAnnotation) o).getOntologyTerm()
                                    .equals(ontologyTerm);
                final boolean two = !((((GeneAnnotation) o).getPubmed() == null && pubmed != null) || 
                					(((GeneAnnotation) o).getPubmed() != null && pubmed == null) ||
                					!((((GeneAnnotation) o).getPubmed() != null && pubmed != null) &&
                							((GeneAnnotation) o).getPubmed().equals(pubmed)));
                return one && two;
            } else if (o instanceof OntologyTerm) {
                    return ((OntologyTerm) o).equals(ontologyTerm);
            }
            return false;
    }
			
    public int hashCode() {
    	if (pubmed != null) return pubmed.hashCode() + ontologyTerm.hashCode();
    	return ontologyTerm.hashCode();
    }
	
}
