package com.realjt.meizu.passwordmanager.activity;

import java.util.Date;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.realjt.meizu.passwordmanager.R;
import com.realjt.meizu.passwordmanager.common.Constants.ReturnCode;
import com.realjt.meizu.passwordmanager.db.PasswordDatabaseHelper;
import com.realjt.meizu.passwordmanager.model.Classification;
import com.realjt.meizu.passwordmanager.model.Password;
import com.realjt.meizu.passwordmanager.utils.DateUtils;
import com.realjt.meizu.passwordmanager.utils.EncryptUtils;
import com.realjt.meizu.passwordmanager.utils.SettingsUtils;
import com.realjt.meizu.passwordmanager.view.ClassificationPopupView;
import com.realjt.meizu.passwordmanager.view.PromptDialog;
import com.realjt.meizu.passwordmanager.view.PromptDialog.PromptDialogOnClickListener;

/**
 * 账号详情Activity
 * 
 * @author Administrator
 * 
 */
public class DetailActivity extends BaseActivity implements OnClickListener,
		PromptDialogOnClickListener
{
	private PasswordDatabaseHelper databaseHelper;

	private TextView nameText, accountText, passwordText, remarksText,
			createTimeText, reviseTimeText, classificationText;

	private RelativeLayout nameLayout, accountLayout, passwordLayout,
			remarksLayout, classificationLayout;

	private Password password;

	/**
	 * 是否已修改
	 */
	private boolean revised = false;

	private PromptDialog promptDialog;

	private Button saveReviseButton, deleteButton;

	private ClassificationPopupView classificationPopupView;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_detail);

		databaseHelper = PasswordDatabaseHelper.getInstance(this);

		nameText = (TextView) findViewById(R.id.tv_detail_name);
		accountText = (TextView) findViewById(R.id.tv_detail_account);
		passwordText = (TextView) findViewById(R.id.tv_detail_password);
		remarksText = (TextView) findViewById(R.id.tv_detail_remarks);
		classificationText = (TextView) findViewById(R.id.tv_detail_classification);
		createTimeText = (TextView) findViewById(R.id.tv_detail_createtime);
		reviseTimeText = (TextView) findViewById(R.id.tv_detail_revisetime);

		nameLayout = (RelativeLayout) findViewById(R.id.rl_detail_name);
		accountLayout = (RelativeLayout) findViewById(R.id.rl_detail_account);
		passwordLayout = (RelativeLayout) findViewById(R.id.rl_detail_password);
		remarksLayout = (RelativeLayout) findViewById(R.id.rl_detail_remarks);
		classificationLayout = (RelativeLayout) findViewById(R.id.rl_detail_classification);

		saveReviseButton = (Button) findViewById(R.id.bt_detail_save_revise);
		deleteButton = (Button) findViewById(R.id.bt_detail_delete);

		initView();
	}

	private void initView()
	{
		// id为主键
		// int id = getIntent().getStringExtra("createTime");
		int id = getIntent().getIntExtra("id", -1);

		if (-1 != id)
		{
			password = databaseHelper.queryPasswordById(id);
		}

		if (null != password)
		{
			nameText.setText(password.getName());
			accountText.setText(password.getAccount());

			if (SettingsUtils.isNotDisplayAllPassword())
			{
				passwordText.setText(EncryptUtils.notAllDisplay(password
						.getPassword()));
			} else
			{
				passwordText.setText(password.getPassword());
			}

			remarksText.setText(password.getRemarks());
			classificationText.setText(password.getClassification()
					.getDescription());
			createTimeText.setText(DateUtils.dateToMinute(password
					.getCreateTime()));
			reviseTimeText.setText(DateUtils.dateToMinute(password
					.getReviseTime()));
		}

		nameLayout.setOnClickListener(this);
		accountLayout.setOnClickListener(this);
		passwordLayout.setOnClickListener(this);
		remarksLayout.setOnClickListener(this);
		classificationLayout.setOnClickListener(this);

		saveReviseButton.setOnClickListener(this);
		deleteButton.setOnClickListener(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		getMenuInflater().inflate(R.menu.menu_detail, menu);

		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{
		case R.id.action_detail_send:
			send();
			break;

		case R.id.action_detail_clear_all:
			clearAll();
			break;

		default:
			break;
		}

		return super.onOptionsItemSelected(item);
	}

	/**
	 * 分享发送
	 */
	private void send()
	{
		promptDialog = new PromptDialog(this, 2, getString(R.string.send),
				"账号密码泄露风险！", R.string.keep_on, R.string.cancel);
		promptDialog.setPromptDialogOnClickListener(this);
		promptDialog.show();
	}

	private void clearAll()
	{
		password.setPassword("");

		nameText.setText("");
		accountText.setText("");
		passwordText.setText("");
		remarksText.setText("");

		classificationText.setText(Classification.getDefaultClassification()
				.getDescription());
	}

	@Override
	public void onClick(View view)
	{
		switch (view.getId())
		{
		case R.id.rl_detail_name:
			toEdit(getString(R.string.name), nameText.getText().toString(),
					200, 0);
			break;

		case R.id.rl_detail_account:
			toEdit(getString(R.string.account), accountText.getText()
					.toString(), 200, 1);
			break;

		case R.id.rl_detail_password:
			toEdit(getString(R.string.password), password.getPassword(), 200, 2);
			break;

		case R.id.rl_detail_remarks:
			toEdit(getString(R.string.remarks), remarksText.getText()
					.toString(), 200, 3);
			break;

		case R.id.rl_detail_classification:
			showClassification();
			break;

		case R.id.bt_detail_save_revise:
			saveRevise();
			break;

		case R.id.bt_detail_delete:
			delete();
			break;

		default:
			break;
		}
	}

	private void toEdit(String editTitle, String editContent, int maxLength,
			int requestCode)
	{
		Intent intent = new Intent(this, TextEditActivity.class);
		intent.putExtra("edittitle", editTitle);
		intent.putExtra("editcontent", editContent);
		intent.putExtra("maxlength", maxLength);

		startActivityForResult(intent, requestCode);

		overridePendingTransition(R.anim.push_right_in, R.anim.push_left_out);
	}

	/**
	 * 分类选择
	 */
	// private void selectClassification()
	// {
	// ClassificationDialog classificationDialog = new ClassificationDialog(
	// this);
	// classificationDialog
	// .setOnClickListener(new DialogInterface.OnClickListener()
	// {
	//
	// @Override
	// public void onClick(DialogInterface dialog, int which)
	// {
	// Classification[] classifications = Classification
	// .values();
	// which = (which + 1) % classifications.length;
	// classificationText.setText(Classification
	// .initClassification(which).getDescription());
	//
	// revised = true;
	// }
	//
	// });
	//
	// classificationDialog.show();
	// }

	private void showClassification()
	{
		if (null == classificationPopupView)
		{
			classificationPopupView = new ClassificationPopupView(this);
			classificationPopupView
					.setOnItemSelectedListener(new ClassificationPopupView.OnItemSelectedListener()
					{
						@Override
						public void onClick(PopupWindow popupWindow, int which)
						{
							Classification[] classifications = Classification
									.values();
							which = (which + 1) % classifications.length;
							classificationText
									.setText(Classification.initClassification(
											which).getDescription());

							revised = true;
						}
					});
		}

		classificationPopupView.showAtLocation(
				findViewById(android.R.id.content), Gravity.BOTTOM, 0, 0);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode,
			Intent intent)
	{
		super.onActivityResult(requestCode, resultCode, intent);

		if (null != intent
				&& ReturnCode.COMPLETE.equals(intent.getAction().toString()))
		{
			String editContent = intent.getStringExtra("editcontent");

			if (0 == requestCode)
			{
				nameText.setText(editContent);
			} else if (1 == requestCode)
			{
				accountText.setText(editContent);
			} else if (2 == requestCode)
			{
				password.setPassword(editContent);
				if (SettingsUtils.isNotDisplayAllPassword())
				{
					passwordText.setText(EncryptUtils.notAllDisplay(password
							.getPassword()));
				} else
				{
					passwordText.setText(password.getPassword());
				}
			} else if (3 == requestCode)
			{
				remarksText.setText(editContent);
			}

			revised = true;
		}
	}

	/**
	 * 保存修改
	 */
	private void saveRevise()
	{
		if (!nameText.getText().toString().equals(""))
		{
			update();

			toastMakeText(R.string.already_save_revised);

			revised = false;

			initView();
		} else
		{
			toastMakeText(R.string.name_not_null);
		}
	}

	private void update()
	{
		password.setName(nameText.getText().toString());
		password.setAccount(accountText.getText().toString());
		password.setRemarks(remarksText.getText().toString());

		Date date = new Date();
		password.setReviseTime(date);

		password.setClassification(Classification.initClassification(this,
				classificationText.getText().toString()));

		databaseHelper.updatePassword(password);
	}

	private void delete()
	{
		promptDialog = new PromptDialog(DetailActivity.this, 1, "确定删除?", null,
				R.string.sure, R.string.cancel);
		promptDialog.setPromptDialogOnClickListener(this);
		promptDialog.show();
	}

	@Override
	public void onBackPressed()
	{
		if (revised)
		{
			promptDialog = new PromptDialog(DetailActivity.this, 0, "保存修改?",
					null, R.string.save, R.string.cancel);
			promptDialog.setPromptDialogOnClickListener(this);
			promptDialog.show();
		} else
		{
			Intent intent = new Intent(ReturnCode.COMPLETE);
			setResult(ReturnCode.SUCCESS, intent);

			super.onBackPressed();

			overridePendingTransition(R.anim.zoom_out_enter,
					R.anim.zoom_out_exit);
		}
	}

	@Override
	public void onPromptDialogClick(AlertDialog alertDialog, int requestCode,
			boolean positive)
	{
		if (0 == requestCode)
		{
			if (positive)
			{
				if (!nameText.getText().toString().equals(""))
				{
					update();

					Intent intent = new Intent(ReturnCode.COMPLETE);
					setResult(ReturnCode.SUCCESS, intent);

					super.onBackPressed();
					overridePendingTransition(R.anim.push_left_in,
							R.anim.push_right_out);
				} else
				{
					toastMakeText("名称不能为空");
				}
			} else
			{
				Intent intent = new Intent(ReturnCode.COMPLETE);
				setResult(ReturnCode.SUCCESS, intent);

				super.onBackPressed();
				overridePendingTransition(R.anim.push_left_in,
						R.anim.push_right_out);
			}
		}
		if (1 == requestCode)
		{
			if (positive)
			{
				databaseHelper.deletePassword(password);

				Intent intent = new Intent(ReturnCode.COMPLETE);
				setResult(ReturnCode.SUCCESS, intent);

				DetailActivity.this.finish();

				overridePendingTransition(R.anim.zoom_out_enter,
						R.anim.zoom_out_exit);
			}
		}
		if (2 == requestCode)
		{
			if (positive)
			{
				String shareText = "账号：" + accountText.getText() + " 密码："
						+ password.getPassword();

				Intent intent = new Intent(Intent.ACTION_SEND);
				intent.setType("text/plain");
				intent.putExtra(Intent.EXTRA_TEXT, shareText);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(Intent.createChooser(intent, "发送到"));
			}
		}
	}

}
