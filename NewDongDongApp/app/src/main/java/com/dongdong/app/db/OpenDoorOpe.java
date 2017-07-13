package com.dongdong.app.db;

import android.content.Context;

import com.dongdong.app.bean.OpenDoorRecordBean;
import com.dongdong.app.db.gen.OpenDoorRecordBeanDao;
import com.dongdong.app.db.gen.OpenDoorRecordBeanDao.Properties;

import org.greenrobot.greendao.query.QueryBuilder;

import java.util.List;

public class OpenDoorOpe {

    /**
     * 添加一条数据至数据库
     *
     * @param context            上下文
     * @param openDoorRecordBean 数据
     */
    public static void insert(Context context, OpenDoorRecordBean openDoorRecordBean) {
        DBManager.getDaoSession(context).getOpenDoorRecordBeanDao().insert(openDoorRecordBean);
    }

    /**
     * 清空数据
     *
     * @param context 上下文
     * @param ids     删除数据的id(本地)
     */
    public static void delete(Context context, List<Long> ids) {
        DBManager.getDaoSession(context).getOpenDoorRecordBeanDao().deleteByKeyInTx(ids);
    }

    /**
     * 查询所有数据（roomId）
     *
     * @param context 上下文
     * @param roomId  设备Id
     * @return roomId对应的数据
     */
    public static List<OpenDoorRecordBean> queryAllByRoomId(Context context,int roomId) {
        QueryBuilder<OpenDoorRecordBean> builder = DBManager.getDaoSession(context).
                getOpenDoorRecordBeanDao().queryBuilder().where(Properties.RoomId.eq(roomId)).
                orderDesc(OpenDoorRecordBeanDao.Properties.Timestamp);
        return builder.build().list();
    }

    /**
     * 查询所有数据
     *
     * @param context 上下文
     * @return 数据库所有数据
     */
    public static List<OpenDoorRecordBean> queryAllDesc(Context context) {
        QueryBuilder<OpenDoorRecordBean> builder = DBManager.getDaoSession(context).
                getOpenDoorRecordBeanDao().queryBuilder().orderDesc(OpenDoorRecordBeanDao.
                Properties.Timestamp);
        return builder.build().list();
    }

    /**
     * 查询所有数据(按时间顺序)
     *
     * @param context 上下文
     * @return 数据库所有数据
     */
    public static List<OpenDoorRecordBean> queryAllAsc(Context context) {
        QueryBuilder<OpenDoorRecordBean> builder = DBManager.getDaoSession(context).
                getOpenDoorRecordBeanDao().queryBuilder().orderAsc(OpenDoorRecordBeanDao.
                Properties.Timestamp);
        return builder.build().list();
    }
}
