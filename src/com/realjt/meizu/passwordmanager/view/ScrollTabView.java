package com.realjt.meizu.passwordmanager.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class ScrollTabView extends View
{
	/**
	 * 画笔
	 */
	private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

	/**
	 * 总个数
	 */
	private int totalNumber;

	/**
	 * 当前位置
	 */
	private int currentPosition;

	/**
	 * 控件宽度
	 */
	private float viewWidth;

	/**
	 * 每一个tab宽度
	 */
	private float tabWidth;

	/**
	 * tab偏移量
	 */
	private float tabOffset;

	public ScrollTabView(Context context)
	{
		super(context);

		paint.setAntiAlias(true);
	}

	public ScrollTabView(Context context, AttributeSet attrs)
	{
		super(context, attrs);

		paint.setAntiAlias(true);
	}

	public void setColor(int color)
	{
		paint.setColor(color);
	}

	public void setTotalNumber(int totalNumber)
	{
		this.totalNumber = totalNumber;
	}

	public void setCurrentPosition(int currentPosition)
	{
		this.currentPosition = currentPosition;
		tabOffset = 0;
	}

	public void notifyChanged(int position, float offset)
	{
		currentPosition = position;
		tabOffset = offset;

		invalidate();
	}

	@Override
	protected void onDraw(Canvas canvas)
	{
		super.onDraw(canvas);

		if (0 == tabWidth)
		{
			viewWidth = getWidth();

			tabWidth = viewWidth / totalNumber;
		}

		// 根据位置和偏移量来计算滑动条的位置
		float left = (currentPosition + tabOffset) * tabWidth;

		float right = left + tabWidth;
		float top = getPaddingTop();
		float bottom = getHeight() - getPaddingBottom();

		if (0 == currentPosition && 0 == tabOffset)
		{
			left = 0;
		}

		if (currentPosition == totalNumber - 1)
		{
			right = getWidth();
		}

		canvas.drawRect(left, top, right, bottom, paint);
	}

	public void setWidth(int pixels)
	{
		getLayoutParams().width = pixels;
	}

}
