package com.eurodyn.qlack2.fuse.aaa.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.naming.AuthenticationException;
import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchResult;
import javax.persistence.EntityManager;

import com.eurodyn.qlack2.fuse.aaa.impl.model.Group;
import com.eurodyn.qlack2.fuse.aaa.impl.model.User;
import com.eurodyn.qlack2.fuse.aaa.impl.model.UserAttribute;

public class LdapUserUtil {
	private static final Logger LOGGER = Logger.getLogger(LdapUserUtil.class.getName());

	private EntityManager em;

	private boolean ldapEnable;
	private String ldapUrl;
	private String ldapBaseDN;
	private String ldapMappingUid;
	private String ldapMappingGid;

	private Map<String, String> attributesMap;

	public void setEm(EntityManager em) {
		this.em = em;
	}

	public void setLdapEnable(boolean ldapEnable) {
		this.ldapEnable = ldapEnable;
	}

	public void setLdapUrl(String ldapUrl) {
		this.ldapUrl = ldapUrl;
	}

	public void setLdapBaseDN(String ldapBaseDN) {
		this.ldapBaseDN = ldapBaseDN;
	}

	public void setLdapMappingUid(String ldapMappingUid) {
		this.ldapMappingUid = ldapMappingUid;
	}

	public void setLdapMappingGid(String ldapMappingGid) {
		this.ldapMappingGid = ldapMappingGid;
	}

	public void setLdapMappingAttrs(String ldapMappingAttrs) {
		attributesMap = new HashMap<>();
		String[] mappings = ldapMappingAttrs.split(",");
		for (String mapping : mappings) {
			String[] names = mapping.split("-");
			attributesMap.put(names[0], names[1]);
		}
	}

	/**
	 * Check if the user can be authenticated with LDAP using 'simple'
	 * authentication (bind operation).
	 *
	 * @param username
	 *            The LDAP username of the user.
	 * @param password
	 *            The LDAP password of the user.
	 * @return The AAA ID of the user if authenticated, null otherwise.
	 */
	public String canAuthenticate(String username, String password) {
		if (!ldapEnable) {
			return null;
		}

		DirContext ctx = ldapBind(username, password);
		if (ctx != null) {
			Map<String, List<String>> ldap = ldapSearch(ctx, username);
			if (ldap != null) {
				User user = User.findByUsername(username, em);
				if (user == null) {
					String userId = createUserFromLdap(username, ldap);
					ldapUnbind(ctx);
					return userId;
				}
				else {
					String userId = user.getId();
					userId = updateUserFromLdap(user, ldap);
					ldapUnbind(ctx);
					return userId;
				}
			}
			else {
				ldapUnbind(ctx);
				return null;
			}
		}
		else {
			return null;
		}
	}

	private DirContext ldapBind(String username, String password) {
		Hashtable<String, String> env = new Hashtable<>();
		env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
		env.put(Context.PROVIDER_URL, ldapUrl);

		env.put(Context.SECURITY_AUTHENTICATION, "simple");
		env.put(Context.SECURITY_PRINCIPAL, ldapMappingUid + "=" + username + "," + ldapBaseDN);
		env.put(Context.SECURITY_CREDENTIALS, password);

		try {
			DirContext ctx = new InitialDirContext(env);
			return ctx;
		}
		catch (AuthenticationException e) {
			LOGGER.log(Level.FINE, "Cannot bind user to ldap service", e);
			return null;
		}
		catch (NamingException e) {
			LOGGER.log(Level.WARNING, "Cannot bind user to ldap service", e);
			return null;
		}
	}

	private Map<String, List<String>> ldapSearch(DirContext ctx, String username) {
		try {
			NamingEnumeration<SearchResult> results =
					ctx.search(ldapBaseDN, "(" + ldapMappingUid + "=" + username + ")", null);
			if (results.hasMore()) {
				SearchResult result = results.next();
				Attributes attributes = result.getAttributes();

				Map<String, List<String>> untypedResult = new HashMap<>();
				NamingEnumeration<? extends Attribute> attributesEnumeration = attributes.getAll();
				while (attributesEnumeration.hasMore()) {
					Attribute attribute = attributesEnumeration.next();
					String id = attribute.getID();
					LOGGER.log(Level.FINEST, "{0}:", id);

					List<String> untypedValues = new ArrayList<>();
					NamingEnumeration<?> valuesEnumeration = attribute.getAll();
					while (valuesEnumeration.hasMore()) {
						Object value = valuesEnumeration.next();
						LOGGER.log(Level.FINEST, "\t{0}", value);
						if (value instanceof String) {
							String string = (String) value;
							untypedValues.add(string);
						}
					}

					untypedResult.put(id, untypedValues);
				}

				results.close();
				return untypedResult;
			}
			else {
				results.close();
				return null;
			}
		}
		catch (NamingException e) {
			LOGGER.log(Level.WARNING, "Cannot search ldap context", e);
			return null;
		}
	}

