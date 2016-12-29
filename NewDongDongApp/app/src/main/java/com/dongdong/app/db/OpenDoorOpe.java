package com.dongdong.app.db;

import android.content.Context;

import com.dongdong.app.bean.OpenDoorRecordBean;
import com.dongdong.app.bean.UserBean;
import com.dongdong.app.db.gen.OpenDoorRecordBeanDao;

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
     */
    public static void delete(Context context, List<Long> ids) {
        DBManager.getDaoSession(context).getOpenDoorRecordBeanDao().deleteByKeyInTx(ids);
    }

    /**
     * 查询所有数据
     *
     * @param context 上下文
     * @return 数据库所有数据
     */
    public static List<OpenDoorRecordBean> queryAll(Context context) {
        QueryBuilder<OpenDoorRecordBean> builder = DBManager.getDaoSession(context).
                getOpenDoorRecordBeanDao().queryBuilder().orderDesc(OpenDoorRecordBeanDao.
                Properties.Timestamp);
        return builder.build().list();
    }
}
