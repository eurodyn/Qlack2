package com.eurodyn.qlack2.util.jndi.alias.internal;

import com.eurodyn.qlack2.util.jndi.alias.JndiAliasService;
import org.apache.commons.lang3.StringUtils;
import org.apache.karaf.jndi.JndiService;

import java.time.Instant;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Creates JNDI aliases based on .cfg properties. Aliases are defined as:
 * source1|target1,source2|target2
 */
public class JndiAliasServiceImpl implements JndiAliasService {
    // JUL reference.
    private final static Logger LOGGER = Logger.getLogger(JndiAliasServiceImpl.class.getName());

    /** The time to wait for each alias. Note that KARAF blacklists bundles with very long initialisation times */
    private long waitFor;

    /** Aliases to create in the form source|target,source|target, etc. Notice that target should begin with "/" */
    private String aliases;

    /** A reference to Karaf's JNDI service */
    private JndiService jndiService;

    /** The amount of time to wait between consecutive tries when the source side of the alias is missing */
    private long waitBetweenTries = 1000;

    public void setWaitFor(long waitFor) {
        this.waitFor = waitFor;
    }

    public void setAliases(String aliases) {
        this.aliases = aliases;
    }

    public void setJndiService(JndiService jndiService) {
        this.jndiService = jndiService;
    }

    /**
     * Checks if the given JNDI name is already registered.
     * @param entry The entry to check.
     * @return If the entry is already registered in JDNI, false otherwise.
     * @throws Exception
     */
    private boolean jndiEntryExists(String entry) throws Exception {
        final Map<String, String> names = jndiService.names();
        if (entry.startsWith("/")) {
            entry = entry.substring(1);
        }
        return names.containsKey(entry);
    }

    /** Bundle intialisation to create aliases */
    public void init() throws Exception {
        LOGGER.log(Level.INFO, "Initialising JndiAliasServiceImpl.");
        final String[] pairs = aliases.split(",");

        if (pairs.length == 0) {
            LOGGER.log(Level.FINE, "Did not find any aliases in the configuration file.");
        } else {
            for (String pair : pairs) {
                LOGGER.log(Level.FINE, "Found alias in configuration file: {0}", pair);
                if (StringUtils.isNotEmpty(pair)) {
                    LOGGER.log(Level.FINE, "Processing alias: {0}", pair);
                    if (pair.split("\\|").length == 2) {
                        final String source = pair.split("\\|")[0];
                        String target = pair.split("\\|")[1];

                        if (jndiEntryExists(target)) {
                            LOGGER.log(Level.FINE, "JNDI name {0} already exists, skipping alias creation.", target);
                        } else {
                            long startTime = Instant.now().toEpochMilli();
                            while (Instant.now().toEpochMilli() - startTime < waitFor) {
                                LOGGER.log(Level.FINE, "Waiting for source {0} to become available.", source);
                                boolean exists = jndiEntryExists(source);
                                if (exists) {
                                    if (!target.startsWith("/")) {
                                        target = "/" + target;
                                    }
                                    jndiService.alias(source, target);
                                    LOGGER.log(Level.INFO, "JNDI alias {0} created for {1}.", new Object[]{target, source});
                                    break;
                                } else {
                                    Thread.sleep(waitBetweenTries);
                                }
                            }
                        }
                    } else {
                        LOGGER.log(Level.WARNING, "Found an invalid alias pair: {0}.", pair);
                    }
                }
            }
        }

    }

}
