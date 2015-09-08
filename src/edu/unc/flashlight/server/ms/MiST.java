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
package edu.unc.flashlight.server.ms;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import edu.unc.flashlight.shared.GenericCommand;
import edu.unc.flashlight.shared.model.Pair;
import edu.unc.flashlight.shared.util.Conversion;

public class MiST { /*extends MassSpecScorer {
	private Map<Long,Map<Long,Set<Long>>> ap2bait2exp;
	private Map<Long,Map<Long,Double>> exp2prey2quant;
	private Map<Long,Integer> prey2length;
	private Double[][] matrix;
	
	public MiST(Map<Long, Long> exp2bait,Map<Long, Map<Long, Integer>> exp2prey2sc, Map<Long,Integer> prey2length,
			Map<Long, Set<Long>> ap2exp) {
		super(exp2bait, exp2prey2sc, ap2exp);
		ap2bait2exp = new HashMap<Long,Map<Long,Set<Long>>>();
		exp2prey2quant = new HashMap<Long,Map<Long,Double>>();
		this.prey2length = prey2length;
	}

	public Map<String, Double> calculateScores(GenericCommand<Double> updateProgress) {
		formatData();
		scoreData();
		normalizeData();
		return pair2score;
	}
	
	private void formatData() {
		for (Long ap : ap2exp.keySet()){
			ap2bait2exp.put(ap,new HashMap<Long,Set<Long>>());
			ap2pair2score.put(ap, new HashMap<String,Double>());
			Map<Long,Set<Long>> bait2exp = ap2bait2exp.get(ap);
			for (Long exp : ap2exp.get(ap)) {
				Long bait = exp2bait.get(exp);
				if (!bait2exp.containsKey(bait)) bait2exp.put(bait, new HashSet<Long>());
				bait2exp.get(bait).add(exp);
				exp2prey2quant.put(exp, new HashMap<Long,Double>());
				Map<Long,Integer> prey2sc = exp2prey2sc.get(exp);
				double sum = 0;
				for (Long prey : prey2sc.keySet()) sum += prey2sc.get(prey);
				double sum2 = 0;
				for (Long prey : prey2sc.keySet()) {
					if (!prey2length.containsKey(prey)) prey2length.put(prey, 355);
					exp2prey2quant.get(exp).put(prey, prey2sc.get(prey)/(sum*prey2length.get(prey)));
					sum2 += exp2prey2quant.get(exp).get(prey);
				}
				for (Long prey : prey2sc.keySet()) {
					exp2prey2quant.get(exp).put(prey, exp2prey2quant.get(exp).get(prey)/sum2);
					String hash = Conversion.doGenePairHash(bait, prey);
					if (!pair2score.containsKey(hash) && !prey.equals(bait)) pair2score.put(hash, null);
					if (!ap2pair2score.get(ap).containsKey(hash) && !prey.equals(bait)) ap2pair2score.get(ap).put(hash, 0D);
				}
			}
		}
	}
	
	private boolean hasPrey(Long bait, Long prey, Long ap) {
		for (Long exp : ap2bait2exp.get(ap).get(bait)) {
			if (exp2prey2quant.get(exp).containsKey(prey)) return true;
		}
		return false;
	}
	
	private void scoreData() {
		for (Long ap : ap2pair2score.keySet()) {
			matrix = new Double[ap2pair2score.get(ap).keySet().size()][3];
			Object[] hashes = ap2pair2score.get(ap).keySet().toArray();
			Map<String, Double> ap_pair2score = ap2pair2score.get(ap);
			for (int i = 0; i < hashes.length; i+=1) {
				String hash = hashes[i].toString();
				Pair<Long,Long> ids = Conversion.undoGenePairHash(hash);
				Long g1 = ids.getFirst();
				Long g2 = ids.getSecond();
				double spec1 = Double.NEGATIVE_INFINITY, spec2 = spec1, abun1 = spec1, abun2 = spec1, repro1 = spec1, repro2 = spec1;
				if (ap2bait2exp.get(ap).containsKey(g1) && hasPrey(g1,g2,ap)) {
					spec1 = specificity(g2,g1,ap);
					abun1 = abundance(g2,g1,ap);
					repro1 = reproducibility(g2,g1,ap);
				}
				if (ap2bait2exp.get(ap).containsKey(g2) && hasPrey(g2,g1,ap)) {
					spec2 = specificity(g1,g2,ap);
					abun2 = abundance(g1,g2,ap);
					repro2 = reproducibility(g1,g2,ap);
				}
				matrix[i][0] = Math.max(spec1,spec2);
				matrix[i][1] = Math.max(abun1,abun2);
				matrix[i][2] = Math.max(repro1,repro2);
			}
			//matrix = new Double[4][2];
			//matrix[0][0] = 2D;
			//matrix[0][1] = 1D;
			//matrix[1][0] = 2D;
			//matrix[1][1] = 3D;
			//matrix[2][0] = 3D;
			//matrix[2][1] = 5D;
			//matrix[3][0] = 4D;
			//matrix[3][1] = 7D;
			try {
				FileWriter fstream = new FileWriter("lookatme.csv", false);
				BufferedWriter out = new BufferedWriter(fstream);	
				for (int i = 0; i < matrix.length; i++) {
					out.write(matrix[i][0].toString() +","+matrix[i][1].toString()+","+matrix[i][2].toString()+"\n");
				}
				out.close();
			
			} catch (Exception e) {
				
			}

			matrix = norm_matrix(matrix);
			Double[] pca_vec = pca(matrix);
			for (int i = 0; i < hashes.length; i+=1) {
				String hash = hashes[i].toString();
				ap_pair2score.put(hash, (matrix[i][0]*pca_vec[0]) +  (matrix[i][1]*pca_vec[1]) + (matrix[i][1]*pca_vec[1]));
			}
		}
	}
	
	private Double[][] norm_matrix(Double[][] matrix) {
		for (int i = 0; i < matrix[0].length; i++) {
			double avg = 0;
			for (int j = 0; j < matrix.length; j++) {
				avg += matrix[j][i];
			}
			avg /= matrix.length;
			for (int j = 0; j < matrix.length; j++) {
				matrix[j][i] -= avg;
			}
		}
		return matrix;
	}
	
	private double mag(Double[] v) {
		double sum = 0;
		for (int i = 0; i < v.length; i++) {
			sum += Math.pow(Math.abs(v[i]),2);
		}
		return Math.sqrt(sum);
	}
	
	private double dot(Double[] v1, Double[] v2) {
		double sum = 0;
		for (int i = 0; i < v1.length; i++) {
			sum += v1[i] * v2[i];
		}
		return sum;
	}
	
	private Double[] vec_mult(Double[] v, double c) {
		Double[] ret = new Double[v.length];
		for (int i = 0; i < v.length; i++) {
			ret[i] = c*v[i];
		}
		return ret;
	}
	
	private Double[] vec_sum(Double[] v1, Double[] v2) {
		Double[] ret = new Double[v1.length];
		for (int i = 0; i < v1.length; i++) {
			ret[i] = v1[i] + v2[i];
		}
		return ret;
	}
	
	private Double[] pca(Double[][] matrix) {
		Double[] p = {Math.random(), Math.random(),Math.random()};
		for (int i = 0; i < 10; i++) {
			Double[] t = {0D,0D,0D};
			for (int j = 0; j < matrix.length; j++) {
				t = vec_sum(t,vec_mult(matrix[j],dot(matrix[j],p)));
			}
			p = vec_mult(t,1/mag(t));
		}
		return p;
	}
	
	private double specificity(Long prey, Long bait, Long ap) {
		double Abp = abundance(prey,bait,ap);
		double Ap = abundance(prey, ap);
		return Abp/Ap;
	}
	
	private double reproducibility(Long prey, Long bait, Long ap) {
		double num = 0;
		for (Long exp : ap2bait2exp.get(ap).get(bait)) {
			if (exp2prey2quant.get(exp).containsKey(prey)) num += exp2prey2quant.get(exp).get(prey) + Math.log(exp2prey2quant.get(exp).get(prey));
		}
		return num * Conversion.logM(ap2bait2exp.get(ap).get(bait).size(),2);
	}
	
	private double abundance(Long prey, Long ap) {
		double num = 0;
		for (Long bait : ap2bait2exp.get(ap).keySet()) {
			num += abundance(prey,bait,ap);
		}
		return num;
	}
	
	private double abundance(Long prey, Long bait, Long ap) {
		double num = 0;
		for (Long exp : ap2bait2exp.get(ap).get(bait)) {
			if (exp2prey2quant.get(exp).containsKey(prey)) num += exp2prey2quant.get(exp).get(prey);
		}
		return num / ap2bait2exp.get(ap).get(bait).size();
	}
	
*/

}
