package com.dongdong.app.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v4.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.dd121.community.R;
import com.dongdong.app.util.BitmapUtil;
import com.dongdong.app.util.LogUtils;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MyAlbumAdapter extends ArrayAdapter<String> implements AbsListView.OnScrollListener {
    /**
     * 记录所有正在执行或等待执行的任务。
     */
    private Set<BitmapWorkerTask> taskCollection;

    /**
     * 图片缓存技术的核心类，用于缓存所有下载好的图片，在程序内存达到设定值时会将最少最近使用的图片移除掉。
     */
    private LruCache<String, Bitmap> mMemoryCache;

    /**
     * GridView的实例
     */
    private GridView mGridView;

    /**
     * GridView中可见的第一张图片的下标
     */
    private int mFirstVisibleItem;

    /**
     * GridView中可见的图片的数量
     */
    private int mVisibleItemCount;
    /**
     * 记录是否是第一次进入该界面
     */
    private boolean isFirstEnterThisActivity = true;

    private List<String> mPaths;
    private Bitmap mDefaultBitmap;

    public MyAlbumAdapter(Context context, int textViewResourceId, List<String> paths, GridView gridView) {
        super(context, textViewResourceId, paths);
        mPaths = paths;
        mGridView = gridView;
        taskCollection = new HashSet<>();
        // 获取应用程序最大可用内存
        int maxMemory = (int) Runtime.getRuntime().maxMemory();
        int cacheSize = maxMemory / 8;
        // 设置图片缓存大小为程序最大可用内存的1/8
        mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                return bitmap.getByteCount()/1024;
            }
        };
        mGridView.setOnScrollListener(this);

        mDefaultBitmap = BitmapFactory.decodeResource(context.getResources(), R.mipmap.image_icon);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        if (convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.my_album_item, null);
        } else {
            view = convertView;
        }
        ImageView imageView = (ImageView) view.findViewById(R.id.iv_icon);
        //给ImageView设置一个Tag，保证异步加载图片时不会乱序
        imageView.setTag(getFilePath(position));
        //为该ImageView设置显示的图片
        setImageView(getFilePath(position), imageView);
        return view;
    }

    /**
     * 给ImageView设置图片。首先从LruCache中取出图片的缓存，设置到ImageView上。如果LruCache中没有该图片的缓存，
     * 就给ImageView设置一张默认图片。
     *
     * @param imageUrl  图片的URL地址，用于作为LruCache的键。
     * @param imageView 用于显示图片的控件。
     */
    private void setImageView(String imageUrl, ImageView imageView) {
        Bitmap bitmap = getBitmapFromMemoryCache(imageUrl);
        if (bitmap != null) {
            imageView.setImageBitmap(bitmap);
        } else {
            imageView.setImageBitmap(mDefaultBitmap);
        }
    }

    /**
     * 将一张图片存储到LruCache中。
     *
     * @param key    LruCache的键，这里传入图片的URL地址。
     * @param bitmap LruCache的键，这里传入从网络上下载的Bitmap对象。
     */
    private void addBitmapToMemoryCache(String key, Bitmap bitmap) {
        if (getBitmapFromMemoryCache(key) == null) {
            mMemoryCache.put(key, bitmap);
        }
    }

    /**
     * 从LruCache中获取一张图片，如果不存在就返回null。
     *
     * @param key LruCache的键，这里传入图片的URL地址。
     * @return 对应传入键的Bitmap对象，或者null。
     */
    private Bitmap getBitmapFromMemoryCache(String key) {
        return mMemoryCache.get(key);
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        // 仅当GridView静止时才去加载图片，GridView滑动时取消所有正在进行的任务
        if (scrollState == SCROLL_STATE_IDLE) {
            loadBitmaps(mFirstVisibleItem, mVisibleItemCount);
        } else {
            cancelAllTasks();
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        mFirstVisibleItem = firstVisibleItem;
        mVisibleItemCount = visibleItemCount;
        // 下载的任务应该由onScrollStateChanged里调用，但首次进入程序时onScrollStateChanged并不会调用，
        // 因此在这里为首次进入程序开启下载任务。
        if (isFirstEnterThisActivity && visibleItemCount > 0) {
            loadBitmaps(firstVisibleItem, visibleItemCount);
            isFirstEnterThisActivity = false;
        }
    }

    /**
     * 加载Bitmap对象。此方法会在LruCache中检查所有屏幕中可见的ImageView的Bitmap对象，
     * 如果发现任何一个ImageView的Bitmap对象不在缓存中，就会从本地中图片。
     *
     * @param firstVisibleItem 第一个可见的ImageView的下标
     * @param visibleItemCount 屏幕中总共可见的元素数
     */
    private void loadBitmaps(int firstVisibleItem, int visibleItemCount) {
        try {
            for (int i = firstVisibleItem; i < firstVisibleItem + visibleItemCount; i++) {
                String imagePath = getFilePath(i);
                Bitmap bitmap = getBitmapFromMemoryCache(imagePath);
                ImageView imageView = (ImageView) mGridView.findViewWithTag(imagePath);
                if (bitmap == null) {
                    BitmapWorkerTask task = new BitmapWorkerTask(imageView);
                    taskCollection.add(task);
                    task.execute(imagePath);
                } else {
                    LogUtils.i("The Photo is exist in cache");
                    //依据Tag找到对应的ImageView显示图片
                    if (imageView != null) {
                        imageView.setImageBitmap(bitmap);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
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
     * 异步加载图片的任务。
     */
    private class BitmapWorkerTask extends AsyncTask<String, Void, Bitmap> {
        /**
         * 图片的URL地址
         */
        private String imageUrl;
        private ImageView imageView;

        private BitmapWorkerTask(ImageView imageView) {
            this.imageView = imageView;
        }

        @Override
        protected Bitmap doInBackground(String... params) {
            imageUrl = params[0];
            // 在后台开始加载图片
            Bitmap bitmap = BitmapUtil.decodeSampledBitmapFromSD(imageUrl,100,100);
            if (bitmap != null) {
                // 图片下载完成后缓存到LruCache中
                addBitmapToMemoryCache(imageUrl, bitmap);
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            // 根据Tag找到相应的ImageView控件，将下载好的图片显示出来。
            if (imageView.getTag() != null && imageView.getTag().equals(imageUrl) &&
                    bitmap != null) {
                imageView.setImageBitmap(bitmap);
            }
            taskCollection.remove(this);
        }
    }

    public void recycle() {
        if (mDefaultBitmap != null && !mDefaultBitmap.isRecycled())
            mDefaultBitmap.recycle();
    }

    public String getFilePath(int position) {
        return mPaths.get(position);
    }

    public String getMIMEType(File file) {
        String type;
        String fileName = file.getName();
        String end = fileName.substring(fileName.lastIndexOf(".") + 1,
                fileName.length()).toLowerCase();

        switch (end) {
            case "m4a":
            case "mp3":
            case "mid":
            case "xmf":
            case "ogg":
            case "wav":
                type = "audio";
                break;
            case "3gp":
            case "mp4":
            case "avi":
                type = "video";
                break;
            case "jpg":
            case "gif":
            case "png":
            case "jpeg":
            case "bmp":
                type = "image";
                break;
            default:
                type = "*";
                break;
        }
        type += "/*";
        return type;
    }
}
