package com.dongdong.app.ui;

import android.graphics.Bitmap;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.dd121.community.R;
import com.dongdong.app.base.BaseActivity;
import com.dongdong.app.widget.TitleBar;
import com.dongdong.app.widget.TitleBar.OnTitleBarClickListener;

public class VisitorPhotoDetailActivity extends BaseActivity implements
        OnTitleBarClickListener {

    private ImageView mIvVisitorPhoto;
    private TextView mTvDeviceName, mTvRoomNum, mTvPhotoTimestamp, mTvType;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_visitor_photo_detail;
    }

    @Override
    public void initView() {
        TitleBar titleBar = (TitleBar) findViewById(R.id.tb_title);
        titleBar.setTitleBarContent(getString(R.string.visitorphotodetail));
        titleBar.setAddArrowShowing(false);
        titleBar.setOnTitleBarClickListener(this);

        mIvVisitorPhoto = (ImageView) findViewById(R.id.iv_visitor_photo_detail);
        mTvDeviceName = (TextView) findViewById(R.id.tv_device_name);
        mTvRoomNum = (TextView) findViewById(R.id.tv_room_number);
        mTvPhotoTimestamp = (TextView) findViewById(R.id.tv_photo_timestamp);
        mTvType = (TextView) findViewById(R.id.tv_type);
    }

    @Override
    public void initData() {
        Bitmap photo = this.getIntent().getParcelableExtra(VisitorPhotoActivity.INTENT_PHOTO_KEY);
        String deviceName = this.getIntent().getStringExtra(VisitorPhotoActivity.INTENT_DEVICE_NAME_KEY);
        String roomNum = this.getIntent().getStringExtra(VisitorPhotoActivity.INTENT_ROOM_NUMBER_KEY);
        String photoTimestamp = this.getIntent().getStringExtra(VisitorPhotoActivity.INTENT_TIMESTAMP_KEY);
        String type = this.getIntent().getStringExtra(VisitorPhotoActivity.INTENT_TYPE_KEY);

        if (photo != null) {
            mIvVisitorPhoto.setImageBitmap(photo);
        }

        if (!TextUtils.isEmpty(deviceName)) {
            mTvDeviceName.setText(String.format("%s", getString(R.string.device) + deviceName));
        } else {
            mTvDeviceName.setText(getString(R.string.unKnow));
        }

        if (!TextUtils.isEmpty(roomNum)) {
            mTvRoomNum.setText(String.format("%s", getString(R.string.room_number) + roomNum));
        } else {
            mTvRoomNum.setText(getString(R.string.unKnow));
        }

        if (!TextUtils.isEmpty(photoTimestamp)) {
            mTvPhotoTimestamp.setText(String.format("%s", getString(R.string.time) + photoTimestamp));
        } else {
            mTvPhotoTimestamp.setText(getString(R.string.unKnow));
        }

        if (!TextUtils.isEmpty(type)) {
            mTvType.setText(String.format("%s", getString(R.string.type) + type));
        } else {
            mTvType.setText(getString(R.string.unKnow));
        }
    }

    @Override
    public void onBackClick() {
        VisitorPhotoDetailActivity.this.finish();
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
