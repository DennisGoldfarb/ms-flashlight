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
package edu.unc.flashlight.client.service;

import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;

import edu.unc.flashlight.shared.model.ClassificationAlgorithm;
import edu.unc.flashlight.shared.model.UserFDR;
import edu.unc.flashlight.shared.model.UserResult;
import edu.unc.flashlight.shared.model.details.MSDetails;
import edu.unc.flashlight.shared.model.table.PageResults;
import edu.unc.flashlight.shared.model.table.PaginationParameters;

public interface UserResultServiceAsync {
	void getData(final PaginationParameters p, AsyncCallback<PageResults<UserResult>> callback);
	void getBrowseData(final PaginationParameters p, AsyncCallback<PageResults<UserResult>> callback);
	void getDataSearch(final PaginationParameters p, final String sym, AsyncCallback<PageResults<UserResult>> callback);
	void getBrowseDataSearch(final PaginationParameters p, final String sym, AsyncCallback<PageResults<UserResult>> callback);
	void getMSDetails(final long prey_id, AsyncCallback<MSDetails> callback);
	void deleteData(AsyncCallback<Void> callback);
	void getFDRForUser(AsyncCallback<List<UserFDR>> callback);
	void getClassificationAlgorithm(AsyncCallback<String> callback);
}
