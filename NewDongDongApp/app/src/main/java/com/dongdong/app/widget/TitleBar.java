package com.dongdong.app.widget;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Button;

import com.dd121.community.R;
import com.dongdong.app.util.LogUtils;
import com.dongdong.app.util.TDevice;

public class TitleBar extends LinearLayout implements OnClickListener {

    private TextView mTvTitleInfo;
    private Button mBtnMyAdd, mBtnMyBack, mBtnMyFinish;

    private OnTitleBarClickListener mListener;

    public interface OnTitleBarClickListener {
        void onBackClick();

        void onTitleClick();

        void onAddClick();

        void onFinishClick();
    }

    public TitleBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        int barHeight = TDevice.getStatuBarHeight();
        View view = LayoutInflater.from(context).inflate(R.layout.titlebar_common, null);
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            params.setMargins(0, barHeight, 0, 0);
        }
        addView(view, params);
        mBtnMyBack = (Button) findViewById(R.id.btn_back);
        mTvTitleInfo = (TextView) findViewById(R.id.tv_title_info);
        mBtnMyAdd = (Button) findViewById(R.id.btn_add);
        mBtnMyFinish = (Button) findViewById(R.id.btn_finish);

        mBtnMyBack.setOnClickListener(this);
        mTvTitleInfo.setOnClickListener(this);
        mBtnMyAdd.setOnClickListener(this);
        mBtnMyFinish.setOnClickListener(this);
        LogUtils.i("TitleBar.clazz--->>>construct barHeight:" + barHeight);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

    }

    public void setOnTitleBarClickListener(OnTitleBarClickListener listener) {
        mListener = listener;
    }

    @Override
    public void onClick(View view) {
        if (mListener == null) {
            return;
        }
        int id = view.getId();
        switch (id) {
            case R.id.btn_back:
                mListener.onBackClick();
                break;
            case R.id.tv_title_info:
                mListener.onTitleClick();
                break;
            case R.id.btn_add:
                mListener.onAddClick();
                break;
            case R.id.btn_finish:
                mListener.onFinishClick();
                break;
        }

    }

    public void setBackArrowShowing(boolean show) {
        mBtnMyBack.setVisibility(show ? View.VISIBLE : View.GONE);

    }

    public void setAddArrowShowing(boolean show) {
        mBtnMyAdd.setVisibility(show ? View.VISIBLE : View.GONE);
        mBtnMyAdd.setCompoundDrawables(null, null, null, null);
    }

    public void setTitleBarContent(String titleInfo) {
        mTvTitleInfo.setText(titleInfo);
    }

    public void setFinishShowing(boolean show) {
        mBtnMyAdd.setVisibility(show ? View.GONE : View.VISIBLE);
        mBtnMyFinish.setVisibility(show ? View.VISIBLE : View.GONE);
    }
}
