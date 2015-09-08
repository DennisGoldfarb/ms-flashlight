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
package edu.unc.flashlight.shared.util;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import edu.unc.flashlight.shared.model.Pair;

public class Conversion {
	
	public static String generateCoxpresURL(String ida, String idb) {
		return Constants.COXPRESDB_URL + "?gene1=" + ida + "&gene2=" + idb;
	}
	
	public static String generateCoxpresImgURL(String ida, String idb) {
		return Constants.COXPRESDB_IMG_URL + ida + "/" + idb + "/" + ida + "_" + idb + ".png";
	}
	
	public static String array2csv(Object[] vals, String sep) {
		String returnVal = "";
		for (int i = 0; i < vals.length-1; i++) {
			returnVal += vals[i].toString() + sep;
		}
		returnVal += vals[vals.length-1];
		return returnVal;
	}
	
	public static String collection2csv(Collection<?> vals, String sep) {
		String returnVal = "";
		Iterator<?> itr = vals.iterator();
		while (itr.hasNext()) {
			returnVal += itr.next().toString();
			if (itr.hasNext()) returnVal += sep + " ";
		}
		return returnVal;
	}
	
	public static String collection2csv(Collection<?> vals) {
		return collection2csv(vals,",");
	}
	
	public static String doGenePairHash(String s1, String s2) {
		if (s1.compareTo(s2) > 0) return s1 + "_" + s2;
		return s2 + "_" + s1;
	}
	
	public static String doGenePairHash(Long s1, Long s2) {
		return doGenePairHash(s1.toString(),s2.toString());
	}
	
	public static Pair<Long,Long> undoGenePairHash(String hash) {
		String vals[] = hash.split("_");
		return new Pair<Long,Long>(Long.parseLong(vals[0]),Long.parseLong(vals[1]));
	}
	
	public static double logM(int x, int base) {
		return Math.log10(x)/Math.log(base);
	}
	
	public static double logM(double x, double base) {
		return Math.log(x)/Math.log(base);
	}
	
	public static double logM(double x, int base) {
		return Math.log(x)/Math.log(base);
	}
	
	public static double logM(int x, double base) {
		return Math.log(x)/Math.log(base);
	}
	
	public static String bool2string(boolean b) {
		return b ? "1" : "0";
	}
	
	public static int bool2int(boolean b) {
		return b ? 1 : 0;
	}
	
	public static <V, K> Map<V, Set<K>> invertMap(Map<K, V> map) {
	    Map<V, Set<K>> inv = new HashMap<V, Set<K>>();

	    for (Entry<K, V> entry : map.entrySet()) {
	    	if (!inv.containsKey(entry.getValue())) {
	    		inv.put(entry.getValue(), new HashSet<K>());
	    	}
	    	inv.get(entry.getValue()).add(entry.getKey());
	    }   
	    
	    return inv;
	}
	
	public static <V, K> Map<V, K> invertOne2OneMap(Map<K, V> map) {
	    Map<V, K> inv = new HashMap<V, K>();

	    for (Entry<K, V> entry : map.entrySet()) {
	    	inv.put(entry.getValue(),entry.getKey());
	    }   
	    
	    return inv;
	}
	
	public static boolean isInt(String s) {
		try {
			Integer.parseInt(s);
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	
	public static <T> int binaryInsertionSearch(List<? extends Comparable<? super T>> list, T key) {
		int index;
		if (list.get(list.size()-1).compareTo(key) <= 0) {
			index = list.size()-1;
		} else if (list.get(0).compareTo(key) > 0) {
			index = 0;
		} else {
			index = Collections.binarySearch(list, key);
			while (index>0 && list.get(index).compareTo(key) >= 0) {
				index--;
			}
			if (index < 0) {
				index = -index-1;
			}
		}
		return index;
	}
}
