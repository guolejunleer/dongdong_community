package com.dongdong.app.util;

import java.io.File;
import java.lang.reflect.Field;
import java.text.NumberFormat;
import java.util.Locale;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.PowerManager;
import android.provider.ContactsContract;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Display;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import com.dd121.louyu.R;
import com.ddclient.dongsdk.DeviceInfo;
import com.dongdong.app.AppContext;
import com.dongdong.app.base.BaseApplication;

public class TDevice {

    // 手机网络类型
    public static final int NETTYPE_WIFI = 0x01;
    public static final int NETTYPE_CMWAP = 0x02;
    public static final int NETTYPE_CMNET = 0x03;

    public static boolean GTE_HC;
    public static boolean GTE_ICS;
    public static boolean PRE_HC;
    private static Boolean mHasBigScreen = null;
    private static Boolean mHasCamera = null;
    private static Boolean mIsTablet = null;
    private static Integer mLoadFactor = null;

    public static float mDisplayDensity = 0.0F;

    static {
        GTE_ICS = Build.VERSION.SDK_INT >= 14;
        GTE_HC = Build.VERSION.SDK_INT >= 11;
        PRE_HC = Build.VERSION.SDK_INT < 11;
    }

    private TDevice() {
    }

    public static float dpToPixel(float dp) {
        return dp * (getDisplayMetrics().densityDpi / 160F);
    }

