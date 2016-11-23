package com.push.message;

import android.content.Context;

import com.ddclient.dongsdk.DongSDKProxy;
import com.ddclient.dongsdk.PushMsgBean;
import com.dongdong.app.util.LogUtils;
import com.dongdong.app.util.UIHelper;

public class PushMessageCallBack {

    private static long oldTime = 0;

    /**
     * 平台离线推送
     *
     * @param context     上下文
     * @param pushMsgBean 消息载休
     */
    public static void pushMessageReceiver(Context context,
                                           PushMsgBean pushMsgBean) {
        long newTime = System.currentTimeMillis();
        if (Math.abs(newTime - oldTime) < 5 * 1000) {
            return;
        }
        oldTime = newTime;
        String alarmTye = pushMsgBean.getMessage().substring(0, 2);
        String deviceID = pushMsgBean.getDeviceId();
        LogUtils.i("PushMessageCallBack.clazz-->> alarmTye:" + alarmTye
                + ",deviceID:" + deviceID);
        if (DongSDKProxy.isInitedDongAccount()) {
            LogUtils.i("PushMessageCallBack.clazz-->>isInitedDongAccount will showVideoViewActivity deviceID:"
                    + deviceID);
            UIHelper.showVideoViewActivity(context, false, deviceID);
        } else {
            LogUtils.i("PushMessageCallBack.clazz-->>not isInitedDongAccount and we will jump MainActivity deviceID:"
                    + deviceID);
            UIHelper.showMainActivity(context, deviceID);
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