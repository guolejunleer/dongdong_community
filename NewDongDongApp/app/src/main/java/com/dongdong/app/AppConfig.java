package com.dongdong.app;

import java.util.Map;

import android.content.Context;

import com.dongdong.app.util.SPUtils;

/**
 * 应用程序配置类：用于保存用户相关信息及设置
 *
 * @author leer
 */
public class AppConfig {
    // sharedPreferences file name
    public static final String DONG_CONFIG_SHARE_PREF_NAME = "dd_configs";

    // all sharedPreferences key name
    public static final String KEY_VERSION_CODE="KEY_VERSION_CODE";
    public static final String KEY_FRITST_START = "KEY_FRIST_START";
    public static final String KEY_CONF_APP_UNIQUEID = "KEY_APP_UNIQUEID";
    public static final String KEY_DOUBLE_CLICK_EXIT = "KEY_DOUBLE_CLICK_EXIT";
    public static final String KEY_USER_ID = "USER_ID";
    public static final String KEY_USER_NAME = "USER_NAME";
    public static final String KEY_USER_PWD = "USER_PWD";
    public static final String KEY_DEVICE_ID = "DEVICE_ID";
    public static final String KEY_DEVICE_SERIAL = "DEVICE_SERIAL";
    public static final String KEY_IS_LOGIN = "IS_LOGIN";
    public static final String KEY_DEFAULT_DEVICE_ID = "KEY_DEFAULT_DEVICE_ID";

    // intent bundle key
    public static final String INTENT_BUNDLE_KEY = "INTENT_BUNDLE_KEY";
    // all bundle key
    public static final String BUNDLE_KEY_USER_ID = "BUNDLE_USER_ID";
    public static final String BUNDLE_KEY_DEVICE_INFO = "BUNDLE_DEVCIE_INFO";
    public static final String BUNDLE_KEY_DEVICE_ID = "BUNDLE_DEVCIE_ID";
    public static final String BUNDLE_KEY_INITIATIVE = "BUNDLE_INITIATIVE";
    public static final String BUNDLE_KEY_PUSH_TIME = "BUNDLE_KEY_PUSH_TIME";

    public static final String BUNDLE_KEY_IS_DELAY_PUSH = "BUNDLE_KEY_IS_DELAY_PUSH";

    public static final String COMPANY_PHONE = "057158111836";
    public static final String CACH_IMAGE_PATH = "temp";
    //DES Key
    public static final String DES_KEY = "DONGDONG";
    public static final String SD_TAKE_PICTURE_PATH = "TakePicture";
    public static final int MAX_OPEN_DOOR_RECORD_COUNT = 100;
    public static final int MAX_VISITOR_PHOTO_COUNT = 40;
    public static final int MAX_BULLETIN_COUNT = 100;

    //公用JSON字符串(物业API交互)
    public static final String JSON_RESULT_CODE="result_code";
    public static final String JSON_RESPONSE_PARAMS="response_params";
    public static final String JSON_CORRECT_RESULT_CODE="200";
    public static final String JSON_ERROR_RESULT_CODE="201";
    public static final String JSON_EMPTY_DATA="[]";

    //访问API地址
//    public static final String BASE_URL = "http://wuye.dd121.com/dd/wuye_api_d/2.0/";
    public static final String BASE_URL = "http://wuye.dd121.com/dd/wuye_api/2.0/";
    // 55测试服务器
    //public static final String BASE_URL = "http://192.168.68.55/web/wuye_api/apiserver/2.0/";
    private Context mContext;
    private static AppConfig mAppConfig;

    public static AppConfig getAppConfig(Context context) {
        if (mAppConfig == null) {
            mAppConfig = new AppConfig();
            mAppConfig.mContext = context;
        }
        return mAppConfig;
    }

    public void setConfigValue(String spName, String key, Object value) {
        SPUtils.setParam(mContext, spName, key, value);
    }

    public Object getConfigValue(String spName, String key, Object defaultValue) {
        return SPUtils.getParam(mContext, spName, key, defaultValue);
    }

    public Map<String, ?> getAll(String spName) {
        return SPUtils.getAll(mContext, spName);
    }

    public void remove(String spName, String key) {
        SPUtils.remove(mContext, spName, key);
    }

    public void removeAll(String spName) {
        SPUtils.removeAll(mContext, spName);
    }

    public boolean contains(String spName, String key) {
        return SPUtils.contains(mContext, spName, key);
    }
}
