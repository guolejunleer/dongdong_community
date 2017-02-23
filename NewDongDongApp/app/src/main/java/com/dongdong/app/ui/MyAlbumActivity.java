package com.dongdong.app.ui;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.dd121.community.R;
import com.dongdong.app.AppConfig;
import com.dongdong.app.adapter.MyAlbumAdapter;
import com.dongdong.app.base.BaseActivity;
import com.dongdong.app.ui.dialog.TipDialogManager;
import com.dongdong.app.widget.TitleBar;
import com.dongdong.app.widget.TitleBar.OnTitleBarClickListener;

public class MyAlbumActivity extends BaseActivity implements OnTitleBarClickListener,
        GridView.OnItemClickListener {

    private GridView mGvPhoto;
    private String mRootPath;
    private MyAlbumAdapter myAlbumAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_my_album;
    }

    public void initView() {
        TitleBar titleBar = (TitleBar) this.findViewById(R.id.tb_title);
        titleBar.setTitleBarContent(getString(R.string.mypicture));
        titleBar.setAddArrowShowing(false);
        titleBar.setOnTitleBarClickListener(this);

        mGvPhoto = (GridView) this.findViewById(R.id.gv_photo);
        mGvPhoto.setOnItemClickListener(this);

        mRootPath = Environment.getExternalStorageDirectory().getPath() + "/" +
                AppConfig.SD_TAKE_PICTURE_PATH + "/image/";
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 退出程序时结束所有的下载任务
        myAlbumAdapter.cancelAllTasks();
        myAlbumAdapter.recycle();
    }

    public void initData() {
        getFileCatalog(mRootPath);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String filePath = myAlbumAdapter.getFilePath(position);
        File file = new File(filePath);
        if (file.isDirectory()) {
            getFileCatalog(filePath);
        } else {
            openFile(file);
        }
    }

    private void getFileCatalog(String filePath) {
        File rootFile = new File(filePath);
        if (!rootFile.exists()) {
            if (!rootFile.mkdirs()) {
                TipDialogManager.showTipDialog(this, R.string.warn, R.string.OPENFILE_ERROR);
                return;
            }
        }
        List<String> paths = new ArrayList<>();
        if (!filePath.equals(mRootPath)) {
            paths.add(mRootPath);
            paths.add(rootFile.getParent());
        }

        File[] files = rootFile.listFiles();
        for (File subFile : files) {
            paths.add(subFile.getPath());
        }
        myAlbumAdapter = new MyAlbumAdapter(this, 0, paths, mGvPhoto);
        mGvPhoto.setAdapter(myAlbumAdapter);
    }

    private void openFile(File file) {
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(Intent.ACTION_VIEW);
        String type = myAlbumAdapter.getMIMEType(file);
        intent.setDataAndType(Uri.fromFile(file), type);
        startActivity(intent);
    }

    @Override
    public void onTitleClick() {
    }

    @Override
    public void onBackClick() {
        MyAlbumActivity.this.finish();
    }

    @Override
    public void onAddClick() {

    }

    @Override
    public void onFinishClick() {
    }
}