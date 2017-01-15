package com.dongdong.app;

import java.util.UUID;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.text.TextUtils;

import com.ddclient.dongsdk.DongSDK;
import com.dongdong.app.api.ApiHttpClient;
import com.dongdong.app.base.BaseApplication;
import com.dongdong.app.util.LogUtils;
import com.dongdong.app.util.SPUtils;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.PersistentCookieStore;

/**
 * 全局应用程序类：用于保存和调用全局应用配置及访问网络数据
 *
 * @author leer (http://www.dd121.com)
 * @version 1.0
 * @created 2016-06-15
 */
public class AppContext extends BaseApplication {

    public static AppConfig mAppConfig;
    private static AppContext mInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        init();
        // Thread.setDefaultUncaughtExceptionHandler(AppException
        // .getAppExceptionHandler(this));
    }

    /**
     * 获得当前应用运行的AppContext
     *
     * @return AppContext
     */
    public static AppContext getInstance() {
        return mInstance;
    }

    private void init() {
        mAppConfig = AppConfig.getAppConfig(this);
        // 初始化网络请求
        AsyncHttpClient client = new AsyncHttpClient();
        PersistentCookieStore myCookieStore = new PersistentCookieStore(this);
        client.setCookieStore(myCookieStore);
        ApiHttpClient.setHttpClient(client);
        ApiHttpClient.setCookie(ApiHttpClient.getCookie(mAppConfig));
        DongSDK.dongSdk_Init();

//        DongSDK.initDongSDK(this);
        // DongSDK.initializePush(this, DongSDK.PUSH_TYPE_ALL);// 初始化推送
        LogUtils.i("AppContext.clazz DongSDK.initDongSDK!!!");
    }

    /**
     * 获取App唯一标识
     *
     * @return appId
     */
    public String getAppId() {
        String uniqueID = (String) mAppConfig.getConfigValue(
                AppConfig.DONG_CONFIG_SHARE_PREF_NAME,
                AppConfig.KEY_CONF_APP_UNIQUEID, "");
        if (TextUtils.isEmpty(uniqueID)) {
            uniqueID = UUID.randomUUID().toString();
            mAppConfig.setConfigValue(AppConfig.DONG_CONFIG_SHARE_PREF_NAME,
                    AppConfig.KEY_CONF_APP_UNIQUEID, uniqueID);
        }
        return uniqueID;
    }

    /**
     * 获取App安装包信息
     *
     * @return
     */
    public PackageInfo getPackageInfo() {
        PackageInfo info = null;
        try {
            info = getPackageManager().getPackageInfo(getPackageName(), 0);
        } catch (NameNotFoundException e) {
            e.printStackTrace(System.err);
        }
        if (info == null)
            info = new PackageInfo();
        return info;
    }

    /**
     * 判断当前版本是否兼容目标版本的方法
     *
     * @param VersionCode
     * @return
     */
    public static boolean isMethodsCompat(int VersionCode) {
        int currentVersion = android.os.Build.VERSION.SDK_INT;
        return currentVersion >= VersionCode;
    }

    public static boolean isFristStart() {
        return (Boolean) SPUtils.getParam(mInstance,
                AppConfig.DONG_CONFIG_SHARE_PREF_NAME,
                AppConfig.KEY_FRITST_START, true);
    }

    public static void setFristStart(boolean frist) {
        SPUtils.setParam(mInstance, AppConfig.DONG_CONFIG_SHARE_PREF_NAME,
                AppConfig.KEY_FRITST_START, frist);
    }

}
