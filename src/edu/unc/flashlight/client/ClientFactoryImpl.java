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
package edu.unc.flashlight.client;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.place.shared.PlaceController;

import edu.unc.flashlight.client.ui.view.AboutView;
import edu.unc.flashlight.client.ui.view.BrowseView;
import edu.unc.flashlight.client.ui.view.FeedbackView;
import edu.unc.flashlight.client.ui.view.MyDataView;
import edu.unc.flashlight.client.ui.view.UploadView;
import edu.unc.flashlight.client.ui.view.WelcomeView;
import edu.unc.flashlight.client.ui.view.impl.AboutViewImpl;
import edu.unc.flashlight.client.ui.view.impl.BrowseViewImpl;
import edu.unc.flashlight.client.ui.view.impl.FeedbackViewImpl;
import edu.unc.flashlight.client.ui.view.impl.MyDataViewImpl;
import edu.unc.flashlight.client.ui.view.impl.UploadViewImpl;
import edu.unc.flashlight.client.ui.view.impl.WelcomeViewImpl;

public class ClientFactoryImpl implements ClientFactory {
	private final EventBus eventBus = new SimpleEventBus();
	private final PlaceController placeController = new PlaceController(eventBus);
	private WelcomeView welcomeView;
	private UploadView uploadView;
	private AboutView aboutView;
	private FeedbackView feedbackView;
	private MyDataView myDataView;
	private BrowseView browseView;
	
	public EventBus getEventBus() {
		return eventBus;
	}
	
	public PlaceController getPlaceController() {
		return placeController;
	}
	
	public WelcomeView getWelcomeView() {
		if (welcomeView == null) welcomeView = new WelcomeViewImpl();
		return welcomeView;
	}
	
	public UploadView getUploadView() {
		if (uploadView == null) uploadView = new UploadViewImpl();
		return uploadView;
		//return new UploadViewImpl();
	}

	public FeedbackView getFeedbackView() {
		if (feedbackView == null) feedbackView = new FeedbackViewImpl();
		return feedbackView;
	}

	public AboutView getAboutView() {
		if (aboutView == null) aboutView = new AboutViewImpl();
		return aboutView;
	}

	public BrowseView getBrowseView() {
		if (browseView == null) browseView = new BrowseViewImpl();
		return browseView;
	}

	public MyDataView getMyDataView() {
		/*if (myDataView == null) myDataView = new MyDataViewImpl();
		else {
			myDataView.updateTable();
		}*/
		return new MyDataViewImpl();
	}
}
