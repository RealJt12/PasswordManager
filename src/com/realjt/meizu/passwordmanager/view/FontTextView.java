package com.realjt.meizu.passwordmanager.view;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * 自定义字体类
 * 
 * @author Administrator
 */
public class FontTextView extends TextView
{
	private static Typeface typeface;

	// public FontTextView(Context context)
	// {
	// super(context);
	// setFont(context);
	// }

	public FontTextView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		setFont(context);
	}

	private void setFont(Context context)
	{
		if (null == typeface)
		{
			typeface = Typeface.createFromAsset(context.getAssets(),
					"consolas.ttf");
		}

		setTypeface(typeface);
	}

}