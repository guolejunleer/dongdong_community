package com.dongdong.app.ui;

import android.content.Intent;
import android.net.Uri;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.alibaba.fastjson.JSON;
import com.dd121.community.R;
import com.ddclient.configuration.DongConfiguration;
import com.dongdong.app.AppConfig;
import com.dongdong.app.adapter.CommonPhoneAdapter;
import com.dongdong.app.adapter.OpenDoorAdapter;
import com.dongdong.app.api.ApiHttpClient;
import com.dongdong.app.base.BaseActivity;
import com.dongdong.app.base.BaseApplication;
import com.dongdong.app.bean.CommonPhoneBean;
import com.dongdong.app.bean.DevicePhoneBean;
import com.dongdong.app.bean.OpenDoorRecordBean;
import com.dongdong.app.db.CommonPhoneOpe;
import com.dongdong.app.db.DevicePhoneOpe;
import com.dongdong.app.ui.dialog.TipDialogManager;
import com.dongdong.app.util.LogUtils;
import com.dongdong.app.widget.TitleBar;
import com.dongdong.app.widget.TitleBar.OnTitleBarClickListener;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class CommonPhoneActivity extends BaseActivity implements OnTitleBarClickListener,
        OnRefreshListener {
    private static final String JSON_COMMON_PHONE_METHOD = "comphones";

    private static final int LOAD_NO_DATA = 1;
    private static final int DO_NOT_LOAD = 2;
    private static final int LOADING = 3;
    private static final int MAX_DATA_COUNT = 10; //每次最多10个
    private static boolean mIsNoMoreData;

    private SwipeRefreshLayout mSwipeRefreshLayout;
    private List<CommonPhoneBean> mAdapterList = new ArrayList<>();
    private CommonPhoneAdapter mCommonPhoneAdapter;

    //上拉加载所需要的最小高度
    //private static float mUpDownloadNeedHeight;
    private boolean mIsLoading;
    private int mStartIndex = 0;
    final LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);

    @Override
    protected int getLayoutId() {
        return R.layout.activity_common_phone;
    }

    @Override
    public void initView() {
        RecyclerView rvCommonPhone = (RecyclerView) findViewById(R.id.rv_common_phone);
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
        TitleBar titleBar = (TitleBar) this.findViewById(R.id.tb_title);
        titleBar.setTitleBarContent(getString(R.string.phone));
        titleBar.setOnTitleBarClickListener(this);
        titleBar.setAddArrowShowing(false);

        rvCommonPhone.setLayoutManager(mLayoutManager);
        rvCommonPhone.setItemAnimator(new DefaultItemAnimator());
        rvCommonPhone.addOnScrollListener(onCommonPhoneScrollListener);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeResources(
                android.R.color.holo_blue_bright, android.R.color.holo_blue_bright,
                R.color.status_color, android.R.color.holo_blue_bright);

        mCommonPhoneAdapter = new CommonPhoneAdapter(CommonPhoneActivity.this, mAdapterList);
        rvCommonPhone.setAdapter(mCommonPhoneAdapter);
        mCommonPhoneAdapter.setOnItemClickListener(new CommonPhoneAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                //准备打电话
                callUser(mAdapterList.get(position));
            }
        });
        mCommonPhoneAdapter.changeLoadStatus(DO_NOT_LOAD);

