package com.realjt.meizu.passwordmanager.common;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import com.realjt.meizu.passwordmanager.utils.LogUtils;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.Signature;
import android.os.Build;

public class CommonUtils
{
	/**
	 * 判断是否是Flyme系统
	 * 
	 * @return
	 */
	public static boolean isFlyme()
	{
		if ("flyme".equals(Build.USER))
		{
			return true;
		}

		return false;
	}

	public static boolean checkSignature(PackageManager packageManager,
			String packageName)
	{
		LogUtils.debug("start check sigature");

		PackageInfo packageInfo;
		try
		{
			packageInfo = packageManager.getPackageInfo(packageName,
					PackageManager.GET_SIGNATURES);
			Signature signatures = packageInfo.signatures[0];
			MessageDigest messageDigest = MessageDigest.getInstance("MD5");

			messageDigest.update(signatures.toByteArray());
			byte[] digest = messageDigest.digest();

			if (Constants.SIGNATURE_MD5.equals(toHexString(digest)))
			{
				LogUtils.debug("check sigature success");

				return true;
			}
		} catch (NameNotFoundException e)
		{
			LogUtils.error("check sigature failed", e);
		} catch (NoSuchAlgorithmException e)
		{
			LogUtils.error("check sigature failed", e);
		}

		return false;
	}

	private static String toHexString(byte[] block)
	{
		StringBuffer stringBuffer = new StringBuffer();
		int length = block.length;

		for (int i = 0; i < length; i++)
		{
			byteToHex(block[i], stringBuffer);
			if (i < length - 1)
			{
				stringBuffer.append(":");
			}
		}

		return stringBuffer.toString();
	}

	private static void byteToHex(byte b, StringBuffer stringBuffer)
	{
		char[] hexChars = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
				'A', 'B', 'C', 'D', 'E', 'F' };

		int high = ((b & 0xf0) >> 4);
		int low = (b & 0x0f);

		stringBuffer.append(hexChars[high]);
		stringBuffer.append(hexChars[low]);
	}

}
