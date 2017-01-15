package com.dongdong.app;

import java.util.Stack;

import android.app.Activity;
import android.content.Context;

import com.ddclient.dongsdk.DongSDK;
import com.ddclient.dongsdk.DongSDKProxy;
import com.dongdong.app.util.LogUtils;

/**
 * activity堆栈式管理
 *
 * @author leer(http://www.dd121.com)
 * @created 2016年06月15日 下午6:22:05
 */
public class AppManager {

    private static Stack<Activity> activityStack;
    private static AppManager instance;

    private AppManager() {
    }

    /**
     * 单一实例
     */
    public static AppManager getAppManager() {
        if (instance == null) {
            instance = new AppManager();
        }
        return instance;
    }

    /**
     * 添加Activity到堆栈
     */
    public void addActivity(Activity activity) {
        if (activityStack == null) {
            activityStack = new Stack<Activity>();
        }
        activityStack.add(activity);
    }

    /**
     * 获取当前Activity（堆栈中最后一个压入的）
     */
    public Activity currentActivity() {
        Activity activity = activityStack.lastElement();
        return activity;
    }

    /**
     * 结束当前Activity（堆栈中最后一个压入的）
     */
    public void finishActivity() {
        Activity activity = activityStack.lastElement();
        finishActivity(activity);
    }

    /**
     * 结束指定的Activity
     */
    public void finishActivity(Activity activity) {
        if (activity != null && !activity.isFinishing()) {
            activityStack.remove(activity);
            activity.finish();
            activity = null;
        }
    }

    /**
     * 结束指定类名的Activity
     */
    public void finishActivity(Class<?> cls) {
        for (Activity activity : activityStack) {
            if (activity.getClass().equals(cls)) {
                finishActivity(activity);
                break;
            }
        }
    }

    /**
     * 结束所有Activity
     */
    public void finishAllActivity() {
        for (int i = 0, size = activityStack.size(); i < size; i++) {
            if (null != activityStack.get(i)) {
                // finishActivity方法中的activity.isFinishing()方法会导致某些activity无法销毁
                // 貌似跳转的时候最后一个activity 是finishing状态，所以没有执行
                // 内部实现不是很清楚，但是实测结果如此，使用下面代码则没有问题
                // find by TopJohn
                // finishActivity(activityStack.get(i));

                activityStack.get(i).finish();
                // break;
            }
        }
        activityStack.clear();
    }

    /**
     * 结束MainActivity之外的所有Activity
     */
    public void finishNOTLMainActivity() {
        LogUtils.i("AppManager.clazz--->>>finishNOTLMainActivity begain --- activityStack:"
                + activityStack);
        for (int i = 0, size = activityStack.size(); i < size; i++) {
            if (activityStack.get(i).getClass().getName()
                    .equals("com.dongdong.app.MainActivity")) {
                continue;
            }
            if (null != activityStack.get(i)) {
                activityStack.get(i).finish();
            }
        }
        for (int i = 0, size = activityStack.size(); i < size; i++) {
            if (!activityStack.get(i).getClass().getName()
                    .equals("com.dongdong.app.MainActivity")) {
                activityStack.remove(activityStack.get(i));
                size--;
                i--;
            }
        }
        LogUtils.i("AppManager.clazz--->>>finishNOTLMainActivity end --- activityStack:"
                + activityStack);
    }

    /**
     * 获取指定的Activity
     */
    public static Activity getActivity(Class<?> cls) {
        if (activityStack != null)
            for (Activity activity : activityStack) {
                if (activity.getClass().equals(cls)) {
                    return activity;
                }
            }
        return null;
    }

    /**
     * 退出应用程序
     */
    public void appExit(Context context) {
        try {
            finishAllActivity();
            DongSDK.finishDongSDK();
            // 杀死该应用进程
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(0);
            LogUtils.i("AppManager.clazz--->>>appExit!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        } catch (Exception e) {
        }
    }
}
