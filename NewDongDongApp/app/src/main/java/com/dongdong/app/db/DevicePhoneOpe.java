package com.dongdong.app.db;

import android.content.Context;

import com.dongdong.app.bean.DevicePhoneBean;
import com.dongdong.app.db.gen.DevicePhoneBeanDao.Properties;

import org.greenrobot.greendao.query.QueryBuilder;

import java.util.List;

public class DevicePhoneOpe {

    /**
     * 添加一条数据至数据库
     *
     * @param context            上下文
     * @param devicePhoneBean 数据
     */
    public static void insert(Context context, DevicePhoneBean devicePhoneBean) {
        DBManager.getDaoSession(context).getDevicePhoneBeanDao().insert(devicePhoneBean);
    }

    /**
     * 清空数据
     *
     * @param context 上下文
     * @param ids     删除数据的id(本地)
     */
    public static void delete(Context context, List<Long> ids) {
        DBManager.getDaoSession(context).getDevicePhoneBeanDao().deleteByKeyInTx(ids);
    }

    /**
     * 查询所有数据（deviceId）
     *
     * @param context  上下文
     * @param deviceId 设备Id
     * @return roomId对应的数据
     */
    public static List<DevicePhoneBean> queryAllByDeviceId(Context context, int deviceId) {
        QueryBuilder<DevicePhoneBean> builder = DBManager.getDaoSession(context).
                getDevicePhoneBeanDao().queryBuilder().where(Properties.DeviceId.eq(deviceId));
        return builder.build().list();
    }

    /**
     * 查询所有数据（phoneId）
     *
     * @param context  上下文
     * @param phoneId 常用电话Id
     * @return phoneId对应的数据
     */
    public static List<DevicePhoneBean> queryAllByPhoneId(Context context,String phoneId) {
        QueryBuilder<DevicePhoneBean> builder = DBManager.getDaoSession(context).
                getDevicePhoneBeanDao().queryBuilder().where(Properties.PhoneId.eq(phoneId));
        return builder.build().list();
    }
}
