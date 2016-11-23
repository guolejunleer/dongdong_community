package com.dongdong.app;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dd121.louyu.R;
import com.ddclient.configuration.DongConfiguration;
import com.ddclient.dongsdk.DongSDK;
import com.ddclient.dongsdk.DongSDKProxy;
import com.dongdong.app.base.BaseApplication;
import com.dongdong.app.fragment.HomePagerFragment;
import com.dongdong.app.fragment.MyPagerFragment;
import com.dongdong.app.ui.DoubleClickExitHelper;
import com.dongdong.app.ui.LoginActivity;
import com.dongdong.app.ui.dialog.TipDialogManager;
import com.dongdong.app.util.LogUtils;
import com.dongdong.app.util.StatusBarCompatUtils;
import com.dongdong.app.util.TDevice;

public class MainActivity extends FragmentActivity implements OnClickListener {

    public static boolean mIsPushStarted = false;
    private DoubleClickExitHelper mDoubleClickExit;

    // 二个tab布局
    private RelativeLayout mHomeLayout, mMyLayout;
    // 底部标签图片
    private ImageView mHomeImg, mMyImg;
    // 底部标签的文本
    private TextView mHomeTv, mMyTv;
    private View mBtUnclock;

    // 底部标签切换的Fragment
    private Fragment mHomeFragment, mMyFragment, mCurrentFragment;

    private long mLastTime;

    private String mDevcieID;

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppManager.getAppManager().addActivity(this);
        setContentView(R.layout.activity_main);
        StatusBarCompatUtils.compat(this);

        DongSDK.initializePush(this, DongSDK.PUSH_TYPE_ALL);// 初始化推送
        Bundle bundle = getIntent().getBundleExtra(AppConfig.INTENT_BUNDLE_KEY);
        if (bundle != null) {// 离线推送
            mDevcieID = bundle.getString(AppConfig.BUNDLE_KEY_DEVICE_ID, "");
            mIsPushStarted = true;
        }
        mDoubleClickExit = new DoubleClickExitHelper(this);
        mHomeLayout = (RelativeLayout) findViewById(R.id.rl_home);
        mMyLayout = (RelativeLayout) findViewById(R.id.rl_me);
        mHomeLayout.setOnClickListener(this);
        mMyLayout.setOnClickListener(this);

        mHomeImg = (ImageView) findViewById(R.id.iv_home);
        mMyImg = (ImageView) findViewById(R.id.iv_me);
        mHomeTv = (TextView) findViewById(R.id.tv_know);
        mMyTv = (TextView) findViewById(R.id.tv_me);
        mBtUnclock = findViewById(R.id.iv_unclock);
        // 中间按键图片触发
        mBtUnclock.setOnClickListener(this);
        if (mHomeFragment == null) {
            mHomeFragment = new HomePagerFragment();
            Bundle bundleFrag = new Bundle();
            bundleFrag.putString(AppConfig.BUNDLE_KEY_DEVICE_ID, mDevcieID);// 这里的values就是我们要传的值
            mHomeFragment.setArguments(bundleFrag);
            // ((HomePagerFragment) mHomeFragment).setDeviceID(mDevcieID);
        }
        if (!mHomeFragment.isAdded()) {
            // 提交事务
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.realtabcontent, mHomeFragment).commit();
            // 记录当前Fragment
            mCurrentFragment = mHomeFragment;
            // 设置图片文本的变化
            mHomeImg.setImageResource(R.mipmap.btn_know_pre);
            mHomeTv.setTextColor(getResources().getColor(
                    R.color.bottomtab_press));
            mMyImg.setImageResource(R.mipmap.btn_my_nor);
            mMyTv.setTextColor(getResources()
                    .getColor(R.color.bottomtab_normal));
        }
        LogUtils.i("MainActivity.clazz--->>>onCreate......");
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkContacts();// 检查手机是否有公司电话
        LogUtils.i("MainActivity.clazz--->>>onResume......");
    }

    @Override
    protected void onPause() {
        super.onPause();
        LogUtils.i("MainActivity.clazz--->>>onPause......");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mIsPushStarted = false;
    }

    private void checkContacts() {
        String contactName = TDevice.getContactFromPhone(
                BaseApplication.context(), AppConfig.COMPANY_PHONE);
        String nickName = getString(R.string.linkman);
        if (!contactName.equals(nickName)) {
            TDevice.insertContact2Phone(BaseApplication.context(), nickName,
                    AppConfig.COMPANY_PHONE);
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
                addOrShowFragment(getSupportFragmentManager().beginTransaction(),
                        mMyFragment);
                mHomeImg.setImageResource(R.mipmap.btn_know_nor);
                mHomeTv.setTextColor(getResources().getColor(
                        R.color.bottomtab_normal));
                mMyImg.setImageResource(R.mipmap.btn_my_pre);
                mMyTv.setTextColor(getResources().getColor(R.color.bottomtab_press));
                break;
            case R.id.iv_unclock:// 点击了开锁操作按钮
                if (TDevice.getNetworkType() == 0) {
                    TipDialogManager.showWithoutNetworDialog(this, null);
                    return;
                }
                if (Math.abs(System.currentTimeMillis() - mLastTime) <= 1000) {
                    return;
                }
                if (!DongSDKProxy.isInitedDongAccount()) {
                    startActivity(new Intent(this, LoginActivity.class));
                } else {
                    if (DongConfiguration.mDeviceInfo == null) {
                        BaseApplication.showToastShortInCenter(R.string.no_device);
                    } else if (DongConfiguration.mDeviceInfo.isOnline) {
                        int deviced = DongConfiguration.mDeviceInfo.dwDeviceID;
                        DongSDKProxy.requestUnlock(deviced);
                        BaseApplication.showToastShortInCenter(R.string.openlock);
                    } else {
                        BaseApplication
                                .showToastShortInCenter(R.string.device_offline);
                    }
                }
                mLastTime = System.currentTimeMillis();
                break;
            default:
                break;
        }
    }

    /**
     * 添加或者显示碎片
     *
     * @param transaction
     * @param fragment
     */
    private void addOrShowFragment(FragmentTransaction transaction,
                                   Fragment fragment) {
        if (mCurrentFragment == fragment)
            return;

        if (!fragment.isAdded()) { // 如果当前fragment未被添加，则添加到Fragment管理器中
            transaction.hide(mCurrentFragment)
                    .add(R.id.realtabcontent, fragment).commit();
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
    protected void onSaveInstanceState(Bundle outState) {
        // 当 API Level > 11 调用这个方法可能导致奔溃（android.os.Build.VERSION.SDK_INT > 11）
    }

}
