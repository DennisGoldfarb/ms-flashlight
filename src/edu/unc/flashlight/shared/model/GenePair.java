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
		name = "GenePair.all",
		query = "from GenePair g"
	), 
	@NamedQuery(
		name = "GenePair.byHash",
		query = "from GenePair g where g.id = :genePairHash"
	),
})

@NamedNativeQueries({
	@NamedNativeQuery(
			name = "GenePair.getFeatureRanges",
			query = "select feature_min, feature_max " + 
					"from feature_stats " +
					"order by feature_index",
			resultSetMapping = "GenePair.getFeatureRanges"
	),
	@NamedNativeQuery(
			name = "GenePair.getFeatureBins",
			query = "select f.feature_index, sum(f.bin_count) as bin_count " + 
					"from feature_bins f " +
					"inner join experiments e on e.bait_gene_id = f.gene_id " +
					"where e.user_id = :user_id " +
					"group by feature_index, bin_index " +
					"order by feature_index, bin_index",
			resultSetMapping = "GenePair.getFeatureBins"
	),
	@NamedNativeQuery(
		name = "GenePair.getScores",
		query = "select t.id, gp.bp_score, gp.cc_score, gp.coxp_human_score, gp.coxp_mouse_score, " + 
				"gp.coxp_worm_score, gp.coxp_chicken_score, gp.coxp_rat_score, gp.coxp_fish_score, " + 
				"gp.coxp_monkey_score, gp.domain_score, gp.homo_int_score " +
				"from gene_pairs_imputed gp " +
				"right join hash_temp t on t.id = gp.gene_pair_hash ",// +
				//"order by t.id",
		resultSetMapping = "GenePair.getScores.mapping"
	),
	@NamedNativeQuery(
		name = "getBestCoexpressionHomolog",
		query = "select c.gene_id_a, c.gene_id_b, c.PCC from coxpress c, " +
				"(select gene_id, group_id from homologene where gene_id = :gene_id_a) as hg1, " +
				"(select gene_id, group_id from homologene where gene_id = :gene_id_b) as hg2, " +
				"(select gene_id, group_id from homologene where tax_id = :tax_id) as h1, " +
				"(select gene_id, group_id from homologene where tax_id = :tax_id) as h2 " +
				"where c.gene_id_a = h1.gene_id and hg1.group_id = h1.group_id " +
				"and c.gene_id_b = h2.gene_id and hg2.group_id = h2.group_id " +
				"order by c.PCC desc",
	    resultSetMapping = "getBestCoexpressionHomolog.mapping"
	),
	@NamedNativeQuery(
		name = "getBestCoexpressionHuman",
		query = "select c.gene_id_a, c.gene_id_b, c.PCC " +
				"from coxpress c " +
				"where c.gene_id_a = :gene_id_a and c.gene_id_b = :gene_id_b " +
				"order by c.PCC desc",
	    resultSetMapping = "getBestCoexpressionHuman.mapping"
	)
})

@SqlResultSetMappings({
	@SqlResultSetMapping(
		name="GenePair.getFeatureRanges", 
		columns={
	        @ColumnResult(name="feature_min"),
	        @ColumnResult(name="feature_max")
	    }
	),
	@SqlResultSetMapping(
		name="GenePair.getFeatureBins", 
		columns={
	        @ColumnResult(name="f.feature_index"),
	        @ColumnResult(name="bin_count")
	    }
	),
	@SqlResultSetMapping(
		name="GenePair.getScores.mapping", 
		columns={
			@ColumnResult(name="t.id"),
	        @ColumnResult(name="gp.bp_score"),
	        @ColumnResult(name="gp.cc_score"),
	        @ColumnResult(name="gp.coxp_human_score"),
	        @ColumnResult(name="gp.coxp_mouse_score"),
	        @ColumnResult(name="gp.coxp_worm_score"),
	        @ColumnResult(name="gp.coxp_chicken_score"),
	        @ColumnResult(name="gp.coxp_rat_score"),
	        @ColumnResult(name="gp.coxp_fish_score"),
	        @ColumnResult(name="gp.coxp_monkey_score"),
	        @ColumnResult(name="gp.domain_score"),
	        @ColumnResult(name="gp.homo_int_score")
	    }
	),
	@SqlResultSetMapping(
		name="getBestCoexpressionHomolog.mapping", 
		columns={
	        @ColumnResult(name="c.gene_id_a"),
	        @ColumnResult(name="c.gene_id_b"),
	        @ColumnResult(name="c.PCC")
	    }
	),
	@SqlResultSetMapping(
		name="getBestCoexpressionHuman.mapping", 
		columns={
	        @ColumnResult(name="c.PCC")
	    }
	)
})

