package com.dongdong.app.ui;

import java.util.ArrayList;

import android.content.DialogInterface;
import android.content.Intent;
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
import com.ddclient.MobileClientLib.InfoUser;
import com.ddclient.dongsdk.AbstractDongSDKProxy.DongAccountCallbackImp;
import com.ddclient.dongsdk.DeviceInfo;
import com.ddclient.dongsdk.DongSDKProxy;
import com.dongdong.app.AppConfig;
import com.dongdong.app.AppContext;
import com.dongdong.app.adapter.AuthorizedAccountListAdapter;
import com.dongdong.app.base.BaseActivity;
import com.dongdong.app.base.BaseApplication;
import com.dongdong.app.bean.UserInfoBean;
import com.dongdong.app.ui.dialog.CommonDialog;
import com.dongdong.app.util.LogUtils;
import com.dongdong.app.util.PhoneMessUtils;
import com.dongdong.app.widget.TitleBar;
import com.dongdong.app.widget.TitleBar.OnTitleBarClickListener;

public class AuthAccountActivity extends BaseActivity implements
		OnTitleBarClickListener, OnClickListener {

	private TitleBar mTitleBar;
	private Button mBtAuth;
	private EditText mEtAccount;
	private ListView mListview;
	private AuthorizedAccountListAdapter mListAdapter;
	private ArrayList<InfoUser> mUserList;
	private UserInfoBean mUserInfoBean;
	private String mAccountName;
	private CommonDialog mDialog, mDialog2;

	private PhoneMessUtils mPhoneMess;

	private DeviceInfo mDeviceInfo;
	private AuthAccountActivityDongAccountProxy mAccountProxy;

	@Override
	protected int getLayoutId() {
		return R.layout.activity_authorized_account;
	}

	@Override
	public void initView() {
		mTitleBar = (TitleBar) findViewById(R.id.tb_title);
		mBtAuth = (Button) findViewById(R.id.bt_auth);
		mEtAccount = (EditText) findViewById(R.id.et_account);
		mListview = (ListView) findViewById(R.id.lv_list_account);
	}

	@Override
	public void initData() {
		mAccountProxy = new AuthAccountActivityDongAccountProxy();
		Intent intent = getIntent();
		mDeviceInfo = (DeviceInfo) intent
				.getSerializableExtra(AppConfig.BUNDLE_KEY_DEVICE_INFO);
		LogUtils.i("AuthAccoutnActivity.clazz--->>> initData mDevcieInfo:"
				+ mDeviceInfo);
		DongSDKProxy.requestGetDeviceAuthorizeAccounts(mDeviceInfo.dwDeviceID);

		mDialog = new CommonDialog(this);
		mDialog2 = new CommonDialog(this);

		mTitleBar.setTitleBarContent(mDeviceInfo.deviceName);
		mTitleBar.setAddArrowShowing(false);
		mTitleBar.setOnTitleBarClickListener(this);
		mListAdapter = new AuthorizedAccountListAdapter(this);
		mListview.setAdapter(mListAdapter);

		mBtAuth.setOnClickListener(this);
		mListview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				mUserInfoBean =mListAdapter.getItem(arg2);
				mDialog.setMessage(R.string.deleteAuthorization);
				mDialog.setPositiveButton(R.string.ok,
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								View view = LayoutInflater.from(
										AuthAccountActivity.this).inflate(
										R.layout.loading_dialog, null);
								TextView tipTextView = (TextView) view
										.findViewById(R.id.tv_tip);
								tipTextView.setText(getString(R.string.wait));
								mDialog2.setContent(view);
								mDialog2.show();
								DongSDKProxy.requestDeleteDevice(
										mUserInfoBean.getUserInfo().userID,
										mDeviceInfo.dwDeviceID);
							}
						});
				mDialog.setNegativeButton(getString(R.string.cancel), null);
				mDialog.show();
			}
		});

	}

	private class CheckPhoneMessThread extends Thread {

		@Override
		public void run() {
			mPhoneMess = new PhoneMessUtils(AuthAccountActivity.this);
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			AuthAccountActivity.this.runOnUiThread(new Runnable() {

				@Override
				public void run() {
					mListAdapter.setConnectUsernameAndPhoneNum(mPhoneMess
							.getPhoneMessBeanList());
					mListAdapter.notifyDataSetChanged();
				}
			});
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		DongSDKProxy.registerAccountCallback(mAccountProxy);
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
	}

	@Override
	public void onFinishClick() {
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.bt_auth:
			mAccountName = mEtAccount.getText().toString();
			if (mAccountName.equals("")) {
				AppContext.showToastShortInBottom(R.string.empty_tip);
			}
			for (InfoUser info : mUserList) {
				if (info.userName.equals(mAccountName)) {
					AppContext.showToastShortInBottom(R.string.yesOrNo);
				}
			}
			View view = LayoutInflater.from(AuthAccountActivity.this).inflate(
					R.layout.loading_dialog, null);
			TextView tipTextView = (TextView) view.findViewById(R.id.tv_tip);
			tipTextView.setText(getString(R.string.wait));
			mDialog2.setContent(view);
			mDialog2.show();
			DongSDKProxy.requestAuthorize(mAccountName, mDeviceInfo.dwDeviceID);
			break;
		default:
			break;
		}
	}

	private class AuthAccountActivityDongAccountProxy extends
			DongAccountCallbackImp {

		@Override
		public int OnAuthenticate(InfoUser tInfo) {
			LogUtils.i("LoginActivity.clazz--->>>OnAuthenticate........tInfo:"
					+ tInfo);
			return 0;
		}

		@Override
		public int OnAddDeviceUser(int result, int userid) {
			if (mDialog != null) {
				mDialog.dismiss();
			}
			if (mDialog2 != null) {
				mDialog2.dismiss();
			}
			if (result == 0) {
				mUserList.add(new InfoUser(userid, mAccountName, new byte[1],
						""));
				mListAdapter.setData(mUserList);
				mListAdapter.notifyDataSetChanged();
				BaseApplication.showToastShortInBottom(R.string.suc);
			} else {
				BaseApplication.showToastShortInBottom(R.string.fail);
			}

			LogUtils.i("AuthAccountActivity.clazz--->>>OnAddDeviceUser........result:"
					+ result);
			return 0;
		}

		@Override
		public int OnDelDevice(int result) {
			if (mDialog != null) {
				mDialog.dismiss();
			}
			if (mDialog2 != null) {
				mDialog2.dismiss();
			}
			for (InfoUser infoUser : mUserList) {
				if (infoUser.userName
						.equals(mUserInfoBean.getUserInfo().userName)) {
					mUserList.remove(infoUser);
					break;
				}
			}
			if (result == 0) {
				mListAdapter.setData(mUserList);
				mListAdapter.notifyDataSetChanged();
				BaseApplication.showToastShortInBottom(R.string.suc);

			} else {
				BaseApplication.showToastShortInBottom(R.string.fail);
			}
			LogUtils.i("AuthAccountActivity.clazz--->>>OnDelDevice........result:"
					+ result);
			return 0;
		}

		@Override
		public int OnGetDeviceUserInfo(ArrayList<InfoUser> infoUsers) {
			mUserList = infoUsers;
			if (mDialog != null) {
				mDialog.dismiss();
			}
			mListAdapter.setData(mUserList);
			mListAdapter.notifyDataSetChanged();
			new CheckPhoneMessThread().start();// 查找手机上存在手机号的人的名称
			LogUtils.i("AuthAccountActivity.clazz--->>>OnGetDeviceUserInfo........infoUsers:" + infoUsers);
			return 0;
		}

		@Override
		public int OnSetDeviceName(int result) {
			if (result == 0) {
				if (mDialog != null) {
					mDialog.dismiss();
				}
				BaseApplication.showToastShortInBottom(getString(R.string.suc));
			} else {
				if (mDialog != null) {
					mDialog.dismiss();
				}
				BaseApplication.showToastShortInBottom(getString(R.string.fail));
			}
			LogUtils.i("AuthAccountActivity.clazz--->>>OnSetDeviceName result:"
					+ result);
			return 0;
		}

		@Override
		public int OnUserError(int nErrNo) {
			LogUtils.i("LoginActivity.clazz--->>>OnUserError........nErrNo:"
					+ nErrNo);
			return 0;
		}
	}
}
