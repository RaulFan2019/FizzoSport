package com.byl.imageselector.utils;


import android.content.Context;
import android.content.SharedPreferences;

public class PreferencesUtils {
	public static String  IMGSELECTOR="IMGSELECTOR";
	
	public static String getSharePreStr(Context mContext,String field){
		SharedPreferences sp=(SharedPreferences) mContext.getSharedPreferences(IMGSELECTOR, 0);
		String s=sp.getString(field,"");
		return s;
	}
	public static int getSharePreInt(Context mContext,String field){
		SharedPreferences sp=(SharedPreferences) mContext.getSharedPreferences(IMGSELECTOR, 0);
		int i=sp.getInt(field,0);
		return i;
	}
	public static boolean getSharePreBoolean(Context mContext,String field){
		SharedPreferences sp=(SharedPreferences) mContext.getSharedPreferences(IMGSELECTOR, 0);
		boolean i=sp.getBoolean(field, false);
		return i;
	}
	public static void putSharePre(Context mContext,String field,String value){
		SharedPreferences sp=(SharedPreferences) mContext.getSharedPreferences(IMGSELECTOR, 0);
		sp.edit().putString(field, value).commit();
	}
	public static void putSharePre(Context mContext,String field,int value){
		SharedPreferences sp=(SharedPreferences) mContext.getSharedPreferences(IMGSELECTOR, 0);
		sp.edit().putInt(field, value).commit();
	}
	
	public static void putSharePre(Context mContext,String field,Boolean value){
		SharedPreferences sp=(SharedPreferences) mContext.getSharedPreferences(IMGSELECTOR, 0);
		sp.edit().putBoolean(field, value).commit();
	}
	
	public static void clearSharePre(Context mContext){
		try {
			SharedPreferences sp=(SharedPreferences) mContext.getSharedPreferences(IMGSELECTOR, 0);
			sp.edit().clear().commit();
		} catch (Exception e) {
		}
	}
		
		
}
