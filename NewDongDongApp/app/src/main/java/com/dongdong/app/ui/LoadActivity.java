package com.dongdong.app.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;

import com.dd121.louyu.R;
import com.dongdong.app.MainActivity;
import com.dongdong.app.base.BaseActivity;
import com.dongdong.app.ui.dialog.TipDialogManager;
import com.dongdong.app.ui.dialog.TipDialogManager.OnTipDialogButtonClick;
import com.dongdong.app.util.LogUtils;
import com.dongdong.app.util.TDevice;

public class LoadActivity extends BaseActivity implements
        OnTipDialogButtonClick {

    @Override
    protected int getLayoutId() {
        // 隐藏状态栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        return R.layout.activity_mymain;
    }

    @Override
    public void initView() {
    }

    @Override
    public void initData() {
    }

    @Override
    protected void onResume() {
        super.onResume();
        int networkType = TDevice.getNetworkType();
        if (networkType == 0) {
            TipDialogManager.showWithoutNetworDialog(this, this);
        } else if (networkType == 2 || networkType == 3) {
            TipDialogManager.showNormalTipDialog(this, this, R.string.tip,
                    R.string.tip_choose_net, R.string.continues,
                    R.string.cancel);
        } else {
            onPositiveButtonClick();
        }
        LogUtils.i("LoadActivity.clazz--->>>onResume......networkType:"
                + networkType);
    }

    @Override
    public void onPositiveButtonClick() {
        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    Thread.sleep(3 * 1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (MainActivity.mIsPushStarted) {// 此时离线推送的话就直接关闭这个界面
                    LoadActivity.this.finish();
                    return;
                }
                LoadActivity.this.startActivity(new Intent(LoadActivity.this,
                        MainActivity.class));
            }
        }).start();
    }

    @Override
    public void onNegativeButtonClick() {
        finish();
    }
}