    /**
     * dp转px
     *
     * @param context
     * @param val
     * @return
     */
    public static int dp2px(Context context, float dpVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                dpVal, context.getResources().getDisplayMetrics());
    }

    /**
     * sp转px
     *
     * @param context
     * @param val
     * @return
     */
    public static int sp2px(Context context, float spVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
                spVal, context.getResources().getDisplayMetrics());
    }

    /**
     * px转dp
     *
     * @param context
     * @param pxVal
     * @return
     */
    public static float px2dp(Context context, float pxVal) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (pxVal / scale);
    }

    /**
     * px转sp
     *
     * @param fontScale
     * @param pxVal
     * @return
     */
    public static float px2sp(Context context, float pxVal) {
        return (pxVal / context.getResources().getDisplayMetrics().scaledDensity);
    }

    public static int getDefaultLoadFactor() {
        if (mLoadFactor == null) {
            Integer integer = Integer.valueOf(0xf & BaseApplication.context()
                    .getResources().getConfiguration().screenLayout);
            mLoadFactor = integer;
            mLoadFactor = Integer.valueOf(Math.max(integer.intValue(), 1));
        }
        return mLoadFactor.intValue();
    }

    /**
     * 获取屏幕密度
     *
     * @return
     */
    public static float getDensity() {
        if (mDisplayDensity == 0.0)
            mDisplayDensity = getDisplayMetrics().density;
        return mDisplayDensity;
    }

    /**
     * 获取显示规格
     *
     * @return
     */
    public static DisplayMetrics getDisplayMetrics() {
        DisplayMetrics displaymetrics = new DisplayMetrics();
        ((WindowManager) BaseApplication.context().getSystemService(
                Context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(
                displaymetrics);
        return displaymetrics;
    }

    public static float getScreenHeight() {
        return getDisplayMetrics().heightPixels;
    }

    public static float getScreenWidth() {
        return getDisplayMetrics().widthPixels;
    }

    public static int[] getRealScreenSize(Activity activity) {
        int[] size = new int[2];
        int screenWidth = 0, screenHeight = 0;
        WindowManager w = activity.getWindowManager();
        Display d = w.getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        d.getMetrics(metrics);
        // since SDK_INT = 1;
        screenWidth = metrics.widthPixels;
        screenHeight = metrics.heightPixels;
        // includes window decorations (statusbar bar/menu bar)
        if (Build.VERSION.SDK_INT >= 14 && Build.VERSION.SDK_INT < 17)
            try {
                screenWidth = (Integer) Display.class.getMethod("getRawWidth")
                        .invoke(d);
                screenHeight = (Integer) Display.class
                        .getMethod("getRawHeight").invoke(d);
            } catch (Exception ignored) {
            }
        // includes window decorations (statusbar bar/menu bar)
        if (Build.VERSION.SDK_INT >= 17)
            try {
                Point realSize = new Point();
                Display.class.getMethod("getRealSize", Point.class).invoke(d,
                        realSize);
                screenWidth = realSize.x;
                screenHeight = realSize.y;
            } catch (Exception ignored) {
            }
        size[0] = screenWidth;
        size[1] = screenHeight;
        return size;
    }

    public static int getStatusBarHeight() {
        Class<?> c = null;
        Object obj = null;
        Field field = null;
        int x = 0;
        try {
            c = Class.forName("com.android.internal.R$dimen");
            obj = c.newInstance();
            field = c.getField("status_bar_height");
            x = Integer.parseInt(field.get(obj).toString());
            return BaseApplication.context().getResources()
                    .getDimensionPixelSize(x);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 判断是否为大屏幕
     *
     * @return
     */
    public static boolean hasBigScreen() {
        boolean flag = true;
        if (mHasBigScreen == null) {
            boolean flag1;
            if ((0xf & BaseApplication.context().getResources()
                    .getConfiguration().screenLayout) >= 3)
                flag1 = flag;
            else
                flag1 = false;
            Boolean boolean1 = Boolean.valueOf(flag1);
            mHasBigScreen = boolean1;
            if (!boolean1.booleanValue()) {
                if (getDensity() <= 1.5F)
                    flag = false;
                mHasBigScreen = Boolean.valueOf(flag);
            }
        }
        return mHasBigScreen.booleanValue();
    }

    /**
     * 判断是否有照像机
     *
     * @return
     */
    public static final boolean hasCamera() {
        if (mHasCamera == null) {
            PackageManager pckMgr = BaseApplication.context()
                    .getPackageManager();
            boolean flag = pckMgr
                    .hasSystemFeature("android.hardware.camera.front");
            boolean flag1 = pckMgr.hasSystemFeature("android.hardware.camera");
            boolean flag2;
            if (flag || flag1)
                flag2 = true;
            else
                flag2 = false;
            mHasCamera = Boolean.valueOf(flag2);
        }
        return mHasCamera.booleanValue();
    }

    /**
     * 判断是否有物理菜单键
     *
     * @param context
     * @return
     */
    @SuppressLint("NewApi")
    public static boolean hasHardwareMenuKey(Context context) {
        boolean flag = false;
        if (PRE_HC)
            flag = true;
        else if (GTE_ICS) {
            flag = ViewConfiguration.get(context).hasPermanentMenuKey();
        } else
            flag = false;
        return flag;
    }

    /**
     * 判断是否有网络
     *
     * @return
     */
    /**
     * @return
     */
    public static boolean hasInternet() {
        boolean flag;
        if (((ConnectivityManager) BaseApplication.context().getSystemService(
                "connectivity")).getActiveNetworkInfo() != null)
            flag = true;
        else
            flag = false;
        return flag;
    }

    /**
     * 判断应用包是否存在
     *
     * @param pckName
     * @return
     */
    public static boolean isPackageExist(String pckName) {
        try {
            PackageInfo pckInfo = BaseApplication.context().getPackageManager()
                    .getPackageInfo(pckName, 0);
            if (pckInfo != null)
                return true;
        } catch (NameNotFoundException e) {
            LogUtils.e("TDevice.clazz-->>>" + e.getMessage());
        }
        return false;
    }

    /**
     * 隐藏view
     *
     * @param view
     */
    public static void hideAnimatedView(View view) {
        if (PRE_HC && view != null)
            view.setPadding(view.getWidth(), 0, 0, 0);
    }

    /**
     * 隐藏输入键盘
     *
     * @param view
     */
    public static void hideSoftKeyboard(View view) {
        if (view == null)
            return;
        ((InputMethodManager) BaseApplication.context().getSystemService(
                Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(
                view.getWindowToken(), 0);
    }

    /**
     * 判断是否横屏
     *
     * @return
     */
    public static boolean isLandscape() {
        boolean flag;
        if (BaseApplication.context().getResources().getConfiguration().orientation == 2)
            flag = true;
        else
            flag = false;
        return flag;
    }

    /**
     * 判断是否坚屏
     *
     * @return
     */
    public static boolean isPortrait() {
        boolean flag = true;
        if (BaseApplication.context().getResources().getConfiguration().orientation != 1)
            flag = false;
        return flag;
    }

    /**
     * 决断是否为平板
     *
     * @return
     */
    public static boolean isTablet() {
        if (mIsTablet == null) {
            boolean flag;
            if ((0xf & BaseApplication.context().getResources()
                    .getConfiguration().screenLayout) >= 3)
                flag = true;
            else
                flag = false;
            mIsTablet = Boolean.valueOf(flag);
        }
        return mIsTablet.booleanValue();
    }

    /**
     * 显示view
     *
     * @param view
     */
    public static void showAnimatedView(View view) {
        if (PRE_HC && view != null)
            view.setPadding(0, 0, 0, 0);
    }

    /**
     * 在对话框中显示输入法
     *
     * @param dialog
     */
    public static void showSoftKeyboard(Dialog dialog) {
        dialog.getWindow().setSoftInputMode(4);
    }

    /**
     * 在View中显示输入法
     *
     * @param dialog
     */
    public static void showSoftKeyboard(View view) {
        ((InputMethodManager) BaseApplication.context().getSystemService(
                Context.INPUT_METHOD_SERVICE)).showSoftInput(view,
                InputMethodManager.SHOW_FORCED);
    }

    public static void toogleSoftKeyboard(View view) {
        ((InputMethodManager) BaseApplication.context().getSystemService(
                Context.INPUT_METHOD_SERVICE)).toggleSoftInput(0,
                InputMethodManager.HIDE_NOT_ALWAYS);
    }

    /**
     * 判断SD卡是否可用
     *
     * @return
     */
    public static boolean isSdcardReady() {
        return Environment.MEDIA_MOUNTED.equals(Environment
                .getExternalStorageState());
    }

    /**
     * 获取当前系统语言模式
     *
     * @return
     */
    public static String getCurCountryLan() {
        return BaseApplication.context().getResources().getConfiguration().locale
                .getLanguage()
                + "-"
                + BaseApplication.context().getResources().getConfiguration().locale
                .getCountry();
    }

    /**
     * 判断是否为中文
     *
     * @return
     */
    public static boolean isZhCN() {
        String lang = BaseApplication.context().getResources()
                .getConfiguration().locale.getCountry();
        if (lang.equalsIgnoreCase("CN")) {
            return true;
        }
        return false;
    }

    /**
     * 获取两个小数的百分比，小数点后取两位
     *
     * @param p1
     * @param p2
     * @return
     */
    public static String percent(double p1, double p2) {
        String str;
        double p3 = p1 / p2;
        NumberFormat nf = NumberFormat.getPercentInstance();
        nf.setMinimumFractionDigits(2);
        str = nf.format(p3);
        return str;
    }

    /**
     * 获取两个小数的百分比，小数点后取0位
     *
     * @param p1
     * @param p2
     * @return
     */
    public static String percent2(double p1, double p2) {
        String str;
        double p3 = p1 / p2;
        NumberFormat nf = NumberFormat.getPercentInstance();
        nf.setMinimumFractionDigits(0);
        str = nf.format(p3);
        return str;
    }

    /**
     * 设置Activity全屏
     *
     * @param activity
     */
    public static void setFullScreen(Activity activity) {
        WindowManager.LayoutParams params = activity.getWindow()
                .getAttributes();
        params.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
        activity.getWindow().setAttributes(params);
        activity.getWindow().addFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
    }

    /**
     * 取消Activity全屏
     *
     * @param activity
     */
    public static void cancelFullScreen(Activity activity) {
        WindowManager.LayoutParams params = activity.getWindow()
                .getAttributes();
        params.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
        activity.getWindow().setAttributes(params);
        activity.getWindow().clearFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
    }

    /**
     * 获取系统对包名的PackageInfo
     *
     * @param pckName
     * @return
     */
    public static PackageInfo getPackageInfo(String pckName) {
        try {
            return BaseApplication.context().getPackageManager()
                    .getPackageInfo(pckName, 0);
        } catch (NameNotFoundException e) {
            LogUtils.e("TDevice.clazz-->>>" + e.getMessage());
        }
        return null;
    }

    /**
     * 获取本程序的VersionCode
     *
     * @return
     */
    public static int getVersionCode() {
        int versionCode = 0;
        try {
            versionCode = BaseApplication.context().getPackageManager()
                    .getPackageInfo(BaseApplication.context().getPackageName(),
                            0).versionCode;
        } catch (NameNotFoundException ex) {
            versionCode = 0;
        }
        return versionCode;
    }

    /**
     * 获取包名对应的VersionCode
     *
     * @param packageName
     * @return
     */
    public static int getVersionCode(String packageName) {
        int versionCode = 0;
        try {
            versionCode = BaseApplication.context().getPackageManager()
                    .getPackageInfo(packageName, 0).versionCode;
        } catch (NameNotFoundException ex) {
            versionCode = 0;
        }
        return versionCode;
    }

    /**
     * 获取本程序的VersionName
     *
     * @return
     */
    public static String getVersionName() {
        String name;
        try {
            name = BaseApplication.context().getPackageManager()
                    .getPackageInfo(BaseApplication.context().getPackageName(), 0).versionName;
        } catch (NameNotFoundException ex) {
            name = "";
        }
        return name;
    }

    /**
     * 判断屏幕是否亮着
     *
     * @return
     */
    public static boolean isScreenOn() {
        PowerManager pm = (PowerManager) BaseApplication.context()
                .getSystemService(Context.POWER_SERVICE);
        return pm.isScreenOn();
    }

    /**
     * 安装File目录下的应用
     *
     * @param context
     * @param file
     */
    public static void installAPK(Context context, File file) {
        if (file == null || !file.exists())
            return;
        Intent intent = new Intent();
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(file),
                "application/vnd.android.package-archive");
        context.startActivity(intent);
    }

    /**
     * 得到File目录下应用包的Intent
     *
     * @param file
     * @return
     */
    public static Intent getInstallApkIntent(File file) {
        Intent intent = new Intent();
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(file),
                "application/vnd.android.package-archive");
        return intent;
    }

    /**
     * 打开系统拨打电话界面
     *
     * @param context
     * @param number
     */
    public static void openDial(Context context, String number) {
        Uri uri = Uri.parse("tel:" + number);
        Intent it = new Intent(Intent.ACTION_DIAL, uri);
        context.startActivity(it);
    }

    /**
     * 打开系统发短信界面
     *
     * @param context
     * @param smsBody
     * @param tel
     */
    public static void openSMS(Context context, String smsBody, String tel) {
        Uri uri = Uri.parse("smsto:" + tel);
        Intent it = new Intent(Intent.ACTION_SENDTO, uri);
        it.putExtra("sms_body", smsBody);
        context.startActivity(it);
    }

    /**
     * 打开系统拨打电话界面
     *
     * @param context
     */
    public static void openDail(Context context) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    /**
     * 打开系统发短信界面
     *
     * @param context
     */
    public static void openSendMsg(Context context) {
        Uri uri = Uri.parse("smsto:");
        Intent intent = new Intent(Intent.ACTION_SENDTO, uri);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    /**
     * 打开照像机
     *
     * @param context
     */
    public static void openCamera(Context context) {
        Intent intent = new Intent(); // 调用照相机
        intent.setAction("android.media.action.STILL_IMAGE_CAMERA");
        intent.setFlags(0x34c40000);
        context.startActivity(intent);
    }

    /**
     * 获取手机IMEI号
     *
     * @return
     */
    public static String getIMEI() {
        TelephonyManager tel = (TelephonyManager) BaseApplication.context()
                .getSystemService(Context.TELEPHONY_SERVICE);
        return tel.getDeviceId();
    }

    /**
     * 获取手机型号
     *
     * @return
     */
    public static String getPhoneType() {
        return Build.MODEL;
    }

    /**
     * 打开传入包名的应用
     *
     * @param context
     * @param packageName
     */
    public static void openApp(Context context, String packageName) {
        Intent mainIntent = BaseApplication.context().getPackageManager()
                .getLaunchIntentForPackage(packageName);
        if (mainIntent == null) {
            mainIntent = new Intent(packageName);
        } else {
            LogUtils.i("TDevice.clazz-->>>" + "Action:"
                    + mainIntent.getAction());
        }
        context.startActivity(mainIntent);
    }

    /**
     * 打开传入包名,Activity的应用
     *
     * @param context
     * @param packageName
     * @param activityName
     * @return
     */
    public static boolean openAppActivity(Context context, String packageName,
                                          String activityName) {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        ComponentName cn = new ComponentName(packageName, activityName);
        intent.setComponent(cn);
        try {
            context.startActivity(intent);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 判断Wifi是否打开
     *
     * @return
     */
    public static boolean isWifiOpen() {
        boolean isWifiConnect = false;
        ConnectivityManager cm = (ConnectivityManager) BaseApplication
                .context().getSystemService(Context.CONNECTIVITY_SERVICE);
        // check the networkInfos numbers
        NetworkInfo[] networkInfos = cm.getAllNetworkInfo();
        for (int i = 0; i < networkInfos.length; i++) {
            if (networkInfos[i].getState() == NetworkInfo.State.CONNECTED) {
                if (networkInfos[i].getType() == ConnectivityManager.TYPE_MOBILE) {
                    isWifiConnect = false;
                }
                if (networkInfos[i].getType() == ConnectivityManager.TYPE_WIFI) {
                    isWifiConnect = true;
                }
            }
        }
        return isWifiConnect;
    }

    /**
     * 卸载传入包名的应用
     *
     * @param context
     * @param packageName
     */
    public static void uninstallApk(Context context, String packageName) {
        if (isPackageExist(packageName)) {
            Uri packageURI = Uri.parse("package:" + packageName);
            Intent uninstallIntent = new Intent(Intent.ACTION_DELETE,
                    packageURI);
            context.startActivity(uninstallIntent);
        }
    }

    /**
     * 复制内容到粘贴板
     *
     * @param string
     */
    @SuppressLint("NewApi")
    @SuppressWarnings("deprecation")
    public static void copyTextToBoard(String string) {
        if (TextUtils.isEmpty(string))
            return;
        ClipboardManager clip = (ClipboardManager) BaseApplication.context()
                .getSystemService(Context.CLIPBOARD_SERVICE);
        clip.setText(string);
        AppContext.showToastLongInBottom(R.string.copy_success);
    }

    /**
     * 发送邮件
     *
     * @param context
     * @param subject 主题
     * @param content 内容
     * @param emails  邮件地址
     */
    public static void sendEmail(Context context, String subject,
                                 String content, String... emails) {
        try {
            Intent intent = new Intent(Intent.ACTION_SEND);
            // 模拟器
            // intent.setType("text/plain");
            intent.setType("message/rfc822"); // 真机
            intent.putExtra(Intent.EXTRA_EMAIL, emails);
            intent.putExtra(Intent.EXTRA_SUBJECT, subject);
            intent.putExtra(Intent.EXTRA_TEXT, content);
            context.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取状态栏的高度
     *
     * @return
     */
    public static int getStatuBarHeight() {
        Class<?> c;
        Object obj;
        Field field;
        int x, sBar = 38;// 默认为38，貌似大部分是这样的
        try {
            c = Class.forName("com.android.internal.R$dimen");
            obj = c.newInstance();
            field = c.getField("status_bar_height");
            x = Integer.parseInt(field.get(obj).toString());
            sBar = BaseApplication.context().getResources()
                    .getDimensionPixelSize(x);

        } catch (Exception e1) {
            e1.printStackTrace();
        }
        return sBar;
    }

    /**
     * 判断是否有状态栏
     *
     * @param activity
     * @return
     */
    public static boolean hasStatusBar(Activity activity) {
        WindowManager.LayoutParams attrs = activity.getWindow().getAttributes();
        if ((attrs.flags & WindowManager.LayoutParams.FLAG_FULLSCREEN) == WindowManager.LayoutParams.FLAG_FULLSCREEN) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * 调用系统安装了的应用分享
     *
     * @param context
     * @param title
     * @param url
     */
    public static void showSystemShareOption(Activity context,
                                             final String title, final String url) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, "分享：" + title);
        intent.putExtra(Intent.EXTRA_TEXT, title + " " + url);
        context.startActivity(Intent.createChooser(intent, "选择分享"));
    }

    /**
     * 获取当前网络类型
     *
     * @return 0：没有网络 1：WIFI网络 2：WAP网络 3：NET网络
     */
    public static int getNetworkType() {
        int netType = 0;
        ConnectivityManager connectivityManager = (ConnectivityManager) AppContext
                .getInstance().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo == null) {
            return netType;
        }
        int nType = networkInfo.getType();
        if (nType == ConnectivityManager.TYPE_MOBILE) {
            String extraInfo = networkInfo.getExtraInfo();
            if (!TextUtils.isEmpty(extraInfo)) {
                if (extraInfo.toLowerCase(Locale.getDefault()).equals("cmnet")) {
                    netType = NETTYPE_CMNET;
                } else {
                    netType = NETTYPE_CMWAP;
                }
            }
        } else if (nType == ConnectivityManager.TYPE_WIFI) {
            netType = NETTYPE_WIFI;
        }
        return netType;
    }

    // 将秒转化成小时分钟秒
    public static String formaSecond(int miss) {
        String hh = miss / 3600 > 9 ? miss / 3600 + "" : "0" + miss / 3600;
        String mm = (miss % 3600) / 60 > 9 ? (miss % 3600) / 60 + "" : "0"
                + (miss % 3600) / 60;
        String ss = (miss % 3600) % 60 > 9 ? (miss % 3600) % 60 + "" : "0"
                + (miss % 3600) % 60;
        return hh + ":" + mm + ":" + ss;
    }

    /**
     * 根据一个已知的电话号码，从通讯录中获取相对应的联系人姓名的代码
     */
    public static String getContactFromPhone(Context context, String phoneNum) {
        String contactName = "";
        ContentResolver cr = context.getContentResolver();
        Cursor pCur = cr.query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                ContactsContract.CommonDataKinds.Phone.NUMBER + " = ?",
                new String[]{phoneNum}, null);
        if (pCur.moveToFirst()) {
            contactName = pCur
                    .getString(pCur
                            .getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            pCur.close();
        }
        return contactName;
    }

    public static void insertContact2Phone(Context context, String nickName,
                                           String phoneNum) {
        /* 往 raw_contacts 中添加数据，并获取添加的id号 */
        Uri uri = Uri.parse("content://com.android.contacts/raw_contacts");
        ContentResolver resolver = context.getContentResolver();
        ContentValues values = new ContentValues();
        long contactId = ContentUris.parseId(resolver.insert(uri, values));
        uri = Uri.parse("content://com.android.contacts/data");
        values.put("raw_contact_id", contactId);
        values.put("mimetype", "vnd.android.cursor.item/name");
        values.put("data2", nickName);
        resolver.insert(uri, values);

        // 添加电话
        values.clear();
        values.put("raw_contact_id", contactId);
        values.put("mimetype", "vnd.android.cursor.item/phone_v2");
        values.put("data2", "2");
        values.put("data1", phoneNum);
        resolver.insert(uri, values);
    }

    public static boolean devieType(DeviceInfo infodevice, int i) {// 23判断设备是授权设备还是我的设备
        if (infodevice == null)
            return false;
        int x = 1 << i;
        if (infodevice.dwCapacity != 0) {
            if ((x & infodevice.dwCapacity) != 0) {
                return true;
            }
        }
        return false;
    }

    public static String getLoginMessage(int code, Context context) {
        switch (code) {
            case 0:// 登录超时
                return context.getString(R.string.overtime);
            case 10002:// 服务器连接失败
                return context.getString(R.string.server_connection_failed);
            case 10003:// 设备连接失败
                return context.getString(R.string.device_connection_failed);
            case 21001:// 服务器连接断开
                return context.getString(R.string.server_disconnected);
            case 20007:// 服务登陆失败
                return context.getString(R.string.server_login_failed);
            case 20002:// 无效的用户名
            case 40002:
                return context.getString(R.string.invalid_user_name);
            case 20003:// 密码错误
            case 40003:
                return context.getString(R.string.incorrect_pwd);
            case 30002:// 设备连接断开
                return context.getString(R.string.device_disconnected);
            case 20005:// 服务器繁忙
                return context.getString(R.string.server_busy);
            case 20006:// 数据库服务器出错
                return context.getString(R.string.database_server_error);
            default:// 其它默认系统错误
                return context.getString(R.string.Systemerror);
        }
    }
}
