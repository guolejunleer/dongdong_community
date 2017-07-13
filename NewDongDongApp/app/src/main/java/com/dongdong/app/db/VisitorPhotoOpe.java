package com.dongdong.app.db;


import android.content.Context;

import com.dongdong.app.bean.VisitorPhotoBean;
import com.dongdong.app.db.gen.VisitorPhotoBeanDao;

import org.greenrobot.greendao.query.QueryBuilder;

import java.util.List;

public class VisitorPhotoOpe {
    /**
     * 添加一条数据至数据库
     *
     * @param context            上下文
     * @param visitorPhotoBean 数据
     */
    public static void insert(Context context, VisitorPhotoBean visitorPhotoBean) {
        DBManager.getDaoSession(context).getVisitorPhotoBeanDao().insert(visitorPhotoBean);
    }

    /**
     * 清空数据
     *
     * @param context 上下文
     */
    public static void delete(Context context, List<Long> ids) {
        DBManager.getDaoSession(context).getVisitorPhotoBeanDao().deleteByKeyInTx(ids);
    }

    /**
     * 通过userId和deviceId
     *
     * @param context 上下文
     * @param userId  用户ID
     */
    public static List<VisitorPhotoBean> queryDataByUserIdAndDevId(
            Context context, int userId, int deviceId) {
        VisitorPhotoBeanDao dao = DBManager.getDaoSession(context).getVisitorPhotoBeanDao();
        QueryBuilder<VisitorPhotoBean> qb = dao.queryBuilder();
        qb.where(VisitorPhotoBeanDao.Properties.UserId.eq(userId));
        qb.where(VisitorPhotoBeanDao.Properties.DeviceId.eq(deviceId));
        qb.orderAsc(VisitorPhotoBeanDao.Properties.PhotoTimestamp);
        return qb.build().list();
    }

    /**
     * 查询所有数据（按时间倒序）
     *
     * @param context 上下文
     * @return 数据库所有数据
     */
    public static List<VisitorPhotoBean> queryAllDesc(Context context) {
        QueryBuilder<VisitorPhotoBean> builder = DBManager.getDaoSession(context).
                getVisitorPhotoBeanDao().queryBuilder().orderDesc(VisitorPhotoBeanDao.
                Properties.PhotoTimestamp);
        return builder.build().list();
    }

    /**
     * 查询所有数据（按时间顺序）
     *
     * @param context 上下文
     * @return 数据库所有数据
     */
    public static List<VisitorPhotoBean> queryAllAsc(Context context) {
        QueryBuilder<VisitorPhotoBean> builder = DBManager.getDaoSession(context).
                getVisitorPhotoBeanDao().queryBuilder().orderAsc(VisitorPhotoBeanDao.
                Properties.PhotoTimestamp);
        return builder.build().list();
    }
}
