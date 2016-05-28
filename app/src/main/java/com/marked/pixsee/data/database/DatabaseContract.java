package com.marked.pixsee.data.database;

import android.provider.BaseColumns;

import com.marked.pixsee.chat.data.MessageConstants;
import com.marked.pixsee.friends.data.DatabaseFriendContract;
import com.marked.pixsee.friends.data.FriendConstants;

/**
 * Created by Tudor Pop on 26-Jan-16.
 */
public class DatabaseContract {

	private DatabaseContract() {
	}

	public static final int DATABASE_VERSION = 1;
	public static final String DATABASE_NAME = "pixy.db";

	/* Add to CREATE_TABLE_ARRAY all the other tables that get created and to DELETE_TABLE_ARRAY all tables that get deleted*/
	public static final String[] CREATE_TABLE_ARRAY = {DatabaseFriendContract.CREATE_TABLE.trim(), User.CREATE_TABLE.trim(), Message.CREATE_TABLE.trim()};
	public static final String[] DELETE_TABLE_ARRAY = {DatabaseFriendContract.DELETE_TABLE, User.DELETE_TABLE, Message.DELETE_TABLE};

	/* App's user*/
	protected static class User implements BaseColumns {
		public static final String TABLE_NAME = "user";
		public static final String COLUMN_ID = FriendConstants.ID;
		public static final String COLUMN_NAME = FriendConstants.NAME;
		public static final String COLUMN_EMAIL = FriendConstants.EMAIL;
		public static final String COLUMN_TOKEN = FriendConstants.TOKEN;

		public static final String CREATE_TABLE =
				" CREATE TABLE " + TABLE_NAME
						+ "(" +
						BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
						+ COLUMN_ID + " TEXT,"
						+ COLUMN_NAME + " TEXT,"
						+ COLUMN_EMAIL + " TEXT,"
						+ COLUMN_TOKEN + " TEXT"
						+ ");";
		public static final String DELETE_TABLE = "DROP TABLE IF EXISTS $TABLE_NAME";
	}

	/* Message */
	public static class Message implements BaseColumns {
		public static final String TABLE_NAME = "messages";
		public static final String COLUMN_ID = MessageConstants.ID;
		public static final String COLUMN_DATA_BODY = MessageConstants.DATA_BODY;
		public static final String COLUMN_TYPE = MessageConstants.MESSAGE_TYPE;
		public static final String COLUMN_DATE = MessageConstants.CREATION_DATE;
		public static final String COLUMN_TO = "_" + MessageConstants.TO;

		public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME
				                                          + "("
				                                          + BaseColumns._ID +
				                                          " INTEGER PRIMARY KEY AUTOINCREMENT," +
				                                          COLUMN_ID + " TEXT,"
				                                          + COLUMN_DATA_BODY + " TEXT,"
				                                          + COLUMN_TYPE + " INTEGER,"
				                                          + COLUMN_DATE + " TEXT,"
				                                          + COLUMN_TO + " TEXT, FOREIGN KEY( "
				                                          + COLUMN_TO + ")" + " REFERENCES "
				                                          + DatabaseFriendContract.TABLE_NAME + "("
				                                          + DatabaseFriendContract.COLUMN_ID + ")"
				                                          + ")";


		public static final String DELETE_TABLE = "DROP TABLE IF EXISTS $TABLE_NAME";
	}
}