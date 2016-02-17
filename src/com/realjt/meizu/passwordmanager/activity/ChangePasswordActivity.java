package com.realjt.meizu.passwordmanager.activity;

import java.util.List;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;

import com.realjt.meizu.passwordmanager.R;
import com.realjt.meizu.passwordmanager.common.Constants;
import com.realjt.meizu.passwordmanager.db.PasswordDatabaseHelper;
import com.realjt.meizu.passwordmanager.model.Password;
import com.realjt.meizu.passwordmanager.task.LoginTask;
import com.realjt.meizu.passwordmanager.task.LoginTask.LoginListener;
import com.realjt.meizu.passwordmanager.utils.SettingsUtils;
import com.realjt.meizu.passwordmanager.view.ProgressDialog;

/**
 * 修改密码Activity
 * 
 * @author Administrator
 * 
 */
public class ChangePasswordActivity extends BaseActivity implements
		OnClickListener
{
	private Button helpButton, completeButton;

	/**
	 * 原密码，新密码，确认密码输入框
	 */
	private EditText originalEditText, newEditText, confirmEditText;

	/**
	 * 处理进度对话框
	 */
	private ProgressDialog progressDialog;

	/**
	 * 修改密码弹出提示
	 */
	private PopupWindow popupWindow;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_change_password);

		helpButton = (Button) findViewById(R.id.bt_change_passowrd_help);
		completeButton = (Button) findViewById(R.id.bt_change_password_complete);

		originalEditText = (EditText) findViewById(R.id.et_change_password_original);
		newEditText = (EditText) findViewById(R.id.et_change_password_new);
		confirmEditText = (EditText) findViewById(R.id.et_change_password_confirm);

		initView();
	}

	/**
	 * 初始化UI界面
	 */
	private void initView()
	{
		helpButton.setOnClickListener(this);
		completeButton.setOnClickListener(this);

		// 输入框输入限制，字符和数字
		originalEditText.addTextChangedListener(new InputTextWatcher(
				originalEditText));
		newEditText.addTextChangedListener(new InputTextWatcher(newEditText));
		confirmEditText.addTextChangedListener(new InputTextWatcher(
				confirmEditText));
	}

	@Override
	public void onClick(View view)
	{
		switch (view.getId())
		{
		case R.id.bt_change_passowrd_help:
			showTips();
			break;
		case R.id.bt_change_password_complete:
			checkInputPassword();
			break;
		default:
			break;
		}
	}

	private void showTips()
	{
		if (null != popupWindow && popupWindow.isShowing())
		{
			popupWindow.dismiss();
		} else
		{
			if (null == popupWindow)
			{
				popupWindow = new PopupWindow(this);

				View popupView = getLayoutInflater().inflate(
						R.layout.popup_password_tips, new RelativeLayout(this));

				popupWindow = new PopupWindow(popupView,
						LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT,
						true);

				popupWindow.setBackgroundDrawable(new ColorDrawable(
						Color.TRANSPARENT));
				popupWindow.setOutsideTouchable(true);
			}

			popupWindow.showAsDropDown(helpButton, 0, 2);
		}
	}

	/**
	 * 检查修改密码
	 */
	private void checkInputPassword()
	{
		if (originalEditText.getText().length() >= 6
				&& originalEditText.getText().length() <= 16)
		{
			if (newEditText.getText().length() >= 6
					&& newEditText.getText().length() <= 16
					&& confirmEditText.getText().length() >= 6
					&& confirmEditText.getText().length() <= 16)
			{
				if (newEditText.getText().toString()
						.equals(confirmEditText.getText().toString()))
				{
					if (!originalEditText.getText().toString()
							.equals(newEditText.getText().toString()))
					{
						changeLoginPassword();
					} else
					{
						toastMakeText("新密码不能和旧密码一致");
					}
				} else
				{
					toastMakeText("新密码输入不一致");
				}
			} else
			{
				toastMakeText("请输入6-16位新密码");
			}
		} else
		{
			toastMakeText("原密码输入错误");
		}
	}

	/**
	 * 修改密码
	 */
	private void changeLoginPassword()
	{
		progressDialog = new ProgressDialog(this);
		progressDialog.setMessage("修改密码...");
		progressDialog.show();

		LoginTask loginTask = new LoginTask();
		loginTask.setLoginListener(new LoginListener()
		{
			@Override
			public void onSuccess()
			{
				// 原密码验证成功
				new ChangeLoginPasswordTask().execute("");
			}

			@Override
			public void onFaiure()
			{
				progressDialog.dismiss();

				toastMakeText("原密码输入错误");
			}

		});

		loginTask.execute(originalEditText.getText().toString());
	}

	class ChangeLoginPasswordTask extends AsyncTask<String, Void, Void>
	{
		@Override
		protected Void doInBackground(String... params)
		{
			// 先取出数据库所有数据
			PasswordDatabaseHelper databaseHelper = PasswordDatabaseHelper
					.getInstance(ChangePasswordActivity.this);
			List<Password> passwords = databaseHelper.queryAllPassword();
			// 删除数据库所有数据
			databaseHelper.deleteAllPassword();
			// 更换登录密码即加密密钥
			Constants.LOGIN_PASSWORD = newEditText.getText().toString();
			// 保存登录密码
			SettingsUtils.setLoginPassword(Constants.LOGIN_PASSWORD);
			// 重新写入数据库
			for (Password password : passwords)
			{
				databaseHelper.insertPassword(password);
			}

			return null;
		}

		@Override
		protected void onPostExecute(Void result)
		{
			super.onPostExecute(result);

			progressDialog.dismiss();

			originalEditText.setText("");
			newEditText.setText("");
			confirmEditText.setText("");

			onFinish();
		}

	}

	private void onFinish()
	{
		super.onBackPressed();
		overridePendingTransition(R.anim.push_left_in, R.anim.push_right_out);
	}

	/**
	 * 内部类，监听输入框，限制只输入字母数字
	 */
	private class InputTextWatcher implements TextWatcher
	{
		private EditText editText;

		private CharSequence charSequence = "";

		public InputTextWatcher(EditText editText)
		{
			this.editText = editText;
		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count)
		{
			charSequence = s;
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after)
		{

		}

		@Override
		public void afterTextChanged(Editable editable)
		{
			if (!Constants.LOGIN_PASSWORD_PATTERN.matcher(charSequence)
					.matches())
			{
				editable.delete(editText.getSelectionStart() - 1,
						editText.getSelectionEnd());
				editText.setText(editable);
				editText.setSelection(editable.length());
			}
		}

	}

	@Override
	public void onBackPressed()
	{
		super.onBackPressed();

		overridePendingTransition(R.anim.push_left_in, R.anim.push_right_out);
	}

}