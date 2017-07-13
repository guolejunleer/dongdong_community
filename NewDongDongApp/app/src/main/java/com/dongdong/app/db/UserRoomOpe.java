package com.dongdong.app.db;

import android.content.Context;

import com.dongdong.app.bean.UserRoomBean;
import com.dongdong.app.db.gen.UserRoomBeanDao;

import org.greenrobot.greendao.query.QueryBuilder;

import java.util.List;

/**
 * 用户-房间关系表
 * Created by 13750831423 on 2017/4/26.
 */

public class UserRoomOpe {


    /**
     * 添加一条数据至数据库
     *
     * @param context      上下文
     * @param userRoomBean 数据
     */
    public static void insertDataByUserRoomBean(Context context, UserRoomBean userRoomBean) {
        DBManager.getDaoSession(context).getUserRoomBeanDao().insert(userRoomBean);
    }

    /**
     * 通过userId查询对应房号
     *
     * @param context 上下文
     * @param userId  用户ID
     */
    public static List<UserRoomBean> queryDataByUserId(Context context, int userId) {
        UserRoomBeanDao dao = DBManager.getDaoSession(context).getUserRoomBeanDao();
        return dao.queryBuilder().where(UserRoomBeanDao.Properties.UserId.eq(userId)).build().list();
    }

    /**
     * 通过userId查询对应房号
     *
     * @param context 上下文
     * @param userId  用户ID
     */
    public static List<UserRoomBean> queryDataByUserIdAndDevId(Context context,
                                                               int userId, int deviceId) {
        UserRoomBeanDao dao = DBManager.getDaoSession(context).getUserRoomBeanDao();
        QueryBuilder<UserRoomBean> qb = dao.queryBuilder();
        qb.where(UserRoomBeanDao.Properties.UserId.eq(userId));
        qb.where(UserRoomBeanDao.Properties.DeviceId.eq(deviceId));

        return qb.build().list();
    }

    /**
     * 通过roomId查询对应房号
     *
     * @param context 上下文
     * @param roomId  房号ID
     */
    public static List<UserRoomBean> queryDataByRoomId(Context context, int roomId) {
        UserRoomBeanDao dao = DBManager.getDaoSession(context).getUserRoomBeanDao();
        return dao.queryBuilder().where(UserRoomBeanDao.Properties.RoomId.eq(roomId)).build().list();
    }
}
