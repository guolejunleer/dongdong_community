package com.dongdong.app.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.dd121.louyu.R;
import com.ddclient.MobileClientLib.InfoWifi;

public class WifiListAdapter extends BaseAdapter {

	private LayoutInflater inflater;
	private ArrayList<InfoWifi> list = new ArrayList<InfoWifi>();

	public WifiListAdapter(Context context) {
		inflater = LayoutInflater.from(context);

	}

	public void setData(ArrayList<InfoWifi> wifilist) {
		this.list = wifilist;

	};

	public ArrayList<InfoWifi> getData() {
		return list;
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public InfoWifi getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int arg0) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Holder hold = null;
		if (convertView == null) {
			hold = new Holder();
			convertView = inflater.inflate(R.layout.wifi_list_item, null);
			convertView.setTag(hold);
		} else {
			hold = (Holder) convertView.getTag();
		}
		hold.wifi_name = (TextView) convertView.findViewById(R.id.wifi_name);
		hold.wifi_static = (TextView) convertView
				.findViewById(R.id.wifi_static);
		hold.wifi_level = (ImageView) convertView.findViewById(R.id.wifi_level);
		hold.wifipwd = (ImageView) convertView.findViewById(R.id.wifiPwd);

		InfoWifi wifiObject = getData().get(position);

		hold.wifi_name.setText(wifiObject.SSID);
		if (wifiObject.bCur) {
			hold.wifi_static.setText(R.string.alreadyconn);
		} else {
			hold.wifi_static.setText(wifiObject.Flags);
		}
		setWifiIcon(hold.wifi_level, hold.wifipwd, wifiObject);

		return convertView;
	}

	public void setWifiIcon(ImageView imageView1, ImageView imageView2,
			InfoWifi wifiObject) {
		if (wifiObject.nSignalLevel == 0) {
			imageView1.setImageResource(R.mipmap.wifi05);
		} else if (wifiObject.nSignalLevel == 1) {
			imageView1.setImageResource(R.mipmap.wifi04);
		} else if (wifiObject.nSignalLevel == 2) {
			imageView1.setImageResource(R.mipmap.wifi03);
		} else if (wifiObject.nSignalLevel == 3) {
			imageView1.setImageResource(R.mipmap.wifi02);
		} else {
			imageView1.setImageResource(R.mipmap.wifi01);
		}

		if (wifiObject.Flags.equals("")) {
			imageView2.setImageResource(R.mipmap.lock);
		} else {
			imageView2.setImageResource(R.mipmap.locked);
		}
	}

	class Holder {
		TextView wifi_name;
		TextView wifi_static;
		ImageView wifipwd;
		ImageView wifi_level;
	}
}
