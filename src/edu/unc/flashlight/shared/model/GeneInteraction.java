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

import javax.persistence.Column;
import javax.persistence.ColumnResult;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedNativeQueries;
import javax.persistence.NamedNativeQuery;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SqlResultSetMapping;
import javax.persistence.SqlResultSetMappings;
import javax.persistence.Table;

import net.sf.gilead.pojo.gwt.LightEntity;

@NamedQueries({
	@NamedQuery(
		name = "GeneInteraction.directByIds",
		query = "from GeneInteraction g " +
				"inner join fetch g.geneA " +
				"inner join fetch g.geneB " +
				"where g.geneA.id in (:ids) or g.geneB.id in (:ids)"
	),
	@NamedQuery(
		name = "GeneInteraction.networkBetweenIds",
		query = "from GeneInteraction g " +
				"inner join fetch g.geneA " +
				"inner join fetch g.geneB " +
				"where g.geneA.id in (:ids) and g.geneB.id in (:ids)"
	),
	@NamedQuery(
		name = "GeneInteraction.all",
		query = "from GeneInteraction g " +
				"inner join fetch g.geneA " +
				"inner join fetch g.geneB " +
				"where g.geneA.taxonomy.id = 9606 and g.geneB.taxonomy.id = 9606"
	),
	@NamedQuery(
		name = "GeneInteraction.all_low",
		query = "from GeneInteraction g " +
				"inner join fetch g.geneA " +
				"inner join fetch g.geneB " +
				"where g.throughput = 'L' and g.geneA.taxonomy.id = 9606 and g.geneB.taxonomy.id = 9606"
	),
	@NamedQuery(
		name = "GeneInteraction.byExperimentalSystem",
		query = "from GeneInteraction g " +
				"inner join fetch g.experimentalSystem e"
	),
	@NamedQuery(
		name = "GeneInteraction.byGeneIds",
		query = "from GeneInteraction g " +
				"inner join fetch g.experimentalSystem " +
				"where g.geneA.id = :id_a and g.geneB.id = :id_b"
	)
})

@Entity
@Table(name="gene_interactions")
public class GeneInteraction extends LightEntity implements Serializable{

	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue
	@Column(name="gene_interaction_id")
	private Long id;
	@ManyToOne
    @JoinColumn(name="gene_id_a")
	private Gene geneA;
	@ManyToOne
    @JoinColumn(name="gene_id_b")
	private Gene geneB;
	@Column(name="biogrid_id_a")
	private Long biogridIDa;
	@Column(name="biogrid_id_b")
	private Long biogridIDb;
	@Column(name="throughput")
	private String throughput;
	@Column(name="pubmed_id")
	private Long pubmedID;
	@ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="experimental_system_id")
	private ExperimentalSystem experimentalSystem;
	
	public GeneInteraction() {}
	
	public Long getId() {
		return id;
	}
	
	public void setId(final Long id) {
		this.id = id;
	}
	
	public Gene getGeneA() {
		return geneA;
	}
	
	public void setGeneA(final Gene geneA) {
		this.geneA = geneA;
	}
	
	public Gene getGeneB() {
		return geneB;
	}
	
	public void setGeneB(final Gene geneB) {
		this.geneB = geneB;
	}
	
	public Long getBiogridIDa() {
		return biogridIDa;
	}
	
	public void setBiogridIDa(final Long biogridIDa) {
		this.biogridIDa = biogridIDa;
	}
	
	public Long getBiogridIDb() {
		return biogridIDb;
	}
	
	public void setBiogridIDb(final Long biogridIDb) {
		this.biogridIDb = biogridIDb;
	}
	
	public Long getPubmedID() {
		return pubmedID;
	}
	
	public void setPubmedID(final Long pubmedID) {
		this.pubmedID = pubmedID;
	}
	
	public ExperimentalSystem getExperimentalSystem() {
		return experimentalSystem;
	}
	
	public void setExperimentalSystem(final ExperimentalSystem experimentalSystem) {
		this.experimentalSystem = experimentalSystem;
	}
	
	public String getThroughput() {
		return throughput;
	}
	
	public void setThroughput(final String throughput) {
		this.throughput = throughput;
	}
	
}
