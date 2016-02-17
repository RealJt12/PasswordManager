package com.realjt.meizu.passwordmanager.adapter;

import java.io.File;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.realjt.meizu.passwordmanager.R;
import com.realjt.meizu.passwordmanager.common.Constants;
import com.realjt.meizu.passwordmanager.utils.FileUtils;

/**
 * 文件浏览适配器
 * 
 * @author Administrator
 */
public class FileAdapter extends BaseAdapter
{
	/**
	 * 布局加载器
	 */
	private LayoutInflater layoutInflater;

	/**
	 * 文件列表
	 */
	private List<File> fileList;

	/**
	 * 构造函数
	 * 
	 * @param context
	 *            上下文对象
	 */
	public FileAdapter(Context context)
	{
		this.layoutInflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount()
	{
		if (null != fileList)
		{
			return fileList.size();
		}

		return 0;
	}

	@Override
	public Object getItem(int position)
	{
		if (null != fileList)
		{
			return fileList.get(position);
		}

		return null;
	}

	@Override
	public long getItemId(int position)
	{
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		ViewHolder viewHolder;

		if (null == convertView)
		{
			convertView = layoutInflater.inflate(R.layout.list_item_file,
					parent, false);

			viewHolder = new ViewHolder();
			viewHolder.fileNameText = (TextView) convertView
					.findViewById(R.id.tv_file_item_name);
			viewHolder.fileIcon = (ImageView) convertView
					.findViewById(R.id.iv_file_item_icon);

			// 保存viewHolder
			convertView.setTag(viewHolder);
		} else
		{
			viewHolder = (ViewHolder) convertView.getTag();
		}

		File file = fileList.get(position);

		if (file.isDirectory())
		{
			viewHolder.fileIcon.setImageResource(R.drawable.ic_folder);
		} else
		{
			if (file.getName().endsWith(Constants.FILE_SUFFIX))
			{
				viewHolder.fileIcon.setImageResource(R.drawable.icon);
			} else
			{
				viewHolder.fileIcon.setImageResource(R.drawable.ic_file);
			}
		}

		viewHolder.fileNameText.setText(file.getName());

		return convertView;
	}

	public List<File> getFileList()
	{
		return fileList;
	}

	public void setFileList(List<File> fileList)
	{
		clearData();
		this.fileList = fileList;
	}

	public void setFileList(File[] files, boolean displayHiddenFile)
	{
		clearData();
		fileList = FileUtils.sortFiles(files, displayHiddenFile);
	}

	private void clearData()
	{
		if (null != fileList)
		{
			fileList.clear();
		}
	}

	/**
	 * Item描述类 性能优化 空间换时间
	 */
	private class ViewHolder
	{
		/**
		 * 文件图标
		 */
		public ImageView fileIcon;

		/**
		 * 文件名
		 */
		public TextView fileNameText;

	}

}
