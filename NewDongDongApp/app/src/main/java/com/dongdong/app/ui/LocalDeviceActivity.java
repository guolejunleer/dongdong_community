package com.dongdong.app.ui;

import java.util.ArrayList;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.dd121.louyu.R;
import com.ddclient.MobileClientLib.InfoUser;
import com.ddclient.configuration.DongConfiguration;
import com.ddclient.dongsdk.AbstractDongSDKProxy.DongAccountCallbackImp;
import com.ddclient.dongsdk.DeviceInfo;
import com.ddclient.dongsdk.DongSDKProxy;
import com.dongdong.app.adapter.LanListAdapter;
import com.dongdong.app.base.BaseActivity;
import com.dongdong.app.ui.dialog.CommonDialog;
import com.dongdong.app.util.LogUtils;
import com.dongdong.app.widget.TitleBar;
import com.dongdong.app.widget.TitleBar.OnTitleBarClickListener;

public class LocalDeviceActivity extends BaseActivity implements
		OnTitleBarClickListener {

	private ListView mLvDevice;
	private LanListAdapter mListAdapter;

	public ArrayList<DeviceInfo> mDeviceList;
	// private DongLan mDongLan;
	// private DongLan mDongLan1;
	private DeviceInfo mDevice;
	private TitleBar mTitleBar;
	private TextView mTvWifiName;
	private CommonDialog mDialog;

	private LocalDeviceActivityDongAccountProxy mDongAccountProxy;

	@Override
	protected int getLayoutId() {
		return R.layout.local_device;
	}

	@Override
	public void initView() {
		mTitleBar = (TitleBar) findViewById(R.id.tb_title);
		mLvDevice = (ListView) findViewById(R.id.lv_list_account);
		mTvWifiName = (TextView) findViewById(R.id.wifiname);
	}

	@Override
	public void initData() {
		mTitleBar.setTitleBarContent(getString(R.string.localdevice));
		mTitleBar.setAddContent(getString(R.string.refrush));
		mTitleBar.setOnTitleBarClickListener(this);
		mListAdapter = new LanListAdapter(this);
		mLvDevice.setAdapter(mListAdapter);
		mLvDevice.setOnItemClickListener(mListItemClick);
		// mDongLan = new DongLan(this);
		// mDongLan.requestDeviceList();
		mDongAccountProxy = new LocalDeviceActivityDongAccountProxy();
		boolean initDongAccountLan = DongSDKProxy.isInitedDongAccountLan();
		DongSDKProxy.initDongAccountLan(mDongAccountProxy);
		DongSDKProxy.requestLanDeviceListFromPlatform();
		LogUtils.i("SerchLocalDeviceSetting.clazz--->>>onCreate........initDongAccountLan:"
				+ initDongAccountLan);
	}

	@Override
	protected void onResume() {
		super.onResume();
		// mDongLan.LanStartScan();
		mTvWifiName.setText("wifi   " + getWifiName());
		DongSDKProxy.requestLanStartScan();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		// mDongLan.LanStopScan();
		boolean initedDongAccountLan = DongSDKProxy.isInitedDongAccountLan();
		if (initedDongAccountLan) {
			DongSDKProxy.requestLanStopScan();
			DongSDKProxy.unRegisterAccountLanCallback();
			DongSDKProxy.requestLanLoginOut();
		}
		LogUtils.i("SerchLocalDeviceSetting.clazz--->>>onDestroy........initedDongAccountLan:"
				+ initedDongAccountLan);
	}

	// 获取当前wifi名字
	public String getWifiName() {
		WifiManager wifiMgr = (WifiManager) this
				.getSystemService(Context.WIFI_SERVICE);
		WifiInfo info = wifiMgr.getConnectionInfo();
		String wifiId = info != null ? info.getSSID() : null;
		return wifiId;
	}

	private OnItemClickListener mListItemClick = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			final DeviceInfo object = mListAdapter.getItem(arg2);
			View diaView = View.inflate(LocalDeviceActivity.this,
					R.layout.login, null);
			final Dialog dialog = new Dialog(LocalDeviceActivity.this,
					R.style.dialog2);
			dialog.setContentView(diaView);
			dialog.show();

			TextView login = (TextView) diaView.findViewById(R.id.login);
			TextView cancel = (TextView) diaView.findViewById(R.id.cancel);
			final EditText password = (EditText) diaView
					.findViewById(R.id.et_password);
			login.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					String pwd = password.getText().toString();
					mDialog = new CommonDialog(LocalDeviceActivity.this);
					View view = LayoutInflater.from(LocalDeviceActivity.this)
							.inflate(R.layout.loading_dialog, null);
					TextView tipTextView = (TextView) view
							.findViewById(R.id.tv_tip);
					tipTextView
							.setText(getString(R.string.waiting_connection_30));
					mDialog.setContent(view);
					mDialog.show();
					mDevice = object;
					// mDongLan1 = new DongLan(LocalDeviceActivity.this);
					// mDongLan1.LanExploreLogin(object.dwDeviceID,
					// object.DeviceName, psw);
					// LogUtils.i("LocalDevice.clazz-->>object.DeviceName:"
					// + object.DeviceName + " ,psw:" + psw);
					//
					// dialog.dismiss();
					DongSDKProxy.initDongAccountLan(mDongAccountProxy);
					DongSDKProxy.requestLanExploreLogin(mDevice.dwDeviceID,
							mDevice.deviceName, pwd);
					LogUtils.i("SerchLocalDeviceSetting.clazz--->>>tvLogin....re....mDeviceInfo:"
							+ mDevice);
					dialog.dismiss();
				}
			});
			cancel.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					dialog.dismiss();
				}
			});
		}

	};

	@Override
	public void onBackClick() {
		finish();
	}

	@Override
	public void onTitleClick() {

	}

	@Override
	public void onAddClick() {
		// mDongLan.LanFlush();
		DongSDKProxy.requestLanFlush();
	}

	private class LocalDeviceActivityDongAccountProxy extends
			DongAccountCallbackImp {

		@Override
		public int OnAuthenticate(InfoUser tInfo) {
			// GViewerXApplication.mDongLan = dongLan1;
			// GViewerXApplication.mUser = dongLan1.getDongdongAccount();
			// GViewerXApplication app = (GViewerXApplication)
			// SerchLocalDeviceSetting.this
			// .getApplication();
			// app.cacheCamera(mDeviceInfo);
			if (mDialog != null)
				mDialog.dismiss();
			startActivity(new Intent(LocalDeviceActivity.this,
					VideoViewActivity.class));
			DongConfiguration.mDeviceInfo = mDevice;
			LogUtils.i("SerchLocalDeviceSetting.clazz--->>>OnAuthenticate........tInfo:"
					+ tInfo);
			return 0;
		}

		@Override
		public int OnNewListInfo() {
			mDeviceList = DongSDKProxy.requestLanGetDeviceListFromCache();
			mListAdapter.setData(mDeviceList);
			mListAdapter.notifyDataSetChanged();
			LogUtils.i("SerchLocalDeviceSetting.clazz--->>>OnNewListInfo........mDeviceList.size():"
					+ mDeviceList.size());
			return 0;
		}

		@Override
		public int OnUserError(int nErrNo) {
			if (mDialog != null) {
				mDialog.dismiss();
			}
			LogUtils.i("SerchLocalDeviceSetting.clazz--->>>OnUserError........nErrNo:"
					+ nErrNo);
			return 0;
		}
	}
}
