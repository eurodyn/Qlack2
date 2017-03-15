package com.eurodyn.qlack2.fuse.rules.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.transaction.TransactionManager;
import javax.transaction.TransactionSynchronizationRegistry;
import javax.transaction.UserTransaction;

import org.drools.core.base.RuleNameEqualsAgendaFilter;
import org.drools.core.command.runtime.rule.FireAllRulesCommand;
import org.drools.core.common.DroolsObjectInputStream;
import org.drools.core.common.DroolsObjectOutputStream;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.impl.KnowledgeBaseImpl;
import org.kie.api.KieBase;
import org.kie.api.KieBaseConfiguration;
import org.kie.api.command.Command;
import org.kie.api.io.Resource;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.Environment;
import org.kie.api.runtime.EnvironmentName;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.StatelessKieSession;
import org.kie.api.runtime.rule.AgendaFilter;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.api.runtime.rule.Match;
import org.kie.api.runtime.rule.QueryResults;
import org.kie.api.runtime.rule.QueryResultsRow;
import org.kie.internal.KnowledgeBaseFactory;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderConfiguration;
import org.kie.internal.builder.KnowledgeBuilderError;
import org.kie.internal.builder.KnowledgeBuilderErrors;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.internal.command.CommandFactory;
import org.kie.internal.definition.KnowledgePackage;
import org.kie.internal.io.ResourceFactory;
import org.kie.internal.persistence.jpa.JPAKnowledgeService;

import com.eurodyn.qlack2.fuse.rules.api.QRulesRuntimeException;
import com.eurodyn.qlack2.fuse.rules.api.RulesRuntimeService;
import com.eurodyn.qlack2.fuse.rules.api.StatelessExecutionResults;
import com.eurodyn.qlack2.fuse.rules.impl.model.RuntimeBaseLibrary;
import com.eurodyn.qlack2.fuse.rules.impl.model.RuntimeBaseState;
import com.eurodyn.qlack2.fuse.rules.impl.model.RuntimeGlobal;
import com.eurodyn.qlack2.fuse.rules.impl.model.RuntimeSession;

public class RulesRuntimeServiceImpl implements RulesRuntimeService {
	private static final Logger logger = Logger.getLogger(RulesRuntimeService.class.getName());

	private UserTransaction utx;
	private TransactionManager tm;
	private TransactionSynchronizationRegistry tsr;

	private EntityManagerFactory emf;
	private EntityManager em;

	private Environment env;

	public void setUtx(UserTransaction utx) {
		this.utx = utx;
	}

	public void setTm(TransactionManager tm) {
		this.tm = tm;
	}

	public void setTsr(TransactionSynchronizationRegistry tsr) {
		this.tsr = tsr;
	}

	public void setEmf(EntityManagerFactory emf) {
		this.emf = emf;
	}

	public void setEm(EntityManager em) {
		this.em = em;
	}

	public void init() {
		logger.log(Level.FINE, "Rules runtime init ...");

		env = KnowledgeBaseFactory.newEnvironment();
		env.set(EnvironmentName.TRANSACTION, utx);
		env.set(EnvironmentName.TRANSACTION_MANAGER, tm);
		env.set(EnvironmentName.TRANSACTION_SYNCHRONIZATION_REGISTRY, tsr);
		env.set(EnvironmentName.ENTITY_MANAGER_FACTORY, emf);
	}

	public void destroy() {
		logger.log(Level.FINE, "Rules runtime destroy ...");
	}

