package com.realjt.meizu.passwordmanager.task;

import android.os.AsyncTask;

import com.realjt.meizu.passwordmanager.utils.EncryptUtils;
import com.realjt.meizu.passwordmanager.utils.SettingsUtils;

/**
 * 登录任务
 * 
 * @author Administrator
 */
public class LoginTask extends AsyncTask<String, Integer, Integer>
{
	/**
	 * 登录事件回调接口
	 */
	public interface LoginListener
	{
		/**
		 * 登陆成功
		 */
		void onSuccess();

		/**
		 * 登陆失败
		 */
		void onFaiure();

	}

	private LoginListener loginListener;

	public LoginTask()
	{

	}

	public void setLoginListener(LoginListener loginListener)
	{
		this.loginListener = loginListener;
	}

	@Override
	protected Integer doInBackground(String... params)
	{
		String temp = SettingsUtils.getLoginPassword();// 加密后的数据
		if (null != temp && !"".equals(temp))
		{
			String inputPassword = EncryptUtils.encryptSHA256(params[0]);
			if (temp.equals(inputPassword))
			{
				return 0;
			}
		}

		return 1;
	}

	@Override
	protected void onPostExecute(Integer result)
	{
		super.onPostExecute(result);
		if (null != loginListener)
		{
			if (0 == result)
			{
				loginListener.onSuccess();
			} else if (1 == result)
			{
				loginListener.onFaiure();
			}
		}

	}
}