package com.dongdong.app.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.dd121.community.R;
import com.ddclient.configuration.DongConfiguration;
import com.ddclient.dongsdk.AbstractDongCallbackProxy;
import com.ddclient.dongsdk.DongSDKProxy;
import com.ddclient.jnisdk.InfoDownloadUrl;
import com.ddclient.jnisdk.InfoUser;
import com.dongdong.app.AppConfig;
import com.dongdong.app.adapter.VisitorPhotoAdapter;
import com.dongdong.app.base.BaseActivity;
import com.dongdong.app.base.BaseApplication;
import com.dongdong.app.bean.VisitorPhotoBean;
import com.dongdong.app.cache.CacheHelper;
import com.dongdong.app.db.VisitorPhotoOpe;
import com.dongdong.app.util.LogUtils;
import com.dongdong.app.util.ProcessDataUtils;
import com.dongdong.app.widget.TitleBar;
import com.dongdong.app.widget.TitleBar.OnTitleBarClickListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class VisitorPhotoActivity extends BaseActivity implements OnTitleBarClickListener {
    public static final String INTENT_VISITOR_PHOTO_BEAN = "VISITOR_PHOTO_BEAN";

    private static final int LOAD_NO_DATA = 1;
    private static final int DO_NOT_LOAD = 2;
    private static final int LOADING = 3;
    private static boolean mIsNoMoreData;

    private static final int MAX_DATA_COUNT = 7;

    private SwipeRefreshLayout mSwipeRefreshLayout;
    private List<VisitorPhotoBean> mAdapterList = new ArrayList<>();
    public static VisitorPhotoAdapter mVisitorPhotoAdapter;
    private CacheHelper mCacheHelper = new CacheHelper();

    //上拉加载所需要的最小高度
//  private static float mUpDownloadNeedHeight;
    private static boolean mIsLoading;
    private int mStartIndex = 0;
    final LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);

    private VisitorRecordsActivityDongAccountProxy mAccountProxy = new
            VisitorRecordsActivityDongAccountProxy();

    @Override
    protected int getLayoutId() {
        return R.layout.activity_visitor_photo;
    }

    @SuppressWarnings("deprecation")
    @Override
    public void initView() {
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
        RecyclerView rvVisitorPhoto = (RecyclerView) findViewById(R.id.rv_visitor_photo);
        TitleBar titleBar = (TitleBar) this.findViewById(R.id.tb_title);

        titleBar.setTitleBarContent(getString(R.string.visitorphoto));
        titleBar.setOnTitleBarClickListener(this);
        titleBar.setAddArrowShowing(false);

        rvVisitorPhoto.setLayoutManager(mLayoutManager);
        rvVisitorPhoto.addOnScrollListener(onVisitorPhotoScrollListener);
        mSwipeRefreshLayout.setOnRefreshListener(new OnVisitorPhotoListener());
        mSwipeRefreshLayout.setColorSchemeResources(
                android.R.color.holo_blue_bright, android.R.color.holo_blue_bright,
                R.color.status_color, android.R.color.holo_blue_bright);

        mVisitorPhotoAdapter = new VisitorPhotoAdapter(VisitorPhotoActivity.this, mAdapterList,
                rvVisitorPhoto);
        rvVisitorPhoto.setAdapter(mVisitorPhotoAdapter);
        mVisitorPhotoAdapter.setOnItemClickListener(onVisitorPhotoItemClickListener);
        mVisitorPhotoAdapter.changeLoadStatus(DO_NOT_LOAD);

//        float density = TDevice.getDensity();
//        int titleBarHeight = (int) (density * getResources().getDimension(R.dimen.title_bard_height));
//        mUpDownloadNeedHeight = TDevice.getScreenHeight() - TDevice.getStatusBarHeight()
//                - titleBarHeight;
//
//        LogUtils.i("VisitorPhotoActivity.clazz-->titleBarHeight:"
//                + titleBarHeight + ",density:" + density
//                + ",TDevice.getScreenHeight():" + TDevice.getScreenHeight());
    }

    @Override
    public void initData() {
        //1查询本地数据库
        List<VisitorPhotoBean> localDataList = VisitorPhotoOpe.queryDataByUserIdAndDevId(
                BaseApplication.context(), DongConfiguration.mUserInfo.userID,
                DongConfiguration.mDeviceInfo.dwDeviceID);
        if (localDataList.size() > 0) {//1.1有本地数据，先在界面显示
            mAdapterList.clear();
            for (VisitorPhotoBean localBean : localDataList) {
                mAdapterList.add(localBean);
            }
            notifyDataSetChanged();
        } else {
            //向云平台请求访客留影
            DongSDKProxy.requestGetDownloadUrlsWithParams(DongConfiguration.mDeviceInfo.dwDeviceID,
                    0, MAX_DATA_COUNT);
        }
        mTimer = new Timer();
        mShouldStop = false;
        mTimeCount = 0;
        mTimer.schedule(mTask, new Date(), 1000);
        LogUtils.i("VisitorPhotoActivity.clazz-->requestGetDownloadUrlsWithParams-->startIndex:" + mStartIndex);
    }

    /**
     * RecyclerView监听事件
     */
    RecyclerView.OnScrollListener onVisitorPhotoScrollListener = new RecyclerView.OnScrollListener() {
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
//            LogUtils.i("VisitorPhotoActivity.clazz-->onScrollStateChanged screenShowH:" + screenShowH +
//                    ",scrollOffset:" + scrollOffset + ",viewH:" + viewH + ",mUpDownloadNeedHeight:" + mUpDownloadNeedHeight);
//            if (screenShowH < mUpDownloadNeedHeight) {//数据不足一屏幕，那么不让上拉加载
//                LogUtils.i("VisitorActivity.clazz-->return onScrollStateChanged screenShowH:"
//                        + screenShowH + ",mUpDownloadNeedHeight:" + mUpDownloadNeedHeight);
//                return;
//            }
            if (screenShowH + scrollOffset >= viewH) {
                boolean isRefreshing = mSwipeRefreshLayout.isRefreshing();

                if (isRefreshing) {
                    LogUtils.i("VisitorPhotoActivity.clazz-->onScrollStateChanged isRefreshing!!!");
                    mVisitorPhotoAdapter.notifyItemRemoved(mVisitorPhotoAdapter.getItemCount());
                    return;
                }
                if (mIsNoMoreData) {
                    LogUtils.i("VisitorPhotoActivity.clazz-->onScrollStateChanged isNoMoreData!!!");
                    mVisitorPhotoAdapter.changeLoadStatus(LOAD_NO_DATA);
                    return;
                }
                if (!mIsLoading) {//下拉加载
                    LogUtils.i("VisitorPhotoActivity.clazz-->onScrollStateChanged down upload!!!");
                    mVisitorPhotoAdapter.changeLoadStatus(LOADING);
                    mIsLoading = true;
                    mShouldStop = false;
                    mTimeCount = 0;
                    DongSDKProxy.requestGetDownloadUrlsWithParams(
                            DongConfiguration.mDeviceInfo.dwDeviceID,
                            mStartIndex += MAX_DATA_COUNT, MAX_DATA_COUNT);
                    LogUtils.i("VisitorPhotoActivity.clazz-->onScrollStateChanged mStartIndex:" + mStartIndex);
                }
            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        DongSDKProxy.registerAccountCallback(mAccountProxy);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mVisitorPhotoAdapter.flushCache();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 退出程序时结束所有的下载任务
        mVisitorPhotoAdapter.cancelAllTasks();
        LogUtils.i("VisitorPhotoActivity.clazz->onDestroy()");
        DongSDKProxy.unRegisterAccountCallback(mAccountProxy);
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
    }

    /**
     * 访客留影详细点击事件
     */
    VisitorPhotoAdapter.OnItemClickListener onVisitorPhotoItemClickListener =
            new VisitorPhotoAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    VisitorPhotoBean visitorPhotoBean = mAdapterList.get(position);
                    Intent intent = new Intent(VisitorPhotoActivity.this,
                            VisitorPhotoDetailActivity.class);
                    intent.putExtra(INTENT_VISITOR_PHOTO_BEAN, visitorPhotoBean);
                    startActivity(intent);
                }

            };

    /**
     * 刷新访客留影数据
     */
    public void notifyDataSetChanged() {
        //倒序排列集合（按时间）
        Collections.sort(mAdapterList, Collections.reverseOrder());
        mVisitorPhotoAdapter.notifyDataSetChanged();
    }

    /**
     * 删除本地数据
     */
    public void delete(List<VisitorPhotoBean> localData) {
        int count = localData.size() - AppConfig.MAX_VISITOR_PHOTO_COUNT;
        if (count > 0) {
            List<Long> visitorPhotoIndex = new ArrayList<>();
            for (int i = 0; i < count; i++) {
                long visitorPhotoId = localData.get(i).getId();
                LogUtils.i("VisitorPhotoActivity.clazz->delete()->visitorPhotoId:" + visitorPhotoId);
                visitorPhotoIndex.add(visitorPhotoId);
            }
            VisitorPhotoOpe.delete(BaseApplication.context(), visitorPhotoIndex);
        }
    }

    /**
     * 处理访客留影集合
     *
     * @param netList 返回的访客留影集合
     */
    public void processData(List<VisitorPhotoBean> netList) {
        //1.查询本地数据
        List<VisitorPhotoBean> localList = VisitorPhotoOpe.queryAllDesc(BaseApplication.context());

        //2对比本地和平台数据
        boolean isAllSame = localList.containsAll(netList);
        LogUtils.i("VisitorPhotoActivity.clazz-->processData()" +
                "%%%%%%%%%%%%%%%%%% isAllSame:" + isAllSame + ",mStartIndex:"
                + mStartIndex + ",localList.size():" + localList.size());
        if (isAllSame) {//2.1如果本地数据库包含下拉刷新获取到的最新数据，那么返回
            if ((mStartIndex != 0 && mStartIndex > localList.size())) {//上拉加载
                mIsNoMoreData = true;
                return;
            } else if (mStartIndex != 0 && mStartIndex < localList.size()) {
                LogUtils.i("VisitorPhotoActivity.clazz-->processData notifyItemRemoved mStartIndex:" + mStartIndex);
                mVisitorPhotoAdapter.changeLoadStatus(VisitorPhotoAdapter.LOAD_NO_DATA);
            }
//            else {
//                BaseApplication.showToastShortInBottom(R.string.is_the_latest_data);
//            }
        } else {
            for (VisitorPhotoBean netBean : netList) {
                boolean isSame = false;
                //2.1.1将本地数据库中的数据与平台返回getPhotoTimestamp数据对比
                for (VisitorPhotoBean localBean : localList) {
                    if (localBean.getPhotoTimestamp().equals(netBean.getPhotoTimestamp()))
                        isSame = true;
                }
                //2.1.2不相同就添加到本地并且更新界面数据
                if (!isSame) {
                    VisitorPhotoOpe.insert(BaseApplication.context(), netBean);
                    mAdapterList.add(netBean);
                }
            }
        }
        mIsNoMoreData = netList.size() < MAX_DATA_COUNT;
        if (mIsNoMoreData) mVisitorPhotoAdapter.changeLoadStatus(VisitorPhotoAdapter.LOAD_NO_DATA);
        List<VisitorPhotoBean> newLocalList = VisitorPhotoOpe.queryAllAsc(BaseApplication.context());
        LogUtils.i("VisitorPhotoActivity.clazz-->processData() mAdapterList.size:" +
                mAdapterList.size() + ",newLocalList.size:" + newLocalList.size()
                + ",netList.size():" + netList.size() + ",mIsNoMoreData:" + mIsNoMoreData);
        //3.删除本地数据
        delete(newLocalList);
        notifyDataSetChanged();
    }

    /**
     * 访客留影下拉刷新
     */
    public class OnVisitorPhotoListener implements OnRefreshListener {
        @Override
        public void onRefresh() {
            mStartIndex = 0;
            setSwipeRefreshLoadingState();
            DongSDKProxy.requestGetDownloadUrlsWithParams(
                    DongConfiguration.mDeviceInfo.dwDeviceID, mStartIndex, MAX_DATA_COUNT);
            mShouldStop = false;
            mTimeCount = 0;
            LogUtils.i("VisitorPhotoActivity.clazz-->requestGetDownloadUrlsWithParams");
        }
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

    private Timer mTimer;// 定时器
    private MyTimerTask mTask = new MyTimerTask();
    private static boolean mShouldStop;
    private static int mTimeCount;

    private class MyTimerTask extends TimerTask {

        @Override
        public void run() {
            mTimeCount++;
            if (mTimeCount > 5 && !mShouldStop) {
                VisitorPhotoActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mShouldStop = true;
                        mTimeCount = 0;
                        mIsLoading = false;
                        setSwipeRefreshLoadedState();
                        mVisitorPhotoAdapter.changeLoadStatus(VisitorPhotoAdapter.LOAD_DATA_FAILED);
                        BaseApplication.showToastShortInCenter(R.string.get_data_failed);
                    }
                });
            }
            LogUtils.i("VisitorPhotoActivity.clazz-->MyTimerTask mTimeCount:"
                    + mTimeCount + ",mShouldStop:" + mShouldStop);
        }
    }

    public class VisitorRecordsActivityDongAccountProxy extends
            AbstractDongCallbackProxy.DongAccountCallbackImp {

        @Override
        public int onAuthenticate(InfoUser infoUser) {
            return 0;
        }

        @Override
        public int onGetDownloadUrls(int deviceId, ArrayList<InfoDownloadUrl> list) {
            mIsLoading = false;
            mShouldStop = true;
            setSwipeRefreshLoadedState();
            LogUtils.i("VisitorPhotoActivity.clazz-->onGetDownloadUrls()-->list.size:" + list.size());
            List<VisitorPhotoBean> netData = new ArrayList<>();
            for (InfoDownloadUrl infoDownloadurl : list) {
                VisitorPhotoBean visitorPhoto = new VisitorPhotoBean();
                visitorPhoto.setSize(infoDownloadurl.nSize);
                visitorPhoto.setType(ProcessDataUtils.visitorPhotoType(infoDownloadurl.nRecReason));
                visitorPhoto.setPhotoUrl(infoDownloadurl.url);
                visitorPhoto.setRoomValue(infoDownloadurl.roomValue);
                visitorPhoto.setDeviceId(DongConfiguration.mDeviceInfo.dwDeviceID);
                visitorPhoto.setDeviceName(infoDownloadurl.deviceName);
                visitorPhoto.setPhotoTimestamp(infoDownloadurl.timeStamp);
                visitorPhoto.setUserId(DongConfiguration.mUserInfo.userID);
                netData.add(visitorPhoto);
            }
            processData(netData);
            return 0;
        }

        @Override
        public int onUserError(int i) {
            return 0;
        }
    }

    @Override
    public void onBackClick() {
        VisitorPhotoActivity.this.finish();
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
