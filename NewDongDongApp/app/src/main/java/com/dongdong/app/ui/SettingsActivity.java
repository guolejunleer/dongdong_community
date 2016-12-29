package com.dongdong.app.ui;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.dd121.community.R;
import com.ddclient.configuration.DongConfiguration;
import com.ddclient.dongsdk.DongSDKProxy;
import com.dongdong.app.AppConfig;
import com.dongdong.app.AppContext;
import com.dongdong.app.AppManager;
import com.dongdong.app.base.BaseActivity;
import com.dongdong.app.base.BaseApplication;
import com.dongdong.app.ui.dialog.CommonDialog;
import com.dongdong.app.util.LogUtils;
import com.dongdong.app.widget.TitleBar;
import com.dongdong.app.widget.TitleBar.OnTitleBarClickListener;
import com.igexin.sdk.PushManager;

public class SettingsActivity extends BaseActivity implements
        OnTitleBarClickListener, View.OnClickListener {

    private Button mBtnLoginOut;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_settings;
    }

    @Override
    public void initView() {
        RelativeLayout rlPushService = (RelativeLayout) findViewById(R.id.rl_push_service);
        RelativeLayout rlNightMode = (RelativeLayout) findViewById(R.id.rl_nightmode);
        RelativeLayout rlNetwork = (RelativeLayout) findViewById(R.id.rl_openvideo_in_data_network);
        RelativeLayout rlAttendedMode = (RelativeLayout) findViewById(R.id.rl_attended_mode);
        RelativeLayout rlCleanSession = (RelativeLayout) findViewById(R.id.rl_clean_session);
        RelativeLayout rlNewVersion = (RelativeLayout) findViewById(R.id.rl_about);

        TitleBar titleBar = (TitleBar) findViewById(R.id.tb_title);
        mBtnLoginOut = (Button) findViewById(R.id.btn_loginout);

        titleBar.setTitleBarContent(getString(R.string.settings));
        titleBar.setOnTitleBarClickListener(this);
        titleBar.setAddArrowShowing(false);

        rlCleanSession.setOnClickListener(this);
        rlNewVersion.setOnClickListener(this);
        rlPushService.setOnClickListener(this);
        rlNightMode.setOnClickListener(this);
        rlNetwork.setOnClickListener(this);
        rlAttendedMode.setOnClickListener(this);
        mBtnLoginOut.setOnClickListener(this);
    }

    @Override
    public void initData() {
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (DongConfiguration.mUserInfo == null) {
            mBtnLoginOut.setVisibility(View.INVISIBLE);
        } else {
            mBtnLoginOut.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onBackClick() {
        this.finish();
    }

    @Override
    public void onTitleClick() {
    }

    @Override
    public void onAddClick() {
    }

    @Override
    public void onFinishClick() {
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_push_service:// 推送通知
                BaseApplication.showToastShortInCenter(R.string.building);
                break;
            case R.id.rl_nightmode:// 夜间模式
                BaseApplication.showToastShortInCenter(R.string.building);
                break;
            case R.id.rl_openvideo_in_data_network:// 连接方式
                BaseApplication.showToastShortInCenter(R.string.building);
                break;
            case R.id.rl_attended_mode:// 数据网络下开启视频
                BaseApplication.showToastShortInCenter(R.string.building);
                break;
            case R.id.rl_clean_session:// 清除缓存
                BaseApplication.showToastShortInCenter(R.string.building);
                break;
            case R.id.rl_about:// 关于软件
                startActivity(new Intent(SettingsActivity.this, AboutActivity.class));
                break;
            case R.id.btn_loginout:
                final CommonDialog commonDialog = new CommonDialog(SettingsActivity.this);
                commonDialog.setMessage(R.string.isexit);
                commonDialog.setPositiveButton(R.string.exit,
                        new OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // 1.关闭推送
                                PushManager.getInstance().turnOffPush(
                                        SettingsActivity.this);
                                com.baidu.android.pushservice.PushManager
                                        .stopWork(BaseApplication.context());
                                boolean initDongAccount = DongConfiguration.mUserInfo != null;
                                // 2.清空SDK信息
                                if (initDongAccount) {
                                    DongSDKProxy.loginOut();
                                    DongConfiguration.clearAllData();
                                    AppContext.mAppConfig.remove(
                                            AppConfig.DONG_CONFIG_SHARE_PREF_NAME,
                                            AppConfig.KEY_DEVICE_ID);
                                    AppContext.mAppConfig.remove(
                                            AppConfig.DONG_CONFIG_SHARE_PREF_NAME,
                                            AppConfig.KEY_IS_LOGIN);
                                    AppManager.getAppManager().finishNOTLMainActivity();
                                    commonDialog.dismiss();
                                    LogUtils.i("SettingsActivity.clazz--->>>logout!!!!");
                                }
                            }
                        });
                commonDialog.setNegativeButton(R.string.cancel, null);
                commonDialog.setCancelable(true);
                commonDialog.show();
                break;
            default:
                break;
        }
    }
}
