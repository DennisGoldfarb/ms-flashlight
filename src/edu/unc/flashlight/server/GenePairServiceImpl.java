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
package edu.unc.flashlight.server;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.gwt.json.client.JSONArray;

import edu.unc.flashlight.client.service.GenePairService;
import edu.unc.flashlight.server.dao.DaoCommand;
import edu.unc.flashlight.server.dao.DaoManager;
import edu.unc.flashlight.server.graph.OntologyNetwork;
import edu.unc.flashlight.server.util.HTTPRequestPoster;
import edu.unc.flashlight.shared.exception.FlashlightException;
import edu.unc.flashlight.shared.model.Gene;
import edu.unc.flashlight.shared.model.GeneInteraction;
import edu.unc.flashlight.shared.model.Pair;
import edu.unc.flashlight.shared.model.details.CoexpressionDetails;
import edu.unc.flashlight.shared.model.details.GeneInteractionDetails;
import edu.unc.flashlight.shared.model.details.InteractionDetails;
import edu.unc.flashlight.shared.model.details.OntologyDetails;
import edu.unc.flashlight.shared.util.Constants;

public class GenePairServiceImpl extends HibernateServlet implements GenePairService{
	private static final long serialVersionUID = 1L;
	

	public InteractionDetails getInteractionDetails(final long geneIda, final long geneIdb) throws FlashlightException {
		DaoCommand<InteractionDetails> daoCommand = new DaoCommand<InteractionDetails>() {
			public InteractionDetails execute(DaoManager<InteractionDetails> manager) {
				Set<Long> ids = new HashSet<Long>();
				ids.add(geneIda);
				ids.add(geneIdb);
				List<GeneInteraction> directInteractions = manager.getGeneInteractionDAO().getDirect(ids);
				for (GeneInteraction gi : directInteractions) {
					ids.add(gi.getGeneA().getId());
					ids.add(gi.getGeneB().getId());
				}
				List<GeneInteraction> network = manager.getGeneInteractionDAO().getNetwork(ids);
				Set<GeneInteractionDetails> networkDetails = new HashSet<GeneInteractionDetails>();
				
				for (GeneInteraction gi : network) {
					networkDetails.add(new GeneInteractionDetails(gi.getGeneA().getOfficialSymbol(),
							gi.getGeneB().getOfficialSymbol(), gi.getThroughput() == "L"));
				}
				
				List<GeneInteraction> interactions = manager.getGeneInteractionDAO().getGeneInteractions(geneIda, geneIdb);
				
				InteractionDetails result = new InteractionDetails();
				result.setNetwork(networkDetails);
				result.setInteractions(interactions);
				return result;
			}
		};
		InteractionDetails details = new DaoManager<InteractionDetails>(getSessionFactory()){}.execute(daoCommand);	
		return details;
	}
	
	public OntologyDetails getOntologyDetails(final long geneIDa, final long geneIDb, final String ontology_type_name) throws FlashlightException {
		DaoCommand<OntologyDetails> daoCommand = new DaoCommand<OntologyDetails>() {
			public OntologyDetails execute(DaoManager<OntologyDetails> manager) {		
				Gene g1 =  manager.getGeneDAO().getGeneWithAnnotations(geneIDa);
				Gene g2 =  manager.getGeneDAO().getGeneWithAnnotations(geneIDb);		
				List<Pair<String,String>> net = manager.getOntologyTermDAO().getOntologyNetworkForGenes(geneIDa,geneIDb,ontology_type_name);
				String json = OntologyNetwork.network2JSON(net,g1.getAnnotationsByType(ontology_type_name),g2.getAnnotationsByType(ontology_type_name));
				OntologyDetails details = new OntologyDetails(g1,g2,json);
				return details;
			}
		};
		OntologyDetails details = new DaoManager<OntologyDetails>(getSessionFactory()){}.execute(daoCommand);	
		return details;
	}

	public CoexpressionDetails generateCoexpressionImage(final long geneIDa,final long geneIDb, final long taxID) throws FlashlightException {
		if (taxID != 9606L) {
			DaoCommand<CoexpressionDetails> daoCommand = new DaoCommand<CoexpressionDetails>() {
				public CoexpressionDetails execute(DaoManager<CoexpressionDetails> manager) {		
						return manager.getGenePairDAO().getBestCoexpressionHomolog(geneIDa, geneIDb, taxID);
					}
			};
			CoexpressionDetails genes = new DaoManager<CoexpressionDetails>(getSessionFactory()){}.execute(daoCommand);	
			if (genes == null || genes.getGeneA() == null || genes.getGeneB() == null) return null;
			Long ida = genes.getGeneA().getId();
			Long idb = genes.getGeneB().getId();
			try {
				String response = HTTPRequestPoster.sendGetRequest(Constants.COXPRESDB_URL, "gene1=" + String.valueOf(ida) + "&gene2=" + String.valueOf(idb));
				if (response == null || !response.contains("img src")) genes.setHasImage(false);
			} catch (Exception e) {
				genes.setHasImage(false);
			} 
			return genes;
		} else {
			DaoCommand<CoexpressionDetails> daoCommand = new DaoCommand<CoexpressionDetails>() {
				public CoexpressionDetails execute(DaoManager<CoexpressionDetails> manager) {		
						return manager.getGenePairDAO().getBestCoexpressionHuman(geneIDa, geneIDb);
					}
			};
			CoexpressionDetails genes = new DaoManager<CoexpressionDetails>(getSessionFactory()){}.execute(daoCommand);
			String response = HTTPRequestPoster.sendGetRequest(Constants.COXPRESDB_URL, "gene1=" + String.valueOf(geneIDa) + "&gene2=" + String.valueOf(geneIDb));
			if (response.contains("[Error]") && response.contains("has no probes")) genes.setHasImage(false);
			return genes;
		}
	}
}
