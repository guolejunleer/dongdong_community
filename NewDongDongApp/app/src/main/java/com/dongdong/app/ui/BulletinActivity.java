package com.dongdong.app.ui;

import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;

import com.dd121.community.R;
import com.ddclient.configuration.DongConfiguration;
import com.dongdong.app.AppConfig;
import com.dongdong.app.adapter.BulletinAdapter;
import com.dongdong.app.api.ApiHttpClient;
import com.dongdong.app.base.BaseActivity;
import com.dongdong.app.base.BaseApplication;
import com.dongdong.app.bean.BulletinBean;
import com.dongdong.app.db.BulletinOpe;
import com.dongdong.app.db.DeviceVillageOpe;
import com.dongdong.app.util.LogUtils;
import com.dongdong.app.widget.TitleBar;
import com.dongdong.app.widget.TitleBar.OnTitleBarClickListener;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import cz.msebera.android.httpclient.Header;

import static com.dongdong.app.api.ApiHttpClient.getDVNotices;

public class BulletinActivity extends BaseActivity implements OnTitleBarClickListener, OnRefreshListener {
    private static final String JSON_BULLETIN_METHOD = "villagenotices";
    public static final String INTENT_KEY_TITLE = "title";
    public static final String INTENT_KEY_NOTICE = "notice";
    public static final String INTENT_KEY_CREATED = "created";
    public static final String INTENT_KEY_VILLAGE_ID = "villageid";


    private static final int LOAD_NO_DATA = 1;
    private static final int DO_NOT_LOAD = 2;
    private static final int LOADING = 3;

    private static final int MAX_DATA_COUNT = 3;

    private static boolean mIsNoMoreData;

    private SwipeRefreshLayout mSwipeRefreshLayout;
    private List<BulletinBean> mBeanList = new ArrayList<>();
    private BulletinAdapter mBulletinAdapter;

