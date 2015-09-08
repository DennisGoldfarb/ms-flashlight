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
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import edu.unc.flashlight.client.ClientFactory;
import edu.unc.flashlight.client.Flashlight;
import edu.unc.flashlight.client.command.ServerOp;
import edu.unc.flashlight.client.ui.event.FileUploadCompleteEvent;
import edu.unc.flashlight.client.ui.event.FileUploadCompleteEventHandler;
import edu.unc.flashlight.client.ui.event.FileUploadStartEvent;
import edu.unc.flashlight.client.ui.event.FileUploadStartEventHandler;
import edu.unc.flashlight.client.ui.place.UploadPlace;
import edu.unc.flashlight.client.ui.view.UploadView;
import edu.unc.flashlight.client.ui.widget.ProgressBar;
import edu.unc.flashlight.shared.model.upload.UploadProgress;
import edu.unc.flashlight.shared.model.upload.UploadResult;

public class UploadActivity extends AbstractActivity implements UploadView.Presenter{
	private ClientFactory clientFactory;
	private UploadView uploadView;
	private UploadProgress uploadProgress;
	private HandlerRegistration handlerRegistrationStart;
	private HandlerRegistration handlerRegistrationComplete;
	private boolean isUploading = false;
	private final Timer timer;

	public UploadActivity(UploadPlace place, ClientFactory clientFactory) {
		this.clientFactory = clientFactory;
		timer = new Timer() { 
			public void run() { 
				checkUploadProgress();
			} 
		}; 
	}

	public void start(AcceptsOneWidget containerWidget, EventBus eventBus) {
		this.uploadView = clientFactory.getUploadView();
		uploadView.setPresenter(this);
		containerWidget.setWidget(uploadView.asWidget());
		Flashlight.getMenu().updateCurrentMenu(Flashlight.getMenu().getUpload());
	}

	public void onStop() {
		if (handlerRegistrationComplete != null) {
			handlerRegistrationComplete.removeHandler();
		}
		if (handlerRegistrationStart != null) {
			handlerRegistrationStart.removeHandler();
		}
	}
	
	public String mayStop() {
		if (!isUploading) return null;
		return "Navigate away? An upload is still in progress.";
	}

	public void goTo(Place place) {
		clientFactory.getPlaceController().goTo(place);
	}

	public void bind() {
		handlerRegistrationComplete = Flashlight.eventBus.addHandler(FileUploadCompleteEvent.TYPE, handler);
		handlerRegistrationStart = Flashlight.eventBus.addHandler(FileUploadStartEvent.TYPE, new FileUploadStartEventHandler() {
			public void onFileUploadStart(FileUploadStartEvent event) {
				onUploadStart();
			}	
		});
		
		uploadView.getUploadWidget().setProgressBar((uploadView.getProgressUpload()));
	}
	
	FileUploadCompleteEventHandler handler = new FileUploadCompleteEventHandler() {
		public void onFileUploadComplete(final FileUploadCompleteEvent event) {
			timer.schedule(500); 
			timer.scheduleRepeating(500);

			new ServerOp<Void>() {
				public void onSuccess(Void result) {

				}
				public void onFailure(Throwable e) {
					timer.cancel();
					resetGUI();
					super.onFailure(e);
				}
				public void begin() {
					Flashlight.uploadService.parseFile(event.getFilename(), 9606L, event.getRole(), event.getMSAlgorithm(), 
							event.getUseRandomForest(), event.getSaintParameters(), this);			 
					timer.run();
				}
			}.begin();
		}
	};
	
	private void onUploadStart() {
		isUploading=true;
		uploadView.getUploadWidget().setSaintParameters(uploadView.getSaintParameters());
		uploadView.getUploadWidget().setMSAlgorithm(uploadView.getMSAlgorithm());
		uploadView.getUploadWidget().setUseRandomForest(uploadView.getUseRandomForest());
		//uploadView.getUploadWidget().setSelectedIds(uploadView.getSelectedIds());
		uploadView.getUploadWidget().setIsParsing(true);
		uploadView.getProgressPanel().setVisible(true);
		uploadView.getErrorPanel().setVisible(false);
		uploadView.getResultPanel().setVisible(false);
	}
	
