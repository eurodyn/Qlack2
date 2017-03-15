package com.eurodyn.qlack2.webdesktop.api.notifications;

public class NotificationBoard {
	public boolean show;
	public boolean badge;

	public NotificationBoard() {
	}

	public NotificationBoard(boolean show, boolean badge) {
		this.show = show;
		this.badge = badge;
	}
}