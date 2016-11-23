package com.dongdong.app.ui;

import java.util.ArrayList;

import android.content.Intent;
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

import com.dd121.louyu.R;
import com.ddclient.MobileClientLib.InfoUser;
import com.ddclient.dongsdk.AbstractDongSDKProxy.DongAccountCallbackImp;
import com.ddclient.dongsdk.DeviceInfo;
import com.ddclient.dongsdk.DongSDKProxy;
import com.dongdong.app.adapter.LanListAdapter;
import com.dongdong.app.base.BaseActivity;
import com.dongdong.app.base.BaseApplication;
import com.dongdong.app.ui.dialog.CommonDialog;
import com.dongdong.app.ui.dialog.TipDialogManager;
import com.dongdong.app.util.LogUtils;
import com.dongdong.app.widget.TitleBar;
import com.dongdong.app.widget.TitleBar.OnTitleBarClickListener;
import com.zbar.lib.CaptureActivity;

public class AddDeviceActivity extends BaseActivity implements
		OnTitleBarClickListener, OnClickListener {

	private TitleBar mTitleBar;
	private Button mBtnQrcode;
	private Button mBtLan;
	private EditText mEtDeviceSerial;
	private EditText mDeviceName;
	private LanListAdapter mListAdapter;
	private ListView mListview;
	public ArrayList<DeviceInfo> mDeviceList;
	private CommonDialog mDialog;

	private AddDeviceActivityDongAccountProxy mAccountProxy;

	@Override
	protected int getLayoutId() {
		return R.layout.activity_add_device;
	}

	@Override
	public void initView() {
		mTitleBar = (TitleBar) findViewById(R.id.tb_title);
		mBtnQrcode = (Button) findViewById(R.id.bt_rc_code);
		mEtDeviceSerial = (EditText) findViewById(R.id.et_device_serial);
		mDeviceName = (EditText) findViewById(R.id.device_name);
		mBtLan = (Button) findViewById(R.id.bt_lan);
		mListview = (ListView) findViewById(R.id.lv_list_account);
	}

	@Override
	public void initData() {
		mAccountProxy = new AddDeviceActivityDongAccountProxy();

		mDialog = new CommonDialog(this);
		mTitleBar.setTitleBarContent(getString(R.string.addDevice));
		mTitleBar.setAddContent(getString(R.string.done));
		mTitleBar.setOnTitleBarClickListener(this);
		mBtLan.setOnClickListener(this);
		mBtnQrcode.setOnClickListener(this);

		mListAdapter = new LanListAdapter(this);
		mListview.setAdapter(mListAdapter);
		mListview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				final DeviceInfo deviceInfo = (DeviceInfo) mListAdapter
						.getItem(arg2);
				mDeviceName.setText(deviceInfo.deviceName);
				mEtDeviceSerial.setText(deviceInfo.deviceSerialNO);
			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (!DongSDKProxy.isInitedDongAccountLan()) {
			DongSDKProxy.initDongAccountLan(mAccountProxy);
		}
		DongSDKProxy.requestLanStartScan();
		LogUtils.i("AddDevcieActivity.clazz-->>> onResume...");
	}

	@Override
	protected void onPause() {
		super.onPause();
		DongSDKProxy.requestLanStopScan();
		DongSDKProxy.unRegisterAccountLanCallback();
		DongSDKProxy.clearDongAccountLan();
		LogUtils.i("AddDevcieActivity.clazz-->>> onPause...");
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
		String devName = mDeviceName.getText().toString();
		String deviceSeri = mEtDeviceSerial.getText().toString();
		if (TextUtils.isEmpty(devName) || TextUtils.isEmpty(deviceSeri)) {
			BaseApplication.showToastShortInBottom(R.string.empty);
			return;
		}

		View view = LayoutInflater.from(this).inflate(R.layout.loading_dialog,
				null);
		TextView tipTextView = (TextView) view.findViewById(R.id.tv_tip);
		tipTextView.setText(getString(R.string.wait));
		mDialog.setContent(view);
		mDialog.show();
		DongSDKProxy.requestAddDevice(devName, deviceSeri);
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.bt_lan:
			DongSDKProxy.requestLanStartScan();
			break;
		case R.id.bt_rc_code:
			startActivity(new Intent(this, CaptureActivity.class));
			break;
		default:
			break;
		}
	}

	private class AddDeviceActivityDongAccountProxy extends
			DongAccountCallbackImp {

		@Override
		public int OnAuthenticate(InfoUser tInfo) {
			LogUtils.i("AddDeviceActivity.clazz--->>>OnAuthenticate........tInfo:"
					+ tInfo);
			return 0;
		}

		@Override
		public int OnNewListInfo() {
			mDeviceList = DongSDKProxy.requestLanGetDeviceListFromCache();
			mListAdapter.setData(mDeviceList);
			mListAdapter.notifyDataSetChanged();
			LogUtils.i("AddDeviceActivity.clazz--->>>OnNewListInfo........mDeviceList size:"
					+ mDeviceList.size());
			return 0;
		}

		@Override
		public int OnAddDevice(int nReason, String username) {
			LogUtils.i("AddDeviceActivity.clazz--->>>OnAddDevice........nReason:"
					+ nReason + ";username:" + username);
			if (mDialog.isShowing())
				mDialog.dismiss();
			if (nReason == 0) {
				BaseApplication.showToastShortInBottom(R.string.suc);
			} else if (nReason == 1) {
				BaseApplication.showToastShortInBottom(R.string.serial_error);
			} else if (nReason == 2) {
				BaseApplication.showToastShortInBottom(R.string.device_already_added);
			} else if (nReason == 3) {

				mDialog.setMessage(getString(R.string.device_already_added)
						+ "(" + username + ")");
				mDialog.setCancelable(false);
				mDialog.setPositiveButton(R.string.ok, null);
				mDialog.show();
			}
			return 0;
		}

		@Override
		public int OnUserError(int nErrNo) {
			LogUtils.i("AddDeviceActivity.clazz--->>>OnUserError........nErrNo:"
					+ nErrNo);
			if (mDialog.isShowing())
				mDialog.dismiss();
			TipDialogManager.showTipDialog(
					AddDeviceActivity.this,
					BaseApplication.context().getString(R.string.tip),
					BaseApplication.context().getString(
							R.string.add_devcie_error)
							+ nErrNo);
			return 0;
		}

	}

}
