package com.dongdong.app.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.dd121.community.R;
import com.ddclient.dongsdk.AbstractDongCallbackProxy;
import com.ddclient.jnisdk.InfoUser;
import com.ddclient.configuration.DongConfiguration;
import com.ddclient.dongsdk.DongSDK;
import com.ddclient.dongsdk.DongSDKProxy;
import com.ddclient.dongsdk.PushInfo;
import com.dongdong.app.AppConfig;
import com.dongdong.app.AppContext;
import com.dongdong.app.adapter.UserPreferenceAdapter;
import com.dongdong.app.base.BaseActivity;
import com.dongdong.app.base.BaseApplication;
import com.dongdong.app.bean.UserBean;
import com.dongdong.app.db.UserOpe;
import com.dongdong.app.fragment.HomePagerFragment;
import com.dongdong.app.ui.dialog.CommonDialog;
import com.dongdong.app.ui.dialog.TipDialogManager;
import com.dongdong.app.util.CyptoUtils;
import com.dongdong.app.util.LogUtils;
import com.dongdong.app.util.TDevice;
import com.dongdong.app.widget.TitleBar;
import com.dongdong.app.widget.TitleBar.OnTitleBarClickListener;
import com.igexin.sdk.PushManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class LoginActivity extends BaseActivity implements
        OnTitleBarClickListener, OnClickListener, AdapterView.OnItemClickListener {

    private LinearLayout mLlLoginParent;
    private TextView mTvForgetPsw, mTvRegister, mTvLocalDevice;
    private CharSequence mEtUserName, mEtUserPwd;
    private CommonDialog mDialog;

    private ImageView mIvSelectedUser;
    private ImageView mIvLogin;
    private Timer mTimer;// 定时器

    private EditText mEtName, mEtPwd;
    private PopupWindow mPopupWindow;

    private LoginActivityDongAccountProxy mDongAccountProxy = new LoginActivityDongAccountProxy();

    @Override
    protected int getLayoutId() {
        return R.layout.activity_login;
    }

    @Override
    public void initView() {
        mEtName = (EditText) findViewById(R.id.et_name);
        mEtPwd = (EditText) findViewById(R.id.et_pwd);
        mTvForgetPsw = (TextView) findViewById(R.id.tv_forget_pwd);
        mTvRegister = (TextView) findViewById(R.id.tv_register);
        mTvLocalDevice = (TextView) findViewById(R.id.tv_local);
        mIvLogin = (ImageView) findViewById(R.id.iv_login);
        mLlLoginParent = (LinearLayout) findViewById(R.id.ll_login_parent);
        mIvSelectedUser = (ImageView) findViewById(R.id.bt_select_user);
    }

    @Override
    public void initData() {
        TitleBar titleBar = (TitleBar) findViewById(R.id.tb_title);
        titleBar.setTitleBarContent(getString(R.string.login));
        titleBar.setAddArrowShowing(false);
        titleBar.setOnTitleBarClickListener(this);
        mTvForgetPsw.setOnClickListener(this);
        mTvRegister.setOnClickListener(this);
        mTvLocalDevice.setOnClickListener(this);
        mIvLogin.setOnClickListener(this);
        mIvSelectedUser.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshUI(UserOpe.queryDataByUserIndex(BaseApplication.context(),
                UserOpe.FIRST_INDEX), true);
        DongSDKProxy.registerAccountCallback(mDongAccountProxy);
    }

    @Override
    protected void onPause() {
        super.onPause();
        DongSDKProxy.unRegisterAccountCallback(mDongAccountProxy);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
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
    public void onFinishClick() {

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.tv_forget_pwd:
                startActivity(new Intent(this, ForgetPwdActivity.class));
                break;
            case R.id.tv_register:
                startActivity(new Intent(this, RegisterActivity.class));
                break;
            case R.id.tv_local:
//              startActivity(new Intent(this, LocalDeviceActivity.class));
                break;
            case R.id.iv_login:
                if (TDevice.getNetworkType() == 0) {
                    TipDialogManager.showWithoutNetworDialog(this, null);
                    return;
                }
                mEtUserName = mEtName.getText();
                mEtUserPwd = mEtPwd.getText();
                if (TextUtils.isEmpty(mEtUserName)) {
                    BaseApplication.showToastShortInTop(R.string.user_name_can_not_empty);
                } else if (TextUtils.isEmpty(mEtUserPwd)) {
                    BaseApplication.showToastShortInTop(R.string.user_pwd_can_not_empty);
                } else {
                    mDialog = new CommonDialog(this);
                    View view = LayoutInflater.from(this).inflate(R.layout.loading_dialog, null);
                    TextView tipTextView = (TextView) view.findViewById(R.id.tv_tip);
                    tipTextView.setText(getString(R.string.login_wait));
                    mDialog.setContent(view);
                    mDialog.show();
                    DongSDKProxy.initDongAccount(mDongAccountProxy);
                    // 后续可能要加入取消回调引用接口
                    DongSDKProxy.login(mEtUserName.toString().trim(), mEtUserPwd.toString().trim());
                    mTimer = new Timer();
                    mTimer.schedule(new MyTimerTask(), new Date(), 1000);
                }
                break;
            case R.id.bt_select_user:
                showPopupWindow();
                break;
            default:
                break;
        }
    }

    /**
     * 显示PopupWindow窗口
     */
    public void showPopupWindow() {
        View userCacheView = this.getLayoutInflater().inflate(
                R.layout.user_cache_options, null);
        int width = mLlLoginParent.getWidth();
        ListView lvUserCache = (ListView) userCacheView.findViewById(R.id.list);
        lvUserCache.setOnItemClickListener(this);
        mPopupWindow = new PopupWindow(userCacheView, width,
                LayoutParams.WRAP_CONTENT, true);
        mPopupWindow.setTouchable(true);
        mPopupWindow.setOutsideTouchable(true);
        mPopupWindow.setBackgroundDrawable(new BitmapDrawable(getResources(), (Bitmap) null));
        mPopupWindow.showAsDropDown(mLlLoginParent, 0, -3);

        //去本地获取数据
        List<UserBean> rawUserBeanList = UserOpe.queryAll(BaseApplication.context());
        List<UserBean> decUserBeanList = new ArrayList<>();
        int size = rawUserBeanList.size();
        LogUtils.i("LoginActivity.clazz--->>>showPopupWindow sort before rawUserBeanList:"
                + rawUserBeanList + ",width:" + width);
        if (size > 0) {
            Collections.sort(rawUserBeanList, new Comparator<UserBean>() {
                @Override
                public int compare(UserBean lhs, UserBean rhs) {
                    return lhs.getIndex() - rhs.getIndex();
                }
            });
            LogUtils.i("LoginActivity.clazz--->>>showPopupWindow sort after rawUserBeanList:"
                    + rawUserBeanList);
            // 判断账户个数
//            if (size > 5) {
//                UserOpe.deleteDataById(LoginActivity.this, rawUserBeanList.get(0).getId());//????
//                rawUserBeanList.remove(0);
//            }
            for (int i = 0; i < size; i++) {
                UserBean rawUserBean = rawUserBeanList.get(i);
                UserBean decUserBean = new UserBean();
                decUserBean.setId(rawUserBean.getId());
                decUserBean.setUserName(CyptoUtils.decode(AppConfig.DES_KEY, rawUserBean.getUserName()));
                decUserBean.setPassWord(CyptoUtils.decode(AppConfig.DES_KEY, rawUserBean.getPassWord()));
                decUserBeanList.add(decUserBean);
            }
            UserPreferenceAdapter adapter = new UserPreferenceAdapter(this, decUserBeanList);
            lvUserCache.setAdapter(adapter);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        LogUtils.i("log6", "LoginActivity.clazz-->>onItemClick position:" + position);
        UserBean userBean = (UserBean) parent.getAdapter().getItem(position);
        mEtName.setText(userBean.getUserName());
        mEtPwd.setText(userBean.getPassWord());
        mPopupWindow.dismiss();
    }

    public void refreshUI(UserBean userBean, boolean isCyp) {
        if (userBean != null && isCyp) {
            String name = CyptoUtils.decode(AppConfig.DES_KEY, userBean.getUserName());
            String pwd = CyptoUtils.decode(AppConfig.DES_KEY, userBean.getPassWord());
            mEtName.setText(name);
            mEtPwd.setText(pwd);
        } else if (userBean != null) {
            String name = mEtName.getText().toString();
            if (userBean.getUserName().equals(name)) {
                mEtName.setText("");
                mEtPwd.setText("");
            }
        }
        mIvSelectedUser.setVisibility(UserOpe.queryAll(BaseApplication.context())
                .isEmpty() ? View.GONE : View.VISIBLE);
        if (mPopupWindow != null)
            mPopupWindow.dismiss();
    }


    private class MyTimerTask extends TimerTask implements TipDialogManager.OnTipDialogButtonClick {

        boolean shouldStop;
        int mLoginCount;

        @Override
        public void run() {
            mLoginCount++;
            if (mLoginCount > 15 && !shouldStop) {
                LoginActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (mDialog.isShowing()) mDialog.dismiss();
                        shouldStop = true;
                        TipDialogManager.showNormalTipDialog(LoginActivity.this, MyTimerTask.this,
                                R.string.tip, R.string.login_overtime, R.string.sure, R.string.cancel);
                    }
                });
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

    private class LoginActivityDongAccountProxy extends AbstractDongCallbackProxy.DongAccountCallbackImp {

        @Override
        public int onAuthenticate(InfoUser tInfo) {
            DongConfiguration.mUserInfo = tInfo;
            LogUtils.i("LoginActivity.clazz--->>>OnAuthenticate........tInfo:" + tInfo);
            String enUserName = CyptoUtils.encode(AppConfig.DES_KEY, mEtUserName.toString());
            String enUserPwd = CyptoUtils.encode(AppConfig.DES_KEY, mEtUserPwd.toString());
            //1.查询所有表
            List<UserBean> rawUserBeanList = UserOpe.queryAll(BaseApplication.context());
            //2 如果表格有数据size>0,如果当前登陆账号不/存在，更新index=0，将其他用户index+1
            boolean isOldUser = false;
            int temIndex = 0;
            if (rawUserBeanList.size() > 0) {
                for (UserBean temUserBean : rawUserBeanList) {
                    if (!temUserBean.getUserName().equals(enUserName)) {
                        temIndex++;
                        temUserBean.setIndex(temIndex);
                    } else {
                        //原来数据库就有这个账号
                        isOldUser = true;
                        temUserBean.setIndex(UserOpe.FIRST_INDEX);
                    }
                    UserOpe.updateDataByUserBean(BaseApplication.context(), temUserBean);
                }
            }
            if (!isOldUser) {//如果数据库不存在将插入登陆的用户
                UserBean userBean = new UserBean();
                userBean.setUserName(enUserName);
                userBean.setPassWord(enUserPwd);
                userBean.setIndex(UserOpe.FIRST_INDEX);
                UserOpe.insertDataByUserBean(BaseApplication.context(), userBean);
            }
            PushManager.getInstance().turnOnPush(LoginActivity.this);
            com.baidu.android.pushservice.PushManager.resumeWork(LoginActivity.this);
            DongSDKProxy.requestSetPushInfo(PushInfo.PUSHTYPE_FORCE_ADD);
            DongSDKProxy.requestGetDeviceListFromPlatform();
            mDialog.dismiss();
            // 保存用户登录信息
            AppContext.mAppConfig.setConfigValue(
                    AppConfig.DONG_CONFIG_SHARE_PREF_NAME,
                    AppConfig.KEY_USER_NAME, mEtUserName.toString().trim());
            AppContext.mAppConfig.setConfigValue(
                    AppConfig.DONG_CONFIG_SHARE_PREF_NAME,
                    AppConfig.KEY_USER_PWD, mEtUserPwd.toString().trim());
            AppContext.mAppConfig.setConfigValue(
                    AppConfig.DONG_CONFIG_SHARE_PREF_NAME,
                    AppConfig.KEY_IS_LOGIN, true);
            LoginActivity.this.finish();
            return 0;
        }

        @Override
        public int onUserError(int nErrNo) {
            LogUtils.i("LoginActivity.clazz--->>>OnUserError........nErrNo:" + nErrNo);
            if (mDialog.isShowing())
                mDialog.dismiss();
            mDialog.setTitle(R.string.tip);
            mDialog.setMessage(TDevice.getLoginMessage(nErrNo, LoginActivity.this) + "("
                    + LoginActivity.this.getString(R.string.errorCode) + nErrNo + ")");
            mDialog.setPositiveButton(R.string.sure, null);
            mDialog.show();
            mTimer.cancel();
            return 0;
        }
    }
}
