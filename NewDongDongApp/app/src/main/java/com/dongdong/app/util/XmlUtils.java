package com.dongdong.app.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlSerializer;

import android.util.Xml;

import com.dongdong.app.bean.FunctionBean;

public class XmlUtils {

	public static void createFunctionXml(File file,
			List<FunctionBean> mFunctionsDatas, boolean deleteFlag) {
		// if (file.exists()) {
		// boolean delete2 = file.delete();
		// LogUtils.e("XmlUtils.clazz createdelete2" + delete2);
		// }
		boolean success = false;
		// StringWriter xmlWriter = new StringWriter();
		FileOutputStream fos = null;

		try {
			fos = new FileOutputStream(file);
			XmlSerializer xmlSerializer = Xml.newSerializer();
			// xmlSerializer.setOutput(xmlWriter);
			xmlSerializer.setOutput(fos, "UTF-8");
			xmlSerializer.setFeature(
					"http://xmlpull.org/v1/doc/features.html#indent-output",
					true);
			xmlSerializer.startDocument("utf-8", null);
			xmlSerializer.startTag("", "functions");
			xmlSerializer.attribute("", "date", new Date().toString());
			int size = mFunctionsDatas.size();
			for (int i = 0; i < size; i++) {
				FunctionBean entry = mFunctionsDatas.get(i);
				xmlSerializer.startTag("", "function"); // 创建person节点

				xmlSerializer.startTag("", "name");
				xmlSerializer.text(entry.getName());
				xmlSerializer.endTag("", "name");

				// xmlSerializer.startTag("", "icon");
				// xmlSerializer.text(entry.getIconId() + "");
				// xmlSerializer.endTag("", "icon");

				xmlSerializer.startTag("", "funcid");
				xmlSerializer.text(entry.getFuncId() + "");
				xmlSerializer.endTag("", "funcid");

				xmlSerializer.startTag("", "sequence");
				xmlSerializer.text(entry.getSequence() + "");
				xmlSerializer.endTag("", "sequence");

				xmlSerializer.endTag("", "function");
			}

			xmlSerializer.endTag("", "functions");
			xmlSerializer.endDocument();
			success = true;
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (!success) {
				boolean delete = file.delete();
				LogUtils.e("XmlUtils.clazz create functionx xml faild!!! delete state:"
						+ delete);
			}
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
					LogUtils.e("XmlUtils.clazz fos close faild!!!");
				}
			}
		}
	}

	public static List<FunctionBean> getFunctionsDataByProp(File file) {
		List<FunctionBean> list = null;
		FileInputStream fis = null;
		FunctionBean function = null;
		boolean success = true;
		try {
			fis = new FileInputStream(file);

			XmlPullParser parser = Xml.newPullParser(); // 由android.util.Xml创建一个XmlPullParser实例
			parser.setInput(fis, "UTF-8"); // 设置输入流 并指明编码方式
			int eventType = parser.getEventType();
			while (eventType != XmlPullParser.END_DOCUMENT) {
				switch (eventType) {
				case XmlPullParser.START_DOCUMENT:
					list = new ArrayList<FunctionBean>();
					break;

				case XmlPullParser.START_TAG:
					if (parser.getName().equals("function")) {
						function = new FunctionBean();
					} else if (parser.getName().equals("name")) {
						eventType = parser.next();
						function.setName(parser.getText());
					} else if (parser.getName().equals("icon")) {
						eventType = parser.next();
						function.setIconId(Integer.parseInt(parser.getText()));
					} else if (parser.getName().equals("sequence")) {
						eventType = parser.next();
						function.setSequence(Integer.parseInt(parser.getText()));
					} else if (parser.getName().equals("funcid")) {
						eventType = parser.next();
						function.setFuncId(Integer.parseInt(parser.getText()));
					}
					break;

				case XmlPullParser.END_TAG:
					if (parser.getName().equals("function")) {
						list.add(function);
						function = null;
					}
					break;
				}
				eventType = parser.next();
			}
		} catch (Exception e) {
			success = false;
			LogUtils.e("XmlUtils.clazz getFunctionsDatasByProp faild!!! "
					+ e.getMessage());
		} finally {
			if (!success) {
				boolean delete = file.delete();
				LogUtils.e("XmlUtils.clazz get functionx xml faild!!! delete state:"
						+ delete);
			}
			if (fis != null) {
				try {
					fis.close();
				} catch (IOException e) {
					e.printStackTrace();
					LogUtils.e("XmlUtils.clazz get fis close faild!!!");
				}
			}
		}
		Collections.sort(list, new SequenceComparator());
		return list;
	}

	private static class SequenceComparator implements Comparator<FunctionBean> {

		@Override
		public int compare(FunctionBean lhs, FunctionBean rhs) {
			return lhs.getSequence() - rhs.getSequence();
		}
	}
}
