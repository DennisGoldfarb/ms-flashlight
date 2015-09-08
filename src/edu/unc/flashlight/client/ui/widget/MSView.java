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

import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.visualization.client.AbstractDataTable.ColumnType;
import com.google.gwt.visualization.client.DataTable;
import com.google.gwt.visualization.client.VisualizationUtils;
import com.google.gwt.visualization.client.visualizations.corechart.AreaChart;
import com.google.gwt.visualization.client.visualizations.corechart.Options;

import edu.unc.flashlight.shared.model.details.MSDetails;
import edu.unc.flashlight.shared.model.details.OrgDataTable;

public class MSView extends SimplePanel {

	public MSView(final MSDetails details) {
		final HorizontalPanel panel = new HorizontalPanel();
		panel.add(new ProteinInteractionView(details));
		Runnable onLoadCallback = new Runnable() {
			public void run() {
				AreaChart chart = new AreaChart(createData(details.getDataTable()), createOptions());
				panel.add(chart);
			}
		};

		// Load the visualization api, passing the onLoadCallback to be called
		// when loading is done.
		VisualizationUtils.loadVisualizationApi(onLoadCallback, AreaChart.PACKAGE);

		setWidget(panel);
	}
	
	public DataTable createData(OrgDataTable table) {
		DataTable data = DataTable.create();
		data.addColumn(ColumnType.STRING, "Abundance");
		data.addColumn(ColumnType.NUMBER, "Experiments");
		data.addColumn(ColumnType.NUMBER, "Controls");
		
		for (int i = 0; i < table.getData().size(); i++) {
			data.addRow();
			for (int j = 0; j < table.getData().get(i).length; j++) {
				if (j > 0) {
					data.setValue(i, j, Double.valueOf(table.getData().get(i)[j]));
				}
				else data.setValue(i, j, table.getData().get(i)[j]);
			}
		}
	
		return data;
	}
	
	private Options createOptions() {
		Options options = Options.create();
		options.setPointSize(10);
		options.setHeight(400);
		options.setWidth(600);
		return options;
	}
}
