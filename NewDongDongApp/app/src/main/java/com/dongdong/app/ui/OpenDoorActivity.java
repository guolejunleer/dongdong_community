package com.dongdong.app.ui;

import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.alibaba.fastjson.JSON;
import com.dd121.community.R;
import com.ddclient.configuration.DongConfiguration;
import com.dongdong.app.AppConfig;
import com.dongdong.app.adapter.OpenDoorAdapter;
import com.dongdong.app.api.ApiHttpClient;
import com.dongdong.app.base.BaseActivity;
import com.dongdong.app.base.BaseApplication;
import com.dongdong.app.bean.OpenDoorRecordBean;
import com.dongdong.app.bean.UserRoomBean;
import com.dongdong.app.db.OpenDoorOpe;
import com.dongdong.app.db.UserRoomOpe;
import com.dongdong.app.util.LogUtils;
import com.dongdong.app.util.ProcessDataUtils;
import com.dongdong.app.widget.TitleBar;
import com.dongdong.app.widget.TitleBar.OnTitleBarClickListener;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class OpenDoorActivity extends BaseActivity implements OnTitleBarClickListener,
        OnRefreshListener {
    private static final String JSON_OPEN_DOOR_RECORD_METHOD = "unlockrecords3";

    public static final String INTENT_OPEN_DOOR_RECORDER_BEAN = "OPEN_DOOR_RECORDER_BEAN";

    private static final int LOAD_NO_DATA = 1;
    private static final int DO_NOT_LOAD = 2;
    private static final int LOADING = 3;
    private static final int MAX_DATA_COUNT = 15;
    private static boolean mIsNoMoreData;

    private SwipeRefreshLayout mSwipeRefreshLayout;
    private List<OpenDoorRecordBean> mAdapterList = new ArrayList<>();
    private OpenDoorAdapter mOpenDoorAdapter;

    //上拉加载所需要的最小高度
    //private static float mUpDownloadNeedHeight;
    private boolean mIsLoading;
    private int mStartIndex = 0;
    final LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);

    @Override
    protected int getLayoutId() {
        return R.layout.activity_open_door;
    }

    @SuppressWarnings("deprecation")
    @Override
    public void initView() {
        RecyclerView rvOpenDoor = (RecyclerView) findViewById(R.id.rv_open_door_record);
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
        TitleBar titleBar = (TitleBar) this.findViewById(R.id.tb_title);
        titleBar.setTitleBarContent(getString(R.string.opendoor));
        titleBar.setOnTitleBarClickListener(this);
        titleBar.setAddArrowShowing(false);

        rvOpenDoor.setLayoutManager(mLayoutManager);
        rvOpenDoor.setItemAnimator(new DefaultItemAnimator());
        rvOpenDoor.addOnScrollListener(onOpenDoorScrollListener);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeResources(
                android.R.color.holo_blue_bright, android.R.color.holo_blue_bright,
                R.color.status_color, android.R.color.holo_blue_bright);

        mOpenDoorAdapter = new OpenDoorAdapter(OpenDoorActivity.this, mAdapterList);
        rvOpenDoor.setAdapter(mOpenDoorAdapter);
        mOpenDoorAdapter.setOnItemClickListener(onOpenDoorItemClickListener);
        mOpenDoorAdapter.changeLoadStatus(DO_NOT_LOAD);

        //        float density = TDevice.getDensity();
//        int titleBarHeight = (int) (density * getResources().getDimension(R.dimen.title_bard_height));
//
//        mUpDownloadNeedHeight = TDevice.getScreenHeight() - TDevice.getStatusBarHeight()
//                - titleBarHeight;
//
//        LogUtils.i("OpenDoorActivity.clazz-->titleBarHeight:"
//                + titleBarHeight + ",density:" + density
//                + ",TDevice.getScreenHeight():" + TDevice.getScreenHeight());
    }

    @Override
    public void initData() {
        //通过userId获取用户房间关系
        List<UserRoomBean> userRoomBeanList = UserRoomOpe.queryDataByUserIdAndDevId(
                BaseApplication.context(), DongConfiguration.mUserInfo.userID,
                DongConfiguration.mDeviceInfo.dwDeviceID);
        LogUtils.i("OpenDoorActivity.clazz->initData()->userRoomBeanListSize:"
                + userRoomBeanList.size());

        if (userRoomBeanList.size() > 0) {//用户下有房号信息
            mAdapterList.clear();
            for (UserRoomBean userRoomBean : userRoomBeanList) {
                //通过roomId获取开门记录
                List<OpenDoorRecordBean> openDoorRecordBeanList = OpenDoorOpe.queryAllByRoomId(
                        BaseApplication.context(), userRoomBean.getRoomId());
                LogUtils.i("OpenDoorActivity.clazz->initData()->openDoorRecordBeanList:"
                        + openDoorRecordBeanList);
                if (openDoorRecordBeanList.size() > 0) {
                    for (OpenDoorRecordBean openDoorRecordBean : openDoorRecordBeanList) {
                        mAdapterList.add(openDoorRecordBean);
                    }
                } else {
                    //本地无数据就下拉刷新
                    getDataFromNet(mStartIndex);
                }
            }
            notifyDataSetChanged();
        } else {
            //本地无数据就下拉刷新
            getDataFromNet(mStartIndex);
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
    RecyclerView.OnScrollListener onOpenDoorScrollListener = new RecyclerView.OnScrollListener() {

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
                    LogUtils.i("OpenDoorActivity.clazz-->onScrollStateChanged isRefreshing!!!");
                    mOpenDoorAdapter.notifyItemRemoved(mOpenDoorAdapter.getItemCount());
                    return;
                }
                if (mIsNoMoreData) {
                    LogUtils.i("OpenDoorActivity.clazz-->onScrollStateChanged isNoMoreData!!!");
                    mOpenDoorAdapter.changeLoadStatus(LOAD_NO_DATA);
                    return;
                }
                if (!mIsLoading) {//下拉加载
                    LogUtils.i("OpenDoorActivity.clazz-->onScrollStateChanged is down upload!!!");
                    mIsLoading = true;
                    mOpenDoorAdapter.changeLoadStatus(LOADING);
                    getDataFromNet(mStartIndex += MAX_DATA_COUNT);
                    LogUtils.i("OpenDoorActivity.clazz-->onScrollStateChanged mStartIndex:" + mStartIndex);
                }
            }
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
        }
    };

    /**
     * 开门记录点击事件
     */
    OpenDoorAdapter.OnItemClickListener onOpenDoorItemClickListener =
            new OpenDoorAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    OpenDoorRecordBean openDoorRecordBean = mAdapterList.get(position);
                    Intent intent = new Intent(OpenDoorActivity.this, OpenDoorDetailActivity.class);
                    intent.putExtra(INTENT_OPEN_DOOR_RECORDER_BEAN, openDoorRecordBean);
                    startActivity(intent);
                }
            };

    /**
     * 向物业平台请求开门记录数据
     *
     * @param startIndex 从第几条开始取数据
     */
    public void getDataFromNet(int startIndex) {
        LogUtils.i("OpenDoorActivity.clazz-->getDataOpenDoorRecord()-->" +
                "deviceId:" + DongConfiguration.mDeviceInfo.dwDeviceID +
                ",userId:" + DongConfiguration.mUserInfo.userID + ",startIndex:" + startIndex);

        // 1.获取开门记录参数(startIndex:开始位置 endIndex:加载数据条数)
        RequestParams params = ApiHttpClient.getUnlockRecords3(AppConfig.BASE_URL,
                DongConfiguration.mUserInfo.userID,
                DongConfiguration.mDeviceInfo.dwDeviceID,
                startIndex, MAX_DATA_COUNT);
        // 2.发送获取开门记录请求
        ApiHttpClient.postDirect(AppConfig.BASE_URL, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                mIsLoading = false;
                setSwipeRefreshLoadedState();
                try {
                    JSONObject receiveDataJson = new JSONObject(new String(responseBody));
                    LogUtils.i("OpenDoorActivity.clazz-->getDataOpenDoorRecord()-->" +
                            "receiveDataJson:" + receiveDataJson);
                    String resultCode = receiveDataJson.getString(AppConfig.JSON_RESULT_CODE);
                    if (resultCode.equals(AppConfig.JSON_CORRECT_RESULT_CODE)) {
                        String jsonInitData = new JSONObject(new String(responseBody)).
                                getString(AppConfig.JSON_RESPONSE_PARAMS);
                        String jsonData = new JSONObject(jsonInitData).
                                getString(JSON_OPEN_DOOR_RECORD_METHOD);
                        if (jsonData.equals(AppConfig.JSON_EMPTY_DATA)) {
                            //2.1这里只会在平台没有数据了时候才会进来
                            mIsNoMoreData = true;
                            mOpenDoorAdapter.changeLoadStatus(LOAD_NO_DATA);
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
                LogUtils.i("OpenDoorActivity.clazz-->getDataOpenDoorRecord()onFailure");
                mIsLoading = false;
                setSwipeRefreshLoadedState();
                mOpenDoorAdapter.changeLoadStatus(OpenDoorAdapter.LOAD_DATA_FAILED);
                BaseApplication.showToastShortInCenter(R.string.get_data_failed);
            }
        });
    }

    /**
     * 开门记录数据改变
     */
    public void notifyDataSetChanged() {
        //倒序排列(按时间)
        Collections.sort(mAdapterList, Collections.reverseOrder());
        mOpenDoorAdapter.notifyDataSetChanged();
    }

    /**
     * 本地开门记录的删除
     *
     * @param localData 本地开门记录数据
     */
    public void deleteLocal(List<OpenDoorRecordBean> localData) {
        int count = localData.size() - AppConfig.MAX_OPEN_DOOR_RECORD_COUNT;
        if (count > 0) {
            List<Long> doorRecordIndex = new ArrayList<>();
            for (int i = 0; i < count; i++) {
                long doorRecordId = localData.get(i).getId();
                LogUtils.i("OpenDoorActivity.clazz-->deleteDoorRecord()-->" +
                        "doorRecordId:" + doorRecordId);
                doorRecordIndex.add(doorRecordId);
            }
            //删除最开始保存的（相较而言时间早一点的）
            OpenDoorOpe.delete(BaseApplication.context(), doorRecordIndex);
        }
    }

    /**
     * 处理用户与房号的关系
     *
     * @param netDataList 新的数据
     */
    private boolean processUserRoom(List<OpenDoorRecordBean> netDataList) {
        //获取所有roomId
        HashSet<String> hashSet = new HashSet<>();
        for (OpenDoorRecordBean openDoorRecordBean : netDataList) {
            hashSet.add(openDoorRecordBean.getRoomId());
        }
        LogUtils.i("OpenDoorActivity.clazz-->processUserRoom hashSet:"
                + hashSet + ",netDataList.size:" + netDataList.size());
        //通过userId和deviceId获取
        List<UserRoomBean> userRoomBeen = UserRoomOpe.queryDataByUserIdAndDevId(
                BaseApplication.context(), DongConfiguration.mUserInfo.userID,
                DongConfiguration.mDeviceInfo.dwDeviceID);

        LogUtils.i("OpenDoorActivity.clazz-->processUserRoom userRoomBeen:"
                + userRoomBeen);
        if (userRoomBeen == null || userRoomBeen.size() == 0) {
            //1 先在界面显示
            for (OpenDoorRecordBean openDoorRecordBean : netDataList) {
                mAdapterList.add(openDoorRecordBean);
            }
            notifyDataSetChanged();

            //2 将userId插入数据库
            for (String roomId : hashSet) {
                UserRoomBean userRoomBean = new UserRoomBean();
                userRoomBean.setDeviceId(DongConfiguration.mDeviceInfo.dwDeviceID);
                userRoomBean.setUserId(DongConfiguration.mUserInfo.userID);
                userRoomBean.setRoomId(Integer.parseInt(roomId));
                UserRoomOpe.insertDataByUserRoomBean(BaseApplication.context(), userRoomBean);
            }
            return true;
        }
        return false;
    }

//        LogUtils.i("OpenDoorActivity.clazz->processUserRoom()->hashSet:" + hashSet);
//        UserRoomBean userRoomBean = new UserRoomBean();
//        userRoomBean.setDeviceId(DongConfiguration.mDeviceInfo.dwDeviceID);
//        userRoomBean.setUserId(DongConfiguration.mUserInfo.userID);
//
//        for (String roomId : hashSet) {
//            //查询对应本地数据
//            List<UserRoomBean> userRoomBeanList = UserRoomOpe.queryDataByRoomId(
//                    BaseApplication.context(), Integer.parseInt(roomId));
//            if (userRoomBeanList.size() > 0) {//有对应关系
//                for (UserRoomBean userRoom : userRoomBeanList) {
//                    if (userRoom.getUserId() != DongConfiguration.mUserInfo.userID) {
//                        userRoomBean.setRoomId(Integer.parseInt(roomId));
//                        UserRoomOpe.insertDataByUserRoomBean(BaseApplication.context(), userRoomBean);
//                    }
//                }
//            } else {//无对应关系
//                userRoomBean.setRoomId(Integer.parseInt(roomId));
//                UserRoomOpe.insertDataByUserRoomBean(BaseApplication.context(), userRoomBean);
//            }
//        }

    /**
     * 处理开门记录JSON数据
     *
     * @param jsonData 开门记录json数据
     */
    public void processJsonData(String jsonData) {
        //1.获取本地数据
        List<OpenDoorRecordBean> localList = OpenDoorOpe.queryAllDesc(BaseApplication.context());
        List<OpenDoorRecordBean> netDataList = JSON.parseArray(jsonData, OpenDoorRecordBean.class);
        //处理用户与房号的关系
        boolean isFirstLoad = processUserRoom(netDataList);
        LogUtils.i("OpenDoorActivity.clazz-->processJsonData()->isFirstLoad:" + isFirstLoad);
        //2对比本地和平台数据
        boolean isAllSame = localList.containsAll(netDataList);
        LogUtils.i("OpenDoorActivity.clazz-->processJsonData()->isAllSame:" + isAllSame);

        LogUtils.i("OpenDoorActivity.clazz-->processJsonData()->mStartIndex:" + mStartIndex);
        if (isAllSame) {//2.1如果本地数据库包含下拉刷新获取到的最新数据，那么返回
            if (mStartIndex != 0 && mStartIndex > localList.size()) {//2.1.1上拉加载发现本地有平台数据
                mIsNoMoreData = true;
                return;
            } else if (mStartIndex != 0 && mStartIndex < localList.size()) {
                LogUtils.i("OpenDoorActivity.clazz-->processData notifyItemRemoved mStartIndex:"
                        + mStartIndex);
                mOpenDoorAdapter.changeLoadStatus(OpenDoorAdapter.LOAD_NO_DATA);
            } else {
                BaseApplication.showToastShortInBottom(R.string.is_the_latest_data);
            }
        } else {
            for (OpenDoorRecordBean netBean : netDataList) {
                boolean isSame = false;
                netBean.setType(ProcessDataUtils.openDoorType(Integer.parseInt(netBean.getType())));
                //2.1.1将本地数据库中的数据与平台返回数据对比
                for (OpenDoorRecordBean localBean : localList) {
                    if (localBean.getTimestamp().trim().equals(netBean.getTimestamp().trim()) &&
                            localBean.getRoomId().trim().equals(netBean.getRoomId().trim()))
                        isSame = true;
                }
                //2.1.2不相同就添加到本地并且更新界面数据
                if (!isSame) {
                    if (!isFirstLoad) {
                        mAdapterList.add(netBean);
                    }
                    OpenDoorOpe.insert(BaseApplication.context(), netBean);
                }
            }
        }
        mIsNoMoreData = netDataList.size() < MAX_DATA_COUNT;
        if (mIsNoMoreData) mOpenDoorAdapter.changeLoadStatus(OpenDoorAdapter.LOAD_NO_DATA);
        List<OpenDoorRecordBean> newLocalList = OpenDoorOpe.queryAllAsc(BaseApplication.context());
        LogUtils.i("OpenDoorActivity.clazz-->processJsonData() mAdapterList.size:" +
                mAdapterList.size() + ",newLocalList.size:" + newLocalList.size()
                + ",netDataList.size():" + netDataList.size() + ",mIsNoMoreData:" + mIsNoMoreData);
        //删除本地数据
        deleteLocal(newLocalList);
        notifyDataSetChanged();
    }

    /**
     * 开门记录下拉刷新(下拉刷新过程中不能继续下拉)
     */
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
        OpenDoorActivity.this.finish();
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
