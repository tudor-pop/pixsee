package com.marked.pixsee.injection.modules;

import android.app.Application;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteOpenHelper;
import android.preference.PreferenceManager;

import com.facebook.appevents.AppEventsLogger;
import com.google.gson.Gson;
import com.marked.pixsee.data.database.PixyDatabase;
import com.marked.pixsee.networking.ServerConstants;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Tudor Pop on 16-Mar-16.
 */
@Module
public class AppModule {
	private Application application;

	public AppModule(Application application) {
		this.application = application;
	}

	@Provides
	@Singleton
	Application providesApplication() {
		AppEventsLogger.activateApp(application);
		return application;
	}

	@Provides
	@Singleton
	OkHttpClient providesHTTPClient() {
		HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
		loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
		OkHttpClient httpClient = new OkHttpClient.Builder()
				.addInterceptor(loggingInterceptor)
				.build();
		return httpClient;
	}

	@Provides
	@Named(ServerConstants.SERVER)
	Retrofit providesRetrofit(OkHttpClient client) {
		return new Retrofit.Builder()
				.addConverterFactory(GsonConverterFactory.create())
				.addCallAdapterFactory(RxJavaCallAdapterFactory.create())
				.baseUrl(ServerConstants.SERVER)
				.client(client)
				.build();
	}

	@Provides
	@Singleton
	SharedPreferences provideSharedPreferences() {
		return PreferenceManager.getDefaultSharedPreferences(application);
	}

	@Provides
	@Singleton
	SQLiteOpenHelper provideDatabase() {
		return PixyDatabase.getInstance(application);
	}

	@Provides
	@Singleton
	Gson provideGson() {
		return new Gson();
	}
}