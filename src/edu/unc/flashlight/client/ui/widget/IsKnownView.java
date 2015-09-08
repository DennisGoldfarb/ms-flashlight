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

import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.SimplePanel;

import edu.unc.flashlight.client.ui.widget.details.GeneSymbolDetails;
import edu.unc.flashlight.shared.model.GeneInteraction;
import edu.unc.flashlight.shared.model.details.InteractionDetails;

public class IsKnownView extends SimplePanel {
	private HorizontalPanel panel;
	private ProteinInteractionView canvas;

	public IsKnownView(InteractionDetails details) {
		canvas = new ProteinInteractionView(details);
		canvas.addStyleName("geneDetails");
		panel = new HorizontalPanel();	
		
		FlexTable ft = new FlexTable();
		ft.setStyleName("geneDetails");
		
		ft.setHTML(0, 0, "<span style=\"font-size: 14px\">Interaction Details</span>");
		ft.getFlexCellFormatter().setColSpan(0, 0, 5);
		ft.getCellFormatter().setHorizontalAlignment(0, 0, HasHorizontalAlignment.ALIGN_CENTER);
		ft.getCellFormatter().addStyleName(0, 0, "bold");
		
		ft.setWidget(1, 0, new GeneSymbolDetails(details.getInteractions().get(0).getGeneA()));
		ft.getFlexCellFormatter().setColSpan(1, 0, 2);
		ft.getCellFormatter().setHorizontalAlignment(1, 0, HasHorizontalAlignment.ALIGN_CENTER);
		ft.setWidget(1, 1, new GeneSymbolDetails(details.getInteractions().get(0).getGeneB()));
		ft.getFlexCellFormatter().setColSpan(1, 1, 3);
		ft.getCellFormatter().setHorizontalAlignment(1, 1, HasHorizontalAlignment.ALIGN_CENTER);
		
		int row = 2;
		for (GeneInteraction i : details.getInteractions()) {
			ft.setHTML(row, 0, "<span>Method</span>");
			ft.setHTML(row, 1, "<span>" + i.getExperimentalSystem().getName() + "</span>");
			ft.setHTML(row, 2, "<span>Throughput</span>");
			ft.setHTML(row, 3, "<span>" + i.getThroughput() + "</span>");
			ft.setWidget(row, 4, new Pubmed(i.getPubmedID()));
			ft.getCellFormatter().addStyleName(row, 0, "bold");
			ft.getCellFormatter().addStyleName(row, 2, "bold");
			row++;
		}
		
		panel.add(canvas);
		panel.add(ft);
		
		this.setWidget(panel);
	}
}
