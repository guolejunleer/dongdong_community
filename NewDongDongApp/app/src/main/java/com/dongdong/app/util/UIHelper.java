package com.dongdong.app.util;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.dongdong.app.AppConfig;
import com.dongdong.app.MainActivity;
import com.dongdong.app.ui.ApplyKeyActivity;
import com.dongdong.app.ui.CommonPhoneActivity;
import com.dongdong.app.ui.DeviceListActivity;
import com.dongdong.app.ui.FunctionManagerActivity;
import com.dongdong.app.ui.HomeSafetyActivity;
import com.dongdong.app.ui.InfoActivity;
import com.dongdong.app.ui.RepairsActivity;
import com.dongdong.app.ui.ShakeOpenDoorActivity;
import com.dongdong.app.ui.VideoViewActivity;
import com.gViewerX.util.LogUtils;

public class UIHelper {
	/**
	 * 显示信息界面
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
	 * 显示信息界面
	 * 
	 * @param context
	 */
	public static void showInfoActivity(Context context) {
		Intent intent = new Intent(context, InfoActivity.class);
		context.startActivity(intent);
	}

	/**
	 * 显示监视界面
	 * 
	 * @param context
	 */
	public static void showVideoViewActivity(Context context,
			boolean isInitiative, String deviceID) {

		try {
			LogUtils.i("UIHelper.clazz--->>>showVideoViewActivity start !!!");
			Intent intent = new Intent(context, VideoViewActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			Bundle bundle = new Bundle();
			bundle.putBoolean(AppConfig.BUNDLE_KEY_INITIATIVE, isInitiative);
			bundle.putString(AppConfig.BUNDLE_KEY_DEVICE_ID, deviceID);
			intent.putExtra(AppConfig.INTENT_BUNDLE_KEY, bundle);
			context.startActivity(intent);
		} catch (Exception e) {
			e.printStackTrace();
			LogUtils.i("UIHelper.clazz--->>>showVideoViewActivity e !!!"
					+ e.toString());
		}

		LogUtils.i("UIHelper.clazz--->>>showVideoViewActivity end!!!");
	}

	/**
	 * 显示申请钥匙界面
	 * 
	 * @param context
	 */
	public static void showApplyKeyActivity(Context context) {
		Intent intent = new Intent(context, ApplyKeyActivity.class);
		context.startActivity(intent);
	}

	/**
	 * 显示摇一摇开门界面
	 * 
	 * @param context
	 */
	public static void showShakeOpenDoorActivity(Context context) {
		Intent intent = new Intent(context, ShakeOpenDoorActivity.class);
		context.startActivity(intent);
	}

	/**
	 * 显示报修界面
	 * 
	 * @param context
	 */
	public static void showRepairsActivity(Context context) {
		Intent intent = new Intent(context, RepairsActivity.class);
		context.startActivity(intent);
	}

	/**
	 * 显示安居家防界面
	 * 
	 * @param context
	 */
	public static void showHomeSafetyActivity(Context context) {
		Intent intent = new Intent(context, HomeSafetyActivity.class);
		context.startActivity(intent);
	}

	/**
	 * 显示访客记录界面
	 * 
	 * @param context
	 */
	public static void showVisitorRecordsActivity(Context context) {
//		Intent intent = new Intent(context, VisitorRecordsActivity.class);
//		context.startActivity(intent);
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

	/**
	 * 显示功能管理界面
	 * 
	 * @param context
	 */
	public static void showFunctionManagerActivity(Context context) {
		Intent intent = new Intent(context, FunctionManagerActivity.class);
		context.startActivity(intent);
	}

}
