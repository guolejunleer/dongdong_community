package com.dongdong.app.util;

import java.util.ArrayList;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.CommonDataKinds.Photo;
import android.text.TextUtils;

import com.dongdong.app.bean.PhoneMessBean;

public class PhoneMessUtils {

	private static final String[] PHONES_PROJECTION = new String[] {
			Phone.DISPLAY_NAME, Phone.NUMBER, Photo.PHOTO_ID, Phone.CONTACT_ID };

	private static final int PHONES_DISPLAY_NAME_INDEX = 0;
	private static final int PHONES_NUMBER_INDEX = 1;

	private ArrayList<PhoneMessBean> mList = new ArrayList<PhoneMessBean>();
	private Context mContex;

	public PhoneMessUtils(Context context) {
		this.mContex = context;
		mList.clear();
		getPhoneContacts();
		getSIMContacts();
	}

	public ArrayList<PhoneMessBean> getPhoneMessBeanList() {
		return mList;
	}

	private void getPhoneContacts() {
		ContentResolver resolver = mContex.getContentResolver();
		Cursor phoneCursor = resolver.query(Phone.CONTENT_URI,
				PHONES_PROJECTION, null, null, null);
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

	private void getSIMContacts() {
		ContentResolver resolver = mContex.getContentResolver();
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

}
