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
package edu.unc.flashlight.client.ui.event;

import java.util.Set;

import com.google.gwt.event.shared.GwtEvent;

import edu.unc.flashlight.client.ui.widget.SingleUploadWidget;
import edu.unc.flashlight.shared.model.SAINT.SaintParameters;
import edu.unc.flashlight.shared.util.Constants.MS_ALGORITHMS;

public class FileUploadCompleteEvent extends GwtEvent<FileUploadCompleteEventHandler> {
	public static Type<FileUploadCompleteEventHandler> TYPE = new Type<FileUploadCompleteEventHandler>();
	private String filename;
	private SingleUploadWidget widget;
	private long role;
	private MS_ALGORITHMS alg;
	private Set<Long> selectedIds;
	private boolean useRandomForest;
	private SaintParameters saintParameters;
	
	public FileUploadCompleteEvent(final String filename, final SingleUploadWidget widget, final long role, 
			final MS_ALGORITHMS alg, final Set<Long> selectedIds, final boolean useRandomForest, final SaintParameters saintParameters) {
		this.filename = filename;
		this.widget = widget;
		this.role = role;
		this.alg = alg;
		this.selectedIds = selectedIds;
		this.useRandomForest = useRandomForest;
		this.saintParameters = saintParameters;
	}
	
	public String getFilename() {
		return filename;
	}
	
	public SingleUploadWidget getSingleUploadWidget() {
		return widget;
	}
	
	public long getRole() {
		return role;
	}
	
	public MS_ALGORITHMS getMSAlgorithm() {
		return alg;
	}
	
	public Set<Long> getSelectedIds() {
		return selectedIds;
	}
	
	public boolean getUseRandomForest() {
		return useRandomForest;
	}
	
	public SaintParameters getSaintParameters() {
		return saintParameters;
	}

	@Override
	public Type<FileUploadCompleteEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(FileUploadCompleteEventHandler handler) {
		handler.onFileUploadComplete(this);		
	}

}
