package com.dongdong.app.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * 数据库帮助类
 * 
 * @author leer
 * 
 */
public class DatabaseHelper extends SQLiteOpenHelper {

	public static final String LINKROOM_DATABASE_NAME = "linkroom_db";

	public static final String LINKROOM_TABLE_NAME = "linkroom_db_name";
	public static final String COMMNPHONE_TABLE_NAME = "Linkroom_db_commonphone_name";

	public static final String CREATE_TABLE = "create table "
			+ LINKROOM_TABLE_NAME
			+ " (_id integer primary key autoincrement, iid integer,"
			+ " time varchar(10), date varchar(10), content text, color integer)";

	public static final String NEWS_LIST = "something";

	public static final String CREATE_NEWS_LIST_TABLE = "create table "
			+ LINKROOM_TABLE_NAME + "("
			+ "_id integer primary key autoincrement, "
			+ "news_id interger, title varchar(10), " + ")";

	public static final String CREATE_COMMNPHONE_TABLE = "create table "
			+ COMMNPHONE_TABLE_NAME
			+ "(_id integer primary key autoincrement, "
			+ "contact_name varchar(50),phone_number varchar(50))";

	public DatabaseHelper(Context context) {
		super(context, LINKROOM_DATABASE_NAME, null, 1);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CREATE_TABLE);
		db.execSQL(CREATE_COMMNPHONE_TABLE);
		db.execSQL("insert into Linkroom_db_commonphone_name values(null,'中国移动','10086')");
		db.execSQL("insert into Linkroom_db_commonphone_name values(null,'中国联通','10010')");
		db.execSQL("insert into Linkroom_db_commonphone_name values(null,'中国电信','10000')");
		db.execSQL("insert into Linkroom_db_commonphone_name values(null,'天气预报','12121')");
		db.execSQL("insert into Linkroom_db_commonphone_name values(null,'中国银行','95566')");
		db.execSQL("insert into Linkroom_db_commonphone_name values(null,'建设银行','95533')");
		db.execSQL("insert into Linkroom_db_commonphone_name values(null,'邮政储蓄','95580')");
		db.execSQL("insert into Linkroom_db_commonphone_name values(null,'中国银联','95516')");
		// db.execSQL(CREATE_NEWS_LIST_TABLE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	}

}