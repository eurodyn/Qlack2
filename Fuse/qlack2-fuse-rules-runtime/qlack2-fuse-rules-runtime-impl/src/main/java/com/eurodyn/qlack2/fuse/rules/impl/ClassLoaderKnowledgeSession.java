package com.eurodyn.qlack2.fuse.rules.impl;

import org.kie.api.runtime.KieSession;

public class ClassLoaderKnowledgeSession {

	public final MapBackedClassLoader classLoader;

	public final KieSession knowledgeSession;

	public ClassLoaderKnowledgeSession(MapBackedClassLoader classLoader, KieSession knowledgeSession) {
		this.classLoader = classLoader;
		this.knowledgeSession = knowledgeSession;
	}

}
