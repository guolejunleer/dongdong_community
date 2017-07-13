package com.dongdong.app.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.dd121.community.R;
import com.dongdong.app.bean.CommonPhoneBean;

import java.util.List;

import static android.provider.Settings.Global.getString;

public class CommonPhoneAdapter extends Adapter<ViewHolder> {
    //加载状态
    public static final int LOAD_NO_DATA = 1;
    public static final int DO_NOT_LOAD = 2;
    public static final int LOADING = 3;
    public static final int LOAD_DATA_FAILED = 4;
    private int mLoadStatus = DO_NOT_LOAD;

    //返回View的类型
    private static final int TYPE_EMPTY = 0;
    private static final int TYPE_NORMAL_ITEM = 1;
    private static final int TYPE_FOOTER = 2;

    private Context mContext;
    private List<CommonPhoneBean> mData;
    private final LayoutInflater mLayoutInflater;

    private OnItemClickListener onItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public CommonPhoneAdapter(Context context, List<CommonPhoneBean> data) {
        this.mData = data;
        this.mContext = context;
        this.mLayoutInflater = LayoutInflater.from(context);
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
            return TYPE_NORMAL_ITEM;
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_EMPTY) {
            return new EmptyViewHolder(mLayoutInflater.inflate(
                    R.layout.empty_view, parent, false));
        } else if (viewType == TYPE_FOOTER) {
            return new FooterViewHolder(mLayoutInflater.inflate(
                    R.layout.footer_view, parent, false));
        } else {
            return new NormalItemHolder(mLayoutInflater.inflate(
                    R.layout.common_phone_item, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        if (holder instanceof NormalItemHolder) {
            NormalItemHolder normalItemHolder = (NormalItemHolder) holder;
            normalItemHolder.mTvCommonPhoneName.setText(mData.get(position).getDepartment());
            normalItemHolder.mTvCommonPhoneAddress.setText(String.format("%s", mContext.getString(
                    R.string.address) + mData.get(position).getAddress())
            );
            normalItemHolder.mTvCommonPhoneNumber.setText(String.format("%s", mContext.getString(
                    R.string.phone_number) + mData.get(position).getPhoneNumber()));

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
            ((EmptyViewHolder) holder).mTvEmpty.setText(mContext.getString(R.string.no_common_phone));
        } else if (holder instanceof FooterViewHolder) {
            switch (mLoadStatus) {
                case LOAD_NO_DATA:
                    ((FooterViewHolder) holder).mPbLoad.setVisibility(View.GONE);
                    ((FooterViewHolder) holder).mTvLoad.setVisibility(View.VISIBLE);
                    ((FooterViewHolder) holder).mTvLoad.setText(mContext.getString(R.string.no_more_data));
                    break;
                case LOADING:
                    ((FooterViewHolder) holder).mPbLoad.setVisibility(View.VISIBLE);
                    ((FooterViewHolder) holder).mTvLoad.setVisibility(View.VISIBLE);
                    ((FooterViewHolder) holder).mTvLoad.setText(mContext.getString(R.string.loading));
                    break;
                case DO_NOT_LOAD:
                    ((FooterViewHolder) holder).mPbLoad.setVisibility(View.GONE);
                    ((FooterViewHolder) holder).mTvLoad.setVisibility(View.GONE);
                    break;
                case LOAD_DATA_FAILED:
                    ((FooterViewHolder) holder).mPbLoad.setVisibility(View.GONE);
                    ((FooterViewHolder) holder).mTvLoad.setVisibility(View.VISIBLE);
                    ((FooterViewHolder) holder).mTvLoad.setText(mContext.getString(R.string.load_fail));
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
    private class EmptyViewHolder extends ViewHolder {
        TextView mTvEmpty;

        EmptyViewHolder(View view) {
            super(view);
            mTvEmpty = (TextView) view.findViewById(R.id.tv_empty_text);
        }
    }

    //填充数据布局
    private class NormalItemHolder extends ViewHolder {
        TextView mTvCommonPhoneName;
        TextView mTvCommonPhoneAddress;
        TextView mTvCommonPhoneNumber;

        NormalItemHolder(View view) {
            super(view);
            mTvCommonPhoneName = (TextView) view.findViewById(R.id.tv_common_phone_name);
            mTvCommonPhoneAddress = (TextView) view.findViewById(R.id.tv_common_phone_address);
            mTvCommonPhoneNumber = (TextView) view.findViewById(R.id.tv_common_phone_number);
        }
    }

    //上拉加载布局
    private class FooterViewHolder extends ViewHolder {
        ProgressBar mPbLoad;
        TextView mTvLoad;

        FooterViewHolder(View view) {
            super(view);
            mPbLoad = (ProgressBar) view.findViewById(R.id.pb_load);
            mTvLoad = (TextView) view.findViewById(R.id.tv_load);
        }
    }
}
