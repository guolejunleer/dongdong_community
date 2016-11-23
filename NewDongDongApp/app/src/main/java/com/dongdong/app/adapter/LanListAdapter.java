package com.dongdong.app.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.dd121.louyu.R;
import com.ddclient.dongsdk.DeviceInfo;

public class LanListAdapter extends BaseAdapter {

	private LayoutInflater inflater;
	private ArrayList<DeviceInfo> list = new ArrayList<DeviceInfo>();

	public LanListAdapter(Context context) {
		inflater = LayoutInflater.from(context);

	}

	public void setData(ArrayList<DeviceInfo> deviceList) {
		this.list = deviceList;

	};

	public ArrayList<DeviceInfo> getData() {
		return list;
	}

	@Override
	public int getCount() {
		return getData().size();
	}

	@Override
	public DeviceInfo getItem(int position) {
		return getData().get(position);
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
			convertView = inflater.inflate(R.layout.lanitem, null);

			convertView.setTag(hold);
		} else {
			hold = (Holder) convertView.getTag();

		}
		hold.deviceName = (TextView) convertView.findViewById(R.id.deviceName);
		hold.deviceName.setText(getData().get(position).deviceName.toString());
		return convertView;
	}

	class Holder {
		TextView deviceName;
	}
}
