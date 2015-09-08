package edu.unc.flashlight.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.Messages;

public interface FlashlightMessages extends Messages {
	public static final FlashlightMessages INSTANCE =  GWT.create(FlashlightMessages.class);
	
	@DefaultMessage("No results found for query: {0}")
	String results_noResultsQuery(String query);
}
