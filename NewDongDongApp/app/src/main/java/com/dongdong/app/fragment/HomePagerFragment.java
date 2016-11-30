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
import com.ddclient.MobileClientLib.InfoPush;
import com.ddclient.MobileClientLib.InfoUser;
import com.ddclient.configuration.DongConfiguration;
import com.ddclient.dongsdk.AbstractDongSDKProxy.DongAccountCallbackImp;
import com.ddclient.dongsdk.DeviceInfo;
import com.ddclient.dongsdk.DongSDKProxy;
import com.ddclient.push.PushMessageChange;
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

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class HomePagerFragment extends BaseFragment implements
        OnTabReselectListener, OnTitleBarClickListener, OnItemClickListener,
        OnDynamicViewChangedPositionListener {

    private TitleBar mTitleBar;
    private File mFuncFile;
    private List<FunctionBean> mFunctionsDatas = new ArrayList<>();
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

        boolean initedDongAccount = DongSDKProxy.isInitedDongAccount();
        if (initedDongAccount) {
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
                DongSDKProxy.login(name, pwd);
                LogUtils.i("HomePagerFragment.clazz--->>>onResume login start ....:");
            }
        }
        // 设置标题栏信息
        showTitleInfo(initedDongAccount);
        LogUtils.i("HomePagerFragment.clazz--->>>onResume initedDongAccount:"
                + initedDongAccount);
    }

    @Override
    public void onPause() {
        super.onPause();
        DongSDKProxy.unRegisterAccountCallback();
        LogUtils.i("HomePagerFragment.clazz--->>>onPause...");
    }

    @Override
    public void initView(View view) {
        mTitleBar = (TitleBar) view.findViewById(R.id.tb_title);
        mTitleBar.setBackArrowShowing(false);
        mTitleBar.setAddArrowShowing(false);
        mTitleBar.setOnTitleBarClickListener(this);

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
            mFunctionsDatas = AppContext.getFunctionsDatasInit();
            XmlUtils.createFunctionXml(mFuncFile, mFunctionsDatas, false);
        } else {
            mFunctionsDatas = XmlUtils.getFunctionsDatasByProp(mFuncFile);
        }
        LinkRoomDynamicLayoutAdapter mNewAdatper = new LinkRoomDynamicLayoutAdapter(
                getActivity(), mFunctionsDatas);
        mDynamicLayout.setAdapter(mNewAdatper);
    }


    private void showTitleInfo(boolean initedDongAccount) {
        if (!initedDongAccount) {// 1.未登录
            mTitleBar.setTitleBarContent("请先登录");
        } else {//2.已登录
            ArrayList<DeviceInfo> deviceList;
            DongConfiguration.mDeviceInfoList = deviceList = DongSDKProxy
                    .requestGetDeviceListFromCache();// 这句话很重要!!!!
            int size = deviceList.size();
            //程序第一次进来会走：包括离线推送和手动进来
            if (size > 0 && DongConfiguration.mDeviceInfo == null) {
                //默认设备序列号
                String deviceSeri = (String) AppContext.mAppConfig.getConfigValue(
                        AppConfig.DONG_CONFIG_SHARE_PREF_NAME,
                        AppConfig.KEY_DEVICE_SERIAL, "");
                LogUtils.i("HomePagerFragment.clazz-->>showTitleInfo deviceSeri:" + deviceSeri +
                        ",mDeviceID:" + mDeviceID);
                if (!TextUtils.isEmpty(mDeviceID)) {//离线推送
                    LogUtils.i("HomePagerFragment.clazz-->>is offline push and we will jump " +
                            "monitor activty deviceID:" + mDeviceID);
                    UIHelper.showVideoViewActivity(getActivity(), false, mDeviceID);
                    mDeviceID = null;
                } else if (!TextUtils.isEmpty(deviceSeri)) {//有默认设备
                    for (DeviceInfo deviceInfo : deviceList) {
                        if (deviceSeri.equals(deviceInfo.deviceSerialNO)) {
                            mTitleBar.setTitleBarContent(deviceInfo.deviceName);
                            DongConfiguration.mDeviceInfo = mDeviceInfo = deviceInfo;
                        }
                    }
                } else {//没有默认设备，选择第一台
                    DeviceInfo deviceInfo = deviceList.get(0);
                    mTitleBar.setTitleBarContent(deviceInfo.deviceName);
                    DongConfiguration.mDeviceInfo = mDeviceInfo = deviceInfo;
                }
            } else if (DongConfiguration.mDeviceInfo != null) {
                mDeviceInfo = DongConfiguration.mDeviceInfo;
                mTitleBar.setTitleBarContent(mDeviceInfo.deviceName);
            } else {
                mTitleBar.setTitleBarContent("暂无设备");
            }
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (TDevice.getNetworkType() == 0) {
            TipDialogManager.showWithoutNetworDialog(getActivity(), null);
            return;
        }
        if (!DongSDKProxy.isInitedDongAccount()) {
            startActivity(new Intent(getActivity(), LoginActivity.class));
            return;
        }

        DynamicItemContainView dynamicView = (DynamicItemContainView) view;
        String name = dynamicView.getName();

        if (name.equals(getString(R.string.message))) {
            BaseApplication.showToastShortInCenter(mDeviceInfo == null ?
                    R.string.please_select_device : R.string.not_open_service);
        } else if (name.equals(getString(R.string.monitor))) {
            if (mDeviceInfo != null && mDeviceInfo.isOnline) {
                UIHelper.showVideoViewActivity(getActivity(), true, "");
            } else if (mDeviceInfo == null) {
                BaseApplication.showToastShortInCenter(R.string.no_device);
            } else {
                BaseApplication.showToastShortInCenter(R.string.device_offline);
            }
        } else if (name.equals(getString(R.string.applykey))) {
            BaseApplication.showToastShortInCenter(mDeviceInfo == null ?
                    R.string.please_select_device : R.string.not_open_service);
        } else if (name.equals(getString(R.string.shapeopendoor))) {
            BaseApplication.showToastShortInCenter(mDeviceInfo == null ?
                    R.string.please_select_device : R.string.not_open_service);
        } else if (name.equals(getString(R.string.repair))) {
            BaseApplication.showToastShortInCenter(mDeviceInfo == null ?
                    R.string.please_select_device : R.string.not_open_service);
        } else if (name.equals(getString(R.string.homesafe))) {
            BaseApplication.showToastShortInCenter(mDeviceInfo == null ?
                    R.string.please_select_device : R.string.not_open_service);
        } else if (name.equals(getString(R.string.visitorrecord))) {
            BaseApplication.showToastShortInCenter(mDeviceInfo == null ?
                    R.string.please_select_device : R.string.not_open_service);
        } else if (name.equals(getString(R.string.phone))) {
            BaseApplication.showToastShortInCenter(mDeviceInfo == null ?
                    R.string.please_select_device : R.string.not_open_service);
//          UIHelper.showCommonPhoneActivity(getActivity());
        } else if (name.equals(getString(R.string.dd_function_parking))) {
            BaseApplication.showToastShortInCenter(mDeviceInfo == null ?
                    R.string.please_select_device : R.string.not_open_service);
        } else if (name.equals(getString(R.string.dd_function_finance))) {
            BaseApplication.showToastShortInCenter(mDeviceInfo == null ?
                    R.string.please_select_device : R.string.not_open_service);
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
        if (!DongSDKProxy.isInitedDongAccount()) {
            // BaseApplication.showToastShort(R.string.pleaseLogin);
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
    public void onItemChangedPostition(int oldPosition, int newPosition) {
        int childCount = mDynamicLayout.getChildCount();
        int funSize = mFunctionsDatas.size();
        for (int i = 0; i < childCount; i++) {
            DynamicItemContainView child = (DynamicItemContainView) mDynamicLayout
                    .getChildAt(i);
            for (int j = 0; j < funSize; j++) {
                FunctionBean function = mFunctionsDatas.get(j);
                if (child.getName() != null
                        && child.getName().equals(function.getName())) {
                    function.setSequence(i);
                }
            }
        }
        LogUtils.d("HomePagerFragment.clazz--->>> onItemChangedPostition >> oldPosition:"
                + oldPosition
                + "; newPosition"
                + newPosition
                + "; mFunctionsDatas:" + mFunctionsDatas);
        new Thread(new Runnable() {

            @Override
            public void run() {
                XmlUtils.createFunctionXml(mFuncFile, mFunctionsDatas, true);
            }
        }).start();
    }

    private class HomePagerFragmentDongAccountProxy extends
            DongAccountCallbackImp {

        @Override
        public int OnAuthenticate(InfoUser tInfo) {
            DongConfiguration.mUserInfo = tInfo;
            LogUtils.i("HomePagerFragment.clazz--->>>OnAuthenticate........tInfo:"
                    + tInfo);
            DongSDKProxy.requestSetPushInfo(InfoPush.PUSHTYPE_FORCE_ADD);
            DongSDKProxy.requestGetDeviceListFromPlatform();
            return 0;
        }

        @Override
        public int OnLoginOtherPlace(String tip) {
            TipDialogManager.showOtherLoginDialog(getActivity(), tip);
            return 0;
        }

        @Override
        public int OnNewListInfo() {
            LogUtils.i("HomePageFragment.clazz -->>OnNewListInfo");
            showTitleInfo(true);
            return 0;
        }

        @Override
        public int OnCall(ArrayList<DeviceInfo> infos) {
            LogUtils.i("HomePageFragment.clazz -->>OnCall infos:" + infos);
            int size = infos.size();
            if (size > 0) {
                DeviceInfo deviceInfo = infos.get(0);
                String message = deviceInfo.msg;
                LogUtils.i("HomePageFragment.clazz-->>OnCall() deviceName:" +
                        deviceInfo.deviceName + ",dwDeviceID：" + deviceInfo.dwDeviceID +
                        ",msg:" + deviceInfo.msg);
                PushMessageChange.pushMessageChange(getActivity(), message);
            }
            return 0;
        }

        @Override
        public int OnUserError(int nErrNo) {
            LogUtils.i("HomePagerFragment.clazz--->>>OnUserError........nErrNo:"
                    + nErrNo);
            return 0;
        }
    }
}
