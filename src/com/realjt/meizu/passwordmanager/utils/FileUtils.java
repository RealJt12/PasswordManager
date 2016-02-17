package com.realjt.meizu.passwordmanager.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.os.Environment;

/**
 * 文件相关工具类
 * 
 * @author Administrator
 */
public class FileUtils
{
	/**
	 * 获取存储根路径文件列表
	 * 
	 * @return
	 */
	// public static File[] getRootPathFiles()
	// {
	// File[] files = handleRootFile();
	// if (null == files || files.length == 1)
	// {
	// files = Environment.getExternalStorageDirectory().listFiles();
	// }
	//
	// return files;
	// }

	/**
	 * 获取存储根路径目录
	 * 
	 * @return
	 */
	public static String getRootPath()
	{
		return Environment.getExternalStorageDirectory().getPath();
	}

	// public static String getRootPath()
	// {
	// File[] files = handleRootFile();
	// String rootPath = Environment.getExternalStorageDirectory().getPath();
	//
	// if (null == files || files.length == 1)
	// {
	// return rootPath;
	// }
	//
	// return rootPath.substring(0, rootPath.lastIndexOf("/"));
	// }

	// public static File[] handleRootFile()
	// {
	// File[] files = null;
	//
	// BufferedReader bufferedReader = null;
	//
	// try
	// {
	// Runtime runtime = Runtime.getRuntime();
	//
	// String line;
	// String mount = new String();
	//
	// bufferedReader = new BufferedReader(new InputStreamReader(runtime
	// .exec("mount").getInputStream()));
	//
	// while ((line = bufferedReader.readLine()) != null)
	// {
	// if (line.contains("secure") || line.contains("asec"))
	// {
	// continue;
	// }
	//
	// String columns[] = line.split(" ");
	//
	// if (columns != null && columns.length > 1)
	// {
	// if (line.contains("fat"))
	// {
	// mount = mount.concat(" " + columns[1]);
	// } else if (line.contains("fuse"))
	// {
	// mount = mount.concat(columns[1]);
	// }
	// }
	// }
	//
	// String path[] = mount.split(" ");
	//
	// files = new File[path.length];
	//
	// for (int i = path.length - 1; i >= 0; i--)
	// {
	// File file = new File(path[i]);
	// if (file.isDirectory())
	// {
	// files[(i + 1) % path.length] = file;
	// }
	// }
	// } catch (FileNotFoundException e)
	// {
	// e.printStackTrace();
	// } catch (IOException e)
	// {
	// e.printStackTrace();
	// } finally
	// {
	// try
	// {
	// if (bufferedReader != null)
	// {
	// bufferedReader.close();
	// }
	// } catch (IOException e)
	// {
	// e.printStackTrace();
	// }
	// }
	//
	// return files;
	// }

	/**
	 * 文件排序,先加载文件夹,后加载文件
	 * 
	 * @param files
	 * @return
	 */
	public static List<File> sortFiles(File[] files, boolean displayHiddenFile)
	{
		if (files != null && files.length > 0)
		{
			Arrays.sort(files);

			List<File> folderList = new ArrayList<File>(files.length / 2);
			List<File> fileList = new ArrayList<File>(files.length / 2);

			for (File file : files)
			{
				if (null != file)
				{
					if (file.isHidden() && displayHiddenFile)
					{
						if (file.isDirectory())
						{
							folderList.add(file);
						}

						if (file.isFile())
						{
							fileList.add(file);
						}
					} else if (!file.isHidden())
					{
						if (file.isDirectory())
						{
							folderList.add(file);
						}

						if (file.isFile())
						{
							fileList.add(file);
						}
					}
				}
			}

			folderList.addAll(fileList);

			return folderList;
		}

		return new ArrayList<File>(0);
	}

}
