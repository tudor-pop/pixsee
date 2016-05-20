package com.marked.pixsee.friends.friends.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;

import com.marked.pixsee.data.database.DatabaseContract;
import com.marked.pixsee.data.database.PixyDatabase;
import com.marked.pixsee.data.mapper.CursorToUserMapper;
import com.marked.pixsee.data.mapper.Mapper;
import com.marked.pixsee.data.mapper.UserToCvMapper;
import com.marked.pixsee.friends.specifications.GetFriendsSpecification;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;

import static com.marked.pixsee.data.database.DatabaseContract.Friend.TABLE_NAME;

/**
 * Created by Tudor on 2016-05-20.
 */
public class FriendsLocalDatasource implements FriendsDatasource {
	private PixyDatabase db;
	private Mapper<Cursor, User> cursorToUserMapper = new CursorToUserMapper();
	private Mapper<User, ContentValues> userToCvMapper = new UserToCvMapper();

	public FriendsLocalDatasource(PixyDatabase db) {
		this.db = db;
	}

	@Override
	public Observable<List<User>> getUsers() {
		List<User> users = new ArrayList<>();
		db.getReadableDatabase().beginTransaction();
		Cursor cursor = db.getReadableDatabase().rawQuery(new GetFriendsSpecification(0, -1).createQuery(), null);
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			User friend = cursorToUserMapper.map(cursor);
			users.add(friend);
			cursor.moveToNext();
		}
		db.getReadableDatabase().setTransactionSuccessful();
		db.getReadableDatabase().endTransaction();
		cursor.close();
		return Observable.just(users);
	}

	@Override
	public Observable<User> getUser(@NonNull User UserId) {
		Cursor cursor = db.getReadableDatabase().query(TABLE_NAME, DatabaseContract.Friend.ALL_TABLES, "WHERE "+UserId.getUserID()+"=?",
				new String[]{UserId.getUserID()}, null, null, null);
		cursor.moveToFirst();
		return Observable.just(cursorToUserMapper.map(cursor));
	}

	@Override
	public void saveUser(@NonNull User user) {
		db.getWritableDatabase().insertWithOnConflict(TABLE_NAME, null,userToCvMapper.map(user),SQLiteDatabase.CONFLICT_REPLACE);
	}

	@Override
	public void refreshUsers() {

	}

	@Override
	public void deleteAllUsers() {
		db.getWritableDatabase().delete(TABLE_NAME, null, null);
	}

	@Override
	public void deleteUsers(@NonNull User userId) {
		db.getWritableDatabase().delete(TABLE_NAME, DatabaseContract.Friend._ID + " = ?", new String[]{userId.getUserID()});
	}

	@Override
	public void saveUser(@NonNull List<User> users) {
		db.getWritableDatabase().beginTransaction();
		{
			for (User element : users) {
				db.getWritableDatabase().insertWithOnConflict(TABLE_NAME, null, userToCvMapper.map(element), SQLiteDatabase.CONFLICT_IGNORE);
			}
		}
		db.getWritableDatabase().setTransactionSuccessful();
	}
}
