package com.dongdong.app.db;

import android.content.Context;

import com.dongdong.app.bean.CommonPhoneBean;
import com.dongdong.app.db.gen.CommonPhoneBeanDao;
import com.dongdong.app.db.gen.CommonPhoneBeanDao.Properties;

import org.greenrobot.greendao.query.QueryBuilder;

import java.util.List;

/**
 * 常用电话缓存的执行者
 */
public class CommonPhoneOpe {

    /**
     * 添加一条数据至数据库
     *
     * @param context         上下文
     * @param commonPhoneBean 数据
     */
    public static void insertDataByCommonPhoneBean(Context context, CommonPhoneBean commonPhoneBean) {
        DBManager.getDaoSession(context).getCommonPhoneBeanDao().insert(commonPhoneBean);
    }

    /**
     * 查询所有数据（roomId）
     *
     * @param context 上下文
     * @param phoneId 常用电话Id
     * @return roomId对应的数据
     */
    public static List<CommonPhoneBean> queryAllByPhoneId(Context context,String phoneId) {
        QueryBuilder<CommonPhoneBean> builder = DBManager.getDaoSession(context).
                getCommonPhoneBeanDao().queryBuilder().where(Properties.PhoneId.eq(phoneId));
        return builder.build().list();
    }

    /**
     * 查询所有数据
     *
     * @param context 上下文
     * @return 数据库所有数据
     */
    public static List<CommonPhoneBean> queryAll(Context context) {
        QueryBuilder<CommonPhoneBean> builder = DBManager.getDaoSession(context).getCommonPhoneBeanDao().
                queryBuilder();
        return builder.build().list();
    }
}
