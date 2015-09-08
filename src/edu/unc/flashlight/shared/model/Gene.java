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
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import net.sf.gilead.pojo.gwt.LightEntity;

import org.hibernate.annotations.Columns;
import org.hibernate.annotations.Type;

@Entity
@Table(name="genes")
public class Gene extends LightEntity implements Serializable{

	private static final long serialVersionUID = 1L;
	
	@Id
	@Column(name="entrez_gene_id")
	private Long id;
	@Column(name="official_symbol")
	private String officialSymbol;
	@Column(name="description")
	private String description;
	@Column(name="chromosome")
	private String chromosome;
	@Column(name="is_obsolete")
	private Boolean isObsolete;
	@ManyToOne
    @JoinColumn(name="tax_id")
	private Taxonomy taxonomy;
	@ManyToOne
    @JoinColumn(name="gene_type_id")
	private GeneType geneType;
	@ElementCollection
	@CollectionTable(name="gene_sequences", joinColumns=@JoinColumn(name="gene_id"))
	@Column(name="sequence_id")
	private Set<String> sequenceIds;
	@OneToMany
	@JoinColumn(name="sequence_id")
	private Set<Sequence> sequences;
	@ElementCollection
	@CollectionTable(name="gene_aliases", joinColumns=@JoinColumn(name="gene_id"))
	@Column(name="name")
	private Set<String> aliases;
	private Collection<GeneAnnotation> annotations;
	private Set<GeneAnnotation> bpAnnotations;
	private Set<GeneAnnotation> ccAnnotations;
	private Set<GeneAnnotation> hsaAnnotations;
	private Set<GeneAnnotation> mmuAnnotations;
	private Set<GeneAnnotation> doAnnotations;
	private Set<GeneInteraction> interactions;
	
	public Gene() {}
	
	public Gene(Long id) {
		setId(id);
	}
	
	public Long getId() {
		return id;
	}
	
	public void setId(final Long id) {
		this.id = id;
	}
	
	public String getChromosome() {
		return chromosome;
	}
	
	public void setChromosome(final String chromosome) {
		this.chromosome = chromosome;
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(final String description) {
		this.description = description;
	}
	
	public String getOfficialSymbol() {
		return officialSymbol;
	}
	
	public void setOfficialSymbol(final String officialSymbol) {
		this.officialSymbol = officialSymbol;
	}
	
	public Taxonomy getTaxonomy() {
		return taxonomy;
	}
	
	public void setTaxonomy(final Taxonomy taxonomy) {
		this.taxonomy = taxonomy;
	}
	
	public GeneType getGeneType() {
		return geneType;
	}
	
	public void setGeneType(final GeneType geneType) {
		this.geneType = geneType;
	}
	
	
	public Set<Sequence> getSequences() {
		return sequences;
	}
	
	public void setSequences(final Set<Sequence> sequences) {
		this.sequences = sequences;
	}
	
	public Set<String> getAliases() {
		return aliases;
	}
	
	public void setAliases(final Set<String> aliases) {
		this.aliases = aliases;
	}
	
	public Set<String> getSequenceIds() {
		return sequenceIds;
	}
	
	public void setSequenceIds(final Set<String> sequenceIds) {
		this.sequenceIds = sequenceIds;
	}
	
	public Collection<GeneAnnotation> getAnnotations() {
		return annotations;
	}
	
	public void setAnnotations(final Collection<GeneAnnotation> annotations) {
		this.annotations = annotations;
	}
	
	public Boolean getIsObsolete() {
		return isObsolete;
	}
	
	public void setIsObsolete(final Boolean isObsolete) {
		this.isObsolete = isObsolete;
	}
	
	public Set<GeneAnnotation> getBpAnnotations() {
		bpAnnotations = new HashSet<GeneAnnotation>();
		for (GeneAnnotation t : annotations) {
			if (t.getOntologyTerm().getOntologyType().getName().equals("Biological Process")) bpAnnotations.add(t);			
		}
		return bpAnnotations;
	}
	
	public Set<GeneAnnotation> getCcAnnotations() {
		ccAnnotations = new HashSet<GeneAnnotation>();
		for (GeneAnnotation t : annotations) {
			if (t.getOntologyTerm().getOntologyType().getName().equals("Cellular Component")) ccAnnotations.add(t);
		}
		return ccAnnotations;
	}
	
	public Set<GeneAnnotation> getHsaAnnotations() {
		hsaAnnotations = new HashSet<GeneAnnotation>();
		for (GeneAnnotation t : annotations) {
			if (t.getOntologyTerm().getOntologyType().getName().equals("Human Phenotype")) hsaAnnotations.add(t);
		}
		return hsaAnnotations;
	}
	
	public Set<GeneAnnotation> getMmuAnnotations() {
		mmuAnnotations = new HashSet<GeneAnnotation>();
		for (GeneAnnotation t : annotations) {
			if (t.getOntologyTerm().getOntologyType().getName().equals("Mouse Phenotype")) mmuAnnotations.add(t);
		}
		return mmuAnnotations;
	}
	
	public Set<GeneAnnotation> getDoAnnotations() {
		doAnnotations = new HashSet<GeneAnnotation>();
		for (GeneAnnotation t : annotations) {
			if (t.getOntologyTerm().getOntologyType().getName().equals("Disease Ontology")) doAnnotations.add(t);
		}
		return doAnnotations;
	}
	
	public Set<GeneAnnotation> getAnnotationsByType(String type) {
		Set<GeneAnnotation> result = new HashSet<GeneAnnotation>();
		for (GeneAnnotation t: annotations) {
			if (t.getOntologyTerm().getOntologyType().getName().equals(type)) {
				result.add(t);
			}
		}
		return result;
	}
	
	public Set<GeneInteraction> getInteractions() {
		return interactions;
	}
	
	public void setInteractions(Set<GeneInteraction> interactions) {
		this.interactions = interactions;
	}
}
