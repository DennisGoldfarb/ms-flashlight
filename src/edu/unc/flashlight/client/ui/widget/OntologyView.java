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
package edu.unc.flashlight.client.ui.widget;

import java.util.HashSet;
import java.util.Set;

import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import edu.unc.flashlight.client.ui.widget.details.GeneSymbolDetails;
import edu.unc.flashlight.client.ui.widget.details.OntologyTermDetails;
import edu.unc.flashlight.shared.model.GeneAnnotation;
import edu.unc.flashlight.shared.model.details.OntologyDetails;

public class OntologyView extends SimplePanel {

	public OntologyView(final OntologyDetails result, final String ontology_type_name) {
		setWidget(createWidget(result, ontology_type_name));
	}

	private Widget createWidget(final OntologyDetails result, final String ontology_type_name) {
		HorizontalPanel hp = new HorizontalPanel();
		final SimplePanel chartPanel = new SimplePanel();	
		//JSONObject data = new JSONObject(result.getNetwork());

		Set<GeneAnnotation> annotations1 = result.getGeneA().getAnnotationsByType(ontology_type_name);
		Set<GeneAnnotation> annotations2 = result.getGeneB().getAnnotationsByType(ontology_type_name);

		GeneSymbolDetails geneDetailsA = new GeneSymbolDetails(result.getGeneA());
		GeneSymbolDetails geneDetailsB = new GeneSymbolDetails(result.getGeneB());
		
		VerticalPanel vp1 = setupVerticalPanel(annotations1, "left",  geneDetailsA);
		VerticalPanel vp2 = setupVerticalPanel(annotations2, "right",  geneDetailsB);
		
		hp.add(vp1);
		hp.add(chartPanel);
		hp.add(vp2);
		hp.setSpacing(7);

		return hp;
	}
	
	private static native void setupD3(JSONValue json) /*-{
    	myJSfunction();
	}-*/;
	
	private VerticalPanel setupVerticalPanel(Set<GeneAnnotation> annotations, String position, GeneSymbolDetails geneDetails) {
		VerticalPanel vp = new VerticalPanel();
		vp.add(geneDetails);
		vp.setSpacing(3);
		vp.setCellHorizontalAlignment(geneDetails, HasHorizontalAlignment.ALIGN_CENTER);
		Set<String> annotationIds = new HashSet<String>();
		for (GeneAnnotation t : annotations) {
			if (!annotationIds.contains(t.getOntologyTerm().getId())) {
				annotationIds.add(t.getOntologyTerm().getId());
				OntologyTermDetails d = new OntologyTermDetails(t);
				d.addStyleDependentName(position);
				vp.add(d);
			}
		}
		vp.addStyleName("ontologyTermList");
		vp.addStyleDependentName("ontologyPopup");
		vp.getWidget(vp.getWidgetCount()-1).addStyleDependentName("last");
		return vp;
	}
}
