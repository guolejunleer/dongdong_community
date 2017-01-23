package com.dongdong.app.db;

import android.content.Context;

import com.dongdong.app.bean.VillageBean;
import com.dongdong.app.db.gen.VillageBeanDao;

public class VillageOpe {
    /**
     * 添加一条数据至数据库
     *
     * @param context     上下文
     * @param villageBean 数据
     */
    public static void insertDataByVillageBean(Context context, VillageBean villageBean) {
        DBManager.getDaoSession(context).getVillageBeanDao().insert(villageBean);
    }

    /**
     * 根据deviceId查询
     *
     * @param context  上下文
     * @param deviceId 设备Id
     * @return 数据
     */
    public static String queryDataByDeviceId(Context context,String deviceId) {
        VillageBeanDao dao = DBManager.getDaoSession(context).getVillageBeanDao();
        VillageBean villageBean= dao.queryBuilder().where(
                VillageBeanDao.Properties.DeviceId.eq(deviceId)).build().unique();
        if (villageBean != null) {
            return villageBean.getVillageId();
        }
        return null;
    }
}
