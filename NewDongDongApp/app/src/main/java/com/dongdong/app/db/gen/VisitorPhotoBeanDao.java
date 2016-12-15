package com.dongdong.app.db.gen;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.internal.DaoConfig;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;

import com.dongdong.app.bean.VisitorPhotoBean;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "VISITOR_PHOTO_BEAN".
*/
public class VisitorPhotoBeanDao extends AbstractDao<VisitorPhotoBean, Long> {

    public static final String TABLENAME = "VISITOR_PHOTO_BEAN";

    /**
     * Properties of entity VisitorPhotoBean.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property PhotoUrl = new Property(1, String.class, "photoUrl", false, "PHOTO_URL");
        public final static Property DeviceName = new Property(2, String.class, "deviceName", false, "DEVICE_NAME");
        public final static Property PhotoTimestamp = new Property(3, String.class, "photoTimestamp", false, "PHOTO_TIMESTAMP");
    }


    public VisitorPhotoBeanDao(DaoConfig config) {
        super(config);
    }
    
    public VisitorPhotoBeanDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"VISITOR_PHOTO_BEAN\" (" + //
                "\"_id\" INTEGER PRIMARY KEY AUTOINCREMENT ," + // 0: id
                "\"PHOTO_URL\" TEXT," + // 1: photoUrl
                "\"DEVICE_NAME\" TEXT," + // 2: deviceName
                "\"PHOTO_TIMESTAMP\" TEXT);"); // 3: photoTimestamp
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"VISITOR_PHOTO_BEAN\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, VisitorPhotoBean entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        String photoUrl = entity.getPhotoUrl();
        if (photoUrl != null) {
            stmt.bindString(2, photoUrl);
        }
 
        String deviceName = entity.getDeviceName();
        if (deviceName != null) {
            stmt.bindString(3, deviceName);
        }
 
        String photoTimestamp = entity.getPhotoTimestamp();
        if (photoTimestamp != null) {
            stmt.bindString(4, photoTimestamp);
        }
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, VisitorPhotoBean entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        String photoUrl = entity.getPhotoUrl();
        if (photoUrl != null) {
            stmt.bindString(2, photoUrl);
        }
 
        String deviceName = entity.getDeviceName();
        if (deviceName != null) {
            stmt.bindString(3, deviceName);
        }
 
        String photoTimestamp = entity.getPhotoTimestamp();
        if (photoTimestamp != null) {
            stmt.bindString(4, photoTimestamp);
        }
    }

    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    @Override
    public VisitorPhotoBean readEntity(Cursor cursor, int offset) {
        VisitorPhotoBean entity = new VisitorPhotoBean();
        readEntity(cursor, entity, offset);
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, VisitorPhotoBean entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setPhotoUrl(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setDeviceName(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setPhotoTimestamp(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
     }
    
    @Override
    protected final Long updateKeyAfterInsert(VisitorPhotoBean entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    @Override
    public Long getKey(VisitorPhotoBean entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    @Override
    public boolean hasKey(VisitorPhotoBean entity) {
        return entity.getId() != null;
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
}
