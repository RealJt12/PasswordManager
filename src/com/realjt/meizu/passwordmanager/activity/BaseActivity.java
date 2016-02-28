package com.realjt.meizu.passwordmanager.activity;

import java.util.Locale;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.meizu.flyme.reflect.StatusBarProxy;
import com.realjt.meizu.passwordmanager.common.Constants;
import com.realjt.meizu.passwordmanager.utils.LogUtils;
import com.realjt.meizu.passwordmanager.utils.SettingsUtils;

/**
 * 基础Activity类，所有Activity都应该继承该类
 * 
 * @author Administrator
 * 
 */
public class BaseActivity extends Activity
{
	/**
	 * 支持沉浸式状态栏
	 */
	public static final int FLAG_TRANSLUCENT_STATUS = 0x04000000;

	/**
	 * 支持沉浸式状态栏
	 */
	public static final int FLAG_TRANSLUCENT_NAVIGATION = 0x08000000;

	/**
	 * 按Home键监听退出
	 */
	private BroadcastReceiver broadcastReceiver = new BroadcastReceiver()
	{
		@Override
		public void onReceive(Context context, Intent intent)
		{
			if (Intent.ACTION_CLOSE_SYSTEM_DIALOGS.equals(intent.getAction()))
			{
				String reason = intent.getStringExtra("reason");
				if (null != reason && !"search".equals(reason))
				{
					if (SettingsUtils.isExitPressHomeKey())
					{
						// onDestroy中进行unregisterReceiver
						Constants.LOGIN_PASSWORD = null;

						BaseActivity.this.finish();
					}
				}
			}
		}

	};

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		// 注册监听广播
		registerReceiver(broadcastReceiver, new IntentFilter(
				Intent.ACTION_CLOSE_SYSTEM_DIALOGS));

		LogUtils.debug(this.getClass().getSimpleName().toLowerCase(Locale.US)
				+ " register broadcast");

		// 沉浸式状态栏
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
		{
			Window window = getWindow();
			window.addFlags(FLAG_TRANSLUCENT_STATUS);
			window.addFlags(FLAG_TRANSLUCENT_NAVIGATION);

			// Flyme系统设置状态栏图标颜色，true为黑色
			// if (CommonUtils.isFlyme())
			// {
			StatusBarProxy.setStatusBarDarkIcon(getWindow(), true);
			// }
		}

