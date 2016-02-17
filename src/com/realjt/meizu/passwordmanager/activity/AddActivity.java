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
 * 新建账号Activity
 * 
 * @author Administrator
 * 
 */
public class AddActivity extends BaseActivity implements OnClickListener,
		PromptDialogOnClickListener
{
	/**
	 * 名称，账号，密码，备注，创建时间，修改时间，分类展示TextView
	 */
	private TextView nameText, accountText, passwordText, remarksText,
			createTimeText, reviseTimeText, classificationText;

	/**
	 * 名称，账号，密码，备注，创建时间，修改时间，分类点击Layout
	 */
	private RelativeLayout nameLayout, accountLayout, passwordLayout,
			remarksLayout, classificationLayout;

	/**
	 * 当前密码对象，自动生成创建时间和修改时间
	 */
	private Password password = new Password();

	/**
	 * 提示对话框
	 */
	private PromptDialog promptDialog;

	/**
	 * 保存按钮
	 */
	private Button saveButton, cancelButton;

	private ClassificationPopupView classificationPopupView;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add);

		nameText = (TextView) findViewById(R.id.tv_add_name);
		accountText = (TextView) findViewById(R.id.tv_add_account);
		passwordText = (TextView) findViewById(R.id.tv_add_password);
		remarksText = (TextView) findViewById(R.id.tv_add_remarks);
		classificationText = (TextView) findViewById(R.id.tv_add_classification);
		createTimeText = (TextView) findViewById(R.id.tv_add_createtime);
		reviseTimeText = (TextView) findViewById(R.id.tv_add_revisetime);

		nameLayout = (RelativeLayout) findViewById(R.id.rl_add_name);
		accountLayout = (RelativeLayout) findViewById(R.id.rl_add_account);
		passwordLayout = (RelativeLayout) findViewById(R.id.rl_add_password);
		remarksLayout = (RelativeLayout) findViewById(R.id.rl_add_remarks);
		classificationLayout = (RelativeLayout) findViewById(R.id.rl_add_classification);

		saveButton = (Button) findViewById(R.id.bt_add_save);
		cancelButton = (Button) findViewById(R.id.bt_add_cancel);

		initView();
	}

	/**
	 * 初始化UI界面
	 */
	private void initView()
	{
		createTimeText.setText(DateUtils.dateToMinute(password.getCreateTime()));
		reviseTimeText.setText(DateUtils.dateToMinute(password.getReviseTime()));

		classificationText.setText(password.getClassification()
				.getDescription());

		nameLayout.setOnClickListener(this);
		accountLayout.setOnClickListener(this);
		passwordLayout.setOnClickListener(this);
		remarksLayout.setOnClickListener(this);
		classificationLayout.setOnClickListener(this);

		saveButton.setOnClickListener(this);
		cancelButton.setOnClickListener(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		getMenuInflater().inflate(R.menu.menu_add, menu);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{
		case R.id.action_add_clear_all:
			clearAll();
			break;

		default:
			break;
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onClick(View view)
	{
		switch (view.getId())
		{
		case R.id.rl_add_name:
			toEdit(getString(R.string.name), nameText.getText().toString(),
					200, 0);
			break;

		case R.id.rl_add_account:
			toEdit(getString(R.string.account), accountText.getText()
					.toString(), 200, 1);
			break;

		case R.id.rl_add_password:
			toEdit(getString(R.string.password), password.getPassword(), 200, 2);
			break;

		case R.id.rl_add_remarks:
			toEdit(getString(R.string.remarks), remarksText.getText()
					.toString(), 200, 3);
			break;

		case R.id.rl_add_classification:
			// selectClassification();
			showClassification();
			break;

		case R.id.bt_add_save:
			addPassword();
			break;

		case R.id.bt_add_cancel:
			onBackPressed();
			break;

		default:
			break;
		}
	}

	/**
	 * 分类选择对话框及回调监听
	 */
	// private void selectClassification()
	// {
	// ClassificationDialog classificationDialog = new ClassificationDialog(
	// this);
	// classificationDialog
	// .setOnClickListener(new DialogInterface.OnClickListener()
	// {
	// @Override
	// public void onClick(DialogInterface dialog, int which)
	// {
	// Classification[] classifications = Classification
	// .values();
	// which = (which + 1) % classifications.length;
	//
	// classificationText.setText(Classification
	// .initClassification(which).getDescription());
	// }
	//
	// });
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
						}
					});
		}

		classificationPopupView.showAtLocation(
				findViewById(android.R.id.content), Gravity.BOTTOM, 0, 0);
	}

	/**
	 * 编辑
	 * 
	 * @param editTitle
	 *            标题
	 * @param editContent
	 *            内容
	 * @param maxLength
	 *            最大长度
	 * @param requestCode
	 *            请求码
	 */
	private void toEdit(String editTitle, String editContent, int maxLength,
			int requestCode)
	{
		Intent intent = new Intent(AddActivity.this, TextEditActivity.class);
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
		}
	}

	/**
	 * 添加一条密码记录
	 */
	private void addPassword()
	{
		if (!"".equals(nameText.getText().toString()))
		{
			PasswordDatabaseHelper databaseHelper = PasswordDatabaseHelper
					.getInstance(this);

			password.setName(nameText.getText().toString());
			password.setAccount(accountText.getText().toString());
			if (null == password.getPassword())
			{
				password.setPassword("");
			}
			password.setRemarks(remarksText.getText().toString());
			password.setClassification(Classification.initClassification(this,
					classificationText.getText().toString()));

			Date date = new Date();
			password.setReviseTime(date);

			databaseHelper.insertPassword(password);

			Intent intent = new Intent(ReturnCode.COMPLETE);
			setResult(ReturnCode.SUCCESS, intent);

			super.onBackPressed();

			overridePendingTransition(R.anim.push_left_in,
					R.anim.push_right_out);
		} else
		{
			toastMakeText(R.string.name_not_null);
		}
	}

	/**
	 * 清除当前页面的所有数据
	 */
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
	public void onBackPressed()
	{
		if (!"".equals(nameText.getText().toString())
				|| !"".equals(accountText.getText().toString())
				|| !"".equals(passwordText.getText().toString())
				|| !"".equals(remarksText.getText().toString()))
		{
			promptDialog = new PromptDialog(AddActivity.this, 0, "保存新建?", null,
					R.string.save, R.string.cancel);
			promptDialog.setPromptDialogOnClickListener(this);
			promptDialog.show();
		} else
		{
			Intent intent = new Intent(ReturnCode.CANCEL);
			setResult(ReturnCode.SUCCESS, intent);

			super.onBackPressed();

			overridePendingTransition(R.anim.zoom_out_enter,
					R.anim.zoom_out_exit);
		}
	}

	/**
	 * 提示对话框回调接口
	 */
	@Override
	public void onPromptDialogClick(AlertDialog alertDialog, int requestCode,
			boolean positive)
	{
		if (positive)
		{
			addPassword();
		} else
		{
			Intent intent = new Intent(ReturnCode.CANCEL);
			setResult(ReturnCode.SUCCESS, intent);

			super.onBackPressed();

			overridePendingTransition(R.anim.push_left_in,
					R.anim.push_right_out);
		}
	}

}
