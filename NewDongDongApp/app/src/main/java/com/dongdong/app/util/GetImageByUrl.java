package com.dongdong.app.util;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.widget.ImageView;

/**
 * 根据图片url路径获取图片
 * 
 * @author LeoLeoHan
 * 
 */
public class GetImageByUrl {

	private PicHandler mPicHandler;
	private ImageView mImage;
	private String url;

	/**
	 * 通过图片url路径获取图片并显示到对应控件上
	 * 
	 * @param mImage
	 * @param url
	 */
	public void setImage(ImageView mImage, String url) {
		this.url = url;
		this.mImage = mImage;
		mPicHandler = new PicHandler();
		Thread t = new LoadPicThread();
		t.start();
	}

	private class LoadPicThread extends Thread {
		@Override
		public void run() {
			Bitmap img = getUrlImage(url);
			System.out.println(img + "---");
			Message msg = mPicHandler.obtainMessage();
			msg.what = 0;
			msg.obj = img;
			mPicHandler.sendMessage(msg);
		}
	}

	private class PicHandler extends Handler {

		@Override
		public void handleMessage(Message msg) {
			Bitmap myimg = (Bitmap) msg.obj;
			mImage.setImageBitmap(myimg);
		}

	}

	private Bitmap getUrlImage(String url) {
		Bitmap img = null;
		try {
			URL picurl = new URL(url);
			HttpURLConnection conn = (HttpURLConnection) picurl.openConnection();
			conn.setConnectTimeout(6000);
			conn.setDoInput(true);
			conn.setUseCaches(false);
			conn.connect();
			InputStream is = conn.getInputStream();
			img = BitmapFactory.decodeStream(is);
			is.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return img;
	}
}
