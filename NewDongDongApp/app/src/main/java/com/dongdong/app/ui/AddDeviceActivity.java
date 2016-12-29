package com.dongdong.app.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.dd121.community.R;
import com.ddclient.dongsdk.AbstractDongCallbackProxy;
import com.ddclient.dongsdk.DeviceInfo;
import com.ddclient.dongsdk.DongSDKProxy;
import com.ddclient.jnisdk.InfoUser;
import com.dongdong.app.adapter.LanListAdapter;
import com.dongdong.app.base.BaseActivity;
import com.dongdong.app.base.BaseApplication;
import com.dongdong.app.ui.dialog.CommonDialog;
import com.dongdong.app.ui.dialog.TipDialogManager;
import com.dongdong.app.util.LogUtils;
import com.dongdong.app.widget.TitleBar;
import com.dongdong.app.widget.TitleBar.OnTitleBarClickListener;
import com.zbar.lib.CaptureActivity;

import java.util.ArrayList;

public class AddDeviceActivity extends BaseActivity implements
        OnTitleBarClickListener, OnClickListener {

    private TitleBar mTitleBar;
    private Button mBtnQrCode;
    private Button mBtLan;
    private EditText mEtDeviceSerial;
    private EditText mDeviceName;
    private LanListAdapter mListAdapter;
    private ListView mListView;
    public ArrayList<DeviceInfo> mDeviceList;
    private CommonDialog mDialog;

    private AddDeviceActivityDongAccountProxy
            mAccountProxy = new AddDeviceActivityDongAccountProxy();

    @Override
    protected int getLayoutId() {
        return R.layout.activity_add_device;
    }

    @Override
    public void initView() {
        mTitleBar = (TitleBar) findViewById(R.id.tb_title);
        mBtnQrCode = (Button) findViewById(R.id.bt_rc_code);
        mEtDeviceSerial = (EditText) findViewById(R.id.et_device_serial);
        mDeviceName = (EditText) findViewById(R.id.et_device_name);
        mBtLan = (Button) findViewById(R.id.bt_lan);
        mListView = (ListView) findViewById(R.id.lv_list_account);
    }

    @Override
    public void initData() {

        mDialog = new CommonDialog(this);
        mTitleBar.setTitleBarContent(getString(R.string.addDevice));
        mTitleBar.setFinishShowing(true);
        mTitleBar.setOnTitleBarClickListener(this);
        mBtLan.setOnClickListener(this);
        mBtnQrCode.setOnClickListener(this);

        mListAdapter = new LanListAdapter(this);
        mListView.setAdapter(mListAdapter);
        mListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                final DeviceInfo deviceInfo = mListAdapter.getItem(arg2);
                mDeviceName.setText(deviceInfo.deviceName);
                mEtDeviceSerial.setText(deviceInfo.deviceSerialNO);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!DongSDKProxy.initCompleteDongAccountLan()) {
            DongSDKProxy.initDongAccountLan(mAccountProxy);
        } else {
            DongSDKProxy.registerAccountLanCallback(mAccountProxy);
        }
        DongSDKProxy.requestLanStartScan();
        LogUtils.i("AddDeviceActivity.clazz-->>> onResume...");
    }

    @Override
    protected void onPause() {
        super.onPause();
        DongSDKProxy.requestLanStopScan();
        DongSDKProxy.unRegisterAccountLanCallback(mAccountProxy);
        DongSDKProxy.clearDongAccountLan();
        LogUtils.i("AddDeviceActivity.clazz-->>> onPause...");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        LogUtils.i("AddDeviceActivity.clazz-->>> onActivityResult requestCode："
                + requestCode + ",resultCode:" + resultCode + ",data:" + data);
        if (data == null) return;
        switch (requestCode) {
            case 0:
                String stringExtra = data.getStringExtra(CaptureActivity.INTENT_RESULT_KEY);
                LogUtils.i("AddDeviceActivity.clazz-->>> onActivityResult..." + stringExtra);
                if (TextUtils.isEmpty(stringExtra))
                    return;
                mDeviceName.setText("设备名称");
                mEtDeviceSerial.setText(stringExtra);
                break;
        }

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
    }

    @Override
    public void onFinishClick() {
        String devName = mDeviceName.getText().toString();
        String deviceSer = mEtDeviceSerial.getText().toString();
        if (TextUtils.isEmpty(devName) || TextUtils.isEmpty(deviceSer)) {
            BaseApplication.showToastShortInBottom(R.string.empty_tip);
            return;
        }
        View view = LayoutInflater.from(this).inflate(R.layout.loading_dialog, null);
        TextView tipTextView = (TextView) view.findViewById(R.id.tv_tip);
        tipTextView.setText(getString(R.string.wait));
        mDialog.setContent(view);
        mDialog.show();
        DongSDKProxy.requestAddDevice(devName, deviceSer);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.bt_lan:
                DongSDKProxy.requestLanStartScan();
                break;
            case R.id.bt_rc_code:
                startActivityForResult(new Intent(this, CaptureActivity.class), 0);
                break;
            default:
                break;
        }
    }

    private class AddDeviceActivityDongAccountProxy extends
            AbstractDongCallbackProxy.DongAccountCallbackImp {

        @Override
        public int onAuthenticate(InfoUser tInfo) {
            LogUtils.i("AddDeviceActivity.clazz--->>>OnAuthenticate........tInfo:"
                    + tInfo);
            return 0;
        }

        @Override
        public int onNewListInfo() {
            mDeviceList = DongSDKProxy.requestLanGetDeviceListFromCache();
            mListAdapter.setData(mDeviceList);
            mListAdapter.notifyDataSetChanged();
            LogUtils.i("AddDeviceActivity.clazz--->>>OnNewListInfo........mDeviceList size:"
                    + mDeviceList.size());
            return 0;
        }

        @Override
        public int onAddDevice(int nReason, String username) {
            LogUtils.i("AddDeviceActivity.clazz--->>>OnAddDevice........nReason:"
                    + nReason + ";username:" + username);
            if (mDialog.isShowing())
                mDialog.dismiss();
            if (nReason == 0) {
//                BaseApplication.showToastShortInBottom(R.string.suc);
                mDialog.setMessage(getString(R.string.device_already_added));
            } else if (nReason == 1) {
//                BaseApplication.showToastShortInBottom(R.string.serial_error);
                mDialog.setMessage(getString(R.string.serial_invalidate));
            } else if (nReason == 3) {
                mDialog.setMessage(getString(R.string.device_already_added, username));
            } else {
                mDialog.setMessage(getString(R.string.serial_failed));
            }
            mDialog.setCancelable(false);
            mDialog.setPositiveButton(R.string.sure, null);
            mDialog.show();
            return 0;
        }

        @Override
        public int onUserError(int nErrNo) {
            LogUtils.i("AddDeviceActivity.clazz--->>>OnUserError........nErrNo:" + nErrNo);
            if (mDialog.isShowing())
                mDialog.dismiss();
            TipDialogManager.showTipDialog(AddDeviceActivity.this,
                    BaseApplication.context().getString(R.string.tip),
                    BaseApplication.context().getString(R.string.add_devcie_error) + nErrNo);
            return 0;
        }

    }

}
