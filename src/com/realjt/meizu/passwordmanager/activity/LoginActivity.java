package com.realjt.meizu.passwordmanager.activity;

import java.util.Calendar;
import java.util.Date;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.meizu.mstore.license.ILicensingService;
import com.meizu.mstore.license.LicenseCheckHelper;
import com.meizu.mstore.license.LicenseResult;
import com.realjt.meizu.passwordmanager.R;
import com.realjt.meizu.passwordmanager.common.CommonUtils;
import com.realjt.meizu.passwordmanager.common.Constants;
import com.realjt.meizu.passwordmanager.task.LoginTask;
import com.realjt.meizu.passwordmanager.task.LoginTask.LoginListener;
import com.realjt.meizu.passwordmanager.utils.DateUtils;
import com.realjt.meizu.passwordmanager.utils.LogUtils;
import com.realjt.meizu.passwordmanager.utils.SettingsUtils;
import com.realjt.meizu.passwordmanager.view.ProgressDialog;
import com.realjt.meizu.passwordmanager.view.PromptDialog;
import com.realjt.meizu.passwordmanager.view.PromptDialog.PromptDialogOnClickListener;

/**
 * 登录Activity
 * 
 * @author Administrator
 */
public class LoginActivity extends BaseActivity implements OnClickListener,
		PromptDialogOnClickListener
{
	/**
	 * 密码输入提示
	 */
	private TextView loginPromptText;

	/**
	 * 密码输入框
	 */
	private EditText inputEditText;

	/**
	 * 登录按钮
	 */
	private Button loginButton, helpButton;

	/**
	 * 处理等待对话框
	 */
	private ProgressDialog progressDialog;

	/**
	 * 提示对话框
	 */
	private PromptDialog promptDialog;

	/**
	 * 魅族收费验证服务
	 */
	private ILicensingService iLicensingService;

	/**
	 * 密码建议弹出窗口
	 */
	private PopupWindow popupWindow;

	/**
	 * 服务绑定的回调，可用于判断服务是否绑定成功
	 */
	private ServiceConnection serviceConnection = new ServiceConnection()
	{
		public void onServiceConnected(ComponentName name, IBinder service)
		{
			LogUtils.debug("meizu service already connected");

			// 服务绑定成功，获取服务实例
			iLicensingService = ILicensingService.Stub.asInterface(service);

			if (null != iLicensingService)
			{
				verifyVersion();
			}
		}

		public void onServiceDisconnected(ComponentName name)
		{
			iLicensingService = null;
		}

	};

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		getActionBar().hide();

		setContentView(R.layout.activity_login);

		loginPromptText = (TextView) findViewById(R.id.tv_login_prompt);
		inputEditText = (EditText) findViewById(R.id.et_login);
		loginButton = (Button) findViewById(R.id.bt_login);
		helpButton = (Button) findViewById(R.id.bt_login_help);

		initView();

		// 初始化验证服务
		// checkSignature();
	}

	/**
	 * 检查签名是否被修改，以及检查付费情况
	 */
	private void checkSignature()
	{
		if (CommonUtils.checkSignature(getPackageManager(), getPackageName()))
		{
			// 初始化验证服务
			initLicensingService();
		} else
		{
			verifyVersionFailed("警告", "密码管理软件已被非法修改，请到应用商店下载使用正版应用，以免造成不必要的损失。");
		}
	}

	/**
	 * 初始化UI
	 */
	private void initView()
	{
		// 判断是否展示使用说明
		if (SettingsUtils.isFirstLogin())
		{
			showUseExplain();
		}

		if (checkLoginLimitTime())
		{
			loginPromptText.setText(SettingsUtils.getLoginPrompt());
		}

		loginButton.setOnClickListener(this);
		inputEditText.addTextChangedListener(textWatcher);

		helpButton.setOnClickListener(this);

		// 监听键盘输入完成
		inputEditText.setOnEditorActionListener(new OnEditorActionListener()
		{
			@Override
			public boolean onEditorAction(TextView textView, int actionId,
					KeyEvent event)
			{
				if (EditorInfo.IME_ACTION_DONE == actionId)
				{
					login();
				}

				return true;
			}
		});
	}

	/**
	 * 展示使用说明提示框
	 */
	private void showUseExplain()
	{
		LogUtils.debug("first login, show use explain dialog");

		promptDialog = new PromptDialog(this, 0, R.string.use_explain,
				R.string.use_explain_content, R.string.accept,
				R.string.do_not_accept);
		promptDialog.setPromptDialogOnClickListener(this);
		promptDialog.setCancelable(false);
		promptDialog.show();
	}

	/**
	 * 初始化魅族收费验证服务
	 */
	private void initLicensingService()
	{
		LogUtils.debug("start bind meizu service");

		// 绑定服务
		Intent serviceIntent = new Intent();
		serviceIntent.setAction(ILicensingService.class.getName());
		serviceIntent.setPackage("com.meizu.mstore");
		this.bindService(serviceIntent, serviceConnection,
				Context.BIND_AUTO_CREATE);
	}

	/**
	 * 验证应用版本：正式版或者试用版,并处理
	 */
	private void verifyVersion()
	{
		// 保存服务验证的结果值
		LicenseResult licenseResult = null;
		try
		{
			// 调用服务接口进行验证,获取返回的结果值
			licenseResult = iLicensingService.checkLicense(getApplication()
					.getPackageName());
		} catch (RemoteException e)
		{
			Constants.EDITION = 3;
			verifyVersionFailed("发生验证错误", "请到应用商店下载应用");
		}

		if (null != licenseResult
				&& LicenseResult.RESPONSE_CODE_SUCCESS == licenseResult
						.getResponseCode())
		{
			// license验证服务验证通过,需要接着对服务返回的结果再次进行校验(使用自己的公钥进行验证)
			boolean success = LicenseCheckHelper.checkResult(
					Constants.APK_PUBLIC_KEY, licenseResult);
			if (success
					&& LicenseResult.PURCHASE_TYPE_NORMAL == licenseResult
							.getPurchaseType())
			{
				// 验证成功，并且为正式版本
				Constants.EDITION = 2;

				LogUtils.debug("app is already purchased");
			} else
			{
				if (success
						&& LicenseResult.PURCHASE_TYPE_TRIAL == licenseResult
								.getPurchaseType())
				{
					// 验证成功，是试用版本
					Constants.EDITION = 1;

					LogUtils.debug("app is trial version");

					// 求剩余的天数
					long dif = Calendar.getInstance().getTimeInMillis()
							- licenseResult.getStartDate().getTimeInMillis();
					int passDays = (int) (dif / (24 * 60 * 60 * 1000));

					// 剩余天数
					int leftDays = Constants.EXPIRE_DAYS - passDays;
					if (leftDays < 0)
					{
						verifyVersionFailed("试用版已过期", "请到应用商店购买应用");

						LogUtils.debug("trial version has expired");
					}
				} else
				{
					// 验证不成功或者版本类型不对，可按试用版处理
					Constants.EDITION = 3;
					verifyVersionFailed("发生验证错误", "请到应用商店下载应用");
				}
			}
		} else
		{
			// license验证服务验证不通过
			Constants.EDITION = 3;
			verifyVersionFailed("发生验证错误", "请到应用商店下载应用");
		}
	}

	/**
	 * 验证失败后退出
	 * 
	 * @param message
	 */
	private void verifyVersionFailed(String title, String message)
	{
		LogUtils.debug("meizu service check app failed");

		if (null != promptDialog)
		{
			promptDialog.dismiss();
		}

		promptDialog = new PromptDialog(this, 1, title, message, R.string.sure,
				0);
		promptDialog.setPromptDialogOnClickListener(this);
		promptDialog.setCancelable(false);
		promptDialog.show();
	}

	@Override
	protected void onDestroy()
	{
		super.onDestroy();

		// 如果服务已经绑定，在退出时需要反绑服务
		if (null != iLicensingService)
		{
			unbindService(serviceConnection);
		}
	}

	@Override
	public void onClick(View view)
	{
		switch (view.getId())
		{
		case R.id.bt_login:
			login();
			break;

		case R.id.bt_login_help:
			showTips();
			break;

		default:
			break;
		}
	}

	private void showProgressDialog(String message)
	{
		progressDialog = new ProgressDialog(this);

		progressDialog.setMessage(message);
		progressDialog.show();
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
	 * 第一次登录
	 * 
	 * @param inputPassword
	 */
	private void firstLogin(String inputPassword)
	{
		LogUtils.debug("first login");

		Constants.LOGIN_PASSWORD = inputPassword;

		SettingsUtils.setFirstLogin(false);
		SettingsUtils.setLoginPassword(inputPassword);

		toastMakeText(R.string.keep_password_in_mind);

		Intent intent = new Intent(this, HomeActivity.class);
		startActivity(intent);

		overridePendingTransition(R.anim.zoom_in_enter, R.anim.zoom_in_exit);

		onBackPressed();
	}

	/**
	 * 登录
	 * 
	 * @param inputPassword
	 *            输入的密码
	 */
	private void login()
	{
		LogUtils.debug("click login button, start login validate");

		String inputText = inputEditText.getText().toString();
		if (inputText.length() >= 6 && inputText.length() <= 16)
		{
			if (SettingsUtils.isFirstLogin()
					&& null == SettingsUtils.getLoginPassword())
			{
				firstLogin(inputText);
			} else if (checkLoginLimitTime())
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

						startActivity(new Intent(LoginActivity.this,
								HomeActivity.class));
						overridePendingTransition(R.anim.zoom_in_enter,
								R.anim.zoom_in_exit);

						// 登录成功后设置登录失败次数和登录时间限制
						SettingsUtils.setLoginFailedTimes(0);
						SettingsUtils.setLoginLimitTime(0);

						LogUtils.debug("login success, enter home activity");

						LoginActivity.this.finish();
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

						LogUtils.error("login failed, password error");
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

	/**
	 * 提示对话框回调函数
	 */
	@Override
	public void onPromptDialogClick(AlertDialog alertDialog, int requestCode,
			boolean positive)
	{
		if (0 == requestCode)
		{
			if (!positive)
			{
				this.finish();
			}
		}
		if (1 == requestCode)
		{
			this.finish();
		}
	}

}