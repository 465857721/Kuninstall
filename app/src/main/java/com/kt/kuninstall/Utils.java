package com.kt.kuninstall;

import java.util.UUID;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

public class Utils {
//	public static String getUUID() {
//
//		String uuid = UUID.randomUUID().toString().trim().replaceAll("-", "");
//		return uuid;
//	}

	public static String getUUID(Context context) {
		TelephonyManager tm = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		final String tmDevice, tmSerial, tmPhone, androidId;
		tmDevice = "" + tm.getDeviceId();
		tmSerial = "" + tm.getSimSerialNumber();
		androidId = ""
				+ android.provider.Settings.Secure.getString(
						context.getContentResolver(),
						android.provider.Settings.Secure.ANDROID_ID);
		UUID deviceUuid = new UUID(androidId.hashCode(),
				((long) tmDevice.hashCode() << 32) | tmSerial.hashCode());
		String uniqueId = deviceUuid.toString();

		Log.d("school", "uuid=" + uniqueId);
		uniqueId = uniqueId.toString().trim().replaceAll("-",
		 "");
		return uniqueId;
	}

	public static void removeString(Context context, String columnName) {
		try {
			Editor passfileEditor = context
					.getSharedPreferences("powerword", Context.MODE_PRIVATE).edit();
			passfileEditor.remove(columnName);
			passfileEditor.commit(); // 提交数据
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 从SharedPreferences中读取String
	 * 
	 * */
	public static int getInt(Context context, String columnName,
			int defValue) {
		try {
			SharedPreferences sharedPreferences = context.getSharedPreferences(
					"powerword", Context.MODE_PRIVATE);
			int show = sharedPreferences.getInt(columnName, defValue);
			return show;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}
	/**
	 * 向SharedPreferences中存入String
	 * 
	 * */
	public static void saveInt(Context context, String columnName,
			int value) {
		try {
			Editor passfileEditor = context
					.getSharedPreferences("powerword", Context.MODE_PRIVATE).edit();
			passfileEditor.putInt(columnName, value);
			
			passfileEditor.commit(); // 提交数据
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
