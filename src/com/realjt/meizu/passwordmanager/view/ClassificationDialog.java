package com.realjt.meizu.passwordmanager.view;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import com.realjt.meizu.passwordmanager.R;

/**
 * 分类选择对话框
 * 
 * @author Administrator
 * 
 */
public class ClassificationDialog extends AlertDialog.Builder
{
	private String[] items;

	/**
	 * 构造函数
	 * 
	 * @param context
	 *            上下文对象
	 */
	public ClassificationDialog(Context context)
	{
		super(context);

		items = context.getResources().getStringArray(R.array.classification);

		// initItems();
	}

	/**
	 * 初始化分类
	 */
	// private void initItems()
	// {
	// Classification[] classifications = Classification.values();
	// items = new String[classifications.length];
	// for (int i = 0; i < items.length - 1; i++)
	// {
	// items[i] = getContext().getString(
	// classifications[i + 1].getDescription());
	// }
	//
	// items[items.length - 1] = getContext().getString(
	// classifications[0].getDescription());
	// }

	public void setOnClickListener(
			DialogInterface.OnClickListener onClickListener)
	{
		setItems(items, onClickListener);
	}

}
