package com.dongdong.app.ui.dialog;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dd121.community.R;

public class DialogTitleView extends FrameLayout {

	public static final int MODE_REGULAR = 0;
	public static final int MODE_SMALL = 1;
	public LinearLayout buttonWell;
	public TextView subTitleTv;
	public View titleDivider;
	public TextView titleTv;

	public DialogTitleView(Context context) {
		super(context);
		init();
	}

	public DialogTitleView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public DialogTitleView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	private void init() {
		inflate(getContext(), R.layout.view_dialog_header, this);
		titleTv = (TextView) findViewById(R.id.tv_title);
		subTitleTv = (TextView) findViewById(R.id.tv_subtitle);
		buttonWell = (LinearLayout) findViewById(R.id.ll_button_well);
		titleDivider = findViewById(R.id.view_title_divder);
	}

	public void addAction(View view, OnClickListener listener) {
		view.setOnClickListener(listener);
		buttonWell.addView(view);
	}

	public void setMode(int mode) {
		int padding = (int) getContext().getResources().getDimension(
				R.dimen.global_dialog_padding);
		if (mode == MODE_SMALL) {
			buttonWell.removeAllViews();
			buttonWell.setVisibility(View.VISIBLE);
			titleTv.setTextSize(1, 16F);
			padding /= 2;
		} else {
			titleTv.setTextSize(1, 22F);
		}
		titleTv.setPadding(padding, padding, padding, padding);
	}
}
