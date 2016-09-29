package com.marked.pixsee.profile;

import com.marked.pixsee.model.user.UserDatasource;
import com.marked.pixsee.utility.BitmapUtils;

import java.io.File;

import dagger.Module;
import dagger.Provides;
import dependencyInjection.scopes.FragmentScope;
import dependencyInjection.scopes.Repository;

/**
 * Created by Tudor on 07-Jun-16.
 */
@Module
class ProfileModule {
	private ProfileContract.View mViewWeakReference;

	public ProfileModule(ProfileContract.View view) {
		mViewWeakReference = view;
	}
	@FragmentScope
	@Provides
	ProfileContract.Presenter providePresenter(@Repository UserDatasource userRepository){
		File publicPictureDirectory = BitmapUtils.getPublicPictureDirectory();
		if (publicPictureDirectory==null)
			publicPictureDirectory = new File(""); //// TODO: 16-Jul-16 is this correct ? or it will return the root of the file system ?
		return new Presenter(mViewWeakReference, userRepository,publicPictureDirectory);
	}
}
