package com.marked.pixsee.chat;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;

import com.marked.pixsee.BuildConfig;
import com.marked.pixsee.PixseeTest;
import com.marked.pixsee.UserUtilTest;
import com.marked.pixsee.di.components.DaggerFakeActivityComponent;
import com.marked.pixsee.di.components.FakeActivityComponent;
import com.marked.pixsee.di.components.FakeAppComponent;
import com.marked.pixsee.di.modules.FakeActivityModule;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.support.v4.SupportFragmentController;

/**
 * Created by Tudor on 13-Oct-16.
 */
@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 23, application = PixseeTest.class)
public class ChatFragmentTest extends ChatFragment {
	// Robolectric Controllers
	private SupportFragmentController<ChatFragmentTest> fragmentController;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		// Robolectric Controllers
		fragmentController = SupportFragmentController.of(this, ChatActivity.class);
		Intent intent = new Intent();
		intent.putExtra(ChatActivity.EXTRA_CONTACT, UserUtilTest.getUserTest());
		fragmentController.withIntent(intent);
	}

	@Test
	public void testHasIntent() throws Exception {


	}

	@Override
	public void injectComponent() {
		DaggerChatComponent.builder().activityComponent(((ChatActivity) getActivity()).getActivityComponent())
				.chatModule(new ChatModule(this))
				.build()
				.inject(this);
		FakeAppComponent fakeAppComponent = (FakeAppComponent) ((PixseeTest) getActivity().getApplication()).getAppComponent();
		FakeActivityComponent activityComponent = DaggerFakeActivityComponent.builder()
				                                          .fakeAppComponent(fakeAppComponent)
				                                          .fakeActivityModule(new FakeActivityModule((AppCompatActivity) getActivity()))
				                                          .build();
		DaggerFakeChatComponent.builder()
				.fakeActivityComponent(activityComponent)
				.fakeChatModule(new FakeChatModule(this))
				.build();
	}
}
