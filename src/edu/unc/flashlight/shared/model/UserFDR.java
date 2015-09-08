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
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import net.sf.gilead.pojo.gwt.LightEntity;

@NamedQueries({
	@NamedQuery(
		name = "UserFDR.deleteByOldUsers",
		query= "delete from UserFDR uf " +
				"where uf.user.id in (select u.id from User u where :now >= u.createdTime and u.userType.id = 1)"
	),
	@NamedQuery(
		name = "UserFDR.deleteByUser",
		query= "delete from UserFDR uf where uf.user.id = :id"
	),
	@NamedQuery(
		name = "UserFDR.getByUser",
		query= "from UserFDR uf where uf.user.id = :id order by fdr"
	)
})

@Entity
@Table(name="user_fdr")
public class UserFDR extends LightEntity implements Serializable{

	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue
	@Column(name="user_fdr_id")
	private Long id;
	@ManyToOne
	@JoinColumn(name="user_id")
	private User user;
	@Column(name="fdr")
	private Double fdr;
	@Column(name="threshold")
	private Double threshold;
	@Column(name="num_accepted")
	private Integer numAccepted;
	
	public UserFDR() {}
	
	public UserFDR(Double fdr, Double threshold, Integer numAccepted) {
		this.fdr = fdr;
		this.threshold = threshold;
		this.numAccepted = numAccepted;
	}
	
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
	
	public Double getFdr() {
		return fdr;
	}
	
	public void setFdr(final Double fdr) {
		this.fdr = fdr;
	}
	
	public Double getThreshold() {
		return threshold;
	}
	
	public void setThreshold(final Double threshold) {
		this.threshold = threshold;
	}
	
	public Integer getNumAccepted() {
		return numAccepted;
	}
	
	public void setNumAccepted(final Integer numAccepted) {
		this.numAccepted = numAccepted;
	}
}
