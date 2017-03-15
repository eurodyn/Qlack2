package com.eurodyn.qlack2.webdesktop.api.util;

import javax.ws.rs.core.MultivaluedMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Constants {
    public static final String OP_ACCESS_APPLICATION = "WD_ACCESS_APPLICATION";
    public static final String OP_UPDATE_APPLICATION = "WD_UPDATE_APPLICATION";
    public static final String OP_MANAGE_GROUPS = "WD_MANAGE_GROUPS";
    public static final String OP_MANAGE_USERS = "WD_MANAGE_USERS";

    /**
     * This is a helper function to extract the token header from a list of headers.
     * Since each application may use a different name for the header, this method
     * extracts the first header found matching 'X-Qlack-Fuse-IDM-Token-.*'.
     *
     * @param httpHeaders
     * @return The value of the header matching 'X-Qlack-Fuse-IDM-Token-.*' or null.
     */
    public static final String getTicketHeader(MultivaluedMap<String, String> httpHeaders) {
        final List<String> list = httpHeaders.keySet().stream().filter(
                p -> p.matches("X-Qlack-Fuse-IDM-Token-.*")).collect(Collectors.toList());
        return list.isEmpty() ? null : httpHeaders.getFirst(list.get(0));
    }

    /**
     * This is a helper function to extract the name of the http header holding
     * the security token. Since each application may use a different name for
     * the header, this method extracts the first header found matching
     * 'X-Qlack-Fuse-IDM-Token-.*'.
     *
     * @param httpHeaders
     * @return The name of the header matching 'X-Qlack-Fuse-IDM-Token-.*' or null.
     */
    public static final String getTicketHeaderName(Map<String, String> httpHeaders) {
        final List<String> list = httpHeaders.keySet().stream().filter(
                p -> p.matches("X-Qlack-Fuse-IDM-Token-.*")).collect(Collectors.toList());
        return list.isEmpty() ? null : list.get(0);
    }

    public enum SecurityEvent {
        ALLOW, DENY, REMOVE;
    }

    // The default topic on which Web Desktop is listening for Atmosphere
    // events.
    public static final String ATMOSPHERE_WEB_DESKTOP_TOPIC = "/web-desktop/default";
    // The topic under which users subscribe for private messages.
    public static final String ATMOSPHERE_PRIVATE_TOPIC_PREFIX = "/web-desktop/private";
}
