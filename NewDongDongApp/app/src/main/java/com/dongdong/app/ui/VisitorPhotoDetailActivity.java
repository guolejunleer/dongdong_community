package com.dongdong.app.ui;

import android.graphics.Bitmap;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.dd121.community.R;
import com.dongdong.app.base.BaseActivity;
import com.dongdong.app.bean.VisitorPhotoBean;
import com.dongdong.app.cache.CacheHelper;
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
        VisitorPhotoBean visitorPhotoBean = (VisitorPhotoBean) this.getIntent().getSerializableExtra
                (VisitorPhotoActivity.INTENT_VISITOR_PHOTO_BEAN);
        if (!TextUtils.isEmpty(visitorPhotoBean.getPhotoUrl())) {
            Bitmap photo = new CacheHelper().getBitmapFromMemoryCache(
                    visitorPhotoBean.getPhotoUrl(), VisitorPhotoActivity.mVisitorPhotoAdapter.getMemoryCache());
            mIvVisitorPhoto.setImageBitmap(photo);
        }

        if (!TextUtils.isEmpty(visitorPhotoBean.getDeviceName())) {
            mTvDeviceName.setText(String.format("%s", getString(R.string.device) + visitorPhotoBean.getDeviceName()));
        } else {
            mTvDeviceName.setText(getString(R.string.unKnow));
        }

        if (!TextUtils.isEmpty(visitorPhotoBean.getRoomValue())) {
            mTvRoomNum.setText(String.format("%s", getString(R.string.room_number) + visitorPhotoBean.getRoomValue()));
        } else {
            mTvRoomNum.setText(getString(R.string.unKnow));
        }

        if (!TextUtils.isEmpty(visitorPhotoBean.getPhotoTimestamp())) {
            mTvPhotoTimestamp.setText(String.format("%s", getString(R.string.time) + visitorPhotoBean.getPhotoTimestamp()));
        } else {
            mTvPhotoTimestamp.setText(getString(R.string.unKnow));
        }

        if (!TextUtils.isEmpty(visitorPhotoBean.getType())) {
            mTvType.setText(String.format("%s", getString(R.string.type) + visitorPhotoBean.getType()));
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
