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
package edu.unc.flashlight.client.ui.widget.details;

import java.util.Collection;
import java.util.Iterator;

import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;

import edu.unc.flashlight.shared.model.Gene;
import edu.unc.flashlight.shared.model.Sequence;
import edu.unc.flashlight.shared.util.Conversion;

public class GeneDetails extends SimplePanel {
	private Gene g;
	private GeneSymbolDetails geneSymbolDetails;
	
	public GeneDetails(Gene g) {
		this.g = g;
		geneSymbolDetails = new GeneSymbolDetails(g);
		initWidget();
	}
	
	private void initWidget() {
		this.setStyleName("geneDetails");
		VerticalPanel vp = new VerticalPanel();
		vp.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		vp.add(geneSymbolDetails);
		vp.setSpacing(3);
		
		FlexTable ft = new FlexTable();
		ft.setHTML(0, 0, "<span>Gene ID</span>");
		ft.setHTML(0, 1, "<span>" + g.getId().toString() + "</span>");
		ft.setHTML(0, 2, "<span>Species</span>");
		ft.setHTML(0, 3, "<span>" + g.getTaxonomy().getName() + "</span>");
		ft.getCellFormatter().addStyleName(0, 0, "bold");
		ft.getCellFormatter().addStyleName(0, 2, "bold");
		
		ft.setHTML(1, 0, "<span>Chromosome</span>");
		ft.setHTML(1, 1, "<span>" + g.getChromosome() + "</span>");
		ft.setHTML(1, 2, "<span>Gene Type</span>");
		ft.setHTML(1, 3, "<span>" + g.getGeneType().getName() + "</span>");
		ft.getCellFormatter().addStyleName(1, 0, "bold");
		ft.getCellFormatter().addStyleName(1, 2, "bold");
		
		ft.setHTML(2, 0, "<span>Description</span>");
		ft.setHTML(2, 1, "<span>" + g.getDescription() + "</span>");
		ft.getFlexCellFormatter().setColSpan(2, 1, 3);
		ft.getCellFormatter().setWordWrap(2, 1, true);
		ft.getCellFormatter().addStyleName(2, 0, "bold");
		
		ft.setHTML(3, 0, "<span>Aliases</span>");
		ft.setHTML(3, 1, "<span>" + Conversion.collection2csv(g.getAliases()) + "</span>");
		ft.getFlexCellFormatter().setColSpan(3, 1, 3);
		ft.getCellFormatter().setWordWrap(3, 1, true);
		ft.getCellFormatter().addStyleName(3, 0, "bold");
		
		ft.setHTML(4, 0, "<span>Sequences</span>");
		ft.setHTML(4, 1, createSequenceHTML(g.getSequenceIds()));
		ft.getFlexCellFormatter().setColSpan(4, 1, 3);
		ft.getCellFormatter().setWordWrap(4, 1, true);
		ft.getCellFormatter().addStyleName(4, 0, "bold");
		
		if (g.getIsObsolete()) {
			ft.setHTML(5, 0, "<span>OBSOLETE</span>");
			ft.getFlexCellFormatter().setColSpan(5, 0, 4);
			ft.getCellFormatter().addStyleName(4, 0, "bold");
		}
		
		vp.add(ft);
		this.setWidget(vp);
		this.setWidth("250px");
	}
	
	private String createSequenceHTML(Collection<String> vals) {
		String returnVal = "";
		Iterator<String> itr = vals.iterator();
		while (itr.hasNext()) {
			String s = itr.next();
			if (s.contains("IPI")) {
				returnVal += s;
			} else {
				returnVal += "<a target='blank' href='http://www.uniprot.org/uniprot/"+s+"'>"+s+"</a>";
			}
			if (itr.hasNext()) returnVal += ", ";
		}
		return returnVal;
	}
}
