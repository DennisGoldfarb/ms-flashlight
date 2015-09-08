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
package edu.unc.flashlight.client.command;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;

import edu.unc.flashlight.client.ui.widget.popup.ExceptionPopup;
import edu.unc.flashlight.shared.exception.FlashlightException;

/**
 * An AsyncCallback implementation with some error handling and recovery.
 * <p>
 * This class provides some assistance for AsyncCallbacks by making the callback
 * restartable, and able to automatically detect and recover from some classes
 * of caught exceptions (such as {@link LoginRequiredException}).
 * </p>
 */
public abstract class ServerOp<T> implements AsyncCallback<T>, Command {
	/** Automatically invoke {@link #begin()}. */
	public void execute() {
		begin();
	}

	public void onFailure(final Throwable e) {
		if (e instanceof FlashlightException) new ExceptionPopup((FlashlightException) e).center();
		else new ExceptionPopup((Exception) e).center();
	}

	/**
	 * Invoked when the user has decided to cancel this operation.
	 * <p>
	 * Not all ServerOps can be cancelled. Typically a cancel can occur if the
	 * server threw back {@link LoginRequiredException} and the UI shows the
	 * user {@link LoginDialog}. When this happens the user has an option to
	 * cancel (not login), at which point there is no way for this operation to
	 * continue.
	 * </p>
	 */
	public void cancel() {
	}

	/**
	 * Invoke the proper async service method with the necessary arguments.
	 * <p>
	 * Subclasses must implement this method to allow callers (such as {@link
	 * LoginDialog} to restart a previously failed server operation.
	 * </p>
	 */
	public abstract void begin();
}
