package com.dongdong.app.ui;

import android.text.TextUtils;
import android.widget.TextView;

import com.dd121.community.R;
import com.dongdong.app.base.BaseActivity;
import com.dongdong.app.bean.OpenDoorRecordBean;
import com.dongdong.app.widget.TitleBar;
import com.dongdong.app.widget.TitleBar.OnTitleBarClickListener;

public class OpenDoorDetailActivity extends BaseActivity implements
        OnTitleBarClickListener {
    private TextView mTvRoomNumber, mTvType, mTvTimeStamp, mTvDeviceName,
            mTvMemberName, mTvComNumber;
//            mTvIdNumber, mTvPhone;

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
//        mTvIdNumber = (TextView) findViewById(R.id.tv_id_number);
        mTvComNumber = (TextView) findViewById(R.id.tv_com_number);
//        mTvPhone = (TextView) findViewById(R.id.tv_mobile_phone);
    }

    @Override
    public void initData() {
        OpenDoorRecordBean openDoorRecordBean = (OpenDoorRecordBean) this.getIntent().
                getSerializableExtra(OpenDoorActivity.INTENT_OPEN_DOOR_RECORDER_BEAN);

        if (TextUtils.isEmpty(openDoorRecordBean.getRoomNumber())) {
            mTvRoomNumber.setText("");
        } else {
            mTvRoomNumber.setText(openDoorRecordBean.getRoomNumber());
        }
        if (TextUtils.isEmpty(openDoorRecordBean.getType())) {
            mTvType.setText("");
        } else {
            mTvType.setText(openDoorRecordBean.getType());
        }
        if (TextUtils.isEmpty(openDoorRecordBean.getTimestamp())) {
            mTvTimeStamp.setText("");
        } else {
            mTvTimeStamp.setText(openDoorRecordBean.getTimestamp());
        }
        if (TextUtils.isEmpty(openDoorRecordBean.getDeviceName())) {
            mTvDeviceName.setText("");
        } else {
            mTvDeviceName.setText(openDoorRecordBean.getDeviceName());
        }
        if (TextUtils.isEmpty(openDoorRecordBean.getMemberName())) {
            mTvMemberName.setText("");
        } else {
            mTvMemberName.setText(openDoorRecordBean.getMemberName());
        }
//        if (TextUtils.isEmpty(idNumber)) {
//            mTvIdNumber.setText("");
//        } else {
//            //用于显示的加*身份证
//            mTvIdNumber.setText(idNumber.substring(0, 5) + "*******" + idNumber.substring(14));
//        }
        if (TextUtils.isEmpty(openDoorRecordBean.getComNumber())) {
            mTvComNumber.setText("");
        } else {
            mTvComNumber.setText(openDoorRecordBean.getComNumber());
        }
//        if (TextUtils.isEmpty(mobilePhone)) {
//            mTvPhone.setText("");
//        } else {
//            mTvPhone.setText(mobilePhone);
//        }
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
