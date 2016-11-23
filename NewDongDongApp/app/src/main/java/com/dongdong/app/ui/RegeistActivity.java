package com.dongdong.app.ui;

import java.util.Random;

import android.content.DialogInterface;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.dd121.louyu.R;
import com.ddclient.configuration.DongConfiguration;
import com.ddclient.dongsdk.AbstractDongSDKProxy.DongRegisterCallbackImp;
import com.ddclient.dongsdk.DongSDKProxy;
import com.dongdong.app.base.BaseActivity;
import com.dongdong.app.base.BaseApplication;
import com.dongdong.app.ui.dialog.CommonDialog;
import com.dongdong.app.ui.dialog.TipDialogManager;
import com.dongdong.app.util.LogUtils;
import com.dongdong.app.widget.TitleBar;
import com.dongdong.app.widget.TitleBar.OnTitleBarClickListener;

public class RegeistActivity extends BaseActivity implements
		OnTitleBarClickListener, OnClickListener {

	private EditText mEtPhone;
	private EditText mEtSmush;
	private EditText mEtPwd;
	private EditText mEtAgainPwd;
	private Button mBtSmush;
	private Button mBtOK;
	private TitleBar mTitleBar;

	private String mRandomCode;
	private CommonDialog mDialog;
	private TimeCount mTime;

	@Override
	protected int getLayoutId() {
		return R.layout.activity_regeist;
	}

	@Override
	public void initView() {
		mEtPhone = (EditText) findViewById(R.id.et_phone);
		mEtSmush = (EditText) findViewById(R.id.et_smush);
		mEtPwd = (EditText) findViewById(R.id.et_pwd);
		mEtAgainPwd = (EditText) findViewById(R.id.et_again_pwd);
		mBtSmush = (Button) findViewById(R.id.bt_smush);
		mBtOK = (Button) findViewById(R.id.bt_ok);
		mTitleBar = (TitleBar) findViewById(R.id.tb_title);
	}

	@Override
	public void initData() {
		mTime = new TimeCount(60000, 1000);
		mTitleBar.setTitleBarContent(getString(R.string.regeistAcount));
		mTitleBar.setAddArrowShowing(false);
		mTitleBar.setOnTitleBarClickListener(this);
		mBtSmush.setOnClickListener(this);
		mBtOK.setOnClickListener(this);
	}

	class TimeCount extends CountDownTimer {
		public TimeCount(long millisInFuture, long countDownInterval) {
			super(millisInFuture, countDownInterval);
		}

		@Override
		public void onFinish() {
			mBtSmush.setText(getString(R.string.get_verification_code));
			mBtSmush.setClickable(true);
		}

		@Override
		public void onTick(long millisUntilFinished) {
			mBtSmush.setClickable(false);
			mBtSmush.setText(millisUntilFinished / 1000 + "s");
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
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.bt_smush:
			String phoneNumber = mEtPhone.getText().toString();
			if (TextUtils.isEmpty(phoneNumber)) {
				BaseApplication
						.showToastShortInTop(R.string.user_name_can_not_empty);
				return;
			}
			mDialog = new CommonDialog(RegeistActivity.this);
			View view = LayoutInflater.from(RegeistActivity.this).inflate(
					R.layout.loading_dialog, null);
			TextView tipTextView = (TextView) view.findViewById(R.id.tv_tip);
			tipTextView.setText(getString(R.string.get_verification_coding));
			mDialog.setContent(view);
			mDialog.show();
			mRandomCode = (new Random().nextInt(999999) + 100000) + "";
			DongConfiguration.mPhoneNumber = phoneNumber;
			DongSDKProxy
					.intDongRegister(new RegisterActivityDongRegisterProxy());
			DongSDKProxy.requestQueryUser(phoneNumber);
			LogUtils.i("RegistActivity.clazz--->>>bt_get_code........mRandomCode:"
					+ mRandomCode + ",phoneNumber:" + phoneNumber);
			break;
		case R.id.bt_ok:
			if (mEtPhone.getText().toString().equals("")) {
				BaseApplication
						.showToastShortInTop(R.string.user_name_can_not_empty);
			} else if (mEtSmush.getText().toString().equals("")) {
				BaseApplication
						.showToastShortInTop(R.string.verification_code_can_not_empty);
			} else if (!mEtSmush.getText().toString().equals(mRandomCode)) {
				BaseApplication
						.showToastShortInTop(R.string.verification_code_mistake);
			} else if (mEtPwd.getText().toString().equals("")) {
				BaseApplication
						.showToastShortInTop(R.string.user_pwd_can_not_empty);
			} else if (!mEtAgainPwd.getText().toString()
					.equals(mEtPwd.getText().toString())) {
				BaseApplication.showToastShortInTop(R.string.pwd_not_same);
			} else {
				DongSDKProxy.requestSetSecret(mEtPwd.getText().toString(),
						DongConfiguration.mPhoneNumber);
			}
			break;
		default:
			break;
		}
	}

	private class RegisterActivityDongRegisterProxy extends
			DongRegisterCallbackImp {

		@Override
		public int OnQueryUser(int nReason) {
			if (nReason == 0) {// 未注册过
				// 用来发短信，接收验证码
				DongSDKProxy.requestSmsAuth(mRandomCode + "",
						DongConfiguration.mPhoneNumber);

			} else {
				BaseApplication.showToastShortInTop(R.string.phone_registered);
				mDialog.dismiss();
			}
			LogUtils.i("RegistActivity.clazz--->>>OnQueryUser........nReason:"
					+ nReason);
			return 0;
		}

		@Override
		public int OnSmsAuth(int nReason) {
			if (nReason == 0) {
				if (mDialog != null) {
					mDialog.dismiss();
				}
				mTime.start();
			} else {
				BaseApplication
						.showToastShortInTop(R.string.verifaction_failed);
				mDialog.dismiss();
			}
			LogUtils.i("RegistActivity.clazz--->>>OnSmsAuth........nReason:"
					+ nReason);
			return 0;
		}

		@Override
		public int OnSetSecret(int arg0) {
			if (arg0 == 0) {
				mDialog.setMessage(R.string.regeist_success);
				mDialog.setPositiveButton(R.string.loginging,
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								RegeistActivity.this.finish();
							}
						});
				mDialog.setCancelable(false);
				mDialog.show();

			} else {
				BaseApplication.showToastShortInTop(R.string.pwd_failed);
				mDialog.dismiss();
			}
			return 0;
		}

		@Override
		public int OnRegisterError(int nErrNo) {
			LogUtils.i("RegistActivity.clazz--->>>OnRegisterError........nReason:"
					+ nErrNo);
			if (mDialog.isShowing())
				mDialog.dismiss();

			TipDialogManager.showTipDialog(RegeistActivity.this,
					BaseApplication.context().getString(R.string.tip),
					BaseApplication.context()
							.getString(R.string.register_error) + nErrNo);
			return 0;
		}
	}

}
