package com.realjt.meizu.passwordmanager.view;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.realjt.meizu.passwordmanager.R;

/**
 * 屏幕下面弹出选择框
 * 
 * @author Administrator
 * 
 */
public class ClassificationPopupView extends PopupWindow implements
		OnClickListener, OnTouchListener
{
	/**
	 * 内容View
	 */
	private View contentView;

	/**
	 * 取消按钮
	 */
	private TextView cancelTextView;

	private TextView lifeTextView, websiteTextView, bankTextView,
			studyTextView, gameTextView, otherTextView;

	private OnItemSelectedListener onItemSelectedListener;

	public interface OnItemSelectedListener
	{
		void onClick(PopupWindow popupWindow, int which);
	}

	public ClassificationPopupView(Context context)
	{
		super(context);

		// 设置SelectPicPopupWindow弹出窗体动画效果
		setAnimationStyle(R.style.PopupButtomStyle);
		// 设置SelectPicPopupWindow弹出窗体的背景
		setBackgroundDrawable(new ColorDrawable(0x00000000));

		contentView = LayoutInflater.from(context).inflate(
				R.layout.popup_classification, (ViewGroup) null);

		setContentView(contentView);

		// 设置SelectPicPopupWindow弹出窗体的宽
		setWidth(LayoutParams.MATCH_PARENT);
		// 设置SelectPicPopupWindow弹出窗体的高
		setHeight(LayoutParams.WRAP_CONTENT);
		// 设置SelectPicPopupWindow弹出窗体可点击
		setFocusable(true);

		// 添加OnTouchListener监听判断获取触屏位置如果在选择框外面则销毁弹出框
		contentView.setOnTouchListener(this);

		initView();
	}

	private void initView()
	{
		cancelTextView = (TextView) contentView
				.findViewById(R.id.tv_popup_classification_cancel);

		lifeTextView = (TextView) contentView
				.findViewById(R.id.tv_popup_classification_life);
		websiteTextView = (TextView) contentView
				.findViewById(R.id.tv_popup_classification_website);
		bankTextView = (TextView) contentView
				.findViewById(R.id.tv_popup_classification_bank);
		studyTextView = (TextView) contentView
				.findViewById(R.id.tv_popup_classification_study);
		gameTextView = (TextView) contentView
				.findViewById(R.id.tv_popup_classification_game);
		otherTextView = (TextView) contentView
				.findViewById(R.id.tv_popup_classification_other);

		cancelTextView.setOnClickListener(this);
		lifeTextView.setOnClickListener(this);
		websiteTextView.setOnClickListener(this);
		bankTextView.setOnClickListener(this);
		studyTextView.setOnClickListener(this);
		gameTextView.setOnClickListener(this);
		otherTextView.setOnClickListener(this);
	}

	public OnItemSelectedListener getOnItemSelectedListener()
	{
		return onItemSelectedListener;
	}

	public void setOnItemSelectedListener(
			OnItemSelectedListener onItemSelectedListener)
	{
		this.onItemSelectedListener = onItemSelectedListener;
	}

	@Override
	public void onClick(View view)
	{
		dismiss();

		switch (view.getId())
		{
		case R.id.tv_popup_classification_cancel:
			// dismiss();
			break;
		case R.id.tv_popup_classification_life:
			onItemSelected(0);
			break;
		case R.id.tv_popup_classification_website:
			onItemSelected(1);
			break;
		case R.id.tv_popup_classification_bank:
			onItemSelected(2);
			break;
		case R.id.tv_popup_classification_study:
			onItemSelected(3);
			break;
		case R.id.tv_popup_classification_game:
			onItemSelected(4);
			break;
		case R.id.tv_popup_classification_other:
			onItemSelected(5);
			break;
		default:
			break;
		}
	}

	private void onItemSelected(int which)
	{
		if (null != onItemSelectedListener)
		{
			onItemSelectedListener.onClick(this, which);
		}
	}

	@Override
	public boolean onTouch(View view, MotionEvent event)
	{
		// 判断获取触屏位置如果在选择框外面则销毁弹出框
		int height = contentView.getTop();
		int y = (int) event.getY();
		if (event.getAction() == MotionEvent.ACTION_UP)
		{
			if (y < height)
			{
				dismiss();
			}
		}

		view.performClick();

		return true;
	}

}