	private void resetGUI() {
		isUploading=false;
		uploadView.getUploadWidget().setIsParsing(false);
		uploadView.getProgressPanel().setVisible(false);
		updateProgressBar(0,uploadView.getProgressUpdate(), false);
		updateProgressBar(0,uploadView.getProgressClassifier(), false);
		updateProgressBar(0,uploadView.getProgressMSScore(), false);
		updateProgressBar(0,uploadView.getProgressMap(), false);
		updateProgressBar(0,uploadView.getProgressParse(), false);
		updateProgressBar(0,uploadView.getProgressUpload(), false);
	}
	
	private void onDataUploadComplete(UploadResult uploadResult) {
		resetGUI();
		if (uploadResult.getErrors().size() > 0) {
			uploadView.getErrorPanel().setVisible(true);
			uploadView.getErrorTable().updateData(uploadResult.getErrors());
		} else {
			uploadView.UpdateCountWidgets(uploadResult.getResultCount(), uploadResult.getNotMappedFilename());
			uploadView.getResultPanel().setVisible(true);
		}
	}
	
	private void updateProgessWidget(UploadProgress result) {
		if (result != null) {
			updateUpdateProgress(result.getUpdateProgress());
			updateClassifierProgress(result.getClassifierProgress());
			updateMSScoreProgress(result.getMSScoreProgress());
			updateMappingProgress(result.getMappingProgress());
			updateParsingProgress(result.getParsingProgress());
		}
	}
	
	private void checkUploadProgress() {
		new ServerOp<UploadProgress>() {
			public void onSuccess(UploadProgress result) {
				uploadProgress = result;
				updateProgessWidget(uploadProgress);
				if (uploadProgress != null && uploadProgress.getUpdateProgress() == 1) {
					getUploadResult();
				}
			}
			public void begin() {
				Flashlight.uploadService.getUploadProgress(this);
			}	
		}.begin();
	}
	
	private void getUploadResult() {
		new ServerOp<UploadResult>() {
			public void onSuccess(UploadResult result) {
				timer.cancel();
				onDataUploadComplete(result);
			}
			public void begin() {
				Flashlight.uploadService.getUploadResult(this);
			}	
		}.begin();
	}

	@Override
	public void updateParsingProgress(double progress) {
		ProgressBar bar = uploadView.getProgressParse();
		updateProgressBar(progress,bar, true);
	}

	@Override
	public void updateMappingProgress(double progress) {
		ProgressBar bar = uploadView.getProgressMap();
		updateProgressBar(progress,bar, uploadProgress.getParsingProgress() >= 1);
	}

	@Override
	public void updateMSScoreProgress(double progress) {
		ProgressBar bar = uploadView.getProgressMSScore();
		updateProgressBar(progress,bar, uploadProgress.getMappingProgress() >= 1);
	}
	
	@Override
	public void updateClassifierProgress(double progress) {
		ProgressBar bar = uploadView.getProgressClassifier();
		updateProgressBar(progress,bar, uploadProgress.getMSScoreProgress() >= 1);
	}

	@Override
	public void updateUpdateProgress(double progress) {
		ProgressBar bar = uploadView.getProgressUpdate();
		updateProgressBar(progress,bar, uploadProgress.getClassifierProgress() >= 1);
	}
	
	public void updateProgressBar(double progress, ProgressBar bar, boolean isVisible) {
		if (progress > 0 || isVisible) {
			bar.setVisible(true);
			bar.setProgress((int)(progress*100));
		}
		else bar.setVisible(false);	
	}
	
	public void onViewDataButtonClicked() {
		Flashlight.placeController.goTo(Flashlight.getPlaceFactory().getMyDataPlace());
	}


}
