package com.dongdong.app.ui;

import android.content.Intent;

import com.dd121.community.R;
import com.dongdong.app.MainActivity;
import com.dongdong.app.base.BaseActivity;

public class LoadActivity extends BaseActivity {

    @Override
    protected int getLayoutId() {
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
        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    Thread.sleep(3 * 1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (MainActivity.mIsPushStarted) {// 此时离线推送的话就直接关闭这个界面
                    LoadActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            LoadActivity.this.finish();
                        }
                    });
                    return;
                }
                LoadActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        LoadActivity.this.startActivity(new Intent(LoadActivity.this, MainActivity.class));
                        LoadActivity.this.finish();
                    }
                });
            }
        }).start();
    }

    @Override
    public void onBackPressed() {
    }


}
