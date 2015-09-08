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
package edu.unc.flashlight.client.ui.activity;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import edu.unc.flashlight.client.ClientFactory;
import edu.unc.flashlight.client.Flashlight;
import edu.unc.flashlight.client.command.ServerOp;
import edu.unc.flashlight.client.ui.place.MyDataPlace;
import edu.unc.flashlight.client.ui.view.MyDataView;
import edu.unc.flashlight.client.ui.widget.popup.ConfirmationPopup;
import edu.unc.flashlight.client.ui.widget.popup.DeletingPopup;
import edu.unc.flashlight.client.ui.widget.popup.ExportPopup;
import edu.unc.flashlight.client.ui.widget.popup.FDRPopup;
import edu.unc.flashlight.shared.util.Constants;

public class MyDataActivity extends AbstractActivity implements MyDataView.Presenter{
	private ClientFactory clientFactory;
	private MyDataView myDataView;
	
	public MyDataActivity(MyDataPlace place, ClientFactory clientFactory) {
		this.clientFactory = clientFactory;
	}
	
	public void start(AcceptsOneWidget containerWidget, EventBus eventBus) {
		myDataView = clientFactory.getMyDataView();
		myDataView.setPresenter(this);
		containerWidget.setWidget(myDataView.asWidget());
		Flashlight.getMenu().updateCurrentMenu(Flashlight.getMenu().getMyData());
	}
	
	public void export() {
		ClickHandler distinct = new ClickHandler() {
			public void onClick(ClickEvent event) {
				Window.open(GWT.getModuleBaseURL() + Constants.DOWNLOAD_SERVLET +"?eu=Distinct", "_blank", ""); 
			}	
		};
		ClickHandler perBait = new ClickHandler() {
			public void onClick(ClickEvent event) {
				Window.open(GWT.getModuleBaseURL() + Constants.DOWNLOAD_SERVLET +"?eu", "_blank", ""); 
			}	
		};
		ClickHandler cancel = new ClickHandler() {
			public void onClick(ClickEvent event) {
			}	
		};
		ExportPopup p = new ExportPopup(distinct,perBait,cancel);
		p.showPopup();		
	}
	
	public void updateTable(String sym) {
		myDataView.updateTable(sym);
	}
	
	public void getFDR() {
		FDRPopup popup = new FDRPopup();
		popup.showPopup();
	}
	
	public void deleteData() {
		ClickHandler accept = new ClickHandler() {
			public void onClick(ClickEvent event) {
				final DeletingPopup popup = new DeletingPopup();	
				new ServerOp<Void>() {
					public void onSuccess(Void result) {
						popup.hide();
						myDataView.updateTable("");
					}
					public void begin() {
						popup.showPopup();
						Flashlight.userResultService.deleteData(this);
					}
				}.begin();
			}	
		};
		ClickHandler cancel = new ClickHandler() {
			public void onClick(ClickEvent event) {
			}	
		};
		ConfirmationPopup p = new ConfirmationPopup(accept,cancel,"Are you sure you want to delete your data?");
		p.showPopup();		
	}
	
	public String mayStop() {
		return null;
	}
	
	public void goTo(Place place) {
		clientFactory.getPlaceController().goTo(place);
	}
}
