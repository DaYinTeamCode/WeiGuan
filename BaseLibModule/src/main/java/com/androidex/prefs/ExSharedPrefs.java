package com.androidex.prefs;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.text.TextUtils;
import android.util.Base64;

import com.androidex.util.IOUtil;
import com.androidex.util.TextUtil;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * 首选项扩展类
 */
public class ExSharedPrefs {

	private SharedPreferences mSharedPrefs;

	public ExSharedPrefs(Context context, String name) {

		mSharedPrefs = context.getSharedPreferences(name, Context.MODE_PRIVATE);
	}

	public Editor edit(){

		return mSharedPrefs.edit();
	}

	public  boolean containsKey(String key)
	{
		return  mSharedPrefs.contains(key);
	}

	public boolean putInt(String key, int value) {

		Editor editor = mSharedPrefs.edit();
		editor.putInt(key, value);
		return editor.commit();
	}

	/**
	 * 如果没有该key，默认返回0
	 * @param key
	 * @return
	 */
	public int getInt(String key) {

		return getInt(key, 0);
	}

	public int getInt(String key, int defaultValue) {

		return mSharedPrefs.getInt(key, defaultValue);
	}

	public boolean putLong(String key, long value) {

		Editor editor = mSharedPrefs.edit();
		editor.putLong(key, value);
		return editor.commit();
	}

	/**
	 * 如果没有该key，默认返回0
	 * @param key
	 * @return
	 */
	public long getLong(String key) {

		return getLong(key, 0);
	}

	public long getLong(String key, long defaultValue) {

		return mSharedPrefs.getLong(key, defaultValue);
	}

	public boolean putFloat(String key, float value) {

		Editor editor = mSharedPrefs.edit();
		editor.putFloat(key, value);
		return editor.commit();
	}

	/**
	 * 如果没有该key，默认返回0
	 * @param key
	 * @return
	 */
	public float getFloat(String key) {

		return getFloat(key, 0);
	}

	public float getFloat(String key, float defaultValue) {

		return mSharedPrefs.getFloat(key, defaultValue);
	}

	public boolean putBoolean(String key, boolean value) {

		Editor editor = mSharedPrefs.edit();
		editor.putBoolean(key, value);
		return editor.commit();
	}

	/**
	 * 如果没有该key，默认返回false
	 * @param key
	 * @return
	 */
	public boolean getBoolean(String key) {

		return getBoolean(key, false);
	}

	public boolean getBoolean(String key, boolean defaultValue) {

		return mSharedPrefs.getBoolean(key, defaultValue);
	}

	public boolean putString(String key, String value) {

		Editor editor = mSharedPrefs.edit();
		editor.putString(key, value);
		return editor.commit();
	}

	/**
	 * 如果没有该key，默认返回空串
	 * @param key
	 * @return
	 */
	public String getString(String key) {

		return getString(key, TextUtil.TEXT_EMPTY);
	}

	public String getString(String key, String defaultValue) {

		return mSharedPrefs.getString(key, defaultValue);
	}

	/**
	 * 保存字符串set
	 * @param key
	 * @param values
	 * @return
	 */
	public boolean putStringSet(String key, Set<String> values){

		Editor editor = mSharedPrefs.edit();
		editor.putStringSet(key, values);
		return editor.commit();
	}

	/**
	 * 获取字符串set
	 * @param key
	 * @return
	 */
	public Set<String> getStringSet(String key){

		return mSharedPrefs.getStringSet(key, null);
	}

	/**
	 * 获取字符串set
	 * @param key
	 * @return
	 */
	public Set<String> getStringSet(String key, Set<String> set){

		return mSharedPrefs.getStringSet(key, set);
	}

