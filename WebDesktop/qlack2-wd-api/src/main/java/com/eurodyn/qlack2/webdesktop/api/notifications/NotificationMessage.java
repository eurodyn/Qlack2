package com.eurodyn.qlack2.webdesktop.api.notifications;

import java.io.Serializable;

/**
 * This is the server-side counterpart of the JS Notification object that
 * applications use to send client-side notifications (see NotificationSrv.coffee).
 * @author European Dynamics SA
 *
 */
@SuppressWarnings("unused")
public class NotificationMessage implements Serializable {
	private static final long serialVersionUID = -2888801443372975828L;
	private String title;
	private String content;
	private boolean audio = false;
	private String icon;
	private boolean error = false;
	private NotificationBoard board;
	private NotificationBubble bubble;

	public NotificationMessage(String title, String content, boolean audio,
			String icon, boolean error, NotificationBoard board,
			NotificationBubble bubble) {
		this.title = title;
		this.content = content;
		this.audio = audio;
		this.icon = icon;
		this.error = error;
		this.board = board;
		this.bubble = bubble;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public boolean isAudio() {
		return audio;
	}

	public void setAudio(boolean audio) {
		this.audio = audio;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public boolean isError() {
		return error;
	}

	public void setError(boolean error) {
		this.error = error;
	}

	public NotificationBoard getBoard() {
		return board;
	}

	public void setBoard(NotificationBoard board) {
		this.board = board;
	}

	public NotificationBubble getBubble() {
		return bubble;
	}

	public void setBubble(NotificationBubble bubble) {
		this.bubble = bubble;
	}

	public NotificationMessage() {

	}

	public static Builder builder() {
		return new Builder();
	}

	public static class Builder {
		private String title;
		private String content;
		private boolean audio = false;
		private String icon = "fa-info-circle";
		private boolean error = false;
		private NotificationBoard board;
		private NotificationBubble bubble;

		public NotificationMessage build() {
			return new NotificationMessage(title, content, audio, icon, error, board, bubble);
		}

		public Builder title(String title) {
			this.title = title;
			return this;
		}
		public Builder content(String content) {
			this.content = content;
			return this;
		}
		public Builder audio(boolean audio) {
			this.audio = audio;
			return this;
		}
		public Builder icon(String icon) {
			this.icon = icon;
			return this;
		}
		public Builder error(boolean error) {
			this.error = error;
			return this;
		}
		public Builder board(boolean show, boolean badge) {
			this.board = new NotificationBoard(show, badge);
			return this;
		}
		public Builder setBubble(boolean show, long timeout) {
			this.bubble = new NotificationBubble(show, timeout);
			return this;
		}
	}

}
