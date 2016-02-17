package com.realjt.meizu.passwordmanager.view;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.realjt.meizu.passwordmanager.common.Constants;

/**
 * 输入对话框
 * 
 * @author Administrator
 * 
 */
public class InputDialog extends AlertDialog.Builder implements OnClickListener
{
	private AlertDialog alertDialog;

	/**
	 * 调用时保存参数
	 */
	private Object object;

	/**
	 * 请求码
	 */
	private int requestCode;

	/**
	 * 输入框
	 */
	private EditText editText;

	/**
	 * 确定，取消按钮
	 */
	private Button positiveButton, negativeButton;

	/**
	 * 回调接口
	 */
	private OnInputDialogOnClickListener inputDialogOnClickListener;

	/**
	 * 输入对话框回调接口
	 * 
	 * @author Administrator
	 * 
	 */
	public interface OnInputDialogOnClickListener
	{
		void onInputDialogClick(AlertDialog alertDialog, int requestCode,
				boolean positive, String inputText);
	}

	/**
	 * 构造函数
	 * 
	 * @param context
	 *            上下文对象
	 */
	public InputDialog(Context context)
	{
		super(context);
	}

	/**
	 * 构造函数
	 * 
	 * @param context
	 *            上下文对象
	 * @param requestCode
	 *            请求码
	 * @param title
	 *            对话框标题
	 * @param positiveButtonText
	 *            确定按钮文字
	 * @param negativeButtonText
	 *            取消按钮问题
	 * @param inputDialogOnClickListener
	 *            回调接口
	 */
	public InputDialog(Context context, int requestCode, String title,
			int positiveButtonText, int negativeButtonText)
	{
		this(context, requestCode, title, context.getResources().getString(
				positiveButtonText), context.getResources().getString(
				negativeButtonText));
	}

	/**
	 * 构造函数
	 * 
	 * @param context
	 *            上下文对象
	 * @param requestCode
	 *            请求码
	 * @param title
	 *            对话框标题
	 * @param positiveButtonText
	 *            确定按钮文字
	 * @param negativeButtonText
	 *            取消按钮问题
	 * @param inputDialogOnClickListener
	 *            回调接口
	 */
	public InputDialog(Context context, int requestCode, String title,
			String positiveButtonText, String negativeButtonText)
	{
		this(context);
		this.requestCode = requestCode;

		setTitle(title);

		setView(initEditText(context));

		setPositiveButton(positiveButtonText, null);
		setNegativeButton(negativeButtonText, null);
	}

	/**
	 * 初始化对话框UI
	 * 
	 * @param context
	 *            上下文对象
	 * @return
	 */
	private RelativeLayout initEditText(Context context)
	{
		editText = new EditText(context);
		editText.setInputType(InputType.TYPE_CLASS_TEXT
				| InputType.TYPE_TEXT_VARIATION_PASSWORD);
		editText.setFilters(new InputFilter[] { new InputFilter.LengthFilter(16) });
		editText.addTextChangedListener(textWatcher);
		editText.setTextSize(20);
		editText.setGravity(Gravity.CENTER_HORIZONTAL);

		RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
				ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);
		layoutParams.topMargin = 60;
		layoutParams.leftMargin = 80;
		layoutParams.rightMargin = 80;

		RelativeLayout relativeLayout = new RelativeLayout(context);
		relativeLayout.addView(editText, layoutParams);

		return relativeLayout;
	}

	@Override
	public AlertDialog show()
	{
		alertDialog = super.show();

		positiveButton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
		positiveButton.setTextSize(16);
		positiveButton.setOnClickListener(this);
		negativeButton = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
		negativeButton.setTextSize(16);
		negativeButton.setOnClickListener(this);

		return alertDialog;
	}

	public void setTag(Object tag)
	{
		this.object = tag;
	}

	public Object getTag()
	{
		return object;
	}

	public void dismiss()
	{
		if (null != alertDialog)
		{
			alertDialog.dismiss();
		}
	}

	@Override
	public void onClick(View view)
	{
		if (view.getId() == positiveButton.getId())
		{
			if (null != inputDialogOnClickListener)
			{
				inputDialogOnClickListener.onInputDialogClick(alertDialog,
						requestCode, true, editText.getText().toString());
			}
		} else if (view.getId() == negativeButton.getId())
		{
			dismiss();
			if (null != inputDialogOnClickListener)
			{
				inputDialogOnClickListener.onInputDialogClick(alertDialog,
						requestCode, false, editText.getText().toString());
			}
		}
	}

	private TextWatcher textWatcher = new TextWatcher()
	{

		private CharSequence revisedStr = "";

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after)
		{
		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count)
		{
			revisedStr = s;
		}

		@Override
		public void afterTextChanged(Editable editable)
		{
			if (!Constants.LOGIN_PASSWORD_PATTERN.matcher(revisedStr).matches())
			{
				editable.delete(editText.getSelectionStart() - 1,
						editText.getSelectionEnd());
				editText.setText(editable);
				editText.setSelection(editable.length());
			}
		}

	};

	public void setInputDialogOnClickListener(
			OnInputDialogOnClickListener inputDialogOnClickListener)
	{
		this.inputDialogOnClickListener = inputDialogOnClickListener;
	}

}
