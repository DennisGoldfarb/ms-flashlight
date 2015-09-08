package edu.unc.flashlight.client.resource;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;

public interface FlashlightClientBundle extends ClientBundle {
	public static final FlashlightClientBundle INSTANCE =  GWT.create(FlashlightClientBundle.class);
	
}
