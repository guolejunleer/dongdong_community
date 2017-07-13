package com.dongdong.app.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
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
    private ImageButton mIbQrCode;
    private ImageButton mIbLanRefresh;

    private View mViewDeviceInfo;
    private TextView mTvAddTip;
    private EditText mEtDeviceName;
    private EditText mEtDeviceSerial;

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
        mIbLanRefresh = (ImageButton) findViewById(R.id.ib_lan);
        mIbQrCode = (ImageButton) findViewById(R.id.ib_rc_code);

        mViewDeviceInfo = LayoutInflater.from(AddDeviceActivity.this).inflate(R.layout.add_device_info_input, null);
        mTvAddTip = (TextView) mViewDeviceInfo.findViewById(R.id.tv_add_tip);
        mEtDeviceSerial = (EditText) mViewDeviceInfo.findViewById(R.id.et_device_serial);
        mEtDeviceName = (EditText) mViewDeviceInfo.findViewById(R.id.et_device_name);

        mListView = (ListView) findViewById(R.id.lv_list_account);
    }

    @Override
    public void initData() {
        mDialog = new CommonDialog(this);

        mTitleBar.setTitleBarContent(getString(R.string.addDevice));
        mTitleBar.setOnTitleBarClickListener(this);
        mTitleBar.setFinishShowing(false);

        mIbLanRefresh.setOnClickListener(this);
        mIbQrCode.setOnClickListener(this);

        mListAdapter = new LanListAdapter(this);
        mListView.setAdapter(mListAdapter);

        mListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                //弹出确认添加的对话框
                final DeviceInfo deviceInfo = mListAdapter.getItem(arg2);
                mListView.setItemChecked(arg2, true);
                //如果是已被添加，不弹出对话框
                ArrayList<DeviceInfo> deviceCache = DongSDKProxy.requestGetDeviceListFromCache();
                for (DeviceInfo deviceInfoCache : deviceCache) {
                    if (deviceInfo.deviceSerialNO.equals(deviceInfoCache.deviceSerialNO)) {
                        return;
                    }
                }

                showInAdd(true, null, deviceInfo);
                mDialog.setContent(mViewDeviceInfo);
                mDialog.setNegativeButton(R.string.cancel, null);
                mDialog.setPositiveButton(R.string.sure, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        addDevice();
                    }
                });
                mDialog.show();
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
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LogUtils.i("AddDeviceActivity.clazz-->>> onDestroy...");
        DongSDKProxy.requestLanStopScan();
        DongSDKProxy.unRegisterAccountLanCallback(mAccountProxy);
        DongSDKProxy.clearDongAccountLan();
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
                if (TextUtils.isEmpty(stringExtra)) {
                    return;
                }
                showInAdd(true, stringExtra, null);
                mDialog.setContent(mViewDeviceInfo);
                mDialog.setNegativeButton(R.string.cancel, null);
                mDialog.setPositiveButton(R.string.sure, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        addDevice();
                    }
                });
                mDialog.show();
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
        showInAdd(false, null, null);
        mDialog.setContent(mViewDeviceInfo);
        mDialog.setNegativeButton(R.string.cancel, null);
        mDialog.setPositiveButton(R.string.sure, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                addDevice();
            }
        });
        mDialog.show();
    }

    @Override
    public void onFinishClick() {

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.ib_lan:
                DongSDKProxy.requestLanStartScan();
                break;
            case R.id.ib_rc_code:
                startActivityForResult(new Intent(this, CaptureActivity.class), 0);
                break;
        }
    }

    //添加设备
    private void addDevice() {
        String devName = mEtDeviceName.getText().toString().trim();
        String deviceSer = mEtDeviceSerial.getText().toString().trim();
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

    //点击自动和手动添加设备信息显示
    private void showInAdd(boolean isAuto, String deviceSerialNO, DeviceInfo deviceInfo) {
        if (isAuto) {
            mTvAddTip.setText(R.string.are_you_sure_to_add_device);
            if (deviceInfo != null) {
                mEtDeviceName.setText(deviceInfo.deviceName);
                mEtDeviceSerial.setText(deviceInfo.deviceSerialNO);
            } else {
                mEtDeviceName.setText("");
                mEtDeviceSerial.setText(deviceSerialNO);
            }
            //设置序列号不可编辑
            mEtDeviceSerial.setFocusable(false);
            mEtDeviceSerial.setFocusableInTouchMode(false);
        } else {
            mTvAddTip.setText(R.string.please_input_add_device_info);
            mEtDeviceName.setText("");
            mEtDeviceSerial.setText("");
            //设置序列号可编辑
            mEtDeviceSerial.setFocusable(true);
            mEtDeviceSerial.setFocusableInTouchMode(true);
        }
    }

    private class AddDeviceActivityDongAccountProxy extends
            AbstractDongCallbackProxy.DongAccountCallbackImp {

        @Override
        public int onAuthenticate(InfoUser tInfo) {
            LogUtils.i("AddDeviceActivity.clazz--->>>OnAuthenticate........tInfo:" + tInfo);
            return 0;
        }

        @Override
        public int onNewListInfo() {
            mDeviceList = DongSDKProxy.requestLanGetDeviceListFromCache();
            mListAdapter.setData(mDeviceList);
            mListAdapter.notifyDataSetChanged();
            LogUtils.i("AddDeviceActivity.clazz--->>>OnNewListInfo........mDeviceList:" + mDeviceList);
            return 0;
        }

        @Override
        public int onAddDevice(int nReason, String username) {
            //reason 0-成功 1-无效设备 3-已经被添加 其他-失败
            LogUtils.i("AddDeviceActivity.clazz--->>>OnAddDevice........nReason:"
                    + nReason + ";username:" + username);
            if (mDialog.isShowing())
                mDialog.dismiss();
            if (nReason == 0) {
//                BaseApplication.showToastShortInBottom(R.string.suc);
                mDialog.setMessage(getString(R.string.device_added));
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
