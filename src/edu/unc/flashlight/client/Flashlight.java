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

import com.google.gwt.activity.shared.ActivityManager;
import com.google.gwt.activity.shared.ActivityMapper;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.place.shared.PlaceHistoryHandler;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SimplePanel;

import edu.unc.flashlight.client.service.GenePairService;
import edu.unc.flashlight.client.service.GenePairServiceAsync;
import edu.unc.flashlight.client.service.GeneService;
import edu.unc.flashlight.client.service.GeneServiceAsync;
import edu.unc.flashlight.client.service.IdConversionService;
import edu.unc.flashlight.client.service.IdConversionServiceAsync;
import edu.unc.flashlight.client.service.UploadService;
import edu.unc.flashlight.client.service.UploadServiceAsync;
import edu.unc.flashlight.client.service.UserResultService;
import edu.unc.flashlight.client.service.UserResultServiceAsync;
import edu.unc.flashlight.client.service.UserService;
import edu.unc.flashlight.client.service.UserServiceAsync;
import edu.unc.flashlight.client.ui.FlashlightMenu;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Flashlight implements EntryPoint {
	
	private static int numPopups = 0;
	private static int maxZ = 0;
	
	private static FlashlightMenu menu = new FlashlightMenu();
	private static PlaceFactory placeFactory = new PlaceFactory();
	private SimplePanel appWidget = new SimplePanel();
	
	public static PlaceHistoryHandler historyHandler;
	public static EventBus eventBus;
	public static PlaceController placeController;
	
	

	/**
	 * Create a remote service proxy to talk to the server-side services.
	 */
	public static final GeneServiceAsync geneService = GWT.create(GeneService.class);
	public static final IdConversionServiceAsync idConversionService = GWT.create(IdConversionService.class);
	public static final UserServiceAsync userService = GWT.create(UserService.class);
	public static final UploadServiceAsync uploadService = GWT.create(UploadService.class);
	public static final UserResultServiceAsync userResultService = GWT.create(UserResultService.class);
	public static final GenePairServiceAsync genePairService = GWT.create(GenePairService.class);

	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {
		ClientFactory clientFactory = GWT.create(ClientFactory.class);
		eventBus = clientFactory.getEventBus();
		placeController = clientFactory.getPlaceController();
		
		// Start ActivityManager for the main widget with our ActivityMapper
		ActivityMapper activityMapper = new AppActivityMapper(clientFactory);
		ActivityManager activityManager = new ActivityManager(activityMapper, eventBus);
		activityManager.setDisplay(appWidget);
		
		// Start PlaceHistoryHandler with our PlaceHistoryMapper
		AppPlaceHistoryMapper historyMapper = GWT.create(AppPlaceHistoryMapper.class);
		historyHandler = new PlaceHistoryHandler(historyMapper);
		historyHandler.register(placeController, eventBus, placeFactory.getWelcomePlace());
		
		RootPanel.get("menu").add(menu.createMenuBar());
		RootPanel.get("content").add(appWidget);
		
		//FlashlightClientBundle.INSTANCE.css().ensureInjected();
		historyHandler.handleCurrentHistory();
	}
	
	public static FlashlightMenu getMenu() {
		return menu;
	}
	
	public static PlaceFactory getPlaceFactory() {
		return placeFactory;
	}
	
	public static int getNumPopups(){
		return numPopups;
	}
	
	public static void setNumPopups(int num) {
		numPopups = num;
		if (numPopups <= 0) {
			numPopups = 0;
			setMaxZ(0);
		}
	}
	
	public static void addPopup(int z) {
		numPopups++;
		if (z > maxZ) setMaxZ(z);
	}
	
	public static void removePopup() {
		numPopups--;
		if (numPopups <= 0) {
			numPopups = 0;
			setMaxZ(0);
		}
	}
	
	public static void setMaxZ(int z) {
		maxZ = z;
	}
	
	public static int getMaxZ() {
		return maxZ;
	}

}
