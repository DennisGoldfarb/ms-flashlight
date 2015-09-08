package edu.unc.flashlight.shared.model.upload;

import java.util.Map;
import java.util.Set;

public class DataForScoring {
	public Map<Long,String> exp2bait;
	public Map<Long,Map<String,Integer>> exp2prey2sc;
	public Map<Long,String> c_exp2bait;
	public Map<Long,Map<String,Integer>> c_exp2prey2sc;
	public Map<String,Integer> prey2length;
	public Set<Long> expsForUser;
	
	public DataForScoring(Map<Long,String> exp2bait, Map<Long,Map<String,Integer>> exp2prey2sc, Map<Long,String> c_exp2bait,
			Map<Long,Map<String,Integer>> c_exp2prey2sc, Map<String,Integer> prey2length, Set<Long> expsForUser){
		this.exp2bait = exp2bait;
		this.exp2prey2sc = exp2prey2sc;
		this.c_exp2bait = c_exp2bait;
		this.c_exp2prey2sc = c_exp2prey2sc;
		this.prey2length = prey2length;
		this.expsForUser = expsForUser;
	}
}
