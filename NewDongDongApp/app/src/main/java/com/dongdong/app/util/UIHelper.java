package com.dongdong.app.util;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.dongdong.app.AppConfig;
import com.dongdong.app.MainActivity;
import com.dongdong.app.ui.ApplyKeyActivity;
import com.dongdong.app.ui.CommonPhoneActivity;
import com.dongdong.app.ui.DeviceListActivity;
import com.dongdong.app.ui.FinanceActivity;
import com.dongdong.app.ui.HomeSafeActivity;
import com.dongdong.app.ui.MessageActivity;
import com.dongdong.app.ui.ParkingActivity;
import com.dongdong.app.ui.RepairsActivity;
import com.dongdong.app.ui.ShakeOpenDoorActivity;
import com.dongdong.app.ui.VideoViewActivity;
import com.dongdong.app.ui.VisitorRecordsActivity;

import java.util.Date;

public class UIHelper {

    public static void showMainActivity(Context context, String deviceID) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Bundle bundle = new Bundle();
        bundle.putString(AppConfig.BUNDLE_KEY_DEVICE_ID, deviceID);
        intent.putExtra(AppConfig.INTENT_BUNDLE_KEY, bundle);
        context.startActivity(intent);
    }

    public static void showMainActivityWithPushTime(Context context, String deviceID, String pushTime) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Bundle bundle = new Bundle();
        bundle.putString(AppConfig.BUNDLE_KEY_DEVICE_ID, deviceID);
        intent.putExtra(AppConfig.INTENT_BUNDLE_KEY, bundle);
        bundle.putString(AppConfig.BUNDLE_KEY_PUSH_TIME, pushTime);
        context.startActivity(intent);
    }

    public static void showDeviceListActivity(Context context) {
        Intent intent = new Intent(context, DeviceListActivity.class);
        context.startActivity(intent);
    }

    public static void showMessageActivity(Context context) {
        Intent intent = new Intent(context, MessageActivity.class);
        context.startActivity(intent);
    }

    public static void showCommonPhoneActivity(Context context) {
        Intent intent = new Intent(context, CommonPhoneActivity.class);
        context.startActivity(intent);
    }

    public static void showVideoViewActivity(Context context, boolean isActive, String deviceID) {
        Intent intent = new Intent(context, VideoViewActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Bundle bundle = new Bundle();
        bundle.putBoolean(AppConfig.BUNDLE_KEY_INITIATIVE, isActive);
        bundle.putString(AppConfig.BUNDLE_KEY_DEVICE_ID, deviceID);
        intent.putExtra(AppConfig.INTENT_BUNDLE_KEY, bundle);
        context.startActivity(intent);
    }

    public static void showApplyKeyActivity(Context context) {
        Intent intent = new Intent(context, ApplyKeyActivity.class);
        context.startActivity(intent);
    }

    public static void showVisitorRecordActivity(Context context, int deviceID) {
        Intent intent = new Intent(context, VisitorRecordsActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Bundle bundle = new Bundle();
        bundle.putInt(AppConfig.BUNDLE_KEY_DEVICE_ID, deviceID);
        intent.putExtra(AppConfig.INTENT_BUNDLE_KEY, bundle);
        context.startActivity(intent);
    }

    public static void showHomeSafeActivity(Context context) {
        Intent intent = new Intent(context, HomeSafeActivity.class);
        context.startActivity(intent);
    }

    public static void showParkingActivity(Context context) {
        Intent intent = new Intent(context, ParkingActivity.class);
        context.startActivity(intent);
    }

    public static void showFinanceActivity(Context context) {
        Intent intent = new Intent(context, FinanceActivity.class);
        context.startActivity(intent);
    }

    public static void showRepairsActivity(Context context) {
        Intent intent = new Intent(context, RepairsActivity.class);
        context.startActivity(intent);
    }

    public static void showShakeOpenDoorActivity(Context context) {
        Intent intent = new Intent(context, ShakeOpenDoorActivity.class);
        context.startActivity(intent);
    }
}
