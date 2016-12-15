package com.dongdong.app.db;

import android.content.Context;

import com.dongdong.app.bean.OpenDoorRecordBean;

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
     * 添加数据至数据库(最多保存15条记录)
     *
     * @param context            上下文
     * @param openDoorRecordBean 数据
     */
    public static void save(Context context, OpenDoorRecordBean openDoorRecordBean) {
        //清空数据
        DBManager.getDaoSession(context).getOpenDoorRecordBeanDao().deleteAll();
        //再插入新数据
        DBManager.getDaoSession(context).getOpenDoorRecordBeanDao().save(openDoorRecordBean);
    }

    /**
     * 查询所有数据
     *
     * @param context 上下文
     * @return 数据库所有数据
     */
    public static List<OpenDoorRecordBean> queryAll(Context context) {
        QueryBuilder<OpenDoorRecordBean> builder = DBManager.getDaoSession(context).
                getOpenDoorRecordBeanDao().queryBuilder();
        return builder.build().list();
    }
}
