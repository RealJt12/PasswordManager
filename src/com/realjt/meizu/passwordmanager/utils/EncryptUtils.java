package com.realjt.meizu.passwordmanager.utils;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Locale;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import android.annotation.SuppressLint;

/**
 * 加解密工具类
 * 
 * @author Administrator
 * 
 */
public class EncryptUtils
{
	/**
	 * SHA-256加密
	 * 
	 * @param text
	 *            待加密字符串
	 * @return 加密后字符串
	 */
	public static String encryptSHA256(String text)
	{
		MessageDigest messageDigest;
		try
		{
			messageDigest = MessageDigest.getInstance("SHA-256");
			messageDigest.update(text.getBytes("UTF-8"));
			byte[] result = messageDigest.digest();

			return byteToHex(result);
		} catch (NoSuchAlgorithmException e)
		{
			return null;
		} catch (UnsupportedEncodingException e)
		{
			return null;
		}
	}

	/**
	 * 加密
	 * 
	 * @param content
	 *            需要加密的内容
	 * @param password
	 *            加密密码
	 * @return
	 */
	/*
	 * public static String encryptAES(String content, String password) { try {
	 * KeyGenerator kgen = KeyGenerator.getInstance("AES"); kgen.init(128, new
	 * SecureRandom(password.getBytes())); SecretKey secretKey =
	 * kgen.generateKey(); byte[] enCodeFormat = secretKey.getEncoded();
	 * SecretKeySpec key = new SecretKeySpec(enCodeFormat, "AES"); Cipher cipher
	 * = Cipher.getInstance("AES");// 创建密码器 byte[] byteContent =
	 * content.getBytes("UTF-8"); cipher.init(Cipher.ENCRYPT_MODE, key);// 初始化
	 * byte[] result = cipher.doFinal(byteContent);
	 * 
	 * return byteToHex(result); } catch (NoSuchAlgorithmException e) {
	 * e.printStackTrace(); } catch (NoSuchPaddingException e) {
	 * e.printStackTrace(); } catch (InvalidKeyException e) {
	 * e.printStackTrace(); } catch (UnsupportedEncodingException e) {
	 * e.printStackTrace(); } catch (IllegalBlockSizeException e) {
	 * e.printStackTrace(); } catch (BadPaddingException e) {
	 * e.printStackTrace(); } return null; }
	 */

	/**
	 * 解密
	 * 
	 * @param content
	 *            待解密内容
	 * @param password
	 *            解密密钥
	 * @return
	 */
	/*
	 * public static String decryptAES(String content, String password) { try {
	 * KeyGenerator kgen = KeyGenerator.getInstance("AES");
	 * 
	 * kgen.init(128, new SecureRandom(password.getBytes())); SecretKey
	 * secretKey = kgen.generateKey(); byte[] enCodeFormat =
	 * secretKey.getEncoded(); SecretKeySpec key = new
	 * SecretKeySpec(enCodeFormat, "AES"); Cipher cipher =
	 * Cipher.getInstance("AES");// 创建密码器 cipher.init(Cipher.DECRYPT_MODE,
	 * key);// 初始化 byte[] result = cipher.doFinal(hexToByte(content));
	 * 
	 * return new String(result); } catch (NoSuchAlgorithmException e) {
	 * e.printStackTrace(); System.out.println(1); } catch
	 * (NoSuchPaddingException e) { e.printStackTrace(); System.out.println(2);
	 * } catch (InvalidKeyException e) { e.printStackTrace();
	 * System.out.println(3); } catch (IllegalBlockSizeException e) {
	 * e.printStackTrace(); System.out.println(4); } catch (BadPaddingException
	 * e) { e.printStackTrace(); System.out.println(5); } return null; }
	 */

	/**
	 * 将二进制转换成16进制
	 */
	public static String byteToHex(byte buf[])
	{
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < buf.length; i++)
		{
			String hex = Integer.toHexString(buf[i] & 0xFF);
			if (hex.length() == 1)
			{
				hex = '0' + hex;
			}
			sb.append(hex.toUpperCase(Locale.US));
		}

