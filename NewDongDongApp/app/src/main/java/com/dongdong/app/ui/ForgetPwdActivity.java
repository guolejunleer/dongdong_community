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

public class ForgetPwdActivity extends BaseActivity implements
		OnTitleBarClickListener, OnClickListener {

	private EditText mEtPhone, mEtSmush, mEtPwd, mEtAgainPwd;
	private Button mbtSmush, mbtOK;
	private String randomCode;
	private TitleBar mTitleBar;
	private CommonDialog mDialog;
	private TimeCount mTime;

	private ForgetPwdActivityDongRegisterProxy mDongRegisterProxy;

	@Override
	protected int getLayoutId() {
		return R.layout.activity_forget_psw;
	}

	@Override
	public void initView() {
		mEtPhone = (EditText) findViewById(R.id.et_phone);
		mEtSmush = (EditText) findViewById(R.id.et_smush);
		mEtPwd = (EditText) findViewById(R.id.et_pwd);
		mEtAgainPwd = (EditText) findViewById(R.id.et_again_pwd);
		mbtSmush = (Button) findViewById(R.id.bt_smush);
		mbtOK = (Button) findViewById(R.id.bt_ok);
		mTitleBar = (TitleBar) findViewById(R.id.tb_title);
	}

	@Override
	public void initData() {
		mTime = new TimeCount(60000, 1000);
		mTitleBar.setTitleBarContent(getString(R.string.reset_pwd));
		mTitleBar.setAddArrowShowing(false);
		mTitleBar.setOnTitleBarClickListener(this);
		mbtSmush.setOnClickListener(this);
		mbtOK.setOnClickListener(this);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		DongSDKProxy.unRegisterDongRegisterCallback();
	}

	class TimeCount extends CountDownTimer {
		public TimeCount(long millisInFuture, long countDownInterval) {
			super(millisInFuture, countDownInterval);
		}

		@Override
		public void onFinish() {
			mbtSmush.setText(getString(R.string.get_verification_code));
			mbtSmush.setClickable(true);
		}

		@Override
		public void onTick(long millisUntilFinished) {
			mbtSmush.setClickable(false);
			mbtSmush.setText(millisUntilFinished / 1000 + "s");
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
			String phoneNum = mEtPhone.getText().toString();
			if (TextUtils.isEmpty(phoneNum)) {
				BaseApplication
						.showToastShortInTop(R.string.user_name_can_not_empty);
				return;
			}
			mDialog = new CommonDialog(ForgetPwdActivity.this);
			View view = LayoutInflater.from(ForgetPwdActivity.this).inflate(
					R.layout.loading_dialog, null);
			TextView tipTextView = (TextView) view.findViewById(R.id.tv_tip);
			tipTextView.setText(getString(R.string.get_verification_coding));
			mDialog.setContent(view);
			mDialog.show();
			randomCode = (new Random().nextInt(999999) + 100000) + "";
			DongConfiguration.mPhoneNumber = phoneNum;
			boolean initedDongRegister = DongSDKProxy.isInitedDongRegister();
			mDongRegisterProxy = new ForgetPwdActivityDongRegisterProxy();
			if (initedDongRegister) {
				DongSDKProxy.registerDongRegisterCallback(mDongRegisterProxy);
			} else {
				DongSDKProxy.intDongRegister(mDongRegisterProxy);
			}
			DongSDKProxy.requestQueryUser(phoneNum);
			LogUtils.i("ForgetPwdActivity.clazz--->>>bt_get_code........initedDongRegister:"
					+ initedDongRegister);
			break;
		case R.id.bt_ok:
			if (mEtPhone.getText().toString().equals("")) {
				BaseApplication
						.showToastShortInTop(R.string.user_name_can_not_empty);
			} else if (mEtSmush.getText().toString().equals("")) {
				BaseApplication
						.showToastShortInTop(R.string.verification_code_can_not_empty);
			} else if (!mEtSmush.getText().toString().equals(randomCode)) {
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

	private class ForgetPwdActivityDongRegisterProxy extends
			DongRegisterCallbackImp {
		@Override
		public int OnQueryUser(int nReason) {
			if (nReason == 0) {// 未注册过
				BaseApplication
						.showToastShortInTop(R.string.phone_not_registered);
				mDialog.dismiss();
			} else {
				// regeist.smsAuth(randomCode, AppContext.phoneNumber);//
				// 用来发短信，接收验证码
				DongSDKProxy.requestSmsAuth(randomCode + "",
						DongConfiguration.mPhoneNumber);
			}
			LogUtils.i("ForgetPwdActivity.clazz--->>>OnQueryUser........nReason:"
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
			LogUtils.i("ForgetPwdActivity.clazz--->>>OnSmsAuth........nReason:"
					+ nReason);
			return 0;
		}

		@Override
		public int OnSetSecret(int arg0) {
			if (arg0 == 0) {
				mDialog.setTitle(R.string.tip);
				mDialog.setMessage(R.string.update_pwd_succ);
				mDialog.setPositiveButton(R.string.ok,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								ForgetPwdActivity.this.finish();
							}
						});
				mDialog.show();

			} else {
				BaseApplication.showToastShortInTop(R.string.update_pwd_failed);
				mDialog.dismiss();
			}
			return 0;
		}

		@Override
		public int OnRegisterError(int nErrNo) {
			if (mDialog.isShowing())
				mDialog.dismiss();
			TipDialogManager.showTipDialog(ForgetPwdActivity.this,
					BaseApplication.context().getString(R.string.tip),
					BaseApplication.context()
							.getString(R.string.register_error) + nErrNo);
			return 0;
		}
	}

}
