package com.magicwork.photoablumlib;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

/**
 * 获取状�?�信�?
 * 
 * @author licq
 *
 */
public class PhoneStateUtils {
	/**
	 * 获取版本�?
	 * 
	 * @return 当前应用的版本号
	 */
	/*public static String getVersion() {
		try {
			PackageManager manager = App.getInstance().getPackageManager();
			PackageInfo info = manager.getPackageInfo(App.getInstance().getPackageName(), 0);
			String version = info.versionName;
			return version;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}*/

	/**
	 * 得到设备屏幕的宽�?
	 */
	public static int getScreenWidth(Context context) {
		return context.getResources().getDisplayMetrics().widthPixels;
	}

	/**
	 * 得到设备屏幕的高�?
	 */
	public static int getScreenHeight(Context context) {
		return context.getResources().getDisplayMetrics().heightPixels;
	}

	/**
	 * 得到设备的密�?
	 */
	public static float getScreenDensity(Context context) {
		return context.getResources().getDisplayMetrics().density;
	}

	public static int dip2px(Context context, float dipValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dipValue * scale + 0.5f);
	}

	public static int px2dip(Context context, float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}
}
