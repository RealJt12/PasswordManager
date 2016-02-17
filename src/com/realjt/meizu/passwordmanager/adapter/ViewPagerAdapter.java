package com.realjt.meizu.passwordmanager.adapter;

import java.util.ArrayList;
import java.util.List;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

/**
 * ViewPager适配器
 * 
 * @author Administrator
 * 
 */
public class ViewPagerAdapter extends PagerAdapter
{
	/**
	 * view列表
	 */
	private List<View> pagerViews;

	public ViewPagerAdapter()
	{
		this.pagerViews = new ArrayList<View>();
	}

	public ViewPagerAdapter(List<View> pagerViews)
	{
		this.pagerViews = pagerViews;
	}

	public List<View> getPageViews()
	{
		return pagerViews;
	}

	public void setPageViews(List<View> pagerViews)
	{
		this.pagerViews = pagerViews;
	}

	@Override
	public int getCount()
	{
		if (null != pagerViews)
		{
			return pagerViews.size();
		}

		return 0;
	}

	@Override
	public boolean isViewFromObject(View view, Object object)
	{
		return view == object;
	}

	@Override
	public int getItemPosition(Object object)
	{
		return super.getItemPosition(object);
	}

	@Override
	public void destroyItem(View view, int position, Object object)
	{
		((ViewPager) view).removeView(pagerViews.get(position));
	}

	@Override
	public Object instantiateItem(View view, int position)
	{
		((ViewPager) view).addView(pagerViews.get(position));

		return pagerViews.get(position);
	}

}
