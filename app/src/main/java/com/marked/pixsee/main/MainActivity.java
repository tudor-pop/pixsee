package com.marked.pixsee.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.firebase.messaging.RemoteMessage;
import com.marked.pixsee.R;
import com.marked.pixsee.chat.ChatActivity;
import com.marked.pixsee.chat.GCMListenerService;
import com.marked.pixsee.friends.FriendFragment;
import com.marked.pixsee.data.repository.user.User;
import com.marked.pixsee.friends.data.FriendConstants;
import com.marked.pixsee.main.commands.SelfieCommand;
import com.marked.pixsee.main.di.DaggerMainComponent;
import com.marked.pixsee.main.di.MainModule;

import javax.inject.Inject;

public class MainActivity extends AppCompatActivity implements MainContract.View,FriendFragment.FriendFragmentInteractionListener, GCMListenerService.Callback {
	@Inject
	MainContract.Presenter mPresenter;

	private FrameLayout mainContainer;
	private ImageButton mChatImageButton;
	private ImageButton mSelfieImageButton;
	private ImageButton mProfileImageButton;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		GCMListenerService.addCallback(this);

		DaggerMainComponent.builder().mainModule(new MainModule(this)).build().inject(this);
		mainContainer = (FrameLayout) findViewById(R.id.mainContainer);
		mChatImageButton = (ImageButton) findViewById(R.id.chatImageButton);
		mSelfieImageButton = (ImageButton) findViewById(R.id.selfieImageButton);
		mProfileImageButton = (ImageButton) findViewById(R.id.profileImageButton);


		mChatImageButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mPresenter.chatClicked();
			}
		});
		mSelfieImageButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mPresenter.execute(new SelfieCommand(MainActivity.this));
			}
		});
	}

	@Override
	protected void onStart() {
		super.onStart();
		mPresenter.start();
		Toast.makeText(MainActivity.this, getIntent().getStringExtra(FriendConstants.USERNAME), Toast.LENGTH_SHORT).show();
	}

	@Override
	public void setPresenter(MainContract.Presenter presenter) {
		mPresenter = presenter;
	}

	@Override
	public void displayChat(boolean show) {
		Fragment fragment = FriendFragment.newInstance();
		getSupportFragmentManager().beginTransaction().replace(R.id.mainContainer, fragment).commit();
	}

	@Override
	public void onFriendClick(User friend) {

		Intent intent = new Intent(this, ChatActivity.class);
		intent.putExtra(ChatActivity.EXTRA_CONTACT, friend);
		startActivity(intent);
//		CardFragment fragment = CardFragment.newInstance(friend);
//		CardLocalDatasource localDatasource = new CardLocalDatasource(PixyDatabase.getInstance(this));
//		CardRemoteDatasource remoteDatasource = new CardRemoteDatasource(PreferenceManager.getDefaultSharedPreferences(this));
//		fragment.setPresenter(new CardPresenter(fragment, new CardRepository(localDatasource,remoteDatasource)));
//		getSupportFragmentManager().beginTransaction().replace(R.id.cardFragmentContainer, fragment).commit();
	}

	@Override
	public void receiveRemoteMessage(RemoteMessage message) {
		Log.d("*** TAG ***", "receiveRemoteMessage: "+message.toString());
	}

	@Override
	protected void onDestroy() {
		GCMListenerService.clear();
		super.onDestroy();
	}
}