	@Override
	public String createKnowledgeBase(List<byte[]> libraries, List<String> rules) {

		// add libraries to classloader
		JarClassLoaderBuilder classLoaderBuilder = new JarClassLoaderBuilder();

		for (byte[] libraryBytes : libraries) {
			classLoaderBuilder.add(libraryBytes);
		}

		ClassLoader classLoader = classLoaderBuilder.buildClassLoader();

		// add packages to knowledge base
		KnowledgeBuilderConfiguration kBuilderConfiguration = KnowledgeBuilderFactory.newKnowledgeBuilderConfiguration(null, classLoader);
		KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder(kBuilderConfiguration);

		for (String rulesText : rules) {
			byte[] rulesBytes = rulesText.getBytes(Charset.forName("UTF-8"));
			Resource rulesResource = ResourceFactory.newByteArrayResource(rulesBytes);
			kbuilder.add(rulesResource, ResourceType.DRL);

			if (kbuilder.hasErrors()) {
				KnowledgeBuilderErrors kerrors = kbuilder.getErrors();
				for (KnowledgeBuilderError kerror : kerrors) {
					logger.log(Level.WARNING, kerror.toString());
				}
				throw new QRulesRuntimeException(kerrors.toString());
			}
		}

		KieBaseConfiguration kBaseConfiguration = KnowledgeBaseFactory.newKnowledgeBaseConfiguration(null, classLoader);
		KieBase kbase = KnowledgeBaseFactory.newKnowledgeBase(kBaseConfiguration);

		Collection<KnowledgePackage> kpackages = kbuilder.getKnowledgePackages();
		((KnowledgeBaseImpl) kbase).addKnowledgePackages(kpackages);

		// save classloader and knowledge base
		String runtimeBaseId = createRuntimeBase(em, libraries, kbase);

		// Entity manager should be flushed so that the entity manager
		// in business engine gets the latest changes
		em.flush();

		return runtimeBaseId;
	}

	@Override
	public void destroyKnowledgeBase(String kbaseId) {
		// XXX what if the knowledge base is in use by the runtime ?

		RuntimeBaseState runtimeBase = em.find(RuntimeBaseState.class, kbaseId);
		if (runtimeBase == null) {
			throw new QRulesRuntimeException("Cannot find knowledge base.");
		}

		ClassLoaderKnowledgeBase clkb = createKnowledgeBaseFromRuntimeBase(em, runtimeBase);

		KieBase kbase = clkb.knowledgeBase;

		// XXX destroy automatically or reject request ?
		for (RuntimeSession runtimeSession : runtimeBase.getSessions()) {
			doDestroyKnowledgeSession(em, kbase, runtimeSession);
		}

		em.remove(runtimeBase);
	}

	private String createRuntimeBase(EntityManager em, List<byte[]> libraries, KieBase kbase) {
		RuntimeBaseState runtimeBase = new RuntimeBaseState();
		String runtimeBaseId = runtimeBase.getId();

		byte[] state = serializeKBaseState(kbase);
		runtimeBase.setState(state);

		List<RuntimeBaseLibrary> kbaseLibraries = new ArrayList<>();
		for (byte[] library : libraries) {
			RuntimeBaseLibrary kbaseLibrary = new RuntimeBaseLibrary();
			kbaseLibrary.setLibrary(library);

			kbaseLibrary.setBase(runtimeBase);
			kbaseLibraries.add(kbaseLibrary);
		}
		runtimeBase.setLibraries(kbaseLibraries);

		em.persist(runtimeBase);

		return runtimeBaseId;
	}

	private ClassLoaderKnowledgeBase createKnowledgeBaseFromRuntimeBase(EntityManager em, RuntimeBaseState runtimeBase) {
		List<byte[]> libraries = new ArrayList<>();
		List<RuntimeBaseLibrary> runtimeLibraries = runtimeBase.getLibraries();
		for (RuntimeBaseLibrary runtimeLibrary : runtimeLibraries) {
			byte[] library = runtimeLibrary.getLibrary();
			libraries.add(library);
		}

		// re-create classloader
		JarClassLoaderBuilder classLoaderBuilder = new JarClassLoaderBuilder();

		for (byte[] libraryBytes : libraries) {
			classLoaderBuilder.add(libraryBytes);
		}

		MapBackedClassLoader classLoader = classLoaderBuilder.buildClassLoader();

		// restore compiled knowledge base
		byte[] state = runtimeBase.getState();
		KieBase kbase = deserializeKBaseState(state, classLoader);

		return new ClassLoaderKnowledgeBase(classLoader, kbase);
	}