//        float density = TDevice.getDensity();
//        int titleBarHeight = (int) (density * getResources().getDimension(R.dimen.title_bard_height));
//        mUpDownloadNeedHeight = TDevice.getScreenHeight() - TDevice.getStatusBarHeight()- titleBarHeight;
//        LogUtils.i("OpenDoorActivity.clazz-->titleBarHeight:"+ titleBarHeight + ",density:" + density
//                + ",TDevice.getScreenHeight():" + TDevice.getScreenHeight());
    }

    @Override
    public void initData() {
        //查询对应关系（根据deviceId）
        List<DevicePhoneBean> phoneIdList = DevicePhoneOpe.queryAllByDeviceId(
                BaseApplication.context(), DongConfiguration.mDeviceInfo.dwDeviceID);
//        LogUtils.i("CommonPhoneActivity.clazz-->initData()->phoneIdList:" + phoneIdList);
        if (phoneIdList.size() > 0) {//本地有对应关系
            for (DevicePhoneBean devicePhoneBean : phoneIdList) {
                List<CommonPhoneBean> commonPhoneBeanList = CommonPhoneOpe.queryAllByPhoneId(
                        BaseApplication.context(), devicePhoneBean.getPhoneId());
                for (CommonPhoneBean commonPhoneBean : commonPhoneBeanList) {
                    mAdapterList.add(commonPhoneBean);
                }
            }
            mCommonPhoneAdapter.notifyDataSetChanged();
        } else {//本地无对应关系
            getDataFromNet(0);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        ApiHttpClient.cancelAll(BaseApplication.context());
    }

    /**
     * RecyclerView监听事件
     */
    RecyclerView.OnScrollListener onCommonPhoneScrollListener = new RecyclerView.OnScrollListener() {

        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);

            /**
             * computeVerticalScrollExtent()是当前recyclerView显示的区域高度
             * computeVerticalScrollOffset() 是当前recyclerView之前滑过的距离
             * computeVerticalScrollRange()是整个recyclerView控件的高度
             */
            int screenShowH = recyclerView.computeVerticalScrollExtent();
            int scrollOffset = recyclerView.computeVerticalScrollOffset();
            int viewH = recyclerView.computeVerticalScrollRange();
//            LogUtils.i("OpenDoorActivity.clazz-->onScrollStateChanged screenShowH:" + screenShowH +
//                    ",scrollOffset:" + scrollOffset + ",viewH:" + viewH + ",mUpDownloadNeedHeight:" + mUpDownloadNeedHeight);
//            if (screenShowH < mUpDownloadNeedHeight) {//数据不足一屏幕，那么不让上拉加载
//                LogUtils.i("OpenDoorActivity.clazz-->return onScrollStateChanged screenShowH:"
//                        + screenShowH + ",mUpDownloadNeedHeight:" + mUpDownloadNeedHeight);
//                return;
//            }
            if (screenShowH + scrollOffset >= viewH) {
                boolean isRefreshing = mSwipeRefreshLayout.isRefreshing();
                if (isRefreshing) {
                    LogUtils.i("CommonPhoneActivity.clazz-->onScrollStateChanged isRefreshing!!!");
                    mCommonPhoneAdapter.notifyItemRemoved(mCommonPhoneAdapter.getItemCount());
                    return;
                }
                if (mIsNoMoreData) {
                    LogUtils.i("CommonPhoneActivity.clazz-->onScrollStateChanged isNoMoreData!!!");
                    mCommonPhoneAdapter.changeLoadStatus(LOAD_NO_DATA);
                    return;
                }
                if (!mIsLoading) {//下拉加载
                    LogUtils.i("CommonPhoneActivity.clazz-->onScrollStateChanged is down upload!!!");
                    mIsLoading = true;
                    mCommonPhoneAdapter.changeLoadStatus(LOADING);
                    getDataFromNet(mStartIndex += MAX_DATA_COUNT);
                    LogUtils.i("CommonPhoneActivity.clazz-->onScrollStateChanged mStartIndex:" + mStartIndex);
                }
            }
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
        }
    };

    //拨打电话提示
    public void callUser(final CommonPhoneBean commonPhoneBean) {
        TipDialogManager.showNormalTipDialog(this, new TipDialogManager.OnTipDialogButtonClick() {
                    @Override
                    public void onPositiveButtonClick() {
                        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" +
                                commonPhoneBean.getPhoneNumber()));
                        startActivity(intent);
                    }

                    @Override
                    public void onNegativeButtonClick() {
                    }
                }, getString(R.string.tip), getString(R.string.call_user, commonPhoneBean.getDepartment()),
                getString(R.string.yes), getString(R.string.no));
    }

    /**
     * 向物业平台请求常用电话数据
     *
     * @param startIndex 从第几条开始取数据
     */
    public void getDataFromNet(int startIndex) {
        LogUtils.i("CommonPhoneActivity.clazz-->getDataCommonPhone()-->" +
                "deviceId:" + DongConfiguration.mDeviceInfo.dwDeviceID + ",startIndex:" + startIndex +
                ",AppConfig.BASE_URL" + AppConfig.BASE_URL);

        // 1.获取常用电话参数(startIndex:开始位置 endIndex:加载数据条数)
        RequestParams params = ApiHttpClient.getDVComphones(AppConfig.BASE_URL,
                DongConfiguration.mDeviceInfo.dwDeviceID, startIndex, MAX_DATA_COUNT);
        // 2.发送获取常用电话请求
        ApiHttpClient.postDirect(AppConfig.BASE_URL, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                mIsLoading = false;
                setSwipeRefreshLoadedState();
                try {
                    JSONObject receiveDataJson = new JSONObject(new String(responseBody));
                    LogUtils.i("CommonPhoneActivity.clazz-->getDataCommonPhone()-->" +
                            "receiveDataJson:" + receiveDataJson);
                    String resultCode = receiveDataJson.getString(AppConfig.JSON_RESULT_CODE);
                    if (resultCode.equals(AppConfig.JSON_CORRECT_RESULT_CODE)) {
                        String jsonInitData = new JSONObject(new String(responseBody)).
                                getString(AppConfig.JSON_RESPONSE_PARAMS);
                        String jsonData = new JSONObject(jsonInitData).getString(JSON_COMMON_PHONE_METHOD);
                        if (jsonData.equals(AppConfig.JSON_EMPTY_DATA)) {//2.1这里只会在平台没有数据了时候才会进来
                            mIsNoMoreData = true;
                            mCommonPhoneAdapter.changeLoadStatus(LOAD_NO_DATA);
//                            if (mStartIndex == 0)
//                                BaseApplication.showToastShortInBottom(R.string.is_the_latest_data);
                            return;
                        }
                        processJsonData(jsonData);//2.1平台数据处理
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    mIsLoading = false;
                    setSwipeRefreshLoadedState();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers,
                                  byte[] responseBody, Throwable error) {
                LogUtils.i("CommonPhoneActivity.clazz-->getDataOpenDoorRecord()->onFailure");
                mIsLoading = false;
                setSwipeRefreshLoadedState();
                mCommonPhoneAdapter.changeLoadStatus(OpenDoorAdapter.LOAD_DATA_FAILED);
                BaseApplication.showToastShortInCenter(R.string.get_data_failed);
            }
        });
    }

    //处理设备与常用电话的关系
    private boolean processDevicePhone(List<CommonPhoneBean> netDataList) {
        //获取所有关于此设备的所有电话关系
        List<DevicePhoneBean> devicePhoneBeanList = DevicePhoneOpe.queryAllByDeviceId(
                BaseApplication.context(), DongConfiguration.mDeviceInfo.dwDeviceID);

        HashSet<String> localPhoneId = new HashSet<>();
        for (DevicePhoneBean devicePhoneBean : devicePhoneBeanList) {
            localPhoneId.add(devicePhoneBean.getPhoneId());
        }
        if (devicePhoneBeanList.size() > 0) {//本地有关系,网络所得遍历本地所有
            for (CommonPhoneBean commonPhoneBean : netDataList) {//拿出网络所有phoneId
                if (!localPhoneId.contains(commonPhoneBean.getPhoneId())) {
                    DevicePhoneBean devicePhoneBean = new DevicePhoneBean();
                    devicePhoneBean.setDeviceId(String.valueOf(DongConfiguration.mDeviceInfo.dwDeviceID));
                    devicePhoneBean.setPhoneId(commonPhoneBean.getPhoneId());
                    DevicePhoneOpe.insert(BaseApplication.context(), devicePhoneBean);
                }
            }
        } else {//本地没有关系
            for (CommonPhoneBean commonPhoneBean : netDataList) {
                //1 先在界面显示
                mAdapterList.add(commonPhoneBean);

                DevicePhoneBean devicePhoneBean = new DevicePhoneBean();
                devicePhoneBean.setDeviceId(String.valueOf(DongConfiguration.mDeviceInfo.dwDeviceID));
                devicePhoneBean.setPhoneId(commonPhoneBean.getPhoneId());
                DevicePhoneOpe.insert(BaseApplication.context(), devicePhoneBean);
            }
            mCommonPhoneAdapter.notifyDataSetChanged();
            return true;
        }
        return false;
    }

    /**
     * 处理常用电话JSON数据
     *
     * @param jsonData 常用电话JSON数据
     */
    public void processJsonData(String jsonData) {
        //1.获取本地数据
        List<CommonPhoneBean> localList = CommonPhoneOpe.queryAll(BaseApplication.context());
        List<CommonPhoneBean> netDataList = JSON.parseArray(jsonData, CommonPhoneBean.class);
        //处理设备与常用电话的关系
        boolean isFirstLoad = processDevicePhone(netDataList);
        LogUtils.i("CommonPhoneActivity.clazz-->processJsonData()->isFirstLoad:" + isFirstLoad);
        //2.对比本地和平台数据
        boolean isAllSame = localList.containsAll(netDataList);
        LogUtils.e("CommonPhoneActivity.clazz-->processJsonData()->isAllSame:" + isAllSame);

        LogUtils.i("CommonPhoneActivity.clazz-->processJsonData()->mStartIndex:" + mStartIndex);
        if (isAllSame) {//2.1如果本地数据库包含下拉刷新获取到的最新数据，那么返回
            if (mStartIndex != 0 && mStartIndex > localList.size()) {//2.1.1上拉加载发现本地有平台数据
                mIsNoMoreData = true;
                return;
            } else if (mStartIndex != 0 && mStartIndex < localList.size()) {
                LogUtils.i("CommonPhoneActivity.clazz-->processJsonData notifyItemRemoved mStartIndex:"
                        + mStartIndex);
                mCommonPhoneAdapter.changeLoadStatus(OpenDoorAdapter.LOAD_NO_DATA);
            }
//            else {
//                BaseApplication.showToastShortInBottom(R.string.is_the_latest_data);
//            }
        } else {
            for (CommonPhoneBean netBean : netDataList) {
                boolean isSame = false;
                //2.1.1将本地数据库中的数据与平台返回数据对比
                for (CommonPhoneBean localBean : localList) {
                    if (localBean.getPhoneId().trim().equals(netBean.getPhoneId().trim()))
                        isSame = true;
                }
                //2.1.2不相同就添加到本地并且更新界面数据
                if (!isSame) {
                    if (!isFirstLoad) {
                        mAdapterList.add(netBean);
                    }
                    CommonPhoneOpe.insertDataByCommonPhoneBean(BaseApplication.context(), netBean);
                }
            }
        }
        mIsNoMoreData = netDataList.size() < MAX_DATA_COUNT;
        if (mIsNoMoreData) mCommonPhoneAdapter.changeLoadStatus(OpenDoorAdapter.LOAD_NO_DATA);
        List<CommonPhoneBean> newLocalList = CommonPhoneOpe.queryAll(BaseApplication.context());
        LogUtils.i("OpenDoorActivity.clazz-->processJsonData() mAdapterList.size:" +
                mAdapterList.size() + ",newLocalList.size:" + newLocalList.size()
                + ",netDataList.size():" + netDataList.size() + ",mIsNoMoreData:" + mIsNoMoreData);
        //删除本地数据
//      deleteLocal(newLocalList);
        mCommonPhoneAdapter.notifyDataSetChanged();
    }


    @Override
    public void onRefresh() {
        mStartIndex = 0;
        setSwipeRefreshLoadingState();
        getDataFromNet(0);
    }

    /**
     * 设置顶部正在加载的状态
     */
    protected void setSwipeRefreshLoadingState() {
        if (mSwipeRefreshLayout != null) {
            mSwipeRefreshLayout.setRefreshing(true);
            // 防止多次重复刷新
            mSwipeRefreshLayout.setEnabled(false);
        }
    }

    /**
     * 设置顶部加载完毕的状态
     */
    protected void setSwipeRefreshLoadedState() {
        if (mSwipeRefreshLayout != null) {
            mSwipeRefreshLayout.setRefreshing(false);
            mSwipeRefreshLayout.setEnabled(true);
        }
    }

    @Override
    public void onBackClick() {
        CommonPhoneActivity.this.finish();
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
}
