package com.realjt.meizu.passwordmanager.model;

import android.content.Context;

import com.realjt.meizu.passwordmanager.R;

/**
 * 密码分类
 * 
 * @author Administrator
 * 
 */
public enum Classification
{
	OTHER(0, R.string.classification_other),

	LIFE(1, R.string.classification_life),

	WEBSITE(2, R.string.classification_website),

	BANK(3, R.string.classification_bank),

	STUDY(4, R.string.classification_study),

	GAME(5, R.string.classification_game);

	private int classification;

	private int description;

	private Classification(int classification, int descriptionResId)
	{
		this.classification = classification;
		this.description = descriptionResId;
	}

	public static Classification getDefaultClassification()
	{
		return LIFE;
	}

	/**
	 * 通过分类码初始化
	 * 
	 * @param classification
	 *            分类码
	 * @return
	 */
	public static Classification initClassification(int classification)
	{
		Classification[] classifications = Classification.values();

		for (Classification classificationTemp : classifications)
		{
			if (classificationTemp.getClassification() == classification)
			{
				return classificationTemp;
			}
		}

		return OTHER;
	}

	/**
	 * 通过分类描述初始化
	 * 
	 * @param description
	 *            分类描述
	 * @return
	 */
	public static Classification initClassification(Context context,
			String description)
	{
		Classification[] classifications = Classification.values();

		for (Classification classification : classifications)
		{
			if (context.getString(classification.getDescription()).equals(
					description))
			{
				return classification;
			}
		}

		return OTHER;
	}

	/**
	 * 得到分类
	 * 
	 * @return
	 */
	public int getClassification()
	{
		return this.classification;
	}

	/**
	 * 得到分类描述
	 * 
	 * @return
	 */
	public int getDescription()
	{
		return this.description;
	}

}
