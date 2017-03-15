package com.eurodyn.qlack2.common.util.url;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.github.slugify.Slugify;

public class UrlUtil {
	private static final Logger logger = Logger.getLogger(UrlUtil.class
			.getName());

	/**
	 * Gets a piece of text as String and computes its slugified version.
	 * 
	 * @param text
	 *            The text input which slugified version should be computed.
	 * @return The slugify process result.
	 */

	public static String getSlugified(String text) {
		// The default slugified URL should be a random String
		String slugifiedURL = null;
		Slugify slg;
		try {
			slg = new Slugify();
			slugifiedURL = slg.slugify(text);
			// In case that it is not possible for slugify library to provide a
			// slugified string,
			// a random UUID will be a assigned to the returned value.
			if (slugifiedURL == null || slugifiedURL.equals("")) {
				slugifiedURL = UUID.randomUUID().toString();
			}
		} catch (IOException e) {
			logger.log(Level.SEVERE, MessageFormat.format(
					"There was an error trying to calculate the slugified URL "
							+ "for text input: ", text, e));
		}
		return slugifiedURL;
	}
}
