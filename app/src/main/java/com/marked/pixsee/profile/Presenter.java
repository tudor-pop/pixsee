package com.marked.pixsee.profile;

import com.marked.pixsee.data.repository.user.User;
import com.marked.pixsee.data.repository.user.UserDatasource;
import com.marked.pixsee.data.repository.user.UserRepository;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tudor on 07-Jun-16.
 */
public class Presenter implements ProfileContract.Presenter {
	private WeakReference<ProfileContract.View> mViewWeakReference;
	private UserDatasource mUserDatasource;

	public Presenter(ProfileContract.View viewWeakReference, UserRepository repository) {
		mViewWeakReference = new WeakReference<>(viewWeakReference);
		mUserDatasource = repository;
		mViewWeakReference.get().setPresenter(this);
	}

	@Override
	public void saveAppUser(User user) {
		mUserDatasource.saveAppUser(user);
	}

	@Override
	public void attach() {

	}

	@Override
	public void logOut() {
		mUserDatasource.deleteAllUsers();
	}

	@Override
	public void picturesData(File[] list) {
		List<String> strings = new ArrayList<>(list.length);
		for(File it : list)
			strings.add(it.getAbsolutePath());
		mViewWeakReference.get().setData(strings);
	}
}
