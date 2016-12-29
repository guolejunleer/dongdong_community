package com.dongdong.app.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dd121.community.R;
import com.ddclient.configuration.DongConfiguration;
import com.ddclient.dongsdk.AbstractDongCallbackProxy;
import com.ddclient.dongsdk.DeviceInfo;
import com.ddclient.dongsdk.DongSDKProxy;
import com.ddclient.jnisdk.InfoUser;
import com.dongdong.app.AppConfig;
import com.dongdong.app.AppContext;
import com.dongdong.app.base.BaseActivity;
import com.dongdong.app.base.BaseApplication;
import com.dongdong.app.ui.dialog.CommonDialog;
import com.dongdong.app.util.LogUtils;
import com.dongdong.app.util.TDevice;
import com.dongdong.app.widget.TitleBar;
import com.dongdong.app.widget.TitleBar.OnTitleBarClickListener;

public class DeviceSettingsActivity extends BaseActivity implements
        OnTitleBarClickListener, OnClickListener {

    private TitleBar mTitleBar;
    private TextView mDeviceSer;
    private EditText mEtDeviceName;
    private TextView mTvDefaultDevice;
    private TextView mTvAuthorizedAccount;
    private TextView mTvDeleteDevice;
    private RelativeLayout mRlDefaultDevice;
    private Button mBtUpdateDeviceName;

    private CommonDialog mUpdateDialog, mDeleteDialog;
    private DeviceInfo mDeviceInfo;
    private DeviceSettingsActivityDongAccountProxy mAccountProxy
            = new DeviceSettingsActivityDongAccountProxy();

    @Override
    protected int getLayoutId() {
        return R.layout.activity_device_settings;
    }

    @Override
    public void initView() {
        mTitleBar = (TitleBar) findViewById(R.id.tb_title);
        mDeviceSer = (TextView) findViewById(R.id.tv_device_serial);
        mTvAuthorizedAccount = (TextView) findViewById(R.id.tv_authorizationaccount);
        mEtDeviceName = (EditText) findViewById(R.id.tv_device_name);
        mRlDefaultDevice = (RelativeLayout) findViewById(R.id.rl_default_device);
        mTvDefaultDevice = (TextView) findViewById(R.id.tv_defaultText);
        mBtUpdateDeviceName = (Button) findViewById(R.id.bt_update);
        mTvDeleteDevice = (TextView) findViewById(R.id.tv_delete_device);
    }

    @Override
    public void initData() {
        mTitleBar.setAddArrowShowing(false);
        mTitleBar.setOnTitleBarClickListener(this);
        mUpdateDialog = new CommonDialog(this);
        mDeleteDialog = new CommonDialog(this);

        Intent intent = getIntent();
        mDeviceInfo = (DeviceInfo) intent
                .getSerializableExtra(AppConfig.BUNDLE_KEY_DEVICE_INFO);
        mDeviceSer.setText(String.format("%s", getString(R.string.device_serial) + " "
                + mDeviceInfo.deviceSerialNO));
        mEtDeviceName.setText(mDeviceInfo.deviceName);
        mTitleBar.setTitleBarContent(mDeviceInfo.deviceName);

        int defaultDeviceId = (int) AppContext.mAppConfig.getConfigValue(
                AppConfig.DONG_CONFIG_SHARE_PREF_NAME,
                AppConfig.KEY_DEFAULT_DEVICE_ID, 0);
        if (defaultDeviceId == mDeviceInfo.dwDeviceID) {
            mTvDefaultDevice.setText(getString(R.string.yes));
        } else {
            mTvDefaultDevice.setText(DeviceSettingsActivity.this.getResources()
                    .getString(R.string.no));
        }
        mBtUpdateDeviceName.setOnClickListener(this);
        mTvDeleteDevice.setOnClickListener(this);
        mRlDefaultDevice.setOnClickListener(this);
        mTvAuthorizedAccount.setOnClickListener(this);

        LogUtils.i("DeviceSettingsActivity.clazz--->>>initData mDeviceInfo:"
                + mDeviceInfo + ",defaultDeviceId:" + defaultDeviceId);

    }

    @Override
    protected void onResume() {
        super.onResume();
        DongSDKProxy.registerAccountCallback(mAccountProxy);
    }

    @Override
    protected void onPause() {
        super.onPause();
        DongSDKProxy.unRegisterAccountCallback(mAccountProxy);
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
            case R.id.bt_update:
                View view = LayoutInflater.from(this).inflate(R.layout.loading_dialog, null);
                TextView tipTextView = (TextView) view.findViewById(R.id.tv_tip);
                tipTextView.setText(getString(R.string.wait));
                mUpdateDialog.setContent(view);
                mUpdateDialog.show();
                DongSDKProxy.requestSetDeviceName(mDeviceInfo.dwDeviceID,
                        mEtDeviceName.getText().toString());
                LogUtils.i("DeviceSettingsActivity.clazz--->>>onClick........mDeviceInfo.dwDeviceID:"
                        + mDeviceInfo.dwDeviceID
                        + ",mTvDeviceName.getText().toString():"
                        + mEtDeviceName.getText().toString());
                break;
            case R.id.tv_delete_device:
                mUpdateDialog.setMessage(R.string.deldeteDevice);
                mUpdateDialog.setPositiveButton(getString(R.string.sure),
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                View view = LayoutInflater.from(
                                        DeviceSettingsActivity.this).inflate(
                                        R.layout.loading_dialog, null);
                                TextView tipTextView = (TextView) view
                                        .findViewById(R.id.tv_tip);
                                tipTextView.setText(getString(R.string.wait));
                                mDeleteDialog.setContent(view);
                                mDeleteDialog.show();
                                DongSDKProxy.requestDeleteDevice(
                                        DongConfiguration.mUserInfo.userID,
                                        mDeviceInfo.dwDeviceID);
                            }
                        });
                mUpdateDialog.setNegativeButton(getString(R.string.cancel), null);
                mUpdateDialog.show();
                break;
            case R.id.rl_default_device:
                // 可以优化用户体验,提示用户是否确认
                int deviceId = mDeviceInfo.dwDeviceID;
                int localDeviceId = (int) AppContext.mAppConfig
                        .getConfigValue(AppConfig.DONG_CONFIG_SHARE_PREF_NAME,
                                DongConfiguration.mUserInfo.userID + "", 0);
                if (localDeviceId == deviceId) {
                    mTvDefaultDevice.setText(DeviceSettingsActivity.this
                            .getResources().getString(R.string.no));
                    deviceId = 0;
                } else {
                    mTvDefaultDevice.setText(DeviceSettingsActivity.this
                            .getResources().getString(R.string.yes));
                    DongConfiguration.mDeviceInfo = mDeviceInfo;
                }
                AppContext.mAppConfig.setConfigValue(
                        AppConfig.DONG_CONFIG_SHARE_PREF_NAME,
                        DongConfiguration.mUserInfo.userID + "", deviceId);
                break;
            case R.id.tv_authorizationaccount:
                if (TDevice.devieType(mDeviceInfo, 23)) {
                    BaseApplication.showToastShortInBottom(R.string.no_permissions);
                    return;
                }
                Intent intent = new Intent(this, AuthAccountActivity.class);
                intent.putExtra(AppConfig.BUNDLE_KEY_DEVICE_INFO, mDeviceInfo);
                startActivity(intent);
                break;
            default:
                break;
        }
    }

    private class DeviceSettingsActivityDongAccountProxy extends
            AbstractDongCallbackProxy.DongAccountCallbackImp {

        @Override
        public int onAuthenticate(InfoUser tInfo) {
            LogUtils.i("DeviceSettingsActivity.clazz--->>>OnAuthenticate........tInfo:"
                    + tInfo);
            return 0;
        }

        @Override
        public int onConnect() {
            LogUtils.i("DeviceSettingsActivity.clazz--->>>OnConnect........");
            return 0;
        }

        @Override
        public int onDelDevice(int result) {
            LogUtils.i("DeviceSettingsActivity.clazz--->>>OnDelDevice........result:"
                    + result);
            if (mUpdateDialog != null && mUpdateDialog.isShowing()) {
                mUpdateDialog.dismiss();
            }
            if (mDeleteDialog != null && mDeleteDialog.isShowing()) {
                mDeleteDialog.dismiss();
            }
            if (result == 0) {
                BaseApplication.showToastShortInBottom(R.string.suc);
                finish();
            } else {
                BaseApplication.showToastShortInBottom(R.string.fail);
            }
            return 0;
        }

        @Override
        public int onSetDeviceName(int result) {
            LogUtils.i("DeviceSettings.clazz--->>>OnSetDeviceName........result:"
                    + result);
            if (result == 0) {
                if (mUpdateDialog != null) {
                    mUpdateDialog.dismiss();
                }
                BaseApplication.showToastShortInBottom(R.string.suc);
            } else {
                if (mUpdateDialog != null) {
                    mUpdateDialog.dismiss();
                }
                BaseApplication.showToastShortInBottom(R.string.fail);
            }
            return 0;
        }

        @Override
        public int onUserError(int nErrNo) {
            LogUtils.i("DeviceSettings.clazz--->>>OnUserError........nErrNo:"
                    + nErrNo);
            return 0;
        }
    }
}
