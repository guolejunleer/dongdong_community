package com.push.message;

import android.content.Context;
import android.text.TextUtils;

import com.ddclient.dongsdk.DongMessage;
import com.ddclient.dongsdk.DongSDKProxy;
import com.ddclient.dongsdk.PushMsgBean;
import com.dongdong.app.util.LogUtils;
import com.dongdong.app.util.UIHelper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

@Deprecated
public class PushMessageCallBack {

    private static long oldTime = 0;

    /**
     * 平台离线推送
     *
     * @param context     上下文
     * @param pushMsgBean 消息载休
     */
    public static void pushMessageReceiver(Context context, DongMessage pushMsgBean) {
        LogUtils.i("PushMessageCallBack.clazz-->> pushMsgBean:" + pushMsgBean);
    }

    /**
     * 自定义的消息格式
     *
     * @param context 上下文
     * @param message 自定义消息
     */
    public static void pushMessageReceiver(Context context, String message) {
        LogUtils.i("PushMessageCallBack.clazz--->>>user-defined or error message:" + message);
    }
}