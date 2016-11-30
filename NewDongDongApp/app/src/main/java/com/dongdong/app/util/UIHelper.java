package com.dongdong.app.util;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.dongdong.app.AppConfig;
import com.dongdong.app.MainActivity;
import com.dongdong.app.ui.CommonPhoneActivity;
import com.dongdong.app.ui.DeviceListActivity;
import com.dongdong.app.ui.VideoViewActivity;
import com.gViewerX.util.LogUtils;

public class UIHelper {
    /**
     * 显示首页
     *
     * @param context
     */
    public static void showMainActivity(Context context, String deviceID) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Bundle bundle = new Bundle();
        bundle.putString(AppConfig.BUNDLE_KEY_DEVICE_ID, deviceID);
        intent.putExtra(AppConfig.INTENT_BUNDLE_KEY, bundle);
        context.startActivity(intent);
    }

    /**
     * 显示监视界面
     *
     * @param context
     */
    public static void showVideoViewActivity(Context context,
                                             boolean isActive, String deviceID) {
        Intent intent = new Intent(context, VideoViewActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Bundle bundle = new Bundle();
        bundle.putBoolean(AppConfig.BUNDLE_KEY_INITIATIVE, isActive);
        bundle.putString(AppConfig.BUNDLE_KEY_DEVICE_ID, deviceID);
        intent.putExtra(AppConfig.INTENT_BUNDLE_KEY, bundle);
        context.startActivity(intent);
    }


    /**
     * 显示常用电话界面
     *
     * @param context
     */
    public static void showCommonPhoneActivity(Context context) {
        Intent intent = new Intent(context, CommonPhoneActivity.class);
        context.startActivity(intent);
    }

    /**
     * 显示选择设备界面
     *
     * @param context
     */
    public static void showDeviceListActivity(Context context) {
        Intent intent = new Intent(context, DeviceListActivity.class);
        context.startActivity(intent);
    }
}
