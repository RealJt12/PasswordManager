package com.realjt.meizu.passwordmanager.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import com.realjt.meizu.passwordmanager.R;
import com.realjt.meizu.passwordmanager.common.Constants;

/**
 * 时间处理工具
 * 
 * @author Administrator
 * 
 */
public class DateUtils
{
	private static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat(
			"yyyyMMddHHmmss", Locale.getDefault());

	private static final SimpleDateFormat SIMPLE_DATE_FORMAT_DAY = new SimpleDateFormat(
			"yyyy.MM.dd", Locale.getDefault());

	private static final SimpleDateFormat SIMPLE_DATE_FORMAT_HOUR = new SimpleDateFormat(
			"yyyy.MM.dd.HH", Locale.getDefault());

	private static final SimpleDateFormat SIMPLE_DATE_FORMAT_MINUTE = new SimpleDateFormat(
			"yyyy年MM月dd日HH时mm分", Locale.getDefault());

	private static final SimpleDateFormat SIMPLE_FORMAT_MINUTE = new SimpleDateFormat(
			"HH时mm分ss秒", Locale.getDefault());

	public static final long TEN_MINUTES = 600000;

	public static final long TWO_MINUTES = 120000;

	public static final long A_DAY = 86400000;

	/**
	 * 把字符串转换成Date
	 * 
	 * @param timeStr
	 *            格式为:20131210042034
	 * @return Date对象
	 */
	public static synchronized Date stringToDate(String dateStr)
	{
		if (null == dateStr || "".equals(dateStr))
		{
			return null;
		}

		try
		{
			Date date = SIMPLE_DATE_FORMAT.parse(dateStr);

			return date;
		} catch (ParseException e)
		{
		}

		return null;
	}

	/**
	 * 把Date转换成字符串
	 * 
	 * @param date
	 *            Date对象
	 * @return 格式为:20131210042034
	 */
	public static synchronized String dateToString(Date date)
	{
		return SIMPLE_DATE_FORMAT.format(date);
	}

	public static synchronized String dateToDay(Date date)
	{
		Date dateNow = new Date();

		long timeDifference = dateNow.getTime() - date.getTime();

		if (TEN_MINUTES >= timeDifference)
		{
			return Constants.APP_CONTEXT.getString(R.string.just);
		}

		String dateStr = SIMPLE_DATE_FORMAT_DAY.format(date);

		String today = SIMPLE_DATE_FORMAT_DAY.format(dateNow);

		if (today.equals(dateStr))
		{
			return Constants.APP_CONTEXT.getString(R.string.today);
		}

		String yesterday = SIMPLE_DATE_FORMAT_DAY.format(new Date(dateNow
				.getTime() - A_DAY));

		if (yesterday.equals(dateStr))
		{
			return Constants.APP_CONTEXT.getString(R.string.yesterday);
		}

		return dateStr;
	}

	public static synchronized String dateToHour(Date date)
	{
		return SIMPLE_DATE_FORMAT_HOUR.format(date);
	}

	public static synchronized String dateToMinute(Date date)
	{
		return SIMPLE_DATE_FORMAT_MINUTE.format(date);
	}

	public static synchronized String dateToLoginLimitTime(Date date)
	{
		return SIMPLE_FORMAT_MINUTE.format(date);
	}

}
