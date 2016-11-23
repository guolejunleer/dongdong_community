package com.dongdong.app.util;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class ReadThread extends Thread {

	Handler handler = null;

	public ReadThread(Handler handler) {
		this.handler = handler;
	}

	@Override
	public void run() {
		super.run();
		int i = 1;
		try {
			while (i < 201) {
				if (!handler.hasMessages(0)) {
					Message m;
					m = handler.obtainMessage();
					m.what = 0;
					m.sendToTarget();

					Log.i("-----thrad", "i = " + i);
					i++;
				}
				Thread.sleep(1000);
			}

		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}
