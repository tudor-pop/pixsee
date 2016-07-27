package com.marked.pixsee.main;

import com.google.firebase.messaging.RemoteMessage;
import com.marked.pixsee.BuildConfig;
import com.marked.pixsee.data.user.User;
import com.marked.pixsee.friends.data.FriendConstants;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

/**
 * Created by Tudor on 21-Jul-16.
 */
@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class RemoteMessageToUserMapperTest {
	public static final String ID = "123";
	public static final String NAME = "name";
	public static final String EMAIL = "email";
	public static final String FROM = "token";
	public static final String ICON_URL = "icon";
	public static final String USERNAME = "username";
	public static final String COVER = "cover";

	RemoteMessage mRemoteMessage;


	@Before
	public void setUp() throws Exception {
		mRemoteMessage = new RemoteMessage.Builder(ID)
				.addData(FriendConstants.ID, ID)
				.addData(FriendConstants.NAME, NAME)
				.addData(FriendConstants.EMAIL, EMAIL)
				.addData(FriendConstants.TOKEN, FROM)
				.addData(FriendConstants.ICON_URL, ICON_URL)
				.addData(FriendConstants.USERNAME, USERNAME)
				.addData(FriendConstants.COVER_URL, COVER)
				.build();
	}

	@Test
	public void testMap() throws Exception {
		RemoteMessageToUserMapper remoteMessageToUserMapper = new RemoteMessageToUserMapper();
		User user = remoteMessageToUserMapper.map(mRemoteMessage);
		Assert.assertEquals(user.getUserID(), ID);
		Assert.assertEquals(user.getName(), NAME);
		Assert.assertEquals(user.getEmail(), EMAIL);
		Assert.assertEquals(user.getToken(), FROM);
		Assert.assertEquals(user.getIconUrl(), ICON_URL);
		Assert.assertEquals(user.getUsername(), USERNAME);
		Assert.assertEquals(user.getPassword(), null);
		Assert.assertEquals(user.getCoverUrl(), COVER);
	}
}