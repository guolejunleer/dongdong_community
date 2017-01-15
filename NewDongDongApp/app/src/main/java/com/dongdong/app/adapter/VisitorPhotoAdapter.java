package com.dongdong.app.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.dd121.community.R;
import com.dongdong.app.base.BaseApplication;
import com.dongdong.app.bean.VisitorPhotoBean;
import com.dongdong.app.cache.CacheHelper;
import com.dongdong.app.cache.DiskLruCache;
import com.dongdong.app.util.LogUtils;

import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class VisitorPhotoAdapter extends Adapter<ViewHolder> {
    //加载状态
    public static final int LOAD_NO_DATA = 1;
    public static final int DO_NOT_LOAD = 2;
    public static final int LOADING = 3;
    public static final int LOAD_DATA_FAILED = 4;
    private int mLoadStatus = LOAD_NO_DATA;

    //返回View的类型
    private static final int TYPE_EMPTY = 0;
    private static final int TYPE_ITEM = 1;
    private static final int TYPE_FOOTER = 2;

    private List<VisitorPhotoBean> mData;
    private LayoutInflater mLayoutInflater;

    /**
     * RecycleView的实例
     */
    private RecyclerView mRecyclerView;

    /**
     * 缓存帮助类
     */
    private CacheHelper mCacheHelper;
    /**
     * 记录所有正在下载或等待下载的任务。
     */
    private Set<BitmapWorkerTask> taskCollection;

    /**
     * 图片缓存技术的核心类，用于缓存所有下载好的图片，在程序内存达到设定值时会将最少最近使用的图片移除掉。
     */
    private LruCache<String, Bitmap> mMemoryCache;

    /**
     * 图片硬盘缓存核心类。
     */
    private DiskLruCache mDiskLruCache;

    private OnItemClickListener onItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public VisitorPhotoAdapter(Context context, List<VisitorPhotoBean> data, RecyclerView recyclerView) {
        this.mData = data;
        this.mRecyclerView = recyclerView;
        this.mLayoutInflater = LayoutInflater.from(context);
        mCacheHelper = new CacheHelper();
        taskCollection = new HashSet<>();
        mMemoryCache = mCacheHelper.getLruCache();
        mDiskLruCache = mCacheHelper.getDiskLruCache(context);
    }

    //获得适配器中的缓存
    public LruCache<String, Bitmap> getMemoryCache() {
        return mMemoryCache;
    }

    @Override
    public int getItemCount() {
        return mData.size() == 0 ? 1 : mData.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (mData.size() == 0) {
            return TYPE_EMPTY;
        } else if (position + 1 == getItemCount()) {
            return TYPE_FOOTER;
        } else {
            return TYPE_ITEM;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_EMPTY) {
            return new EmptyViewHolder(mLayoutInflater.inflate(R.layout.empty_view, parent, false));
        } else if (viewType == TYPE_FOOTER) {
            return new FooterViewHolder(mLayoutInflater.inflate(R.layout.footer_view, parent, false));
        } else {
            return new NormalItemHolder(mLayoutInflater.inflate(R.layout.visitor_photo_item, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        if (holder instanceof NormalItemHolder) {
            ((NormalItemHolder) holder).mTvRoomNum.setText("房号 " + mData.get(position).getRoomValue());
            ((NormalItemHolder) holder).mTvPhotoTimestamp.setText(mData.get(position).getPhotoTimestamp());
            boolean ignore = mData.get(position).getType().equals(BaseApplication.context().getString(R.string.cloud_call_time_out));
            ((NormalItemHolder) holder).mTvState.setTextColor(ignore ? Color.RED :
                    BaseApplication.context().getResources().getColor(R.color.day_textColor));
            ((NormalItemHolder) holder).mTvState.setText((ignore ? "未接听" : "已接听"))
            ;
            // 给ImageView设置一个Tag，保证异步加载图片时不会乱序
            ((NormalItemHolder) holder).mIvPhoto.setTag(mData.get(position).getPhotoUrl());
            ((NormalItemHolder) holder).mIvPhoto.setImageResource(R.mipmap.default_photo);
            loadBitmaps(((NormalItemHolder) holder).mIvPhoto, mData.get(position).getPhotoUrl());

            if (onItemClickListener != null) {
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int position = holder.getLayoutPosition();
                        onItemClickListener.onItemClick(holder.itemView, position);
                    }
                });
            }

        } else if (holder instanceof EmptyViewHolder) {
            ((EmptyViewHolder) holder).mTvEmpty.setText("暂无访客留影");
        } else if (holder instanceof FooterViewHolder) {
            switch (mLoadStatus) {
                case LOAD_NO_DATA:
                    ((FooterViewHolder) holder).mPbLoad.setVisibility(View.GONE);
                    ((FooterViewHolder) holder).mTvLoad.setVisibility(View.VISIBLE);
                    ((FooterViewHolder) holder).mTvLoad.setText("没有更多数据了");
                    break;
                case LOADING:
                    ((FooterViewHolder) holder).mPbLoad.setVisibility(View.VISIBLE);
                    ((FooterViewHolder) holder).mTvLoad.setVisibility(View.VISIBLE);
                    ((FooterViewHolder) holder).mTvLoad.setText("加载中");
                    break;
                case DO_NOT_LOAD:
                    ((FooterViewHolder) holder).mPbLoad.setVisibility(View.GONE);
                    ((FooterViewHolder) holder).mTvLoad.setVisibility(View.GONE);
                    break;
                case LOAD_DATA_FAILED:
                    ((FooterViewHolder) holder).mPbLoad.setVisibility(View.GONE);
                    ((FooterViewHolder) holder).mTvLoad.setVisibility(View.VISIBLE);
                    ((FooterViewHolder) holder).mTvLoad.setText("加载数据失败了");
                    break;
            }
        }
    }

    //改变加载状态
    public void changeLoadStatus(int status) {
        mLoadStatus = status;
        notifyDataSetChanged();
    }

    //空数据布局
    private class EmptyViewHolder extends RecyclerView.ViewHolder {
        TextView mTvEmpty;

        EmptyViewHolder(View itemView) {
            super(itemView);
            mTvEmpty = (TextView) itemView.findViewById(R.id.tv_empty_text);
        }
    }

    //填充数据布局
    private class NormalItemHolder extends RecyclerView.ViewHolder {
        ImageView mIvPhoto;
        TextView mTvRoomNum;
        TextView mTvPhotoTimestamp;
        TextView mTvState;

        NormalItemHolder(View view) {
            super(view);
            mIvPhoto = (ImageView) view.findViewById(R.id.iv_visitor_photo);
            mTvRoomNum = (TextView) view.findViewById(R.id.tv_photo_room_num);
            mTvPhotoTimestamp = (TextView) view.findViewById(R.id.tv_photo_timestamp);
            mTvState = (TextView) view.findViewById(R.id.tv_state);
        }
    }

    //上拉加载布局
    private class FooterViewHolder extends RecyclerView.ViewHolder {
        ProgressBar mPbLoad;
        TextView mTvLoad;

        FooterViewHolder(View view) {
            super(view);
            mPbLoad = (ProgressBar) view.findViewById(R.id.pb_load);
            mTvLoad = (TextView) view.findViewById(R.id.tv_load);
        }
    }

    /**
     * 加载Bitmap对象。此方法会在LruCache中检查所有屏幕中可见的ImageView的Bitmap对象，
     * 如果发现任何一个ImageView的Bitmap对象不在缓存中，就会开启异步线程去下载图片。
     */
    private void loadBitmaps(ImageView imageView, String imageUrl) {
        try {
            Bitmap bitmap = mCacheHelper.getBitmapFromMemoryCache(imageUrl, mMemoryCache);

            if (bitmap == null) {
                BitmapWorkerTask task = new BitmapWorkerTask();
                taskCollection.add(task);
                task.execute(imageUrl);
            } else {
                if (imageView != null) {
                    Bitmap resizeBitmap = resizeImage(bitmap, 200, 160);
                    imageView.setImageBitmap(resizeBitmap);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static Bitmap resizeImage(Bitmap bitmap, int w, int h) {
        Bitmap BitmapOrg = bitmap;
        int width = BitmapOrg.getWidth();
        int height = BitmapOrg.getHeight();
        int newWidth = w;
        int newHeight = h;

        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;

        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        // if you want to rotate the Bitmap
        // matrix.postRotate(45);
        Bitmap resizedBitmap = Bitmap.createBitmap(BitmapOrg, 0, 0, width,
                height, matrix, true);
        return resizedBitmap;
    }

    /**
     * 取消所有正在下载或等待下载的任务。
     */
    public void cancelAllTasks() {
        if (taskCollection != null) {
            for (BitmapWorkerTask task : taskCollection) {
                task.cancel(false);
            }
        }
    }

    /**
     * 将缓存记录同步到journal文件中。
     */
    public void flushCache() {
        if (mDiskLruCache != null) {
            try {
                mDiskLruCache.flush();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 异步下载图片的任务。
     *
     * @author guolin
     */
    private class BitmapWorkerTask extends AsyncTask<String, Void, Bitmap> {

        /**
         * 图片的URL地址
         */
        private String imageUrl;

        @Override
        protected Bitmap doInBackground(String... params) {
            imageUrl = params[0];
            FileDescriptor fileDescriptor = null;
            FileInputStream fileInputStream = null;
            DiskLruCache.Snapshot snapShot;
            try {
                // 生成图片URL对应的key
                final String key = mCacheHelper.hashKeyForDisk(imageUrl);
                // 查找key对应的缓存
                snapShot = mDiskLruCache.get(key);
                if (snapShot == null) {
                    // 如果没有找到对应的缓存，则准备从网络上请求数据，并写入缓存
                    DiskLruCache.Editor editor = mDiskLruCache.edit(key);
                    if (editor != null) {
                        OutputStream outputStream = editor.newOutputStream(0);
                        if (mCacheHelper.downloadUrlToStream(imageUrl, outputStream)) {
                            editor.commit();
                        } else {
                            editor.abort();
                        }
                    }
                    // 缓存被写入后，再次查找key对应的缓存
                    snapShot = mDiskLruCache.get(key);
                }
                if (snapShot != null) {
                    fileInputStream = (FileInputStream) snapShot.getInputStream(0);
                    fileDescriptor = fileInputStream.getFD();
                }
                // 将缓存数据解析成Bitmap对象
                Bitmap bitmap = null;
                if (fileDescriptor != null) {
                    bitmap = BitmapFactory.decodeFileDescriptor(fileDescriptor);
                }
                if (bitmap != null) {
                    // 将Bitmap对象添加到内存缓存当中
                    mCacheHelper.addBitmapToMemoryCache(params[0], bitmap, mMemoryCache);
                }
                return bitmap;
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (fileDescriptor == null && fileInputStream != null) {
                    try {
                        fileInputStream.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            // 根据Tag找到相应的ImageView控件，将下载好的图片显示出来。
            ImageView imageView = (ImageView) mRecyclerView.findViewWithTag(imageUrl);
            if (imageView != null && bitmap != null) {
                imageView.setImageBitmap(resizeImage(bitmap, 200, 160));//重新改变Bitmap
            }
            taskCollection.remove(this);
        }
    }
}
