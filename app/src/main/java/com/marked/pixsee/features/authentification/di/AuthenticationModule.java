package com.marked.pixsee.features.authentification.di;

import android.content.SharedPreferences;

import com.marked.pixsee.data.user.UserDatasource;
import com.marked.pixsee.di.scopes.ActivityScope;
import com.marked.pixsee.di.scopes.Repository;
import com.marked.pixsee.features.authentification.AuthenticationContract;
import com.marked.pixsee.features.authentification.Presenter;
import com.marked.pixsee.features.authentification.login.LoginAPI;
import com.marked.pixsee.networking.ServerConstants;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;
import retrofit2.Retrofit;

/**
 * Created by Tudor on 13-Jun-16.
 */
@Module
@ActivityScope
public class AuthenticationModule {
	private AuthenticationContract.View mView;

	public AuthenticationModule(AuthenticationContract.View view) {
		mView = view;
	}

	@ActivityScope
	@Provides
	AuthenticationContract.Presenter providePresenter(@Named(ServerConstants.SERVER) Retrofit retrofit,
	                                                  @Repository UserDatasource datasource,
	                                                  SharedPreferences preferences) {
		return new Presenter(mView, retrofit.create(LoginAPI.class),datasource,preferences);
	}
}