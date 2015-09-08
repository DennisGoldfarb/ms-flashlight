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
		name = "ExperimentData.all",
		query = "from ExperimentData ed"
	),
	@NamedQuery(
		name = "ExperimentData.deleteByOldUsers",
		query = "delete from ExperimentData ed " +
				"where ed.experiment.id in (select e.id from Experiment e where e.id in (select u.id from User u where :now >= u.createdTime and u.userType.id = 1))"
	),
	@NamedQuery(
		name = "ExperimentData.deleteByUser",
		query = "delete from ExperimentData ed " +
				"where ed.experiment.id in (select e.id from Experiment e where e.user.id = :id)"
	)				
})

@NamedNativeQueries({
	@NamedNativeQuery(
		name = "ExperimentData.getHashes",
		query = "select ed.gene_pair_hash, group_concat(distinct e.bait_upload_id order by e.bait_upload_id SEPARATOR '\\|') as bait_upload_id, " +
				"group_concat(distinct ed.prey_upload_id order by e.bait_upload_id SEPARATOR '\\|') as prey_upload_id, " +
				"max(e.bait_gene_id) as bait_gene_id, min(ed.prey_gene_id) as prey_gene_id " +
				"from experiment_data ed " +
				"inner join experiments e on e.experiment_id = ed.experiment_id " +
				"where e.user_id = :id and e.bait_gene_id != ed.prey_gene_id " +
				"and e.experiment_type_id = :exp_type " +
				"and ed.gene_pair_hash is not null " +
				"group by ed.gene_pair_hash " +
				"order by ed.gene_pair_hash",
	    resultSetMapping = "ExperimentData.getHashes.mapping"
	),
	@NamedNativeQuery(
		name = "getNumInteractions",
		query = "select count(1) as count " +
				"from experiment_data ed " +
				"inner join experiments e on e.experiment_id = ed.experiment_id " +
				"where e.user_id = :user_id",
	    resultSetMapping = "getNumInteractions.mapping"
	),
	@NamedNativeQuery(
		name = "getExp2Prey2SC",
		query = "select ed.experiment_id, ed.prey_gene_id, ed.spectral_count as sc, ed.prey_upload_id " +
				"from experiment_data ed " +
				"inner join experiments e on e.experiment_id = ed.experiment_id " +
				"where e.user_id = :user_id and e.experiment_type_id = :exp_type",
	    resultSetMapping = "getExp2Prey2SC.mapping"
	)
})

@SqlResultSetMappings({
	@SqlResultSetMapping(
		name="ExperimentData.getHashes.mapping", 
		columns={
	        @ColumnResult(name="ed.gene_pair_hash"),
	        @ColumnResult(name="bait_upload_id"),
	        @ColumnResult(name="prey_upload_id"),
	        @ColumnResult(name="bait_gene_id"),
	        @ColumnResult(name="prey_gene_id")
	    }
	),
	@SqlResultSetMapping(
		name="getNumInteractions.mapping", 
		columns={
	        @ColumnResult(name="count")
	    }
	),
	@SqlResultSetMapping(
		name="getExp2Prey2SC.mapping", 
		columns={
	        @ColumnResult(name="ed.experiment_id"),
	        @ColumnResult(name="ed.prey_gene_id"),
	        @ColumnResult(name="sc"),
	        @ColumnResult(name="ed.prey_upload_id")
	    }
	)
})

@Entity
@Table(name="experiment_data")
public class ExperimentData extends LightEntity implements Serializable{

	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue
	@Column(name="experiment_data_id")
	private Long id;
	@ManyToOne
    @JoinColumn(name="experiment_id")
	private Experiment experiment;
	@ManyToOne
    @JoinColumn(name="prey_gene_id")
	private Gene prey;
	@Column(name="spectral_count")
	private Integer spectralCount;
	@ManyToOne
    @JoinColumn(name="gene_pair_hash")
	private GenePair genePair;
	@Column(name="prey_upload_id")
	private String preyUploadId;
	@Column(name="prey_nice_name")
	private String preyNiceName;
	
	public ExperimentData() {}
	
	public Long getId() {
		return id;
	}
	
	public void setId(final Long id) {
		this.id = id;
	}
	
	public GenePair getGenePair() {
		return genePair;
	}
	
	public void setGenePair(final GenePair genePair) {
		this.genePair = genePair;
	}
	
	public Experiment getExperiment() {
		return experiment;
	}
	
	public void setExperiment(final Experiment experiment) {
		this.experiment = experiment;
	}
	
	public Gene getPrey() {
		return prey;
	}
	
	public void setPrey(final Gene prey) {
		this.prey = prey;
	}
	
	public Integer getSpectralCount() {
		return spectralCount;
	}
	
	public void setSpectralCount(final Integer spectralCount) {
		this.spectralCount = spectralCount;
	}
	
	public String getPreyUploadId() {
		return preyUploadId;
	}
	
	public void setPreyUploadId(final String preyUploadId) {
		this.preyUploadId = preyUploadId;
	}
	
	public String getPreyNiceName() {
		return preyNiceName;
	}
	
	public void setPreyNiceName(final String preyNiceName) {
		this.preyNiceName = preyNiceName;
	}
}
