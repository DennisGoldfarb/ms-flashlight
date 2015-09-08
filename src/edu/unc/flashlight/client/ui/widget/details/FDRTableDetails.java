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

import java.util.List;

import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.SimplePanel;

import edu.unc.flashlight.shared.model.UserFDR;

public class FDRTableDetails extends SimplePanel {
	
	public FDRTableDetails(List<UserFDR> fdrs) {
		FlexTable ft = new FlexTable();
		ft.setHTML(0, 0, "<span>Desired FDR</span>");
		ft.setHTML(0, 1, "<span>P-value Threshold</span>");
		ft.setHTML(0, 2, "<span># Accepted Interactions</span>");
		ft.getCellFormatter().addStyleName(0, 0, "bold");
		ft.getCellFormatter().addStyleName(0, 1, "bold");
		ft.getCellFormatter().addStyleName(0, 2, "bold");
		
		double[] selected_fdrs = new double[]{.01, .05, .1, .2};
		for (int i = 0; i < selected_fdrs.length; i++) {
			double d = selected_fdrs[i];
			ft.setHTML(i+1, 0, "<span>"+NumberFormat.getFormat("0.00").format(d)+"</span>");
			for (int j = 0; j < fdrs.size(); j++) {
				UserFDR fdr = fdrs.get(j);
				if (fdr.getFdr() == d) {
					ft.setHTML(i+1, 1, "<span>&#8804 "+NumberFormat.getFormat("0.##E0").format(fdr.getThreshold())+"</span>");
					ft.setHTML(i+1, 2, "<span>"+String.valueOf(fdr.getNumAccepted())+"</span>");
					break;
				}
			}
		}
		setWidget(ft);
	}
}
