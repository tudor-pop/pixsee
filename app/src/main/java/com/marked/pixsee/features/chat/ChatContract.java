package com.marked.pixsee.features.chat;

import android.net.Uri;
import android.support.annotation.NonNull;

import com.marked.pixsee.BasePresenter;
import com.marked.pixsee.BaseView;
import com.marked.pixsee.commands.Command;
import com.marked.pixsee.data.message.Message;
import com.marked.pixsee.data.user.User;

import java.io.File;
import java.util.List;

/**
 * Created by Tudor on 2016-05-19.
 */
interface ChatContract {

	interface Presenter extends BasePresenter {
		void loadMore(int limit, boolean forceUpdate);

		void onTyping(boolean typing);

		void execute(Command command);

		void isTyping(boolean typing);

		void chatClicked(Message message, int position);

		void sendMessage(@NonNull String message);

		void sendImage();

		void receiveMessage(@NonNull Message message);


		void setChatInteraction(ChatFragment.ChatFragmentInteraction listener);

		void pictureTaken(File file);

		void onCameraClick();

		void clearImageButton();

		File getPictureFile();

		void chatImageClicked(Uri uri);

		void setThatUser(User thatUser);

	}

	interface View extends BaseView<Presenter> {
		void showMessages(List<Message> cards);

		void showEmptyMessages();

		void addMessage(Message message);

		void pop();

		void remove(Message message, int position);

		void showImage(File path);

		void pictureTaken(File file);

		void showPictureContainer(boolean show);

		void showImageFullscreen(Uri uri);

		void imageSent(Message message);
	}
}