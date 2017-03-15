package com.eurodyn.qlack2.util.liquibase.impl;

import com.eurodyn.qlack2.util.liquibase.api.MigrationDiscoveryMode;
import com.eurodyn.qlack2.util.liquibase.api.MigrationExecutionStrategy;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A class of settings for this component, specified in OSGi config-admin under
 * `etc/com.eurodyn.qlack2.util.liquibase.cfg`.
 */
public class Settings {
    /**
     * JUL reference
     */
    private final static Logger LOGGER = Logger.getLogger(Settings.class.getName());

    public static final String HEADER_Q_LIQUIBASE_CHANGELOG = "Q-Liquibase-ChangeLog";
    public static final String HEADER_Q_LIQUIBASE_PRIORITY = "Q-Liquibase-Priority";
    public static final int LIQUIBASE_DEFAULT_PRIORITY = 100;
    public final String LIQUIBASE_PARAMETER_PREFIX = "liquibase.";

    /**
     * The names of the configuration keys in config-admin.
     */
    private enum CONFIG_KEYS {
        migrationExecutionStrategy, datasource, maxWaitForDS, maxWaitForSL,
        bootCompleteSL, initialExecutionDelay, discoveryMode, liquibase
    }

    ;

    /**
     * A flag indicating configuration has been set by config admin
     */
    private boolean configSet = false;

    /**
     * Sets the changelogs execution strategy to be used
     */
    private MigrationExecutionStrategy migrationExecutionStrategy = MigrationExecutionStrategy.START_LEVEL;

    /**
     * The name of the datasource against which changelogs will be executed.
     */
    private String datasource;

    /**
     * The number of msec the component is waiting for the datasource to appear in
     * JDNI. If this duration expires without having managed to obtain a reference to
     * the datasource the component fails.
     */
    private long maxWaitForDS = 180000;

    /**
     * The number of msec the component is waiting for requested start-level to be
     * reached. If this duration expires without having reached the desired start-level
     * the component fails.
     */
    private long maxWaitForSL = 300000;

    /**
     * The start-level in which you consider bootstrap having finished. Be careful that
     * at least one of the bundles being deployed in your OSGi framework are actually
     * set to start at this level.
     */
    private int bootCompleteSL = 100;

    /**
     * As soon as all preconditions are met (the migration strategy as well as the
     * registration of the datasource), this is an additional delay being applied before
     * changelogs start to be executed.
     */
    private long initialExecutionDelay = 5000;

    /**
     * The discovery mode to use.
     */
    private MigrationDiscoveryMode discoveryMode = MigrationDiscoveryMode.AUTO;

    /**
     * An arbitrary list of parameters to be passed directly to Liquibase.
     */
    private Map<String, String> liquibase;

    private void addLiquibaseProperty(String key, String value) {
        if (liquibase == null) {
            liquibase = new HashMap<>();
        }
        liquibase.put(key, value);
    }

    public MigrationExecutionStrategy getMigrationExecutionStrategy() {
        return migrationExecutionStrategy;
    }

    /** A helper converter from Dictionary to a Map */
    private static <K, V> Map<K, V> dictionaryToMap(Dictionary<K, V> dictionary) {
        if (dictionary == null) {
            return null;
        }
        Map<K, V> map = new HashMap<K, V>(dictionary.size());
        Enumeration<K> keys = dictionary.keys();
        while (keys.hasMoreElements()) {
            K key = keys.nextElement();
            map.put(key, dictionary.get(key));
        }
        return map;
    }

    public String getDatasource() {
        return datasource;
    }

    public long getMaxWaitForDS() {
        return maxWaitForDS;
    }

    public long getMaxWaitForSL() {
        return maxWaitForSL;
    }

    public int getBootCompleteSL() {
        return bootCompleteSL;
    }

    public long getInitialExecutionDelay() {
        return initialExecutionDelay;
    }

    public MigrationDiscoveryMode getDiscoveryMode() {
        return discoveryMode;
    }

    public Map<String, String> getLiquibase() {
        return liquibase;
    }

    public boolean isConfigSet() {
        return configSet;
    }

    /** A helper function to set config admin properties manually read */
    public void setConfigAdmin(Dictionary<String, ?> properties) {
        setConfigAdmin(dictionaryToMap(properties));
    }

    /**
     * Config admin setter
     */
    public void setConfigAdmin(Map<String, ?> properties) {
        LOGGER.log(Level.CONFIG, "Setting liquibase properties from config admin.");
        for (String key : properties.keySet()) {
            String value = (String) properties.get(key);

            if (key.equals(CONFIG_KEYS.migrationExecutionStrategy.toString())) {
                migrationExecutionStrategy = MigrationExecutionStrategy.valueOf(value);
            } else if (key.equals(CONFIG_KEYS.datasource.toString())) {
                datasource = value;
            } else if (key.equals(CONFIG_KEYS.bootCompleteSL.toString())) {
                bootCompleteSL = Integer.parseInt(value);
            } else if (key.equals(CONFIG_KEYS.discoveryMode.toString())) {
                discoveryMode = MigrationDiscoveryMode.valueOf(value);
            } else if (key.equals(CONFIG_KEYS.initialExecutionDelay.toString())) {
                initialExecutionDelay = Long.parseLong(value);
            } else if (key.equals(CONFIG_KEYS.maxWaitForDS.toString())) {
                maxWaitForDS = Long.parseLong(value);
            } else if (key.equals(CONFIG_KEYS.maxWaitForSL.toString())) {
                maxWaitForSL = Long.parseLong(value);
            } else if (key.startsWith(LIQUIBASE_PARAMETER_PREFIX)) {
                String plainKey = key.substring(
                        LIQUIBASE_PARAMETER_PREFIX.length());
                addLiquibaseProperty(plainKey, value);
            }
        }
        configSet = true;
        LOGGER.log(Level.FINE, this.toString());
    }

    @Override
    public String toString() {
        return "Settings {" +
                "migrationExecutionStrategy=" + migrationExecutionStrategy +
                ", datasource='" + datasource + '\'' +
                ", maxWaitForDS=" + maxWaitForDS +
                ", maxWaitForSL=" + maxWaitForSL +
                ", bootCompleteSL=" + bootCompleteSL +
                ", initialExecutionDelay=" + initialExecutionDelay +
                ", discoveryMode=" + discoveryMode +
                (liquibase != null && liquibase.entrySet() != null ?
                    ", liquibase=" + Arrays.toString(liquibase.entrySet().toArray()) : "") +
                '}';
    }
}