	/**
	 * 存储序列化对象
	 * @param key
	 * @param value
	 * @return
	 */
	public boolean putSerializable(String key, Serializable value) {

		if(TextUtil.isEmpty(key))
			return false;

		ByteArrayOutputStream baos = null;
		ObjectOutputStream oos = null;

		try {

			if(value == null){

				return removeKey(key);
			}else{

				baos = new ByteArrayOutputStream();
				oos = new ObjectOutputStream(baos);
				oos.writeObject(value);
				return putString(key, new String(Base64.encode(baos.toByteArray(), Base64.DEFAULT)));
			}

		} catch (Exception e) {
			
			e.printStackTrace();
			
		}finally{
			
			IOUtil.closeOutStream(oos);
			IOUtil.closeOutStream(baos);
		}
		
		return false;
	}

	/**
	 * 获取序列化对象
	 * @param key
	 * @return
	 */
	public Serializable getSerializable(String key) {

		if (TextUtils.isEmpty(key))
			return null;

		String objBase64 = getString(key, TextUtil.TEXT_EMPTY);
		if (TextUtils.isEmpty(objBase64))
			return null;

		ByteArrayInputStream bais = null;
		ObjectInputStream ois = null;

		try {

			byte[] base64Bytes = Base64.decode(objBase64.getBytes(), Base64.DEFAULT);
			bais = new ByteArrayInputStream(base64Bytes);
			ois = new ObjectInputStream(bais);
			return (Serializable) ois.readObject();

		} catch (Exception e) {

			e.printStackTrace();

		}finally{

			IOUtil.closeInStream(ois);
			IOUtil.closeInStream(bais);
		}

		return null;
	}

	/**
	 * 批量保存
	 * @param params
	 * @return
	 */
	public boolean putBatch(Map<String, Object> params) {

		if(params == null)
			return false;

		Editor editor = mSharedPrefs.edit();
		Iterator iter = params.entrySet().iterator();
		Map.Entry entry = null;
		String key = null;
		Object value = null;
		while (iter.hasNext()) {

			entry = (Map.Entry) iter.next();
			key = entry.getKey().toString();
			value = entry.getValue();

			if (value instanceof Integer) {

				editor.putInt(key, (Integer) value);

			} else if (value instanceof String) {

				editor.putString(key, (String) value);

			} else if (value instanceof Float) {

				editor.putFloat(key, (Float) value);

			} else if (value instanceof Long) {

				editor.putLong(key, (Long) value);

			} else if (value instanceof Boolean) {

				editor.putBoolean(key, (Boolean) value);

			}else if(value instanceof Serializable){

				putSerializable(key, (Serializable) value);
			}
		}

		return editor.commit();
	}

	/**
	 * 移除某个key所对应的值
	 * @param key
	 * @return
	 */
	public boolean removeKey(String key) {

		Editor editor = mSharedPrefs.edit();
		editor.remove(key);
		return editor.commit();
	}

	/**
	 * 批量删除指定key的值
	 * @param key
	 * @return
	 */
	public boolean removeKeys(String... key) {

		Editor editor = mSharedPrefs.edit();

		for (String string : key) {

			editor.remove(string);
		}

		return editor.commit();
	}

	/**
	 * 移除所有存储的值
	 * @return
	 */
	public boolean removeAllKey() {

		Editor editor = mSharedPrefs.edit();
		editor.clear();
		return editor.commit();
	}

	/**
	 * 将一个序列化对象转成String
	 * @param value
	 * @return
	 */
	public String serializableTransformString(Serializable value){

		ByteArrayOutputStream baos = null;
		ObjectOutputStream oos = null;

		try {

			baos = new ByteArrayOutputStream();
			oos = new ObjectOutputStream(baos);
			oos.writeObject(value);
			return new String(Base64.encode(baos.toByteArray(), Base64.DEFAULT));

		} catch (Exception e) {

			e.printStackTrace();

		}finally{

			IOUtil.closeOutStream(oos);
			IOUtil.closeOutStream(baos);
		}

		return null;
	}

	public Editor editor(){

		return mSharedPrefs.edit();
	}
}
