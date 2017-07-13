package com.dongdong.app.ui;

import java.util.Random;

import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.dd121.community.R;
import com.ddclient.configuration.DongConfiguration;
import com.ddclient.dongsdk.AbstractDongCallbackProxy;
import com.ddclient.dongsdk.DongSDKProxy;
import com.dongdong.app.AppConfig;
import com.dongdong.app.AppContext;
import com.dongdong.app.base.BaseActivity;
import com.dongdong.app.base.BaseApplication;
import com.dongdong.app.bean.UserBean;
import com.dongdong.app.db.UserOpe;
import com.dongdong.app.ui.dialog.CommonDialog;
import com.dongdong.app.ui.dialog.TipDialogManager;
import com.dongdong.app.util.CyptoUtils;
import com.dongdong.app.util.LogUtils;
import com.dongdong.app.util.TDevice;
import com.dongdong.app.widget.TitleBar;
import com.dongdong.app.widget.TitleBar.OnTitleBarClickListener;

import static com.dongdong.app.fragment.MyPagerFragment.INTENT_IS_FORGET_KEY;
import static com.dongdong.app.util.PhoneUtils.isMobile;

public class ForgetPwdActivity extends BaseActivity implements OnTitleBarClickListener,
        OnClickListener {

    private EditText mEtPhone, mEtSms, mEtOldPwd, mEtPwd, mEtAgainPwd;
    private Button mbtSms, mbtOK;
    private String randomCode;
    private TitleBar mTitleBar;
    private CommonDialog mDialog;
    private TimeCount mTime;
    boolean mIsForget = false;

    private ForgetPwdActivityDongRegisterProxy mDongRegisterProxy
            = new ForgetPwdActivityDongRegisterProxy();

    @Override
    protected int getLayoutId() {
        return R.layout.activity_forget_psw;
    }

    @Override
    public void initView() {
        mEtPhone = (EditText) findViewById(R.id.et_phone);
        mEtSms = (EditText) findViewById(R.id.et_smush);
        mEtOldPwd = (EditText) findViewById(R.id.et_old_pwd);
        mEtPwd = (EditText) findViewById(R.id.et_pwd);
        mEtAgainPwd = (EditText) findViewById(R.id.et_again_pwd);
        mbtSms = (Button) findViewById(R.id.bt_smush);
        mbtOK = (Button) findViewById(R.id.bt_ok);
        mTitleBar = (TitleBar) findViewById(R.id.tb_title);

        mIsForget = (boolean) getIntent().getExtras().get(INTENT_IS_FORGET_KEY);
        if (mIsForget) {
            findViewById(R.id.h1).setVisibility(View.VISIBLE);
            mEtPhone.setVisibility(View.VISIBLE);
            findViewById(R.id.ll_smush).setVisibility(View.VISIBLE);
            findViewById(R.id.h2).setVisibility(View.VISIBLE);
        } else {
            mEtOldPwd.setVisibility(View.VISIBLE);
            findViewById(R.id.h3).setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void initData() {
        boolean initDongRegister = DongSDKProxy.initCompleteDongRegister();
        DongSDKProxy.intDongRegister(mDongRegisterProxy);
        LogUtils.i("ForgetPwdActivity.clazz--->>>initData........initDongRegister:" + initDongRegister);
        mDialog = new CommonDialog(this);
        mTime = new TimeCount(60000, 1000);
        mTitleBar.setTitleBarContent(getString(R.string.reset_pwd));
        mTitleBar.setAddArrowShowing(false);
        mTitleBar.setOnTitleBarClickListener(this);
        mbtSms.setOnClickListener(this);
        mbtOK.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        DongSDKProxy.registerDongRegisterCallback(mDongRegisterProxy);
    }

    @Override
    protected void onPause() {
        super.onPause();
        DongSDKProxy.unRegisterDongRegisterCallback(mDongRegisterProxy);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private class TimeCount extends CountDownTimer {
        TimeCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onFinish() {
            mbtSms.setText(getString(R.string.get_verification_code));
            mbtSms.setClickable(true);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            mbtSms.setClickable(false);
            mbtSms.setText(String.format("%s", millisUntilFinished / 1000 + "s"));
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
    public void onFinishClick() {
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        String phoneNumber = mEtPhone.getText().toString().trim();
        if (TDevice.getNetworkType() == 0) {
            TipDialogManager.showWithoutNetworDialog(this, null);
            return;
        }
        if (mIsForget) {
            if (TextUtils.isEmpty(phoneNumber)) {
                BaseApplication.showToastShortInTop(R.string.user_name_can_not_empty);
                return;
            }
            if (phoneNumber.length() != 11 || !isMobile(phoneNumber)) {
                BaseApplication.showToastShortInTop(R.string.un_know_user_name);
                return;
            }
        }
        switch (id) {
            case R.id.bt_smush:
                View view = LayoutInflater.from(ForgetPwdActivity.this).
                        inflate(R.layout.loading_dialog, null);
                TextView tipTextView = (TextView) view.findViewById(R.id.tv_tip);
                tipTextView.setText(getString(R.string.get_verification_coding));
                mDialog.setContent(view);
                mDialog.show();
                randomCode = (new Random().nextInt(999999) + 100000) + "";
                DongConfiguration.mPhoneNumber = phoneNumber;
                DongSDKProxy.requestQueryUser(phoneNumber);
                break;
            case R.id.bt_ok:
                if (mIsForget) {//忘记密码进来(不记得原密码)
                    if (TextUtils.isEmpty(mEtSms.getText().toString().trim())) {
                        BaseApplication.showToastShortInTop(R.string.verification_code_can_not_empty);
                    } else if (!mEtSms.getText().toString().equals(randomCode)) {
                        BaseApplication.showToastShortInTop(R.string.verification_code_mistake);
                    } else if (TextUtils.isEmpty(mEtPwd.getText().toString().trim())) {
                        BaseApplication.showToastShortInTop(R.string.user_pwd_can_not_empty);
                    } else {
                        commonJude();
                    }
                } else {//重置密码进来（记得原密码）
                    if (TextUtils.isEmpty(mEtOldPwd.getText().toString().trim())) {
                        BaseApplication.showToastShortInTop(R.string.old_pwd_can_not_empty);
                    } else if (!mEtOldPwd.getText().toString().trim().equals(AppContext.mAppConfig.
                            getConfigValue(AppConfig.DONG_CONFIG_SHARE_PREF_NAME, AppConfig.KEY_USER_PWD, ""))) {
                        BaseApplication.showToastShortInTop(R.string.old_pwd_mistake);
                    } else if (TextUtils.isEmpty(mEtPwd.getText().toString().trim())) {
                        BaseApplication.showToastShortInTop(R.string.user_pwd_can_not_empty);
                    } else if (mEtOldPwd.getText().toString().trim().equals(mEtPwd.getText().toString().trim())) {
                        BaseApplication.showToastShortInTop(R.string.old_new_same);
                    } else {
                        commonJude();
                    }
                }
                break;
            default:
                break;
        }
    }

    /**
     * 公共的判断新密码功能
     */
    private void commonJude() {
        if (TextUtils.isEmpty(mEtAgainPwd.getText().toString().trim())) {
            BaseApplication.showToastShortInTop(R.string.confrim_pwd_can_not_empty);
        } else if (!mEtAgainPwd.getText().toString().equals(mEtPwd.getText().toString())) {
            BaseApplication.showToastShortInTop(R.string.pwd_not_same);
        } else {
            if (mIsForget) {
                DongSDKProxy.requestSetSecret(mEtPwd.getText().toString().trim(),
                        DongConfiguration.mPhoneNumber);
            } else {
                LogUtils.i("ForgetPwdActivity.clazz->commonJude()->username:" +
                        DongConfiguration.mUserInfo.userName);
                DongSDKProxy.requestSetSecret(mEtPwd.getText().toString().trim(),
                        DongConfiguration.mUserInfo.userName);
            }
        }
    }

    private class ForgetPwdActivityDongRegisterProxy extends
            AbstractDongCallbackProxy.DongRegisterCallbackImp {
        @Override
        public int onQueryUser(int nReason) {
            if (nReason == 0) {// 未注册过
                BaseApplication.showToastShortInTop(R.string.phone_not_registered);
                mDialog.dismiss();
            } else {
                // register.smsAuth(randomCode, AppContext.phoneNumber);//
                // 用来发短信，接收验证码
                DongSDKProxy.requestSmsAuth(randomCode, DongConfiguration.mPhoneNumber);
            }
            LogUtils.i("ForgetPwdActivity.clazz--->>>OnQueryUser........nReason:" + nReason);
            return 0;
        }

        @Override
        public int onSmsAuth(int nReason) {
            if (nReason == 0) {
                if (mDialog != null) {
                    mDialog.dismiss();
                }
                mTime.start();
            } else {
                BaseApplication.showToastShortInTop(R.string.verifaction_failed);
                mDialog.dismiss();
            }
            LogUtils.i("ForgetPwdActivity.clazz--->>>OnSmsAuth........nReason:" + nReason);
            return 0;
        }

        @Override
        public int onSetSecret(int arg0) {
            if (mDialog.isShowing())
                mDialog.dismiss();
            if (arg0 == 0) {
                BaseApplication.showToastShortInBottom(R.string.update_pwd_succ);
                if(!mIsForget){
                    //删除本地数据库对应用户数据
                    LogUtils.i("ForgetPwdActivity.clazz--->>>onSetSecret->username:" +
                            DongConfiguration.mUserInfo.userName);
                    UserBean userBean = UserOpe.queryDataByUserName(BaseApplication.context(),
                            CyptoUtils.encode(AppConfig.DES_KEY, DongConfiguration.mUserInfo.userName));
                    if (userBean != null) {
                        LogUtils.i("ForgetPwdActivity.clazz--->>>onSetSecret->updateUser");
                        userBean.setPassWord("");
                        UserOpe.updateDataByUserBean(BaseApplication.context(), userBean);
                        LogUtils.i("ForgetPwdActivity.clazz--->>>onSetSecret->userBean:" + userBean.toString());
                    }
                    //更新本地的缓存(清除本地密码)
                    AppContext.mAppConfig.setConfigValue(
                            AppConfig.DONG_CONFIG_SHARE_PREF_NAME,
                            AppConfig.KEY_USER_PWD, "");
                }
                ForgetPwdActivity.this.finish();
            } else {
                BaseApplication.showToastShortInTop(R.string.update_pwd_failed);
            }
            return 0;
        }

        @Override
        public int onRegisterError(int nErrNo) {
            if (mDialog.isShowing())
                mDialog.dismiss();
            TipDialogManager.showTipDialog(ForgetPwdActivity.this,
                    BaseApplication.context().getString(R.string.tip),
                    BaseApplication.context().getString(R.string.register_error) + nErrNo);
            return 0;
        }
    }
}
