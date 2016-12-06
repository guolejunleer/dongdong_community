package com.dongdong.app.adapter;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.dd121.community.R;
import com.dongdong.app.bean.PhoneBean;

public class CommonPhoneGridViewAdapter extends BaseAdapter {
	private Context context;
	private List<PhoneBean> mDatas;

	public CommonPhoneGridViewAdapter(Context context, List<PhoneBean> datas) {
		this.context = context;
		mDatas = datas;
	}

	@Override
	public int getCount() {
		return mDatas.size();
	}

	@Override
	public PhoneBean getItem(int position) {
		return mDatas.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder;
		if (convertView == null) {
			convertView = View.inflate(context, R.layout.gradview_item, null);
			viewHolder = new ViewHolder();
			viewHolder.iv_cmImg = (ImageView) convertView
					.findViewById(R.id.iv_icon);
			viewHolder.tv_cmName = (TextView) convertView
					.findViewById(R.id.tv_des);
			viewHolder.tv_cmPhone = (TextView) convertView
					.findViewById(R.id.phone);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		PhoneBean commonPhone = mDatas.get(position);
		viewHolder.iv_cmImg.setBackgroundResource(R.mipmap.phone);
		viewHolder.tv_cmName.setText(commonPhone.getCommonname());
		viewHolder.tv_cmPhone.setText(commonPhone.getCommonphone());
		return convertView;
	}

	private class ViewHolder {
		private ImageView iv_cmImg;
		private TextView tv_cmName;
		private TextView tv_cmPhone;
	}
}