	private byte[] serializeKBaseState(KieBase kbase) {
		// XXX check stream close (inner/outer stream, success/fail path)
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			DroolsObjectOutputStream dos = new DroolsObjectOutputStream(baos);

			dos.writeObject(kbase);

			byte[] state = baos.toByteArray();

			dos.close();

			return state;
		}
		catch (IOException e) {
			throw new QRulesRuntimeException(e);
		}
	}

	private KieBase deserializeKBaseState(byte[] state, ClassLoader classLoader) {
		// XXX check stream close (inner/outer stream, success/fail path)
		try {
			ByteArrayInputStream bais = new ByteArrayInputStream(state);
			DroolsObjectInputStream dis = new DroolsObjectInputStream(bais, classLoader);
			KieBase kbase = (KieBase) dis.readObject();

			dis.close();

			return kbase;
		}
		catch (IOException e) {
			throw new QRulesRuntimeException(e);
		}
		catch (ClassNotFoundException e) {
			throw new QRulesRuntimeException(e);
		}
	}

	@Override
	public StatelessExecutionResults statelessExecute(String kbaseId, Map<String, byte[]> inputGlobals, List<byte[]> inputFacts) {
		return statelessExecute(kbaseId, null, inputGlobals, inputFacts);
	}

	@Override
	public StatelessExecutionResults statelessExecute(String kbaseId, String rule, Map<String, byte[]> inputGlobals, List<byte[]> inputFacts) {
		RuntimeBaseState runtimeBase = RuntimeBaseState.findById(em, kbaseId);
		if (runtimeBase == null) {
			throw new QRulesRuntimeException("Cannot find knowledge base.");
		}

		/* XXX We create a new instance of a k-base for each validation call,
		 * we should probably be caching knowledge bases in a Map.
		 * Wrt clustering, this would be a per-node cache of immutable objects,
		 * we do not support changing the k-base after creation.
		 * Wrt to k-session to k-base mapping, I recall that @perperidis was suggesting
		 * that using the same k-base from multiple threads is a bad idea,
		 * we should be caching k-base pools with one instance per client request.
		 */
		ClassLoaderKnowledgeBase clkb = createKnowledgeBaseFromRuntimeBase(em, runtimeBase);

		ClassLoader classLoader = clkb.classLoader;

		KieBase kbase = clkb.knowledgeBase;

		// globals
		Map<String, Object> globals = new LinkedHashMap<>();
		for (Entry<String, byte[]> inputGlobal : inputGlobals.entrySet()) {
			String id = inputGlobal.getKey();
			Object object = Utils.deserializeObject(classLoader, inputGlobal.getValue());
			globals.put(id, object);
		}

		List<Command<?>> commands = new ArrayList<>();
		for (Entry<String, Object> global : globals.entrySet()) {
			String id = global.getKey();
			commands.add(CommandFactory.newSetGlobal(id, global.getValue()));
		}

		// facts
		List<Object> facts = new ArrayList<>();
		for (byte[] inputFact : inputFacts) {
			Object object = Utils.deserializeObject(classLoader, inputFact);
			facts.add(object);
		}

		for (Object fact : facts) {
			commands.add(CommandFactory.newInsert(fact));
		}

		// fire
		Command<?> fireCommand = null;
		if (rule == null) {
			fireCommand = new FireAllRulesCommand();
		}
		else {
			AgendaFilter ruleNameFilter = new RuleNameEqualsAgendaFilter(rule);
			fireCommand = new FireAllRulesCommand(ruleNameFilter);
		}
		commands.add(fireCommand);

		StatelessKieSession ksession = kbase.newStatelessKieSession();
		ksession.execute(CommandFactory.newBatchExecution(commands));

		// globals
		Map<String, byte[]> outputGlobals = new LinkedHashMap<>();
		for (Entry<String, Object> object : globals.entrySet()) {
			String id = object.getKey();
			byte[] bytes = Utils.serializeObject(object.getValue());
			outputGlobals.put(id, bytes);
		}

		// facts
		List<byte[]> outputFacts = new ArrayList<>();
		for (Object object : facts) {
			byte[] bytes = Utils.serializeObject(object);
			outputFacts.add(bytes);
		}

		StatelessExecutionResults results = new StatelessExecutionResults();
		results.setGlobals(outputGlobals);
		results.setFacts(outputFacts);

		return results;
	}

	@Override
	public String createKnowledgeSession(String kbaseId) {
		RuntimeBaseState runtimeBase = RuntimeBaseState.findById(em, kbaseId);
		if (runtimeBase == null) {
			throw new QRulesRuntimeException("Cannot find knowledge base.");
		}

		ClassLoaderKnowledgeBase clkb = createKnowledgeBaseFromRuntimeBase(em, runtimeBase);

		KieBase kbase = clkb.knowledgeBase;

		KieSession ksession = JPAKnowledgeService.newStatefulKnowledgeSession(kbase, null, env);
		int ksessionId = ksession.getId();
		logger.log(Level.INFO, "New persistent knowledge session with id: {0}", ksessionId);

		RuntimeSession runtimeSession = new RuntimeSession();
		String runtimeId = runtimeSession.getId();

		runtimeSession.setSessionId(ksessionId);
		runtimeSession.setBase(runtimeBase);

		em.persist(runtimeSession);

		return runtimeId;
	}

	@Override
	public void destroyKnowledgeSession(String runtimeId) {
		RuntimeSession runtimeSession = em.find(RuntimeSession.class, runtimeId);
		if (runtimeSession == null) {
			throw new QRulesRuntimeException("Cannot find knowledge session.");
		}

		String kbaseId = runtimeSession.getBase().getId();

		RuntimeBaseState runtimeBase = RuntimeBaseState.findById(em, kbaseId);

		ClassLoaderKnowledgeBase clkb = createKnowledgeBaseFromRuntimeBase(em, runtimeBase);

		KieBase kbase = clkb.knowledgeBase;

		doDestroyKnowledgeSession(em, kbase, runtimeSession);
	}

	private void doDestroyKnowledgeSession(EntityManager em, KieBase kbase, RuntimeSession runtimeSession) {
		int ksessionId = runtimeSession.getSessionId();

		KieSession ksession = JPAKnowledgeService.loadStatefulKnowledgeSession(ksessionId, kbase, null, env);
		ksession.destroy(); // for persistent sessions

		em.remove(runtimeSession);
	}

	@Override
	public void setGlobal(String runtimeId, String globalId, byte[] inputGlobal) {
		RuntimeSession runtimeSession = em.find(RuntimeSession.class, runtimeId);
		if (runtimeSession == null) {
			throw new QRulesRuntimeException("Cannot find knowledge session.");
		}

		RuntimeGlobal existingRuntimeGlobal = RuntimeGlobal.findByGlobalId(em, runtimeId, globalId);
		if (existingRuntimeGlobal != null) {
			throw new QRulesRuntimeException("A global with the same identifier already exists in knowledge session.");
		}

		RuntimeGlobal runtimeGlobal = new RuntimeGlobal();
		runtimeGlobal.setSession(runtimeSession);
		runtimeGlobal.setGlobalId(globalId);
		runtimeGlobal.setState(inputGlobal);

		em.persist(runtimeGlobal);
	}

	@Override
	public byte[] getGlobal(String runtimeId, String globalId) {
		RuntimeSession runtimeSession = em.find(RuntimeSession.class, runtimeId);
		if (runtimeSession == null) {
			throw new QRulesRuntimeException("Cannot find knowledge session.");
		}

		RuntimeGlobal runtimeGlobal = RuntimeGlobal.findByGlobalId(em, runtimeId, globalId);
		if (runtimeGlobal == null) {
			throw new QRulesRuntimeException("Cannot find global in knowledge session.");
		}

		return runtimeGlobal.getState();
	}

	@Override
	public String insertFact(String runtimeId, byte[] fact) {
		List<byte[]> facts = Arrays.asList(fact);
		List<String> factIds = insertFacts(runtimeId, facts);
		return factIds.get(0);
	}

	@Override
	public byte[] getFact(String runtimeId, String factId) {
		List<String> factIds = Arrays.asList(factId);
		List<byte[]> facts = getFacts(runtimeId, factIds);
		return facts.get(0);
	}

	@Override
	public List<String> insertFacts(String runtimeId, List<byte[]> inputFacts) {
		RuntimeSession runtimeSession = em.find(RuntimeSession.class, runtimeId);
		if (runtimeSession == null) {
			throw new QRulesRuntimeException("Cannot find knowledge session.");
		}

		ClassLoaderKnowledgeSession clks = createKnowledgeSessionFromRuntimeSession(em, runtimeSession);

		ClassLoader classLoader = clks.classLoader;

		KieSession ksession = clks.knowledgeSession;

		List<String> factIds = new ArrayList<>();
		for (byte[] inputFact : inputFacts) {
			Object fact = Utils.deserializeObject(classLoader, inputFact);
			FactHandle handle = ksession.insert(fact);

			String factId = getIdFromHandle(handle);
			factIds.add(factId);
		}

		return factIds;
	}

	@Override
	public List<byte[]> getFacts(String runtimeId, List<String> factIds) {
		RuntimeSession runtimeSession = em.find(RuntimeSession.class, runtimeId);
		if (runtimeSession == null) {
			throw new QRulesRuntimeException("Cannot find knowledge session.");
		}

		ClassLoaderKnowledgeSession clks = createKnowledgeSessionFromRuntimeSession(em, runtimeSession);

		KieSession ksession = clks.knowledgeSession;

		List<byte[]> outputFacts = new ArrayList<>();
		for (String factId : factIds) {
			FactHandle handle = getHandleFromId(ksession, factId);
			if (handle == null) {
				throw new QRulesRuntimeException("Cannot find handle in knowledge session.");
			}

			Object fact = ksession.getObject(handle);
			if (fact == null) {
				throw new QRulesRuntimeException("Cannot find fact in knowledge session.");
			}

			byte[] outputFact = Utils.serializeObject(fact);
			outputFacts.add(outputFact);
		}

		return outputFacts;
	}

	@Override
	public void deleteFact(String runtimeId, String factId) {
		RuntimeSession runtimeSession = em.find(RuntimeSession.class, runtimeId);
		if (runtimeSession == null) {
			throw new QRulesRuntimeException("Cannot find knowledge session.");
		}

		ClassLoaderKnowledgeSession clks = createKnowledgeSessionFromRuntimeSession(em, runtimeSession);

		KieSession ksession = clks.knowledgeSession;

		FactHandle handle = getHandleFromId(ksession, factId);
		if (handle == null) {
			throw new QRulesRuntimeException("Cannot find handle in knowledge session.");
		}

		ksession.delete(handle);
	}

	private String getIdFromHandle(FactHandle handle) {
		int factId;
		if (handle instanceof InternalFactHandle) {
			InternalFactHandle internalHandle = (InternalFactHandle) handle;
			factId = internalHandle.getId();
		}
		else {
			throw new QRulesRuntimeException("Unexpected handle type.");
		}

		String stringFactId = String.valueOf(factId);

		return stringFactId;
	}

	private FactHandle getHandleFromId(KieSession ksession, String stringFactId) {
		int factId;
		try {
			factId = Integer.valueOf(stringFactId);
		}
		catch (NumberFormatException e) {
			throw new QRulesRuntimeException("Invalid fact id.");
		}

		return findFactHandleById(ksession, factId);
	}

	private FactHandle findFactHandleById(KieSession ksession, int id) {
		Collection<FactHandle> existingHandles = ksession.getFactHandles();
		for (FactHandle existingHandle : existingHandles) {
			if (existingHandle instanceof InternalFactHandle) {
				InternalFactHandle internalHandle = (InternalFactHandle) existingHandle;
				if (internalHandle.getId() == id) {
					return existingHandle;
				}
			}
			else {
				throw new QRulesRuntimeException("Unexpected handle type.");
			}
		}
		return null;
	}

	private ClassLoaderKnowledgeSession createKnowledgeSessionFromRuntimeSession(EntityManager em, RuntimeSession runtimeSession) {
		String kbaseId = runtimeSession.getBase().getId();

		RuntimeBaseState runtimeBase = RuntimeBaseState.findById(em, kbaseId);

		ClassLoaderKnowledgeBase clkb = createKnowledgeBaseFromRuntimeBase(em, runtimeBase);

		MapBackedClassLoader classLoader = clkb.classLoader;

		KieBase kbase = clkb.knowledgeBase;

		int ksessionId = runtimeSession.getSessionId();

		KieSession ksession = JPAKnowledgeService.loadStatefulKnowledgeSession(ksessionId, kbase, null, env);

		return new ClassLoaderKnowledgeSession(classLoader, ksession);
	}

	@Override
	public void fireRules(String runtimeId, final List<String> rules) {
		RuntimeSession runtimeSession = em.find(RuntimeSession.class, runtimeId);
		if (runtimeSession == null) {
			throw new QRulesRuntimeException("Cannot find knowledge session.");
		}

		ClassLoaderKnowledgeSession clks = createKnowledgeSessionFromRuntimeSession(em, runtimeSession);

		ClassLoader classLoader = clks.classLoader;

		KieSession ksession = clks.knowledgeSession;

		// restore globals
		List<RuntimeGlobal> runtimeGlobals = runtimeSession.getGlobals();
		for (RuntimeGlobal runtimeGlobal : runtimeGlobals) {
			String globalId = runtimeGlobal.getGlobalId();
			byte[] bytes = runtimeGlobal.getState();

			Object global = Utils.deserializeObject(classLoader, bytes);
			runtimeGlobal.setObject(global);

			ksession.setGlobal(globalId, global);
		}

		// fire rules
		if (rules == null) {
			ksession.fireAllRules();
		}
		else {
			AgendaFilter filter = new AgendaFilter() {
				@Override public boolean accept(Match match) {
					String matchedRule = match.getRule().getName();
					return rules.contains(matchedRule);
				}
			};

			ksession.fireAllRules(filter);
		}

		// save globals
		for (RuntimeGlobal runtimeGlobal : runtimeGlobals) {
			Object global = runtimeGlobal.getObject();

			byte[] bytes = Utils.serializeObject(global);
			runtimeGlobal.setState(bytes);
		}
	}

	@Override
	public List<Map<String, byte[]>> getQueryResults(String runtimeId, String query, List<byte[]> inputArguments) {
		RuntimeSession runtimeSession = em.find(RuntimeSession.class, runtimeId);
		if (runtimeSession == null) {
			throw new QRulesRuntimeException("Cannot find knowledge session.");
		}

		ClassLoaderKnowledgeSession clks = createKnowledgeSessionFromRuntimeSession(em, runtimeSession);

		ClassLoader classLoader = clks.classLoader;

		KieSession ksession = clks.knowledgeSession;

		// arguments
		List<Object> arguments = new ArrayList<>();
		for (byte[] inputArgument : inputArguments) {
			Object object = Utils.deserializeObject(classLoader, inputArgument);
			arguments.add(object);
		}

		QueryResults queryResults = ksession.getQueryResults(query, arguments.toArray());

		String[] identifiers = queryResults.getIdentifiers();

		// results
		List<Map<String, byte[]>> results = new ArrayList<>();
		for (QueryResultsRow queryResultsRow : queryResults) {
			Map<String, byte[]> row = new LinkedHashMap<>();
			for (String identifier : identifiers) {
				Object object = queryResultsRow.get(identifier);
				byte[] bytes = Utils.serializeObject(object);
				row.put(identifier, bytes);
			}
			results.add(row);
		}

		return results;
	}

	@Override
	public String findKnowledgeBaseIdBySessionId(String runtimeId) {
		RuntimeSession runtimeSession = em.find(RuntimeSession.class, runtimeId);
		if (runtimeSession == null) {
			throw new QRulesRuntimeException("Cannot find knowledge session.");
		}

		RuntimeBaseState runtimeBase = runtimeSession.getBase();

		return runtimeBase.getId();
	}

}
