package com.realjt.meizu.passwordmanager.view;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.realjt.meizu.passwordmanager.R;

/**
 * 处理等待对话框
 * 
 * @author Administrator
 * 
 */
public class ProgressDialog extends AlertDialog.Builder
{
	private AlertDialog alertDialog;

	private TextView messageTextView;

	private static final int WIDTH = 800;

	private static final int HEIGHT = 300;

	/**
	 * 构造函数
	 * 
	 * @param context
	 *            上下文对象
	 * @param message
	 *            显示消息
	 */
	public ProgressDialog(Context context)
	{
		super(context);

		View view = LayoutInflater.from(context).inflate(
				R.layout.dialog_progress, new RelativeLayout(context), false);
		messageTextView = (TextView) view
				.findViewById(R.id.tv_dialog_progress_message);

		setView(view);
	}

	public void setMessage(String message)
	{
		messageTextView.setText(message);
	}

	/**
	 * 展示
	 */
	@Override
	public AlertDialog show()
	{
		this.alertDialog = super.show();

		WindowManager.LayoutParams layoutParams = this.alertDialog.getWindow()
				.getAttributes();
		layoutParams.width = WIDTH;
		layoutParams.height = HEIGHT;

		this.alertDialog.getWindow().setAttributes(layoutParams);

		return this.alertDialog;
	}

	/**
	 * 隐藏
	 */
	public void dismiss()
	{
		if (null != this.alertDialog)
		{
			alertDialog.dismiss();
		}
	}

}
