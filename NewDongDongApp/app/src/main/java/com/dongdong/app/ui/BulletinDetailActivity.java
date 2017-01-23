package com.dongdong.app.ui;

import android.text.TextUtils;
import android.widget.TextView;

import com.dd121.community.R;
import com.dongdong.app.base.BaseActivity;
import com.dongdong.app.widget.TitleBar;

public class BulletinDetailActivity extends BaseActivity implements
        TitleBar.OnTitleBarClickListener {

    public static final String BULLETIN_TITLE = "title";
    public static final String BULLETIN_NOTICE = "notice";
    public static final String BULLETIN_CREATED = "created";

    private TextView mTvTitle,mTvNotice,mTvCreated;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_bulletin_detail;
    }

    @Override
    public void initView() {
        TitleBar titleBar = (TitleBar) this.findViewById(R.id.tb_title);
        titleBar.setTitleBarContent(getString(R.string.bulletin_detail));
        titleBar.setOnTitleBarClickListener(this);
        titleBar.setAddArrowShowing(false);

        mTvTitle= (TextView) findViewById(R.id.tv_detail_title);
        mTvNotice= (TextView) findViewById(R.id.tv_detail_notice);
        mTvCreated= (TextView) findViewById(R.id.tv_detail_created);
    }

    @Override
    public void initData() {
        String title = this.getIntent().getStringExtra("title");
        String notice= this.getIntent().getStringExtra("notice");
        String created= this.getIntent().getStringExtra("created");

        if (TextUtils.isEmpty(title)) {
            mTvTitle.setText("");
        } else {
            mTvTitle.setText(title);
        }
        if (TextUtils.isEmpty(notice)) {
            mTvNotice.setText("");
        } else {
            mTvNotice.setText(notice);
        }
        if (TextUtils.isEmpty(created)) {
            mTvCreated.setText("");
        } else {
            mTvCreated.setText(created);
        }
    }

    @Override
    public void onBackClick() {
        BulletinDetailActivity.this.finish();
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
