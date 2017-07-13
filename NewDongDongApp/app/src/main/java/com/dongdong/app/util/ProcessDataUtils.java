package com.dongdong.app.util;

import com.dd121.community.R;
import com.dongdong.app.base.BaseApplication;

public class ProcessDataUtils {
    //开门记录
    private static final int APP_OPEN_DOOR = 1;//手机开门
    private static final int CARD_OPEN_DOOR = 2;//刷卡开门
    private static final int WIFI_OPEN_DOOR = 3;//wifi开门
    private static final int TEMPORARY_PASSWORD_OPEN_DOOR = 4;//临时密码开门
    private static final int HOUSEHOLD_PASSWORD_OPEN_DOOR = 5;//住户密码开门
    private static final int SELECT_CARD_OPEN_DOOR = 6; //平台查询卡号开门
    private static final int BLUE_TOOTH_OPEN_DOOR = 7;//蓝牙开门
    private static final int PHONE_OPEN_DOOR = 10;//电话呼叫住户开门

    //访客留影
    private static final int CLOUD_CALL_TIME_OUT = 21;//呼叫超时，未接听
    private static final int APP_ANSWER = 22;//App接听
    private static final int PHONE_ANSWER = 23;//电话接听
    private static final int APP_UNLOCK = 1;//App开锁
    private static final int PHONE_UNLOCK = 10;//电话开锁
    private static final int CARD_UNLOCK = 2;//刷卡开锁
    private static final int PASSWORD_UNLOCK = 5;//密码开锁

    /**
     * 判断开门类型
     *
     * @param type 类型标识
     * @return typename 开门类型
     */
    public static String openDoorType(int type) {
        int nameResId;
        switch (type) {
            case APP_OPEN_DOOR:
                nameResId = R.string.App;
                break;
            case CARD_OPEN_DOOR:
                nameResId = R.string.accesscards;
                break;
            case WIFI_OPEN_DOOR:
                nameResId = R.string.WIFI;
                break;
            case TEMPORARY_PASSWORD_OPEN_DOOR:
                nameResId = R.string.TemporaryPassword;
                break;
            case HOUSEHOLD_PASSWORD_OPEN_DOOR:
                nameResId = R.string.Householdpassword;
                break;
            case SELECT_CARD_OPEN_DOOR:
                nameResId = R.string.accesscards;
                break;
            case BLUE_TOOTH_OPEN_DOOR:
                nameResId = R.string.bluetooth;
                break;
            case PHONE_OPEN_DOOR:
                nameResId = R.string.phonecall;
                break;
            default:
                nameResId = R.string.unKnow;
        }
        return BaseApplication.context().getString(nameResId);
    }

    /**
     * 判断访客留影类型
     *
     * @param type 类型标识
     * @return typename 留影类型
     */
    public static String visitorPhotoType(int type) {
        int nameResId;
        switch (type) {
            case CLOUD_CALL_TIME_OUT:
                nameResId = R.string.cloud_call_time_out;
                break;
            case APP_ANSWER:
                nameResId = R.string.app_answer;
                break;
            case PHONE_ANSWER:
                nameResId = R.string.phone_answer;
                break;
            case APP_UNLOCK:
                nameResId = R.string.app_unlock;
                break;
            case PHONE_UNLOCK:
                nameResId = R.string.phone_unlock;
                break;
            case CARD_UNLOCK:
                nameResId = R.string.card_unlock;
                break;
            case PASSWORD_UNLOCK:
                nameResId = R.string.password_unlock;
                break;
            default:
                nameResId = R.string.unKnow;
        }
        return BaseApplication.context().getString(nameResId);
    }

}
