package edu.unc.flashlight.client.ui.widget.popup;

import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.i18n.client.LocaleInfo;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.PopupPanel.PositionCallback;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.TextBoxBase;

import eu.maydu.gwt.validation.client.description.Description;
import eu.maydu.gwt.validation.client.i18n.ValidationMessages;

public class ValidationPopupDescription implements Description<TextBoxBase> {

	private ValidationMessages messages;
	private LocaleInfo localeInfo = LocaleInfo.getCurrentLocale();
	
	/**
	 * Creates a new PopupDescription and configures it with the specified 
	 * <code>ValidationMessages</code>. Be sure to overwrite the 
	 * <code>getDescriptionMessage</code> method to i18n the description
	 * text. 
	 * 
	 * 
	 * @param messages The validation messages instance to use 
	 */
	public ValidationPopupDescription(ValidationMessages messages) {
		this.messages = messages;
	}
	

	/**
	 * Adds a popup description to a widget.
	 * 
	 * @param key This key is used to get the i18n text that should be displayed. This key will be passed as argument to the <code>ValidationMessages.getDescriptionMessage</code> method.
	 * @param widget The widget that should show a popup description when it gets the focus.
	 */
	public void addDescription(String key, final TextBoxBase widget) {
		
		final PopupPanel p = new PopupPanel(true);
		
		String content = messages.getDescriptionMessage(key);
		
		if(localeInfo.isRTL())
			content = "<div align=\"right\">" + content + "</div>";
		
		HTML html = new HTML(content, false);
		p.setWidget(html);
		
		
		widget.addFocusHandler(new FocusHandler() {

			@Override
			public void onFocus(FocusEvent event) {
				p.setPopupPositionAndShow(new PositionCallback() {

					public void setPosition(int offsetWidth, int offsetHeight) {
						
						int left, top, height, width;
						
						if(!localeInfo.isRTL()) {
							left = widget.getAbsoluteLeft();
						}else {
							left = widget.getAbsoluteLeft()+widget.getOffsetWidth();
							left -= p.getOffsetWidth();
						}
						top = widget.getAbsoluteTop();
						height = widget.getOffsetHeight();
						width = widget.getOffsetWidth();

						p.setPopupPosition(left+width, top-3);
					}
				});				
			}
		});
		
		widget.addBlurHandler(new BlurHandler() {

			public void onBlur(BlurEvent event) {
				p.hide();
			}
			
		});
	}

	public void addDescription(String key, final SuggestBox suggest) {
		addDescription(key, suggest.getTextBox());
	}
}
