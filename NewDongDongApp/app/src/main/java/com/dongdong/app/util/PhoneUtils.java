package com.dongdong.app.util;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.CommonDataKinds.Photo;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import com.dongdong.app.base.BaseApplication;
import com.dongdong.app.bean.PhoneMessBean;

public class PhoneUtils {

    private static final String[] PHONES_PROJECTION = new String[]{
            Phone.DISPLAY_NAME, Phone.NUMBER, Photo.PHOTO_ID, Phone.CONTACT_ID};

    private static final int PHONES_DISPLAY_NAME_INDEX = 0;
    private static final int PHONES_NUMBER_INDEX = 1;

    private ArrayList<PhoneMessBean> mList = new ArrayList<>();
    private Context mContext;

    public PhoneUtils(Context context) {
        this.mContext = context;
        mList.clear();
        getPhoneContacts();
        getSIMContacts();
    }

    public ArrayList<PhoneMessBean> getPhoneMessBeanList() {
        return mList;
    }

    public void getPhoneContacts() {
        ContentResolver resolver = mContext.getContentResolver();
        Cursor phoneCursor = resolver.query(Phone.CONTENT_URI,
                PHONES_PROJECTION, null, null, null);
        if (phoneCursor != null) {
            while (phoneCursor.moveToNext()) {
                String phoneNumber = phoneCursor.getString(PHONES_NUMBER_INDEX);
                if (TextUtils.isEmpty(phoneNumber))
                    continue;
                String contactName = phoneCursor.getString(PHONES_DISPLAY_NAME_INDEX);
                mList.add(new PhoneMessBean(contactName, phoneNumber));
            }
            LogUtils.i("PhoneUtil.clazz->mList:" + mList);
            phoneCursor.close();
        }
    }

    private void getSIMContacts() {
        ContentResolver resolver = mContext.getContentResolver();
        Uri uri = Uri.parse("content://icc/adn");
        Cursor phoneCursor = resolver.query(uri, PHONES_PROJECTION, null, null,
                null);
        if (phoneCursor != null) {
            while (phoneCursor.moveToNext()) {
                String phoneNumber = phoneCursor.getString(PHONES_NUMBER_INDEX);
                if (TextUtils.isEmpty(phoneNumber))
                    continue;
                String contactName = phoneCursor
                        .getString(PHONES_DISPLAY_NAME_INDEX);
                mList.add(new PhoneMessBean(contactName,
                        phoneNumber));
            }
            phoneCursor.close();
        }
    }

    //获取所有联系人与号码
    public static void getPhoneNumberFromMobile(Context context) {
        Cursor cursor = context.getContentResolver().query(Phone.CONTENT_URI, null, null, null, null);
        //moveToNext方法返回的是一个boolean类型的数据
        while (cursor.moveToNext()) {
            //读取通讯录的姓名
            String name = cursor.getString(cursor.getColumnIndex(Phone.DISPLAY_NAME));
            //读取通讯录的号码
            String number = cursor.getString(cursor.getColumnIndex(Phone.NUMBER));
            LogUtils.i("PhoneUtil.clazz->getPhoneNumberFromMobile()->name:" + name + ",number:" + number);
        }
    }

    /**
     * 通过手机号获取联系人姓名
     */
    private static String getContactNameByPhone(Context context, String phoneNum) {
        String contactName = "";
//        Uri uri = Uri.parse("content://icc/adn");
        ContentResolver cr = context.getContentResolver();
//        Cursor phoneCursor = cr.query(uri, PHONES_PROJECTION, null, null, null);
        Cursor pCur = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                ContactsContract.CommonDataKinds.Phone.NUMBER + " = ?",
                new String[]{phoneNum}, null);
        if (pCur != null) {
            while (pCur.moveToNext()) {
                contactName = pCur.getString(pCur.getColumnIndex(Phone.DISPLAY_NAME));
                if (!TextUtils.isEmpty(contactName)) {
                    break;
                }
            }
        }
        return contactName;
    }

    public static String getContactNameByPhoneNum(String phoneNumber) {
        //普通手机号
        String contactName = getContactNameByPhone(BaseApplication.context(), phoneNumber);
        LogUtils.i("PhoneUtil.clazz->contactName:" + contactName);
        //如果查询联系人姓名为空，而且为手机号
        if (TextUtils.isEmpty(contactName) && phoneNumber.length() <= 11) {
            String p = phoneNumber.substring(0, 3);
            String n = phoneNumber.substring(3, 7);
            String r = phoneNumber.substring(7, 11);
            //加空格手机号
            String firstPhoneNumber = p + " " + n + " " + r;
            contactName = PhoneUtils.getContactNameByPhone(BaseApplication.context(), firstPhoneNumber);
            LogUtils.i("PhoneUtil.clazz->firstContactName:" + contactName);

            if (TextUtils.isEmpty(contactName)) {
                //+86手机号
                String threePhoneNumber = "+86" + phoneNumber;
                contactName = PhoneUtils.getContactNameByPhone(BaseApplication.context(), threePhoneNumber);
                LogUtils.i("PhoneUtil.clazz->threeContactName:" + contactName);
            }

            if (TextUtils.isEmpty(contactName)) {
                //+86和空格手机号
                String secondPhoneNumber = "+86 " + firstPhoneNumber;
                contactName = PhoneUtils.getContactNameByPhone(BaseApplication.context(), secondPhoneNumber);
                LogUtils.i("PhoneUtil.clazz->threeContactName:" + contactName);
            }
        }
        return contactName;
    }

    public static void insertContact2Phone(Context context, String nickName, String phoneNum) {
        /* 往 raw_contacts 中添加数据，并获取添加的id号 */
        Uri uri = Uri.parse("content://com.android.contacts/raw_contacts");
        ContentResolver resolver = context.getContentResolver();
        ContentValues values = new ContentValues();
        long contactId = ContentUris.parseId(resolver.insert(uri, values));
        uri = Uri.parse("content://com.android.contacts/data");
        values.put("raw_contact_id", contactId);
        values.put("mimetype", "vnd.android.cursor.item/name");
        values.put("data2", nickName);
        resolver.insert(uri, values);

        // 添加电话
        values.clear();
        values.put("raw_contact_id", contactId);
        values.put("mimetype", "vnd.android.cursor.item/phone_v2");
        values.put("data2", "2");
        values.put("data1", phoneNum);
        resolver.insert(uri, values);
    }

    /**
     * 判断手机是否是通话中
     */
    public static boolean isTelephonyCalling(Context context) {
        boolean calling = false;
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if (TelephonyManager.CALL_STATE_OFFHOOK == telephonyManager.getCallState() ||
                TelephonyManager.CALL_STATE_RINGING == telephonyManager.getCallState()) {
            calling = true;
        }
        return calling;
    }

    public static boolean isMobile(String phoneNumber) {
        Pattern pattern = Pattern.compile("^1[0-9]{10}$");
        Matcher matcher = pattern.matcher(phoneNumber);
        return matcher.matches();
    }
}
