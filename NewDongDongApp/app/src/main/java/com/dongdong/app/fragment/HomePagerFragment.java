package com.dongdong.app.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import com.dd121.community.R;
import com.ddclient.dongsdk.AbstractDongCallbackProxy;
import com.ddclient.jnisdk.InfoPush;
import com.ddclient.jnisdk.InfoUser;
import com.ddclient.configuration.DongConfiguration;
import com.ddclient.dongsdk.DeviceInfo;
import com.ddclient.dongsdk.DongSDKProxy;
import com.ddclient.push.DongPushMsgManager;
import com.dongdong.app.AppConfig;
import com.dongdong.app.AppContext;
import com.dongdong.app.adapter.LinkRoomDynamicLayoutAdapter;
import com.dongdong.app.base.BaseApplication;
import com.dongdong.app.base.BaseFragment;
import com.dongdong.app.bean.FunctionBean;
import com.dongdong.app.interf.OnTabReselectListener;
import com.dongdong.app.ui.LoginActivity;
import com.dongdong.app.ui.dialog.TipDialogManager;
import com.dongdong.app.util.LogUtils;
import com.dongdong.app.util.TDevice;
import com.dongdong.app.util.UIHelper;
import com.dongdong.app.util.XmlUtils;
import com.dongdong.app.widget.DynamicItemContainView;
import com.dongdong.app.widget.LinkRoomDynamicLayout;
import com.dongdong.app.widget.LinkRoomDynamicLayout.OnDynamicViewChangedPositionListener;
import com.dongdong.app.widget.TitleBar;
import com.dongdong.app.widget.TitleBar.OnTitleBarClickListener;
import com.igexin.sdk.PushManager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class HomePagerFragment extends BaseFragment implements
        OnTabReselectListener, OnTitleBarClickListener, OnItemClickListener,
        OnDynamicViewChangedPositionListener {

    private TitleBar mTitleBar;
    private File mFuncFile;
    private List<FunctionBean> mFunctionsList = new ArrayList<>();
    private LinkRoomDynamicLayout mDynamicLayout;

    private String mDeviceID;//推送传过來的值

    private DeviceInfo mDeviceInfo;
    private HomePagerFragmentDongAccountProxy mDongAccountProxy =
            new HomePagerFragmentDongAccountProxy();

    @Override
    protected int getLayoutId() {
        return R.layout.home_pager_view_layout;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(getLayoutId(), container, false);
        Bundle bundle = getArguments();
        mDeviceID = bundle.getString(AppConfig.BUNDLE_KEY_DEVICE_ID);
        LogUtils.i("HomePagerFragment.clazz--->>>onCreateView ...mDeviceID:" + mDeviceID);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
        initData();
        LogUtils.i("HomePagerFragment.clazz--->>>onViewCreated ...");
    }

    @Override
    public void onResume() {
        super.onResume();
        mTitleBar.setTitleAnimator();
        boolean initDongAccount = DongConfiguration.mUserInfo != null;
        if (initDongAccount) {
            DongSDKProxy.registerAccountCallback(mDongAccountProxy);
        } else {
            String name = (String) AppContext.mAppConfig.getConfigValue(
                    AppConfig.DONG_CONFIG_SHARE_PREF_NAME,
                    AppConfig.KEY_USER_NAME, "");
            String pwd = (String) AppContext.mAppConfig.getConfigValue(
                    AppConfig.DONG_CONFIG_SHARE_PREF_NAME,
                    AppConfig.KEY_USER_PWD, "");
            boolean isLogin = (Boolean) AppContext.mAppConfig.getConfigValue(
                    AppConfig.DONG_CONFIG_SHARE_PREF_NAME,
                    AppConfig.KEY_IS_LOGIN, false);
            LogUtils.i("HomePagerFragment.clazz--->>>onResume name:" + name
                    + ",pwd:" + pwd + ",isLogin:" + isLogin);
            if (isLogin && !TextUtils.isEmpty(name) && !TextUtils.isEmpty(pwd)) {
                DongSDKProxy.initDongAccount(mDongAccountProxy);
                DongSDKProxy.login(name.trim(), pwd.trim());
                LogUtils.i("HomePagerFragment.clazz--->>>onResume login start ....:");
            }
        }
        // 设置标题栏信息
        showTitleInfo(!initDongAccount);
        LogUtils.i("HomePagerFragment.clazz--->>>onResume initDongAccount:" + initDongAccount);
    }

    @Override
    public void onPause() {
        super.onPause();
        // DongSDKProxy.unRegisterAccountCallback(mDongAccountProxy);
        LogUtils.i("HomePagerFragment.clazz--->>>onPause...");
    }

    @Override
    public void initView(View view) {
        mTitleBar = (TitleBar) view.findViewById(R.id.tb_title);
        mTitleBar.setBackArrowShowing(false);
        mTitleBar.setAddArrowShowing(false);
        mTitleBar.setOnTitleBarClickListener(this);
        mTitleBar.setTitleAnimator();

        mDynamicLayout = (LinkRoomDynamicLayout) view.findViewById(R.id.link_drag_grid_view);
        mDynamicLayout.setOnItemClickListener(this);
        mDynamicLayout.OnDynamicViewChangedPositionListener(this);
    }

    @Override
    public void initData() {
        mFuncFile = new File(BaseApplication.context().getDir("funprop",
                Context.MODE_PRIVATE), "funcprop.xml");
        LogUtils.i("HomePagerFragment.clazz--->>>dir is exist "
                + mFuncFile.exists());
        if (!mFuncFile.exists()) {
            mFunctionsList = AppContext.getFunctionsDatasInit();
            XmlUtils.createFunctionXml(mFuncFile, mFunctionsList, false);
        } else {
            mFunctionsList = XmlUtils.getFunctionsDatasByProp(mFuncFile);
        }
        LinkRoomDynamicLayoutAdapter dynamicAdapter = new LinkRoomDynamicLayoutAdapter(
                getActivity(), mFunctionsList);
        mDynamicLayout.setAdapter(dynamicAdapter);
    }

    // 第二次进入此Fragment, onResume()方法没执行,需要加入此方法来重新设置accountName
    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            LogUtils.i("HomePagerFragment.clazz-->>>onHiddenChanged showTitleInfo" +
                    " DongConfiguration.mUserInfo:" + DongConfiguration.mUserInfo);
            showTitleInfo(DongConfiguration.mUserInfo == null);
        }
        LogUtils.i("HomePagerFragment.clazz-->>>onHiddenChanged hidden:" + hidden);
    }

    private void showTitleInfo(boolean isNotLogin) {
        LogUtils.i("HomePagerFragment.clazz-->>showTitleInfo isNotLogin " + isNotLogin);
        if (isNotLogin) {// 1.未登录
            mTitleBar.setTitleBarContent(getString(R.string.pleaseLogin));
        } else {//2.已登录
            ArrayList<DeviceInfo> deviceList;
            DongConfiguration.mDeviceInfoList = deviceList = DongSDKProxy
                    .requestGetDeviceListFromCache();// 这句话很重要!!!!
            int size = deviceList.size();
            LogUtils.i("HomePagerFragment.clazz-->>showTitleInfo size " + size
                    + ",DongConfiguration.mDeviceInfo:" + DongConfiguration.mDeviceInfo);
            //2.1程序第一次进来会走：包括离线推送和手动进来
            if (size > 0 && DongConfiguration.mDeviceInfo == null) {
                //2.2默认设备序列号
                int defaultDeviceId = (int) AppContext.mAppConfig.getConfigValue(
                        AppConfig.DONG_CONFIG_SHARE_PREF_NAME,
                        /*AppConfig.KEY_DEFAULT_DEVICE_ID*/DongConfiguration.mUserInfo.userID + "", 0);
                LogUtils.i("HomePagerFragment.clazz-->>showTitleInfo defaultDeviceId:"
                        + defaultDeviceId + ",mDeviceID:" + mDeviceID + ",all device size:" + size);
                if (!TextUtils.isEmpty(mDeviceID)) {//2.3离线推送
                    LogUtils.i("HomePagerFragment.clazz-->>showTitleInfo is offline push and we will jump " +
                            "monitor activity deviceID:" + mDeviceID);
                    UIHelper.showVideoViewActivity(getActivity(), false, mDeviceID);
                    mDeviceID = null;
                } else if (defaultDeviceId != 0) {//2.4有默认设备
                    for (DeviceInfo deviceInfo : deviceList) {
                        LogUtils.i("HomePagerFragment.clazz-->>showTitleInfo deviceInfo.dwDeviceID " + deviceInfo.dwDeviceID);
                        if (defaultDeviceId == deviceInfo.dwDeviceID) {
                            mTitleBar.setTitleBarContent(deviceInfo.deviceName);
                            DongConfiguration.mDeviceInfo = mDeviceInfo = deviceInfo;
                            LogUtils.i("HomePagerFragment.clazz-->> ********** default !!!!mDeviceInfo " + mDeviceInfo);
                        }
                    }
                } else {//2.5没有默认设备，选择第一台
                    DeviceInfo deviceInfo = deviceList.get(0);
                    mTitleBar.setTitleBarContent(deviceInfo.deviceName);
                    DongConfiguration.mDeviceInfo = mDeviceInfo = deviceInfo;
                }
            } else if (DongConfiguration.mDeviceInfo != null) {
                mDeviceInfo = DongConfiguration.mDeviceInfo;
                mTitleBar.setTitleBarContent(mDeviceInfo.deviceName);
            } else {
                mTitleBar.setTitleBarContent(getString(R.string.no_device));
            }
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //判断登录
        if (DongConfiguration.mUserInfo == null) {
            startActivity(new Intent(getActivity(), LoginActivity.class));
            return;
        }
        //判断有无设备
        if (mDeviceInfo == null) {
            BaseApplication.showToastShortInCenter(R.string.no_device);
            return;
        }

        DynamicItemContainView dynamicView = (DynamicItemContainView) view;
        String name = dynamicView.getName();

        if (name.equals(getString(R.string.message))) {
            UIHelper.showMessageActivity(getActivity());
        } else if (name.equals(getString(R.string.monitor))) {
            //判断网络，只有在观看设备的时候需要网
            if (TDevice.getNetworkType() == 0) {
                TipDialogManager.showWithoutNetworDialog(getActivity(), null);
                return;
            }
            //解决第一次进入首页设备信息没及时更新的问题
            ArrayList<DeviceInfo> deviceInfoList = DongSDKProxy.requestGetDeviceListFromCache();
            for (DeviceInfo deviceInfo : deviceInfoList) {
                if (mDeviceInfo.dwDeviceID == deviceInfo.dwDeviceID) {
                    DongConfiguration.mDeviceInfo = mDeviceInfo = deviceInfo;
                }
            }
            if (mDeviceInfo.isOnline) {
                UIHelper.showVideoViewActivity(getActivity(), true, "");
            } else {
                BaseApplication.showToastShortInCenter(R.string.device_offline);
            }
        } else if (name.equals(getString(R.string.applykey))) {
            UIHelper.showApplyKeyActivity(getActivity());
        } else if (name.equals(getString(R.string.shapeopendoor))) {
            UIHelper.showShakeOpenDoorActivity(getActivity());
        } else if (name.equals(getString(R.string.repair))) {
            UIHelper.showRepairsActivity(getActivity());
        } else if (name.equals(getString(R.string.visitorphoto))) {
            UIHelper.showHomeSafeActivity(getActivity());
        } else if (name.equals(getString(R.string.opendoor))) {
            UIHelper.showVisitorRecordActivity(getActivity());
        } else if (name.equals(getString(R.string.phone))) {
            UIHelper.showCommonPhoneActivity(getActivity());
        } else if (name.equals(getString(R.string.dd_function_parking))) {
            UIHelper.showParkingActivity(getActivity());
        } else if (name.equals(getString(R.string.dd_function_finance))) {
            UIHelper.showFinanceActivity(getActivity());
        } else if (name.equals(getString(R.string.dd_function_more))) {
            System.out.print("消除警告用");
        }
    }

    @Override
    public void onTabReselect() {
    }

    @Override
    public void onBackClick() {
    }

    @Override
    public void onTitleClick() {
        if (DongConfiguration.mUserInfo == null) {
            startActivity(new Intent(getActivity(), LoginActivity.class));
        } else {
            UIHelper.showDeviceListActivity(getActivity());
        }
    }

    @Override
    public void onAddClick() {
    }

    @Override
    public void onFinishClick() {
    }

    @Override
    public void onItemChangedPosition(int oldPosition, int newPosition) {
        int childCount = mDynamicLayout.getChildCount();
        int funSize = mFunctionsList.size();
        for (int i = 0; i < childCount; i++) {
            DynamicItemContainView child = (DynamicItemContainView) mDynamicLayout
                    .getChildAt(i);
            for (int j = 0; j < funSize; j++) {
                FunctionBean function = mFunctionsList.get(j);
                if (child.getName() != null
                        && child.getName().equals(function.getName())) {
                    function.setSequence(i);
                }
            }
        }
        LogUtils.d("HomePagerFragment.clazz--->>> onItemChangedPosition >> oldPosition:"
                + oldPosition + "; newPosition" + newPosition
                + "; mFunctionsData:" + mFunctionsList);
        new Thread(new Runnable() {

            @Override
            public void run() {
                XmlUtils.createFunctionXml(mFuncFile, mFunctionsList, true);
            }
        }).start();
    }

    private class HomePagerFragmentDongAccountProxy extends
            AbstractDongCallbackProxy.DongAccountCallbackImp {

        @Override
        public int onAuthenticate(InfoUser tInfo) {
            DongConfiguration.mUserInfo = tInfo;
            LogUtils.i("HomePagerFragment.clazz--->>>OnAuthenticate........tInfo:" + tInfo);
            PushManager.getInstance().turnOnPush(HomePagerFragment.this.getActivity());
            com.baidu.android.pushservice.PushManager.resumeWork(HomePagerFragment.this.getActivity());
            DongSDKProxy.requestSetPushInfo(InfoPush.PUSHTYPE_FORCE_ADD);
            DongSDKProxy.requestGetDeviceListFromPlatform();
            return 0;
        }

        @Override
        public int onLoginOtherPlace(String tip) {
            TipDialogManager.showOtherLoginDialog(getActivity(), tip);
            return 0;
        }

        @Override
        public int onNewListInfo() {
            LogUtils.i("HomePageFragment.clazz -->>OnNewListInfo");
            showTitleInfo(DongConfiguration.mUserInfo == null);
            return 0;
        }

        @Override
        public int onCall(ArrayList<DeviceInfo> deviceInfoList) {
            LogUtils.i("HomePageFragment.clazz -->>OnCall deviceInfoList:" + deviceInfoList);
            int size = deviceInfoList.size();
            if (size > 0) {
                DeviceInfo deviceInfo = deviceInfoList.get(0);
                String message = deviceInfo.msg;
                LogUtils.i("HomePageFragment.clazz-->>OnCall() deviceName:" +
                        deviceInfo.deviceName + ",dwDeviceID：" + deviceInfo.dwDeviceID +
                        ",msg:" + deviceInfo.msg);
                DongPushMsgManager.pushMessageChange(getActivity(), message);
            }
            return 0;
        }

        @Override
        public int onTunnelUnlock(int result) {//暂时只会回调0成功
            LogUtils.i("HomePagerFragment.clazz--->>>onTunnelUnlock....result:" + result);
            BaseApplication.showToastShortInCenter(R.string.openlock);
            return 0;
        }

        @Override
        public int onUserError(int nErrNo) {
            LogUtils.i("HomePagerFragment.clazz--->>>OnUserError.....nErrNo:" + nErrNo);
            TipDialogManager.showTipDialog(HomePagerFragment.this.getActivity(),
                    R.string.tip, R.string.pwd_error_tip);
            return 0;
        }
    }
}