    private boolean mIsLoading;
    private int mStartIndex = 0;
    final LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);

    @Override
    protected int getLayoutId() {
        return R.layout.activity_bulletin;
    }

    @Override
    public void initView() {
        TitleBar titleBar = (TitleBar) findViewById(R.id.tb_title);
        titleBar.setTitleBarContent(getString(R.string.message));
        titleBar.setOnTitleBarClickListener(this);
        titleBar.setAddArrowShowing(false);

        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeResources(
                android.R.color.holo_blue_bright, android.R.color.holo_blue_bright,
                R.color.status_color, android.R.color.holo_blue_bright);

        RecyclerView rvBulletin = (RecyclerView) findViewById(R.id.rv_bulletin);
        rvBulletin.setLayoutManager(mLayoutManager);
        rvBulletin.setItemAnimator(new DefaultItemAnimator());
        rvBulletin.addOnScrollListener(onBulletinScrollListener);
        mBulletinAdapter = new BulletinAdapter(BulletinActivity.this, mBeanList);
        rvBulletin.setAdapter(mBulletinAdapter);
        mBulletinAdapter.setOnItemClickListener(onBulletinItemClickListener);
        mBulletinAdapter.changeLoadStatus(DO_NOT_LOAD);
    }

    @Override
    public void initData() {
        String villageId = DeviceVillageOpe.queryDataByDeviceId(BaseApplication.context(),
                String.valueOf(DongConfiguration.mDeviceInfo.dwDeviceID));
        if (!TextUtils.isEmpty(villageId)) {
            List<BulletinBean> localList = BulletinOpe.queryDataByVillageId(BaseApplication.context(), villageId);
            mBeanList.clear();
            for (BulletinBean localBean : localList) {
                mBeanList.add(localBean);
            }
            notifyDataSetChanged();
        }
    }

    /**
     * RecyclerView监听事件
     */
    RecyclerView.OnScrollListener onBulletinScrollListener = new RecyclerView.OnScrollListener() {

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
                    LogUtils.i("BulletinActivity.clazz-->onScrollStateChanged isRefreshing!!!");
                    mBulletinAdapter.notifyItemRemoved(mBulletinAdapter.getItemCount());
                    return;
                }
                if (mIsNoMoreData) {
                    LogUtils.i("BulletinActivity.clazz-->onScrollStateChanged isNoMoreData!!!");
                    mBulletinAdapter.changeLoadStatus(LOAD_NO_DATA);
                    return;
                }
                if (!mIsLoading) {//下拉加载
                    LogUtils.i("BulletinActivity.clazz-->onScrollStateChanged is down upload!!!");
                    mIsLoading = true;
                    mBulletinAdapter.changeLoadStatus(LOADING);
                    getBulletinFromNet(mStartIndex += MAX_DATA_COUNT);
                    LogUtils.i("BulletinActivity.clazz-->onScrollStateChanged mStartIndex:" + mStartIndex);
                }
            }
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
        }
    };

    /**
     * 物业公告点击事件
     */
    BulletinAdapter.OnItemClickListener onBulletinItemClickListener = new
            BulletinAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    Intent intent = new Intent(BulletinActivity.this, BulletinDetailActivity.class);
                    intent.putExtra(BulletinDetailActivity.BULLETIN_TITLE,
                            mBeanList.get(position).getTitle());
                    intent.putExtra(BulletinDetailActivity.BULLETIN_NOTICE,
                            mBeanList.get(position).getNotice());
                    intent.putExtra(BulletinDetailActivity.BULLETIN_CREATED,
                            mBeanList.get(position).getCreated());
                    startActivity(intent);
                }
            };

    /**
     * 获取物业公告
     */
    public void getBulletinFromNet(int startIndex) {
        RequestParams params = getDVNotices(AppConfig.BASE_URL, DongConfiguration.mDeviceInfo.dwDeviceID,
                startIndex, MAX_DATA_COUNT);
        ApiHttpClient.postDirect(AppConfig.BASE_URL, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                mIsLoading = false;
                setSwipeRefreshLoadedState();
                try {
                    List<BulletinBean> netDataList = new ArrayList<>();
                    JSONObject receiveDataJson = new JSONObject(new String(responseBody));
                    LogUtils.i("BulletinActivity.clazz-->getBulletinFromNet()-->receiveDataJson:" +
                            receiveDataJson);
                    String resultCode = receiveDataJson.getString(AppConfig.JSON_RESULT_CODE);
                    if (resultCode.equals(AppConfig.JSON_CORRECT_RESULT_CODE)) {
                        String jsonInitData = receiveDataJson.getString(AppConfig.JSON_RESPONSE_PARAMS);
                        if (jsonInitData.equals(AppConfig.JSON_EMPTY_DATA)) {
                            mIsNoMoreData = true;
                            mBulletinAdapter.changeLoadStatus(LOAD_NO_DATA);
//                            if (mStartIndex == 0)
//                                BaseApplication.showToastShortInBottom(R.string.is_the_latest_data);
                            return;
                        }
                        LogUtils.i("BulletinActivity.clazz-->getBulletinFromNet()-->jsonInitData:" +
                                jsonInitData);
                        JSONArray jsonArray = new JSONObject(jsonInitData).getJSONArray(
                                JSON_BULLETIN_METHOD);
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            BulletinBean bulletinBean = new BulletinBean();
                            bulletinBean.setTitle(jsonObject.getString(INTENT_KEY_TITLE));
                            bulletinBean.setNotice(jsonObject.getString(INTENT_KEY_NOTICE));
                            bulletinBean.setCreated(jsonObject.getString(INTENT_KEY_CREATED));
                            bulletinBean.setDeviceId(String.valueOf(DongConfiguration.mDeviceInfo.dwDeviceID));
                            bulletinBean.setVillageId(jsonObject.getString(INTENT_KEY_VILLAGE_ID));
                            netDataList.add(bulletinBean);
                        }
                        processJsonData(netDataList);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    mIsLoading = false;
                    setSwipeRefreshLoadedState();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                LogUtils.i("BulletinActivity.clazz-->getDataOpenDoorRecord()onFailure");
                mIsLoading = false;
                setSwipeRefreshLoadedState();
                mBulletinAdapter.changeLoadStatus(BulletinAdapter.LOAD_DATA_FAILED);
                BaseApplication.showToastShortInCenter(R.string.get_data_failed);
            }
        });
    }

    //物业公告更新
    public void notifyDataSetChanged() {
        //倒序排列(按时间)
        Collections.sort(mBeanList, Collections.reverseOrder());
        mBulletinAdapter.notifyDataSetChanged();
    }

    //物业公告删除
    public void deleteLocal(List<BulletinBean> localData) {
        int count = localData.size() - AppConfig.MAX_BULLETIN_COUNT;
        if (count > 0) {
            List<Long> bulletinIndex = new ArrayList<>();
            for (int i = 0; i < count; i++) {
                long bulletinId = localData.get(i).getId();
                bulletinIndex.add(bulletinId);
            }
            //删除最开始保存的（相较而言时间早一点的）
            BulletinOpe.delete(BaseApplication.context(), bulletinIndex);
        }
    }

    /**
     * 处理物业公告json数据
     *
     * @param netDataList 物业公告数据
     */
    public void processJsonData(List<BulletinBean> netDataList) {
        //1.获取本地数据
        List<BulletinBean> localList = BulletinOpe.queryAll(BaseApplication.context());

        //2对比本地和平台数据
        boolean isAllSame = localList.containsAll(netDataList);
        LogUtils.i("BulletinActivity.clazz-->processJsonData()%%%%%%%%%%%%% isAllSame:"
                + isAllSame + ",localList.size:" + localList.size() + ",mStartIndex:" + mStartIndex);
        if (isAllSame) {//2.1如果本地数据库包含下拉刷新获取到的最新数据，那么返回
            if (mStartIndex != 0 && mStartIndex > localList.size()) {//2.1.1上拉加载发现本地有平台数据
                mIsNoMoreData = true;
                return;
            } else if (mStartIndex != 0 && mStartIndex < localList.size()) {
                LogUtils.i("BulletinActivity.clazz-->processData notifyItemRemoved mStartIndex:" + mStartIndex);
                mBulletinAdapter.changeLoadStatus(BulletinAdapter.LOAD_NO_DATA);
            }
//          else {
//                BaseApplication.showToastShortInBottom(R.string.is_the_latest_data);
//            }
        } else {
            for (BulletinBean netBean : netDataList) {
                boolean isSame = false;
                //2.1.1将本地数据库中的数据与平台返回数据对比
                for (BulletinBean localBean : localList) {
                    if (localBean.getCreated().equals(netBean.getCreated())
                            && localBean.getVillageId().equals(netBean.getVillageId()))
                        isSame = true;
                }
                //2.1.2不相同就添加到本地并且更新界面数据
                if (!isSame) {
                    BulletinOpe.insert(BaseApplication.context(), netBean);
                    mBeanList.add(netBean);
                }
            }
        }
        mIsNoMoreData = netDataList.size() < MAX_DATA_COUNT;
        if (mIsNoMoreData) mBulletinAdapter.changeLoadStatus(BulletinAdapter.LOAD_NO_DATA);
        List<BulletinBean> newLocalList = BulletinOpe.queryAll(BaseApplication.context());
        LogUtils.i("BulletinActivity.clazz-->processJsonData() mBeanList.size:" +
                mBeanList.size() + ",newLocalList.size:" + newLocalList.size()
                + ",netDataList.size():" + netDataList.size() + ",mIsNoMoreData:" + mIsNoMoreData);
        //删除本地数据
        //deleteLocal(newLocalList);
        notifyDataSetChanged();
    }

    /**
     * 物业公告下拉刷新(下拉刷新过程中不能继续下拉)
     */
    @Override
    public void onRefresh() {
        mStartIndex = 0;
        setSwipeRefreshLoadingState();
        getBulletinFromNet(mStartIndex);
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
        BulletinActivity.this.finish();
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
