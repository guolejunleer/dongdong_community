package com.dongdong.app.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.RadioButton;

import com.dd121.community.R;
import com.ddclient.configuration.DongConfiguration;
import com.dongdong.app.AppConfig;
import com.dongdong.app.adapter.CommonViewPagerAdapter;
import com.dongdong.app.adapter.OpenDoorRecordAdapter;
import com.dongdong.app.adapter.VisitorPhotoAdapter;
import com.dongdong.app.api.ApiHttpClient;
import com.dongdong.app.base.BaseActivity;
import com.dongdong.app.bean.OpenDoorRecordBean;
import com.dongdong.app.bean.VisitorPhotoBean;
import com.dongdong.app.widget.SwipeRefreshLayout;
import com.dongdong.app.widget.SwipeRefreshLayout.OnLoadListener;
import com.dongdong.app.widget.SwipeRefreshLayout.OnRefreshListener;
import com.dongdong.app.widget.TitleBar;
import com.dongdong.app.widget.TitleBar.OnTitleBarClickListener;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class VisitorRecordsActivity extends BaseActivity implements OnTitleBarClickListener {
    private RadioButton mRbOpenDoorRecord, mRbVisitorPhoto;
    private SwipeRefreshLayout mSlOpenDoor;
    private SwipeRefreshLayout mSlVisitorPhoto;
    private ViewPager mVpVisitorRecords;
    private ListView mLvOpenDoorRecord, mLvVisitorPhoto;

    private List<OpenDoorRecordBean> mOpenDoorList = new ArrayList<>();
    private OpenDoorRecordAdapter mOpenDoorRecordAdapter;
    private VisitorPhotoAdapter mVisitorPhotoAdapter;

    private ApiHttpClient mApiHttpClient = new ApiHttpClient();

    @Override
    protected int getLayoutId() {
        return R.layout.activity_visitor_records;
    }

    @SuppressWarnings("deprecation")
    @Override
    public void initView() {
        TitleBar titleBar = (TitleBar) this.findViewById(R.id.tb_title);
        mRbOpenDoorRecord = (RadioButton) this.findViewById(R.id.rb_open_door_record);
        mRbVisitorPhoto = (RadioButton) this.findViewById(R.id.rb_visitor_photo);
        mVpVisitorRecords = (ViewPager) this.findViewById(R.id.vw_my_view_pager);

        mRbOpenDoorRecord.setChecked(true);
        titleBar.setTitleBarContent(getString(R.string.visitorrecord));
        titleBar.setOnTitleBarClickListener(this);
        titleBar.setAddArrowShowing(false);

        mVpVisitorRecords.setOnPageChangeListener(new MyOnPageChangeListener());

        mRbOpenDoorRecord.setOnClickListener(onRbListener);
        mRbVisitorPhoto.setOnClickListener(onRbListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    public class MyOnPageChangeListener implements OnPageChangeListener {

        @Override
        public void onPageSelected(int arg0) {
            switch (arg0) {
                case 0:
                    mRbOpenDoorRecord.setChecked(true);
                    break;
                case 1:
                    mRbVisitorPhoto.setChecked(true);
                    break;
            }
        }

        @Override
        public void onPageScrollStateChanged(int arg0) {
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
        }
    }

    OnClickListener onRbListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.rb_open_door_record:
                    mVpVisitorRecords.setCurrentItem(0);
                    break;
                case R.id.rb_visitor_photo:
                    mVpVisitorRecords.setCurrentItem(1);
                    break;
                default:
                    break;
            }
        }
    };

    @SuppressLint({"InlinedApi", "ResourceAsColor"})
    @Override
    public void initData() {
        /****************************开门记录**********************************/
        View vwOpenDoorRecord = this.getLayoutInflater().inflate(
                R.layout.visitor_records_open_door_record, null);
        mLvOpenDoorRecord = (ListView) vwOpenDoorRecord.findViewById(R.id.opendoorrecordListView);
        mLvOpenDoorRecord.setOnItemClickListener(onOpenDoorRecordListViewItemClickListener);

        mSlOpenDoor = (SwipeRefreshLayout)
                vwOpenDoorRecord.findViewById(R.id.swipe_container);
        mSlOpenDoor.setOnRefreshListener(onOpenDoorRefreshListener);
        mSlOpenDoor.setOnLoadListener(onOpenDoorLoadListener);
        mSlOpenDoor.setColor(
                android.R.color.holo_blue_bright, android.R.color.holo_blue_bright,
                R.color.status_color, android.R.color.holo_blue_bright);
        mSlOpenDoor.setMode(SwipeRefreshLayout.Mode.BOTH);
        mSlOpenDoor.setLoadNoFull(false);
        /*********************************************************/
        // mOpenDoorEmptyswipeLayout = (SwipeRefreshLayout) mVwOpenDoorRecord
        // .findViewById(R.id.swipe_container_empty);
        // mOpenDoorEmptyswipeLayout.setOnRefreshListener(this);
        // mOpenDoorEmptyswipeLayout.setOnLoadListener(this);
        // mOpenDoorEmptyswipeLayout.setColorSchemeResources(
        // android.R.color.holo_blue_bright,
        // android.R.color.holo_green_light,
        // android.R.color.holo_orange_light,
        // android.R.color.holo_red_light);
        // mOpenDoorEmptyswipeLayout.setMode(SwipeRefreshLayout.Mode.BOTH);
        // mOpenDoorEmptyswipeLayout.setLoadNoFull(false);
        /*****************************访客留影****************************/
        View vwVisitorPhoto = this.getLayoutInflater().inflate(
                R.layout.visitor_records_visitor_photo, null);
        mLvVisitorPhoto = (ListView) vwVisitorPhoto.findViewById(R.id.lv_visitor_photo_ListView);
        mLvVisitorPhoto.setOnItemClickListener(onVisitorPhotoListViewItemClickListener);

        mSlVisitorPhoto = (SwipeRefreshLayout) vwVisitorPhoto.findViewById(R.id.swipe_container);
        mSlVisitorPhoto.setOnRefreshListener(onVisitorPhotoRefreshListener);
        mSlVisitorPhoto.setOnLoadListener(onVisitorPhotoLoadListener);
        mSlVisitorPhoto.setColor(
                android.R.color.holo_blue_bright, android.R.color.holo_blue_bright,
                R.color.status_color, android.R.color.holo_blue_bright);
        mSlVisitorPhoto.setMode(SwipeRefreshLayout.Mode.BOTH);
        mSlVisitorPhoto.setLoadNoFull(false);
        /*****************************************************/
        List<View> listViews = new ArrayList<>();
        listViews.add(vwOpenDoorRecord);
        listViews.add(vwVisitorPhoto);

        mVpVisitorRecords.setAdapter(new CommonViewPagerAdapter(listViews));
        getDataOpenDoorRecord(0);
        dealVisitorPhoto("");
    }

    //开门记录详细点击事件
    OnItemClickListener onOpenDoorRecordListViewItemClickListener = new OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> arg0, View arg1, int position,
                                long arg3) {
            OpenDoorRecordBean door_record = mOpenDoorList.get(position);

            String type = door_record.getType();
            String roomNumber = door_record.getRoomNumber();
            String timestamp = door_record.getTimestamp();
            String deviceName = door_record.getDeviceName();
            String memberName = door_record.getMemberName();
            String idNumber = door_record.getIdNumber();
            String comNumber = door_record.getComNumber();
            String mobilePhone = door_record.getMobilePhone();

            Intent intent = new Intent();
            intent.setClass(VisitorRecordsActivity.this, DoorRecordDetailActivity.class);
            intent.putExtra("type", type);
            intent.putExtra("roomNumber", roomNumber);
            intent.putExtra("timestamp", timestamp);
            intent.putExtra("deviceName", deviceName);
            intent.putExtra("memberName", memberName);
            intent.putExtra("idNumber", idNumber);
            intent.putExtra("comNumber", comNumber);
            intent.putExtra("mobilePhone", mobilePhone);
            startActivity(intent);
        }
    };

    //访客留影详细点击事件
    OnItemClickListener onVisitorPhotoListViewItemClickListener = new OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {

        }
    };

    @SuppressWarnings("static-access")
    public void getDataOpenDoorRecord(int startIndex) {
        // 正式服务器
        //String BASE_URL = "http://wuye.dd121.com/dd/wuye_api_d/2.0";
        // 测试服务器
        String BASE_URL = "http://192.168.68.55/web/wuye_api/apiserver/2.0/";

        Bundle bundle = getIntent().getBundleExtra(AppConfig.INTENT_BUNDLE_KEY);
        int deviceId = bundle.getInt(AppConfig.BUNDLE_KEY_DEVICE_ID);
        int userId = DongConfiguration.mUserInfo.userID;
        // 获取开门记录参数(startindex:开始位置 endindex:加载数据条数)
        RequestParams params = mApiHttpClient.getUnlockRecords3(
                BASE_URL, userId, deviceId, startIndex, 15);
        // 发送获取开门记录请求
        mApiHttpClient.postDirect(BASE_URL, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers,
                                  byte[] responseBody) {
                try {
                    JSONObject init_json = new JSONObject(new String(responseBody));
                    String json_data = init_json.getString("response_params");
                    dealDoorRecordJsonData(json_data);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers,
                                  byte[] responseBody, Throwable error) {
            }
        });
    }

    @Override
    public void onBackClick() {
        VisitorRecordsActivity.this.finish();
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

    // 处理JSON数据
    public void dealDoorRecordJsonData(String jsonData) {
        try {
            JSONArray jsonArray = new JSONArray(jsonData);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject obj = jsonArray.getJSONObject(i);

                String roomNumber = obj.getString("roomNumber");
                String type = opendoortype(obj.getInt("type"));
                String timestamp = obj.getString("timestamp");
                String comNumber = obj.getString("comNumber");
                String deviceName = obj.getString("deviceName");
                String memberName = obj.getString("memberName");
                String mobilePhone = obj.getString("mobilePhone");
                String idNumber = obj.getString("idNumber");

                OpenDoorRecordBean odr = new OpenDoorRecordBean();
                odr.setComNumber(comNumber);
                odr.setRoomNumber(roomNumber);
                odr.setType(type);
                odr.setTimestamp(timestamp);
                odr.setDeviceName(deviceName);
                odr.setIdNumber(idNumber);
                odr.setMemberName(memberName);
                odr.setMobilePhone(mobilePhone);

                mOpenDoorList.add(odr);
            }
            mOpenDoorRecordAdapter = new
                    OpenDoorRecordAdapter(VisitorRecordsActivity.this, mOpenDoorList);
            mLvOpenDoorRecord.setAdapter(mOpenDoorRecordAdapter);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 解析访客留影url
     *
     * @param url 照片URL
     *
     */
    public void dealVisitorPhoto(String url) {
        String url1 = "http://img0.imgtn.bdimg.com/it/u=3101474196,798135612&fm=21&gp=0.jpg";
        String url2 = "http://img5.imgtn.bdimg.com/it/u=1124453212,2157292849&fm=21&gp=0.jpg";
        String url3 = "http://img4.imgtn.bdimg.com/it/u=632817588,1703012578&fm=21&gp=0.jpg";
        String url4 = "http://img1.imgtn.bdimg.com/it/u=107899241,2440486256&fm=21&gp=0.jpg";
        String url5 = "http://img2.imgtn.bdimg.com/it/u=1302881290,2812444762&fm=21&gp=0.jpg";

        List<VisitorPhotoBean> my_list = new ArrayList<>();

        VisitorPhotoBean visitor_photo1 = new VisitorPhotoBean();
        visitor_photo1.setPhotoUrl(url1);
        visitor_photo1.setDeviceName("door1");
        visitor_photo1.setPhotoTimestamp("2016-10-28 17:53:30");

        VisitorPhotoBean visitor_photo2 = new VisitorPhotoBean();
        visitor_photo2.setPhotoUrl(url2);
        visitor_photo2.setDeviceName("door2");
        visitor_photo2.setPhotoTimestamp("2016-10-28 17:53:50");

        VisitorPhotoBean visitor_photo3 = new VisitorPhotoBean();
        visitor_photo3.setPhotoUrl(url3);
        visitor_photo3.setDeviceName("door3");
        visitor_photo3.setPhotoTimestamp("2016-10-28 17:33:40");

        VisitorPhotoBean visitor_photo4 = new VisitorPhotoBean();
        visitor_photo4.setPhotoUrl(url4);
        visitor_photo4.setDeviceName("door4");
        visitor_photo4.setPhotoTimestamp("2016-10-28 17:23:10");

        VisitorPhotoBean visitor_photo5 = new VisitorPhotoBean();
        visitor_photo5.setPhotoUrl(url5);
        visitor_photo5.setDeviceName("door5");
        visitor_photo5.setPhotoTimestamp("2016-10-28 18:23:10");

        my_list.add(visitor_photo1);
        my_list.add(visitor_photo2);
        my_list.add(visitor_photo3);
        my_list.add(visitor_photo4);
        my_list.add(visitor_photo5);

        mVisitorPhotoAdapter = new VisitorPhotoAdapter(this, my_list);
        mLvVisitorPhoto.setAdapter(mVisitorPhotoAdapter);
    }


    //判断开门类型
    public String opendoortype(int type) {
        String typename = null;
        switch (type) {
            case 1:   //手机开门
                typename = getString(R.string.App);
                break;
            case 2:   //刷卡开门
                typename = getString(R.string.accesscards);
                break;
            case 3:   //wifi开门
                typename = getString(R.string.WIFI);
                break;
            case 4:   //临时密码开门
                typename = getString(R.string.TemporaryPassword);
                break;
            case 5:   //住户密码开门
                typename = getString(R.string.Householdpassword);
                break;
            case 6:   //平台查询卡号开门
                typename = getString(R.string.accesscards);
                break;
            case 7:   //蓝牙开门
                typename = getString(R.string.bluetooth);
                break;
            case 10:  //呼叫住户开门
                typename = getString(R.string.phonecall);
                break;

            default:
                break;
        }
        return typename;
    }

//    private class VisitorRecordsActivityDongAccountProxy extends AbstractDongSDKProxy.DongAccountCallbackImp{
//        @Override
//        public int OnAuthenticate(InfoUser infoUser) {
//            return 0;
//        }
//
//        @Override
//        public int OnSdkTunnel(int deviceid, byte[] data) {
//            return super.OnSdkTunnel(deviceid, data);
//        }
//
//        @Override
//        public int OnUserError(int i) {
//            return 0;
//        }
//    }


    /**
     * 开门记录下拉刷新
     */
    OnRefreshListener onOpenDoorRefreshListener = new OnRefreshListener() {
        @Override
        public void onRefresh() {
//            mlist.clear();
//            getDataOpenDoorRecord(0);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    mSlOpenDoor.setRefreshing(false);
//                  mOpenDoorRecordAdapter.notifyDataSetChanged();
                }
            }, 2000);
        }
    };

    /**
     * 开门记录上拉加载
     */
    OnLoadListener onOpenDoorLoadListener = new OnLoadListener() {
        @Override
        public void onLoad() {
//            getDataOpenDoorRecord(start_index += 15);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    mSlOpenDoor.setLoading(false);
//                  mOpenDoorRecordAdapter.notifyDataSetChanged();
                }
            }, 2000);
        }
    };

    /**
     * 访客留影下拉刷新
     */
    OnRefreshListener onVisitorPhotoRefreshListener = new OnRefreshListener() {
        @Override
        public void onRefresh() {
//            mlist.clear();
//            getDataOpenDoorRecord(0);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    mSlVisitorPhoto.setRefreshing(false);
                    mVisitorPhotoAdapter.notifyDataSetChanged();
                }
            }, 2000);
        }
    };

    /**
     * 访客留影上拉加载
     */
    OnLoadListener onVisitorPhotoLoadListener = new OnLoadListener() {
        @Override
        public void onLoad() {
//            getDataOpenDoorRecord(start_index += 15);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    mSlVisitorPhoto.setLoading(false);
                    mVisitorPhotoAdapter.notifyDataSetChanged();
                }
            }, 2000);
        }
    };
}
