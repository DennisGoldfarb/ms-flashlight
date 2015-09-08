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

import com.google.gwt.activity.shared.Activity;
import com.google.gwt.activity.shared.ActivityMapper;
import com.google.gwt.place.shared.Place;

import edu.unc.flashlight.client.ui.activity.AboutActivity;
import edu.unc.flashlight.client.ui.activity.BrowseActivity;
import edu.unc.flashlight.client.ui.activity.FeedbackActivity;
import edu.unc.flashlight.client.ui.activity.MyDataActivity;
import edu.unc.flashlight.client.ui.activity.UploadActivity;
import edu.unc.flashlight.client.ui.activity.WelcomeActivity;
import edu.unc.flashlight.client.ui.place.AboutPlace;
import edu.unc.flashlight.client.ui.place.BrowsePlace;
import edu.unc.flashlight.client.ui.place.FeedbackPlace;
import edu.unc.flashlight.client.ui.place.MyDataPlace;
import edu.unc.flashlight.client.ui.place.UploadPlace;
import edu.unc.flashlight.client.ui.place.WelcomePlace;

public class AppActivityMapper implements ActivityMapper {
	private ClientFactory clientFactory;
	
	public AppActivityMapper(ClientFactory clientFactory) {
		super();
		this.clientFactory = clientFactory;
	}
	
	public Activity getActivity(Place place) {
		if (place instanceof WelcomePlace) {
			return new WelcomeActivity((WelcomePlace) place, clientFactory);
		} else if (place instanceof UploadPlace) {
			return new UploadActivity((UploadPlace) place, clientFactory);
		} else if (place instanceof BrowsePlace) {
			return new BrowseActivity((BrowsePlace) place, clientFactory);
		} else if (place instanceof FeedbackPlace) {
			return new FeedbackActivity((FeedbackPlace) place, clientFactory);
		} else if (place instanceof AboutPlace) {
			return new AboutActivity((AboutPlace) place, clientFactory);
		} else if (place instanceof MyDataPlace) {
			return new MyDataActivity((MyDataPlace) place, clientFactory);
		}
		return null;
	}
}
