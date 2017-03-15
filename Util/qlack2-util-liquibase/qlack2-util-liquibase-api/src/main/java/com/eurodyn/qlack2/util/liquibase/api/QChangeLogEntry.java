package com.eurodyn.qlack2.util.liquibase.api;

import org.osgi.framework.Bundle;

import java.time.Instant;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

/**
 * A data-holder for information pertaining to a discovered changelog within an
 * OSGi bundle.
 */
public class QChangeLogEntry {
    /**
     * The point in time this changelog was discovered.
     */
    private Instant addedOn;

    /**
     * The bundle in which this changelog was discovered.
     */
    private Bundle bundle;

    /**
     * The priority header of this changelog (Q-Liquibase-Priority).
     */
    private int priority;

    /**
     * The location of the changelog.
     */
    private String changeLog;

    /**
     * Generic properties to be passed directly to the underling Liquibase driver.
     */
    private Map<String, Object> properties = new HashMap<>();

    public Instant getAddedOn() {
        return addedOn;
    }

    public void setAddedOn(Instant addedOn) {
        this.addedOn = addedOn;
    }

    public Bundle getBundle() {
        return bundle;
    }

    public void setBundle(Bundle bundle) {
        this.bundle = bundle;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    @Override
    public boolean equals(Object obj) {
        return bundle.getSymbolicName().equals(((QChangeLogEntry) obj).getBundle().getSymbolicName());
    }

    @Override
    public String toString() {
        return "QChangeLogEntry{" +
                "addedOn=" + addedOn +
                ", bundle=" + bundle +
                ", priority=" + priority +
                ", changeLog='" + changeLog + '\'' +
                ", properties=" + properties +
                '}';
    }

    public static class QChangeLogEntryComparator implements Comparator<QChangeLogEntry> {
        @Override
        public int compare(QChangeLogEntry o1, QChangeLogEntry o2) {
            return new Integer(o1.getPriority()).compareTo(new Integer(o2.getPriority()));
        }
    }

    public void setProperty(String name, Object value) {
        properties.put(name, value);
    }

    public Object getProperty(String name) {
        return properties.get(name);
    }

    public Map<String, Object> getProperties() {
        return properties;
    }

    public String getChangeLog() {
        return changeLog;
    }

    public void setChangeLog(String changeLog) {
        this.changeLog = changeLog;
    }
}

