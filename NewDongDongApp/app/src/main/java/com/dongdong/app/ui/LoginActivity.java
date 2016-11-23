package com.dongdong.app.ui;

import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.dd121.louyu.R;
import com.ddclient.MobileClientLib.InfoUser;
import com.ddclient.configuration.DongConfiguration;
import com.ddclient.dongsdk.AbstractDongSDKProxy.DongAccountCallbackImp;
import com.ddclient.dongsdk.DongSDK;
import com.ddclient.dongsdk.DongSDKProxy;
import com.ddclient.dongsdk.PushInfo;
import com.dongdong.app.AppConfig;
import com.dongdong.app.AppContext;
import com.dongdong.app.base.BaseActivity;
import com.dongdong.app.base.BaseApplication;
import com.dongdong.app.ui.dialog.CommonDialog;
import com.dongdong.app.ui.dialog.TipDialogManager;
import com.dongdong.app.util.LogUtils;
import com.dongdong.app.util.TDevice;
import com.dongdong.app.widget.TitleBar;
import com.dongdong.app.widget.TitleBar.OnTitleBarClickListener;
import com.igexin.sdk.PushManager;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class LoginActivity extends BaseActivity implements
        OnTitleBarClickListener, OnClickListener {

    private EditText mEtUname, mEtPwd;
    private TextView mTvForgetPsw, mTvRegeist, mTvLocalDevice;
    private ImageView mIvLogin;
    private CharSequence mEtUserName, mEtUserPwd;
    private TitleBar mtTitleBar;
    private CommonDialog mDialog;

    private Timer mTimer;// 定时器
    private int mLoginCount;

    private LoginActivityDongAccountProxy mDongAccountProxy = new LoginActivityDongAccountProxy();

    @Override
    protected int getLayoutId() {
        return R.layout.activity_login;
    }

    @Override
    public void initView() {
        mEtUname = (EditText) findViewById(R.id.et_name);
        mEtPwd = (EditText) findViewById(R.id.et_pwd);
        mTvForgetPsw = (TextView) findViewById(R.id.tv_forget_pwd);
        mTvRegeist = (TextView) findViewById(R.id.tv_regeist);
        mTvLocalDevice = (TextView) findViewById(R.id.tv_local);
        mIvLogin = (ImageView) findViewById(R.id.iv_login);
    }

    @Override
    public void initData() {
        mtTitleBar = (TitleBar) findViewById(R.id.tb_title);
        mtTitleBar.setTitleBarContent(getString(R.string.login));
        mtTitleBar.setAddArrowShowing(false);

        mtTitleBar.setOnTitleBarClickListener(this);
        mTvForgetPsw.setOnClickListener(this);
        mTvRegeist.setOnClickListener(this);
        mTvLocalDevice.setOnClickListener(this);
        mIvLogin.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        String name = (String) AppContext.mAppConfig.getConfigValue(
                AppConfig.DONG_CONFIG_SHARE_PREF_NAME, AppConfig.KEY_USER_NAME,
                "");
        String pwd = (String) AppContext.mAppConfig.getConfigValue(
                AppConfig.DONG_CONFIG_SHARE_PREF_NAME, AppConfig.KEY_USER_PWD,
                "");
        mEtUname.setText(name);
        mEtPwd.setText(pwd);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        DongSDKProxy.unRegisterAccountCallback();
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
    }

    @Override
    public void onBackClick() {
        finish();
    }

    @Override
    public void onTitleClick() {
    }

    @Override
    public void onAddClick() {
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.tv_forget_pwd:
                startActivity(new Intent(this, ForgetPwdActivity.class));
                break;
            case R.id.tv_regeist:
                startActivity(new Intent(this, RegeistActivity.class));
                break;
            case R.id.tv_local:
                startActivity(new Intent(this, LocalDeviceActivity.class));
                break;
            case R.id.iv_login:
                if (TDevice.getNetworkType() == 0) {
                    TipDialogManager.showWithoutNetworDialog(this, null);
                    return;
                }
                mEtUserName = mEtUname.getText();
                mEtUserPwd = mEtPwd.getText();
                if (TextUtils.isEmpty(mEtUserName)) {
                    BaseApplication
                            .showToastShortInTop(R.string.user_name_can_not_empty);
                } else if (TextUtils.isEmpty(mEtUserPwd)) {
                    BaseApplication
                            .showToastShortInTop(R.string.user_pwd_can_not_empty);
                } else {
                    mDialog = new CommonDialog(this);
                    View view = LayoutInflater.from(this).inflate(
                            R.layout.loading_dialog, null);
                    TextView tipTextView = (TextView) view
                            .findViewById(R.id.tv_tip);
                    tipTextView.setText(getString(R.string.login_wait));
                    mDialog.setContent(view);
                    mDialog.show();
                    DongSDKProxy.initDongAccount(mDongAccountProxy);
                    // 后续可能要加入取消回调引用接口
                    DongSDKProxy.login(mEtUserName.toString(),
                            mEtUserPwd.toString());
                    mTimer = new Timer();
                    mTimer.schedule(new MyTimerTask(), new Date(), 1000 * 1);
                }
                break;
            default:
                break;

        }
    }

    private class LoginActivityDongAccountProxy extends DongAccountCallbackImp {

        @Override
        public int OnAuthenticate(InfoUser tInfo) {
            DongConfiguration.mUserInfo = tInfo;
            PushManager.getInstance().turnOnPush(LoginActivity.this);
            DongSDKProxy.requestSetPushInfo(PushInfo.PUSHTYPE_FORCE_ADD);
            mDialog.dismiss();
            LogUtils.i("LoginActivity.clazz--->>>OnAuthenticate........tInfo:"
                    + tInfo + ",mEtUserName:" + mEtUserName + ",mEtUserPwd:"
                    + mEtUserPwd);
            // 保存用户登录信息
            AppContext.mAppConfig.setConfigValue(
                    AppConfig.DONG_CONFIG_SHARE_PREF_NAME,
                    AppConfig.KEY_USER_NAME, mEtUserName.toString());
            AppContext.mAppConfig.setConfigValue(
                    AppConfig.DONG_CONFIG_SHARE_PREF_NAME,
                    AppConfig.KEY_USER_PWD, mEtUserPwd.toString());
            AppContext.mAppConfig.setConfigValue(
                    AppConfig.DONG_CONFIG_SHARE_PREF_NAME,
                    AppConfig.KEY_IS_LOGIN, true);
            DongSDKProxy.requestGetDeviceListFromPlatform();
            LoginActivity.this.finish();
            return 0;
        }

        @Override
        public int OnUserError(int nErrNo) {
            LogUtils.i("LoginActivity.clazz--->>>OnUserError........nErrNo:"
                    + nErrNo);
            if (mDialog.isShowing())
                mDialog.dismiss();
            mDialog.setTitle(R.string.tip);
            mDialog.setMessage(TDevice.getLoginMessage(nErrNo,
                    LoginActivity.this)
                    + "("
                    + LoginActivity.this.getString(R.string.errorCode)
                    + nErrNo
                    + ")");
            mDialog.setPositiveButton(R.string.ok, null);
            mDialog.show();
            return 0;
        }
    }

    private class MyTimerTask extends TimerTask implements TipDialogManager.OnTipDialogButtonClick {

        boolean shouldStop;

        @Override
        public void run() {
            // TODO Auto-generated method stub
            mLoginCount++;
            if (mLoginCount > 15 && !shouldStop) {
                if (mDialog.isShowing()) mDialog.dismiss();
                shouldStop = true;
                TipDialogManager.showNormalTipDialog(LoginActivity.this, this,
                        R.string.tip, R.string.login_overtime, R.string.suc, R.string.cancel);
            }
        }

        @Override
        public void onPositiveButtonClick() {
            DongSDK.reInitDongSDK();
        }

        @Override
        public void onNegativeButtonClick() {
            DongSDK.reInitDongSDK();
        }
    }

}
