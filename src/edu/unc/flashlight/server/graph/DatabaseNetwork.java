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
package edu.unc.flashlight.server.graph;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jgrapht.GraphPath;
import org.jgrapht.UndirectedGraph;
import org.jgrapht.alg.DijkstraShortestPath;
import org.jgrapht.alg.FloydWarshallShortestPaths;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;

import edu.unc.flashlight.server.dao.GeneDAO;
import edu.unc.flashlight.shared.model.Gene;
import edu.unc.flashlight.shared.model.GeneInteraction;
import edu.unc.flashlight.shared.model.details.GeneInteractionDetails;

public class DatabaseNetwork {
/*	private static Set<Long> PTM_PROTEINS = new HashSet<Long>(Arrays.asList(new Long[] {7316L, 10054L, 9636L, 4738L, 7314L}));
	private UndirectedGraph<Long, DefaultEdge> PPI_Network;
	private Map<Long,Map<Long,Integer>> network_distances;
	private Map<Long,Map<Long,List<DefaultEdge>>> shortest_paths;
	
	public DatabaseNetwork() {
		PPI_Network = new SimpleGraph<Long, DefaultEdge>(DefaultEdge.class);
		network_distances = new HashMap<Long, Map<Long,Integer>>();
		shortest_paths = new HashMap<Long, Map<Long,List<DefaultEdge>>>();
	}
	
	public DatabaseNetwork(Collection<GeneInteraction> interactions) {
		this();
		addInteractions(interactions);
	}
	
	public boolean containsEdge(long id_a, long id_b) {
		return PPI_Network.containsEdge(id_a, id_b);
	}
	
	public List<DefaultEdge> getUniqunessNetwork(Collection<Long> ids, GeneDAO geneDAO) {
		List<DefaultEdge> network_path = new ArrayList<DefaultEdge>();
		for (long id: ids) {
			for (long id2: ids) {
				List<DefaultEdge> path = getShortestPath(id, id2);
				network_path.addAll(path);
			}
		}
		return network_path;
	}
	
	public Set<GeneInteractionDetails> getNetwork(Collection<Long> ids, Boolean isLow, GeneDAO geneDAO) {
		Set<GeneInteractionDetails> interactions = new HashSet<GeneInteractionDetails>();
		Set<Long> nbrs = new HashSet<Long>();
		for (long id : ids) {
			Gene g1 = geneDAO.getGene(id);
			for (long nbr : getNeighbors(id)) {
				if (id != nbr) {
					Gene g2 = geneDAO.getGene(nbr);
					interactions.add(new GeneInteractionDetails(g1.getOfficialSymbol(), g2.getOfficialSymbol(), isLow));
					nbrs.add(nbr);
				}
			}
		}	
		
		for (long id : nbrs) {
			Gene g1 = geneDAO.getGene(id);
			for (long id2 : nbrs) {
				Gene g2 = geneDAO.getGene(id2);
				if (id != id2 && PPI_Network.containsEdge(id, id2)) {
					interactions.add(new GeneInteractionDetails(g1.getOfficialSymbol(), g2.getOfficialSymbol(), isLow));
				}
			}
		}
		
		return interactions;
	}
	
	public Set<Long> getNeighbors(Long id) {
		Set<Long> neighbors = new HashSet<Long>();
		if (PPI_Network.containsVertex(id)) {
			for(DefaultEdge e : PPI_Network.edgesOf(id)){
			       neighbors.add(PPI_Network.getEdgeTarget(e));
			}
		}
		return neighbors;
	}
	
	public void addInteractions(Collection<GeneInteraction> interactions) {
		for (GeneInteraction gi : interactions) {
			Long gene_id_a = gi.getGeneA().getId();
			if (PTM_PROTEINS.contains(gene_id_a)) continue;
			Long gene_id_b = gi.getGeneB().getId();
			if (PTM_PROTEINS.contains(gene_id_b)) continue;
			
			if (!PPI_Network.containsVertex(gene_id_a)) PPI_Network.addVertex(gene_id_a);
			if (!PPI_Network.containsVertex(gene_id_b)) PPI_Network.addVertex(gene_id_b);
			if (gene_id_a != gene_id_b) {
				PPI_Network.addEdge(gi.getGeneA().getId(), gi.getGeneB().getId());
			}
		}	
		//shortest_paths = new FloydWarshallShortestPaths<Long,DefaultEdge>(PPI_Network);
	}
	
	public List<DefaultEdge> getShortestPath(Long gene_id_a, Long gene_id_b) {
		try {
		if (!shortest_paths.containsKey(gene_id_a)) shortest_paths.put(gene_id_a, new HashMap<Long,List<DefaultEdge>>());
		if (shortest_paths.get(gene_id_a).containsKey(gene_id_b)) return shortest_paths.get(gene_id_a).get(gene_id_b);
		if (PPI_Network.containsVertex(gene_id_a) && PPI_Network.containsVertex(gene_id_b)) {
			List<DefaultEdge> path =  DijkstraShortestPath.findPathBetween(PPI_Network, gene_id_a, gene_id_b);
			if (path == null) path = new ArrayList<DefaultEdge>();
			shortest_paths.get(gene_id_a).put(gene_id_b, path);
			return path;
		}
		shortest_paths.get(gene_id_a).put(gene_id_b, new ArrayList<DefaultEdge>());
		} catch (Exception e) {
			return shortest_paths.get(gene_id_a).get(gene_id_b);
		}
		return shortest_paths.get(gene_id_a).get(gene_id_b);
	}
	
	public int getShortestPathLength(int diameter, Long gene_id_a, Long gene_id_b) {
		try {
		if (!network_distances.containsKey(gene_id_a)) network_distances.put(gene_id_a, new HashMap<Long,Integer>());
		if (!network_distances.containsKey(gene_id_b)) network_distances.put(gene_id_b, new HashMap<Long,Integer>());
		int len = diameter;
		if (!network_distances.get(gene_id_a).containsKey(gene_id_b)) {
			if (PPI_Network.containsVertex(gene_id_a) && PPI_Network.containsVertex(gene_id_b)) {
				List<DefaultEdge> path = DijkstraShortestPath.findPathBetween(PPI_Network, gene_id_a, gene_id_b);
				if (path != null) {
					len = path.size();
					for (int i = 0; i < path.size(); i++) {
						DefaultEdge e = path.get(i);
						String[] edge = e.toString().replaceAll("\\(|\\)", "").split(" : ");
						Long source = Long.parseLong(edge[0]);
						Long sink = Long.parseLong(edge[1]);
						if (!network_distances.containsKey(source)) network_distances.put(source, new HashMap<Long,Integer>());
						if (!network_distances.containsKey(sink)) network_distances.put(sink, new HashMap<Long,Integer>());
						
						Long lastSource = source;
						Long lastSink = sink;
						for (int j = i; j < path.size(); j++) {
							DefaultEdge e2 = path.get(j);
							String[] edge2 = e2.toString().replaceAll("\\(|\\)", "").split(" : ");
							Long source2 = Long.parseLong(edge2[0]);
							Long sink2 = Long.parseLong(edge2[1]);
							if (!network_distances.containsKey(source2)) network_distances.put(source2, new HashMap<Long,Integer>());
							if (!network_distances.containsKey(sink2)) network_distances.put(sink2, new HashMap<Long,Integer>());
							
							Long linker;
							if (source2 == lastSource || source2 == lastSink) linker = source2;
							else if (sink2 == lastSource || sink2 == lastSink) linker = sink2;
							
							if (source != )
							
							network_distances.get(source).put(sink2, j-i+1);
							network_distances.get(sink2).put(source, j-i+1);
						}
					}
				}
			}
			network_distances.get(gene_id_a).put(gene_id_b, len);
			network_distances.get(gene_id_b).put(gene_id_a, len);
		} 
		return network_distances.get(gene_id_a).get(gene_id_b);
		} catch (Exception e) {
			return 0;
		}
	}
	
	public double calcMSPL(int diameter, long bait, Object[] ids) {
		int path_length = 0;
		for (int i = 0; i < ids.length; i++) {
			if (bait != (Long)ids[i]) {
				path_length += getShortestPathLength(diameter, bait, (Long)ids[i]);
			}
		}
		if (ids.length == 1) return 0;
		return path_length/(double)(ids.length-1);
	}
	
	public double calcMSPL(int diameter, Object[] ids) {
		try {
		double count = (ids.length * (ids.length - 1))/2D;
		if (count == 0) count = 1;
		int path_length = 0;
		Set<DefaultEdge> edges = new HashSet<DefaultEdge>();
		for (int i = 0; i < ids.length; i++) {
			for (int j = i+1; j < ids.length; j++) {
				long min_id = Math.min((Long)ids[i], (Long)ids[j]);
				long max_id = Math.max((Long)ids[i], (Long)ids[j]);
				try {
//				List<DefaultEdge> path = getShortestPath(min_id,max_id);
//				if (path == null) {
//					int blah = 1;
//				}
				path_length += getShortestPathLength(diameter,min_id,max_id);
//				GraphPath<Long, DefaultEdge> path = shortest_paths.getShortestPath(min_id, max_id);
//				for (DefaultEdge e : path) {
//					edges.add(e);
//				}
				} catch (Exception e) {
					int blah = 1;
				}
			}
		}
		return path_length / count;
//		return edges.size() + ids.length;
		} catch (Exception e) {
			return 0;
		}
	}*/
}
