package com.dongdong.app.ui.dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;

import com.dd121.community.R;
import com.ddclient.configuration.DongConfiguration;
import com.ddclient.dongsdk.DongSDKProxy;
import com.ddclient.dongsdk.PushInfo;
import com.dongdong.app.AppConfig;
import com.dongdong.app.AppContext;
import com.dongdong.app.AppManager;
import com.dongdong.app.base.BaseApplication;
import com.dongdong.app.fragment.HomePagerFragment;
import com.dongdong.app.util.LogUtils;
import com.igexin.sdk.PushManager;

public class TipDialogManager {

    public interface OnTipDialogButtonClick {
        void onPositiveButtonClick();

        void onNegativeButtonClick();
    }

    public static void showTipDialog(Context context, int titleId, int msgId) {
        final CommonDialog tipDialog = new CommonDialog(context);
        tipDialog.setTitle(titleId);
        tipDialog.setMessage(msgId);
        tipDialog.setPositiveButton(R.string.know, null);
        tipDialog.setCancelable(true);
        tipDialog.show();
    }

    public static void showTipDialog(Context context, String titleId,
                                     String msgId) {
        final CommonDialog tipDialog = new CommonDialog(context);
        tipDialog.setTitle(titleId);
        tipDialog.setMessage(msgId);
        tipDialog.setPositiveButton(R.string.know, null);
        tipDialog.setCancelable(true);
        tipDialog.show();
    }

    public static void showOtherLoginDialog(final Context context, String time) {
        final CommonDialog tipDialog = new CommonDialog(context);
        String tip = time
                + BaseApplication.context().getString(
                R.string.login_other_plach_warning);

        tipDialog.setTitle(BaseApplication.context()
                .getString(R.string.warning));
        tipDialog.setMessage(tip);
        tipDialog.setPositiveButton(R.string.exit, new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // 1.关闭推送
                PushManager.getInstance().turnOffPush(context);
                com.baidu.android.pushservice.PushManager
                        .stopWork(BaseApplication.context());
                boolean initedDongAccount = DongSDKProxy.isInitedDongAccount();
                // 2.清空SDK信息
                if (initedDongAccount) {
                    DongSDKProxy.loginOut();
                    DongConfiguration.clearAllData();
//                    HomePagerFragment.mIsFirstChooseDefaultDevice = true;
                    AppContext.mAppConfig.remove(
                            AppConfig.DONG_CONFIG_SHARE_PREF_NAME,
                            AppConfig.KEY_DEVICE_ID);
                    AppContext.mAppConfig.remove(
                            AppConfig.DONG_CONFIG_SHARE_PREF_NAME,
                            AppConfig.KEY_IS_LOGIN);
                    tipDialog.dismiss();
                    AppManager.getAppManager().finishNOTLMainActivity();

                }
                LogUtils.i("WarnDialog.clazz--->>>logout!!!!initedDongAccount:"
                        + initedDongAccount);
            }
        });
        tipDialog.setNegativeButton(R.string.relogin, new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                boolean initedDongAccount = DongSDKProxy.isInitedDongAccount();
                // 2.清空SDK信息
                if (initedDongAccount) {
                    DongSDKProxy
                            .requestSetPushInfo(PushInfo.PUSHTYPE_FORCE_ADD);
                    BaseApplication.showToastShortInBottom(R.string.loginSucc);
                }
                tipDialog.dismiss();
                LogUtils.i("WarnDialog.clazz--->>>relogin!!!!initedDongAccount:"
                        + initedDongAccount);
            }
        });
        tipDialog.setCancelable(false);
        tipDialog.show();
    }

    /**
     * 没有可用网络提示框
     *
     * @param context 上下文Activity
     */
    public static void showWithoutNetworDialog(final Context context,
                                               final OnTipDialogButtonClick callback) {
        final CommonDialog tipDialog = new CommonDialog(context);
        tipDialog.setTitle(R.string.tip);
        tipDialog.setMessage(R.string.no_network);
        tipDialog.setPositiveButton(R.string.network_settings,
                new OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (callback != null) {
                            callback.onNegativeButtonClick();
                        }
                        context.startActivity(new Intent(
                                android.provider.Settings.ACTION_SETTINGS));
                        tipDialog.dismiss();
                    }
                });
        tipDialog.setNegativeButton(R.string.cancel, new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (callback != null) {
                    callback.onNegativeButtonClick();
                }
                tipDialog.dismiss();
            }
        });
        tipDialog.show();
    }

    public static void showNormalTipDialog(final Context context,
                                           final OnTipDialogButtonClick callback, int titleResId,
                                           int msgResId, int positiveResId, int negativeResId) {
        final CommonDialog tipDialog = new CommonDialog(context);
        tipDialog.setTitle(titleResId);
        tipDialog.setMessage(msgResId);
        tipDialog.setPositiveButton(positiveResId, new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (callback != null) {
                    callback.onPositiveButtonClick();
                }
                tipDialog.dismiss();
            }
        });
        tipDialog.setNegativeButton(negativeResId, new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (callback != null) {
                    callback.onNegativeButtonClick();
                }
                tipDialog.dismiss();
            }
        });
        tipDialog.show();
    }

    public static void showNormalTipDialog(final Context context,
                                           final OnTipDialogButtonClick callback, String titleRes,
                                           String msgRes, String positiveRes, String negativeRes) {
        final CommonDialog tipDialog = new CommonDialog(context);
        tipDialog.setTitle(titleRes);
        tipDialog.setMessage(msgRes);
        tipDialog.setPositiveButton(positiveRes, new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (callback != null) {
                    callback.onPositiveButtonClick();
                }
                tipDialog.dismiss();
            }
        });
        tipDialog.setNegativeButton(negativeRes, new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (callback != null) {
                    callback.onNegativeButtonClick();
                }
                tipDialog.dismiss();
            }
        });
        tipDialog.show();
    }
}
