package com.dongdong.app.ui;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.dd121.community.R;
import com.ddclient.MobileClientLib.InfoUser;
import com.ddclient.configuration.DongConfiguration;
import com.ddclient.dongsdk.AbstractDongSDKProxy.DongAccountCallbackImp;
import com.ddclient.dongsdk.AbstractDongSDKProxy.DongDeviceCallbackImp;
import com.ddclient.dongsdk.AbstractDongSDKProxy.DongDeviceSettingCallbackImp;
import com.ddclient.dongsdk.DeviceInfo;
import com.ddclient.dongsdk.DongSDKProxy;
import com.dongdong.app.AppConfig;
import com.dongdong.app.base.BaseActivity;
import com.dongdong.app.base.BaseApplication;
import com.dongdong.app.data.SoundPlay;
import com.dongdong.app.ui.dialog.CommonDialog;
import com.dongdong.app.ui.dialog.TipDialogManager;
import com.dongdong.app.util.LogUtils;
import com.dongdong.app.util.TDevice;

import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR1)
public class VideoViewActivity extends BaseActivity implements OnClickListener,
        OnTouchListener, OnGestureListener {

    // 左右滑动距离标志
    private static final float FLIP_DISTANCE = 80 * TDevice.getDensity();

    // 1.第一组
    private FrameLayout mFlVideo;
    private SurfaceView mSurfaceView;
    private ImageView mIvDongIcon;
    private ImageView mIvLockImage;

    // 2.第二组
    private View mControlParent;
    private TextView mTvVideoQuality;
    private TextView mTvUploadDataTip;
    private TextView mTvDownloadDataTip;
    private ImageView mIvScreenChange;

    // 3.第三组
    private LinearLayout mLlControl;//功能模块
    private LinearLayout mLlFunction;
    private LinearLayout mLlAccept;

    // 4.第四组
//    private TextView mTvUnlock;
//    private TextView mTvTakePicture;
    private TextView mTvHandFree;
    private TextView mTvAudio;
    private TextView mTvVideo;

    // 5.第五组
    private TextView mTvHangup;
    private TextView mTvAccept;

    private GestureDetector mGestureDetector;

    // 下面是状态位
    private boolean isMicroOn;// 麦克风是否打开
    private boolean isHandsFree;// true-免提，false--听筒
    private boolean isVideoOn;// 是否观看视频
    private boolean isVideoSuccess;// 视频界面是否成功播放
    private boolean shouldPlayNextDevice;

    private int mLight;// 视频亮度
    private short mAudio;// 视频音量大小

    private AudioManager mAudioManager;
    private MediaPlayer mMediaPlayer = new MediaPlayer();

    private boolean isActive;// 是推送还是主动监视
    private Timer mTimer;// 定时器
    private int mShouldPlayNextDeviceCount;// 友善提示计时标志
    private int mDownloadDataZeroCount;// 检测下载视频数据计时标志
    private int mCloseVideoActivityCount;// 检测下载视频数据为0，过5s关闭这个界面
    private int mDeviceBusyCount;
    private boolean isDeviceBusy;

    private CommonDialog mConnDeviceStateDialog;
    private CommonDialog mTipDialog;
    private SoundPlay mSoundPlay;
    private long mUnlockLastTime, mPictureLastTime, mHandsFreeLastTime,
            mSpkLastTime, mVideoLastTime;

    // private String mDeviceID;
    private DeviceInfo mDeviceInfo;
    // private VideoViewActivityDongAccountCallbackImp mDongAccountCallBackImpl;
    private VideoViewActivityDongDeviceCallBackImpl mDongDeviceCallBackImpl;
    // private VideoViewActivityDongDeviceSettingImpl mDongDeviceSettingImpl;

    private PhoneReceiver mReceiver;
    private NetBroadcastReceiver mNetReceiver;
    private VideoViewActivityDongAccountCallbackImp mDongAccountCallBackImpl;
    private VideoViewActivityDongDeviceSettingImpl mDongDeviceSettingImpl;

    @Override
    protected int getLayoutId() {
        // 隐藏状态栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        return R.layout.activity_video_view;
    }

    @Override
    public void initView() {
        mFlVideo = (FrameLayout) findViewById(R.id.fl_video);
        mSurfaceView = (SurfaceView) findViewById(R.id.sv_video);
        mIvDongIcon = (ImageView) findViewById(R.id.iv_dong_sign);
        mIvLockImage = (ImageView) findViewById(R.id.iv_openLock);

        mControlParent = findViewById(R.id.fl_video_view_control_parent);
        mTvVideoQuality = (TextView) findViewById(R.id.tv_video_quality_cotrol);
        ImageView ivLight = (ImageView) findViewById(R.id.iv_light_control);
        ImageView ivVideoAudio = (ImageView) findViewById(R.id.iv_video_audio_control);
        mTvUploadDataTip = (TextView) findViewById(R.id.iv_upload_data_tip);
        mTvDownloadDataTip = (TextView) findViewById(R.id.iv_download_data_tip);
        mIvScreenChange = (ImageView) findViewById(R.id.iv_screen_change);

        mLlControl = (LinearLayout) findViewById(R.id.ll_control);
        mLlFunction = (LinearLayout) findViewById(R.id.ll_function);
        mLlAccept = (LinearLayout) findViewById(R.id.ll_accept);

        TextView tvUnlock = (TextView) findViewById(R.id.tv_unlock);
        TextView tvTakePicture = (TextView) findViewById(R.id.tv_take_picture);
        mTvHandFree = (TextView) findViewById(R.id.tv_hand_free);
        mTvAudio = (TextView) findViewById(R.id.tv_audio);
        mTvVideo = (TextView) findViewById(R.id.tv_video);

        mTvHangup = (TextView) findViewById(R.id.tv_hang_up);
        mTvAccept = (TextView) findViewById(R.id.tv_accept);

        mGestureDetector = new GestureDetector(this, this);

        mFlVideo.setOnTouchListener(this);
        mFlVideo.setLongClickable(true);

        mTvVideoQuality.setOnClickListener(this);
        ivLight.setOnClickListener(this);
        ivVideoAudio.setOnClickListener(this);
        mIvScreenChange.setOnClickListener(this);

        tvUnlock.setOnClickListener(this);
        tvTakePicture.setOnClickListener(this);
        mTvHandFree.setOnClickListener(this);
        mTvAudio.setOnClickListener(this);
        mTvVideo.setOnClickListener(this);

        mTvHangup.setOnClickListener(this);
        mTvAccept.setOnClickListener(this);
    }

    @Override
    public void initData() {
        mDongAccountCallBackImpl = new VideoViewActivityDongAccountCallbackImp();
        try {
            mMediaPlayer.setDataSource(this, Uri.parse("android.resource://"
                    + this.getPackageName() + "/" + R.raw.doorbell1));
            mMediaPlayer.setLooping(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        mTimer = new Timer();
        mTimer.schedule(new MyTimerTask(), new Date(), 1000);

        mConnDeviceStateDialog = new CommonDialog(this);
        mTipDialog = new CommonDialog(this);

        mSoundPlay = new SoundPlay(VideoViewActivity.this);
        mAudioManager = (AudioManager) this
                .getSystemService(Context.AUDIO_SERVICE);

        // 后台在线推送时，自动点亮屏幕
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        LogUtils.i("log5", "VideoViewActivity.clazz-->>initData...");
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 注册来电广播
        mReceiver = new PhoneReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(TelephonyManager.ACTION_PHONE_STATE_CHANGED);
        filter.addAction(Intent.ACTION_NEW_OUTGOING_CALL);
        registerReceiver(mReceiver, filter);

        mNetReceiver = new NetBroadcastReceiver();//注册网络广播
        filter = new IntentFilter();
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(mNetReceiver, filter);

        boolean initDongAccountLan = DongSDKProxy.isInitedDongAccountLan();
        if (initDongAccountLan) {
            DongSDKProxy.registerAccountLanCallback(mDongAccountCallBackImpl);
        } else {
            DongSDKProxy.registerAccountCallback(mDongAccountCallBackImpl);
        }

        Bundle bundle = getIntent().getBundleExtra(AppConfig.INTENT_BUNDLE_KEY);
        //得到设备ID
        String mDeviceID = bundle.getString(AppConfig.BUNDLE_KEY_DEVICE_ID, "");
        //是否主动监视
        isActive = bundle.getBoolean(AppConfig.BUNDLE_KEY_INITIATIVE, false);
        if (TextUtils.isEmpty(mDeviceID) && isActive) {// 1.主动监视
            mDeviceInfo = DongConfiguration.mDeviceInfo;
            // 设置功能按钮界面，主动进来是对讲、挂断按钮
            mTvAudio.setVisibility(View.VISIBLE);
            mTvVideo.setVisibility(View.GONE);
            mTvAccept.setVisibility(View.GONE);
            mTvHangup.setText(R.string.stop);
        } else {// 2. 推送进来?????????????????????????
            ArrayList<DeviceInfo> list = DongConfiguration.mDeviceInfoList =
                    DongSDKProxy.requestGetDeviceListFromCache();
            for (DeviceInfo deviceInfo : list) {//通过推动过来的设备ID,获取帐号下对应的设备
                if (mDeviceID.equals(String.valueOf(deviceInfo.dwDeviceID))) {
                    mDeviceInfo = deviceInfo;
                }
            }
            playMusic(mMediaPlayer);
            isVideoOn = true;
            mTvAudio.setVisibility(View.GONE);
            mTvVideo.setVisibility(View.VISIBLE);
            mTvAccept.setVisibility(View.VISIBLE);
            mTvHangup.setText(R.string.reject);
        }
        LogUtils.i("VideoViewActivity.clazz--->>>onResume ... mPlayDevice:"
                + mDeviceInfo + ",initDongAccountLan:" + initDongAccountLan
                + ",mDeviceID:" + mDeviceID + ",isActive:" + isActive);
        startVideoPlay();
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mReceiver);
        unregisterReceiver(mNetReceiver);
        if (DongSDKProxy.isInitedDongAccountLan()) {
            DongSDKProxy.unRegisterAccountLanCallback(mDongAccountCallBackImpl);
        } else {
            DongSDKProxy.unRegisterAccountCallback(mDongAccountCallBackImpl);
        }
        DongSDKProxy.unRegisterDongDeviceCallback(mDongDeviceCallBackImpl);
        DongSDKProxy.unRegisterDongDeviceSettingCallback(mDongDeviceSettingImpl);
        LogUtils.i("log5", "VideoViewActivity.clazz-->>onPause...unregister");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (DongSDKProxy.isInitedDongDevice() && DongSDKProxy.isInitedDongDeviceSetting()) {// 用户没点击挂断后也要停止播放，释放资源等操作
            stopVideo();
        }
        if (mMediaPlayer != null) {
            mMediaPlayer.release();
        }
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
        LogUtils.i("log5", "VideoViewActivity.clazz-->>onDestroy...");
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mSurfaceView.requestLayout();
        if (TDevice.isLandscape()) {
            mLlControl.setOrientation(LinearLayout.HORIZONTAL);
            mLlFunction.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT, 1));
            mLlAccept.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT, 1));
        } else {
            mLlControl.setOrientation(LinearLayout.VERTICAL);
            mLlFunction.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT, 1));
            mLlAccept.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT, 1));
        }
    }

    private void startVideoPlay() {
        if (mDeviceInfo == null) {
            BaseApplication.showToastShortInBottom("没有找到这台设备,请关闭界面!");
            return;
        }
        mDongDeviceCallBackImpl = new VideoViewActivityDongDeviceCallBackImpl();
        DongSDKProxy.initDongDevice(mDongDeviceCallBackImpl);
        LogUtils.i("VideoViewActivity.clazz--->>>videoPlay ... initDongDevice");

        mDongDeviceSettingImpl = new VideoViewActivityDongDeviceSettingImpl();
        DongSDKProxy.initDongDeviceSetting(mDongDeviceSettingImpl);
        LogUtils.i("VideoViewActivity.clazz--->>>videoPlay ... initDongDeviceSetting");

        //start work
        DongSDKProxy.requstStartPlayDevice(this, mSurfaceView, mDeviceInfo);
        DongSDKProxy.requestRealtimePlay(DongSDKProxy.PLAY_TYPE_VIDEO);
        LogUtils.i("VideoViewActivity.clazz--->>>videoPlay ... requestRealtimePlay");
    }

    private void stopVideo() {
        stopMusic(mMediaPlayer);
        DongSDKProxy.requestClosePhoneSound();
        if (isMicroOn) {
            DongSDKProxy.requestClosePhoneMic();// 关闭 手机麦克风
        }
        if (mDongDeviceCallBackImpl != null) {
            isMicroOn = false;
            isVideoSuccess = false;
            DongSDKProxy.requestStopDeice();
            DongSDKProxy.requestReleaseAudio();
        }
    }

    private void playMusic(MediaPlayer mp) {
        try {
            mp.prepare();
        } catch (Exception e) {
            e.printStackTrace();
        }
        mp.start();
    }

    private void stopMusic(MediaPlayer mp) {
        if (mp.isPlaying()) {
            mp.stop();
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return mGestureDetector.onTouchEvent(event);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.tv_unlock:// 开锁
                if (Math.abs(System.currentTimeMillis() - mUnlockLastTime) <= 1000) {
                    return;
                }
                DongSDKProxy.requestDOControl();
                // 图片渐变模糊度始终
                AlphaAnimation aa = new AlphaAnimation(1f, 0f);
                // 渐变时间
                aa.setDuration(1000);
                // 展示图片渐变动画
                mIvLockImage.startAnimation(aa);
                // 渐变过程监听
                aa.setAnimationListener(new AnimationListener() {

                    /**
                     * 动画开始时
                     */
                    @Override
                    public void onAnimationStart(Animation animation) {
                        System.out.println("动画开始...");
                        mIvLockImage.setVisibility(View.VISIBLE);
                    }

                    /**
                     * 重复动画时
                     */
                    @Override
                    public void onAnimationRepeat(Animation animation) {
                        System.out.println("动画重复...");
                    }

                    /**
                     * 动画结束时
                     */
                    @Override
                    public void onAnimationEnd(Animation animation) {
                        System.out.println("动画结束...");
                        mIvLockImage.setVisibility(View.GONE);
                        mIvLockImage.clearAnimation();
                    }
                });
                mUnlockLastTime = System.currentTimeMillis();
                LogUtils.i("VideoViewActivity.clazz-->>onClick tv_unlock");
                break;
            case R.id.tv_take_picture:// 拍照
                if (Math.abs(System.currentTimeMillis() - mPictureLastTime) <= 1000) {
                    return;
                }
                if (isVideoSuccess) {
                    DongSDKProxy.requestTakePicture(AppConfig.SD_TAKE_PICTURE_PATH, mDeviceInfo);
                    mSoundPlay.play(1, 0);
                    BaseApplication.showToastShortInBottom(R.string.captureSuccess);
                }
                mPictureLastTime = System.currentTimeMillis();
                LogUtils.i("VideoViewActivity.clazz-->>onClick tv_take_picture isVideoSuccess:"
                        + isVideoSuccess);
                break;
            case R.id.tv_hand_free:// 免提
                if (Math.abs(System.currentTimeMillis() - mHandsFreeLastTime) <= 1000) {
                    return;
                }
                if (!isHandsFree) {
                    mAudioManager.setMode(AudioManager.MODE_NORMAL);// 扬声器
                    setCompoundTopDrawables(mTvHandFree, R.mipmap.hand_free_pressed);
                } else {
                    mAudioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);// 听筒
                    setCompoundTopDrawables(mTvHandFree, R.mipmap.hand_free_normal);
                }
                mHandsFreeLastTime = System.currentTimeMillis();
                LogUtils.i("VideoViewActivity.clazz-->>onClick tv_hand_free isHandsFree:"
                        + isHandsFree);
                isHandsFree = !isHandsFree;
                break;
            case R.id.tv_audio:// 对讲
                if (Math.abs(System.currentTimeMillis() - mSpkLastTime) <= 1000) {
                    return;
                }
                stopMusic(mMediaPlayer);
                if (!isMicroOn) {
                    DongSDKProxy.requestRealtimePlay(DongSDKProxy.PLAY_TYPE_AUDIO);// 打开设备音响
                    DongSDKProxy.requestOpenPhoneMic();// 打开手机麦克风
                    DongSDKProxy.requestOpenPhoneSound();// 打开手机音频
                    setCompoundTopDrawables(mTvAudio, R.mipmap.audio_pressed);
                    isMicroOn = true;
                } else {
                    BaseApplication.showToastShortInCenter(R.string.spk_can_not_close);
                }
                mSpkLastTime = System.currentTimeMillis();
                LogUtils.i("VideoViewActivity.clazz-->>onClick tv_audio isMicroOn:"
                        + isMicroOn);
                break;
            case R.id.tv_video:// 观看视频
                if (Math.abs(System.currentTimeMillis() - mVideoLastTime) <= 1000) {
                    return;
                }
                if (!isVideoOn) {
                    setCompoundTopDrawables(mTvAudio, R.mipmap.audio_pressed);
                    DongSDKProxy.requestRealtimePlay(DongSDKProxy.PLAY_TYPE_VIDEO);// 打开设备摄像头
                    mSurfaceView.setVisibility(View.VISIBLE);
                    mIvDongIcon.setVisibility(View.INVISIBLE);
                } else {
                    setCompoundTopDrawables(mTvAudio, R.mipmap.video_normal);
                    DongSDKProxy.requestStop(DongSDKProxy.PLAY_TYPE_VIDEO);// 关闭设备摄像头
                    mSurfaceView.setVisibility(View.INVISIBLE);
                    mIvDongIcon.setVisibility(View.VISIBLE);
                }
                mVideoLastTime = System.currentTimeMillis();
                LogUtils.i("VideoViewActivity.clazz-->>onClick tv_video isVideoOn:" + isVideoOn);
                isVideoOn = !isVideoOn;
                break;
            case R.id.tv_hang_up:// 挂断
                // DongSDKProxy.requestTakePicture("Viewer",
                // mDeviceInfo); 拍照截图每调用一次就会生成一张图片，慎用
                DongSDKProxy.requestTakeOnePicture(AppConfig.CACH_IMAGE_PATH, mDeviceInfo);// 截图
                if (isVideoSuccess) {
                    stopVideo();
                }
                mAudioManager.setMode(AudioManager.MODE_NORMAL);// 扬声器
                finish();
                LogUtils.i("VideoViewActivity.clazz-->>onClick tv_hang_up isVideoSuccess:"
                        + isVideoSuccess);
                break;
            case R.id.tv_accept:// 接听
                mTvAccept.setVisibility(View.GONE);
                mTvHangup.setText(getString(R.string.stop));
                mTvAudio.performClick();
                LogUtils.i("VideoViewActivity.clazz-->>onClick tv_accept isVideoSuccess:"
                        + isVideoSuccess);
                break;

            case R.id.tv_video_quality_cotrol:// 视频质量设置
                if (TDevice.devieType(mDeviceInfo, 23)) {
                    TipDialogManager.showTipDialog(this, R.string.warn,
                            R.string.no_permissions);
                } else {
                    View diaView = View.inflate(this, R.layout.set_video_quality_dialog, null);
                    final Dialog dialog = new Dialog(this, R.style.transparent_dialog_theme);
                    Window dialogWindow = dialog.getWindow();
                    if (dialogWindow != null) {
                        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
                        dialogWindow.setGravity(Gravity.TOP);
                        dialog.setContentView(diaView);
                        dialog.setCanceledOnTouchOutside(true);
                        lp.y = 220; // 新位置Y坐标
                        dialogWindow.setAttributes(lp);
                    }
                    dialog.show();
                    TextView hightDefi = (TextView) diaView.findViewById(R.id.tv_hight_definition);
                    TextView standardDefi = (TextView) diaView.findViewById(R.id.tv_standard_definition);
                    TextView fluencyDefi = (TextView) diaView.findViewById(R.id.tv_fluency_definition);

                    hightDefi.setOnClickListener(new OnClickListener() {

                        @Override
                        public void onClick(View arg0) {
                            DongSDKProxy.requestSetQuality(2);
                            DongSDKProxy.requestGetQuality();
                            dialog.dismiss();
                        }
                    });
                    standardDefi.setOnClickListener(new OnClickListener() {

                        @Override
                        public void onClick(View arg0) {
                            DongSDKProxy.requestSetQuality(1);
                            DongSDKProxy.requestGetQuality();
                            dialog.dismiss();
                        }
                    });
                    fluencyDefi.setOnClickListener(new OnClickListener() {

                        @Override
                        public void onClick(View arg0) {
                            DongSDKProxy.requestSetQuality(0);
                            DongSDKProxy.requestGetQuality();
                            dialog.dismiss();
                        }
                    });
                }
                break;
            case R.id.iv_light_control:// 亮度设置
                if (TDevice.devieType(mDeviceInfo, 23)) {
                    TipDialogManager.showTipDialog(this, R.string.warn, R.string.no_permissions);
                } else {
                    DongSDKProxy.requestGetBCHS();// 获取设备的亮度
                    View diaView = View.inflate(this, R.layout.light_dialog, null);
                    final Dialog dialog = new Dialog(this, R.style.transparent_dialog_theme);
                    Window dialogWindow = dialog.getWindow();
                    if (dialogWindow != null) {
                        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
                        dialogWindow.setGravity(Gravity.TOP);
                        dialog.setContentView(diaView);
                        dialog.setCanceledOnTouchOutside(true);
                        lp.y = 220; // 新位置Y坐标
                        dialogWindow.setAttributes(lp);
                    }
                    dialog.show();
                    final SeekBar seekBarLight = (SeekBar) diaView.findViewById(R.id.seek_lightbar);
                    seekBarLight.setProgress(mLight);
                    seekBarLight
                            .setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

                                @Override
                                public void onStopTrackingTouch(SeekBar arg0) {
                                    DongSDKProxy.requestSetBCHS(seekBarLight.getProgress() + 1);
                                    DongSDKProxy.requestGetBCHS();
                                }

                                @Override
                                public void onProgressChanged(SeekBar seekBar,
                                                              int progress, boolean fromUser) {
                                }

                                @Override
                                public void onStartTrackingTouch(SeekBar seekBar) {

                                }
                            });
                }
                break;
            case R.id.iv_video_audio_control:// 音量设置
                if (TDevice.devieType(mDeviceInfo, 23)) {
                    TipDialogManager.showTipDialog(this, R.string.warn, R.string.no_permissions);
                } else {
                    DongSDKProxy.requestGetAudioQuality();// 获取声音的大小
                    View diaView = View.inflate(this, R.layout.device_audio_dialog, null);
                    final Dialog dialog = new Dialog(this, R.style.transparent_dialog_theme);
                    Window dialogWindow = dialog.getWindow();
                    if (dialogWindow != null) {
                        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
                        dialogWindow.setGravity(Gravity.TOP);
                        dialog.setContentView(diaView);
                        dialog.setCanceledOnTouchOutside(true);
                        lp.y = 220; // 新位置Y坐标
                        dialogWindow.setAttributes(lp);
                    }
                    dialog.show();
                    final SeekBar seekBar = (SeekBar) diaView.findViewById(R.id.device_audio_bar);
                    seekBar.setProgress(mAudio);
                    LogUtils.i("AudioButton.setOnClickListener-->>maudio:" + mAudio);
                    seekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

                        @Override
                        public void onStopTrackingTouch(SeekBar arg0) {
                            DongSDKProxy.requestSetAudioQuality((short) (seekBar
                                    .getProgress() + 1));
                            DongSDKProxy.requestGetAudioQuality();
                        }

                        @Override
                        public void onStartTrackingTouch(SeekBar arg0) {

                        }

                        @Override
                        public void onProgressChanged(SeekBar arg0, int arg1,
                                                      boolean arg2) {
                        }

                    });
                }
                break;
            case R.id.iv_screen_change:
                if (TDevice.isLandscape()) {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                    mIvScreenChange.setImageResource(R.mipmap.screen_1);
                } else {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                    mIvScreenChange.setImageResource(R.mipmap.screen2);
                }
                break;
            default:
                break;
        }
    }

    private void setCompoundTopDrawables(TextView texView, int drawableResId) {
        Drawable topDrawable = getResources().getDrawable(drawableResId);
        topDrawable.setBounds(0, 0, topDrawable.getMinimumWidth(), topDrawable.getMinimumHeight());
        texView.setCompoundDrawables(null, topDrawable, null, null);
    }

    private class PhoneReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Intent.ACTION_NEW_OUTGOING_CALL)) {
                String phoneNumber = intent
                        .getStringExtra(Intent.EXTRA_PHONE_NUMBER);
                LogUtils.i("PhoneReceiver.clazz--->>>ACTION_NEW_OUTGOING_CALL phoneNumber:"
                        + phoneNumber);
            } else {
                // 查了下android文档，貌似没有专门用于接收来电的action,所以，非去电即来电.
                // 如果我们想要监听电话的拨打状况，需要这么几步 :
                LogUtils.i("PhoneReceiver.clazz--->>>coming!!!!!!!!!!!!!!!!!!!!");
                mTvHangup.performClick();
                BaseApplication
                        .showToastShortInBottom(R.string.video_stop_phone_comming);
            }
        }
    }

    private class NetBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (ConnectivityManager.CONNECTIVITY_ACTION.equals(action)) {
                ConnectivityManager connectivity = (ConnectivityManager) context
                        .getSystemService(Context.CONNECTIVITY_SERVICE);
                if (null != connectivity) {
                    final NetworkInfo info = connectivity.getActiveNetworkInfo();
                    LogUtils.i("NetBroadcastReceiver.clazz--->>>%%%%%%%%%%%%%%%%%%........NetworkInfo:" + info);
                    if (null == info || !info.isConnected()) {
                        VideoViewActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                // DongSDKProxy.requestStopDeice();
                                VideoViewActivity.this.finish();
                                //   mTvHangup.performClick();
                                LogUtils.i("NetBroadcastReceiver.clazz--->>>%%%%%%%%%%%%%%%%%%........is null:" + (info == null));
                            }
                        });
                    }
                }
            }
        }
    }


    private class VideoViewActivityDongAccountCallbackImp extends
            DongAccountCallbackImp {

        @Override
        public int OnCall(ArrayList<DeviceInfo> list) {
            LogUtils.i("VideoViewAvtivityDongAccountCallbackImp.clazz--->>>OnCall........list):" + list);
            if (mDeviceInfo != null && list != null && list.size() > 0) {
                final DeviceInfo deviceInfo = list.get(0);
                if (mDeviceInfo.dwDeviceID == deviceInfo.dwDeviceID) {
                    return 0;// 如果报警了正在当前播放的设备，忽略此次报警
                }
                //监视中收到另一台设备的呼叫
                if (mTipDialog.isShowing())
                    mTipDialog.dismiss();
                mTipDialog.setMessage("(" + deviceInfo.deviceName + ")" + getString(R.string.vistor));
                mTipDialog.setNegativeButton(getString(R.string.Ignore), null);
                mTipDialog.setPositiveButton(getString(R.string.accept), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        stopMusic(mMediaPlayer);
                        mShouldPlayNextDeviceCount = 0;
                        setCompoundTopDrawables(mTvAudio, R.mipmap.audio_normal);
                        mSurfaceView.setVisibility(View.INVISIBLE);
                        mIvDongIcon.setVisibility(View.VISIBLE);
                        mDeviceInfo = deviceInfo;
                        if (isMicroOn) {
                            isMicroOn = false;
                            DongSDKProxy.requestClosePhoneMic();// 关闭音频，防止不断发送音频数据
                        }
                        shouldPlayNextDevice = false;
                        isActive = false;
                        DongSDKProxy.requestStopDeice();

                        startVideoPlay();
                        mTipDialog.dismiss();
                        mTvAccept.performClick();
                    }
                });
                mTipDialog.show();
            }
            return 0;
        }

        @Override
        public int OnAuthenticate(InfoUser tInfo) {
            LogUtils.i("VideoViewAvtivityDongAccountCallbackImp.clazz--->>>OnAuthenticate........tInfo:"
                    + tInfo);
            return 0;
        }

        @Override
        public int OnUserError(int nErrNo) {
            LogUtils.i("VideoViewAvtivityDongAccountCallbackImp.clazz--->>>OnUserError........nErrNo:"
                    + nErrNo);
            return 0;
        }
    }

    private class VideoViewActivityDongDeviceSettingImpl extends
            DongDeviceSettingCallbackImp {
        @Override
        public int OnGetQuality(int result) {
            if (result == 0) {
                mTvVideoQuality.setText(VideoViewActivity.this.getString(R.string.video1));
            } else if (result == 1) {
                mTvVideoQuality.setText(VideoViewActivity.this.getString(R.string.video2));
            } else {
                mTvVideoQuality.setText(VideoViewActivity.this.getString(R.string.video3));
            }
            return 0;
        }

        @Override
        public int OnGetBCHS(int nBrightness) {
            mLight = nBrightness;
            return 0;
        }

        @Override
        public int OnGetAudioQuality(short wSpkVolume) {
            mAudio = wSpkVolume;
            return 0;
        }

    }

    private class VideoViewActivityDongDeviceCallBackImpl extends
            DongDeviceCallbackImp {
        private TextView mTvConnState;// 视频缓冲文字

        VideoViewActivityDongDeviceCallBackImpl() {
            View view = LayoutInflater.from(VideoViewActivity.this).inflate(
                    R.layout.loading_dialog, null);
            mConnDeviceStateDialog.setContent(view);
            mTvConnState = (TextView) view.findViewById(R.id.tv_tip);
            mTvConnState.setText(getString(R.string.waiting_connection_30));
            mConnDeviceStateDialog.show();
        }

        @Override
        public int OnConnect(int nType) {
            LogUtils.i("VideoViewActivityDongDeviceCallBackImpl.clazz--->>>OnConnect nType:" + nType);
            mTvConnState.setText(getString(R.string.waiting_certification_60));
            return 0;
        }

        @Override
        public int OnAuthenticate(int nType) {// 认证成功会回调两次-----音频认证成功，视频认证成功
            mTvConnState.setText(getString(R.string.waiting_media_90));
            // 获取音频大小
            int audioSize = DongSDKProxy.requestGetAudioQuality();
            // 获取设备亮度
            int bCHS = DongSDKProxy.requestGetBCHS();
            // 获取视频品质
            int quality = DongSDKProxy.requestGetQuality();
            mFlVideo.setBackgroundResource(0);
            // timer();
            LogUtils.i("log5", "VideoViewAvtivityDongDeviceCallBackImpl.clazz--->>>OnAuthenticate nType:"
                    + nType + ";audioSize:" + audioSize + ";bCHS:"
                    + bCHS + ";quality:" + quality);
            return 0;
        }

        @Override
        public int OnVideoSucc() {
            LogUtils.i("log5", "VideoViewAvtivityDongDeviceCallBackImpl.clazz--->>>OnVideoSucc");
            isVideoSuccess = true;
            shouldPlayNextDevice = true;
            mSurfaceView.setVisibility(View.VISIBLE);
            mIvDongIcon.setVisibility(View.INVISIBLE);
            mConnDeviceStateDialog.dismiss();
            return 0;
        }

        @Override
        public int OnViewError(int nErrNo) {
            LogUtils.i("VideoViewAvtivityDongDeviceCallBackImpl.clazz--->>>OnViewError...nErrNo:"
                    + nErrNo + ",isVideoSuccess:" + isVideoSuccess);
            if (isVideoSuccess) {
                stopVideo();
            }
            if (mConnDeviceStateDialog.isShowing()) {
                mConnDeviceStateDialog.dismiss();
            }
            TipDialogManager.showTipDialog(VideoViewActivity.this,
                    BaseApplication.context().getString(R.string.tip), BaseApplication.context().getString(
                            R.string.video_play_error) + nErrNo);
            return 0;
        }

        @Override
        public int OnTrafficStatistics(float upload, float download) {
            LogUtils.i("VideoViewAvtivityDongDeviceCallBackImpl.clazz--->>>OnTrafficStatistics upload:"
                    + upload + ";download:" + download);
            if (download < 1) {// 如果下载数据小于1，持续时间为10s,那么提示用户网络差
                mDownloadDataZeroCount++;
            } else {
                mDownloadDataZeroCount = 0;
            }
            mTvUploadDataTip.setText(String.format("%s", Math.round(upload * 100) / 100 + "K/S"));
            mTvDownloadDataTip.setText(String.format("%s", Math.round(download * 100) / 100 + "K/S"));
            return 0;
        }

        @Override
        public int OnPlayError(int nReason, String username) {
            LogUtils.i("VideoViewAvtivityDongDeviceCallBackImpl.clazz--->>>OnPlayError nReason:"
                    + nReason + ";username:" + username);
            // reason 1:没有权限 ;2:用户主动挂断;3:设备占线;4:音频已经被其他用户占用;5:请通话
            if (mConnDeviceStateDialog.isShowing())
                mConnDeviceStateDialog.dismiss();
            if (mTipDialog.isShowing())
                mTipDialog.dismiss();
            switch (nReason) {
                case 1:
                    BaseApplication.showToastShortInCenter(R.string.no_permissions);
                    break;
                case 2:// 用户主动挂断
                    mTvHangup.performClick();// 挂断音视频
                    BaseApplication.showToastShortInCenter(R.string.handup);
                    break;
                case 3:// 设备占线
                    isDeviceBusy = true;
                    break;
                case 4:// 音频已经被其他用户占用
                    TipDialogManager.showTipDialog(VideoViewActivity.this, R.string.tip, R.string.audio_busy);
                    break;
                case 5:
                    BaseApplication.showToastShortInCenter(R.string.please_talking);
                    break;
            }
            return 0;
        }
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {
    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        int state = mControlParent.getVisibility();
        mControlParent.setVisibility(state == View.VISIBLE ? View.INVISIBLE : View.VISIBLE);
        if (TDevice.isLandscape()) {
            state = mLlControl.getVisibility();
            mLlControl.setVisibility(state == View.VISIBLE ? View.GONE : View.VISIBLE);
        }
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        boolean isLeft = e1.getX() - e2.getX() > FLIP_DISTANCE;
        boolean isRight = e2.getX() - e1.getX() > FLIP_DISTANCE;

        LogUtils.i("log5", "onFling-------->>>>>>has inline device isActive:" + isActive + ",shouldPlayNextDevice:" + shouldPlayNextDevice);
        if (isActive && shouldPlayNextDevice && (isLeft || isRight)) {// 主动监视、下一台设备正常播放
            ArrayList<DeviceInfo> dataList = DongConfiguration.mDeviceInfoList;
            if (dataList == null || mDeviceInfo == null) {
                return false;
            }
            ArrayList<DeviceInfo> onlineTemp = new ArrayList<>();
            for (DeviceInfo deviceInfo : dataList) {
                if (deviceInfo.isOnline) {
                    onlineTemp.add(deviceInfo);// 1.先将在线设备放在缓存集合中
                }
            }
            int dataSize = onlineTemp.size();
            LogUtils.i("log5", "onFling-------->>>>>>has online device size:" + dataSize + ",isLeft:" + isLeft + ",isRight:" + isRight + ",data:" + onlineTemp);
            for (int i = 0; i < dataSize; i++) {
                DeviceInfo deviceInfo = onlineTemp.get(i);
                if (mDeviceInfo.dwDeviceID == deviceInfo.dwDeviceID) {// 2.找到当前正在播放的设备位置
                    LogUtils.i("log5", "..............i:" + i);
                    if (i == 0 && isRight) {// 3.向右拉，此时设备是第一台
                        BaseApplication.showToastShortInBottom(R.string.first_camera);
                        break;
                    } else if (i == dataSize - 1 && isLeft) {// 4.向左拉，此时设备是最后一台
                        BaseApplication.showToastShortInBottom(R.string.no_camera);
                        break;
                    } else {
                        mShouldPlayNextDeviceCount = 0;
                        setCompoundTopDrawables(mTvAudio, R.mipmap.audio_normal);
                        mSurfaceView.setVisibility(View.INVISIBLE);
                        mIvDongIcon.setVisibility(View.VISIBLE);
                        int index = isLeft ? i + 1 : i - 1;
                        mDeviceInfo = onlineTemp.get(index);
                        if (isMicroOn) {
                            isMicroOn = false;
                            DongSDKProxy.requestClosePhoneMic();// 关闭音频，防止不断发送音频数据
                        }
                        shouldPlayNextDevice = false;
                        DongSDKProxy.requestStopDeice();
                        startVideoPlay();
                        // mIvStart.performClick();
                        LogUtils.i("log5", "index..............go next device:"
                                + mDeviceInfo + ",index:" + index);
                        break;
                    }
                }
            }
        }
        return false;
    }

    private class MyTimerTask extends TimerTask {

        @Override
        public void run() {
            mShouldPlayNextDeviceCount++;

            if (isActive && mShouldPlayNextDeviceCount > 10
                    && !shouldPlayNextDevice) {// 1. 重新打开滑动下一台设备检查
                shouldPlayNextDevice = true;
                mShouldPlayNextDeviceCount = 0;
                VideoViewActivity.this.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        if (mTipDialog != null && mTipDialog.isShowing()) {
                            mTipDialog.dismiss();
                        }
                        if (mConnDeviceStateDialog.isShowing()) {
                            mConnDeviceStateDialog.dismiss();
                        }
                        int networkType = TDevice.getNetworkType();
                        if (networkType == 0) {// 10s后发现无网
                            TipDialogManager.showWithoutNetworDialog(
                                    VideoViewActivity.this, null);
                        } else {// 10s后还没有视频数据
                            mTipDialog.setTitle(R.string.tip);
                            mTipDialog
                                    .setMessage(R.string.video_get_data_error);
                            mTipDialog.setPositiveButton(R.string.i_know, null);
                            mTipDialog.show();
                        }
                        LogUtils.i("VideoViewActivity.clazz--->>>MyTimerTask coming!!!!!!! mShouldPlayNextDeviceCount ..."
                                + mShouldPlayNextDeviceCount);
                    }
                });
            }

            if (mDownloadDataZeroCount > 10) {// 2.下载视频数据10s后连续为0，那么提示用户联接质量
                mCloseVideoActivityCount++;
                VideoViewActivity.this.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        final String time = BaseApplication.context()
                                .getString(
                                        R.string.video_connect_quality_error,
                                        7 - mCloseVideoActivityCount);
                        if (mCloseVideoActivityCount == 1) {// 2.1只允许对话框弹一次
                            if (mTipDialog != null && mTipDialog.isShowing()) {
                                mTipDialog.dismiss();
                            }
                            if (mConnDeviceStateDialog.isShowing()) {
                                mConnDeviceStateDialog.dismiss();
                            }
                            mTipDialog.setTitle(R.string.tip);
                            mTipDialog.setMessage(time);
                            mTipDialog.setPositiveButton(R.string.i_know, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    mTipDialog.dismiss();
                                    mTvHangup.performClick();
                                }
                            });
                            mTipDialog.show();
                        } else if (mCloseVideoActivityCount < 7
                                && mCloseVideoActivityCount > 1) {// 2.2.更改对话框提示字符
                            mTipDialog.setMessage(time);
                        } else {// 2.3.下载视频数据10s后连续为0，5s后关闭界面
                            mTipDialog.dismiss();
                            mTvHangup.performClick();
                        }
                    }
                });
            }

            if (isDeviceBusy) {// 3.设备占线提醒，5s手动关闭界面
                mDeviceBusyCount++;
                VideoViewActivity.this.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        final String time = BaseApplication.context()
                                .getString(R.string.device_busy, 7 - mDeviceBusyCount);
                        if (mDeviceBusyCount == 1) {
                            mTipDialog.setTitle(R.string.tip);
                            mTipDialog.setMessage(time);
                            mTipDialog.setPositiveButton(R.string.i_know,
                                    new DialogInterface.OnClickListener() {

                                        @Override
                                        public void onClick(
                                                DialogInterface dialog,
                                                int which) {
                                            mTvHangup.performClick();
                                            mTipDialog.dismiss();
                                        }
                                    });
                            mTipDialog.show();
                        } else if (mDeviceBusyCount < 7 && mDeviceBusyCount > 1) {
                            mTipDialog.setMessage(time);
                        } else {
                            mTvHangup.performClick();
                            mTipDialog.dismiss();
                        }
                    }
                });

            }
        }
    }
}
