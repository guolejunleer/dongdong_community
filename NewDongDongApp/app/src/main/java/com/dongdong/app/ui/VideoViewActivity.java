package com.dongdong.app.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.dd121.community.R;
import com.ddclient.configuration.DongConfiguration;
import com.ddclient.dongsdk.AbstractDongCallbackProxy;
import com.ddclient.dongsdk.DeviceInfo;
import com.ddclient.dongsdk.DongSDKProxy;
import com.ddclient.jnisdk.InfoUser;
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
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class VideoViewActivity extends BaseActivity implements OnClickListener,
        OnTouchListener, OnGestureListener, PopupWindow.OnDismissListener {

    // 左右滑动距离标志
    private static final float FLIP_DISTANCE = 80 * TDevice.getDensity();

    // 1.第一组
    private FrameLayout mFlVideo;
    private SurfaceView mSurfaceView;
    private ImageView mIvDongIcon;
    private ImageView mIvLockImage;

    // 2.第二组
    private LinearLayout mLlDataTip;
    private LinearLayout mLlVideoCommonData;
    private LinearLayout mLlVideoPortrait;
    private LinearLayout mLlVideoLandscape;
    private ImageView mIvMore;
    private ImageView mIvScreenAdd;
    private ImageView mIvScreenReduce;
    private TextView mTvDeviceName;
    private TextView mTvUploadDataTip;
    private TextView mTvDownloadDataTip;
    private TextView mTvCurrentTime;

    // 3.第三组
    private TextView mTvUnlock;
    private TextView mTvTakePicture;
    private TextView mTvHandFree;
    private TextView mTvSpeak;
    private TextView mTvVideo;
    private ImageView mIvHangupPortrait;
    private ImageView mIvAcceptPortrait;

    private ImageView mIvUnlock;
    private ImageView mIvTakePicture;
    private ImageView mIvHandFree;
    private ImageView mIvSpeak;
    private ImageView mIvVideo;
    private ImageView mIvHangupLandscape;
    private ImageView mIvAcceptLandscape;

    // 4.第四组
    private PopupWindow mPopupWindow;

    private Button mBtnFluency;
    private Button mBtnStandard;
    private Button mBtnHigh;
    private SeekBar mSoundSeekBar;
    private SeekBar mLightSeekBar;
    private TextView mTvReboot;

    private GestureDetector mGestureDetector;

    // 下面是状态位
    private boolean isMicroOn;// 麦克风是否打开
    private boolean isHandsFree;// true-免提，false--听筒
    private boolean isVideoOn;// 是否观看视频
    private boolean isVideoSuccess;// 视频界面是否成功播放
    private boolean shouldPlayNextDevice;

    private int mVideoQuality;//视频品质
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
    private boolean isDeviceBusy;//设备有没有被占用
    private boolean isAnswered;

    private CommonDialog mConnDeviceStateDialog;
    private CommonDialog mTipDialog;
    private SoundPlay mSoundPlay;
    private long mUnlockLastTime, mPictureLastTime, mHandsFreeLastTime,
            mSpkLastTime, mVideoLastTime;

    private PhoneReceiver mReceiver;
    private NetBroadcastReceiver mNetReceiver;

    private DeviceInfo mDeviceInfo;
    private VideoViewActivityDongAccountCallbackImp mDongAccountCallBackImpl
            = new VideoViewActivityDongAccountCallbackImp();
    private VideoViewActivityDongDeviceCallBackImpl mDongDeviceCallBackImpl
            = new VideoViewActivityDongDeviceCallBackImpl();
    private VideoViewActivityDongDeviceSettingImpl mDongDeviceSettingImpl
            = new VideoViewActivityDongDeviceSettingImpl();
    private int channelId;

    //在主线程里面处理消息并更新UI界面
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    long sysTime = System.currentTimeMillis();
                    CharSequence sysTimeStr = DateFormat.format("hh:mm", sysTime);
                    mTvCurrentTime.setText(sysTimeStr); //更新时间
                    break;
            }
        }
    };

    @Override
    protected int getLayoutId() {
        // 隐藏状态栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        return R.layout.activity_video_view;
    }

    @Override
    public void initView() {
        mFlVideo = (FrameLayout) findViewById(R.id.fl_video);
        mSurfaceView = (SurfaceView) findViewById(R.id.sv_video);
        mIvDongIcon = (ImageView) findViewById(R.id.iv_dong_sign);
        mIvLockImage = (ImageView) findViewById(R.id.iv_openLock);

        mLlDataTip = (LinearLayout) findViewById(R.id.ll_data_tip);
        mLlVideoCommonData = (LinearLayout) findViewById(R.id.ll_video_common_data);
        mLlVideoPortrait = (LinearLayout) findViewById(R.id.ll_video_portrait);
        mLlVideoLandscape = (LinearLayout) findViewById(R.id.ll_video_landscape);

        mIvMore = (ImageView) findViewById(R.id.iv_more);
        mIvScreenAdd = (ImageView) findViewById(R.id.iv_screen_add);
        mIvScreenReduce = (ImageView) findViewById(R.id.iv_screen_reduce);

        mTvDeviceName = (TextView) findViewById(R.id.tv_device_name);
        mTvUploadDataTip = (TextView) findViewById(R.id.tv_upload_data_tip);
        mTvDownloadDataTip = (TextView) findViewById(R.id.tv_download_data_tip);
        mTvCurrentTime = (TextView) findViewById(R.id.tv_current_time);

        mTvUnlock = (TextView) findViewById(R.id.tv_unlock);
        mTvTakePicture = (TextView) findViewById(R.id.tv_take_picture);
        mTvHandFree = (TextView) findViewById(R.id.tv_hand_free);
        mTvSpeak = (TextView) findViewById(R.id.tv_spk);
        mTvVideo = (TextView) findViewById(R.id.tv_video);

        mIvUnlock = (ImageView) findViewById(R.id.iv_unlock);
        mIvTakePicture = (ImageView) findViewById(R.id.iv_take_picture);
        mIvHandFree = (ImageView) findViewById(R.id.iv_hand_free);
        mIvSpeak = (ImageView) findViewById(R.id.iv_spk);
        mIvVideo = (ImageView) findViewById(R.id.iv_video);

        mIvHangupPortrait = (ImageView) findViewById(R.id.iv_hangup_portrait);
        mIvAcceptPortrait = (ImageView) findViewById(R.id.iv_accept_portrait);
        mIvHangupLandscape = (ImageView) findViewById(R.id.iv_hangup_landscape);
        mIvAcceptLandscape = (ImageView) findViewById(R.id.iv_accept_landscape);

        mGestureDetector = new GestureDetector(this, this);

        mFlVideo.setOnTouchListener(this);
        mFlVideo.setLongClickable(true);

        mIvMore.setOnClickListener(this);
        mIvScreenAdd.setOnClickListener(this);
        mIvScreenReduce.setOnClickListener(this);

        mTvUnlock.setOnClickListener(this);
        mTvTakePicture.setOnClickListener(this);
        mTvHandFree.setOnClickListener(this);
        mTvSpeak.setOnClickListener(this);
        mTvVideo.setOnClickListener(this);

        mIvUnlock.setOnClickListener(this);
        mIvTakePicture.setOnClickListener(this);
        mIvHandFree.setOnClickListener(this);
        mIvSpeak.setOnClickListener(this);
        mIvVideo.setOnClickListener(this);

        mIvHangupPortrait.setOnClickListener(this);
        mIvAcceptPortrait.setOnClickListener(this);
        mIvHangupLandscape.setOnClickListener(this);
        mIvAcceptLandscape.setOnClickListener(this);

        mConnDeviceStateDialog = new CommonDialog(this);
        mTipDialog = new CommonDialog(this);
        mDongDeviceCallBackImpl.initTipDialog();

        //设置设备名称
        if (DongConfiguration.mDeviceInfo != null) {
            mTvDeviceName.setText(DongConfiguration.mDeviceInfo.deviceName);
        }
        //处理开始时的界面显示
        showInDifferentMode();
    }

    @Override
    public void initData() {
        try {
            mMediaPlayer.setDataSource(this, Uri.parse("android.resource://"
                    + this.getPackageName() + "/" + R.raw.doorbell1));
            mMediaPlayer.setLooping(false);
        } catch (Exception e) {
            e.printStackTrace();
        }
        mTimer = new Timer();
        mTimer.schedule(new MyTimerTask(), new Date(), 1000);

        mSoundPlay = new SoundPlay(VideoViewActivity.this);
        mAudioManager = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);

        // 后台在线推送时，自动点亮屏幕
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        LogUtils.i("log5", "VideoViewActivity.clazz-->>initData...");

        boolean initDongAccountLan = DongSDKProxy.initCompleteDongAccountLan();
        if (initDongAccountLan) {
            DongSDKProxy.registerAccountLanCallback(mDongAccountCallBackImpl);
        } else {
            DongSDKProxy.registerAccountCallback(mDongAccountCallBackImpl);
        }

        playVideo();
    }

    private void playVideo() {
        Bundle bundle = getIntent().getBundleExtra(AppConfig.INTENT_BUNDLE_KEY);
        //得到设备ID
        String mDeviceID = bundle.getString(AppConfig.BUNDLE_KEY_DEVICE_ID, "");
        //是否主动监视
        isActive = bundle.getBoolean(AppConfig.BUNDLE_KEY_INITIATIVE, false);
        //test
        channelId = isActive ? new Random().nextInt(8888) : 0;
        LogUtils.i("VideoViewActivity.clazz--->>>onResume channelId:" + channelId);
        isHandsFree = true;
        if (TextUtils.isEmpty(mDeviceID) && isActive) {// 1.主动监视
            mDeviceInfo = DongConfiguration.mDeviceInfo;
            //主动进来是挂断、对讲按钮
            mTvSpeak.setVisibility(View.VISIBLE);
            mTvVideo.setVisibility(View.GONE);
            mIvAcceptPortrait.setVisibility(View.GONE);

            mIvSpeak.setVisibility(View.VISIBLE);
            mIvVideo.setVisibility(View.GONE);
            mIvAcceptLandscape.setVisibility(View.GONE);
        } else {// 2. 推送进来?????????????????????????
            ArrayList<DeviceInfo> list = DongConfiguration.mDeviceInfoList =
                    DongSDKProxy.requestGetDeviceListFromCache();
            for (DeviceInfo deviceInfo : list) {//通过推送过来的设备ID,获取帐号下对应的设备
                if (mDeviceID.equals(String.valueOf(deviceInfo.dwDeviceID))) {
                    mDeviceInfo = deviceInfo;
                    mTvDeviceName.setText(mDeviceInfo.deviceName);
                }
            }
            playMusic(mMediaPlayer);
            isVideoOn = true;
            setCompoundTopDrawables(mTvVideo, R.mipmap.function_speak_pre);
            setFunctionTextColors(mTvVideo, true);
            //推送进来是接听、挂断、视频(根据网络状态判断是否打开，未实现)按钮
            mTvSpeak.setVisibility(View.GONE);
            mTvVideo.setVisibility(View.VISIBLE);
            mIvAcceptPortrait.setVisibility(View.VISIBLE);

            mIvSpeak.setVisibility(View.GONE);
            mIvVideo.setVisibility(View.VISIBLE);
            mIvVideo.setImageResource(R.mipmap.function_speak_pre);
            mIvAcceptLandscape.setVisibility(View.VISIBLE);
        }
        LogUtils.i("VideoViewActivity.clazz--->>>onResume ... mPlayDevice:"
                + mDeviceInfo + ",mDeviceID:" + mDeviceID + ",isActive:" + isActive);
        startVideoPlay();
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
//        filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
//        filter.addAction("android.net.wifi.WIFI_STATE_CHANGED");
//        filter.addAction("android.net.wifi.STATE_CHANGE");
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        registerReceiver(mNetReceiver, filter);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        unregisterReceiver(mReceiver);
        unregisterReceiver(mNetReceiver);
        if (DongSDKProxy.initCompleteDongAccountLan()) {
            DongSDKProxy.unRegisterAccountLanCallback(mDongAccountCallBackImpl);
        } else {
            DongSDKProxy.unRegisterAccountCallback(mDongAccountCallBackImpl);
        }
        DongSDKProxy.unRegisterDongDeviceCallback(mDongDeviceCallBackImpl);
        DongSDKProxy.unRegisterDongDeviceSettingCallback(mDongDeviceSettingImpl);
        LogUtils.i("log5", "VideoViewActivity.clazz-->>onPause...unregister");
        if (DongSDKProxy.initCompleteDongDeviceSetting()) {
            // 用户没点击挂断后也要停止播放，释放资源等操作
            stopVideo();
        }
        if (mMediaPlayer != null) {
            mMediaPlayer.release();
        }
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
        if (mConnDeviceStateDialog != null) {
            mConnDeviceStateDialog.dismiss();
        }
        LogUtils.i("VideoViewActivity.clazz-->>onDestroy...");
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mSurfaceView.requestLayout();
        if (mPopupWindow != null) {//设置界面消失
            mPopupWindow.dismiss();
        }
        //不同方向上的显示
        showInDifferentMode();
    }

    private void startVideoPlay() {
        if (mDeviceInfo == null) {
            BaseApplication.showToastShortInBottom(R.string.play_init_error);
            return;
        }
        DongSDKProxy.initDongDevice(mDongDeviceCallBackImpl);
        LogUtils.i("VideoViewActivity.clazz--->>>videoPlay ... initDongDevice");

        DongSDKProxy.initDongDeviceSetting(mDongDeviceSettingImpl);
        LogUtils.i("VideoViewActivity.clazz--->>>videoPlay ... initDongDeviceSetting");

        //发送请求播放指令
        DongSDKProxy.requestStartPlayDevice(this, mSurfaceView, mDeviceInfo, false);
        DongSDKProxy.requestRealtimePlay(DongSDKProxy.PLAY_TYPE_VIDEO);
        //DongSDKProxy.requestRealtimePlayWithChannelId(DongSDKProxy.PLAY_TYPE_VIDEO,  channelId);
        LogUtils.i("VideoViewActivity.clazz--->>>videoPlay ......................hahahah requestRealtimePlay");
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
                unlockClick();
                break;
            case R.id.tv_take_picture:// 拍照
                takePhotoClick();
                break;
            case R.id.tv_hand_free:// 免提
                handfreeClick();
                break;
            case R.id.tv_spk:// 对讲
                speakClick();
                break;
            case R.id.tv_video:// 视频
                videoClick();
                break;
            case R.id.iv_unlock:// 开锁
                unlockClick();
                break;
            case R.id.iv_take_picture:// 拍照
                takePhotoClick();
                break;
            case R.id.iv_hand_free:// 免提
                handfreeClick();
                break;
            case R.id.iv_spk:// 对讲
                speakClick();
                break;
            case R.id.iv_video:// 视频
                videoClick();
                break;
            case R.id.iv_hangup_portrait:// 挂断
                hangUpClick();
                break;
            case R.id.iv_accept_portrait:// 接听
                acceptClick();
                break;
            case R.id.iv_hangup_landscape:// 挂断
                hangUpClick();
                break;
            case R.id.iv_accept_landscape:// 接听
                acceptClick();
                break;
            case R.id.iv_more://设置
                settingClick();
                break;
            case R.id.iv_screen_add:
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                break;
            case R.id.iv_screen_reduce:
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                break;
            default:
                break;
        }
    }

    /**
     * 设置点击事件
     */
    private void settingClick() {
        if (TDevice.deviceType(mDeviceInfo, 23)) {
            BaseApplication.showToastShortInCenter(R.string.no_permissions);
        } else {
            mLlVideoCommonData.setVisibility(View.GONE);
            mLlVideoPortrait.setVisibility(View.GONE);
            mLlVideoLandscape.setVisibility(View.GONE);

            View contentView = LayoutInflater.from(VideoViewActivity.this).
                    inflate(R.layout.more_setting, null);
            //操作逻辑处理
            deviceSetting(contentView);
            int width = mFlVideo.getWidth();
            int height = mFlVideo.getHeight();
            int needWidth, needHeight;
            if (TDevice.isLandscape()) {
                needWidth = width * 2 / 3;
                needHeight = height;
            } else {
                needWidth = width;
                needHeight = height / 2;
            }
            mPopupWindow = new PopupWindow(contentView, needWidth, needHeight);
            mPopupWindow.setAnimationStyle(R.style.anim);
            //菜单背景色
            mPopupWindow.setFocusable(true);
            mPopupWindow.setBackgroundDrawable(new BitmapDrawable());
            mPopupWindow.setOutsideTouchable(true);
            mPopupWindow.setOnDismissListener(this);
            mPopupWindow.showAtLocation(mFlVideo, Gravity.CENTER, 0, 0);
        }
    }

    /**
     * 开锁点击事件
     */
    private void unlockClick() {
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
                mIvLockImage.setVisibility(View.VISIBLE);
            }

            /**
             * 重复动画时
             */
            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            /**
             * 动画结束时
             */
            @Override
            public void onAnimationEnd(Animation animation) {
                mIvLockImage.setVisibility(View.GONE);
                mIvLockImage.clearAnimation();
            }
        });
        mUnlockLastTime = System.currentTimeMillis();
        LogUtils.i("VideoViewActivity.clazz-->>onClick tv_unlock");
    }

    /**
     * 拍照点击事件
     */
    private void takePhotoClick() {
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
    }

    /**
     * 免提点击事件
     */
    private void handfreeClick() {
        if (Math.abs(System.currentTimeMillis() - mHandsFreeLastTime) <= 1000) {
            return;
        }
        if (!isHandsFree) {
//            mAudioManager.setMode(AudioManager.MODE_NORMAL);// 扬声器
            openSpeaker();
            setCompoundTopDrawables(mTvHandFree, R.mipmap.function_handfree_pre);
            setFunctionTextColors(mTvHandFree, true);

            mIvHandFree.setImageResource(R.mipmap.function_handfree_pre);
        } else {
//            mAudioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);// 听筒
            closeSpeaker();
            setCompoundTopDrawables(mTvHandFree, R.mipmap.function_handfree_nor);
            setFunctionTextColors(mTvHandFree, false);

            mIvHandFree.setImageResource(R.mipmap.function_handfree_nor);
        }
        mHandsFreeLastTime = System.currentTimeMillis();
        LogUtils.i("VideoViewActivity.clazz-->>onClick tv_hand_free isHandsFree:"
                + isHandsFree);
        isHandsFree = !isHandsFree;
    }

    int currVolume;

    //打开扬声器
    private void openSpeaker() {
        try {
            AudioManager audioManager = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
            audioManager.setMode(AudioManager.ROUTE_SPEAKER);
            currVolume = audioManager.getStreamVolume(AudioManager.STREAM_VOICE_CALL);

            if (!audioManager.isSpeakerphoneOn()) {
                audioManager.setSpeakerphoneOn(true);

                audioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL,
                        audioManager.getStreamMaxVolume(AudioManager.STREAM_VOICE_CALL),
                        AudioManager.STREAM_VOICE_CALL);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //关闭扬声器
    private void closeSpeaker() {
        try {
            AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
            if (audioManager != null) {
                if (audioManager.isSpeakerphoneOn()) {
                    audioManager.setSpeakerphoneOn(false);
                    audioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL, currVolume,
                            AudioManager.STREAM_VOICE_CALL);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        //Toast.makeText(context,"揚聲器已經關閉",Toast.LENGTH_SHORT).show();
    }

    /**
     * 对讲点击事件
     */
    private void speakClick() {
        if (Math.abs(System.currentTimeMillis() - mSpkLastTime) <= 1000) {
            return;
        }
        stopMusic(mMediaPlayer);
        if (!isMicroOn) {
            DongSDKProxy.requestRealtimePlay(DongSDKProxy.PLAY_TYPE_AUDIO + DongSDKProxy.PLAY_TYPE_AUDIO_USER);
//                    DongSDKProxy.requestRealtimePlayWithChannelId(DongSDKProxy.PLAY_TYPE_AUDIO, channelId);// 打开设备音响
            DongSDKProxy.requestOpenPhoneMic();// 打开手机麦克风
            DongSDKProxy.requestOpenPhoneSound();// 打开手机音频
            isMicroOn = true;
            setCompoundTopDrawables(mTvSpeak, R.mipmap.function_speak_pre);
            setFunctionTextColors(mTvSpeak, true);
            mIvSpeak.setImageResource(R.mipmap.function_speak_pre);
            isHandsFree = false;
            mTvHandFree.performClick();
        } else {
            BaseApplication.showToastShortInCenter(R.string.spk_can_not_close);
        }
        mSpkLastTime = System.currentTimeMillis();
        LogUtils.i("VideoViewActivity.clazz-->>onClick tv_audio isMicroOn:"
                + isMicroOn);
    }

    /**
     * 视频点击事件
     */
    private void videoClick() {
        if (Math.abs(System.currentTimeMillis() - mVideoLastTime) <= 1000) {
            return;
        }
        if (!isVideoOn) {
            DongSDKProxy.requestRealtimePlay(DongSDKProxy.PLAY_TYPE_VIDEO);
//                    DongSDKProxy.requestRealtimePlayWithChannelId(DongSDKProxy.PLAY_TYPE_VIDEO, channelId);// 打开设备摄像头
            mSurfaceView.setVisibility(View.VISIBLE);
            mIvDongIcon.setVisibility(View.INVISIBLE);
            setCompoundTopDrawables(mTvVideo, R.mipmap.function_video_pre);
            setFunctionTextColors(mTvVideo, true);

            mIvVideo.setImageResource(R.mipmap.function_video_pre);
        } else {
            DongSDKProxy.requestStop(DongSDKProxy.PLAY_TYPE_VIDEO);// 关闭设备摄像头
            mSurfaceView.setVisibility(View.INVISIBLE);
            mIvDongIcon.setVisibility(View.VISIBLE);
            setCompoundTopDrawables(mTvVideo, R.mipmap.function_video_nor);
            setFunctionTextColors(mTvVideo, false);

            mIvVideo.setImageResource(R.mipmap.function_video_nor);
        }
        mVideoLastTime = System.currentTimeMillis();
        LogUtils.i("VideoViewActivity.clazz-->>onClick tv_video isVideoOn:" + isVideoOn);
        isVideoOn = !isVideoOn;
    }

    /**
     * 接听点击事件
     */
    private void acceptClick() {
        isAnswered = true;
        mIvAcceptPortrait.setVisibility(View.GONE);
        mTvSpeak.performClick();
        mTvHandFree.performClick();

        mIvAcceptLandscape.setVisibility(View.GONE);
        LogUtils.i("VideoViewActivity.clazz-->>onClick tv_accept isVideoSuccess:"
                + isVideoSuccess);
    }

    /**
     * 挂断点击事件
     */
    private void hangUpClick() {
        //拍照截图每调用一次就会生成一张图片，慎用
        DongSDKProxy.requestTakeOnePicture(AppConfig.CACH_IMAGE_PATH, mDeviceInfo);// 截图
        if (isVideoSuccess) {
            stopVideo();
        }
        mAudioManager.setMode(AudioManager.MODE_NORMAL);// 扬声器
        VideoViewActivity.this.finish();
        LogUtils.i("VideoViewActivity.clazz-->>onClick tv_hang_up isVideoSuccess:"
                + isVideoSuccess);
    }

    /**
     * 处理品质按钮文字颜色
     */
    private void setVideoQualityTextColors(Button button, boolean checked) {
        if (checked) {
            button.setTextColor(Color.parseColor("#17abe3"));
        } else {
            button.setTextColor(Color.parseColor("#FFFFFF"));
        }
    }

    /**
     * 处理功能按钮的文字颜色
     */
    private void setFunctionTextColors(TextView textView, boolean checked) {
        if (checked) {
            textView.setTextColor(Color.parseColor("#17abe3"));
        } else {
            textView.setTextColor(Color.parseColor("#FFFFFF"));
        }
    }

    /**
     * 设置功能按钮的图片
     */
    private void setCompoundTopDrawables(TextView texView, int drawableResId) {
        Drawable topDrawable = getResources().getDrawable(drawableResId);
        topDrawable.setBounds(0, 0, topDrawable.getMinimumWidth(), topDrawable.getMinimumHeight());
        texView.setCompoundDrawables(null, topDrawable, null, null);
    }

    /**
     * 处理界面横竖屏的显示
     */
    private void showInDifferentMode() {
        mLlVideoCommonData.setVisibility(View.VISIBLE);
        if (TDevice.isLandscape()) {
            mLlVideoPortrait.setVisibility(View.GONE);
            mLlVideoLandscape.setVisibility(View.VISIBLE);
        } else {
            mLlVideoPortrait.setVisibility(View.VISIBLE);
            mLlVideoLandscape.setVisibility(View.GONE);
        }
    }

    /**
     * 处理设置界面的逻辑
     */
    private void deviceSetting(View contentView) {
        mBtnFluency = (Button) contentView.findViewById(R.id.btn_fluency_definition);
        mBtnStandard = (Button) contentView.findViewById(R.id.btn_standard_definition);
        mBtnHigh = (Button) contentView.findViewById(R.id.btn_high_definition);

        mLightSeekBar = (SeekBar) contentView.findViewById(R.id.sb_light);
        mSoundSeekBar = (SeekBar) contentView.findViewById(R.id.sb_sound);

        mTvReboot = (TextView) contentView.findViewById(R.id.tv_reboot);

        switch (mVideoQuality) {
            case 0:
                setVideoQualityTextColors(mBtnFluency, true);
                setVideoQualityTextColors(mBtnStandard, false);
                setVideoQualityTextColors(mBtnHigh, false);
                break;
            case 1:
                setVideoQualityTextColors(mBtnFluency, false);
                setVideoQualityTextColors(mBtnStandard, true);
                setVideoQualityTextColors(mBtnHigh, false);
                break;
            case 2:
                setVideoQualityTextColors(mBtnFluency, false);
                setVideoQualityTextColors(mBtnStandard, false);
                setVideoQualityTextColors(mBtnHigh, true);
                break;
        }

        DongSDKProxy.requestGetBCHS();// 获取设备的亮度
        if (mLight != 0) {
            mLightSeekBar.setProgress(mLight);
        }

        DongSDKProxy.requestGetAudioQuality();// 获取声音的大小
        if (mAudio != 0) {
            mSoundSeekBar.setProgress(mAudio);
        }

        mTvReboot.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mTipDialog.setTitle(R.string.tip);
                mTipDialog.setMessage(R.string.reboot_device_tip);
                mTipDialog.setNegativeButton(R.string.no, null);
                mTipDialog.setPositiveButton(R.string.yes,
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(
                                    DialogInterface dialog,
                                    int which) {
                                DongSDKProxy.requestSystemCommand((short) 2, 0); //给设备发送重启命令
                                mTipDialog.dismiss();
                                mIvHangupPortrait.performClick();
                            }
                        });
                mTipDialog.show();
            }
        });

        mBtnFluency.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                DongSDKProxy.requestSetQuality(0);
                DongSDKProxy.requestGetQuality();
                mPopupWindow.dismiss();
            }
        });

        mBtnStandard.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                DongSDKProxy.requestSetQuality(1);
                DongSDKProxy.requestGetQuality();
                mPopupWindow.dismiss();
            }
        });

        mBtnHigh.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                DongSDKProxy.requestSetQuality(2);
                DongSDKProxy.requestGetQuality();
                mPopupWindow.dismiss();
            }
        });

        mLightSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                DongSDKProxy.requestSetBCHS(mLightSeekBar.getProgress() + 1);
                DongSDKProxy.requestGetAudioQuality();
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }
        });

        mSoundSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                DongSDKProxy.requestSetAudioQuality((short) (mSoundSeekBar.getProgress() + 1));
                DongSDKProxy.requestGetAudioQuality();
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }
        });
    }

    private class PhoneReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Intent.ACTION_NEW_OUTGOING_CALL)) {
                String phoneNumber = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
                LogUtils.i("PhoneReceiver.clazz--->>>ACTION_NEW_OUTGOING_CALL phoneNumber:"
                        + phoneNumber);
            } else {
                // 查了下android文档，貌似没有专门用于接收来电的action,所以，非去电即来电.
                // 如果我们想要监听电话的拨打状况，需要这么几步 :
                LogUtils.i("PhoneReceiver.clazz--->>>coming!!!!!!!!!!!!!!!!!!!!");
                mIvHangupPortrait.performClick();
                BaseApplication.showToastShortInBottom(R.string.video_stop_phone_comming);
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
                                DongSDKProxy.requestStopDeice();
                                mIvHangupPortrait.performClick();
                                LogUtils.i("NetBroadcastReceiver.clazz--->>>%%%%%%%%%%%%%%%%%%........is null:" + (info == null));
                            }
                        });
                    }
                }
            }
        }
    }

    private class VideoViewActivityDongAccountCallbackImp extends
            AbstractDongCallbackProxy.DongAccountCallbackImp {

        @Override
        public int onCall(ArrayList<DeviceInfo> list) {
            LogUtils.i("VideoViewActivityDongAccountCallbackImp.clazz--->>>OnCall........list):" + list);
            if (mDeviceInfo != null && list != null && list.size() > 0) {
                final DeviceInfo deviceInfo = list.get(0);

                String[] strArray = deviceInfo.msg.split("[|]");
                try {
                    String msgContent = strArray[0];//推送信息:C1<360269ggb> <设备呼叫> <2016-12-29 15:41:11>
                    int pushState = Integer.parseInt(strArray[1]);//呼叫状态：8-设备呼叫，9-呼叫接听，10-呼叫结束
                    String deviceId = strArray[2];//设备Id
                    if (pushState == 8) {
//                      Toast.makeText(VideoViewActivity.this, "设备呼叫", Toast.LENGTH_LONG).show();
                    } else if (pushState == 11) {
                        Toast.makeText(VideoViewActivity.this, getString(R.string.call_answered), Toast.LENGTH_LONG).show();
                        return 0;
                    } else if (pushState == 12) {
                        Toast.makeText(VideoViewActivity.this, getString(R.string.call_over), Toast.LENGTH_LONG).show();
                        return 0;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
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
//                        setButtonStatus(mBtnAudio, false);
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
                        mIvAcceptPortrait.performClick();
                    }
                });
                mTipDialog.show();
            }
            return 0;
        }

        @Override
        public int onAuthenticate(InfoUser tInfo) {
            LogUtils.i("DongAccountCallbackImp.clazz--->>>OnAuthenticate........tInfo:"
                    + tInfo);
            return 0;
        }

        @Override
        public int onUserError(int nErrNo) {
            LogUtils.i("DongAccountCallbackImp.clazz--->>>OnUserError........nErrNo:"
                    + nErrNo);
            return 0;
        }
    }

    private class VideoViewActivityDongDeviceSettingImpl extends
            AbstractDongCallbackProxy.DongDeviceSettingCallbackImp {
        @Override
        public int onGetQuality(int result) {
            mVideoQuality = result;
            return 0;
        }

        @Override
        public int onGetBCHS(int nBrightness) {
            mLight = nBrightness;
            return 0;
        }

        @Override
        public int onGetAudioQuality(short wSpkVolume) {
            mAudio = wSpkVolume;
            return 0;
        }

        @Override
        public int onOpenDoor(int result) {
            LogUtils.i("DongDeviceSettingImpl.clazz--->>>onOpenDoor result:" + result);
            if (result != 0) {
                BaseApplication.showToastShortInCenter(R.string.openLockFail);
            } else {
                BaseApplication.showToastShortInCenter(R.string.openlock);
            }
            return 0;
        }

    }

    private class VideoViewActivityDongDeviceCallBackImpl extends
            AbstractDongCallbackProxy.DongDeviceCallbackImp {
        private TextView mTvConnState;// 视频缓冲文字

        void initTipDialog() {
            View view = LayoutInflater.from(VideoViewActivity.this).inflate(
                    R.layout.loading_dialog, null);
            mConnDeviceStateDialog.setContent(view);
            mTvConnState = (TextView) view.findViewById(R.id.tv_tip);
            mTvConnState.setText(getString(R.string.waiting_connection_30));
            mConnDeviceStateDialog.show();
        }

        @Override
        public int onConnect(int nType) {
            LogUtils.i("DongDeviceCallBackImpl.clazz--->>>OnConnect nType:" + nType);
            mTvConnState.setText(getString(R.string.waiting_certification_60));
            return 0;
        }

        @Override
        public int onAuthenticate(int nType) {// 认证成功会回调两次-----音频认证成功，视频认证成功
            mTvConnState.setText(getString(R.string.waiting_media_90));
            // 获取音频大小
            int audioSize = DongSDKProxy.requestGetAudioQuality();
            // 获取设备亮度
            int bCHS = DongSDKProxy.requestGetBCHS();
            // 获取视频品质
            int quality = DongSDKProxy.requestGetQuality();
            mFlVideo.setBackgroundResource(0);
            LogUtils.i("DongDeviceCallBackImpl.clazz--->>>OnAuthenticate nType:"
                    + nType + ";audioSize:" + audioSize + ";bCHS:"
                    + bCHS + ";quality:" + quality);
            return 0;
        }

        @Override
        public int onVideoSucc() {
            LogUtils.i("DongDeviceCallBackImpl.clazz--->>>OnVideoSucc");
            isVideoSuccess = true;
            shouldPlayNextDevice = true;
            mSurfaceView.setVisibility(View.VISIBLE);
            mIvDongIcon.setVisibility(View.INVISIBLE);
            mConnDeviceStateDialog.dismiss();
            return 0;
        }

        @Override
        public int onViewError(int nErrNo) {
            LogUtils.i("DongDeviceCallBackImpl.clazz--->>>OnViewError...nErrNo:"
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
        public int onTrafficStatistics(float upload, float download) {
            LogUtils.i("DongDeviceCallBackImpl.clazz--->>>OnTrafficStatistics upload:"
                    + upload + ";download:" + download);
//            if (download < 1) {// 如果下载数据小于1，持续时间为10s,那么提示用户网络差
//                mDownloadDataZeroCount++;
//            } else {
//                mDownloadDataZeroCount = 0;
//            }
            mTvUploadDataTip.setText(String.format("%s", Math.round(upload * 100) / 100 + "K/S"));
            mTvDownloadDataTip.setText(String.format("%s", Math.round(download * 100) / 100 + "K/S"));
            return 0;
        }

        @Override
        public int onPlayError(int nReason, String username) {
            LogUtils.i("DongDeviceCallBackImpl.clazz--->>>OnPlayError nReason:"
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
                    mIvHangupPortrait.performClick();// 挂断音视频
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
    public void onDismiss() {
        showInDifferentMode();
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
        if (TDevice.isLandscape()) {
            int state = mLlVideoLandscape.getVisibility();
            LogUtils.i("VideoViewActivity.clazz->onSingleTapUp()->LandscapeState:" + state);
            mLlVideoCommonData.setVisibility(state == View.VISIBLE ? View.GONE : View.VISIBLE);
            mLlVideoLandscape.setVisibility(state == View.VISIBLE ? View.GONE : View.VISIBLE);
        } else {
            int state = mLlDataTip.getVisibility();
            LogUtils.i("VideoViewActivity.clazz->onSingleTapUp()->PortraitState:" + state);
            mLlDataTip.setVisibility(state == View.VISIBLE ? View.GONE : View.VISIBLE);
            mIvScreenAdd.setVisibility(state == View.VISIBLE ? View.GONE : View.VISIBLE);
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

        LogUtils.i("log5", "onFling-------->>>>>>has inline device isActive:"
                + isActive + ",shouldPlayNextDevice:" + shouldPlayNextDevice);
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
            LogUtils.i("log5", "onFling-------->>>>>>has online device size:" + dataSize
                    + ",isLeft:" + isLeft + ",isRight:" + isRight + ",data:" + onlineTemp);
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
//                        setButtonStatus(mBtnAudio, false);
                        mSurfaceView.setVisibility(View.INVISIBLE);
                        mIvDongIcon.setVisibility(View.VISIBLE);
                        int index = isLeft ? i + 1 : i - 1;
                        mDeviceInfo = onlineTemp.get(index);
                        mTvDeviceName.setText(mDeviceInfo.deviceName);
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

//            if (mShouldPlayNextDeviceCount > 20) {//主动进来的
//                VideoViewActivity.this.runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        if (mTipDialog != null && mTipDialog.isShowing()) {
//                            mTipDialog.dismiss();
//                        }
//                        if (mConnDeviceStateDialog.isShowing()) {
//                            mConnDeviceStateDialog.dismiss();
//                        }
//                        if (isActive && !isVideoSuccess) {//主动观看，视频没正常播放
//                            BaseApplication.showToastShortInCenter(R.string.network_error);
//                            mBtnHangup.performClick();
//                        } else if (!isActive && isVideoSuccess && !isAnswered) {//推送进来,视频播放正常，但没接听
//                            BaseApplication.showToastShortInCenter(R.string.unanswered);
//                            mBtnHangup.performClick();
//                        } else if (!isActive && !isVideoSuccess) {//推送进来，视频没正常播放
//                            BaseApplication.showToastShortInCenter(R.string.network_error);
//                            mBtnHangup.performClick();
//                        }
//                    }
//                });
//            }

            try {
                Thread.sleep(1000);
                if (isVideoSuccess) {
                    Message msg = new Message();
                    msg.what = 1;  //消息(一个整型值)
                    mHandler.sendMessage(msg);// 每隔1秒发送一个msg给mHandler
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

//            if (isActive && mShouldPlayNextDeviceCount > 10
//                    && !shouldPlayNextDevice) {// 1. 重新打开滑动下一台设备检查
//                shouldPlayNextDevice = true;
//                mShouldPlayNextDeviceCount = 0;
//                VideoViewActivity.this.runOnUiThread(new Runnable() {
//
//                    @Override
//                    public void run() {
//                        if (mTipDialog != null && mTipDialog.isShowing()) {
//                            mTipDialog.dismiss();
//                        }
//                        if (mConnDeviceStateDialog.isShowing()) {
//                            mConnDeviceStateDialog.dismiss();
//                        }
//                        int networkType = TDevice.getNetworkType();
//                        if (networkType == 0) {// 10s后发现无网
//                            TipDialogManager.showWithoutNetworDialog(
//                                    VideoViewActivity.this, null);
//                        } else {// 10s后还没有视频数据
//                            mTipDialog.setTitle(R.string.tip);
//                            mTipDialog
//                                    .setMessage(R.string.video_get_data_error);
//                            mTipDialog.setPositiveButton(R.string.i_know, null);
//                            if (!VideoViewActivity.this.isFinishing())
//                                mTipDialog.show();
//                        }
//                        LogUtils.i("VideoViewActivity.clazz--->>>MyTimerTask coming!!!!!!! mShouldPlayNextDeviceCount ..."
//                                + mShouldPlayNextDeviceCount);
//                    }
//                });
//            }

            if (mDownloadDataZeroCount > 10) {// 2.下载视频数据10s后连续为0，那么提示用户连接质量差
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
                                    mIvHangupPortrait.performClick();
                                }
                            });
                            mTipDialog.show();
                        } else if (mCloseVideoActivityCount < 7
                                && mCloseVideoActivityCount > 1) {// 2.2.更改对话框提示字符
                            mTipDialog.setMessage(time);
                        } else {// 2.3.下载视频数据10s后连续为0，5s后关闭界面
                            mTipDialog.dismiss();
                            mIvHangupPortrait.performClick();
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
                                            mIvHangupPortrait.performClick();
                                            mTipDialog.dismiss();
                                        }
                                    });
                            mTipDialog.show();
                        } else if (mDeviceBusyCount < 7 && mDeviceBusyCount > 1) {
                            mTipDialog.setMessage(time);
                        } else {
                            mIvHangupPortrait.performClick();
                            mTipDialog.dismiss();
                        }
                    }
                });
            }
        }
    }
}