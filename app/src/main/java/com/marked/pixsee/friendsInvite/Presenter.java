package com.marked.pixsee.friendsInvite;

import com.marked.pixsee.commands.Command;

import java.lang.ref.WeakReference;

/**
 * Created by Tudor on 03-Jun-16.
 */
class Presenter implements FriendsInviteContract.Presenter {
	private WeakReference<FriendsInviteContract.View> mView;

	public Presenter(FriendsInviteContract.View mView) {
		this.mView = new WeakReference<FriendsInviteContract.View>(mView);
		mView.setPresenter(this);
	}

	@Override
	public void start() {

	}

	@Override
	public void execute(Command command) {
		command.execute();
	}
}