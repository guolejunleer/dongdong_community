package com.dongdong.app.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.dd121.louyu.R;
import com.dongdong.app.base.BaseApplication;
import com.dongdong.app.bean.UserBean;
import com.dongdong.app.db.UserOpe;
import com.dongdong.app.ui.LoginActivity;
import com.dongdong.app.ui.dialog.TipDialogManager;

import java.util.ArrayList;
import java.util.List;

public class UserPreferenceAdapter extends BaseAdapter {

    private LoginActivity mLoginActivity;
    private List<UserBean> mUserBeanList = new ArrayList<>();

    public UserPreferenceAdapter(LoginActivity activity, List<UserBean> list) {
        mLoginActivity = activity;
        this.mUserBeanList = list;
    }

    @Override
    public int getCount() {
        return mUserBeanList.size();
    }

    @Override
    public Object getItem(int position) {
        return mUserBeanList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(BaseApplication.context()).
                    inflate(R.layout.option_item, null);
            holder.textView = (TextView) convertView.findViewById(R.id.item_text);
            holder.imageView = (ImageView) convertView.findViewById(R.id.delImage);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.textView.setText(mUserBeanList.get(position).getUserName());
        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final UserBean userBean = mUserBeanList.get(position);
                String msg = BaseApplication.context().getString(R.string.tip_delete_user,
                        userBean.getUserName());
                TipDialogManager.showNormalTipDialog(mLoginActivity, new TipDialogManager.OnTipDialogButtonClick() {
                            @Override
                            public void onPositiveButtonClick() {
                                mUserBeanList.remove(userBean);
                                UserOpe.deleteDataById(BaseApplication.context(), userBean.getId());
                                notifyDataSetChanged();
                                mLoginActivity.refreshUI(userBean, false);
                            }

                            @Override
                            public void onNegativeButtonClick() {
                            }
                        }, BaseApplication.context().getString(R.string.tip),
                        msg, BaseApplication.context().getString(R.string.ok),
                        BaseApplication.context().getString(R.string.cancel));

            }
        });
        return convertView;
    }

    static class ViewHolder {
        TextView textView;
        ImageView imageView;
    }

}


