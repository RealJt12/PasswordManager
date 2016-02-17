package com.realjt.meizu.passwordmanager.task;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.json.JSONObject;

import android.content.Context;
import android.os.AsyncTask;
import android.util.JsonReader;

import com.realjt.meizu.passwordmanager.common.Constants.ReturnCode;
import com.realjt.meizu.passwordmanager.db.PasswordDatabaseHelper;
import com.realjt.meizu.passwordmanager.model.Password;
import com.realjt.meizu.passwordmanager.utils.EncryptUtils;

/**
 * 数据恢复任务
 * 
 * @author Administrator
 * 
 */
public class RecoveryDataTask extends AsyncTask<Void, Void, Integer>
{
	/**
	 * 数据库服务
	 */
	private PasswordDatabaseHelper databaseHelper;

	/**
	 * json输入流
	 */
	private JsonReader jsonReader;

	/**
	 * 恢复使用的解密密码
	 */
	private String password;

	/**
	 * 恢复回调接口
	 */
	private RecoveryDataTaskListener recoveryDataTaskListener;

	public interface RecoveryDataTaskListener
	{
		void onRecoveryComplete(boolean success, String message);
	}

	/**
	 * 构造函数
	 * 
	 * @param context
	 *            上下文对象
	 * @param jsonReader
	 *            json输入流
	 * @param password
	 *            恢复使用的解密密码
	 * @param recoveryDataTaskListener
	 *            恢复回调接口
	 */
	public RecoveryDataTask(Context context, JsonReader jsonReader,
			String password)
	{
		this.databaseHelper = PasswordDatabaseHelper.getInstance(context);
		this.jsonReader = jsonReader;
		this.password = password;
	}

	@Override
	protected Integer doInBackground(Void... params)
	{
		try
		{
			jsonReader.beginArray();

			while (jsonReader.hasNext())
			{
				Password password = new Password();
				JSONObject jsonObject = new JSONObject(EncryptUtils.decrypt(
						jsonReader.nextString(), this.password));
				password.setName(EncryptUtils.decrypt(jsonObject.get("name")
						.toString(), this.password));
				password.setAccount(EncryptUtils.decrypt(
						jsonObject.get("account").toString(), this.password));
				password.setPassword(EncryptUtils.decrypt(
						jsonObject.get("password").toString(), this.password));
				password.setRemarks(EncryptUtils.decrypt(
						jsonObject.get("remarks").toString(), this.password));
				password.setCreateTime(EncryptUtils.decrypt(
						jsonObject.get("createtime").toString(), this.password));
				password.setReviseTime(EncryptUtils.decrypt(
						jsonObject.get("revisetime").toString(), this.password));
				password.setClassification(EncryptUtils.decrypt(
						jsonObject.get("classification").toString(),
						this.password));

				databaseHelper.recoveryPassword(password);
			}

			jsonReader.endArray();
			jsonReader.endObject();
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
				jsonReader.close();
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
		if (recoveryDataTaskListener != null)
		{
			if (ReturnCode.SUCCESS == result)
			{
				recoveryDataTaskListener.onRecoveryComplete(true, "恢复成功");
			} else if (1 == result)
			{
				recoveryDataTaskListener.onRecoveryComplete(false, "恢复失败");
			}
		}
	}

	public void setRecoveryDataTaskListener(
			RecoveryDataTaskListener recoveryDataTaskListener)
	{
		this.recoveryDataTaskListener = recoveryDataTaskListener;
	}

}
