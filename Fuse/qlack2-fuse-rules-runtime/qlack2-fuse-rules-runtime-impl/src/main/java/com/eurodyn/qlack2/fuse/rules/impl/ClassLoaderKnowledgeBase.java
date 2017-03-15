package com.eurodyn.qlack2.fuse.rules.impl;

import org.kie.api.KieBase;

public class ClassLoaderKnowledgeBase {

	public final MapBackedClassLoader classLoader;

	public final KieBase knowledgeBase;

	public ClassLoaderKnowledgeBase(MapBackedClassLoader classLoader, KieBase knowledgeBase) {
		this.classLoader = classLoader;
		this.knowledgeBase = knowledgeBase;
	}

}
