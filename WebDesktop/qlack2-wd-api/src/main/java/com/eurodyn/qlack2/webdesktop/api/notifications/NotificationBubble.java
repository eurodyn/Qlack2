package com.eurodyn.qlack2.webdesktop.api.notifications;

public class NotificationBubble {
	public boolean show;
	public long timeout;

	public NotificationBubble(boolean show, long timeout) {
		this.show = show;
		this.timeout = timeout;
	}

	public NotificationBubble() {
	}
}