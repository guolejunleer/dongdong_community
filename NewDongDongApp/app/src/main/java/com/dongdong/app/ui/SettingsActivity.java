package com.dongdong.app.ui;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.dd121.louyu.R;
import com.ddclient.configuration.DongConfiguration;
import com.ddclient.dongsdk.DongSDKProxy;
import com.dongdong.app.AppConfig;
import com.dongdong.app.AppContext;
import com.dongdong.app.AppManager;
import com.dongdong.app.base.BaseActivity;
import com.dongdong.app.base.BaseApplication;
import com.dongdong.app.fragment.HomePagerFragment;
import com.dongdong.app.ui.dialog.CommonDialog;
import com.dongdong.app.util.LogUtils;
import com.dongdong.app.widget.TitleBar;
import com.dongdong.app.widget.TitleBar.OnTitleBarClickListener;
import com.igexin.sdk.PushManager;

public class SettingsActivity extends BaseActivity implements
		OnTitleBarClickListener, View.OnClickListener {

	private TitleBar mTitleBar;
	private RelativeLayout mRlPushService, mRlNightMode, mRlAttendedMode,
			mRlOpenVideoInDatanetwork, mRlCleanSession, mRlNewVersion;
	private Button mBtnLoginOut;

	@Override
	protected int getLayoutId() {
		return R.layout.activity_settings;
	}

	@Override
	public void initView() {
		mRlPushService = (RelativeLayout) findViewById(R.id.rl_push_service);
		mRlNightMode = (RelativeLayout) findViewById(R.id.rl_nightmode);
		mRlOpenVideoInDatanetwork = (RelativeLayout) findViewById(R.id.rl_openvideo_in_data_network);
		mRlAttendedMode = (RelativeLayout) findViewById(R.id.rl_attended_mode);
		mRlCleanSession = (RelativeLayout) findViewById(R.id.rl_clean_session);
		mRlNewVersion = (RelativeLayout) findViewById(R.id.rl_about);
		mTitleBar = (TitleBar) findViewById(R.id.tb_title);
		mBtnLoginOut = (Button) findViewById(R.id.btn_loginout);

		mTitleBar.setTitleBarContent(getString(R.string.settings));
		mTitleBar.setOnTitleBarClickListener(this);
		mTitleBar.setAddArrowShowing(false);

		mRlCleanSession.setOnClickListener(this);
		mRlNewVersion.setOnClickListener(this);
		mRlPushService.setOnClickListener(this);
		mRlNightMode.setOnClickListener(this);
		mRlOpenVideoInDatanetwork.setOnClickListener(this);
		mRlAttendedMode.setOnClickListener(this);
		mBtnLoginOut.setOnClickListener(this);
	}

	@Override
	public void initData() {
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (DongConfiguration.mUserInfo == null) {
			mBtnLoginOut.setVisibility(View.INVISIBLE);
		} else {
			mBtnLoginOut.setVisibility(View.VISIBLE);
		}
	}

	@Override
	public void onBackClick() {
		this.finish();
	}

	@Override
	public void onTitleClick() {
	}

	@Override
	public void onAddClick() {
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.rl_push_service:// 推送通知
			BaseApplication.showToastShortInBottom(R.string.building);
			break;
		case R.id.rl_nightmode:// 夜间模式
			BaseApplication.showToastShortInBottom(R.string.building);
			break;
		case R.id.rl_openvideo_in_data_network:// 连接方式
			BaseApplication.showToastShortInBottom(R.string.building);
			break;
		case R.id.rl_attended_mode:// 数据网络下开启视频
			BaseApplication.showToastShortInBottom(R.string.building);
			break;
		case R.id.rl_clean_session:// 清除缓存
			BaseApplication.showToastShortInBottom(R.string.building);
			break;
		case R.id.rl_about:// 关于软件
			startActivity(new Intent(SettingsActivity.this, AboutActivity.class));
			break;
		case R.id.btn_loginout:
			CommonDialog commonDialog = new CommonDialog(SettingsActivity.this);
			commonDialog.setMessage(R.string.isexit);
			commonDialog.setPositiveButton(R.string.exit,
					new OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// 1.关闭推送
							PushManager.getInstance().turnOffPush(
									SettingsActivity.this);
							com.baidu.android.pushservice.PushManager
									.stopWork(BaseApplication.context());
							boolean initedDongAccount = DongSDKProxy
									.isInitedDongAccount();
							// 2.清空SDK信息
							if (initedDongAccount) {
								DongSDKProxy.loginOut();
								DongConfiguration.clearAllData();
								HomePagerFragment.mIsFirstComming = true;
								AppContext.mAppConfig.remove(
										AppConfig.DONG_CONFIG_SHARE_PREF_NAME,
										AppConfig.KEY_DEVICE_ID);
								AppContext.mAppConfig.remove(
										AppConfig.DONG_CONFIG_SHARE_PREF_NAME,
										AppConfig.KEY_IS_LOGIN);
								dialog.dismiss();
								AppManager.getAppManager()
										.finishNOTLMainActivity();
								LogUtils.i("SettingsActivity.clazz--->>>logout!!!!initedDongAccount:"
										+ initedDongAccount);
							}
						}
					});
			commonDialog.setNegativeButton(R.string.cancel, null);
			commonDialog.setCancelable(true);
			commonDialog.show();
			break;
		default:
			break;
		}
	}
}
