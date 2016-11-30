package com.dongdong.app.ui;

import java.util.ArrayList;
import java.util.List;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

import com.dd121.community.R;
import com.dongdong.app.adapter.CommonPhoneGridViewAdapter;
import com.dongdong.app.base.BaseActivity;
import com.dongdong.app.bean.PhoneBean;
import com.dongdong.app.db.DatabaseHelper;
import com.dongdong.app.interf.OnTabReselectListener;
import com.dongdong.app.ui.dialog.CommonDialog;
import com.dongdong.app.widget.TitleBar;
import com.dongdong.app.widget.TitleBar.OnTitleBarClickListener;

public class CommonPhoneActivity extends BaseActivity implements
        OnTabReselectListener, OnTitleBarClickListener, OnItemClickListener {

    //	private TitleBar mTitleBar;
    private List<PhoneBean> mStrList = new ArrayList<>();
    private GridView mGridView;
    private String mtel;
//	private PhoneBean mCommonPhone;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_commomphone;
    }

    @Override
    public void initView() {
        TitleBar titleBar = (TitleBar) this.findViewById(R.id.tb_title);

        titleBar.setTitleBarContent(getString(R.string.phone));
        titleBar.setOnTitleBarClickListener(this);
        titleBar.setAddArrowShowing(false);

        mGridView = (GridView) this.findViewById(R.id.gv_grid_view1);
        mGridView.setOnItemClickListener(this);
    }

    @Override
    public void initData() {
        DatabaseHelper dbh = new DatabaseHelper(CommonPhoneActivity.this);
        SQLiteDatabase db = dbh.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "select * from Linkroom_db_commonphone_name", null);
        while (cursor.moveToNext()) {
            PhoneBean common = new PhoneBean();
            common.setCommonname(cursor.getString(1));
            common.setCommonphone(cursor.getString(2));
            mStrList.add(common);
        }
        cursor.close();
        db.close();
        dbh.close();

        CommonPhoneGridViewAdapter adapter = new CommonPhoneGridViewAdapter(
                CommonPhoneActivity.this, mStrList);
        mGridView.setAdapter(adapter);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {
        switch (position) {
            case 0:
                call(position);
                break;
            case 1:
                call(position);
                break;
            case 2:
                call(position);
                break;
            case 3:
                call(position);
                break;
            case 4:
                call(position);
                break;
            case 5:
                call(position);
                break;
            case 6:
                call(position);
                break;
            case 7:
                call(position);
                break;
        }
    }

    private void call(int position) {
        PhoneBean commonPhone = mStrList.get(position);
        mtel = commonPhone.getCommonphone();
        CommonDialog commonDialog = new CommonDialog(CommonPhoneActivity.this);
        commonDialog.setMessage(R.string.callphone);
        commonDialog.setPositiveButton(R.string.yes, new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:"
                        + mtel));
                startActivity(intent);
                dialog.dismiss();
            }
        });
        commonDialog.setNegativeButton(R.string.no, null);
        commonDialog.setCancelable(true);
        commonDialog.show();
    }

    @Override
    public void onBackClick() {
        CommonPhoneActivity.this.finish();
    }

    @Override
    public void onTitleClick() {

    }

    @Override
    public void onTabReselect() {

    }

    @Override
    public void onAddClick() {
    }

    @Override
    public void onFinishClick() {
    }
}
