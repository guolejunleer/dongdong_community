package com.dongdong.app.adapter;

import java.util.List;

import com.dd121.community.R;
import com.dongdong.app.bean.VisitorPhotoBean;
import com.dongdong.app.util.GetImageByUrl;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class VisitorPhotoAdapter extends BaseAdapter {
	private List<VisitorPhotoBean> mList;
	public Context mContext;
	private LayoutInflater mLayoutInflater;

	public VisitorPhotoAdapter(Context context, List<VisitorPhotoBean> mlist) {
		this.mContext = context;
		this.mList = mlist;
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
			convertView =mLayoutInflater.inflate(R.layout.visitor_photo_item,null);
			convertView.setTag(holder);

			holder.mIvPhoto = (ImageView) convertView
					.findViewById(R.id.iv_visitor_photo);
			holder.mTvDeviceName = (TextView) convertView
					.findViewById(R.id.tv_photo_device_name);
			holder.mTvPhotoTimeStamp = (TextView) convertView
					.findViewById(R.id.tv_photo_timestamp);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		new GetImageByUrl().setImage(holder.mIvPhoto, mList.get(position).getPhotoUrl());
		holder.mTvDeviceName.setText(mList.get(position).getDeviceName());
		holder.mTvPhotoTimeStamp.setText(mList.get(position).getPhotoTimestamp());
		return convertView;
	}

	private static class ViewHolder {
		private ImageView mIvPhoto;
		private TextView mTvDeviceName;
		private TextView mTvPhotoTimeStamp;
	}

}
