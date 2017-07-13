package com.dongdong.app.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.dd121.community.R;
import com.ddclient.jnisdk.InfoUser;
import com.dongdong.app.base.BaseApplication;
import com.dongdong.app.bean.PhoneMessBean;
import com.dongdong.app.bean.UserInfoBean;
import com.dongdong.app.util.LogUtils;
import com.dongdong.app.util.PhoneUtils;
import com.dongdong.app.util.TDevice;

public class AuthorizedAccountListAdapter extends BaseAdapter {
    private LayoutInflater mInflater;
    private ArrayList<UserInfoBean> mList = new ArrayList<>();

    public AuthorizedAccountListAdapter(Context context) {
        mInflater = LayoutInflater.from(context);
    }

    public void setData(ArrayList<InfoUser> userList) {
        mList.clear();
        for (InfoUser infoUser : userList) {
            mList.add(new UserInfoBean(infoUser, null));
        }
    }

    public ArrayList<UserInfoBean> getData() {
        return mList;
    }

    @Override
    public int getCount() {
        return getData().size();
    }

    @Override
    public UserInfoBean getItem(int position) {
        return getData().get(position);
    }

    @Override
    public long getItemId(int arg0) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Holder hold;
        if (convertView == null) {
            hold = new Holder();
            convertView = mInflater.inflate(R.layout.authorized_account_item, null);
            hold.userName = (TextView) convertView.findViewById(R.id.tv_user_name);
            hold.nickname = (TextView) convertView.findViewById(R.id.tv_nick_name);
            convertView.setTag(hold);
        } else {
            hold = (Holder) convertView.getTag();
        }
        UserInfoBean userInfoBean = getData().get(position);
        hold.userName.setText(userInfoBean.getUserInfo().userName);
        hold.nickname.setText(PhoneUtils.getContactNameByPhoneNum(userInfoBean.getUserInfo().userName));
        return convertView;
    }

    private static class Holder {
        TextView userName;
        TextView nickname;
    }
}
