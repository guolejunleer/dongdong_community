package com.dongdong.app.ui;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dd121.community.R;
import com.dongdong.app.AppContext;
import com.dongdong.app.base.BaseActivity;
import com.dongdong.app.util.LogUtils;
import com.dongdong.app.util.TDevice;
import com.dongdong.app.widget.TitleBar;
import com.dongdong.app.widget.TitleBar.OnTitleBarClickListener;

public class AboutActivity extends BaseActivity implements OnTitleBarClickListener {

    //private TitleBar mTitleBar;
    private TextView mTvCurrentVersion;
    //private RelativeLayout mLayoutVersionUpdate, mLayoutFunctionIntroduction,mLayoutHelp;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_about;
    }

    @Override
    public void initView() {
        TitleBar titleBar = (TitleBar) findViewById(R.id.tb_title);
        titleBar.setTitleBarContent(getString(R.string.about));
        titleBar.setOnTitleBarClickListener(this);
        titleBar.setAddArrowShowing(false);

        mTvCurrentVersion = (TextView) findViewById(R.id.tv_currentversion);
        RelativeLayout layoutVersionUpdate = (RelativeLayout) findViewById(R.id.rl_versionupdate);
        RelativeLayout layoutFunctionIntroduction = (RelativeLayout) findViewById(R.id.rl_functionintroduction);
        RelativeLayout layoutHelp = (RelativeLayout) findViewById(R.id.rl_help);

        layoutFunctionIntroduction.setOnClickListener(onmLayoutClickListener);
        layoutVersionUpdate.setOnClickListener(onmLayoutClickListener);
        layoutHelp.setOnClickListener(onmLayoutClickListener);
    }

    OnClickListener onmLayoutClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.rl_versionupdate:
                    // 版本更新
                    AppContext.showToast(R.string.building, Toast.LENGTH_SHORT, 0, 0);
                    break;
                case R.id.rl_functionintroduction:
                    // 功能介绍
                    AppContext.showToast(R.string.building, Toast.LENGTH_SHORT, 0, 0);
                    break;
                case R.id.rl_help:
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
        String versionName = "V  " + TDevice.getVersionName() + "_beta";
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

    @Override
    public void onFinishClick() {
    }

}
