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

import com.dd121.louyu.R;
import com.dongdong.app.util.LogUtils;
import com.dongdong.app.util.TDevice;

public class TitleBar extends LinearLayout implements OnClickListener {

    private LinearLayout mLlBack;
    private TextView mTvTitleInfo, mTvAdd;

    private OnTitleBarClickListener mListener;

    public interface OnTitleBarClickListener {
        void onBackClick();

        void onTitleClick();

        void onAddClick();
    }

    public TitleBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        int barHeight = TDevice.getStatuBarHeight();
        View view = LayoutInflater.from(context).inflate(R.layout.titlebar_common, null);
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            params.setMargins(0, barHeight, 0, 0);
        }
        addView(view, params);
        mLlBack = (LinearLayout) findViewById(R.id.ll_back);
        mTvTitleInfo = (TextView) findViewById(R.id.tv_title_info);
        mTvAdd = (TextView) findViewById(R.id.tv_add);
        mLlBack.setOnClickListener(this);
        mTvTitleInfo.setOnClickListener(this);
        mTvAdd.setOnClickListener(this);
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
            case R.id.ll_back:
                mListener.onBackClick();
                break;
            case R.id.tv_title_info:
                mListener.onTitleClick();
                break;
            case R.id.tv_add:
                mListener.onAddClick();
                break;
        }

    }

    public void setBackArrowShowing(boolean show) {
        mLlBack.setVisibility(show ? View.VISIBLE : View.GONE);

    }

    public void setAddArrowShowing(boolean show) {
        mTvAdd.setVisibility(show ? View.VISIBLE : View.GONE);

    }

    public void setTitleBarContent(String titleInfo) {
        mTvTitleInfo.setText(titleInfo);
    }

    public void setAddContent(String addInfo) {
        mTvAdd.setText(addInfo);
    }
}
