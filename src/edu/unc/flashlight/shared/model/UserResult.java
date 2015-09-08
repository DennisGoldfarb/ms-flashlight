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

import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

@NamedQueries({
	@NamedQuery(
	name = "UserResult.all",
	query = "from UserResult ur"
	),
	@NamedQuery(
	name = "UserResult.deleteByOldUsers",
	query = "delete from UserResult ur where ur.user.id in (select u.id from User u where :now >= u.createdTime and u.userType.id = 1)"
	),
	@NamedQuery(
	name = "UserResult.deleteByUser",
	query = "delete from UserResult ur where ur.user.id = :id"
	),
	@NamedQuery(
	name = "getData",
	query = "from UserResult ur " +
			"left join fetch ur.genePair " +
			"left join fetch ur.baitGene " +
			"left join fetch ur.preyGene " +
			"where ur.user.id = :id " +
			"order by ur.classifierScore desc, ur.msPValue"
	),
	@NamedQuery(
	name = "getDataSearch",
	query = "select distinct ur " +
			"from UserResult ur " +
			"left join fetch ur.genePair " +
			"inner join fetch ur.user " +
			"left join fetch ur.baitGene " +
			"left join fetch ur.preyGene " +
			//"left join ur.baitGene.aliases as a " +
			//"left join ur.preyGene.aliases as b " +
			//"where ur.user.id = :id and (ur.baitGene.officialSymbol = :sym or ur.preyGene.officialSymbol = :sym or :sym in a or :sym in b or ur.baitUploadId = :sym or ur.preyUploadId = :sym)  " +
			"where ur.user.id = :id and (ur.baitGene.officialSymbol = :sym or ur.preyGene.officialSymbol = :sym or ur.baitUploadId = :sym or ur.preyUploadId = :sym)  " +
			"order by ur.classifierScore desc, ur.msPValue"
	),
	@NamedQuery(
	name = "getDataSearch.size",
	query = "select count(distinct ur) " +
			"from UserResult ur " +
			"left join ur.genePair " +
			"inner join ur.user " +
			"left join ur.baitGene " +
			"left join ur.preyGene " +
			//"left join ur.baitGene.aliases as a " +
			//"left join ur.preyGene.aliases as b " +
			//"where ur.user.id = :id and (ur.baitGene.officialSymbol = :sym or ur.preyGene.officialSymbol = :sym or :sym in a or :sym in b or ur.baitUploadId = :sym or ur.preyUploadId = :sym) " +
			"where ur.user.id = :id and (ur.baitGene.officialSymbol = :sym or ur.preyGene.officialSymbol = :sym or ur.baitUploadId = :sym or ur.preyUploadId = :sym)  " +
			"order by ur.classifierScore desc, ur.msPValue"
	),
	@NamedQuery(
	name = "getBrowseDataSearch",
	query = "select distinct ur " +
			"from UserResult ur " +
			"left join fetch ur.genePair " +
			"inner join fetch ur.user " +
			"left join fetch ur.baitGene " +
			"left join fetch ur.preyGene " +
			"left join ur.baitGene.aliases as a " +
			"left join ur.preyGene.aliases as b " +
			"where ur.user.id in (select u.id from User u where u.experimentRole.id = 1) " +
			"and (ur.baitGene.officialSymbol = :sym or ur.preyGene.officialSymbol = :sym or :sym in a or :sym in b) " +
			"order by ur.classifierScore desc, ur.msPValue"
	),
	@NamedQuery(
	name = "getBrowseDataSearch.size",
	query = "select count(distinct ur) " +
			"from UserResult ur " +
			"left join ur.genePair " +
			"left join ur.baitGene " +
			"left join ur.preyGene " +
			"left join ur.preyGene.aliases as a " +
			"left join ur.baitGene.aliases as b " +
			"where ur.user.id in (select u.id from User u where u.experimentRole.id = 1) " +
			"and (ur.baitGene.officialSymbol = :sym or ur.preyGene.officialSymbol = :sym or :sym in a or :sym in b) " +
			"order by ur.classifierScore desc, ur.msPValue"
	),
	@NamedQuery(
	name = "getBrowseData",
	query = "from UserResult ur " +
			"left join fetch ur.genePair " +
			"inner join fetch ur.user " +
			"left join fetch ur.baitGene " +
			"left join fetch ur.preyGene " +
			"where ur.user.id in (select u.id from User u where u.experimentRole.id = 1) " +
			"order by ur.classifierScore desc, ur.msPValue"
	),
	@NamedQuery(
	name = "getBrowseData.size",
	query = "select count(ur) " +
			"from UserResult ur " +
			"join ur.user u " +
			"where u.experimentRole.id = 1"
	),
	@NamedQuery(
	name = "getClassificationAlgorithm.byUser",
	query = "select ur.classificationAlgorithm.name " +
			"from UserResult ur " +
			"inner join ur.user " +
			"inner join ur.classificationAlgorithm " +
			"where ur.user.id = :uid"
	),
	@NamedQuery(
	name = "getData.size",
	query = "select count(ur.id) " +
			"from UserResult ur " +
			"join ur.user u " +
			"where u.id = :id"
	)
})