		return sb.toString();
	}

	/**
	 * 将16进制转换为二进制
	 */
	public static byte[] hexToByte(String hexStr)
	{
		if (hexStr.length() < 1)
		{
			return null;
		}

		byte[] result = new byte[hexStr.length() / 2];
		for (int i = 0; i < hexStr.length() / 2; i++)
		{
			int high = Integer.parseInt(hexStr.substring(i * 2, i * 2 + 1), 16);
			int low = Integer.parseInt(hexStr.substring(i * 2 + 1, i * 2 + 2),
					16);
			result[i] = (byte) (high * 16 + low);
		}

		return result;
	}

	/**
	 * AES加密
	 * 
	 * @param content
	 *            需要加密的内容
	 * @param password
	 *            加密密码
	 * @return 加密后的内容
	 */
	public static String encrypt(String content, String password)
	{
		try
		{
			byte[] rawKey = getRawKey(password.getBytes("UTF-8"));
			byte[] result = encrypt(rawKey, content.getBytes("UTF-8"));

			return byteToHex(result);
		} catch (Exception e)
		{

		}

		return null;
	}

	/**
	 * AES解密
	 * 
	 * @param content
	 *            待解密内容
	 * @param password
	 *            解密密钥
	 * @return 解密后内容
	 */
	public static String decrypt(String content, String password)
	{
		try
		{
			byte[] rawKey = getRawKey(password.getBytes("UTF-8"));
			byte[] enc = hexToByte(content);
			byte[] result = decrypt(rawKey, enc);

			return new String(result);
		} catch (Exception e)
		{

		}

		return null;
	}

	@SuppressLint("TrulyRandom")
	private static byte[] getRawKey(byte[] seed) throws Exception
	{
		KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
		// SHA1PRNG 强随机种子算法, 要区别4.2以上版本的调用方法
		SecureRandom secureRandom = null;
		if (android.os.Build.VERSION.SDK_INT >= 17)
		{
			secureRandom = SecureRandom.getInstance("SHA1PRNG", "Crypto");
		} else
		{
			secureRandom = SecureRandom.getInstance("SHA1PRNG");
		}

		secureRandom.setSeed(seed);
		keyGenerator.init(256, secureRandom); // 256 bits or 128 bits,192bits
		SecretKey secretKey = keyGenerator.generateKey();
		byte[] raw = secretKey.getEncoded();

		return raw;
	}

	private static byte[] encrypt(byte[] key, byte[] src) throws Exception
	{
		SecretKeySpec secretKeySpec = new SecretKeySpec(key, "AES");
		Cipher cipher = Cipher.getInstance("AES");
		// AES/CBC/PKCS5Padding
		cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
		byte[] encrypted = cipher.doFinal(src);

		return encrypted;
	}

	private static byte[] decrypt(byte[] key, byte[] encrypted)
			throws Exception
	{
		SecretKeySpec secretKeySpec = new SecretKeySpec(key, "AES");
		Cipher cipher = Cipher.getInstance("AES");
		cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
		byte[] decrypted = cipher.doFinal(encrypted);

		return decrypted;
	}

	/**
	 * 密码做匿名化处理
	 * 
	 * @param text
	 * @return
	 */
	public static String notAllDisplay(String text)
	{
		if (null != text && text.length() > 0)
		{
			if (text.length() < 3)
			{
				return text;
			} else if (text.length() == 3)
			{
				return "☆☆" + text.substring(2, 3);
			} else if (text.length() == 4)
			{
				return "☆☆" + text.substring(2, 4);
			} else if (text.length() == 5)
			{
				return "☆☆☆" + text.substring(3, 5);
			} else
			{
				StringBuilder temp = new StringBuilder();
				for (int i = 0; i < text.length() - 3; i++)
				{
					temp.append("☆");
				}

				return new StringBuilder(text.substring(0, 1))
						.append(temp)
						.append(text.substring(text.length() - 2, text.length()))
						.toString();
			}
		}

		return "";
	}

}
