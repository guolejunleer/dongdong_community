package com.dongdong.app.base;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.content.res.Resources;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.dd121.community.R;
import com.dongdong.app.bean.FunctionBean;
import com.dongdong.app.util.TDevice;

import java.util.ArrayList;
import java.util.List;

@SuppressLint("InflateParams")
public class BaseApplication extends Application {

	/**
	 * 根据厂家不同，控制编译功能模块 0 : 默认值 ;1 : xxx ;2 : yyy ; 3 : zzz; 4 : aaa ;5 : bbb ;6
	 * : ccc
	 */
	public static final int FUNCTION_MESSAGE = 1;
	public static final int FUNCTION_MONITOR = 2;
	public static final int FUNCTION_APPLYKEY = 3;
	public static final int FUNCTION_SHAPEOPENDOOR = 4;
	public static final int FUNCTION_REPAIR = 5;
	public static final int FUNCTION_HOMESAFE = 6;
	public static final int FUNCTION_VISITORRECORD = 7;
	public static final int FUNCTION_PHONE = 8;
	public static final int FUNCTION_DD_FUNCTION_FINANCE = 9;
	public static final int FUNCTION_DD_FUNCTION_PARKING = 10;
	// public static final int FUNCTION_DD_FUNCTION_MORE = 11;
	// public static final int FUNCTION_DD_FUNCTION_CARCAR =;
	// public static final int FUNCTION_DD_FUNCTION_CAR = 10;
	// public static final int FUNCTION_DD_FUNCTION_AMUSE = 11;
	private static final String[][] mVenderInfoArray = {
			{ "com.dongdong.app", "www.dd121.com", "123" },
			{ "com.dd121.community", "www.dd121.com", "123" },
			{ "com.dongdong.app.test002", "www.dd121.com", "123" } };

	private static Context mContext;
	private static Resources mResource;

	private static String mLastToast = "";
	private static long mLastToastTime;

	@Override
	public void onCreate() {
		super.onCreate();
		mContext = getApplicationContext();
		mResource = mContext.getResources();
	}

	public static synchronized BaseApplication context() {
		return (BaseApplication)mContext;
	}

	public static Resources resources() {
		return mResource;
	}

	// long
	public static void showToastLongInBottom(int message) {
		showToastInBottom(message, Toast.LENGTH_LONG, 0);
	}

	public static void showToastLongInBottom(int message, int icon) {
		showToastInBottom(message, Toast.LENGTH_LONG, icon);
	}

	public static void showToastLongInBottom(String message) {
		showToast(message, Toast.LENGTH_LONG, 0, Gravity.BOTTOM);
	}

	public static void showToastLongInCenter(int message) {
		showToast(message, Toast.LENGTH_LONG, 0, Gravity.CENTER);
	}

	// short
	public static void showToastShortInBottom(int message) {
		showToastInBottom(message, Toast.LENGTH_SHORT, 0);
	}

	public static void showToastShortInBottom(int message, Object... args) {
		showToast(message, Toast.LENGTH_SHORT, 0, Gravity.BOTTOM, args);
	}

	public static void showToastShortInBottom(String message) {
		showToast(message, Toast.LENGTH_SHORT, 0, Gravity.BOTTOM);
	}

	public static void showToastInBottom(int message, int duration, int icon) {
		showToast(message, duration, icon, Gravity.BOTTOM);
	}

	public static void showToastShortInCenter(int message) {
		showToast(message, Toast.LENGTH_SHORT, 0, Gravity.CENTER);
	}

	public static void showToastShortInTop(int message) {
		showToast(message, Toast.LENGTH_SHORT, 0, Gravity.TOP);
	}

	public static void showToast(int message, int duration, int icon,
			int gravity) {
		showToast(context().getString(message), duration, icon, gravity);
	}

	public static void showToast(int message, int duration, int icon,
			int gravity, Object... args) {
		showToast(context().getString(message, args), duration, icon, gravity);
	}

	public static void showToast(String message, int duration, int icon,
			int gravity) {
		if (!TextUtils.isEmpty(message) && !message.equalsIgnoreCase("")) {
			long time = System.currentTimeMillis();
			if (!message.equalsIgnoreCase(mLastToast)
					|| Math.abs(time - mLastToastTime) > 2000) {
				View view = LayoutInflater.from(context()).inflate(
						R.layout.view_toast, null);
				((TextView) view.findViewById(R.id.tv_title)).setText(message);
				if (icon != 0) {
					((ImageView) view.findViewById(R.id.icon_iv))
							.setImageResource(icon);
					(view.findViewById(R.id.icon_iv))
							.setVisibility(View.VISIBLE);
				}
				Toast toast = new Toast(context());
				toast.setView(view);
				if (gravity == Gravity.CENTER) {
					toast.setGravity(gravity, 0, 0);
				} else if (gravity == Gravity.TOP) {
					toast.setGravity(gravity, 0,
							TDevice.dp2px(BaseApplication.context(), 50));
				} else {
					toast.setGravity(gravity, 0, 35);
				}

				toast.setDuration(duration);
				toast.show();
				mLastToast = message;
				mLastToastTime = System.currentTimeMillis();
			}
		}
	}

