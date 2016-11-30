package com.dongdong.app.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;

import com.dd121.community.R;
import com.dongdong.app.base.BaseActivity;
import com.dongdong.app.ui.dialog.CommonDialog;
import com.dongdong.app.widget.TitleBar;

public class MoreSettingActivity extends BaseActivity implements OnClickListener {

	private TitleBar mTitleBar;
	private RelativeLayout mRlDeviceMessage;
	private RelativeLayout mRlWifiSetting;
	private RelativeLayout mRlRecordSetting;
	private RelativeLayout mRlRestartDevice;

	@Override
	protected int getLayoutId() {
		return R.layout.activity_more_setting;
	}

	@Override
	public void initView() {
		mTitleBar = (TitleBar) findViewById(R.id.tb_title);
		mRlDeviceMessage = (RelativeLayout) findViewById(R.id.rl_devicemessage);
		mRlWifiSetting = (RelativeLayout) findViewById(R.id.rl_wifisetting);
		mRlRecordSetting = (RelativeLayout) findViewById(R.id.rl_recorddevice);
		mRlRestartDevice = (RelativeLayout) findViewById(R.id.rl_restartdevice);
	}

	@Override
	public void initData() {
		mTitleBar.setTitleBarContent(getString(R.string.deviceMessage));
		mTitleBar.setAddArrowShowing(false);
		mTitleBar.setOnTitleBarClickListener(new TitleBar.OnTitleBarClickListener() {

					@Override
					public void onTitleClick() {

					}

					@Override
					public void onBackClick() {
						MoreSettingActivity.this.finish();
					}

					@Override
					public void onAddClick() {

					}
					@Override
					public void onFinishClick() {
					}
				});
		mRlWifiSetting.setOnClickListener(this);
		mRlDeviceMessage.setOnClickListener(this);
		mRlRecordSetting.setOnClickListener(this);
		mRlRestartDevice.setOnClickListener(this);

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.rl_wifisetting:
//			startActivity(new Intent(MoreSettingActivity.this, WifiListActivity.class));
			break;
		case R.id.rl_devicemessage:
			// DeviceInfo deviceInfo = MonitorActivity.mMonitorActivityInstance
			// .getDevice();
			// if (deviceInfo == null) {
			// AppContext.showToast(R.string.noDevice, Toast.LENGTH_SHORT, 0,
			// 0);
			// return;
			// }
			// startActivity(new Intent(MoreSetting.this,
			// com.dongdong.app.ui.DeviceInfo.class));
			break;
		case R.id.rl_recorddevice:
			break;
		case R.id.rl_restartdevice:
			CommonDialog dialog = new CommonDialog(MoreSettingActivity.this);
			dialog.setTitle(R.string.warn);
			dialog.setMessage(R.string.researtDivece);
			dialog.setPositiveButton(R.string.ok,
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							// AppContext.mUser.SystemCommand((short) 2, 0);
							dialog.dismiss();
						}
					});
			dialog.setNegativeButton(R.string.cancel, null);
			dialog.show();
			break;
		default:
			break;
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
	}
}
