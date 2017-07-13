package com.dongdong.app.db;

import android.content.Context;

import com.dongdong.app.bean.DeviceVillageBean;
import com.dongdong.app.db.gen.DeviceVillageBeanDao;

public class DeviceVillageOpe {
    /**
     * 添加一条数据至数据库
     *
     * @param context     上下文
     * @param villageBean 数据
     */
    public static void insertDataByVillageBean(Context context, DeviceVillageBean villageBean) {
        DBManager.getDaoSession(context).getDeviceVillageBeanDao().insert(villageBean);
    }

    /**
     * 根据deviceId查询
     *
     * @param context  上下文
     * @param deviceId 设备Id
     * @return 数据
     */
    public static String queryDataByDeviceId(Context context,String deviceId) {
        DeviceVillageBeanDao dao = DBManager.getDaoSession(context).getDeviceVillageBeanDao();
        DeviceVillageBean villageBean= dao.queryBuilder().where(
                DeviceVillageBeanDao.Properties.DeviceId.eq(deviceId)).build().unique();
        if (villageBean != null) {
            return villageBean.getVillageId();
        }
        return null;
    }
}
