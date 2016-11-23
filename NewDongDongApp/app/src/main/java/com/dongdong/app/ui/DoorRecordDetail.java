package com.dongdong.app.ui;

import android.annotation.SuppressLint;
import android.widget.TextView;

import com.dd121.louyu.R;
import com.dongdong.app.base.BaseActivity;
import com.dongdong.app.widget.TitleBar;
import com.dongdong.app.widget.TitleBar.OnTitleBarClickListener;

public class DoorRecordDetail extends BaseActivity implements
		OnTitleBarClickListener {
	private TitleBar mTitleBar;
	private TextView mTvRoomNumber, mTvType, mTvTimeStamp, mTvDeviceName,
			mTvMemberName, mTvIdNumber, mTvComNumber, mTvMobliePhone;

	@Override
	protected int getLayoutId() {
		return R.layout.activity_doorrecorddetail;
	}

	@SuppressLint("CutPasteId")
	@Override
	public void initView() {
		mTitleBar = (TitleBar) this.findViewById(R.id.tb_title);
		mTitleBar.setTitleBarContent(getString(R.string.opendoorrecorddetail));
		mTitleBar.setOnTitleBarClickListener(this);
		mTitleBar.setAddArrowShowing(false);

		mTvRoomNumber = (TextView) findViewById(R.id.tv_roomnumber);
		mTvType = (TextView) findViewById(R.id.tv_type);
		mTvTimeStamp = (TextView) findViewById(R.id.tv_timestamp);
		mTvDeviceName = (TextView) findViewById(R.id.tv_devicename);
		mTvMemberName = (TextView) findViewById(R.id.tv_membername);
		mTvIdNumber = (TextView) findViewById(R.id.tv_idnumber);
		mTvComNumber = (TextView) findViewById(R.id.tv_comnumber);
		mTvMobliePhone = (TextView) findViewById(R.id.tv_mobilephone);
	}

	@Override
	public void initData() {
		String roomnumber = this.getIntent().getStringExtra("roomnumber");
		String type = this.getIntent().getStringExtra("type");
		String timestamp = this.getIntent().getStringExtra("timestamp");
		String devicename = this.getIntent().getStringExtra("devicename");
		String membername = this.getIntent().getStringExtra("membername");
		String idnumber = this.getIntent().getStringExtra("idnumber");
		String comnumber = this.getIntent().getStringExtra("comnumber");
		String mobilephone = this.getIntent().getStringExtra("mobilephone");

		System.out.println("跳转后>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>" + roomnumber+ type + timestamp + devicename + membername + idnumber+ comnumber + mobilephone);
		if (roomnumber == null) {
			mTvRoomNumber.setText("");
		} else {
			mTvRoomNumber.setText(roomnumber);
		}
		if (type == null) {
			mTvType.setText("");
		} else {
			mTvType.setText(type);
		}
		if (timestamp == null) {
			mTvTimeStamp.setText("");
		} else {
			mTvTimeStamp.setText(timestamp);
		}
		if (devicename == null) {
			mTvDeviceName.setText("");
		} else {
			mTvDeviceName.setText(devicename);
		}
		if (membername == null) {
			mTvMemberName.setText("");
		} else {
			mTvMemberName.setText(membername);
		}
		if (idnumber == null) {
			mTvIdNumber.setText("");
		} else {
			mTvIdNumber.setText(idnumber);
		}
		if (comnumber == null) {
			mTvComNumber.setText("");
		} else {
			mTvComNumber.setText(comnumber);
		}
		if (mobilephone == null) {
			mTvMobliePhone.setText("");
		} else {
			mTvMobliePhone.setText(mobilephone);
		}

	}

	@Override
	public void onBackClick() {
		DoorRecordDetail.this.finish();
	}

	@Override
	public void onTitleClick() {
	}

	@Override
	public void onAddClick() {
	}

}
