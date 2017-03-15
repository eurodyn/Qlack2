package com.eurodyn.qlack2.fuse.rules.api;

import java.util.List;
import java.util.Map;

public interface RulesRuntimeService {

	String createKnowledgeBase(List<byte[]> libraries, List<String> rules);

	void destroyKnowledgeBase(String kbaseId);

	StatelessExecutionResults statelessExecute(String kbaseId, Map<String, byte[]> globals, List<byte[]> facts);

	StatelessExecutionResults statelessExecute(String kbaseId, String rule, Map<String, byte[]> globals, List<byte[]> facts);

	String createKnowledgeSession(String kbaseId);

	void destroyKnowledgeSession(String ksessionId);

	void setGlobal(String ksessionId, String globalId, byte[] global);

	byte[] getGlobal(String ksessionId, String globalId);

	String insertFact(String ksessionId, byte[] fact);

	List<String> insertFacts(String ksessionId, List<byte[]> fact);

	byte[] getFact(String ksessionId, String factId);

	List<byte[]> getFacts(String ksessionId, List<String> factIds);

	void deleteFact(String ksessionId, String factId);

	void fireRules(String ksessionId, List<String> rules);

	List<Map<String, byte[]>> getQueryResults(String ksessionId, String query, List<byte[]> arguments);

	String findKnowledgeBaseIdBySessionId(String ksessionId);

}
