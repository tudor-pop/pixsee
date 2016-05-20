package com.marked.pixsee.data.mapper;

import android.content.ContentValues;

import com.marked.pixsee.friends.friends.data.User;
import com.marked.pixsee.friends.friends.data.FriendConstants;

/**
 * Created by Tudor Pop on 29-Mar-16.
 */
public class UserToCvMapper implements Mapper<User,ContentValues> {
	@Override
	public ContentValues map(User user) {
		ContentValues values = new ContentValues();
		values.put(FriendConstants.ID, user.getUserID());
		values.put(FriendConstants.NAME, user.getName());
		values.put(FriendConstants.EMAIL, user.getEmail());
		values.put(FriendConstants.TOKEN, user.getToken());
		return values;
	}
}
