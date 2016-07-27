package com.marked.pixsee;

import com.marked.pixsee.injection.components.AppComponent;
import com.marked.pixsee.injection.components.DaggerAppComponentFake;
import com.marked.pixsee.injection.modules.AppModuleFake;

/**
 * Created by Tudor on 22-Jul-16.
 */
public class PixseeTest extends Pixsee{
	AppComponent mAppComponentFake;

	@Override
	public void onCreate() {
		super.onCreate();
		mAppComponentFake = DaggerAppComponentFake.builder().appModuleFake(new AppModuleFake(this)).build();
	}

	@Override
	public AppComponent getAppComponent() {
		return mAppComponentFake;
	}
}