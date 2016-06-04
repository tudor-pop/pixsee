package com.marked.pixsee.entry;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.marked.pixsee.chat.data.MessageConstants;
import com.marked.pixsee.friends.data.FriendConstants;
import com.marked.pixsee.main.MainActivity;
import com.marked.pixsee.utility.GCMConstants;

import bolts.AppLinks;


public class EntryActivity extends AppCompatActivity {
	private boolean mUserRegistered;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mUserRegistered = PreferenceManager.getDefaultSharedPreferences(this)
		                                   .getBoolean(GCMConstants.SENT_TOKEN_TO_SERVER, false);
		Uri targetUrl = AppLinks.getTargetUrlFromInboundIntent(this, getIntent());
		if (targetUrl != null) {
			Log.i("Activity", "App Link Target URL: " + targetUrl.toString());
		}
	}

	public void onResume() {
		super.onResume();
		Intent intent;
//		intent = new Intent(this, SelfieActivity.class);


		if (mUserRegistered) {
			intent = new Intent(this, MainActivity.class);
			intent.putExtra(MessageConstants.FROM, getIntent().getStringExtra(MessageConstants.FROM));
			intent.putExtra(FriendConstants.USERNAME, getIntent().getStringExtra(FriendConstants.USERNAME));
		} else {
			intent = new Intent(this, AuthActivity.class);
		}
		startActivity(intent);
		finish();
	}
}
