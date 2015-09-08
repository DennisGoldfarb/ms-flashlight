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

import java.util.List;

import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.SimplePager;
import com.google.gwt.user.cellview.client.SimplePager.TextLocation;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.view.client.ListDataProvider;

import edu.unc.flashlight.shared.model.upload.UploadError;

public class UploadErrorTable extends Composite {
	
	CellTable<UploadError> cellTable;
	private SimplePager pager;
	private ListDataProvider<UploadError> dataProvider;
	
	public UploadErrorTable() {
		VerticalPanel panel = new VerticalPanel();
		initWidget(panel);
		
		cellTable = new CellTable<UploadError>();
		SimplePager.Resources pagerResources = GWT.create(SimplePager.Resources.class);
		pager = new SimplePager(TextLocation.CENTER, pagerResources,false,0,true);
		pager.setDisplay(cellTable);
		pager.setPageSize(10);
		
		dataProvider = new ListDataProvider<UploadError>();
	    dataProvider.addDataDisplay(cellTable);

		initTableColumns();
		
		panel.setCellHorizontalAlignment(pager, HasHorizontalAlignment.ALIGN_CENTER);
		
		panel.add(cellTable);
		panel.add(pager);
	}
	
	public void updateData(List<UploadError> data) {
		dataProvider.setList(data);
	}
	
	private void initTableColumns() {
		Column<UploadError, String> rowColumn = new Column<UploadError, String>(new TextCell()) {
			public String getValue(UploadError object) {
				return String.valueOf(object.getRow());
			}
		};
		cellTable.addColumn(rowColumn, "Row");
		
		Column<UploadError, String> colColumn = new Column<UploadError, String>(new TextCell()) {
			public String getValue(UploadError object) {
				if (object.getCol() == 0) return "";
				return String.valueOf(object.getCol());
			}
		};
		cellTable.addColumn(colColumn, "Column");
		
		Column<UploadError, String> dataColumn = new Column<UploadError, String>(new TextCell()) {
			public String getValue(UploadError object) {
				return String.valueOf(object.getData());
			}
		};
		cellTable.addColumn(dataColumn, "Value");
		
		Column<UploadError, String> errorColumn = new Column<UploadError, String>(new TextCell()) {
			public String getValue(UploadError object) {
				return object.getException().format();
			}
		};
		cellTable.addColumn(errorColumn, "Error");	
	}
}