@NamedNativeQueries({
	@NamedNativeQuery(
	name = "getDataPerExp",
	query = "select e.bait_upload_id, g1.official_symbol, e.bait_gene_id, ed.prey_upload_id, g2.official_symbol, ed.prey_gene_id, " +
			"ur.classifier_score, ur.classifier_p_Value, ur.ms_score, ur.ms_p_value, group_concat(ed.spectral_count SEPARATOR \"|\") as es, " +
			"(select group_concat(edc.spectral_count SEPARATOR \"|\")" +
			"from experiments ec " +
			"inner join experiment_data edc on edc.experiment_id = ec.experiment_id  " +
			"where edc.prey_nice_name = ed.prey_nice_name " +
			"and ec.user_id = e.user_id " +
			"and ec.experiment_type_id = 1) as cs, " +
			"gp.* " +
			"from user_results ur " +
			"inner join experiments e on e.bait_nice_name = ur.bait_nice_name and e.user_id = ur.user_id " +
			"inner join experiment_data ed on e.experiment_id = ed.experiment_id and ed.prey_nice_name = ur.prey_nice_name " +
			"left join genes g2 on g2.entrez_gene_id = ed.prey_gene_id " +
			"left join genes g1 on g1.entrez_gene_id = e.bait_gene_id " +
			"left join gene_pairs gp on gp.gene_pair_hash = ur.gene_pair_hash " +
			"where e.user_id = :id and e.bait_nice_name <> ed.prey_nice_name " +
			"and e.experiment_type_id = 2 " +
			"group by e.bait_nice_name, ed.prey_nice_name " +
			"order by ur.classifier_p_value, ur.ms_p_value ",
        resultSetMapping = "getDataPerExp.mapping"
	),
	@NamedNativeQuery(
	name = "UserResult.updateBaitGeneIds",
	query = "update user_results " +
			"set bait_gene_id = (select e.bait_gene_id " + 
								"from experiments e where e.user_id = :user_id " +
								"and user_results.bait_nice_name = e.bait_nice_name limit 1) "+
			"where user_results.user_id = :user_id",
	resultSetMapping = "void"
	),
	@NamedNativeQuery(
	name = "UserResult.updatePreyGeneIds",
	query = "update user_results " +
			"set prey_gene_id = (select ed.prey_gene_id " + 
								"from experiment_data ed " +
								"inner join experiments e on e.experiment_id = ed.experiment_id " +
								"where user_results.prey_nice_name = ed.prey_nice_name and e.user_id = :user_id limit 1) "+
			"where user_results.user_id = :user_id",
	resultSetMapping = "void"
	),
	@NamedNativeQuery(
	name = "UserResult.updateBaitUploadIds",
	query = "update user_results " +
			"set bait_upload_id = (select e.bait_upload_id " + 
								"from experiments e where e.user_id = :user_id " +
								"and user_results.bait_nice_name = e.bait_nice_name limit 1) " +
			"where user_results.user_id = :user_id",
	resultSetMapping = "void"
	),
	@NamedNativeQuery(
	name = "UserResult.updatePreyUploadIds",
	query = "update user_results " +
			"set prey_upload_id = (select ed.prey_upload_id " + 
								"from experiment_data ed " +
								"inner join experiments e on e.experiment_id = ed.experiment_id " +
								"where user_results.prey_nice_name = ed.prey_nice_name and e.user_id = :user_id limit 1) " +
			"where user_results.user_id = :user_id",
	resultSetMapping = "void"
	)
})

@SqlResultSetMappings({
@SqlResultSetMapping(name="getDataPerExp.mapping", 
		columns={
	            @ColumnResult(name="e.bait_upload_id"),
	            @ColumnResult(name="g1.official_symbol"),
	            @ColumnResult(name="e.bait_gene_id"),
	            @ColumnResult(name="ed.prey_upload_id"),
	            @ColumnResult(name="g2.official_symbol"),
	            @ColumnResult(name="ed.prey_gene_id"),
	            @ColumnResult(name="ur.classifier_score"),
	            @ColumnResult(name="ur.classifier_p_value"),
	            @ColumnResult(name="ur.ms_score"),
	            @ColumnResult(name="ur.ms_p_value"),
	            @ColumnResult(name="es"),
	            @ColumnResult(name="cs"),
	            @ColumnResult(name="gp.gene_pair_hash"),
	            @ColumnResult(name="gp.bp_score"),
	            @ColumnResult(name="gp.cc_score"),
	            @ColumnResult(name="gp.coxp_human_score"),
	            @ColumnResult(name="gp.coxp_mouse_score"),
	            @ColumnResult(name="gp.coxp_monkey_score"),
	            @ColumnResult(name="gp.coxp_dog_score"),
	            @ColumnResult(name="gp.coxp_rat_score"),
	            @ColumnResult(name="gp.coxp_fish_score"),
	            @ColumnResult(name="gp.domain_score"),
	            @ColumnResult(name="gp.homo_int_score"),
	            @ColumnResult(name="gp.human_phen_score"),
	            @ColumnResult(name="gp.mouse_phen_score"),
	            @ColumnResult(name="gp.disease_score"),
	            @ColumnResult(name="gp.is_known")
	            }
	),

	@SqlResultSetMapping(name="void", 
	columns={
	        @ColumnResult(name="void"),
	        }
	)
})


