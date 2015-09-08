package edu.unc.flashlight.client.ui.validation;

import edu.unc.flashlight.client.FlashlightConstants;
import eu.maydu.gwt.validation.client.i18n.ValidationMessages;

public class FlashlightValidationMessages extends ValidationMessages {
	private FlashlightConstants constants = FlashlightConstants.INSTANCE;

	
	@Override
	public String getPropertyName(String propertyName) {
		return propertyName;
	}

	public String getDescriptionMessage(String msgKey) {	  
		return constants.getString(msgKey);
	}
}
