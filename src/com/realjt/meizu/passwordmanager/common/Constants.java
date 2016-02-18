package com.realjt.meizu.passwordmanager.common;

import java.util.regex.Pattern;

import android.content.Context;

public class Constants
{
	public static Context APP_CONTEXT;

	/**
	 * 软件版本,初始为未认证 0 未认证 1试用版 2正式版 3认证失败
	 */
	public static int EDITION = 0;

	/**
	 * 从魅族开发者中心里获取到的属于该第三方应用的公钥
	 */
	public static final String APK_PUBLIC_KEY = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCQVNwBTX4mEh85UNrubyOOE0nd1/MhcKgE3AONw/6WRi37m0vVCSkuou/msbC3gL8GSi29UmV4UU3GZvRgX5fA0TXJNK7x4uXPepAusijrb7wRqzVtPIHmCftir7lPMRkUVdDSFz7aRR5YPK5KmZltIcIS5ITT8HpErbAMYGnOtQIDAQAB";

	/**
	 * 通过该路径，可以打开魅族应用商店中此应用页面
	 */
	public static final String APK_IN_STORE_ADDRESS = "mstore:http://app.meizu.com/phone/apps/e83943a999284a69b3961477f966f9e6";

	/**
	 * 应用支持试用天数
	 */
	public static final int EXPIRE_DAYS = 2;

	/**
	 * 登录后保存于内存的登录密码
	 */
	public static String LOGIN_PASSWORD = "";

	/**
	 * 备份文件后缀名
	 */
	public static final String FILE_SUFFIX = ".pmax";

	/**
	 * MD5签名信息
	 */
	public static final String SIGNATURE_MD5 = "0B:15:73:6A:3C:C9:37:56:E4:FB:D1:97:FD:2A:AC:2D";

	/**
	 * 登录密码正则匹配
	 */
	public static final String LOGIN_PASSWORD_REGULAR = "[a-z0-9~!@#$%^&:.,;/'<>-_=]*";

	/**
	 * 密码正则表达式预编译
	 */
	public static final Pattern LOGIN_PASSWORD_PATTERN = Pattern.compile(
			LOGIN_PASSWORD_REGULAR, Pattern.CASE_INSENSITIVE);

	/**
	 * 登录3次失败后则进行登录限制
	 */
	public static final int LOGIN_ALLOW_FAIL_TIMES = 3;

	public interface ReturnCode
	{
		public static final int SUCCESS = 0;

		public static final String COMPLETE = "COMPLETE";

		public static final String CANCEL = "CANCEL";

	}

}
