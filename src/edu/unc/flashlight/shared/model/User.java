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
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import net.sf.gilead.pojo.gwt.LightEntity;

import org.hibernate.annotations.Formula;
import org.hibernate.annotations.Source;
import org.hibernate.annotations.SourceType;

@NamedQueries({
	@NamedQuery(
		name = "User.all",
		query= "from User u"
	),
	@NamedQuery(
		name = "User.deleteByOldUsers",
		query= "delete from User u " +
				"where :now >= u.createdTime and u.userType.id = 1"
	),
	@NamedQuery(
		name = "User.byId",
		query= "from User u where id = :id"
	),
	@NamedQuery(
		name = "User.byUsername",
		query= "from User u where username = :username"
	),
	@NamedQuery(
		name = "User.getPublic",
		query= "from User u inner join fetch u.experimentRole where u.experimentRole.name = 'Public'"
	)
})

@Entity
@Table(name="users")
public class User extends LightEntity implements Serializable{

	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue
	@Column(name="user_id")
	private Long id;
	@ManyToOne
	@JoinColumn(name="user_type_id")
	private UserType userType;
	@Column(name="email")
	private String email;
	@Column(name="username")
	private String username;
	@Column(name="password")
	private String password;
	@Source(SourceType.VM)
	@Column(name="created_time")
	private Date createdTime;
	@ManyToOne
	@JoinColumn(name="experiment_role_id")
	private ExperimentRole experimentRole;
	@Column(name="purification_method")
	private String purificationMethod;
	@Column(name="lab_name")
	private String labName;
	@Column(name="pubmed_id")
	private Long pubmedId;
	@Formula("(select count(1) from experiments e where e.user_id = user_id and e.experiment_type_id = 2)")
	private Integer numExp;
	@Formula("(select count(1) from experiments e where e.user_id = user_id and e.experiment_type_id = 1)")
	private Integer numCtrl;
	@Formula("(select count(1) from experiment_data ed inner join experiments e on e.experiment_id = ed.experiment_id " +
			"where e.user_id = user_id)")
	private Integer numInteractions;
	
	
	public User() {}
	
	public Long getId() {
		return id;
	}
	
	public void setId(final Long id) {
		this.id = id;
	}
	
	public ExperimentRole getExperimentRole() {
		return experimentRole;
	}
	
	public void setExperimentRole(final ExperimentRole experimentRole) {
		this.experimentRole = experimentRole;
	}
	
	public String getEmail() {
		return email;
	}
	
	public void setEmail(final String email) {
		this.email = email;
	}
	
	public String getUsername() {
		return username;
	}
	
	public void setUsername(final String username) {
		this.username = username;
	}
	
	public String getPassword() {
		return password;
	}
	
	public void setPassword(final String password) {
		this.password = password;
	}
	
	public UserType getUserType() {
		return userType;
	}
	
	public void setUserType(final UserType userType) {
		this.userType = userType;
	}
	
	public Date getCreatedTime() {
		return createdTime;
	}
	
	public void setCreatedTime(final Date createdTime) {
		this.createdTime = createdTime;
	}
	
	public String getLabName() {
		return labName;
	}
	
	public void setLabName(final String labName) {
		this.labName = labName;
	}
	
	public String getPurificationMethod() {
		return purificationMethod;
	}
	
	public void setPurificationMethod(final String purificationMethod) {
		this.purificationMethod = purificationMethod;
	}
	
	public Long getPubmedId() {
		return pubmedId;
	}
	
	public void setPubmedId(final Long pubmedId) {
		this.pubmedId = pubmedId;
	}
	
	public Integer getNumExp() {
		return numExp;
	}
	
	public void setNumExp(final Integer numExp) {
		this.numExp = numExp;
	}
	
	public Integer getNumCtrl() {
		return numCtrl;
	}
	
	public void setNumCtrl(final Integer numCtrl) {
		this.numCtrl = numCtrl;
	}
	
	public Integer getNumInteractions() {
		return numInteractions;
	}
	
	public void setNumInteractions(final Integer numInteractions) {
		this.numInteractions = numInteractions;
	}
	
}
