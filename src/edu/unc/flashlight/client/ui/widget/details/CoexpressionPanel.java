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

import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import edu.unc.flashlight.shared.util.Conversion;

public class CoexpressionPanel extends SimplePanel {
	private Image img;
	private VerticalPanel content;
	private NumberFormat doubleFormat = NumberFormat.getFormat("#.###");
	
	public CoexpressionPanel(Long ida, Long idb, Double pcc, Boolean hasImage) {
		content = new VerticalPanel();
		content.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		if (hasImage) {
			img = new Image(Conversion.generateCoxpresImgURL(ida.toString(),idb.toString()) );
		} else {
			img = new Image("images/no_coex.png");
		}
		img.setSize("250px", "250px");
	
		content.add(createLabel(ida,idb,pcc));
		content.add(img);
		this.setWidget(content);
	}
	
	private Widget createLabel(Long ida, Long idb,Double pcc) {
		HorizontalPanel content = new HorizontalPanel();
		this.setStyleName("geneSymbolDetails");
		this.addStyleDependentName("coexpressionPanel");
		content = new HorizontalPanel();
		content.setSpacing(1);
		content.setStyleName("container");
		VerticalPanel imgs = new VerticalPanel();
		imgs.setSpacing(1);
		content.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		content.add(new Label("r = " + doubleFormat.format(pcc)));
		imgs.add(new HTML("<a style=\"background-image: url('images/coxpresdb.png')\" target='blank' href='" + Conversion.generateCoxpresURL(ida.toString(), idb.toString()) + "'><span>coxpresdb</span></a>"));
		content.add(imgs);
		return content;
	}
}
