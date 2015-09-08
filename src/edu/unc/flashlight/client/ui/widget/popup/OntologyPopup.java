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
package edu.unc.flashlight.client.ui.widget.popup;

import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;

import edu.unc.flashlight.client.Flashlight;
import edu.unc.flashlight.client.command.ServerOp;
import edu.unc.flashlight.client.ui.widget.OntologyView;
import edu.unc.flashlight.shared.model.Gene;
import edu.unc.flashlight.shared.model.details.OntologyDetails;

public class OntologyPopup extends DraggablePopup {
	private OntologyView cx_panel;
	long ida;
	long idb;
	final String ontology_type_name;
	
	public OntologyPopup(Gene gene_a, Gene gene_b, String abr, String ontology_type_name) {
		super(abr + "<br>" + gene_a.getOfficialSymbol() + " vs. " + gene_b.getOfficialSymbol());
		this.addStyleDependentName("feature");
		this.ida = gene_a.getId();
		this.idb = gene_b.getId();
		this.ontology_type_name = ontology_type_name;
	}
	
	public void showPopup() {
		setContentWidget(new Image("images/ajax-loader.gif"));
		center(true);
		new ServerOp<OntologyDetails>() {
			public void onSuccess(final OntologyDetails result) {	
				setContentWidget(new OntologyView(result, ontology_type_name));
				center(false);
			}

			public void onFailure(Throwable e) {
				hide();
				super.onFailure(e);
			}

			public void begin() {
				Flashlight.genePairService.getOntologyDetails(ida, idb, ontology_type_name, this);
			}
		}.begin();
	}
}