		getActionBar().setDisplayShowHomeEnabled(false);
	}

	// public BroadcastReceiver getBroadcastReceiver()
	// {
	// return broadcastReceiver;
	// }

	@Override
	protected void onDestroy()
	{
		// 取消注册广播
		unregisterReceiver(broadcastReceiver);

		LogUtils.debug(this.getClass().getSimpleName().toLowerCase(Locale.US)
				+ " unregister broadcast");

		super.onDestroy();
	}

	// @Override
	// protected void onPause()
	// {
	// super.onPause();

	// 检查当前页面Activity是否是应用Activity
	// if (!isTopActivity())
	// {
	// Intent intent = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
	// intent.putExtra("reason", "switch");
	//
	// sendBroadcast(intent);
	// }
	// }

	/**
	 * 判断指定activity是否在任务栈的最前端 需要权限：android.permission.GET_TASKS
	 * 
	 * @param context
	 * @param activityName
	 * @return
	 */
	// private boolean isTopActivity()
	// {
	// String className = getOldTopPackageName();

	// if (Build.VERSION.SDK_INT >= 21)
	// {
	// className = getNewTopPackageName();
	// } else
	// {
	// className = getOldTopPackageName();
	// }

	// if (null != className && className.contains(this.getPackageName()))
	// {
	// return true;
	// }
	//
	// return false;
	// }

	// private String getNewTopPackageName()
	// {
	// try
	// {
	// Field field = ActivityManager.RunningAppProcessInfo.class
	// .getDeclaredField("processState");
	//
	// List<ActivityManager.RunningAppProcessInfo> runningAppProcessInfos =
	// ((ActivityManager) getSystemService(Context.ACTIVITY_SERVICE))
	// .getRunningAppProcesses();
	// for (ActivityManager.RunningAppProcessInfo runningAppProcessInfo :
	// runningAppProcessInfos)
	// {
	// if (runningAppProcessInfo.importance <=
	// ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND
	// && runningAppProcessInfo.importanceReasonCode == 0)
	// {
	// int state = field.getInt(runningAppProcessInfo);
	// if (2 == state)
	// {
	// String[] packageName = runningAppProcessInfo.pkgList;
	//
	// return packageName[0];
	// }
	// }
	// }
	// } catch (Exception e)
	// {
	// throw new RuntimeException(e);
	// }

	// return null;
	// }

	// private String getOldTopPackageName()
	// {
	// ActivityManager activityManager = (ActivityManager) this
	// .getSystemService(Context.ACTIVITY_SERVICE);
	//
	// List<RunningTaskInfo> runningTaskInfos = activityManager
	// .getRunningTasks(1);
	//
	// String className = runningTaskInfos.get(0).topActivity.getClassName();
	//
	// return className;
	// }

	// @Override
	// protected void onStop()
	// {
	//
	// // 检查当前页面Activity是否是应用Activity
	//
	// if (!isAppOnForeground())
	// {
	// Intent intent = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
	// // Intent intent = new Intent("PASS");
	// intent.putExtra("reason", "switch");
	//
	// sendBroadcast(intent);
	// }
	//
	// super.onStop();
	// }

	/**
	 * 判断当前应用是否在前台,有延时
	 * 
	 * @return
	 */
	// public boolean isAppOnForeground()
	// {
	// ActivityManager activityManager = (ActivityManager)
	// getApplicationContext()
	// .getSystemService(Context.ACTIVITY_SERVICE);
	// String packageName = getApplicationContext().getPackageName();
	//
	// List<RunningAppProcessInfo> appProcesses = activityManager
	// .getRunningAppProcesses();
	// if (null == appProcesses)
	// {
	// return false;
	// }
	//
	// for (RunningAppProcessInfo appProcess : appProcesses)
	// {
	// if (appProcess.processName.equals(packageName)
	// && appProcess.importance == RunningAppProcessInfo.IMPORTANCE_FOREGROUND)
	// {
	// return true;
	// }
	// }
	//
	// return false;
	// }

	@Override
	public boolean dispatchTouchEvent(MotionEvent event)
	{
		// 一个点
		if (1 == event.getPointerCount())
		{
			return super.dispatchTouchEvent(event);
		}

		return true;
	}

	/**
	 * 判断是否有smartbar
	 * 
	 * @return
	 */
	// public boolean hasSmartBar()
	// {
	// try
	// {
	// // 可用反射调用Build.hasSmartBar()
	// Method method = Class.forName("android.os.Build").getMethod(
	// "hasSmartBar");
	//
	// return ((Boolean) method.invoke(null)).booleanValue();
	// } catch (Exception e)
	// {
	// e.printStackTrace();
	// }
	//
	// return false;
	// }

	/**
	 * 隐藏顶部actionbar
	 * 
	 * @param visibility
	 */
	// public void setActionBarVisibility(boolean visibility)
	// {
	// try
	// {
	// Method method = Class.forName("android.app.ActionBar").getMethod(
	// "setActionBarViewCollapsable",
	// new Class[] { boolean.class });
	// method.invoke(getActionBar(), visibility);
	// } catch (SecurityException e)
	// {
	// e.printStackTrace();
	// } catch (NoSuchMethodException e)
	// {
	// e.printStackTrace();
	// } catch (ClassNotFoundException e)
	// {
	// e.printStackTrace();
	// } catch (IllegalAccessException e)
	// {
	// e.printStackTrace();
	// } catch (IllegalArgumentException e)
	// {
	// e.printStackTrace();
	// } catch (InvocationTargetException e)
	// {
	// e.printStackTrace();
	// }
	// }

	/*
	 * 隐藏底部SmartBar
	 */
	// public void setSmartBarVisibility(boolean visibility)
	// {
	// if (visibility)
	// {
	// requestWindowFeature(Window.FEATURE_ACTION_BAR);
	// } else
	// {
	// requestWindowFeature(Window.FEATURE_NO_TITLE);
	// }
	// }

	// private void setDarkStatusBarIcon(Window window, boolean isDarkIcon)
	// {
	// WindowManager.LayoutParams localLayoutParams = window.getAttributes();
	//
	// int i = getMeizuFlagDarkStatusBarIcon();
	// if (-1 == i)
	// {
	// return;
	// }
	//
	// int j = getMeizuFlag(localLayoutParams);
	//
	// int k = 0;
	// if (isDarkIcon)
	// {
	// k = j | i;
	// }
	//
	// setMeizuFlag(localLayoutParams, k);
	// window.setAttributes(localLayoutParams);
	// }

	// private int getMeizuFlag(WindowManager.LayoutParams layoutParams)
	// {
	// return getMeizuFlag(layoutParams, "meizuFlags", 1);
	// }
	//
	// private int getMeizuFlagDarkStatusBarIcon()
	// {
	// return getMeizuFlag("MEIZU_FLAG_DARK_STATUS_BAR_ICON", -1);
	// }

	// private int getMeizuFlagInterceptHomeKey()
	// {
	// return getMeizuFlag("MEIZU_FLAG_INTERCEPT_HOME_KEY", 32);
	// }
	//
	// private int getMeizuFlagNavigationBar()
	// {
	// return getMeizuFlag("MEIZU_FLAG_NAVIGATIONBAR", 1);
	// }

	// private int getMeizuFlag(WindowManager.LayoutParams layoutParams,
	// String name, int defaultValue)
	// {
	// try
	// {
	// return WindowManager.LayoutParams.class.getField(name).getInt(
	// layoutParams);
	// } catch (NoSuchFieldException localNoSuchFieldException)
	// {
	// Log.e("AlarmClockReflectUtils",
	// "No such field WindowManager.LayoutParams." + name,
	// localNoSuchFieldException);
	// } catch (IllegalAccessException localIllegalAccessException)
	// {
	// Log.e("AlarmClockReflectUtils",
	// "Illegal access field WindowManager.LayoutParams." + name,
	// localIllegalAccessException);
	// }
	//
	// return defaultValue;
	// }

	// private int getMeizuFlag(String name, int defaultValue)
	// {
	// try
	// {
	// return WindowManager.LayoutParams.class.getField(name).getInt(null);
	// } catch (NoSuchFieldException e)
	// {
	// Log.e("AlarmClockReflectUtils",
	// "No such field WindowManager.LayoutParams." + name, e);
	// } catch (IllegalAccessException e)
	// {
	// Log.e("AlarmClockReflectUtils",
	// "Illegal access field WindowManager.LayoutParams." + name,
	// e);
	// }
	//
	// return defaultValue;
	// }

	// private void setMeizuFlag(WindowManager.LayoutParams layoutParams, int
	// value)
	// {
	// try
	// {
	// WindowManager.LayoutParams.class.getField("meizuFlags").setInt(
	// layoutParams, value);
	// } catch (NoSuchFieldException e)
	// {
	// Log.e("AlarmClockReflectUtils",
	// "No such field WindowManager.LayoutParams.meizuFlags", e);
	// } catch (IllegalAccessException e)
	// {
	// Log.e("AlarmClockReflectUtils",
	// "Illegal access field WindowManager.LayoutParams.meizuFlags",
	// e);
	// }
	// }

	public void toastMakeText(String text)
	{
		Toast toast = Toast.makeText(this, text, Toast.LENGTH_SHORT);
		toast.setGravity(Gravity.CENTER, 0, dpTopx(160));
		toast.show();
	}

	public void toastMakeText(int resId)
	{
		Toast toast = Toast.makeText(this, resId, Toast.LENGTH_SHORT);
		toast.setGravity(Gravity.CENTER, 0, dpTopx(160));
		toast.show();
	}

	public void toastMakeTextLong(String text)
	{
		Toast toast = Toast.makeText(this, text, Toast.LENGTH_LONG);
		toast.setGravity(Gravity.CENTER, 0, dpTopx(160));
		toast.show();
	}

	public void toastMakeTextLong(int resId)
	{
		Toast toast = Toast.makeText(this, resId, Toast.LENGTH_LONG);
		toast.setGravity(Gravity.CENTER, 0, dpTopx(160));
		toast.show();
	}

	/**
	 * 若隐藏actionbar,应复写该方法
	 * 
	 * @param color
	 */
	public void setActionBarTextColor(int color)
	{
		int titleId = Resources.getSystem().getIdentifier("action_bar_title",
				"id", "android");
		TextView titleTextView = (TextView) findViewById(titleId);

		if (null != titleTextView)
		{
			titleTextView.setTextColor(color);
		}
	}

	/**
	 * 若隐藏actionbar,应复写该方法
	 * 
	 * @param color
	 */
	public void setActionBarTextSize(float textSize)
	{
		int titleId = Resources.getSystem().getIdentifier("action_bar_title",
				"id", "android");
		TextView titleTextView = (TextView) findViewById(titleId);

		if (null != titleTextView)
		{
			titleTextView.setTextSize(textSize);
		}
	}

	/**
	 * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
	 */
	public int dpTopx(float dpValue)
	{
		final float scale = this.getResources().getDisplayMetrics().density;

		return (int) (dpValue * scale + 0.5f);
	}

	/**
	 * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
	 */
	public int pxTodp(float pxValue)
	{
		final float scale = this.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}

}
