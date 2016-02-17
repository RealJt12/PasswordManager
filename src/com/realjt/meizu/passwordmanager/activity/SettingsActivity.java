package com.realjt.meizu.passwordmanager.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.realjt.meizu.passwordmanager.R;
import com.realjt.meizu.passwordmanager.common.Constants.ReturnCode;
import com.realjt.meizu.passwordmanager.utils.SettingsUtils;

/**
 * 设置Activity
 * 
 * @author Administrator
 */
public class SettingsActivity extends BaseActivity implements OnClickListener,
		OnCheckedChangeListener
{
	private TextView loginPromptText, backupSavePathText;

	private RelativeLayout loginPromptLayout, backSavePathLayout,
			statisticsLayout, changePasswordLayout, notDisplayAllLayout,
			useLoginPasswordLayout, immediatelyExitLayout, aboutLayout;

	private Switch notDisplayAllSwitch, useLoginPasswordSwitch,
			immediatelyExitSwitch;

	private String rootPath = Environment.getExternalStorageDirectory()
			.getAbsolutePath();

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);

		loginPromptText = (TextView) findViewById(R.id.tv_settings_login_prompt);
		backupSavePathText = (TextView) findViewById(R.id.tv_settings_backup_savepath);

		loginPromptLayout = (RelativeLayout) findViewById(R.id.rl_settings_login_prompt);
		backSavePathLayout = (RelativeLayout) findViewById(R.id.rl_settings_backup_savepath);
		statisticsLayout = (RelativeLayout) findViewById(R.id.rl_settings_statistics);
		changePasswordLayout = (RelativeLayout) findViewById(R.id.rl_settings_change_password);
		notDisplayAllLayout = (RelativeLayout) findViewById(R.id.rl_settings_not_alldisplay_password);
		useLoginPasswordLayout = (RelativeLayout) findViewById(R.id.rl_settings_use_login_password);
		immediatelyExitLayout = (RelativeLayout) findViewById(R.id.rl_settings_immediately_exit);
		aboutLayout = (RelativeLayout) findViewById(R.id.rl_settings_about);

		notDisplayAllSwitch = (Switch) findViewById(R.id.sw_settings_not_alldisplay_password);
		useLoginPasswordSwitch = (Switch) findViewById(R.id.sw_settings_use_login_password);
		immediatelyExitSwitch = (Switch) findViewById(R.id.sw_settings_immediately_exit);

		// initCustomActionBar();

		initView();
	}

	// private void initCustomActionBar()
	// {
	// getActionBar().setDisplayShowCustomEnabled(true);
	//
	// getActionBar().setCustomView(R.layout.actionbar_settings);
	//
	// getActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
	// getActionBar().setDisplayShowCustomEnabled(true);
	//
	// TextView titleTextView = (TextView) getActionBar().getCustomView()
	// .findViewById(R.id.tv_actionbar_settings_title);
	// titleTextView.setTextColor(getResources().getColor(
	// R.color.blue_actionbar));
	//
	// Button personalButton = (Button) getActionBar().getCustomView()
	// .findViewById(R.id.bt_actionbar_settings_personal);
	// personalButton.setOnClickListener(this);
	// }

	private void initView()
	{
		loginPromptLayout.setOnClickListener(this);
		backSavePathLayout.setOnClickListener(this);
		statisticsLayout.setOnClickListener(this);
		changePasswordLayout.setOnClickListener(this);
		notDisplayAllLayout.setOnClickListener(this);
		useLoginPasswordLayout.setOnClickListener(this);
		immediatelyExitLayout.setOnClickListener(this);
		aboutLayout.setOnClickListener(this);

		loginPromptText.setText(SettingsUtils.getLoginPrompt());

		String backupPath = SettingsUtils.getBackupSavePath();
		if (backupPath.length() == rootPath.length())
		{
			backupSavePathText.setText(R.string.sd_card);
		} else
		{
			backupSavePathText.setText(backupPath.substring(
					rootPath.length() + 1, backupPath.length()));
		}

		notDisplayAllSwitch.setChecked(SettingsUtils.isNotDisplayAllPassword());
		useLoginPasswordSwitch.setChecked(SettingsUtils
				.isUseLoginPasswordToBackup());
		immediatelyExitSwitch.setChecked(SettingsUtils.isExitPressHomeKey());

		notDisplayAllSwitch.setOnCheckedChangeListener(this);
		useLoginPasswordSwitch.setOnCheckedChangeListener(this);
		immediatelyExitSwitch.setOnCheckedChangeListener(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// getMenuInflater().inflate(R.menu.menu_settings, menu);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{
		case R.id.action_settings_skin:

			// getActionBar().setBackgroundDrawable(
			// getDrawable(R.drawable.bg_actionbar_bottom_coral));
			break;

		default:
			break;
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onClick(View view)
	{
		Intent intent = new Intent();
		switch (view.getId())
		{
		case R.id.bt_actionbar_settings_personal:
			toastMakeText("换肤功能，敬请期待");
			break;

		case R.id.rl_settings_login_prompt:
			toEdit(getString(R.string.login_prompt), loginPromptText.getText()
					.toString(), 20, 0);
			break;

		case R.id.rl_settings_backup_savepath:
			intent.setClass(SettingsActivity.this, RecoveryActivity.class);
			intent.putExtra("choosepath", true);
			intent.putExtra("path", Environment.getExternalStorageDirectory()
					.getAbsolutePath());
			startActivityForResult(intent, 1);
			overridePendingTransition(R.anim.push_right_in,
					R.anim.push_left_out);
			break;

		case R.id.rl_settings_not_alldisplay_password:
			notDisplayAllSwitch.setChecked(!notDisplayAllSwitch.isChecked());
			break;

		case R.id.rl_settings_use_login_password:
			useLoginPasswordSwitch.setChecked(!useLoginPasswordSwitch
					.isChecked());
			break;

		case R.id.rl_settings_immediately_exit:
			immediatelyExitSwitch
					.setChecked(!immediatelyExitSwitch.isChecked());
			break;

		case R.id.rl_settings_change_password:
			intent.setClass(this, ChangePasswordActivity.class);
			startActivity(intent);
			overridePendingTransition(R.anim.push_right_in,
					R.anim.push_left_out);
			break;

		case R.id.rl_settings_statistics:
			intent.setClass(this, StatisticsActivity.class);
			startActivity(intent);
			overridePendingTransition(R.anim.push_right_in,
					R.anim.push_left_out);
			break;

		case R.id.rl_settings_about:
			intent.setClass(this, AboutActivity.class);
			startActivity(intent);
			overridePendingTransition(R.anim.push_right_in,
					R.anim.push_left_out);
			break;

		default:
			break;
		}
	}

	private void toEdit(String editTitle, String editContent, int maxLength,
			int requestCode)
	{
		Intent intent = new Intent(SettingsActivity.this,
				TextEditActivity.class);
		intent.putExtra("edittitle", editTitle);
		intent.putExtra("editcontent", editContent);
		intent.putExtra("maxlength", maxLength);

		startActivityForResult(intent, requestCode);

		overridePendingTransition(R.anim.push_right_in, R.anim.push_left_out);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode,
			Intent intent)
	{
		super.onActivityResult(requestCode, resultCode, intent);

		if (null != intent
				&& ReturnCode.COMPLETE.equals(intent.getAction().toString()))
		{
			if (0 == requestCode)
			{
				loginPromptText.setText(intent.getStringExtra("editcontent"));
				SettingsUtils.setLoginPrompt(intent
						.getStringExtra("editcontent"));
			}
			if (1 == requestCode)
			{
				if (intent.hasExtra("path"))
				{
					String backupPath = intent.getStringExtra("path");
					if (backupPath.length() == rootPath.length())
					{
						backupSavePathText.setText(R.string.sd_card);
					} else
					{
						backupSavePathText.setText(backupPath.substring(
								rootPath.length() + 1, backupPath.length()));
					}

					SettingsUtils.setBackupSavePath(backupPath);
				}
			}
		}
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
	{
		if (buttonView.getId() == R.id.sw_settings_use_login_password)
		{
			SettingsUtils.setUseLoginPasswordToBackup(isChecked);
		} else if (buttonView.getId() == R.id.sw_settings_immediately_exit)
		{
			SettingsUtils.setExitPressHomeKey(isChecked);
		} else if (buttonView.getId() == R.id.sw_settings_not_alldisplay_password)
		{
			SettingsUtils.setNotDisplayAllPassword(isChecked);
		}
	}

	@Override
	public void onBackPressed()
	{
		Intent intent = new Intent(ReturnCode.COMPLETE);
		setResult(ReturnCode.SUCCESS, intent);

		super.onBackPressed();

		overridePendingTransition(R.anim.push_left_in, R.anim.push_right_out);
	}

}
