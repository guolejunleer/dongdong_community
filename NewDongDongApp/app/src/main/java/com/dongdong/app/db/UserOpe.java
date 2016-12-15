package com.dongdong.app.db;

import android.content.Context;

import com.dongdong.app.bean.UserBean;
import com.dongdong.app.db.gen.UserBeanDao;

import org.greenrobot.greendao.query.QueryBuilder;

import java.util.List;

/**
 * 用户登录缓存的执行者
 * author leer （http://www.dd121.com）
 * created at 2016/8/24 16:24
 */
public class UserOpe {

    public static final int FIRST_INDEX = 0;

    /**
     * 添加一条数据至数据库
     *
     * @param context  上下文
     * @param userBean 数据
     */
    public static void insertDataByUserBean(Context context, UserBean userBean) {
        DBManager.getDaoSession(context).getUserBeanDao().insert(userBean);
    }

    /**
     * 添加数据至数据库，如果存在，将原来的数据覆盖
     *
     * @param context  上下文
     * @param userBean 数据
     */
    public static void saveData(Context context, UserBean userBean) {
        DBManager.getDaoSession(context).getUserBeanDao().save(userBean);
    }

    /**
     * 通过Id删除用户
     *
     * @param context    上下文
     * @param userBeanId 数据ID
     */
    public static void deleteDataById(Context context, long userBeanId) {
        DBManager.getDaoSession(context).getUserBeanDao().deleteByKey(userBeanId);
    }

    /**
     * 通过用户名查询用户
     *
     * @param context  上下文
     * @param userName 卡号
     * @return 数据
     */
    public static UserBean queryDataByUserName(Context context, String userName) {
        UserBeanDao dao = DBManager.getDaoSession(context).getUserBeanDao();
        UserBean userBean = dao.queryBuilder().where(UserBeanDao.Properties.UserName.eq(userName)).build().unique();
        if (userBean != null) {
            return userBean;
        }
        return null;
    }

    /**
     * 通过index查询用户
     *
     * @param context  上下文
     * @param index  排序index
     * @return 数据
     */
    public static UserBean queryDataByUserIndex(Context context, int index) {
        UserBeanDao dao = DBManager.getDaoSession(context).getUserBeanDao();
        UserBean userBean = dao.queryBuilder().where(
                UserBeanDao.Properties.Index.eq(index)).build().unique();
        if (userBean != null) {
            return userBean;
        }
        return null;
    }


    /**
     * 通过用户名更新用户
     *
     * @param context 上下文
     * @param userBean 数据
     */
    public static void updateDataByUserBean(Context context, UserBean userBean) {
        DBManager.getDaoSession(context).getUserBeanDao().update(userBean);
    }

    /**
     * 查询所有数据
     *
     * @param context 上下文
     * @return 数据库所有数据
     */
    public static List<UserBean> queryAll(Context context) {
        QueryBuilder<UserBean> builder = DBManager.getDaoSession(context).getUserBeanDao().
                queryBuilder();
        return builder.build().list();
    }
}
