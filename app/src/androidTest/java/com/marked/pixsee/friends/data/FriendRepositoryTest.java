package com.marked.pixsee.friends.data;


import android.content.Context;
import android.preference.PreferenceManager;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.SmallTest;

import com.marked.pixsee.data.database.PixyDatabase;
import com.marked.pixsee.friends.data.datasource.FriendsDiskDatasource;
import com.marked.pixsee.friends.data.datasource.FriendsNetworkDatasource;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * Created by Tudor Pop on 29-Mar-16.
 */
@RunWith(AndroidJUnit4.class)
@SmallTest
public class FriendRepositoryTest {
	FriendRepository friendRepository;
	Context context;
	PixyDatabase pixydb;

	@Before
	void setupViewModel() {
		context = InstrumentationRegistry.getTargetContext();
		pixydb = PixyDatabase.getInstance(context);
		FriendsDiskDatasource friendsDiskDatasource = new FriendsDiskDatasource(pixydb);
		FriendsNetworkDatasource friendsNetworkDatasource = new FriendsNetworkDatasource(PreferenceManager.getDefaultSharedPreferences(context));
		friendRepository = new FriendRepository(friendsDiskDatasource, friendsNetworkDatasource);
	}

	@Test
	void testLength() {
//        Mockito.`when`(pixydb.readableDatabase.select(DatabaseContract.Friend.TABLE_NAME)).thenReturn(selectQueryBuilder)
//        Mockito.`when`(pixydb.readableDatabase.select(DatabaseContract.Friend.TABLE_NAME).exec { count }).thenReturn(0)
		Assert.assertEquals(0, friendRepository.length());
	}
}