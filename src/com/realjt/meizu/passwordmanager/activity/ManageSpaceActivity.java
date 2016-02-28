package com.realjt.meizu.passwordmanager.activity;

import java.util.Date;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.realjt.meizu.passwordmanager.R;
import com.realjt.meizu.passwordmanager.common.Constants;
import com.realjt.meizu.passwordmanager.db.PasswordDatabaseHelper;
import com.realjt.meizu.passwordmanager.task.LoginTask;
import com.realjt.meizu.passwordmanager.task.LoginTask.LoginListener;
import com.realjt.meizu.passwordmanager.utils.DateUtils;
import com.realjt.meizu.passwordmanager.utils.LogUtils;
import com.realjt.meizu.passwordmanager.utils.SettingsUtils;
import com.realjt.meizu.passwordmanager.view.ProgressDialog;
import com.realjt.meizu.passwordmanager.view.PromptDialog;

public class ManageSpaceActivity extends BaseActivity implements
		OnClickListener
{
	private PromptDialog promptDialog;

	private TextView loginPromptText;

	private EditText inputEditText;

	private CheckBox checkBox;

	private Button clearButton;

	private ProgressDialog progressDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_manage_space);

		loginPromptText = (TextView) findViewById(R.id.tv_manage_space_login_prompt);
		inputEditText = (EditText) findViewById(R.id.et_manage_space);
		checkBox = (CheckBox) findViewById(R.id.cb_manage_space_retain_password);
		clearButton = (Button) findViewById(R.id.bt_manage_apace_clear);

		initView();
	}

	private void initView()
	{
		promptDialog = new PromptDialog(this, 0, R.string.explain,
				R.string.clear_data_tip, R.string.sure, 0);
		promptDialog.show();

		if (checkLoginLimitTime())
		{
			loginPromptText.setText(SettingsUtils.getLoginPrompt());
		}

		inputEditText.addTextChangedListener(textWatcher);
		clearButton.setOnClickListener(this);
	}

	/**
	 * 检查当前时间是否允许登录
	 * 
	 * @return
	 */
	private boolean checkLoginLimitTime()
	{
		if (Constants.LOGIN_ALLOW_FAIL_TIMES <= SettingsUtils
				.getLoginFailedTimes())
		{
			long loginLimitTime = SettingsUtils.getLoginLimitTime();
			if (new Date().getTime() < loginLimitTime)
			{
				String str = DateUtils.dateToLoginLimitTime(new Date(
						loginLimitTime));

				loginPromptText.setText("请在" + str + "后重试");
				loginPromptText.setTextColor(getResources().getColor(
						android.R.color.holo_red_light));

				return false;
			}
		}

		return true;
	}

	@Override
	public void onClick(View v)
	{
		loginValidate();
	}

	private void loginValidate()
	{
		LogUtils.debug("click clear data button, start login validate");

		if (SettingsUtils.isFirstLogin()
				&& null == SettingsUtils.getLoginPassword())
		{
			// 尚未第一次登录使用
			toastMakeText("无数据可清除");

			return;
		}

		String inputText = inputEditText.getText().toString();
		if (inputText.length() >= 6 && inputText.length() <= 16)
		{
			if (checkLoginLimitTime())
			{
				showProgressDialog(getString(R.string.verificating));

				LoginTask loginTask = new LoginTask();
				loginTask.setLoginListener(new LoginListener()
				{
					@Override
					public void onSuccess()
					{
						progressDialog.dismiss();
						Constants.LOGIN_PASSWORD = inputEditText.getText()
								.toString();

						// 登录成功后设置登录失败次数和登录时间限制
						SettingsUtils.setLoginFailedTimes(0);
						SettingsUtils.setLoginLimitTime(0);

						LogUtils.debug("login validate success, start clear data");

						clearData();
					}

					@Override
					public void onFaiure()
					{
						progressDialog.dismiss();

						loginPromptText.setText(R.string.wrong_password);
						loginPromptText.setTextColor(getResources().getColor(
								android.R.color.holo_red_light));

						// 登录失败后纪录次数与登录限制时间
						int loginFailedTimes = SettingsUtils
								.getLoginFailedTimes();
						if (loginFailedTimes < Constants.LOGIN_ALLOW_FAIL_TIMES)
						{
							loginFailedTimes++;
						}
						SettingsUtils.setLoginFailedTimes(loginFailedTimes);

						if (Constants.LOGIN_ALLOW_FAIL_TIMES == loginFailedTimes)
						{
							long loginLimitTime = new Date().getTime()
									+ DateUtils.TWO_MINUTES;
							SettingsUtils.setLoginLimitTime(loginLimitTime);

							String str = DateUtils
									.dateToLoginLimitTime(new Date(
											loginLimitTime));

							loginPromptText.setText("请在" + str + "后重试");
							loginPromptText.setTextColor(getResources()
									.getColor(android.R.color.holo_red_light));
						}

						LogUtils.error("login validate failed, password error");
					}

				});

				loginTask.execute(inputText);
			}
		} else
		{
			loginPromptText.setText(R.string.password_at_least_6_pleace);
			loginPromptText.setTextColor(getResources().getColor(
					android.R.color.holo_red_light));

			LogUtils.error("password input illegal");
		}
	}

	private void clearData()
	{
		if (!checkBox.isChecked())
		{
			SettingsUtils.deleteAllData();
		}

		PasswordDatabaseHelper.getInstance(this).deleteAllPassword();

		onBackPressed();
	}

	private void showProgressDialog(String message)
	{
		progressDialog = new ProgressDialog(this);

		progressDialog.setMessage(message);
		progressDialog.show();
	}

	/**
	 * 输入合法性及时监听
	 */
	private TextWatcher textWatcher = new TextWatcher()
	{
		private CharSequence revisedStr = "";

		@Override
		public void beforeTextChanged(CharSequence charSequence, int start,
				int count, int after)
		{
		}

		@Override
		public void onTextChanged(CharSequence charSequence, int start,
				int before, int count)
		{
			revisedStr = charSequence;
		}

		@Override
		public void afterTextChanged(Editable editable)
		{
			if (!Constants.LOGIN_PASSWORD_PATTERN.matcher(revisedStr).matches())
			{
				editable.delete(inputEditText.getSelectionStart() - 1,
						inputEditText.getSelectionEnd());
				inputEditText.setText(editable);
				inputEditText.setSelection(editable.length());
			}
		}
	};

	@Override
	public void onBackPressed()
	{
		super.onBackPressed();
		overridePendingTransition(R.anim.push_left_in, R.anim.push_right_out);

		Constants.LOGIN_PASSWORD = null;
	}

	@Override
	protected void onDestroy()
	{
		super.onDestroy();

		Constants.LOGIN_PASSWORD = null;
	}

}