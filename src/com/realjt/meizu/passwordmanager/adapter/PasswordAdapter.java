package com.realjt.meizu.passwordmanager.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.realjt.meizu.passwordmanager.R;
import com.realjt.meizu.passwordmanager.model.Password;
import com.realjt.meizu.passwordmanager.utils.DateUtils;

/**
 * 主页密码列表适配器
 */
public class PasswordAdapter extends BaseAdapter
{
	/**
	 * 布局加载器
	 */
	private LayoutInflater layoutInflater = null;

	/**
	 * 当前列表的数据
	 */
	private List<Password> passwordList = null;

	/**
	 * 构造函数
	 * 
	 * @param context
	 *            上下文对象
	 */
	public PasswordAdapter(Context context)
	{
		this.layoutInflater = LayoutInflater.from(context);
		this.passwordList = new ArrayList<Password>();
	}

	@Override
	public int getCount()
	{
		if (null != passwordList)
		{
			return passwordList.size();
		}

		return 0;
	}

	@Override
	public Object getItem(int position)
	{
		if (passwordList != null)
		{
			return passwordList.get(position);
		}

		return null;
	}

	@Override
	public long getItemId(int position)
	{
		return position;
	}

	@Override
	public boolean isEnabled(int position)
	{
		return true;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		ViewHolder viewHolder;

		if (null == convertView)
		{
			convertView = layoutInflater.inflate(R.layout.list_item_password,
					parent, false);

			viewHolder = new ViewHolder();
			viewHolder.nameTextView = (TextView) convertView
					.findViewById(R.id.tv_password_item_name);
			viewHolder.accountTextView = (TextView) convertView
					.findViewById(R.id.tv_password_item_account);
			viewHolder.createtimeTextView = (TextView) convertView
					.findViewById(R.id.tv_password_item_createtime);

			// 保存viewHolder
			convertView.setTag(viewHolder);
		} else
		{
			viewHolder = (ViewHolder) convertView.getTag();
		}

		Password password = passwordList.get(position);

		viewHolder.nameTextView.setText(password.getName());
		viewHolder.accountTextView.setText(password.getAccount());
		viewHolder.createtimeTextView.setText(DateUtils.dateToDay(password
				.getCreateTime()));

		return convertView;
	}

	public List<Password> getPasswordList()
	{
		return passwordList;
	}

	public void setPasswordList(List<Password> passwordList)
	{
		clearData();
		this.passwordList = passwordList;
	}

	private void clearData()
	{
		if (null != passwordList)
		{
			passwordList.clear();
		}
	}

	/**
	 * 内部类，优化ListView UI性能，用内存空间换取加载时间，不用频繁使用findViewById
	 */
	private class ViewHolder
	{
		/**
		 * 名称 账号 创建时间
		 */
		public TextView nameTextView, accountTextView, createtimeTextView;

	}

}
