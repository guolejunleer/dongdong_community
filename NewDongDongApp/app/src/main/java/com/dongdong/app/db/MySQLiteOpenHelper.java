package com.dongdong.app.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.dongdong.app.db.gen.BulletinBeanDao;
import com.dongdong.app.db.gen.DaoMaster;
import com.dongdong.app.db.gen.OpenDoorRecordBeanDao;
import com.dongdong.app.db.gen.UserBeanDao;
import com.dongdong.app.db.gen.VisitorPhotoBeanDao;

import github.yuweiguocn.library.greendao.MigrationHelper;

public class MySQLiteOpenHelper extends DaoMaster.OpenHelper {
    public MySQLiteOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory) {
        super(context, name, factory);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        MigrationHelper.migrate(db, BulletinBeanDao.class, OpenDoorRecordBeanDao.class,
                UserBeanDao.class, VisitorPhotoBeanDao.class);
    }
}
