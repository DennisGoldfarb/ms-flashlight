package edu.unc.flashlight.client.ui.widget;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.ui.MultiWordSuggestOracle;

import edu.unc.flashlight.client.Flashlight;
import edu.unc.flashlight.client.command.ServerOp;
import edu.unc.flashlight.shared.model.table.PageResults;

public class AsyncSuggestOracle extends MultiWordSuggestOracle {

	public void requestSuggestions(final Request request, final Callback callback) {
		final String query = request.getQuery();
		final int limit = request.getLimit();

		// Get candidates from search words.
		if (query.length() < 2) {
			callback.onSuggestionsReady(request, new Response(new ArrayList<MultiWordSuggestion>()));
		} else {	
			new ServerOp<PageResults<String>>() {
				public void onSuccess(PageResults<String> result) {
					// Convert candidates to suggestions.
					int numberTruncated = Math.max(0, (int) result.getSize() - limit);
					List<MultiWordSuggestion> suggestions = convertToFormattedSuggestions(query, result.getResults());
	
					Response response = new Response(suggestions);
					response.setMoreSuggestionsCount(numberTruncated);
	
					callback.onSuggestionsReady(request, response);
				}
				public void begin() {
					Flashlight.geneService.getSymbolSuggestions(query, this);
				}
			}.begin();
		}
	}
	
	/**
	   * Returns real suggestions with the given query in <code>strong</code> html
	   * font.
	   *
	   * @param query query string
	   * @param candidates candidates
	   * @return real suggestions
	   */
	  private List<MultiWordSuggestion> convertToFormattedSuggestions(String query,
	      List<String> candidates) {
	    List<MultiWordSuggestion> suggestions = new ArrayList<MultiWordSuggestion>();

	    for (int i = 0; i < candidates.size(); i++) {
	      String candidate = candidates.get(i);

	      // Create strong search string.
	      SafeHtmlBuilder accum = new SafeHtmlBuilder();
	      accum.appendHtmlConstant("<strong>");
	      accum.appendEscaped(candidate.substring(0,query.length()));
	      accum.appendHtmlConstant("</strong>");
	      accum.appendEscaped(candidate.substring(query.length(), candidate.length()));

	      MultiWordSuggestion suggestion = createSuggestion(candidate, accum.toSafeHtml().asString());
	      suggestions.add(suggestion);
	    }
	    return suggestions;
	  }
}