@Entity
@Table(name="gene_pairs")
public class GenePair extends LightEntity implements Serializable {
	private static final long serialVersionUID = 1L;
	/** BP, CC, MR_human, MR_mouse, MR_worm, MR_chicken, MR_rat, MR_fish, MR_monkey, domain, homo_int, intercept **/
	@Id
	@Column(name="gene_pair_hash")
	private String id;
	@Column(name="bp_score")
	private Double bpScore;
	@Column(name="cc_score")
	private Double ccScore;
	@Column(name="coxp_human_score")
	private Double coxpHumanScore;
	@Column(name="coxp_monkey_score")
	private Double coxpMonkeyScore;
	@Column(name="coxp_worm_score")
	private Double coxpWormScore;
	@Column(name="coxp_chicken_score")
	private Double coxpChickenScore;
	@Column(name="coxp_dog_score")
	private Double coxpDogScore;
	@Column(name="coxp_mouse_score")
	private Double coxpMouseScore;
	@Column(name="coxp_rat_score")
	private Double coxpRatScore;
	@Column(name="coxp_fish_score")
	private Double coxpFishScore;
	@Column(name="domain_score")
	private Double domainScore;
	@Column(name="homo_int_score")
	private Double humanIntScore;
	@Column(name="human_phen_score")
	private Double humanPhenScore;
	@Column(name="mouse_phen_score")
	private Double mousePhenScore;
	@Column(name="disease_score")
	private Double diseaseScore;
	@Column(name="is_known")
	private String isKnown;


	public GenePair() {

	}
	
	public GenePair(String genePairHash) {
		setId(genePairHash);
	}
	
	public void setId(final String id) {
		this.id = id;
	}
	
	public String getId() {
		return id;
	}

	public void setBpScore(final Double bpScore) {
		this.bpScore = bpScore;
	}

	public Double getBpScore() {
		return bpScore;
	}

	public void setCcScore(final Double ccScore) {
		this.ccScore = ccScore;
	}

	public Double getCcScore() {
		return ccScore;
	}

	public void setCoxpHumanScore(final Double coxpHumanScore) {
		this.coxpHumanScore = coxpHumanScore;
	}

	public Double getCoxpHumanScore() {
		return coxpHumanScore;
	}

	public void setCoxpMouseScore(final Double coxpMouseScore) {
		this.coxpMouseScore = coxpMouseScore;
	}

	public Double getCoxpMouseScore() {
		return coxpMouseScore;
	}

	public void setCoxpRatScore(final Double coxpRatScore) {
		this.coxpRatScore = coxpRatScore;
	}

	public Double getCoxpRatScore() {
		return coxpRatScore;
	}

	public void setCoxpDogScore(final Double coxpDogScore) {
		this.coxpDogScore = coxpDogScore;
	}

	public Double getCoxpDogScore() {
		return coxpDogScore;
	}

	public void setCoxpFishScore(final Double coxpFishScore) {
		this.coxpFishScore = coxpFishScore;
	}

	public Double getCoxpFishScore() {
		return coxpFishScore;
	}

	public void setCoxpWormScore(final Double coxpWormScore) {
		this.coxpWormScore = coxpWormScore;
	}

	public Double getCoxpWormScore() {
		return coxpWormScore;
	}
	
	public void setCoxpChickenScore(final Double coxpChickenScore) {
		this.coxpChickenScore = coxpChickenScore;
	}

	public Double getCoxpChickenScore() {
		return coxpChickenScore;
	}
	
	public void setCoxpMonkeyScore(final Double coxpMonkeyScore) {
		this.coxpMonkeyScore = coxpMonkeyScore;
	}

	public Double getCoxpMonkeyScore() {
		return coxpMonkeyScore;
	}

	public void setDomainScore(final Double domainScore) {
		this.domainScore = domainScore;
	}

	public Double getDomainScore() {
		return domainScore;
	}

	public void setHumanIntScore(final Double humanIntScore) {
		this.humanIntScore = humanIntScore;
	}

	public Double getHumanIntScore() {
		return humanIntScore;
	}

	public void setHumanPhenScore(final Double humanPhenScore) {
		this.humanPhenScore = humanPhenScore;
	}

	public Double getHumanPhenScore() {
		return humanPhenScore;
	}

	public void setMousePhenScore(final Double mousePhenScore) {
		this.mousePhenScore = mousePhenScore;
	}

	public Double getMousePhenScore() {
		return mousePhenScore;
	}
	
	public void setDiseaseScore(final Double diseaseScore) {
		this.diseaseScore = diseaseScore;
	}

	public Double getDiseaseScore() {
		return diseaseScore;
	}

	public void setIsKnown(String isKnown) {
		this.isKnown = isKnown;
	}

	public String getIsKnown() {
		return isKnown;
	}
}
