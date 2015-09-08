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
import java.util.Set;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ColumnResult;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedNativeQueries;
import javax.persistence.NamedNativeQuery;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.SqlResultSetMapping;
import javax.persistence.SqlResultSetMappings;
import javax.persistence.Table;

import net.sf.gilead.pojo.gwt.LightEntity;

@NamedQueries({
	@NamedQuery(
		name = "OntologyTerm.all",
		query= "from OntologyTerm ot"
	),
	@NamedQuery(
		name = "OntologyTerm.all,byType",
		query= "from OntologyTerm ot " +
//				"left join fetch ot.parents " +
//				"left join fetch ot.children " +
				"where ot.ontologyType.name = :name"
	)
})

@NamedNativeQueries({
	@NamedNativeQuery(
		name = "OntologyTerm.getNetworkForGenes",
		query = "CALL GetAncestryGene(:id_a, :id_b, :type, :relation_type)",
	    resultSetMapping = "OntologyTerm.getNetworkForGenes.mapping"
	)
})

@SqlResultSetMappings({
	@SqlResultSetMapping(
		name="OntologyTerm.getNetworkForGenes.mapping", 
		columns={
	        @ColumnResult(name="name_ch"),
	        @ColumnResult(name="name_p") 
	        }
	)
})

@Entity
@Table(name="ontology_terms")
public class OntologyTerm extends LightEntity implements Serializable{

	private static final long serialVersionUID = 1L;
	
	@Id
	@Column(name="ontology_term_id")
	private String id;
	@Column(name="name")
	private String name;
	@Column(name="ic")
	private Float ic;
	@Column(name="count")
	private Integer count;
/*	@ManyToMany
	@JoinTable(
	        name="ontology_hierarchy",
	        joinColumns=@JoinColumn(name="parent_ontology_term_id"),
	        inverseJoinColumns=@JoinColumn(name="child_ontology_term_id")
	)
	private Set<OntologyTerm> children;
	@ManyToMany
	@JoinTable(
	        name="ontology_hierarchy",
	        joinColumns=@JoinColumn(name="child_ontology_term_id"),
	        inverseJoinColumns=@JoinColumn(name="parent_ontology_term_id")
	)
	private Set<OntologyTerm> parents;
	@ElementCollection
	@CollectionTable(name="ontology_hierarchy", joinColumns=@JoinColumn(name="child_ontology_term_id"))
	@Column(name="parent_ontology_term_id")
	private Set<String> parentIds;*/
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="ontology_type_id")
	private OntologyType ontologyType;
	
	public OntologyTerm() {}
	
	public String getId() {
		return id;
	}
	
	public void setId(final String id) {
		this.id = id;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(final String name) {
		this.name = name;
	}
	
	/*public Set<OntologyTerm> getChildren() {
		return children;
	}
	
	public void setChildren(final Set<OntologyTerm> children) {
		this.children = children;
	}
	
	public Set<OntologyTerm> getParents() {
		return parents;
	}
	
	public void setParents(final Set<OntologyTerm> parents) {
		this.parents = parents;
	}
	
	public Set<String> getParentIds() {
		return parentIds;
	}
	
	public void setParentIds(final Set<String> parentIds) {
		this.parentIds = parentIds;
	}*/
	
	public OntologyType getOntologyType() {
		return ontologyType;
	}
	
	public void setOntologyType(final OntologyType ontologyType) {
		this.ontologyType = ontologyType;
	}
	
	public String toString() {
		return name;
	}
	
	public void setCount(final Integer count) {
		this.count = count;
	}
	
	public Integer getCount() {
		return count;
	}
	
	public void setIc(final Float ic) {
		this.ic = ic;
	}
	
	public Float getIc() {
		return ic;
	} 
	
	public boolean equals(Object o) {
		return ((OntologyTerm) o).getId().equals(getId());
	}
}
