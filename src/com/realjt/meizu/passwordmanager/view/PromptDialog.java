package com.realjt.meizu.passwordmanager.view;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.widget.Button;

/**
 * 提示对话框
 * 
 * @author Administrator
 * 
 */
public class PromptDialog extends AlertDialog.Builder implements
		OnClickListener
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
	 * 确定，取消按钮
	 */
	private Button positiveButton, negativeButton;

	/**
	 * 提示对话框回调接口
	 * 
	 * @author Administrator
	 * 
	 */
	public interface PromptDialogOnClickListener
	{
		void onPromptDialogClick(AlertDialog alertDialog, int requestCode,
				boolean positive);
	}

	/**
	 * 回调接口
	 */
	private PromptDialogOnClickListener promptDialogOnClickListener;

	private PromptDialog(Context context)
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
	 * @param message
	 *            提示信息
	 * @param positiveButtonText
	 *            确定按钮显示文字
	 * @param negativeButtonText
	 *            取消按钮显示文字
	 * @param promptDialogOnClickListener
	 *            回调接口
	 */
	public PromptDialog(Context context, int requestCode, String title,
			String message, String positiveButtonText, String negativeButtonText)
	{
		this(context);
		this.requestCode = requestCode;

		setTitle(title);
		setMessage(message);

		if (null != positiveButtonText)
		{
			setPositiveButton(positiveButtonText, this);
		}
		if (null != negativeButtonText)
		{
			setNegativeButton(negativeButtonText, this);
		}
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
	 * @param message
	 *            提示信息
	 * @param positiveButtonText
	 *            确定按钮显示文字
	 * @param negativeButtonText
	 *            取消按钮显示文字
	 * @param promptDialogOnClickListener
	 *            回调接口
	 */
	public PromptDialog(Context context, int requestCode, int title,
			int message, int positiveButtonText, int negativeButtonText)
	{
		this(context, requestCode, context.getResources().getString(title),
				context.getResources().getString(message), positiveButtonText,
				negativeButtonText);
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
	 * @param message
	 *            提示信息
	 * @param positiveButtonText
	 *            确定按钮显示文字
	 * @param negativeButtonText
	 *            取消按钮显示文字
	 * @param promptDialogOnClickListener
	 *            回调接口
	 */
	public PromptDialog(Context context, int requestCode, String title,
			String message, int positiveButtonText, int negativeButtonText)
	{
		this(context);
		this.requestCode = requestCode;

		setTitle(title);
		setMessage(message);

		if (0 != positiveButtonText)
		{
			setPositiveButton(
					context.getResources().getString(positiveButtonText), this);
		}
		if (0 != negativeButtonText)
		{
			setNegativeButton(
					context.getResources().getString(negativeButtonText), this);
		}
	}

	public void setPromptDialogOnClickListener(
			PromptDialogOnClickListener promptDialogOnClickListener)
	{
		this.promptDialogOnClickListener = promptDialogOnClickListener;
	}

	@Override
	public AlertDialog show()
	{
		alertDialog = super.show();

		positiveButton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
		if (null != positiveButton)
		{
			positiveButton.setTextSize(16);
		}

		negativeButton = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
		if (null != negativeButton)
		{
			negativeButton.setTextSize(16);
		}

		return alertDialog;
	}

	public void dismiss()
	{
		if (null != alertDialog)
		{
			alertDialog.dismiss();
		}
	}

	public void setTag(Object tag)
	{
		this.object = tag;
	}

	public Object getTag()
	{
		return object;
	}

	@Override
	public void onClick(DialogInterface dialog, int which)
	{
		if (null != promptDialogOnClickListener)
		{
			if (-1 == which)
			{
				promptDialogOnClickListener.onPromptDialogClick(alertDialog,
						requestCode, true);
			} else if (-2 == which)
			{
				promptDialogOnClickListener.onPromptDialogClick(alertDialog,
						requestCode, false);
			}
		}
	}

}
