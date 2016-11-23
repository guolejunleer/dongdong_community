package com.dongdong.app.ui;

import java.util.ArrayList;

import android.app.Dialog;
import android.content.DialogInterface;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.dd121.louyu.R;
import com.ddclient.MobileClientLib.InfoUser;
import com.ddclient.MobileClientLib.InfoWifi;
import com.ddclient.dongsdk.AbstractDongSDKProxy.DongAccountCallbackImp;
import com.ddclient.dongsdk.AbstractDongSDKProxy.DongDeviceSettingCallbackImp;
import com.ddclient.dongsdk.DongSDKProxy;
import com.dongdong.app.AppContext;
import com.dongdong.app.adapter.WifiListAdapter;
import com.dongdong.app.base.BaseActivity;
import com.dongdong.app.ui.dialog.CommonDialog;
import com.dongdong.app.widget.TitleBar;

public class WifiListActivity extends BaseActivity {

	private TitleBar mTitleBar;
	private ArrayList<InfoWifi> mWifiList;
	private ListView mLvlist;
	private WifiListAdapter mWifiListAdapter;
	private CommonDialog mDialog;
	private WifiListActivityDongAccountProxy mAccountProxy;
	private WifiListActivityDongDeviceSettingProxy mDeviceSettingProxy;

	@Override
	protected int getLayoutId() {
		return R.layout.activity_wifi_llist;
	}

	@Override
	public void initView() {
		mTitleBar = (TitleBar) findViewById(R.id.tb_title);
		mLvlist = (ListView) findViewById(R.id.lv_wifi_list);
	}

	@Override
	public void initData() {
		mAccountProxy = new WifiListActivityDongAccountProxy();
		mDeviceSettingProxy = new WifiListActivityDongDeviceSettingProxy();

		mTitleBar.setTitleBarContent("wifi");
		mTitleBar.setAddArrowShowing(false);
		mTitleBar
				.setOnTitleBarClickListener(new TitleBar.OnTitleBarClickListener() {

					@Override
					public void onTitleClick() {

					}

					@Override
					public void onBackClick() {
						WifiListActivity.this.finish();
					}

					@Override
					public void onAddClick() {

					}
				});

		mWifiListAdapter = new WifiListAdapter(this);
		mLvlist.setAdapter(mWifiListAdapter);

		// AppContext.mUser.GetWifiList();
		DongSDKProxy.requestGetWifiList();
		mLvlist.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				final InfoWifi infoWifi = (InfoWifi) mWifiListAdapter
						.getItem(arg2);
				if (!infoWifi.bCur) {
					View diaView = View.inflate(WifiListActivity.this,
							R.layout.login, null);
					final Dialog dialog = new Dialog(WifiListActivity.this,
							R.style.dialog2);

					dialog.setContentView(diaView);
					dialog.show();
					TextView button = (TextView) diaView
							.findViewById(R.id.login);
					TextView button1 = (TextView) diaView
							.findViewById(R.id.cancel);
					final EditText text2 = (EditText) diaView
							.findViewById(R.id.et_password);
					button.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							String password = text2.getText().toString();
							// AppContext.mUser.SetPlatformWifi(infoWifi.Flags,
							// infoWifi.SSID, infoWifi.Mac, password);
							DongSDKProxy.requestSetPlatformWifi(infoWifi.Flags,
									infoWifi.SSID, infoWifi.Mac, password);
							dialog.dismiss();
						}
					});
					button1.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							dialog.dismiss();
						}
					});
				} else {
					mDialog = new CommonDialog(WifiListActivity.this);
					mDialog = new CommonDialog(WifiListActivity.this);
					mDialog.setTitle(WifiListActivity.this
							.getString(R.string.warn));
					mDialog.setMessage(infoWifi.SSID + "");
					mDialog.setPositiveButton(
							WifiListActivity.this.getString(R.string.cancel),
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									// AppContext.mUser.ForgetPlatformWifi(
									// infoWifi.Flags, infoWifi.SSID,
									// infoWifi.Mac);
									DongSDKProxy.requestForgetPlatformWifi(
											infoWifi.Flags, infoWifi.SSID,
											infoWifi.Mac);
								}
							});
					mDialog.setNegativeButton(getString(R.string.cancel), null);
					mDialog.show();
				}
			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();
		// AppContext.mUser.registerDongAccountCallbackListener(this);
		// AppContext.mUser.registerDongDeviceSettingCallbackListener(this);
		DongSDKProxy.registerAccountCallback(mAccountProxy);
		DongSDKProxy.registerDongDeviceSettingCallback(mDeviceSettingProxy);
	}

	@Override
	protected void onPause() {
		super.onPause();
		DongSDKProxy.unRegisterAccountCallback();
		DongSDKProxy.unRegisterDongDeviceSettingCallback();
	}

	private class WifiListActivityDongAccountProxy extends
			DongAccountCallbackImp {

		@Override
		public int OnAuthenticate(InfoUser arg0) {
			return 0;
		}

		@Override
		public int OnUserError(int arg0) {
			// TipDialogManager.showDialog(WifiListActivity.this, mDialog,
			// arg0);
			return 0;
		}
	}

	private class WifiListActivityDongDeviceSettingProxy extends
			DongDeviceSettingCallbackImp {

		@Override
		public int OnWifiList(ArrayList<InfoWifi> arg0) {
			WifiListActivity.this.mWifiList = arg0;
			mWifiListAdapter.setData(mWifiList);
			mWifiListAdapter.notifyDataSetChanged();
			return 0;
		}

		@Override
		public int OnSetPlatformWifi(int arg0) {
			if (arg0 == 0) {
				AppContext.showToastShortInBottom(R.string.connectSucc);
				// AppContext.mUser.GetWifiList();
				DongSDKProxy.requestGetWifiList();
				mWifiListAdapter.setData(mWifiList);
				mWifiListAdapter.notifyDataSetChanged();
			} else {
				AppContext.showToastShortInBottom(R.string.ConFailed);
			}
			return 0;
		}
	}

}