@Entity
@Table(name="user_results")
public class UserResult extends LightEntity implements Serializable{

	private static final long serialVersionUID = 1L;
	
	@Id
	@Column(name="user_results_id")
	private Long id;
	@ManyToOne
    @JoinColumn(name="user_id")
	private User user;
	@ManyToOne
    @JoinColumn(name="bait_gene_id")
	private Gene baitGene;
	@ManyToOne
    @JoinColumn(name="prey_gene_id")
	private Gene preyGene;
	@Column(name="bait_upload_id")
	private String baitUploadId;
	@Column(name="prey_upload_id")
	private String preyUploadId;
	@Column(name="bait_nice_name")
	private String baitNiceName;
	@Column(name="prey_nice_name")
	private String preyNiceName;
	@ManyToOne
    @JoinColumn(name="classification_algorithm_id")
	private ClassificationAlgorithm classificationAlgorithm;
	@Column(name="ms_score")
	private Double msScore;
	@Column(name="classifier_score")
	private Double classifierScore;
	@Column(name="ms_p_value")
	private Double msPValue;
	@Column(name="classifier_p_value")
	private Double classifierPValue;
	@ManyToOne
    @JoinColumn(name="gene_pair_hash")
	@NotFound(action = NotFoundAction.IGNORE)
	private GenePair genePair;
	
	public UserResult() {}
	
	public Long getId() {
		return id;
	}
	
	public void setId(final Long id) {
		this.id = id;
	}
	
	public ClassificationAlgorithm getClassificationAlgorithm() {
		return classificationAlgorithm;
	}
	
	public void setClassificationAlgorithm(final ClassificationAlgorithm classificationAlgorithm) {
		this.classificationAlgorithm = classificationAlgorithm;
	}
	
	public String getBaitUploadId() {
		return baitUploadId;
	}
	
	public void setBaitUploadId(final String baitUploadId) {
		this.baitUploadId = baitUploadId;
	}
	
	public String getPreyUploadId() {
		return preyUploadId;
	}
	
	public void setPreyUploadId(final String preyUploadId) {
		this.preyUploadId = preyUploadId;
	}
	
	public Double getClassifierScore() {
		return classifierScore;
	}
	
	public void setClassifierScore(final Double classifierScore) {
		this.classifierScore = classifierScore;
	}
	
	public Double getClassifierPValue() {
		return classifierPValue;
	}
	
	public void setClassifierPValue(final Double classifierPValue) {
		this.classifierPValue = classifierPValue;
	}
	
	public GenePair getGenePair() {
		return genePair;
	}
	
	public void setGenePair(final GenePair genePair) {
		this.genePair = genePair;
	}
	
	public User getUser() {
		return user;
	}
	
	public void setUser(final User user) {
		this.user = user;
	}
	
	public Gene getBaitGene() {
		return baitGene;
	}
	
	public void setBaitGene(final Gene baitGene) {
		this.baitGene = baitGene;
	}
	
	public Gene getPreyGene() {
		return preyGene;
	}
	
	public void setPreyGene(final Gene preyGene) {
		this.preyGene = preyGene;
	}
	
	public Double getMsScore() {
		return msScore;
	}
	
	public void setMsScore(final Double msScore) {
		this.msScore = msScore;
	}
	
	public Double getMsPValue() {
		return msPValue;
	}
	
	public void setMsPValue(final Double msPValue) {
		this.msPValue = msPValue;
	}
	
	public String getBaitGeneName() {
		return baitGene == null ? "" : baitGene.getOfficialSymbol();
	}
	
	public String getPreyGeneName() {
		return preyGene == null ? "" : preyGene.getOfficialSymbol();
	}
	
	public String getBaitGeneIdasString() {
		return baitGene == null ? "" : baitGene.getId().toString();
	}
	
	public String getPreyGeneIdasString() {
		return preyGene == null ? "" : preyGene.getId().toString();
	}
	
	public String getBaitNiceName() {
		return baitNiceName;
	}
	
	public String getPreyNiceName() {
		return preyNiceName;
	}
	
	public void setBaitNiceName(String baitNiceName) {
		this.baitNiceName = baitNiceName;
	}
	
	public void setPreyNiceName(String preyNiceName) {
		this.preyNiceName = preyNiceName;
	}
	
	public String getBaitDisplayName() {
		return baitGene == null ? getBaitNiceName() : baitGene.getOfficialSymbol();
	}
	
	public String getPreyDisplayName() {
		return preyGene == null ? getPreyNiceName() : preyGene.getOfficialSymbol();
	}
}
