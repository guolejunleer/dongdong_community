package com.dongdong.app.adapter;


import java.io.File;
import java.util.List;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.dd121.louyu.R;

public class OpenFileAdapter extends BaseAdapter{
	private LayoutInflater mInflater = null;
	private List<String> mItems = null;
	private List<String> mPaths = null;
	private Bitmap mRootIcon = null;
	private Bitmap mUpperIcon = null;
	private Bitmap mVideoFileIcon = null;
	private Bitmap mImageFileIcon = null;
	private Bitmap mVideoIcon = null;
	private Bitmap mImageIcon = null;

	public OpenFileAdapter(Context context, List<String> items, List<String> paths) {
		mInflater = LayoutInflater.from(context);
		mItems = items;
		mPaths = paths;
		Resources res = context.getResources();
		mRootIcon = BitmapFactory.decodeResource(res, R.mipmap.root_icon);
		mUpperIcon = BitmapFactory.decodeResource(res, R.mipmap.upper_icon);
		mVideoFileIcon = BitmapFactory.decodeResource(res, R.mipmap.video_file_icon);
		mImageFileIcon = BitmapFactory.decodeResource(res, R.mipmap.image_file_icon);
		mVideoIcon = BitmapFactory.decodeResource(res, R.mipmap.video_icon);
		mImageIcon = BitmapFactory.decodeResource(res, R.mipmap.image_icon);
	}

	@Override
	public int getCount() {
		return mItems.size();
	}

	@Override
	public Object getItem(int position) {
		return mItems.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position,View convertView,ViewGroup parent) {
		ViewHolder holder;

		if(convertView == null) {
			convertView = mInflater.inflate(R.layout.openfile_item, null);
			holder = new ViewHolder();
			holder.text = (TextView) convertView.findViewById(R.id.text);
			holder.icon = (ImageView) convertView.findViewById(R.id.icon);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		File file = new File(mPaths.get(position));
		if (mItems.get(position).equals("rootPath")) {
			holder.text.setText(R.string.rootDir);
			holder.icon.setImageBitmap(mRootIcon);
		} else if (mItems.get(position).equals("upperPath")) {
			holder.text.setText(R.string.upperDir);
			holder.icon.setImageBitmap(mUpperIcon);
		} else {
			holder.text.setText(file.getName());
			if (getMIMEType(file).equals("video/*")) {
				holder.icon.setImageBitmap(mVideoIcon);
			} else if (getMIMEType(file).equals("image/*")) {
				holder.icon.setImageBitmap(mImageIcon);
			} else if (file.getName().endsWith("video")) {
				holder.icon.setImageBitmap(mVideoFileIcon);
			} else if (file.getName().endsWith("image")){
				holder.icon.setImageBitmap(mImageFileIcon);
			}
		}
		return convertView;
	}

	public String getFilePath(int position) {
		return mPaths.get(position);
	}

	public String getMIMEType(File file) {
		String type = "";
		String fileName = file.getName();
		String end = fileName
				.substring(fileName.lastIndexOf(".") + 1, fileName.length())
				.toLowerCase();
		if (end.equals("m4a") || end.equals("mp3") || end.equals("mid")
				|| end.equals("xmf") || end.equals("ogg") || end.equals("wav")) {
			type = "audio";
		} else if (end.equals("3gp") || end.equals("mp4") || end.equals("avi")) {
			type = "video";
		} else if (end.equals("jpg") || end.equals("gif") || end.equals("png")
				|| end.equals("jpeg") || end.equals("bmp")) {
			type = "image";
		} else {
			type = "*";
		}
		type += "/*";
		return type;
	}

	private class ViewHolder {
		TextView text;
		ImageView icon;
	}
}
