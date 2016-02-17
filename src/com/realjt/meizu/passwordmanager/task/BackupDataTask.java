package com.realjt.meizu.passwordmanager.task;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.os.AsyncTask;

import com.realjt.meizu.passwordmanager.R;
import com.realjt.meizu.passwordmanager.common.Constants;
import com.realjt.meizu.passwordmanager.common.Constants.ReturnCode;
import com.realjt.meizu.passwordmanager.db.PasswordDatabaseHelper;
import com.realjt.meizu.passwordmanager.model.Password;
import com.realjt.meizu.passwordmanager.utils.DateUtils;
import com.realjt.meizu.passwordmanager.utils.EncryptUtils;
import com.realjt.meizu.passwordmanager.utils.FileUtils;
import com.realjt.meizu.passwordmanager.utils.SettingsUtils;

/**
 * 备份任务
 * 
 * @author Administrator
 * 
 */
public class BackupDataTask extends AsyncTask<Void, Void, Integer>
{
	/**
	 * 上下文对象
	 */
	private Context context;

	/**
	 * 备份密码
	 */
	private String password;

	/**
	 * 备份路径
	 */
	private String backupPath;

	/**
	 * 备份回调接口
	 * 
	 * @author Administrator
	 */
	public interface BackupDataTaskListener
	{
		void onBackupComplete(boolean success, String message);
	}

	/**
	 * 备份回调接口
	 */
	private BackupDataTaskListener backupDataTaskListener;

	/**
	 * 构造函数
	 * 
	 * @param context
	 *            上下文对象
	 * @param password
	 *            备份使用的加密密码
	 * @param backupDataTaskListener
	 *            回调接口
	 */
	public BackupDataTask(Context context, String password)
	{
		this.context = context;
		this.password = password;
	}

	@Override
	protected Integer doInBackground(Void... params)
	{
		String temp = EncryptUtils.encryptSHA256(password);

		Date date = new Date();
		String fileName = "Backup" + DateUtils.dateToString(date)
				+ Constants.FILE_SUFFIX;

		File folder = new File(SettingsUtils.getBackupSavePath());
		if (!folder.exists())
		{
			folder.mkdirs();
		}

		File file = new File(SettingsUtils.getBackupSavePath() + "/" + fileName);

		backupPath = file.getAbsolutePath();

		FileOutputStream fileOutputStream = null;
		try
		{
			file.createNewFile();
			fileOutputStream = new FileOutputStream(file);

			String outTemp = "{\"filepassword\":\"" + temp
					+ "\",\n\"passwords\":[";
			fileOutputStream.write(outTemp.getBytes("UTF-8"));

			List<Password> passwords = PasswordDatabaseHelper.getInstance(
					context).queryAllPassword();
			int length = passwords.size();
			for (int i = 0; i < length; i++)
			{
				outTemp = "\""
						+ EncryptUtils.encrypt(
								passwords.get(i).toEncryptJson(password),
								password) + "\"";
				if (i < length - 1)
				{
					outTemp += ",";
				}
				fileOutputStream.write(outTemp.getBytes("UTF-8"));
				fileOutputStream.write("\n".getBytes("UTF-8"));
			}

			fileOutputStream.write("]}".getBytes());
			fileOutputStream.flush();
		} catch (FileNotFoundException e)
		{
			return 1;
		} catch (IOException e)
		{
			return 1;
		} catch (Exception e)
		{
			return 1;
		} finally
		{
			try
			{
				if (null != fileOutputStream)
				{
					fileOutputStream.close();
				}
			} catch (IOException e)
			{
			}
		}

		return ReturnCode.SUCCESS;
	}

	@Override
	protected void onPostExecute(Integer result)
	{
		super.onPostExecute(result);
		if (null != backupDataTaskListener)
		{
			if (ReturnCode.SUCCESS == result)
			{
				backupDataTaskListener.onBackupComplete(
						true,
						"已备份到SD Card"
								+ backupPath.substring(FileUtils.getRootPath()
										.length(), backupPath.length()));
			} else if (1 == result)
			{
				backupDataTaskListener.onBackupComplete(false,
						context.getString(R.string.backup_failed));
			}
		}
	}

	public void setBackupDataTaskListener(
			BackupDataTaskListener backupDataTaskListener)
	{
		this.backupDataTaskListener = backupDataTaskListener;
	}

}
