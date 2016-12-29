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
     * 查询所有数据
     *
     * @param context 上下文
     * @return 数据库所有数据
     */
    public static List<VisitorPhotoBean> queryAll(Context context) {
        QueryBuilder<VisitorPhotoBean> builder = DBManager.getDaoSession(context).
                getVisitorPhotoBeanDao().queryBuilder().orderDesc(VisitorPhotoBeanDao.
                Properties.PhotoTimestamp);
        return builder.build().list();
    }
}
