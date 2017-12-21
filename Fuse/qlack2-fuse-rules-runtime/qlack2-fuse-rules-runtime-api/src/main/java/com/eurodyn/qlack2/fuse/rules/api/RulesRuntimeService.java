package com.eurodyn.qlack2.fuse.rules.api;

import java.util.List;
import java.util.Map;

public interface RulesRuntimeService {

	String createKnowledgeBase(List<byte[]> libraries, List<String> rules);

	void destroyKnowledgeBase(String kbaseId);

	StatelessExecutionResults statelessExecute(String kbaseId, Map<String, byte[]> globals, List<byte[]> facts);

	StatelessExecutionResults statelessExecute(String kbaseId, String rule, Map<String, byte[]> globals, List<byte[]> facts);

  /**
   * Direct stateless rules execution.
   * <br>
   * A stateless session can be called like a function passing it some data and then receiving some results back.
	 * <p>
	 * A classloader which have the Classes of the facts is needed to be passed as argument, because the
	 * default Drools implementation is to use the Thread classloader which may not have the needed Classes.
	 * Usually the callers classloader is used ( e.g. this.getClass().getClassLoader() )
   *
   * @param rules the contents of the Drools (.drl) files
   * @param facts the objects to execute against the rules
   * @param globals global variables, available to the rules
	 * @param classLoader to load the Classes of the facts
   */
  void statelessExecute(List<String> rules, List<Object> facts, Map<String, Object> globals, ClassLoader classLoader);

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