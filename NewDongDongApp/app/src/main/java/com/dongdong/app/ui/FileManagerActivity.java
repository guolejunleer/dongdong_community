package com.dongdong.app.ui;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.app.ListActivity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ListView;

import com.dd121.louyu.R;
import com.dongdong.app.adapter.OpenFileAdapter;
import com.dongdong.app.ui.dialog.TipDialogManager;
import com.dongdong.app.util.StatusBarCompatUtils;
import com.dongdong.app.widget.TitleBar;
import com.dongdong.app.widget.TitleBar.OnTitleBarClickListener;

public class FileManagerActivity extends ListActivity {

    private TitleBar mTitleBar;
    private String mRootPath;
    private OpenFileAdapter openfileAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.openfile);
        StatusBarCompatUtils.compat(this);
        initView();
        initData();
    }

    public void initView() {
        mTitleBar = (TitleBar) this.findViewById(R.id.tb_title);
        mTitleBar.setTitleBarContent(getString(R.string.mypicture));
        mTitleBar.setAddArrowShowing(false);

        mRootPath = Environment.getExternalStorageDirectory().getPath()
                + "/Takepicture";
    }

    public void initData() {
        mTitleBar.setOnTitleBarClickListener(new OnTitleBarClickListener() {

            @Override
            public void onTitleClick() {
            }

            @Override
            public void onBackClick() {
                FileManagerActivity.this.finish();
            }

            @Override
            public void onAddClick() {

            }
        });

        getFileCatalog(mRootPath);

    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        String filePath = openfileAdapter.getFilePath(position);
        File file = new File(filePath);
        if (file.isDirectory()) {
            getFileCatalog(filePath);
        } else {
            openFile(file);
        }
    }

    private void getFileCatalog(String filePath) {
        File rootfile = new File(filePath);
        if (!rootfile.exists()) {
            if (!rootfile.mkdirs()) {
                TipDialogManager.showTipDialog(this, R.string.warn,
                        R.string.OPENFILE_ERROR);
                return;
            }
        }
        List<String> items = new ArrayList<String>();
        List<String> paths = new ArrayList<String>();
        if (!filePath.equals(mRootPath)) {
            items.add("rootPath");
            paths.add(mRootPath);
            items.add("upperPath");
            paths.add(rootfile.getParent());
        }

        File[] files = rootfile.listFiles();
        for (int i = 0; i < files.length; i++) {
            File subfile = files[i];
            items.add(subfile.getName());
            paths.add(subfile.getPath());
        }
        openfileAdapter = new OpenFileAdapter(this, items, paths);
        setListAdapter(openfileAdapter);
    }

    private void openFile(File file) {
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(Intent.ACTION_VIEW);
        String type = openfileAdapter.getMIMEType(file);
        intent.setDataAndType(Uri.fromFile(file), type);
        startActivity(intent);
    }

}