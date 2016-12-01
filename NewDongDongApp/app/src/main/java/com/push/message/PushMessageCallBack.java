package com.push.message;

import android.content.Context;
import android.text.TextUtils;

import com.ddclient.dongsdk.DongSDKProxy;
import com.ddclient.dongsdk.PushMsgBean;
import com.dongdong.app.util.LogUtils;
import com.dongdong.app.util.UIHelper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class PushMessageCallBack {

    private static long oldTime = 0;

    /**
     * 平台离线推送
     *
     * @param context     上下文
     * @param pushMsgBean 消息载休
     */
    public static void pushMessageReceiver(Context context, PushMsgBean pushMsgBean) {
        long newTime = System.currentTimeMillis();
        if (Math.abs(newTime - oldTime) < 5 * 1000) {
            return;
        }
        oldTime = newTime;
        String deviceID = pushMsgBean.getDeviceId();
        LogUtils.i("PushMessageCallBack.clazz-->> pushMsgBean:" + pushMsgBean);
        if (DongSDKProxy.isInitedDongAccount()) {
            LogUtils.i("PushMessageCallBack.clazz-->>is login will showVideoViewActivity deviceID:"
                    + deviceID);
            UIHelper.showVideoViewActivity(context, false, deviceID);
        } else {//如果离线推送大于3分钟，那么我们就在首页提示用户多少分钟前有人呼叫过
            String pushTime = pushMsgBean.getPushTime();
            LogUtils.i("PushMessageCallBack.clazz-->>not login and we will jump MainActivity deviceID:"
                    + deviceID + ",pushTime:" + pushTime);
            UIHelper.showMainActivityWithPushTime(context, deviceID, pushTime);
        }
        // 1. 应用卸载后，再次安装还会收到推送，这个问题需要解决!!!

        // 2.推送界面会有多个，需要过滤
    }

    /**
     * 自定义的消息格式
     *
     * @param context 上下文
     * @param message 自定义消息
     */
    public static void pushMessageReceiver(Context context, String message) {
        LogUtils.i("PushMessageCallBack.clazz--->>>user-defined or error message:"
                + message);
    }
}