	private void ldapUnbind(DirContext ctx) {
		try {
			ctx.close();
		}
		catch (NamingException e) {
			LOGGER.log(Level.WARNING, "Cannot close ldap context", e);
		}
	}

	private String createUserFromLdap(String username, Map<String, List<String>> ldap) {
		User user = new User();
		String userId = user.getId();

		user.setUsername(username);
		user.setPassword("external");
		user.setSalt("external");
		user.setStatus((byte) 1);
		user.setSuperadmin(false);
		user.setExternal(true);

		String groupId = addGroupFromLdap(user, ldap);
		if (groupId == null) {
			return null;
		}

		em.persist(user);

		createUserAttributesFromLdap(user, ldap);

		return userId;
	}

	private String addGroupFromLdap(User user, Map<String, List<String>> ldap) {
		String groupId = getFirst(ldap, ldapMappingGid);
		if (groupId == null) {
			return null;
		}

		Group group = em.find(Group.class, groupId);
		if (group == null) {
			return null;
		}

		List<User> users = group.getUsers();
		users.add(user);

		List<Group> groups = new ArrayList<Group>();
		groups.add(group);
		user.setGroups(groups);

		return groupId;
	}

	private void createUserAttributesFromLdap(User user, Map<String, List<String>> ldap) {

		for (Entry<String, String> entry : attributesMap.entrySet()) {
			String aaaAttr = entry.getKey();
			String ldapAttr = entry.getValue();

			UserAttribute attribute = new UserAttribute();
			attribute.setUser(user);
			attribute.setName(aaaAttr);
			attribute.setData(getFirst(ldap, ldapAttr));
			em.persist(attribute);
		}
	}

	private String updateUserFromLdap(User user, Map<String, List<String>> ldap) {
		String userId = user.getId();

		String groupId = updateGroupFromLdap(user, ldap);
		if (groupId == null) {
			return null;
		}

		updateUserAttributesFromLdap(user, ldap);

		return userId;
	}

	private String updateGroupFromLdap(User user, Map<String, List<String>> ldap) {
		Group oldGroup = user.getGroups().get(0);
		String oldGroupId = oldGroup.getId();

		String newGroupId = getFirst(ldap, ldapMappingGid);
		if (newGroupId == null) {
			return null;
		}

		Group newGroup = em.find(Group.class, newGroupId);
		if (newGroup == null) {
			return null;
		}

		if (!newGroupId.equals(oldGroupId)) {
			oldGroup.getUsers().remove(user);
			newGroup.getUsers().add(user);
			user.getGroups().remove(oldGroup);
			user.getGroups().add(newGroup);
		}

		return newGroupId;
	}

	private void updateUserAttributesFromLdap(User user, Map<String, List<String>> ldap) {
		List<UserAttribute> attributes = user.getUserAttributes();

		for (Entry<String, String> entry : attributesMap.entrySet()) {
			String aaaAttr = entry.getKey();
			String ldapAttr = entry.getValue();

			UserAttribute attribute = findByName(attributes, aaaAttr);
			if (attribute == null) {
				continue;
			}

			attribute.setData(getFirst(ldap, ldapAttr));
		}

	}

	private UserAttribute findByName(List<UserAttribute> attributes, String name) {
		for (UserAttribute attribute : attributes) {
			if (attribute.getName().equals(name)) {
				return attribute;
			}
		}
		return null;
	}

	private String getFirst(Map<String, List<String>> ldap, String key) {
		List<String> values = ldap.get(key);
		if (values != null && !values.isEmpty()) {
			return values.get(0);
		}
		else {
			return null;
		}
	}

}
