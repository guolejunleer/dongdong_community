package com.dongdong.app.base;

import android.app.Activity;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.dongdong.app.AppManager;
import com.dongdong.app.interf.BaseViewInterface;
import com.dongdong.app.ui.LoadActivity;
import com.dongdong.app.ui.VideoViewActivity;
import com.dongdong.app.util.LogUtils;
import com.dongdong.app.util.StatusBarCompatUtils;

public abstract class BaseActivity extends Activity implements
        BaseViewInterface {

    protected LayoutInflater mInflater;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        onBeforeSetContentLayout();
        if (getLayoutId() != 0) {
            setContentView(getLayoutId());
        }
        if (this instanceof VideoViewActivity) {
            LogUtils.i("BaseActivity.clazz--->>> we don't compat status color");
        } else if (this instanceof LoadActivity) {
            // Òþ²Ø×´Ì¬À¸
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
            StatusBarCompatUtils.compat(this, Color.parseColor("#00000000"));
            LogUtils.i("BaseActivity.clazz--->>> we don't compat status color and " +
                    "set navigation bar transparent");
        } else {
            StatusBarCompatUtils.compat(this);
        }
        init(savedInstanceState);
        initView();
        initData();
        AppManager.getAppManager().addActivity(this);
    }

    protected void onBeforeSetContentLayout() {
    }

    protected int getLayoutId() {
        return 0;
    }

    protected View inflateView(int resId) {
        return mInflater.inflate(resId, null);
    }

    protected boolean hasBackButton() {
        return false;
    }

    protected void init(Bundle savedInstanceState) {
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
