package com.dongdong.app.ui;

import android.text.TextUtils;
import android.widget.TextView;

import com.dd121.community.R;
import com.dongdong.app.base.BaseActivity;
import com.dongdong.app.widget.TitleBar;
import com.dongdong.app.widget.TitleBar.OnTitleBarClickListener;

public class OpenDoorDetailActivity extends BaseActivity implements
        OnTitleBarClickListener {
    private TextView mTvRoomNumber, mTvType, mTvTimeStamp, mTvDeviceName,
            mTvMemberName, mTvIdNumber, mTvComNumber, mTvPhone;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_open_door_detail;
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

        if (TextUtils.isEmpty(roomNumber)) {
            mTvRoomNumber.setText("");
        } else {
            mTvRoomNumber.setText(roomNumber);
        }
        if (TextUtils.isEmpty(type)) {
            mTvType.setText("");
        } else {
            mTvType.setText(type);
        }
        if (TextUtils.isEmpty(timestamp)) {
            mTvTimeStamp.setText("");
        } else {
            mTvTimeStamp.setText(timestamp);
        }
        if (TextUtils.isEmpty(deviceName)) {
            mTvDeviceName.setText("");
        } else {
            mTvDeviceName.setText(deviceName);
        }
        if (TextUtils.isEmpty(memberName)) {
            mTvMemberName.setText("");
        } else {
            mTvMemberName.setText(memberName);
        }
        if (TextUtils.isEmpty(idNumber)) {
            mTvIdNumber.setText("");
        } else {
            // 用于显示的加*身份证
            //mTvIdNumber.setText(idNumber.substring(0, 5) + "*******" + idNumber.substring(14));
        }
        if (TextUtils.isEmpty(comNumber)) {
            mTvComNumber.setText("");
        } else {
            mTvComNumber.setText(comNumber);
        }
        if (TextUtils.isEmpty(mobilePhone)) {
            mTvPhone.setText("");
        } else {
            mTvPhone.setText(mobilePhone);
        }

    }

    @Override
    public void onBackClick() {
        OpenDoorDetailActivity.this.finish();
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
