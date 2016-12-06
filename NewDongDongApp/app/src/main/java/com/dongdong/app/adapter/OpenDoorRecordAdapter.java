package com.dongdong.app.adapter;

import java.util.List;

import com.dd121.community.R;
import com.dongdong.app.bean.OpenDoorRecordBean;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class OpenDoorRecordAdapter extends BaseAdapter {
	private Context mContext;
	private List<OpenDoorRecordBean> mList;
	private LayoutInflater mLayoutInflater;

	public OpenDoorRecordAdapter(Context context, List<OpenDoorRecordBean> list) {
		this.mContext=context;
		this.mList = list;
		mLayoutInflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		return mList.size();
	}

	@Override
	public Object getItem(int position) {
		return mList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView =mLayoutInflater.inflate(R.layout.open_door_record_item, null);
			convertView.setTag(holder);

			holder.mTvRoomNumber = (TextView) convertView
					.findViewById(R.id.tv_room_number);
			holder.mTvType = (TextView) convertView.findViewById(R.id.tv_type);
			holder.mTvTimesStamp = (TextView) convertView.findViewById(R.id.tv_timestamp);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		holder.mTvRoomNumber.setText(mList.get(position).getRoomNumber());
		holder.mTvType.setText(mList.get(position).getType());
		holder.mTvTimesStamp.setText(mList.get(position).getTimestamp());
		return convertView;
	}

	private static class ViewHolder {
		private TextView mTvRoomNumber;
		private TextView mTvType;
		private TextView mTvTimesStamp;
	}

}
