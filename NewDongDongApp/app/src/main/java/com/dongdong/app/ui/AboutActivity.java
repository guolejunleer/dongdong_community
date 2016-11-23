package com.dongdong.app.ui;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dd121.louyu.R;
import com.dongdong.app.AppContext;
import com.dongdong.app.base.BaseActivity;
import com.dongdong.app.util.LogUtils;
import com.dongdong.app.util.TDevice;
import com.dongdong.app.widget.TitleBar;
import com.dongdong.app.widget.TitleBar.OnTitleBarClickListener;

public class AboutActivity extends BaseActivity implements OnTitleBarClickListener {

    private TitleBar mTitleBar;
    private TextView mTvCurrentVersion;
    private RelativeLayout mLayoutVersionUpdate, mLayoutFunctionIntroduction,
            mLayoutHelp;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_about;
    }

    @Override
    public void initView() {
        mTitleBar = (TitleBar) findViewById(R.id.tb_title);
        mTitleBar.setTitleBarContent(getString(R.string.about));
        mTitleBar.setOnTitleBarClickListener(this);
        mTitleBar.setAddArrowShowing(false);

        mTvCurrentVersion = (TextView) findViewById(R.id.currentversion);
        mLayoutVersionUpdate = (RelativeLayout) findViewById(R.id.versionupdate);
        mLayoutFunctionIntroduction = (RelativeLayout) findViewById(R.id.functionintroduction);
        mLayoutHelp = (RelativeLayout) findViewById(R.id.help);

        mLayoutFunctionIntroduction.setOnClickListener(onmLayoutClickListener);
        mLayoutVersionUpdate.setOnClickListener(onmLayoutClickListener);
        mLayoutHelp.setOnClickListener(onmLayoutClickListener);
    }

    OnClickListener onmLayoutClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.versionupdate:
                    // 版本更新
                    AppContext.showToast(R.string.building, Toast.LENGTH_SHORT, 0,
                            0);
                    break;
                case R.id.functionintroduction:
                    // 功能介绍
                    AppContext.showToast(R.string.building, Toast.LENGTH_SHORT, 0,
                            0);
                    break;
                case R.id.help:
                    // 帮助
                    AppContext.showToast(R.string.building, Toast.LENGTH_SHORT, 0,
                            0);
                    break;
                default:
                    break;
            }
        }
    };

    // 设置当前版本号
    public void setCurrentVersion() {
        String versionName = "version beta:" + TDevice.getVersionName();
        mTvCurrentVersion.setText(versionName);
        LogUtils.i("AboutActivity.clazz--->>> versionName :" + versionName);
    }

    @Override
    public void initData() {
        setCurrentVersion();
    }

    @Override
    public void onBackClick() {
        AboutActivity.this.finish();
    }

    @Override
    public void onTitleClick() {
    }

    @Override
    public void onAddClick() {
    }

}
