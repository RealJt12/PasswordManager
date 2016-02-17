package com.realjt.meizu.passwordmanager.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.realjt.meizu.passwordmanager.R;
import com.realjt.meizu.passwordmanager.common.Constants.ReturnCode;
import com.realjt.meizu.passwordmanager.view.PromptDialog;
import com.realjt.meizu.passwordmanager.view.PromptDialog.PromptDialogOnClickListener;

/**
 * 编辑Activity
 * 
 * @author Administrator
 * 
 */
public class TextEditActivity extends BaseActivity implements
		PromptDialogOnClickListener, OnClickListener
{
	/**
	 * 输入计数
	 */
	private TextView textCount;

	/**
	 * 输入框
	 */
	private EditText editText;

	/**
	 * 最大输入限制长度
	 */
	private int maxLength;

	/**
	 * 原来的字符串
	 */
	private String originalTemp;

	/**
	 * 提示对话框
	 */
	private PromptDialog promptDialog;

	/**
	 * 完成按钮
	 */
	private Button completeButton;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_textedit);

		textCount = (TextView) findViewById(R.id.tv_textedit_count);
		editText = (EditText) findViewById(R.id.et_textedit);
		completeButton = (Button) findViewById(R.id.bt_textedit_complete);

		initView();
	}

	private void initView()
	{
		Intent intent = getIntent();
		setTitle(intent.getStringExtra("edittitle"));
		editText.setText(intent.getStringExtra("editcontent"));
		originalTemp = intent.getStringExtra("editcontent");
		editText.setSelection(editText.getText().length());
		editText.addTextChangedListener(textWatcher);
		maxLength = intent.getIntExtra("maxlength", 100);

		editText.setFilters(new InputFilter[] { new InputFilter.LengthFilter(
				maxLength) });
		textCount.setText(editText.getText().length() + "/" + maxLength);

		completeButton.setOnClickListener(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		getMenuInflater().inflate(R.menu.menu_textedit, menu);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{
		case R.id.action_text_edit_clear:
			editText.setText("");
			break;

		default:
			break;
		}

		return super.onOptionsItemSelected(item);
	}

	private void complete()
	{
		Intent intent = new Intent(ReturnCode.COMPLETE);
		intent.putExtra("edittitle", getTitle().toString());
		intent.putExtra("editcontent", editText.getText().toString());

		setResult(ReturnCode.SUCCESS, intent);

		super.onBackPressed();

		overridePendingTransition(R.anim.push_left_in, R.anim.push_right_out);
	}

	@Override
	public void onBackPressed()
	{
		if (originalTemp.equals(editText.getText().toString()))
		{
			Intent intent = new Intent(ReturnCode.CANCEL);
			setResult(ReturnCode.SUCCESS, intent);

			super.onBackPressed();

			overridePendingTransition(R.anim.push_left_in,
					R.anim.push_right_out);
		} else
		{
			promptDialog = new PromptDialog(TextEditActivity.this, 0, "保存修改?",
					null, R.string.sure, R.string.cancel);
			promptDialog.setPromptDialogOnClickListener(this);
			promptDialog.show();
		}
	}

	TextWatcher textWatcher = new TextWatcher()
	{

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count)
		{
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after)
		{
		}

		@Override
		public void afterTextChanged(Editable s)
		{
			textCount.setText(s.length() + "/" + maxLength);
		}
	};

	@Override
	public void onPromptDialogClick(AlertDialog alertDialog, int requestCode,
			boolean positive)
	{
		if (positive)
		{
			complete();
		} else
		{
			Intent intent = new Intent(ReturnCode.CANCEL);
			setResult(ReturnCode.SUCCESS, intent);

			super.onBackPressed();

			overridePendingTransition(R.anim.push_left_in,
					R.anim.push_right_out);
		}
	}

	@Override
	public void onClick(View view)
	{
		if (R.id.bt_textedit_complete == view.getId())
		{
			complete();
		}
	}

}