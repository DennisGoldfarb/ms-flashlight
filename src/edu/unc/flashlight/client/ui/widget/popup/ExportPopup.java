package edu.unc.flashlight.client.ui.widget.popup;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;

public class ExportPopup extends ModalPopup implements ClickHandler {
	private Button distinct;
	private Button perBait;
	private Button cancel;
	private Label content;
	
	public ExportPopup(ClickHandler distinctHandler, ClickHandler perBaitHandler, ClickHandler cancelHandler) {
		super(false,true,true,false);
		this.setTitle("Export Data");
		distinct = new Button("Distinct Interactions");
		perBait = new Button("Interactions per Bait");
		cancel = new Button("Cancel");
		distinct.addClickHandler(distinctHandler);
		perBait.addClickHandler(perBaitHandler);
		cancel.addClickHandler(cancelHandler);
		distinct.addClickHandler(this);
		perBait.addClickHandler(this);
		cancel.addClickHandler(this);
		content = new HTML("Choose whether redundant interactions should be output.</br> " +
				"This occurs when an interaction is tested in both directions as baits.");
		distinct.addStyleName("blue");
		perBait.addStyleName("blue");
		cancel.addStyleName("blue");
	}
	
	public void onClick(ClickEvent event) {
		hide();
	}
	
	public void showPopup() {
		FlowPanel fp = new FlowPanel();
		fp.add(content);
		fp.add(distinct);
		fp.add(perBait);
		fp.add(cancel);
		setContentWidget(fp);
		center(true);
	}
}
