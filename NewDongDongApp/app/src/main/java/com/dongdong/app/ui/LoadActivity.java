package com.dongdong.app.ui;

import android.content.Intent;

import com.dd121.community.R;
import com.dongdong.app.MainActivity;
import com.dongdong.app.base.BaseActivity;
import com.tencent.stat.StatConfig;
import com.tencent.stat.StatReportStrategy;
import com.tencent.stat.StatService;

public class LoadActivity extends BaseActivity {

    @Override
    protected int getLayoutId() {
        // 腾讯MAT///////////////////////////////////////
        // 打开debug开关，可查看mta上报日志或错误
        // 发布时，请务必要删除本行或设为false
        // StatConfig.setDebugEnable(true);
        initMTAConfig(false);
        StatService.trackCustomEvent(this, "onCreate", "");
        // 自动activity埋点
        StatService.registerActivityLifecycleCallbacks(getApplication());
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
        StatService.onResume(this);// 腾讯MAT 页面开始
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
    protected void onPause() {
        super.onPause();
        // 腾讯MAT 页面结束
        StatService.onPause(this);
    }

    @Override
    public void onBackPressed() {
    }

    /**
     * 根据不同的模式，建议设置的开关状态，可根据实际情况调整，仅供参考。
     *
     * @param isDebugMode 根据调试或发布条件，配置对应的MTA配置
     */
    private void initMTAConfig(boolean isDebugMode) {
        if (isDebugMode) { // 调试时建议设置的开关状态
            // 查看MTA日志及上报数据内容
            StatConfig.setDebugEnable(true);
            // 禁用MTA对app未处理异常的捕获，方便开发者调试时，及时获知详细错误信息。
            StatConfig.setAutoExceptionCaught(false);
        } else { // 发布时，建议设置的开关状态，请确保以下开关是否设置合理
            // 禁止MTA打印日志
            StatConfig.setDebugEnable(false);
            // 根据情况，决定是否开启MTA对app未处理异常的捕获
            StatConfig.setAutoExceptionCaught(true);
            // 选择默认的上报策略
            StatConfig.setStatSendStrategy(StatReportStrategy.APP_LAUNCH);
        }
    }

}