	// 登录，注册，忘记密码
	public static void showMyToast(int message) {
		WindowManager wm = (WindowManager) context().getSystemService(Context.WINDOW_SERVICE);
		// 获得屏幕的宽和高
		int width = wm.getDefaultDisplay().getWidth();
		int height = wm.getDefaultDisplay().getHeight();

		LayoutInflater inflater = (LayoutInflater) context().getSystemService(
				Context.LAYOUT_INFLATER_SERVICE);
		// 设置toast的宽和高
		LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
				(width / 100) * 99, height / 12);

		View view = inflater.inflate(R.layout.view_mytoast, null);
		LinearLayout linearLayout = (LinearLayout) view.findViewById(R.id.ll_mytoastlayout);
		linearLayout.setLayoutParams(layoutParams);
		ImageView imageView = (ImageView) view.findViewById(R.id.iv_mytoastimg);
		imageView.setBackgroundResource(R.mipmap.warning_sign);
		TextView textView = (TextView) view.findViewById(R.id.tv_mytoasttxt);
		textView.setText(message);

		Toast toast = new Toast(context());
		toast.setDuration(Toast.LENGTH_SHORT);
		toast.setGravity(Gravity.TOP, 0, 0);
		toast.setView(view);
		toast.show();
	}

	public static List<FunctionBean> getFunctionsDatasInit() {
		List<FunctionBean> list = new ArrayList<>();
		String nowPackageName = mContext.getPackageName();

		if (nowPackageName.equals(mVenderInfoArray[0][0])) {
			// list.add(new Function("", 0, -1));// 对应广告页
			list.add(new FunctionBean(mContext.getString(R.string.message),
					R.mipmap.ic_dd_message, 0));
			list.add(new FunctionBean(mContext.getString(R.string.phone),
					R.mipmap.ic_dd_phone, 1));
			list.add(new FunctionBean(mContext.getString(R.string.monitor),
					R.mipmap.ic_dd_monitor, 2));
			list.add(new FunctionBean(mContext.getString(R.string.applykey),
					R.mipmap.ic_dd_applykey, 3));
			list.add(new FunctionBean(mContext
					.getString(R.string.visitorrecord),
					R.mipmap.ic_dd_visitorrecord, 4));
			list.add(new FunctionBean(mContext.getString(R.string.homesafe),
					R.mipmap.ic_dd_homesafe, 5));
			list.add(new FunctionBean(mContext.getString(R.string.repair),
					R.mipmap.ic_dd_repair, 6));
			list.add(new FunctionBean(mContext
					.getString(R.string.shapeopendoor),
					R.mipmap.ic_dd_shapeopendoor, 7));
		} else if (nowPackageName.equals(mVenderInfoArray[1][0])) {
			list.add(new FunctionBean(mContext.getString(R.string.message),
					FUNCTION_MESSAGE, 1));
			list.add(new FunctionBean(mContext.getString(R.string.phone),
					FUNCTION_PHONE, 2));
			list.add(new FunctionBean(mContext.getString(R.string.monitor),
					FUNCTION_MONITOR, 3));
			list.add(new FunctionBean(mContext.getString(R.string.applykey),
					FUNCTION_APPLYKEY, 4));
			list.add(new FunctionBean(mContext
					.getString(R.string.visitorrecord), FUNCTION_VISITORRECORD,
					5));
			list.add(new FunctionBean(mContext.getString(R.string.homesafe),
					FUNCTION_HOMESAFE, 6));
			list.add(new FunctionBean(mContext
					.getString(R.string.dd_function_parking),
					FUNCTION_DD_FUNCTION_PARKING, 7));
			list.add(new FunctionBean(mContext
					.getString(R.string.dd_function_finance),
					FUNCTION_DD_FUNCTION_FINANCE, 8));
			list.add(new FunctionBean(mContext.getString(R.string.repair),
					FUNCTION_REPAIR, 9));
			list.add(new FunctionBean(mContext
					.getString(R.string.shapeopendoor), FUNCTION_SHAPEOPENDOOR,
					10));
		}
//		else if (nowPackageName.equals(mVenderInfoArray[2][0])) {
//
//		}
		return list;
	}
}
