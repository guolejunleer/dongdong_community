package com.dongdong.app.ui;

import android.widget.TextView;

import com.dd121.community.R;
import com.dongdong.app.base.BaseActivity;
import com.dongdong.app.widget.TitleBar;
import com.dongdong.app.widget.TitleBar.OnTitleBarClickListener;

public class DoorRecordDetailActivity extends BaseActivity implements
		OnTitleBarClickListener {
	private TextView mTvRoomNumber, mTvType, mTvTimeStamp, mTvDeviceName,
			mTvMemberName, mTvIdNumber, mTvComNumber, mTvPhone;

	@Override
	protected int getLayoutId() {
		return R.layout.activity_door_record_detail;
	}

	@Override
	public void initView() {
		TitleBar titleBar = (TitleBar) this.findViewById(R.id.tb_title);
		titleBar.setTitleBarContent(getString(R.string.opendoorrecorddetail));
		titleBar.setOnTitleBarClickListener(this);
		titleBar.setAddArrowShowing(false);

		mTvRoomNumber = (TextView) findViewById(R.id.tv_room_number);
		mTvType = (TextView) findViewById(R.id.tv_type);
		mTvTimeStamp = (TextView) findViewById(R.id.tv_timestamp);
		mTvDeviceName = (TextView) findViewById(R.id.tv_device_name);
		mTvMemberName = (TextView) findViewById(R.id.tv_member_name);
		mTvIdNumber = (TextView) findViewById(R.id.tv_id_number);
		mTvComNumber = (TextView) findViewById(R.id.tv_com_number);
		mTvPhone = (TextView) findViewById(R.id.tv_mobile_phone);
	}

	@Override
	public void initData() {
		String roomNumber = this.getIntent().getStringExtra("roomNumber");
		String type = this.getIntent().getStringExtra("type");
		String timestamp = this.getIntent().getStringExtra("timestamp");
		String deviceName = this.getIntent().getStringExtra("deviceName");
		String memberName = this.getIntent().getStringExtra("memberName");
		String idNumber = this.getIntent().getStringExtra("idNumber");
		String comNumber = this.getIntent().getStringExtra("comNumber");
		String mobilePhone = this.getIntent().getStringExtra("mobilePhone");

		if (roomNumber == null) {
			mTvRoomNumber.setText("");
		} else {
			mTvRoomNumber.setText(roomNumber);
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
		if (deviceName == null) {
			mTvDeviceName.setText("");
		} else {
			mTvDeviceName.setText(deviceName);
		}
		if (memberName == null) {
			mTvMemberName.setText("");
		} else {
			mTvMemberName.setText(memberName);
		}
		if (idNumber == null) {
			mTvIdNumber.setText("");
		} else {
			mTvIdNumber.setText(idNumber);
		}
		if (comNumber == null) {
			mTvComNumber.setText("");
		} else {
			mTvComNumber.setText(comNumber);
		}
		if (mobilePhone == null) {
			mTvPhone.setText("");
		} else {
			mTvPhone.setText(mobilePhone);
		}

	}

	@Override
	public void onBackClick() {
		DoorRecordDetailActivity.this.finish();
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

}
