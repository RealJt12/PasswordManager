package com.realjt.meizu.passwordmanager.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;

import com.realjt.meizu.passwordmanager.R;
import com.realjt.meizu.passwordmanager.common.Constants;

/**
 * 应用设置工具类
 * 
 * @author Administrator
 * 
 */
public class SettingsUtils
{
	private static final String SHAREDPREFERENCES_NAME = "Settings";

	private static SharedPreferences sharedPreferences;

	private static Context context;

	public static void initSharedPreferences(Context context)
	{
		LogUtils.debug("start init settings sharedpreferences");

		Constants.APP_CONTEXT = context;
		if (sharedPreferences == null)
		{
			SettingsUtils.context = context;
			sharedPreferences = context.getSharedPreferences(
					SHAREDPREFERENCES_NAME, Context.MODE_PRIVATE);
		}
	}

	/**
	 * 是否第一次登录
	 * 
	 * @param context
	 *            上下文对象
	 * @return 是否是第一次登录
	 */
	public static boolean isFirstLogin()
	{
		return sharedPreferences.getBoolean("first_login", true);
	}

	/**
	 * 设置是否是第一次登录
	 * 
	 * @param context
	 *            上下文对象
	 * @param firstLogin
	 *            是否是第一次登录
	 */
	public static void setFirstLogin(boolean firstLogin)
	{
		sharedPreferences.edit().putBoolean("first_login", firstLogin).commit();
	}

	public static void deleteFirstLogin()
	{
		sharedPreferences.edit().remove("first_login").commit();
	}

	/**
	 * 加密设置登录密码
	 * 
	 * @param context
	 *            上下文对象
	 * @param loginPassword
	 *            登录密码 未加密
	 */
	public static void setLoginPassword(String loginPassword)
	{
		sharedPreferences
				.edit()
				.putString("login_password",
						EncryptUtils.encryptSHA256(loginPassword)).commit();
	}

	/**
	 * 得到登录密码,未解密
	 * 
	 * @param context
	 *            上下文对象
	 * @return 登录密码 未解密
	 */
	public static String getLoginPassword()
	{
		return sharedPreferences.getString("login_password", null);
	}

	public static void deleteLoginPassword()
	{
		if (null != Constants.LOGIN_PASSWORD
				&& !"".equals(Constants.LOGIN_PASSWORD))
		{
			sharedPreferences.edit().remove("login_password").commit();
		}
	}

	/**
	 * 按下HOME键是否立即退出
	 * 
	 * @param context
	 *            上下文对象
	 * @return 是否立即退出
	 */
	public static boolean isExitPressHomeKey()
	{
		return sharedPreferences.getBoolean("press_homekey_exit", true);
	}

	/**
	 * 设置按下HOME键是否立即退出
	 * 
	 * @param context
	 *            上下文对象
	 * @param exit
	 *            是否立即退出
	 */
	public static void setExitPressHomeKey(boolean exit)
	{
		sharedPreferences.edit().putBoolean("press_homekey_exit", exit)
				.commit();
	}

	/**
	 * 得到登录提示语
	 * 
	 * @param context
	 *            上下文对象
	 * @return 登录提示语
	 */
	public static String getLoginPrompt()
	{
		return sharedPreferences.getString("login_prompt", context
				.getResources().getString(R.string.password_prompt));
	}

	public static void deleteLoginPrompt()
	{
		sharedPreferences.edit().remove("login_prompt").commit();
	}

	public static void deleteAllData()
	{
		sharedPreferences.edit().clear().commit();
	}

	/**
	 * 设置登录提示语
	 * 
	 * @param context
	 *            上下文对象
	 * @param promptStr
	 *            登录提示语
	 */
	public static void setLoginPrompt(String promptStr)
	{
		sharedPreferences.edit().putString("login_prompt", promptStr).commit();
	}

	/**
	 * 是否使用登录密码备份文件
	 * 
	 * @param context
	 *            上下文对象
	 * @return 是否使用登录密码备份文件
	 */
	public static boolean isUseLoginPasswordToBackup()
	{
		return sharedPreferences.getBoolean("use_login_password_backup", false);
	}

	/**
	 * 设置是否使用登录密码备份文件
	 * 
	 * @param context
	 *            上下文对象
	 * @param useLoginPassword
	 *            是否使用登录密码备份文件
	 */
	public static void setUseLoginPasswordToBackup(boolean useLoginPassword)
	{
		sharedPreferences.edit()
				.putBoolean("use_login_password_backup", useLoginPassword)
				.commit();
	}

	/**
	 * 账号详情密码是否全显示
	 * 
	 * @param context
	 *            上下文对象
	 * @return 账号详情密码是否全显示
	 */
	public static boolean isNotDisplayAllPassword()
	{
		return sharedPreferences.getBoolean("not_display_all_password", true);
	}

	/**
	 * 设置账号详情密码是否全显示
	 * 
	 * @param context
	 *            上下文对象
	 * @param notDisplayPassword
	 *            账号详情密码是否全显示
	 */
	public static void setNotDisplayAllPassword(boolean notDisplayPassword)
	{
		sharedPreferences.edit()
				.putBoolean("not_display_all_password", notDisplayPassword)
				.commit();
	}

	/**
	 * 得到备份使用的存储路径
	 * 
	 * @param context
	 *            上下文对象
	 * @return 存储路径
	 */
	public static String getBackupSavePath()
	{
		return sharedPreferences.getString("backup_path",
				Environment.getExternalStorageDirectory() + "/PasswordManager");
	}

	/**
	 * 设置备份使用的存储路径
	 * 
	 * @param context
	 *            上下文对象
	 * @param backupSavePath
	 *            存储路径
	 */
	public static void setBackupSavePath(String backupSavePath)
	{
		sharedPreferences.edit().putString("backup_path", backupSavePath)
				.commit();
	}

	/**
	 * 登录失败次数
	 * 
	 * @return
	 */
	public static int getLoginFailedTimes()
	{
		return sharedPreferences.getInt("login_failed_times", 0);
	}

	public static void setLoginFailedTimes(int failedTimes)
	{
		sharedPreferences.edit().putInt("login_failed_times", failedTimes)
				.commit();
	}

	/**
	 * 登录失败3次后，在此时间后才可以再次尝试登录
	 * 
	 * @return
	 */
	public static long getLoginLimitTime()
	{
		return sharedPreferences.getLong("login_limit_time", 0);
	}

	public static void setLoginLimitTime(long limitTime)
	{
		sharedPreferences.edit().putLong("login_limit_time", limitTime)
				.commit();
	}

	public static void setDisplayHiddenFile(boolean displayHiddenFile)
	{
		sharedPreferences.edit()
				.putBoolean("display_hidden_file", displayHiddenFile).commit();
	}

	public static boolean getDisplayHiddenFile()
	{
		return sharedPreferences.getBoolean("display_hidden_file", false);
	}

}
