package com.marked.vifo.model;

import android.os.Bundle;

import com.marked.vifo.extra.MessageConstants;

import java.io.Serializable;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by Tudor Pop on 04-Dec-15.
 */
public final class Message implements Serializable, MessageConstants {
	private final String collapseKey;
	private final Boolean delayWhileIdle;
	private final Integer timeToLive;
	private final Map<String, String> data;
	private final Map<String, String> notificationParams;
	private final Boolean dryRun;
	private final String restrictedPackageName;
	private int messageType;

	private Message(Builder builder) {
		collapseKey = builder.collapseKey;
		delayWhileIdle = builder.delayWhileIdle;
		data = Collections.unmodifiableMap(builder.data);
		notificationParams = Collections.unmodifiableMap(builder.notificationParams);
		timeToLive = builder.timeToLive;
		dryRun = builder.dryRun;
		restrictedPackageName = builder.restrictedPackageName;
		messageType = builder.viewType;
	}


	/**
	 * Gets the message type.
	 */
	public int getMessageType() {
		return messageType;
	}

	/**
	 * Gets the collapse key.
	 */
	public String getCollapseKey() {
		return collapseKey;
	}

	/**
	 * Gets the delayWhileIdle flag.
	 */
	public Boolean isDelayWhileIdle() {
		return delayWhileIdle;
	}

	/**
	 * Gets the time to live (in seconds).
	 */
	public Integer getTimeToLive() {
		return timeToLive;
	}

	/**
	 * Gets the dryRun flag.
	 */
	public Boolean isDryRun() {
		return dryRun;
	}

	/**
	 * Gets the restricted package name.
	 */
	public String getRestrictedPackageName() {
		return restrictedPackageName;
	}

	/**
	 * Gets the payload data, which is immutable.
	 */
	public Map<String, String> getData() {
		return data;
	}

	/**
	 * Gets the notification params, which are immutable.
	 */
	public Map<String, String> getNotificationParams() {
		return notificationParams;
	}

	public Bundle toBundle() {
		Bundle bundle = new Bundle();
		if (data.containsKey(COLLAPSE_OPTION))
			bundle.putString(COLLAPSE_OPTION, data.get(COLLAPSE_OPTION));
		if (data.containsKey(TIME_TO_LIVE_OPTION))
			bundle.putString(TIME_TO_LIVE_OPTION, data.get(TIME_TO_LIVE_OPTION));
		if (data.containsKey(DELAY_WHILE_IDLE_OPTION))
			bundle.putString(DELAY_WHILE_IDLE_OPTION, data.get(DELAY_WHILE_IDLE_OPTION));
		if (data.containsKey(DRY_RUN_OPTION))
			bundle.putString(DRY_RUN_OPTION, data.get(DRY_RUN_OPTION));
		if (data.containsKey(RESTRICTED_PACKAGE_NAME_OPTION))
			bundle.putString(RESTRICTED_PACKAGE_NAME_OPTION, data.get(RESTRICTED_PACKAGE_NAME_OPTION));
		if (data.containsKey(TEXT_PAYLOAD))
			bundle.putString(TEXT_PAYLOAD, data.get(TEXT_PAYLOAD));

		return bundle;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder("Message(");
		if (collapseKey != null) {
			builder.append(COLLAPSE_OPTION + "=").append(collapseKey).append(", ");
		}
		if (timeToLive != null) {
			builder.append(TIME_TO_LIVE_OPTION + "timeToLive=").append(timeToLive).append(", ");
		}
		if (delayWhileIdle != null) {
			builder.append(DELAY_WHILE_IDLE_OPTION + "=").append(delayWhileIdle).append(", ");
		}
		if (dryRun != null) {
			builder.append("dryRun:").append(dryRun).append(", ");
		}
		if (restrictedPackageName != null) {
			builder.append(RESTRICTED_PACKAGE_NAME_OPTION + "=").append(restrictedPackageName)
			       .append(", ");
		}
		appendMap(builder, "data", data);
		appendMap(builder, "notificationParams", notificationParams);
		// Remove trailing ", "
		if (builder.charAt(builder.length() - 1) == ' ') {
			builder.delete(builder.length() - 2, builder.length());
		}
		builder.append(")");
		return builder.toString();
	}

