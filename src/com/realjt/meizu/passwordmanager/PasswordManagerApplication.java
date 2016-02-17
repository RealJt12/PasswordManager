package com.realjt.meizu.passwordmanager;

import android.app.Application;

import com.realjt.meizu.passwordmanager.utils.SettingsUtils;

/**
 * 密码管理
 * 
 * @author Administrator
 */
public class PasswordManagerApplication extends Application
{
	@Override
	public void onCreate()
	{
		super.onCreate();

		SettingsUtils.initSharedPreferences(getApplicationContext());
	}

}