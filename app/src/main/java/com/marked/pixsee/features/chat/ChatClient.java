package com.marked.pixsee.features.chat;

import android.os.Handler;
import android.os.Looper;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.marked.pixsee.RxBus;
import com.marked.pixsee.data.message.Message;
import com.marked.pixsee.data.user.User;
import com.marked.pixsee.features.chat.data.MessageConstants;
import com.marked.pixsee.networking.ServerConstants;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

/**
 * Created by Tudor on 13-Oct-16.
 */

class ChatClient implements ChattingInterface {
	static final String ON_NEW_MESSAGE = "onMessage";
	static final String ON_NEW_ROOM = "onRoom";
	static final String ON_TYPING = "onTyping";
	private final User mAppUser;
	private final User mThatUser;
	private Socket mSocket;
	private Emitter.Listener onMessage;
	private Emitter.Listener onTyping;

	ChatClient(User appUser, User thatUser) {
		mAppUser = appUser;
		mThatUser = thatUser;
		try {
			mSocket = IO.socket(ServerConstants.SERVER);
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		onMessage = onNewMessage();
		onTyping = onTyping();

	}

	@Override
	public void connect() {
		mSocket.connect();
		mSocket.on(ON_NEW_MESSAGE, onMessage);
		mSocket.on(ON_TYPING, onTyping);
	}

	@Override
	public void disconnect() {
		mSocket.off(ON_NEW_MESSAGE, onMessage);
		mSocket.off(ON_TYPING, onTyping);
		mSocket.disconnect();
	}

	@Override
	public void emit(String event, Object... objects) {
		mSocket.emit(event, objects);
	}

	private Emitter.Listener onNewMessage() {
		return new Emitter.Listener() {
			@Override
			public void call(final Object... args) {
				new Handler(Looper.getMainLooper()).post(new Runnable() {
					@Override
					public void run() {
						try {
							JSONObject json = new JSONObject(args[0].toString());
							Gson gson = new GsonBuilder().create();
							Message message = gson.fromJson(json.toString(), Message.class);
							Message.Builder builder = new Message.Builder()
									                          .date(message.getDate())
									                          .id(message.getId())
									                          .from(message.getFrom())
									                          // the database doesn't have from but in this case from & to are the same
									                          // from & to are switching here
									                          // I get message from you and now to send message back to you
									                          // to becomes from
									                          .to(message.getFrom())
									                          .messageType(message.getMessageType())
									                          .addData(message.getData());

							if (message.getMessageType() == MessageConstants.MessageType.YOU_IMAGE) {
								builder.addData(MessageConstants.DATA_BODY,
												ServerConstants.SERVER_USER_IMAGE +
														"/?img=" + message.getData().get(MessageConstants.DATA_BODY));
							}
							message = builder.build();

							if (message.getMessageType() == MessageConstants.MessageType.ME_MESSAGE)
								message.setMessageType(MessageConstants.MessageType.YOU_MESSAGE);
							RxBus.getInstance().post(message);
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
				});
			}
		};
	}

	/**
	 * keep track if the user is interacting with the app. If not, disconnect the socket
	 */

	public Emitter.Listener onTyping() { // // FIXME: 23-Jun-16 gets called twice when I click a friend
		return new Emitter.Listener() {
			@Override
			public void call(final Object... args) {
				new Handler(Looper.getMainLooper()).post(new Runnable() {
					@Override
					public void run() {
						boolean typing = (boolean) args[0];
						RxBus.getInstance().post(typing);
					}
				});

			}
		};
	}

}
