package com.marked.pixsee.data.mapper;

import android.content.ContentValues;

import com.marked.pixsee.friends.data.DatabaseFriendContract;
import com.marked.pixsee.friends.data.User;

/**
 * Created by Tudor Pop on 29-Mar-16.
 */
public class CvToUserMapper implements Mapper<ContentValues, User> {
	@Override
	public User map(ContentValues values) {
		String id = values.getAsString(DatabaseFriendContract.COLUMN_ID);
		String name = values.getAsString(DatabaseFriendContract.COLUMN_NAME);
		String email = values.getAsString(DatabaseFriendContract.COLUMN_EMAIL);
		String token = values.getAsString(DatabaseFriendContract.COLUMN_TOKEN) ;
		return new User(id, name, email, token);
	}
}
