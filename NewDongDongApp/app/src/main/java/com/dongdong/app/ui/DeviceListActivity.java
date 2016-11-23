package com.dongdong.app.ui;

import java.util.ArrayList;

import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.dd121.louyu.R;
import com.ddclient.MobileClientLib.InfoUser;
import com.ddclient.configuration.DongConfiguration;
import com.ddclient.dongsdk.AbstractDongSDKProxy.DongAccountCallbackImp;
import com.ddclient.dongsdk.DeviceInfo;
import com.ddclient.dongsdk.DongSDKProxy;
import com.ddclient.push.PushMessageChange;
import com.dongdong.app.AppConfig;
import com.dongdong.app.AppContext;
import com.dongdong.app.MainActivity;
import com.dongdong.app.adapter.DeviceInfoAdapter;
import com.dongdong.app.base.BaseActivity;
import com.dongdong.app.base.BaseApplication;
import com.dongdong.app.ui.dialog.TipDialogManager;
import com.dongdong.app.util.LogUtils;
import com.dongdong.app.util.UIHelper;
import com.dongdong.app.widget.TitleBar;
import com.dongdong.app.widget.TitleBar.OnTitleBarClickListener;

public class DeviceListActivity extends BaseActivity implements
        OnTitleBarClickListener {

    private ListView mLvInfo;
    private TitleBar mTitleBar;
    private DeviceInfoAdapter mInfoAdapter;
    private DeviceListActivityDongAccountProxy mAccountProxy;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_choose;
    }

    @Override
    public void initView() {
        mLvInfo = (ListView) findViewById(R.id.mlvInfo);
        mTitleBar = (TitleBar) findViewById(R.id.tb_title);
        mInfoAdapter = new DeviceInfoAdapter(this);
        mLvInfo.setAdapter(mInfoAdapter);
    }

    @Override
    public void initData() {
        mAccountProxy = new DeviceListActivityDongAccountProxy();
        mTitleBar.setTitleBarContent(getString(R.string.list));
        mTitleBar.setOnTitleBarClickListener(this);
        mLvInfo.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                DeviceInfo deviceInfo = (DeviceInfo) mInfoAdapter.getItem(arg2);
                AppContext.mAppConfig.setConfigValue(
                        AppConfig.DONG_CONFIG_SHARE_PREF_NAME,
                        AppConfig.KEY_DEVICE_ID, deviceInfo.dwDeviceID);
                DongConfiguration.mDeviceInfo = deviceInfo;
                Intent intent = new Intent(DeviceListActivity.this,
                        MainActivity.class);
                LogUtils.i("DeviceListActivity.clazz--->>>IsOnline:"
                        + deviceInfo.isOnline);
                intent.putExtra(AppConfig.BUNDLE_KEY_DEVICE_INFO, deviceInfo);
                startActivity(intent);
                if (!deviceInfo.isOnline) {
                    BaseApplication.showToastShortInBottom(R.string.device_offline);
                } else {
                    UIHelper.showVideoViewActivity(DeviceListActivity.this, true, "");
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        DongSDKProxy.registerAccountCallback(mAccountProxy);
        ArrayList<DeviceInfo> deviceList = DongSDKProxy
                .requestGetDeviceListFromCache();
        mInfoAdapter.setData(deviceList);
        mInfoAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onPause() {
        super.onPause();
        DongSDKProxy.unRegisterAccountCallback();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onBackClick() {
        finish();
    }

    @Override
    public void onTitleClick() {
    }

    @Override
    public void onAddClick() {
        startActivity(new Intent(this, AddDeviceActivity.class));
        finish();
    }

    private class DeviceListActivityDongAccountProxy extends
            DongAccountCallbackImp {

        @Override
        public int OnAuthenticate(InfoUser infoUser) {
            LogUtils.i("DeviceListActivity.clazz -->>> OnAuthenticate infoUser:"
                    + infoUser);
            return 0;
        }

        @Override
        public int OnLoginOtherPlace(String tip) {
            TipDialogManager.showOtherLoginDialog(DeviceListActivity.this, tip);
            return 0;
        }

        @Override
        public int OnCall(ArrayList<DeviceInfo> infos) {
            LogUtils.i("DeviceListActivity.clazz -->>OnCall infos:" + infos);
            int size = infos.size();
            if (size > 0) {
                DeviceInfo deviceInfo = infos.get(0);
                String message = deviceInfo.msg;
                LogUtils.i("DeviceListActivity.clazz-->>OnCall() deviceName:"
                        + deviceInfo.deviceName + ",dwDeviceID："
                        + deviceInfo.dwDeviceID + ",msg:" + deviceInfo.msg);
                PushMessageChange.pushMessageChange(DeviceListActivity.this,
                        message);
            }
            return 0;
        }

        @Override
        public int OnNewListInfo() {
            ArrayList<DeviceInfo> deviceList = DongSDKProxy
                    .requestGetDeviceListFromCache();
            mInfoAdapter.setData(deviceList);
            mInfoAdapter.notifyDataSetChanged();
            LogUtils.i("DeviceListActivity.clazz -->>>OnNewListInfo deviceList.size:"
                    + deviceList.size());
            return 0;
        }

        @Override
        public int OnUserError(int error) {
            LogUtils.i("DeviceListActivity.clazz -->>> OnUserError error:"
                    + error);
            return 0;
        }
    }
}