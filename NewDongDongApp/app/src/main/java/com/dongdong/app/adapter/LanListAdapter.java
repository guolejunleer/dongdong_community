package com.dongdong.app.adapter;

import android.content.Context;
import android.graphics.Color;
import android.text.AndroidCharacter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.dd121.community.R;
import com.ddclient.dongsdk.DeviceInfo;
import com.ddclient.dongsdk.DongSDKProxy;
import com.dongdong.app.util.LogUtils;

import java.util.ArrayList;

public class LanListAdapter extends BaseAdapter {

    private LayoutInflater mInflater;
    private ArrayList<DeviceInfo> mDeviceInfoList = new ArrayList<>();

    public LanListAdapter(Context context) {
        mInflater = LayoutInflater.from(context);
    }

    public void setData(ArrayList<DeviceInfo> deviceList) {
        mDeviceInfoList.clear();
        for (DeviceInfo deviceInfo : deviceList) {
            if (deviceInfo != null) {
                mDeviceInfoList.add(deviceInfo);
            }
        }
    }

    public ArrayList<DeviceInfo> getData() {
        return mDeviceInfoList;
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
        Holder hold;
        if (convertView == null) {
            hold = new Holder();
            convertView = mInflater.inflate(R.layout.lanitem, null);
            convertView.setTag(hold);
        } else {
            hold = (Holder) convertView.getTag();
        }
        hold.deviceName = (TextView) convertView.findViewById(R.id.tv_deviceName);
        hold.addEarly = (TextView) convertView.findViewById(R.id.tv_add_early);
        hold.deviceName.setText(getData().get(position).deviceName);

        ArrayList<DeviceInfo> myDevice = DongSDKProxy.requestGetDeviceListFromCache();
        for (DeviceInfo deviceInfo : myDevice) {
            if (deviceInfo.deviceSerialNO.equals(mDeviceInfoList.get(position).deviceSerialNO)) {
                hold.addEarly.setVisibility(View.VISIBLE);
                hold.deviceName.setTextColor(Color.parseColor("#a9a9a9"));
            }
        }
        return convertView;
    }

    private class Holder {
        TextView deviceName;
        TextView addEarly;
    }
}
