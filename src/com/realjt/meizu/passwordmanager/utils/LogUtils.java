package com.realjt.meizu.passwordmanager.utils;

import android.util.Log;

public class LogUtils
{
	public static final String LOG_TAG = "PasswordManager.Log";

	public static void info(String message)
	{
		Log.i(LOG_TAG, message);
	}

	public static void info(String message, Throwable throwable)
	{
		Log.i(LOG_TAG, message, throwable);
	}

	public static void debug(String message)
	{
		Log.d(LOG_TAG, message);
	}

	public static void debug(String message, Throwable throwable)
	{
		Log.d(LOG_TAG, message, throwable);
	}

	public static void error(String message)
	{
		Log.e(LOG_TAG, message);
	}

	public static void error(String message, Throwable throwable)
	{
		Log.e(LOG_TAG, message, throwable);
	}

	public static void warn(String message)
	{
		Log.w(LOG_TAG, message);
	}

	public static void warn(String message, Throwable throwable)
	{
		Log.w(LOG_TAG, message, throwable);
	}

}
