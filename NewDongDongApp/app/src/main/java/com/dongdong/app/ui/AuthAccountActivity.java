package com.dongdong.app.ui;

import java.util.ArrayList;

import android.content.DialogInterface;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.dd121.community.R;
import com.ddclient.dongsdk.AbstractDongCallbackProxy;
import com.ddclient.jnisdk.InfoUser;
import com.ddclient.dongsdk.DeviceInfo;
import com.ddclient.dongsdk.DongSDKProxy;
import com.dongdong.app.AppConfig;
import com.dongdong.app.AppContext;
import com.dongdong.app.adapter.AuthorizedAccountListAdapter;
import com.dongdong.app.base.BaseActivity;
import com.dongdong.app.base.BaseApplication;
import com.dongdong.app.bean.UserInfoBean;
import com.dongdong.app.ui.dialog.CommonDialog;
import com.dongdong.app.util.LogUtils;
import com.dongdong.app.widget.TitleBar;
import com.dongdong.app.widget.TitleBar.OnTitleBarClickListener;

public class AuthAccountActivity extends BaseActivity implements
        OnTitleBarClickListener, OnClickListener, OnItemClickListener {

    private TitleBar mTitleBar;
    private Button mBtAuth;
    private EditText mEtAccount;
    private ListView mListView;
    private AuthorizedAccountListAdapter mListAdapter;
    private ArrayList<InfoUser> mUserList = new ArrayList<>();
    private UserInfoBean mUserInfoBean;
    private String mAccountName;
    private CommonDialog mDialog, mDialog2;

    private DeviceInfo mDeviceInfo;
    private AuthAccountActivityDongAccountProxy mAccountProxy
            = new AuthAccountActivityDongAccountProxy();

    @Override
    protected int getLayoutId() {
        return R.layout.activity_authorized_account;
    }

    @Override
    public void initView() {
        mTitleBar = (TitleBar) findViewById(R.id.tb_title);
        mBtAuth = (Button) findViewById(R.id.bt_auth);
        mEtAccount = (EditText) findViewById(R.id.et_account);
        mListView = (ListView) findViewById(R.id.lv_list_account);
    }

    @Override
    public void initData() {
        Intent intent = getIntent();
        mDeviceInfo = (DeviceInfo) intent.getSerializableExtra(AppConfig.BUNDLE_KEY_DEVICE_INFO);
        LogUtils.i("AuthAccountActivity.clazz--->>> initData mDeviceInfo:" + mDeviceInfo);

        mDialog = new CommonDialog(this);
        mDialog2 = new CommonDialog(this);

        mTitleBar.setTitleBarContent(mDeviceInfo.deviceName);
        mTitleBar.setAddArrowShowing(false);
        mTitleBar.setOnTitleBarClickListener(this);

        mListAdapter = new AuthorizedAccountListAdapter(this);
        mListView.setAdapter(mListAdapter);

        mBtAuth.setOnClickListener(this);
        mListView.setOnItemClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        DongSDKProxy.registerAccountCallback(mAccountProxy);
        DongSDKProxy.requestGetDeviceAuthorizeAccounts(mDeviceInfo.dwDeviceID);
    }

    @Override
    protected void onPause() {
        super.onPause();
        DongSDKProxy.unRegisterAccountCallback(mAccountProxy);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onBackClick() {
        finish();
    }

    @Override
    public void onTitleClick() {
    }

    @Override
    public void onAddClick() {
    }

    @Override
    public void onFinishClick() {
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.bt_auth:
                mAccountName = mEtAccount.getText().toString().trim();
                if (TextUtils.isEmpty(mAccountName)) {
                    BaseApplication.showToastShortInTop(R.string.empty_authorization);
                    return;
                }
                for (InfoUser info : mUserList) {
                    if (info.userName.equals(mAccountName)) {
                        AppContext.showToastShortInBottom(R.string.yesOrNo);
                        return;
                    }
                }
                View view = LayoutInflater.from(AuthAccountActivity.this).inflate(
                        R.layout.loading_dialog, null);
                TextView tipTextView = (TextView) view.findViewById(R.id.tv_tip);
                tipTextView.setText(getString(R.string.wait));
                mDialog2.setContent(view);
                mDialog2.show();
                DongSDKProxy.requestAuthorize(mAccountName, mDeviceInfo.dwDeviceID);
                break;
            default:
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        mUserInfoBean = mListAdapter.getItem(position);
        mDialog.setMessage(R.string.deleteAuthorization);
        mDialog.setPositiveButton(R.string.sure, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                View view = LayoutInflater.from(AuthAccountActivity.this).inflate(
                        R.layout.loading_dialog, null);
                TextView tipTextView = (TextView) view.findViewById(R.id.tv_tip);
                tipTextView.setText(getString(R.string.wait));
                mDialog2.setContent(view);
                mDialog2.show();
                DongSDKProxy.requestDeleteDevice(mUserInfoBean.getUserInfo().userID,
                        mDeviceInfo.dwDeviceID);
            }
        });
        mDialog.setNegativeButton(getString(R.string.cancel), null);
        mDialog.show();
    }

    private class AuthAccountActivityDongAccountProxy extends
            AbstractDongCallbackProxy.DongAccountCallbackImp {

        @Override
        public int onAuthenticate(InfoUser tInfo) {
            LogUtils.i("AuthAccountActivity.clazz--->>>OnAuthenticate........tInfo:" + tInfo);
            return 0;
        }

        @Override
        public int onAddDeviceUser(int result, int userid) {
            if (mDialog != null) {
                mDialog.dismiss();
            }
            if (mDialog2 != null) {
                mDialog2.dismiss();
            }
            if (result == 0) {
                mUserList.add(new InfoUser(userid, mAccountName, new byte[1], ""));
                mListAdapter.setData(mUserList);
                mListAdapter.notifyDataSetChanged();
                BaseApplication.showToastShortInBottom(R.string.suc);
            } else {
                BaseApplication.showToastShortInBottom(R.string.fail);
            }

            LogUtils.i("AuthAccountActivity.clazz--->>>OnAddDeviceUser........result:"
                    + result);
            return 0;
        }

        @Override
        public int onDelDevice(int result) {
            if (mDialog != null) {
                mDialog.dismiss();
            }
            if (mDialog2 != null) {
                mDialog2.dismiss();
            }
            for (InfoUser infoUser : mUserList) {
                if (infoUser.userName.equals(mUserInfoBean.getUserInfo().userName)) {
                    mUserList.remove(infoUser);
                    break;
                }
            }
            if (result == 0) {
                mListAdapter.setData(mUserList);
                mListAdapter.notifyDataSetChanged();
                BaseApplication.showToastShortInBottom(R.string.suc);

            } else {
                BaseApplication.showToastShortInBottom(R.string.fail);
            }
            LogUtils.i("AuthAccountActivity.clazz--->>>OnDelDevice........result:"
                    + result);
            return 0;
        }

        @Override
        public int onGetDeviceUserInfo(ArrayList<InfoUser> infoUsers) {
            mUserList = infoUsers;
            if (mDialog != null) {
                mDialog.dismiss();
            }
            mListAdapter.setData(mUserList);
            mListAdapter.notifyDataSetChanged();
            LogUtils.i("AuthAccountActivity.clazz--->>>OnGetDeviceUserInfo infoUsers:" + infoUsers);
            return 0;
        }

        @Override
        public int onSetDeviceName(int result) {
            if (result == 0) {
                if (mDialog != null) {
                    mDialog.dismiss();
                }
                BaseApplication.showToastShortInBottom(getString(R.string.suc));
            } else {
                if (mDialog != null) {
                    mDialog.dismiss();
                }
                BaseApplication.showToastShortInBottom(getString(R.string.fail));
            }
            LogUtils.i("AuthAccountActivity.clazz--->>>OnSetDeviceName result:" + result);
            return 0;
        }

        @Override
        public int onUserError(int nErrNo) {
            LogUtils.i("AuthAccountActivity.clazz--->>>OnUserError........nErrNo:" + nErrNo);
            return 0;
        }
    }
}
