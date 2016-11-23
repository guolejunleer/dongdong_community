package com.dongdong.app.data;

import java.util.HashMap;

import android.annotation.SuppressLint;
import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;

import com.dd121.louyu.R;

@SuppressLint("UseSparseArrays")
public class SoundPlay {

	// 音效的音量
	int mStreamVolume;

	// 定义SoundPool 对象
	private SoundPool mSoundPool;

	// 定义HASH表
	private HashMap<Integer, Integer> mSoundPoolMap;

	public SoundPlay(Context context) {
		initSounds(context);
		intSound(context);
	}

	public void intSound(Context context) {
		this.loadSfx(context, R.raw.picture_sound, 1);
	}

	/***************************************************************
	 * Function: initSounds(); Parameters: null Returns: None. Description:
	 * 初始化声音系统 Notes: none.
	 ***************************************************************/
	public void initSounds(Context context) {
		// 初始化soundPool 对象,第一个参数是允许有多少个声音流同时播放,第2个参数是声音类型,第三个参数是声音的品质
		mSoundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 100);

		// 初始化HASH表
		mSoundPoolMap = new HashMap<Integer, Integer>();

		// 获得声音设备和设备音量
		AudioManager mgr = (AudioManager) context
				.getSystemService(Context.AUDIO_SERVICE);
		mStreamVolume = mgr.getStreamVolume(AudioManager.STREAM_MUSIC);
	}

	/**
	 * 把资源中的音效加载到指定的ID(播放的时候就对应到这个ID播放就行了) Function: loadSfx(); Parameters: null
	 * Returns: None. Description: 加载音效资源 Notes: none.
	 */
	public void loadSfx(Context context, int raw, int ID) {
		mSoundPoolMap.put(ID, mSoundPool.load(context, raw, 1));
	}

	/***************************************************************
	 * Function: play(); Parameters: sound:要播放的音效的ID, loop:循环次数 Returns: None.
	 * Description: 播放声音 Notes: none.
	 ***************************************************************/
	public void play(int sound, int uLoop) {
		mSoundPool.play(mSoundPoolMap.get(sound), mStreamVolume, mStreamVolume,
				1, uLoop, 1f);
		new Thread(new Runnable() {

			public void run() {
				try {
					Thread.sleep(2000);
					mSoundPool.release();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		});
	}
}
