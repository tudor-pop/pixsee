package com.marked.pixsee.injection.modules;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteOpenHelper;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;

import com.marked.pixsee.UserUtilTest;
import com.marked.pixsee.data.database.DatabaseContract;
import com.marked.pixsee.data.database.PixyDatabase;
import com.marked.pixsee.data.user.User;
import com.marked.pixsee.data.user.UserDatasource;
import com.marked.pixsee.data.user.UserDiskDatasource;
import com.marked.pixsee.data.user.UserNetworkDatasource;
import com.marked.pixsee.data.user.UserRepository;
import com.marked.pixsee.injection.scopes.ActivityScope;
import com.marked.pixsee.injection.scopes.Local;
import com.marked.pixsee.injection.scopes.Remote;
import com.marked.pixsee.injection.scopes.Repository;

import org.mockito.Mockito;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Tudor on 24-Jul-16.
 */
@Module
@ActivityScope
public class FakeActivityModule {
	private AppCompatActivity activity;

	public FakeActivityModule(AppCompatActivity activity) {
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
	SQLiteOpenHelper provideDatabase() {
		return Mockito.mock(PixyDatabase.class);
	}

	@Provides
	@ActivityScope
	@Local
	UserDatasource provideUserRepositoryLocal(SQLiteOpenHelper database) {
		return Mockito.mock(UserDiskDatasource.class);
	}
	@Provides
	@ActivityScope
	@Remote
	UserDatasource provideUserRepositoryRemote(SharedPreferences preferences) {
		return Mockito.mock(UserNetworkDatasource.class);
	}

	@Provides
	@ActivityScope
	@Repository
	UserDatasource provideUserRepository(@Local UserDatasource local, @Remote UserDatasource remote) {
		return Mockito.mock(UserRepository.class);
	}

	@Provides
	@ActivityScope
	@Named(DatabaseContract.AppsUser.TABLE_NAME)
	User provideAppsUser(@Repository UserDatasource repository){
		return UserUtilTest.getUserTest();
	}
}
