package com.marked.pixsee.injection.modules;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;

import com.marked.pixsee.data.database.DatabaseContract;
import com.marked.pixsee.data.database.PixyDatabase;
import com.marked.pixsee.data.repository.user.User;
import com.marked.pixsee.data.repository.user.UserDiskDatasource;
import com.marked.pixsee.data.repository.user.UserNetworkDatasource;
import com.marked.pixsee.data.repository.user.UserRepository;
import com.marked.pixsee.injection.scopes.ActivityScope;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Tudor Pop on 19-Mar-16.
 */
@Module
@ActivityScope
public class ActivityModule {
	private AppCompatActivity activity;

	public ActivityModule(AppCompatActivity activity) {
		this.activity = activity;
	}

	@Provides
	@ActivityScope
	ProgressDialog provideProgressDialog() {
		ProgressDialog dialog = new ProgressDialog(activity);
		dialog.setTitle("Login");
		dialog.setMessage("Please wait...");
		dialog.setIndeterminate(true);
		return dialog;
	}

	@Provides
	@ActivityScope
	LocalBroadcastManager provideLocalBroadcastManager() {
		return LocalBroadcastManager.getInstance(activity);
	}

	@Provides
	@ActivityScope
	SharedPreferences provideSharedPreferences() {
		return PreferenceManager.getDefaultSharedPreferences(activity);
	}

	@Provides
	@ActivityScope
	AppCompatActivity provideActivity() {
		return activity;
	}

	@Provides
	@ActivityScope
	Context provideContext() {
		return activity;
	}

	@Provides
	@ActivityScope
	PixyDatabase provideDatabase() {
		return PixyDatabase.getInstance(activity);
	}

	@Provides
	@ActivityScope
	UserRepository provideUserRepository(PixyDatabase database) {
		return new UserRepository(
				new UserDiskDatasource(database),
				new UserNetworkDatasource(PreferenceManager.getDefaultSharedPreferences(activity)));
	}
	@Provides
	@ActivityScope
	@Named(DatabaseContract.AppsUser.TABLE_NAME)
	User provideAppsUser(UserRepository repository){
		return repository.getUser(DatabaseContract.AppsUser.TABLE_NAME);
	}
}
