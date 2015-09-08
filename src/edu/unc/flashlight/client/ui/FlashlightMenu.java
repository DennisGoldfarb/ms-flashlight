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
package edu.unc.flashlight.client.ui;

import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.Widget;

import edu.unc.flashlight.client.Flashlight;
import edu.unc.flashlight.client.FlashlightConstants;

public class FlashlightMenu {
	private FlashlightConstants constants = FlashlightConstants.INSTANCE;
	
	private MenuItem currentItem;
	private final MenuItem home = new MenuItem(new SafeHtmlBuilder().appendEscaped(constants.menu_home()).toSafeHtml());
	private final MenuItem myData = new MenuItem(new SafeHtmlBuilder().appendEscaped(constants.menu_myData()).toSafeHtml());
	private final MenuItem upload = new MenuItem(new SafeHtmlBuilder().appendEscaped(constants.menu_upload()).toSafeHtml());
	private final MenuItem browse = new MenuItem(new SafeHtmlBuilder().appendEscaped(constants.menu_browse()).toSafeHtml());
	private final MenuItem about = new MenuItem(new SafeHtmlBuilder().appendEscaped(constants.menu_about()).toSafeHtml());
	private final MenuItem feedback = new MenuItem(new SafeHtmlBuilder().appendEscaped(constants.menu_feedback()).toSafeHtml());
	
	public FlashlightMenu() {
		
	}
	
	public Widget createMenuBar() {
		MenuBar menu = new MenuBar();
		menu.setAutoOpen(true);
		menu.setAnimationEnabled(true);		
		
		home.setCommand(new Command() {
			public void execute() {
				Flashlight.placeController.goTo(Flashlight.getPlaceFactory().getWelcomePlace());
			}
		});
		
		myData.setCommand(new Command() {
			public void execute() {
				Flashlight.placeController.goTo(Flashlight.getPlaceFactory().getMyDataPlace());
			}
		});
		
		upload.setCommand(new Command() {
			public void execute() {
				Flashlight.placeController.goTo(Flashlight.getPlaceFactory().getUploadPlace());
			}
		});
		
		browse.setCommand(new Command() {
			public void execute() {
				Flashlight.placeController.goTo(Flashlight.getPlaceFactory().getBrowsePlace());
			}
		});
		
		about.setCommand(new Command() {
			public void execute() {
				Flashlight.placeController.goTo(Flashlight.getPlaceFactory().getAboutPlace());
			}
		});
		
		feedback.setCommand(new Command() {
			public void execute() {
				Flashlight.placeController.goTo(Flashlight.getPlaceFactory().getFeedbackPlace());
			}
		});
		
		menu.addItem(home);
		menu.addItem(myData);
		menu.addItem(upload);
		menu.addItem(browse);
		menu.addItem(about);
		menu.addItem(feedback);
		return menu;
	}	
	
	public void updateCurrentMenu(MenuItem item) {
		if (currentItem != null) currentItem.removeStyleName("gwt-MenuItem-current");
		item.addStyleName("gwt-MenuItem-current");
		currentItem = item;
	}
	
	public MenuItem getHome() {
		return home;
	}
	
	public MenuItem getBrowse() {
		return browse;
	}
	
	public MenuItem getUpload() {
		return upload;
	}
	
	public MenuItem getFeedback() {
		return feedback;
	}
	
	public MenuItem getMyData() {
		return myData;
	}
	
	public MenuItem getAbout() {
		return about;
	}
}
