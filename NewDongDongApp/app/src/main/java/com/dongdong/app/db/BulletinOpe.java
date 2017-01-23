package com.dongdong.app.db;

import android.content.Context;

import com.dongdong.app.bean.BulletinBean;
import com.dongdong.app.db.gen.BulletinBeanDao;

import org.greenrobot.greendao.query.QueryBuilder;

import java.util.List;

/**
 * 操作物业公告的执行者
 */
public class BulletinOpe {

    /**
     * 添加一条数据至数据库
     *
     * @param context      上下文
     * @param bulletinBean 数据
     */
    public static void insert(Context context, BulletinBean bulletinBean) {
        DBManager.getDaoSession(context).getBulletinBeanDao().insert(bulletinBean);
    }

    /**
     * 清空数据
     *
     * @param context 上下文
     */
    public static void delete(Context context, List<Long> ids) {
        DBManager.getDaoSession(context).getBulletinBeanDao().deleteByKeyInTx(ids);
    }

    /**
     * 通过villageId查询物业公告
     *
     * @param context   上下文
     * @param villageId 小区ID
     */
    public static List<BulletinBean> queryDataByVillageId(Context context, String villageId) {
        BulletinBeanDao dao = DBManager.getDaoSession(context).getBulletinBeanDao();
        return dao.queryBuilder().where(
                BulletinBeanDao.Properties.VillageId.eq(villageId)).build().list();
    }

    /**
     * 通过deviceId查询物业公告
     *
     * @param context  上下文
     * @param deviceId 设备Id
     */
    public static List<BulletinBean> queryDataByDeviceId(Context context, String deviceId) {
        BulletinBeanDao dao = DBManager.getDaoSession(context).getBulletinBeanDao();
        return dao.queryBuilder().where(
                BulletinBeanDao.Properties.DeviceId.eq(deviceId)).build().list();
    }

    /**
     * 查询所有数据
     *
     * @param context 上下文
     * @return 数据库所有数据
     */
    public static List<BulletinBean> queryAll(Context context) {
        QueryBuilder<BulletinBean> builder = DBManager.getDaoSession(context).
                getBulletinBeanDao().queryBuilder().orderDesc(BulletinBeanDao.Properties.Created);
        return builder.build().list();
    }
}
