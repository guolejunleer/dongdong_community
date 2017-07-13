package com.push.message;

import android.content.Context;
import android.text.TextUtils;

import com.dd121.community.R;
import com.ddclient.configuration.DongConfiguration;
import com.ddclient.dongsdk.PushMsgBean;
import com.dongdong.app.base.BaseApplication;
import com.dongdong.app.util.UIHelper;
import com.gViewerX.util.LogUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import static com.dongdong.app.util.PhoneUtils.isTelephonyCalling;

/**
 * 必须拷贝这个类到应用中去，此类用于离线推送处理的中转站,已过时
 */
public class ProcessPushMsgProxy {

    private final static ThreadLocal<SimpleDateFormat> mDateFormat = new ThreadLocal<SimpleDateFormat>() {
        @Override
        protected SimpleDateFormat initialValue() {
            // yyyy-MM-dd HH:mm:ss
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        }
    };

    private final static ArrayList<String> mpushTimeList = new ArrayList<>();

    /**
     * 处理离线推送
     *
     * @param context     上下文
     * @param pushMsgBean 消息载体
     */
    public static void processPushMsg(Context context, PushMsgBean pushMsgBean) {
        String deviceID = pushMsgBean.getDeviceId();
        String pushTime = pushMsgBean.getPushTime();

        LogUtils.i("ProcessPushMsgProxy.clazz--->>>pushMessageReceiver PushMsgBean:"
                + pushMsgBean);
        //0.过滤时间相同的推送，因为这有百度、个推二种，有可能时间是相同的，我们只取一种
        if (mpushTimeList.contains(pushTime)) {//0.0如果上次有时间相同的，那么返回
            return;
        } else {//0.1如果没有，添加进集合
            mpushTimeList.add(pushTime);
        }

        //1.这里比较当前时间和离线推送消息的时候是否超过1小时
        Date date;
        if (TextUtils.isEmpty(pushTime)) {
            date = new Date();
        } else {
            try {
                date = mDateFormat.get().parse(pushTime);
            } catch (ParseException e) {
                e.printStackTrace();
                date = new Date();
            }
        }
        long delSecond = (new Date(System.currentTimeMillis()).getTime() - date.getTime()) / 1000;
        LogUtils.i("ProcessPushMsgProxy.clazz-->delSecond " + delSecond);
        int pushState = pushMsgBean.getPushState();
        if (pushState == 8) {
//            BaseApplication.showToastShortInCenter(R.string.equipment_call);
        } else if (pushState == 11) {
            BaseApplication.showToastShortInCenter(R.string.call_answered);
            return;
        } else if (pushState == 12) {
            BaseApplication.showToastShortInCenter(R.string.call_over);
            return;
        }
        if (delSecond > 3600) {//2.如果离线推送大于1小时，那么我们就在首页提示用户多少分钟前有人呼叫过
            UIHelper.showMainActivityWithPushTime(context, deviceID, pushTime);
        } else {//3.如果离线推送在规定时间内，那么跳转到对应界面
            if (isTelephonyCalling(BaseApplication.context())){//正在通话中或正在拨打电话不进视频界面
                BaseApplication.showToastShortInBottom(R.string.video_stop_phone_comming);
                return;
            }
            if (DongConfiguration.mUserInfo != null) {//3.1在线推送进入监视界面
                LogUtils.i("ProcessPushMsgProxy.clazz-->>is login will" +
                        "showVideoViewActivity push time is 0 and deviceID:" + deviceID);
                UIHelper.showVideoViewActivity(context, false, deviceID);
            } else {//3.2离线推送先进入首页，再进入监视界面
                LogUtils.i("ProcessPushMsgProxy.clazz-->>not login and we will jump " +
                        "MainActivity push time is 0 and deviceID:" + deviceID);
                UIHelper.showMainActivity(context, deviceID);
            }
        }
    }

    /**
     * 自定义的消息格式
     *
     * @param context 上下文对象
     * @param msg     自定义推送信息
     */
    public static void processPushMsg(Context context, String msg) {
        LogUtils.i("ProcessPushMsgProxy.clazz----------->>>message define message:" + msg);
    }
}
