package com.grupocsi.btrmovil;

import android.content.Context;
import android.content.SharedPreferences;

public class ClsConfiguracion {
	private final String SHARED_PREFS_FILE = "UserPrefs";
	private final String KEY_NAME = "name";
	private Context mContext;
	
	public ClsConfiguracion(Context context){
		mContext = context;
	}
	
	private SharedPreferences getSettings(){
		return mContext.getSharedPreferences(SHARED_PREFS_FILE, 0);
	}
	
	public String getUserName(){
		return getSettings().getString(KEY_NAME, null);
	}
	
	public void setUserName(String name){
		SharedPreferences.Editor editor = getSettings().edit();
		editor.putString(KEY_NAME, name);
		editor.commit();
	}
}
