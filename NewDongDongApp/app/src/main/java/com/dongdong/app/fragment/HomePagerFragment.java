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
import com.ddclient.configuration.DongConfiguration;
import com.ddclient.dongsdk.AbstractDongCallbackProxy;
import com.ddclient.dongsdk.DeviceInfo;
import com.ddclient.dongsdk.DongSDKProxy;
import com.ddclient.jnisdk.InfoPush;
import com.ddclient.jnisdk.InfoUser;
import com.ddclient.push.DongPushMsgManager;
import com.dongdong.app.AppConfig;
import com.dongdong.app.AppContext;
import com.dongdong.app.adapter.ADViewPagerAdapter;
import com.dongdong.app.adapter.BulletinViewPagerAdapter;
import com.dongdong.app.adapter.HomePagerFragmentAdapter;
import com.dongdong.app.api.ApiHttpClient;
import com.dongdong.app.base.BaseApplication;
import com.dongdong.app.base.BaseFragment;
import com.dongdong.app.bean.BulletinBean;
import com.dongdong.app.bean.FunctionBean;
import com.dongdong.app.bean.DeviceVillageBean;
import com.dongdong.app.db.BulletinOpe;
import com.dongdong.app.db.DeviceVillageOpe;
import com.dongdong.app.interf.OnTabReselectListener;
import com.dongdong.app.ui.LoginActivity;
import com.dongdong.app.ui.dialog.TipDialogManager;
import com.dongdong.app.util.FileUtils;
import com.dongdong.app.util.LogUtils;
import com.dongdong.app.util.TDevice;
import com.dongdong.app.util.UIHelper;
import com.dongdong.app.util.XmlUtils;
import com.dongdong.app.widget.CommonViewPager;
import com.dongdong.app.widget.DynamicItemContainView;
import com.dongdong.app.widget.HomePagerFragmentLayout;
import com.dongdong.app.widget.HomePagerFragmentLayout.OnDynamicViewChangedPositionListener;
import com.dongdong.app.widget.TitleBar;
import com.dongdong.app.widget.TitleBar.OnTitleBarClickListener;
import com.igexin.sdk.PushManager;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import cz.msebera.android.httpclient.Header;

import static com.dongdong.app.api.ApiHttpClient.getDVNotices;
import static com.dongdong.app.util.TDevice.getLoginMessage;
import static com.dongdong.app.widget.HomePagerFragmentLayout.KEY_VIEW_GROUP;
import static com.dongdong.app.widget.HomePagerFragmentLayout.KEY_VIEW_PAGER;

