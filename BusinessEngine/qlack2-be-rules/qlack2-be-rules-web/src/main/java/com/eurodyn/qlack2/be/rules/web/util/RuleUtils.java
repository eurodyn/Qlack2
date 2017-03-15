package com.eurodyn.qlack2.be.rules.web.util;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// XXX duplicate with rules-impl, how do we share code between rules-impl and rules-web ?
public class RuleUtils {
	private static final String RULE_NAME_PATTERN = "^\\s*rule\\s*\\\"([^\"]+)\\\"\\s*$"; // rule "name"

	private RuleUtils() {
	}

	/**
	 * Find rule names in a DRL file.
	 */
	public static List<String> findRuleNames(String drl) {
		Pattern p = Pattern.compile(RULE_NAME_PATTERN);

		List<String> names = new ArrayList<>();

		String lines[] = drl.split("\\r?\\n");

		for (String line : lines) {
			Matcher m = p.matcher(line);
			if (m.find()) {
				String name = m.group(1);
				names.add(name);
			}
		}

		return names;
	}
}
