package com.dongdong.app.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

import android.content.Context;
import android.content.SharedPreferences;

public class SPUtils {

	/**
	 * 通过索引键，设置配置文件的值
	 * 
	 * @param context
	 * @param spName配置文件名称
	 * @param key
	 *            键名
	 * @param object
	 *            值类型
	 */
	public static void setParam(Context context, String spName, String key,
			Object object) {

		String type = object.getClass().getSimpleName();
		SharedPreferences sp = context.getSharedPreferences(spName,
				Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit();

		if ("String".equals(type)) {
			editor.putString(key, (String) object);
		} else if ("Integer".equals(type)) {
			editor.putInt(key, (Integer) object);
		} else if ("Boolean".equals(type)) {
			editor.putBoolean(key, (Boolean) object);
		} else if ("Float".equals(type)) {
			editor.putFloat(key, (Float) object);
		} else if ("Long".equals(type)) {
			editor.putLong(key, (Long) object);
		}
		LogUtils.i("SPUtils.clazz--->>> key:" + key + ",object:" + object);
		editor.commit();
	}

	/**
	 * 通过索引键，获取配置文件的值
	 * 
	 * @param context
	 * @param spName
	 *            配置文件名称
	 * @param key
	 *            键名
	 * @param defaultObject
	 *            默认值
	 * @return
	 */
	public static Object getParam(Context context, String spName, String key,
			Object defaultObject) {

		String type = defaultObject.getClass().getSimpleName();
		SharedPreferences sp = context.getSharedPreferences(spName,
				Context.MODE_PRIVATE);

		if ("String".equals(type)) {
			return sp.getString(key, (String) defaultObject);
		} else if ("Integer".equals(type)) {
			return sp.getInt(key, (Integer) defaultObject);
		} else if ("Boolean".equals(type)) {
			return sp.getBoolean(key, (Boolean) defaultObject);
		} else if ("Float".equals(type)) {
			return sp.getFloat(key, (Float) defaultObject);
		} else if ("Long".equals(type)) {
			return sp.getLong(key, (Long) defaultObject);
		}
		return null;
	}

	/**
	 * 移除某个key值已经对应的值
	 * 
	 * @param spName
	 *            配置文件名称
	 * @param context
	 * @param key
	 *            键名
	 */
	public static void remove(Context context, String spName, String key) {
		SharedPreferences sp = context.getSharedPreferences(spName,
				Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit();
		editor.remove(key);
		SharedPreferencesCompat.apply(editor);
	}

	/**
	 * 清除所有数据
	 * 
	 * @param spName
	 *            配置文件名称
	 * @param context
	 */
	public static void removeAll(Context context, String spName) {
		SharedPreferences sp = context.getSharedPreferences(spName,
				Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit();
		editor.clear();
		SharedPreferencesCompat.apply(editor);
	}

	/**
	 * 查询某个key是否已经存在
	 * 
	 * @param spName
	 *            配置文件名称
	 * @param context
	 * @param key
	 *            键名
	 * @return
	 */
	public static boolean contains(Context context, String spName, String key) {
		SharedPreferences sp = context.getSharedPreferences(spName,
				Context.MODE_PRIVATE);
		return sp.contains(key);
	}

	/**
	 * 返回所有的键值对
	 * 
	 * @param spName
	 *            配置文件名称
	 * @param context
	 * @return
	 */
	public static Map<String, ?> getAll(Context context, String spName) {
		SharedPreferences sp = context.getSharedPreferences(spName,
				Context.MODE_PRIVATE);
		return sp.getAll();
	}

	/**
	 * 创建一个解决SharedPreferencesCompat.apply方法的一个兼容类
	 * 
	 * @author leer
	 * 
	 */
	private static class SharedPreferencesCompat {
		private static final Method sApplyMethod = findApplyMethod();

		/**
		 * 反射查找apply的方法
		 * 
		 * @return
		 */
		@SuppressWarnings({ "unchecked", "rawtypes" })
		private static Method findApplyMethod() {
			try {
				Class clz = SharedPreferences.Editor.class;
				return clz.getMethod("apply");
			} catch (NoSuchMethodException e) {
			}

			return null;
		}

		/**
		 * 如果找到则使用apply执行，否则使用commit
		 * 
		 * @param editor
		 */
		public static void apply(SharedPreferences.Editor editor) {
			try {
				if (sApplyMethod != null) {
					sApplyMethod.invoke(editor);
					return;
				}
			} catch (IllegalArgumentException e) {
			} catch (IllegalAccessException e) {
			} catch (InvocationTargetException e) {
			}
			editor.commit();
		}
	}

}
