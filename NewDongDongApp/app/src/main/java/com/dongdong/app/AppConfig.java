package com.dongdong.app;

import java.util.Map;

import android.content.Context;

import com.dongdong.app.util.SPUtils;

/**
 * 
 * 应用程序配置类：用于保存用户相关信息及设置
 * 
 * @author leer
 * 
 */
public class AppConfig {
	// sharedPreferences file name
	public static final String DONG_CONFIG_SHARE_PREF_NAME = "dd_configs";

	// all sharedPreferences key name
	public static final String KEY_FRITST_START = "KEY_FRIST_START";
	public static final String KEY_CONF_APP_UNIQUEID = "KEY_APP_UNIQUEID";
	public static final String KEY_DOUBLE_CLICK_EXIT = "KEY_DOUBLE_CLICK_EXIT";
	public static final String KEY_USER_NAME = "USER_NAME";
	public static final String KEY_USER_PWD = "USER_PWD";
	public static final String KEY_DEVICE_ID = "DEVICE_ID";
	public static final String KEY_DEVICE_SERIAL = "DEVICE_SERIAL";
	public static final String KEY_IS_LOGIN = "IS_LOGIN";
	public static final String KEY_DEVCIE_NAME="KEY_DEVCIE_NAME";

	// intent bundle key
	public static final String INTENT_BUNDLE_KEY = "INTENT_BUNDLE_KEY";
	// all bundle key
	public static final String BUNDLE_KEY_DEVICE_INFO = "BUNDLE_DEVCIE_INFO";
	public static final String BUNDLE_KEY_DEVICE_ID = "BUNDLE_DEVCIE_ID";
	public static final String BUNDLE_KEY_INITIATIVE = "BUNDLE_INITIATIVE";

	public static final String COMPANY_PHONE = "057158111836";
	public static final String CACH_IMAGE_PATH = "temp";

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