public class HomePagerFragment extends BaseFragment implements
        OnTabReselectListener, OnTitleBarClickListener, OnItemClickListener,
        OnDynamicViewChangedPositionListener {

    private List<BulletinBean> mBulletinList = new ArrayList<>();
    private BulletinViewPagerAdapter mBulletinViewPagerAdapter;
    private ViewGroup mBulletinViewPagerPoints;

    private CommonViewPager mADViewPager;//广告轮播图
    private ViewGroup mADPagerPoints;

    private TitleBar mTitleBar;
    private File mFuncFile;
    private List<FunctionBean> mFunctionsList = new ArrayList<>();
    private HomePagerFragmentLayout mDynamicLayout;

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
        LogUtils.i("HomePagerFragment.clazz--->>>onCreateView befor...mDeviceID:"
                + mDeviceID + ",bundle:" + bundle);
        if (bundle != null)
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
//        mTitleBar.setTitleAnimator();
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
        // 1.设置标题栏信息
        showTitleInfo(!initDongAccount);
        // 2.登录后查看设置物业公告信息
        showBulletinInfo();
        LogUtils.i("HomePagerFragment.clazz--->>>onResume initDongAccount:" + initDongAccount);
    }

    @Override
    public void onPause() {
        super.onPause();
        DongSDKProxy.unRegisterAccountCallback(mDongAccountProxy);
        LogUtils.i("HomePagerFragment.clazz--->>>onPause...");
    }

    @Override
    public void initView(View view) {
        mTitleBar = (TitleBar) view.findViewById(R.id.tb_title);
        mTitleBar.setBackArrowShowing(false);
        mTitleBar.setAddArrowShowing(false);
        mTitleBar.setOnTitleBarClickListener(this);
//        mTitleBar.setTitleAnimator();

        mDynamicLayout = (HomePagerFragmentLayout) view.findViewById(R.id.link_drag_grid_view);
        mDynamicLayout.setOnItemClickListener(this);
        mDynamicLayout.OnDynamicViewChangedPositionListener(this);
    }

    @Override
    public void initData() {
        //解决本地功能配置模块文件的更新问题
        dealWithFunctionXml();
        HomePagerFragmentAdapter dynamicAdapter = new HomePagerFragmentAdapter(
                getActivity(), mFunctionsList);
        mDynamicLayout.setAdapter(dynamicAdapter);

        CommonViewPager bulletinViewPager = (CommonViewPager) mDynamicLayout.getBulletinViewPager().get(KEY_VIEW_PAGER);
        mBulletinViewPagerPoints = (ViewGroup) mDynamicLayout.getBulletinViewPager().get(KEY_VIEW_GROUP);

        mBulletinViewPagerAdapter = new BulletinViewPagerAdapter(getActivity(),
                bulletinViewPager, mBulletinViewPagerPoints/*, mBulletinList*/);
//        mBulletinViewPager.setCurrentItem(Integer.MAX_VALUE / 2);
        bulletinViewPager.setAdapter(mBulletinViewPagerAdapter);
//        mBulletinViewPager.setCurrentItem(Integer.MAX_VALUE / 2);
        LogUtils.i("HomePagerFragment.clazz-->initData()-->bulletinViewPager:" + bulletinViewPager);
        mADViewPager = (CommonViewPager) mDynamicLayout.getADViewPager().get("viewPager");
        mADPagerPoints = (ViewGroup) mDynamicLayout.getADViewPager().get("viewGroup");

        //初始化广告
        getADFromNet();
    }

    // 第二次进入此Fragment, onResume()方法没执行,需要加入此方法来重新设置accountName
    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            LogUtils.i("HomePagerFragment.clazz-->>>onHiddenChanged showTitleInfo & showBulletinInfo" +
                    " DongConfiguration.mUserInfo:" + DongConfiguration.mUserInfo);
            showTitleInfo(DongConfiguration.mUserInfo == null);
            showBulletinInfo();
        }
        LogUtils.i("HomePagerFragment.clazz-->>>onHiddenChanged hidden:" + hidden);
    }

    /**
     * 解决本地功能配置模块文件的更新问题
     */
    private void dealWithFunctionXml() {
        //首页界面配置文件
        mFuncFile = new File(BaseApplication.context().getDir("funprop", Context.MODE_PRIVATE),
                "funcprop.xml");
        LogUtils.i("HomePagerFragment.clazz--->>>dir is exist " + mFuncFile.exists());
        int versionCode = (int) AppContext.mAppConfig.getConfigValue(
                AppConfig.DONG_CONFIG_SHARE_PREF_NAME, AppConfig.KEY_VERSION_CODE, 0);
        boolean mIsNotSameVersion = false;
        if (versionCode /*!= TDevice.getVersionCode()*/ < 8) {// every update function should changed it
            mIsNotSameVersion = true;
            //储存对应的versionCode
            AppContext.mAppConfig.setConfigValue(
                    AppConfig.DONG_CONFIG_SHARE_PREF_NAME, AppConfig.KEY_VERSION_CODE,
                    TDevice.getVersionCode());
        }
        if (!mFuncFile.exists()) {
            mFunctionsList = AppContext.getFunctionsDataInit();
            XmlUtils.createFunctionXml(mFuncFile, mFunctionsList, false);
        } else if (mIsNotSameVersion) {
            //删除本地xml文件
            mFuncFile.delete();
            mFunctionsList = AppContext.getFunctionsDataInit();
            XmlUtils.createFunctionXml(mFuncFile, mFunctionsList, false);
        } else {
            mFunctionsList = XmlUtils.getFunctionsDataByProp(mFuncFile);
        }
    }

    /**
     * 显示标题栏的设备名称
     *
     * @param isNotLogin 是否登录
     */
    private void showTitleInfo(boolean isNotLogin) {
        LogUtils.i("HomePagerFragment.clazz-->>showTitleInfo isNotLogin " + isNotLogin);
        if (isNotLogin) {// 1.未登录
            mTitleBar.setTitleBarContent(getString(R.string.pleaseLogin));
        } else {//2.已登录
            ArrayList<DeviceInfo> deviceList;
            DongConfiguration.mDeviceInfoList = deviceList = DongSDKProxy.requestGetDeviceListFromCache();// 这句话很重要!!!!
            int size = deviceList.size();
            LogUtils.i("HomePagerFragment.clazz-->>showTitleInfo size " + size
                    + ",DongConfiguration.mDeviceInfo:" + DongConfiguration.mDeviceInfo);
            //2.1程序第一次进来会走：包括离线推送和手动进来
            if (size > 0 && DongConfiguration.mDeviceInfo == null) {
                //2.2默认设备序列号
                int defaultDeviceId = (int) AppContext.mAppConfig.getConfigValue(
                        AppConfig.DONG_CONFIG_SHARE_PREF_NAME,
                        DongConfiguration.mUserInfo.userID + "", 0);
                LogUtils.i("HomePagerFragment.clazz-->>showTitleInfo defaultDeviceId:"
                        + defaultDeviceId + ",mDeviceID:" + mDeviceID + ",all device size:" + size);
                if (!TextUtils.isEmpty(mDeviceID)) {//2.3离线推送
                    LogUtils.i("HomePagerFragment.clazz-->>showTitleInfo is online push and we will jump " +
                            "monitor activity deviceID:" + mDeviceID);
                    UIHelper.showVideoViewActivity(getActivity(), false, mDeviceID);
                    mDeviceID = null;
                } else if (defaultDeviceId != 0) {//2.4有默认设备
                    for (DeviceInfo deviceInfo : deviceList) {
                        LogUtils.i("HomePagerFragment.clazz-->>showTitleInfo deviceInfo.dwDeviceID " + deviceInfo.dwDeviceID);
                        if (defaultDeviceId == deviceInfo.dwDeviceID) {
                            mTitleBar.setTitleBarContent(deviceInfo.deviceName);
                            DongConfiguration.mDeviceInfo = deviceInfo;
                            LogUtils.i("HomePagerFragment.clazz-->> ********** default" +
                                    " !!!!DongConfiguration.mDeviceInfo  " + DongConfiguration.mDeviceInfo);
                        }
                    }
                    if (DongConfiguration.mDeviceInfo == null) {//如果循环遍历设备后发现本地默认设备值发生错误，那么选择第一台
                        DeviceInfo deviceInfo = deviceList.get(0);
                        mTitleBar.setTitleBarContent(deviceInfo.deviceName);
                        DongConfiguration.mDeviceInfo = deviceInfo;
                    }
                } else {//2.5没有默认设备，选择第一台
                    DeviceInfo deviceInfo = deviceList.get(0);
                    mTitleBar.setTitleBarContent(deviceInfo.deviceName);
                    DongConfiguration.mDeviceInfo = deviceInfo;
                }

            } else if (DongConfiguration.mDeviceInfo != null) {
                mTitleBar.setTitleBarContent(DongConfiguration.mDeviceInfo.deviceName);
            } else {
                mTitleBar.setTitleBarContent(getString(R.string.no_device));
            }

            //向物业平台请求
            if ((DongConfiguration.mDeviceInfo != null) && (!mHasDeviceInfo
                    || mDeviceInfo != DongConfiguration.mDeviceInfo)) {//当用户登录并且没有去平台获取物业公告/设备变化后
                LogUtils.i("HomePagerFragment.clazz-->getBulletinFromNet start......");
                mHasDeviceInfo = true;
                getBulletinFromNet();
            }
            mDeviceInfo = DongConfiguration.mDeviceInfo;
        }
    }

    public static boolean mHasDeviceInfo;

    /**
     * 获取广告
     */
    private void getADFromNet() {
        ADViewPagerAdapter adViewPagerAdapter = new ADViewPagerAdapter(getActivity(), mADViewPager, mADPagerPoints);
        mADViewPager.setAdapter(adViewPagerAdapter);
        adViewPagerAdapter.showNoticeViewPoints();
        mADViewPager.setCurrentItem(Integer.MAX_VALUE / 2);
    }

    /**
     * 获取物业公告
     */
    private void getBulletinFromNet() {
        RequestParams params = getDVNotices(AppConfig.BASE_URL, DongConfiguration.mDeviceInfo.dwDeviceID, 0, 3);
        LogUtils.i("HomePagerFragment.clazz-->getBulletinFromNet()-->" +
                "DongConfiguration.mDeviceInfo.dwDeviceID:" + DongConfiguration.mDeviceInfo.dwDeviceID);

        ApiHttpClient.postDirect(AppConfig.BASE_URL, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    List<BulletinBean> netDataList = new ArrayList<>();
                    JSONObject receiveDataJson = new JSONObject(new String(responseBody));
                    LogUtils.i("HomePagerFragment.clazz-->getBulletinFromNet()-->receiveDataJson:" + receiveDataJson);
                    String resultCode = receiveDataJson.getString("result_code");
                    if (resultCode.equals("200")) {
                        String jsonInitData = receiveDataJson.getString("response_params");
                        if (jsonInitData.equals("[]")) {
                            return;
                        }
                        LogUtils.i("HomePagerFragment.clazz-->getBulletinFromNet()-->jsonInitData:" + jsonInitData);
                        JSONArray jsonArray = new JSONObject(jsonInitData).getJSONArray("villagenotices");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            BulletinBean bulletinBean = new BulletinBean();
                            bulletinBean.setTitle(jsonObject.getString("title"));
                            bulletinBean.setNotice(jsonObject.getString("notice"));
                            bulletinBean.setCreated(jsonObject.getString("created"));
                            bulletinBean.setDeviceId(String.valueOf(DongConfiguration.mDeviceInfo.dwDeviceID));
                            bulletinBean.setVillageId(jsonObject.getString("villageid"));
                            netDataList.add(bulletinBean);
                        }
                        processBulletinData(netDataList);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers,
                                  byte[] responseBody, Throwable error) {
                LogUtils.i("HomePagerFragment.clazz-->getBulletinFromNet()onFailure");
            }
        });
    }

    /**
     * 处理物业公告
     */
    private void processBulletinData(List<BulletinBean> netDataList) {
        List<BulletinBean> localList = BulletinOpe.queryAll(BaseApplication.context());
        LogUtils.i("HomePagerFragment.clazz--->>>processBulletinData localList" +
                " = BulletinOpe.size " + localList.size());
        String netVillageId = netDataList.get(0).getVillageId();
        String localVillageId = DeviceVillageOpe.queryDataByDeviceId(BaseApplication.context(),
                String.valueOf(DongConfiguration.mDeviceInfo.dwDeviceID));
        //如果数据库中没有就添加
        if (localVillageId == null) {
            LogUtils.i("HomePagerFragment.clazz--->>>processBulletinData()-->addVillage");
            DeviceVillageBean villageBean = new DeviceVillageBean();
            villageBean.setDeviceId(netDataList.get(0).getDeviceId());
            villageBean.setVillageId(netVillageId);
            DeviceVillageOpe.insertDataByVillageBean(BaseApplication.context(), villageBean);
        }
        for (BulletinBean netBean : netDataList) {
            boolean isSame = false;
            //2.1将本地数据库中的数据与平台返回数据对比
            for (BulletinBean localBean : localList) {//2.1.2如果发布时间和小区id相同，那么就认为是相同物业公告
                if (localBean.getCreated().equals(netBean.getCreated())
                        && localBean.getVillageId().equals(netBean.getVillageId()))
                    isSame = true;
            }
            //2.2不相同就添加到本地并且更新界面数据
            if (!isSame) {
                BulletinOpe.insert(BaseApplication.context(), netBean);
            }
        }
        mBulletinList.clear();
        mBulletinViewPagerPoints.removeAllViews();
        List<BulletinBean> newLocalList = BulletinOpe.queryDataByVillageId(
                BaseApplication.context(), netVillageId);
        LogUtils.i("HomeFragment.clazz--->>>processBulletinData" +
                " newLocalList.size " + newLocalList.size());
        //倒序排列(按时间)
        Collections.sort(newLocalList, Collections.reverseOrder());
        if (newLocalList.size() > 3) {
            for (int i = 0; i < 3; i++) {
                mBulletinList.add(newLocalList.get(i));
            }
        } else {
            for (BulletinBean bulletinBean : newLocalList) {
                mBulletinList.add(bulletinBean);
            }
        }
        mBulletinViewPagerAdapter.setBulletinData(mBulletinList);
    }

    /**
     * 显示物业公告
     */
    private void showBulletinInfo() {
        if (DongConfiguration.mDeviceInfo == null) {// 1.登录后没选择设备
            LogUtils.i("HomePagerFragment.clazz-->>showBulletinInfo " +
                    "DongConfiguration.mDeviceInfo:" + DongConfiguration.mDeviceInfo);
            mBulletinList.clear();
            mBulletinViewPagerPoints.removeAllViews();
            mBulletinViewPagerAdapter.hiddenADViewPoints();
            //mBulletinViewPager.setCurrentItem(0);
        } else {//2.已登录 判断设备是否有物业公告
            String villageId = DeviceVillageOpe.queryDataByDeviceId(BaseApplication.context(),
                    String.valueOf(DongConfiguration.mDeviceInfo.dwDeviceID));
            LogUtils.i("HomePagerFragment.clazz-->showBulletinInfo()-->villageId:" + villageId);
            if (!TextUtils.isEmpty(villageId)) {
                mBulletinList.clear();
                mBulletinViewPagerPoints.removeAllViews();
                List<BulletinBean> localBean = BulletinOpe.queryDataByVillageId(
                        BaseApplication.context(), villageId);
                //倒序排列(按时间)
                Collections.sort(localBean, Collections.reverseOrder());
                if (localBean.size() > 3) {
                    for (int i = 0; i < 3; i++) {
                        mBulletinList.add(localBean.get(i));
                    }
                } else {
                    if (localBean.size() == 1) {
                        mBulletinViewPagerPoints.removeAllViews();
                        mBulletinViewPagerAdapter.hiddenADViewPoints();
                    }
                    for (BulletinBean bulletinBean : localBean) {
                        mBulletinList.add(bulletinBean);
                    }
                }
                LogUtils.e("HomePagerFragment.clazz-->>showBulletinInfo Login mBulletinList.size:"
                        + mBulletinList.size() + ",localBean,size:" + localBean.size());
            } else {
                mBulletinList.clear();
                mBulletinViewPagerPoints.removeAllViews();
                mBulletinViewPagerAdapter.hiddenADViewPoints();
            }
        }
        mBulletinViewPagerAdapter.setBulletinData(mBulletinList);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //判断登录
        if (DongConfiguration.mUserInfo == null) {
            startActivity(new Intent(getActivity(), LoginActivity.class));
            return;
        }

        DynamicItemContainView dynamicView = (DynamicItemContainView) view;
        String name = dynamicView.getName();

        //判断有无设备
        if (DongConfiguration.mDeviceInfo == null) {
            if (!name.equals(getString(R.string.my_device))) {
                BaseApplication.showToastShortInCenter(R.string.no_device);
                return;
            }
        }

        if (name.equals(getString(R.string.message))) {
            UIHelper.showBulletinActivity(getActivity());
        } else if (name.equals(getString(R.string.monitor))) {
            //判断网络，只有在观看设备的时候需要网
            if (TDevice.getNetworkType() == 0) {
                TipDialogManager.showWithoutNetDialog(getActivity(), null);
                return;
            }
            //解决第一次进入首页设备信息没及时更新的问题
            ArrayList<DeviceInfo> deviceInfoList = DongSDKProxy.requestGetDeviceListFromCache();
            for (DeviceInfo deviceInfo : deviceInfoList) {
                if (DongConfiguration.mDeviceInfo.dwDeviceID == deviceInfo.dwDeviceID) {
                    DongConfiguration.mDeviceInfo = mDeviceInfo = deviceInfo;
                }
            }
            if (DongConfiguration.mDeviceInfo.isOnline) {
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
        } else if (name.equals(getString(R.string.my_device))) {
            UIHelper.showDeviceListActivity(getActivity());
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
//            UIHelper.showDeviceListActivity(getActivity());
            BaseApplication.showToastShortInCenter(R.string.join_device_list);
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
            LogUtils.i("HomePagerFragment.clazz--->>>OnAuthenticate.......showBulletinInfo.tInfo:" + tInfo);
            PushManager.getInstance().turnOnPush(HomePagerFragment.this.getActivity());
            com.baidu.android.pushservice.PushManager.resumeWork(HomePagerFragment.this.getActivity());
            DongSDKProxy.requestSetPushInfo(InfoPush.PUSHTYPE_FORCE_ADD);
            DongSDKProxy.requestGetDeviceListFromPlatform();
            // 设置物业公告信息
            showBulletinInfo();
            return 0;
        }

        @Override
        public int onLoginOtherPlace(String tip) {
            LogUtils.i("HomePageFragment.clazz -->>onLoginOtherPlace tip:" + tip);
            TipDialogManager.showOtherLoginDialog(getActivity(), tip);
            return 0;
        }

        @Override
        public int onNewListInfo() {
            LogUtils.e("HomePageFragment.clazz -->>OnNewListInfo");
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
            if (result != 0) {
                BaseApplication.showToastShortInCenter(R.string.openLockFail);
            } else {
                BaseApplication.showToastShortInCenter(R.string.openlock);
            }
            return 0;
        }

        @Override
        public int onUserError(int nErrNo) {
            LogUtils.i("HomePagerFragment.clazz--->>>OnUserError.....nErrNo:" + nErrNo);
            TipDialogManager.showTipDialog(HomePagerFragment.this.getActivity(),
                    BaseApplication.context().getString(R.string.tip), getLoginMessage(nErrNo, HomePagerFragment.this.getActivity()));
            return 0;
        }
    }
}
