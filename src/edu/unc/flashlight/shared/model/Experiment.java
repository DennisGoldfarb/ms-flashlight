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
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
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
import javax.persistence.OneToMany;
import javax.persistence.SqlResultSetMapping;
import javax.persistence.SqlResultSetMappings;
import javax.persistence.Table;

import net.sf.gilead.pojo.gwt.LightEntity;



@NamedQueries({
	@NamedQuery(
		name = "Experiment.all",
		query= "from Experiment e"
	),
	@NamedQuery(
		name = "Experiment.deleteByOldUsers",
		query= "delete from Experiment e " +
				"where e.user.id in (select u.id from User u where :now >= u.createdTime and u.userType.id = 1)"
	),
	@NamedQuery(
		name = "Experiment.deleteByUser",
		query= "delete from Experiment e where e.user.id = :id"
	)
})

@NamedNativeQueries({
	@NamedNativeQuery(
		name = "ExperimentName.byExperimentName",
		query = "select name from experiments inner join exp_temp t on t.id = name and user_id = :id",
	    resultSetMapping = "ExperimentName.byExperimentName.mapping"
	),
	@NamedNativeQuery(
		name = "Experiment.baitsByUser",
		query = "select e.experiment_id, e.bait_gene_id, e.bait_upload_id " +
				"from experiments e where e.user_id = :user_id " +
				"and e.experiment_type_id = :exp_type",
	    resultSetMapping = "Experiment.baitsByUser.mapping"
	),
	@NamedNativeQuery(
		name = "Experiment.expByUser",
		query = "select e.experiment_id " +
				"from experiments e " +
				"where e.user_id = :user_id " +
				"and e.experiment_type_id = :exp_type",
	    resultSetMapping = "Experiment.expByUser.mapping"
	),
	@NamedNativeQuery(
		name = "Experiment.numMappedBaits",
		query = "select count(1) as num " +
				"from experiments e " +
				"where e.user_id = :user_id " +
				"and e.bait_gene_id is not null " +
				"and e.experiment_type_id = 2",
		resultSetMapping = "Experiment.numMappedBaits.mapping"
	)
})

@SqlResultSetMappings({
	@SqlResultSetMapping(
		name="Experiment.numMappedBaits.mapping", 
		columns={
	        @ColumnResult(name="num")
	    }
	),
	@SqlResultSetMapping(
		name="ExperimentName.byExperimentName.mapping", 
		columns={
	        @ColumnResult(name="name")
	        }
	),
	@SqlResultSetMapping(
		name="Experiment.baitsByUser.mapping", 
		columns={
	        @ColumnResult(name="e.experiment_id"),
	        @ColumnResult(name="e.bait_gene_id"),
	        @ColumnResult(name="e.bait_upload_id")
	        }
	),
	@SqlResultSetMapping(
		name="Experiment.expByUser.mapping", 
		columns={
	        @ColumnResult(name="e.experiment_id")
	        }
	)
})


@Entity
@Table(name="experiments")
public class Experiment extends LightEntity implements Serializable{

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	@Column(name="experiment_id")
	private Long id;
	@ManyToOne
	@JoinColumn(name="user_id")
	private User user;
	@Column(name="name")
	private String name;
	@OneToMany(mappedBy="experiment",cascade=CascadeType.REMOVE)
	private Set<ExperimentData> data;
	@ManyToOne
	@JoinColumn(name="bait_gene_id")
	private Gene bait;
	@ManyToOne
	@JoinColumn(name="experiment_type_id")
	private ExperimentType experimentType;
	@ManyToOne
	@JoinColumn(name="experiment_role_id")
	private ExperimentRole experimentRole;
	@Column(name="bait_upload_id")
	private String baitUploadId;
	@Column(name="bait_nice_name")
	private String baitNiceName;

	public Experiment() {}

	public Long getId() {
		return id;
	}

	public void setId(final Long id) {
		this.id = id;
	}

	public User getUser() {
		return user;
	}

	public void setUser(final User user) {
		this.user = user;
	}

	public String getName() {
		return name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public Set<ExperimentData> getData() {
		return data;
	}

	public void setData(final Set<ExperimentData> data) {
		this.data = data;
	}

	public void addDatum(final ExperimentData datum) {
		if (data == null) data = new HashSet<ExperimentData>();
		data.add(datum);
	}

	public Gene getBait() {
		return bait;
	}

	public void setBait(final Gene bait) {
		this.bait = bait;
	}

	public ExperimentType getExperimentType() {
		return experimentType;
	}

	public void setExperimentType(final ExperimentType experimentType) {
		this.experimentType = experimentType;
	}

	public ExperimentRole getExperimentRole() {
		return experimentRole;
	}

	public void setExperimentRole(final ExperimentRole experimentRole) {
		this.experimentRole = experimentRole;
	}

	public String getBaitUploadId() {
		return baitUploadId;
	}

	public void setBaitUploadId(final String baitUploadId) {
		this.baitUploadId = baitUploadId;
	}

	public String getBaitNiceName() {
		return baitNiceName;
	}

	public void setBaitNiceName(final String baitNiceName) {
		this.baitNiceName = baitNiceName;
	}
}
