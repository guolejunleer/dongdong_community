package com.dongdong.app.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v4.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.dd121.community.R;
import com.ddclient.configuration.DongConfiguration;
import com.ddclient.dongsdk.DeviceInfo;
import com.dongdong.app.AppConfig;
import com.dongdong.app.AppContext;
import com.dongdong.app.ui.DeviceSettingsActivity;
import com.dongdong.app.util.BitmapUtil;
import com.dongdong.app.util.TDevice;

import java.util.ArrayList;

import static com.dongdong.app.util.BitmapUtil.zoom;

public class DeviceInfoAdapter extends BaseAdapter {

    private static final int ROUND_VALUE_PX = (int) (5 * TDevice.getDensity());

    private Context mContext;
    private LayoutInflater mInflate;
    private ArrayList<DeviceInfo> mDataList = new ArrayList<>();

    private String mImagePath;

    /**
     * 图片缓存技术的核心类，用于缓存所有下载好的图片，在程序内存达到设定值时会将最少最近使用的图片移除掉。
     */
    private LruCache<String, Bitmap> mMemoryCache;
    private Bitmap mDefaultBitmap;

    public DeviceInfoAdapter(Context context) {
        this.mContext = context;
        mInflate = LayoutInflater.from(context);
        if (Environment.isExternalStorageEmulated()) {
            mImagePath = Environment.getExternalStorageDirectory().getPath();
        }
        // 获取应用程序最大可用内存
        int maxMemory = (int) Runtime.getRuntime().maxMemory();
        int cacheSize = maxMemory / 8;
        // 设置图片缓存大小为程序最大可用内存的1/8
        mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
            @SuppressLint("NewApi")
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                return bitmap.getByteCount();
            }
        };
        mDefaultBitmap = BitmapUtil.getRoundedCornerBitmap(BitmapFactory.
                decodeResource(mContext.getResources(), R.mipmap.dongdongicon), ROUND_VALUE_PX);
    }

    public void setData(ArrayList<DeviceInfo> deviceList) {
        mDataList.clear();
        for (DeviceInfo deviceInfo : deviceList) {
            if (deviceInfo != null) {
                mDataList.add(deviceInfo);
            }
        }
    }

    public ArrayList<DeviceInfo> getData() {
        return mDataList;
    }

    @Override
    public int getCount() {
        return mDataList.size();
    }

    @Override
    public Object getItem(int position) {
        return mDataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Holder mHold;
        if (convertView == null) {
            mHold = new Holder();
            convertView = mInflate.inflate(R.layout.info_item, null);
            convertView.setTag(mHold);
            mHold.deviceState = (ImageView) convertView.findViewById(R.id.iv_device_state);
            mHold.deviceName = (TextView) convertView.findViewById(R.id.tv_device_name);
            mHold.deviceType = (TextView) convertView.findViewById(R.id.tv_device_type);
            mHold.defaultDevice = (TextView) convertView.findViewById(R.id.tv_default_device);
            mHold.deviceSettings = (TextView) convertView.findViewById(R.id.tv_device_setting);
            mHold.backGround = (ImageView) convertView.findViewById(R.id.iv_background);
        } else {
            mHold = (Holder) convertView.getTag();
        }

        final DeviceInfo deviceInfo = getData().get(position);
        mHold.backGround.setTag(deviceInfo.deviceSerialNO);
        setImageView(deviceInfo.deviceSerialNO, mHold.backGround);
        mHold.deviceName.setText(deviceInfo.deviceName);
        //设备状态
        if (deviceInfo.isOnline) {
            mHold.deviceState.setImageResource(R.drawable.online);
        } else {
            mHold.deviceState.setImageResource(R.drawable.offline);
        }
        //设备类型
        if (TDevice.deviceType(deviceInfo, 23)) {
            mHold.deviceType.setText(mContext.getString(R.string.auth_device));
        } else {
            mHold.deviceType.setText(mContext.getString(R.string.my_device));
        }

        int defaultDeviceId = (int) AppContext.mAppConfig.getConfigValue(
                AppConfig.DONG_CONFIG_SHARE_PREF_NAME,
                DongConfiguration.mUserInfo.userID + "", 0);
        if (defaultDeviceId == deviceInfo.dwDeviceID) {
            mHold.defaultDevice.setTextColor(Color.parseColor("#00A2E9"));
        } else {
            mHold.defaultDevice.setTextColor(Color.parseColor("#a9a9a9"));
        }
        // 设置点击事件
        mHold.deviceSettings.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent(mContext, DeviceSettingsActivity.class);
                intent.putExtra(AppConfig.BUNDLE_KEY_DEVICE_INFO, deviceInfo);
                mContext.startActivity(intent);
            }
        });

        mHold.defaultDevice.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                int deviceId = deviceInfo.dwDeviceID;
                int localDeviceId = (int) AppContext.mAppConfig
                        .getConfigValue(AppConfig.DONG_CONFIG_SHARE_PREF_NAME,
                                DongConfiguration.mUserInfo.userID + "", 0);
                if (localDeviceId == deviceId) {
                    mHold.defaultDevice.setTextColor(Color.parseColor("#a9a9a9"));
                    deviceId = 0;
                } else {
                    mHold.defaultDevice.setTextColor(Color.parseColor("#00A2E9"));
                    DongConfiguration.mDeviceInfo = deviceInfo;
                }
                AppContext.mAppConfig.setConfigValue(
                        AppConfig.DONG_CONFIG_SHARE_PREF_NAME,
                        DongConfiguration.mUserInfo.userID + "", deviceId);
            }
        });
        return convertView;
    }

    private void setImageView(String imageUrl, ImageView imageView) {
        Bitmap bitmap = getBitmapFromMemoryCache(imageUrl);
        if (bitmap != null) {
            Bitmap roundBitmap = BitmapUtil.getRoundedCornerBitmap(bitmap, ROUND_VALUE_PX);
            if (roundBitmap != null)
                imageView.setImageBitmap(roundBitmap);
        } else {
            imageView.setImageBitmap(BitmapUtil.getRoundedCornerBitmap(
                    zoom(mDefaultBitmap, 400, 300), ROUND_VALUE_PX));
            BitmapWorkerTask task = new BitmapWorkerTask(imageView);
            task.execute(imageUrl);
        }
    }

    private void addBitmapToMemoryCache(String key, Bitmap bitmap) {
        if (getBitmapFromMemoryCache(key) == null) {
            mMemoryCache.put(key, bitmap);
        }
    }

    private Bitmap getBitmapFromMemoryCache(String key) {
        return mMemoryCache.get(key);
    }

    private class BitmapWorkerTask extends AsyncTask<String, Void, Bitmap> {

        private String imageUrl;
        private ImageView imageView;

        private BitmapWorkerTask(ImageView imageView) {
            this.imageView = imageView;
        }

        @Override
        protected Bitmap doInBackground(String... arg0) {
            imageUrl = arg0[0];
            // LogUtils.i("DeviceInfoAdapter.clazz--->>imageUrl:" + imageUrl);
            String filePath = mImagePath + "/" + AppConfig.CACH_IMAGE_PATH + "/image/" + imageUrl + ".jpg";
            Bitmap bitmap = BitmapUtil.decodeSampledBitmapFromSD(filePath, 400, 300);
            if (bitmap != null) {
                addBitmapToMemoryCache(imageUrl, bitmap);
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            if (imageView.getTag() != null && imageView.getTag().equals(imageUrl) &&
                    result != null) {
                Bitmap roundBitmap = BitmapUtil.getRoundedCornerBitmap(result, ROUND_VALUE_PX);
                if (roundBitmap != null) imageView.setImageBitmap(roundBitmap);
            }
        }
    }

    public void recycle() {
        if (mDefaultBitmap != null && !mDefaultBitmap.isRecycled()) {
            mDefaultBitmap.recycle();
            mDefaultBitmap = null;
        }
    }

    private static class Holder {
        ImageView deviceState;
        TextView deviceName;
        TextView deviceType;
        TextView defaultDevice;
        TextView deviceSettings;
        ImageView backGround;
    }

}
