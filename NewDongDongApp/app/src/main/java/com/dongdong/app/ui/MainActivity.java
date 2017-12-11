package com.dongdong.app.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.dd121.community.R;
import com.ddclient.configuration.DongConfiguration;
import com.ddclient.dongsdk.DeviceInfo;
import com.ddclient.dongsdk.DongSDK;
import com.ddclient.dongsdk.DongSDKProxy;
import com.ddclient.push.DongPushMsgManager;
import com.dongdong.app.AppConfig;
import com.dongdong.app.AppContext;
import com.dongdong.app.AppManager;
import com.dongdong.app.base.BaseApplication;
import com.dongdong.app.fragment.HomePagerFragment;
import com.dongdong.app.fragment.MyPagerFragment;
import com.dongdong.app.ui.dialog.TipDialogManager;
import com.dongdong.app.ui.dialog.TipDialogManager.OnTipDialogButtonClick;
import com.dongdong.app.util.LogUtils;
import com.dongdong.app.util.PhoneUtils;
import com.dongdong.app.util.StatusBarCompatUtils;
import com.dongdong.app.util.TDevice;

import java.util.ArrayList;

public class MainActivity extends FragmentActivity implements OnClickListener,
        OnTipDialogButtonClick {
    public static boolean mIsPushStarted = false;


//    private MediaPlayer mMediaPlayer = new MediaPlayer();

    private DoubleClickExitHelper mDoubleClickExit;
    // 底部标签图片
    private ImageView mHomeImg, mMyImg;
    // 底部标签的文本
    private TextView mHomeTv, mMyTv;

    // 底部标签切换的Fragment
    private Fragment mHomeFragment, mMyFragment, mCurrentFragment;

    private long mLastTime;

    private String mDeviceID = "";
    private String mPushTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DongSDK.initializePush(this, DongPushMsgManager.PUSH_TYPE_ALL);// 1.初始化推送
//        PushManager.getInstance().turnOffPush(this);
        com.baidu.android.pushservice.PushManager.stopWork(this);
        AppManager.getAppManager().addActivity(this);
        setContentView(R.layout.activity_main);
        StatusBarCompatUtils.compat(this);
        Bundle bundle = getIntent().getBundleExtra(AppConfig.INTENT_BUNDLE_KEY);
        if (bundle != null) {// 离线推送
            mIsPushStarted = true;
            mDeviceID = bundle.getString(AppConfig.BUNDLE_KEY_DEVICE_ID, "");
            mPushTime = bundle.getString(AppConfig.BUNDLE_KEY_PUSH_TIME);
            // 后台在线推送时，自动点亮屏幕
//            TDevice.wakeUpAndUnlockScreen(this);暂时不用
//            getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
//                    | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
            final Window win = getWindow();
            win.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                    | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                    | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                    | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
            LogUtils.i("log5", "MainActivity.clazz-->>initView wakeUpAndUnlockScreen...");
        }
        mDoubleClickExit = new DoubleClickExitHelper(this);
        View homeLayout = findViewById(R.id.rl_home);
        View myLayout = findViewById(R.id.rl_me);
        homeLayout.setOnClickListener(this);
        myLayout.setOnClickListener(this);

        mHomeImg = (ImageView) findViewById(R.id.iv_home);
        mMyImg = (ImageView) findViewById(R.id.iv_me);
        mHomeTv = (TextView) findViewById(R.id.tv_know);
        mMyTv = (TextView) findViewById(R.id.tv_me);
        View btUnlock = findViewById(R.id.iv_unclock);

        // 中间按键图片触发
        btUnlock.setOnClickListener(this);
        if (mHomeFragment == null) {
            mHomeFragment = new HomePagerFragment();
            LogUtils.i("MainActivity.clazz--->>>onCreate mPushTime:" + mPushTime);
            if (TextUtils.isEmpty(mPushTime)) {//2.配置进行进入监视界面参数
                Bundle bundleFrag = new Bundle();
                bundleFrag.putString(AppConfig.BUNDLE_KEY_DEVICE_ID, mDeviceID);
                mHomeFragment.setArguments(bundleFrag);
            } else {//3.提示用户离线推送曾经呼叫过
                TipDialogManager.showTipDialog(this, BaseApplication.context().getString(R.string.tip),
                        BaseApplication.context().getString(R.string.tip_push_time, mPushTime));
            }
        }
        if (!mHomeFragment.isAdded()) {
            // 4.提交事务
            getSupportFragmentManager().beginTransaction().add(
                    R.id.fl_realtabcontent, mHomeFragment).commit();
            // 5.记录当前Fragment
            mCurrentFragment = mHomeFragment;
            // 6.设置图片文本的变化
            mHomeImg.setImageResource(R.mipmap.btn_know_pre);
            mHomeTv.setTextColor(getResources().getColor(R.color.bottomtab_press));
            mMyImg.setImageResource(R.mipmap.btn_my_nor);
            mMyTv.setTextColor(getResources().getColor(R.color.bottomtab_normal));
        }
        //7.检查网络
        int networkType = TDevice.getNetworkType();
        if (networkType == 0) {
            TipDialogManager.showWithoutNetDialog(this, this);
        } else if (networkType == 2 || networkType == 3) {
            TipDialogManager.showNormalTipDialog(this, this,
                    R.string.tip, R.string.tip_choose_net,
                    R.string.continues, R.string.cancel);
        }
        LogUtils.i("MainActivity.clazz--->>>onCreate......networkType:" + networkType);
        checkContacts();// 检查手机是否有公司电话
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mIsPushStarted = false;
    }

    @Override
    public void onPositiveButtonClick() {
    }

    @Override
    public void onNegativeButtonClick() {
    }

    private void checkContacts() {
        try {
            String contactName = PhoneUtils.getContactNameByPhoneNum(AppConfig.COMPANY_PHONE);
            String nickName = getString(R.string.linkman);
            if (!contactName.equals(nickName)) {
                PhoneUtils.insertContact2Phone(
                        BaseApplication.context(), nickName, AppConfig.COMPANY_PHONE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.rl_home:
                if (mHomeFragment == null) {
                    mHomeFragment = new HomePagerFragment();
                }
                addOrShowFragment(getSupportFragmentManager().beginTransaction(),
                        mHomeFragment);
                // 设置底部tab变化
                mHomeImg.setImageResource(R.mipmap.btn_know_pre);
                mHomeTv.setTextColor(getResources().getColor(
                        R.color.bottomtab_press));
                mMyImg.setImageResource(R.mipmap.btn_my_nor);
                mMyTv.setTextColor(getResources()
                        .getColor(R.color.bottomtab_normal));
                break;
            case R.id.rl_me:
                if (mMyFragment == null) {
                    mMyFragment = new MyPagerFragment();
                }
                addOrShowFragment(getSupportFragmentManager().beginTransaction(), mMyFragment);
                mHomeImg.setImageResource(R.mipmap.btn_know_nor);
                mHomeTv.setTextColor(getResources().getColor(
                        R.color.bottomtab_normal));
                mMyImg.setImageResource(R.mipmap.btn_my_pre);
                mMyTv.setTextColor(getResources().getColor(R.color.bottomtab_press));
                break;
            case R.id.iv_unclock:// 点击了开锁操作按钮
                if (TDevice.getNetworkType() == 0) {
                    TipDialogManager.showWithoutNetDialog(this, null);
                    return;
                }
                if (Math.abs(System.currentTimeMillis() - mLastTime) <= 1000) {
                    return;
                }
                LogUtils.i("MainActivity.clazz--->>>iv_unlock click......" +
                        "DongConfiguration.mDeviceInfo:" + DongConfiguration.mDeviceInfo);
                if (DongConfiguration.mUserInfo == null) {
                    startActivity(new Intent(this, LoginActivity.class));
                } else {
                    if (DongConfiguration.mDeviceInfo == null) {
                        BaseApplication.showToastShortInCenter(R.string.no_device);
                    }/* else if (DongConfiguration.mDeviceInfo.isOnline) {
                        int deviceID = DongConfiguration.mDeviceInfo.dwDeviceID;
                        DongSDKProxy.requestUnlock(deviceID);
                        BaseApplication.showToastShortInCenter(R.string.openlock);
                    }*/ else {
//                        BaseApplication.showToastShortInCenter(R.string.device_offline);
                        ArrayList<DeviceInfo> deviceList = DongSDKProxy.requestGetDeviceListFromCache();
                        for (DeviceInfo deviceInfo : deviceList) {//更新设备状态
                            if (DongConfiguration.mDeviceInfo.dwDeviceID == deviceInfo.dwDeviceID) {
                                DongConfiguration.mDeviceInfo = deviceInfo;
                            }
                        }
                        LogUtils.i("MainActivity.clazz--->>>iv_unlock click......after" +
                                "DongConfiguration.mDeviceInfo:" + DongConfiguration.mDeviceInfo);
                        if (DongConfiguration.mDeviceInfo.isOnline) {
                            int deviceID = DongConfiguration.mDeviceInfo.dwDeviceID;
                            DongSDKProxy.requestUnlock(deviceID);
                        } else {
                            BaseApplication.showToastShortInCenter(R.string.device_offline);
                        }
                    }
                }
                mLastTime = System.currentTimeMillis();
                break;
            default:
                break;
        }
    }

    private void addOrShowFragment(FragmentTransaction transaction, Fragment fragment) {
        if (mCurrentFragment == fragment)
            return;
        if (!fragment.isAdded()) { // 如果当前fragment未被添加，则添加到Fragment管理器中
            transaction.hide(mCurrentFragment).add(R.id.fl_realtabcontent, fragment).commit();
        } else {
            transaction.hide(mCurrentFragment).show(fragment).commit();
        }
        mCurrentFragment = fragment;
    }

    /**
     * 监听返回--是否退出程序
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            // 是否退出应用
            if ((Boolean) AppContext.mAppConfig.getConfigValue(
                    AppConfig.DONG_CONFIG_SHARE_PREF_NAME,
                    AppConfig.KEY_DOUBLE_CLICK_EXIT, true)) {
                return mDoubleClickExit.onKeyDown(keyCode, event);
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Bundle bundle = intent.getBundleExtra(AppConfig.INTENT_BUNDLE_KEY);
        if (bundle != null) {// 离线推送
            mDeviceID = bundle.getString(AppConfig.BUNDLE_KEY_DEVICE_ID, "");
            mPushTime = bundle.getString(AppConfig.BUNDLE_KEY_PUSH_TIME);
        }
        LogUtils.i("MainActivity.clazz--->>>onNewIntent......mDeviceID:"
                + mDeviceID + ",mPushTime:" + mPushTime);
        if (!TextUtils.isEmpty(mPushTime)) {//3.提示用户离线推送曾经呼叫过
            TipDialogManager.showCancelableTipDialog(this,
                    BaseApplication.context().getString(R.string.tip),
                    BaseApplication.context().getString(R.string.tip_push_time, mPushTime),
                    false);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        // 当 API Level > 11 调用这个方法可能导致崩溃（android.os.Build.VERSION.SDK_INT > 11）
        LogUtils.i("MainActivity.clazz--->>>onSaveInstanceState......");
    }

}
