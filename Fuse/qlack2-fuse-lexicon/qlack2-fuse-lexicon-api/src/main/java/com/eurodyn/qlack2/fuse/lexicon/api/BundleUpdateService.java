package com.eurodyn.qlack2.fuse.lexicon.api;

import org.osgi.framework.Bundle;

import java.net.URL;

public interface BundleUpdateService {
	void processBundle(Bundle bundle, String lexiconYAML);

	void updateBundleTranslations(String symbolicName, URL yamlUrl);
}