	private void appendMap(StringBuilder builder, String name, Map<String, String> map) {
		if (!map.isEmpty()) {
			builder.append(name).append("= {");
			for (Map.Entry<String, String> entry : map.entrySet()) {
				builder.append(entry.getKey()).append("=").append(entry.getValue()).append(",");
			}
			// Remove trailing ","
			builder.delete(builder.length() - 1, builder.length());
			builder.append("}, ");
		}
	}

	public static final class Builder {

		private final Map<String, String> data;
		private final Map<String, String> notificationParams;

		// optional parameters
		private String collapseKey;
		private Boolean delayWhileIdle;
		private Integer timeToLive;
		private Boolean dryRun;
		private String restrictedPackageName;

		private int viewType;

		public Builder() {
			this.data = new LinkedHashMap<>();
			this.notificationParams = new LinkedHashMap<>();
		}

		/**
		 * Adds a key/value pair to the payload data.
		 */
		public Builder addData(String key, String value) {
			data.put(key, value);
			return this;
		}

		/**
		 * Adds a bundle to the payload data.
		 */
		public Builder addData(Bundle bundle) {
			if (collapseKey != null)
				data.put(COLLAPSE_OPTION, bundle.getString(COLLAPSE_OPTION));
			if (timeToLive != null)
				data.put(TIME_TO_LIVE_OPTION, bundle.getString(TIME_TO_LIVE_OPTION));
			if (delayWhileIdle != null)
				data.put(DELAY_WHILE_IDLE_OPTION, bundle.getString(DELAY_WHILE_IDLE_OPTION));
			if (dryRun != null)
				data.put(DRY_RUN_OPTION, bundle.getString(DRY_RUN_OPTION));
			if (restrictedPackageName != null)
				data.put(RESTRICTED_PACKAGE_NAME_OPTION, bundle.getString(RESTRICTED_PACKAGE_NAME_OPTION));

			/*data:{'text':'very long string'}*/
			data.put(TEXT_PAYLOAD, bundle.getString(TEXT_PAYLOAD));
			return this;
		}

		/**
		 * Sets the collapseKey property.
		 */
		public Builder collapseKey(String value) {
			collapseKey = value;
			return this;
		}

		/**
		 * Sets the delayWhileIdle property (default value is {@literal false}).
		 */
		public Builder delayWhileIdle(boolean value) {
			delayWhileIdle = value;
			return this;
		}
		/**
		 * Sets the messageType property (default value is {@literal 0}).
		 * MessageType is defined in MessageConstants.MessageType( ME, YOU, PHOTO, VIDEO )
		 */
		public Builder viewType(int value) {
			viewType = value;
			return this;
		}


		/**
		 * Sets the time to live, in seconds.
		 */
		public Builder timeToLive(int value) {
			timeToLive = value;
			return this;
		}


		/**
		 * Sets the dryRun property (default value is {@literal false}).
		 */
		public Builder dryRun(boolean value) {
			dryRun = value;
			return this;
		}

		/**
		 * Sets the restrictedPackageName property.
		 */
		public Builder restrictedPackageName(String value) {
			restrictedPackageName = value;
			return this;
		}

		/**
		 * Sets the notification icon.
		 */
		public Builder notificationIcon(String value) {
			notificationParams.put(NOTIFICATION_ICON, value);
			return this;
		}

		/**
		 * Sets the notification title text.
		 */
		public Builder notificationTitle(String value) {
			notificationParams.put(NOTIFICATION_TITLE, value);
			return this;
		}

		/**
		 * Sets the notification body text.
		 */
		public Builder notificationBody(String value) {
			notificationParams.put(NOTIFICATION_BODY, value);
			return this;
		}

		/**
		 * Sets the notification click action.
		 */
		public Builder notificationClickAction(String value) {
			notificationParams.put(NOTIFICATION_ACTION_CLICK, value);
			return this;
		}

		/**
		 * Sets the notification sound.
		 */
		public Builder notificationSound(String value) {
			notificationParams.put("sound", value);
			return this;
		}

		/**
		 * Sets the notification tag.
		 */
		public Builder notificationTag(String value) {
			notificationParams.put("tag", value);
			return this;
		}

		/**
		 * Sets the notification color.
		 */
		public Builder notificationColor(String value) {
			notificationParams.put("color", value);
			return this;
		}

		public Message build() {
			return new Message(this);
		}

	}

}