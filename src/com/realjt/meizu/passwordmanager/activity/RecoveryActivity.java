package com.realjt.meizu.passwordmanager.activity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Locale;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.JsonReader;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.realjt.meizu.passwordmanager.R;
import com.realjt.meizu.passwordmanager.adapter.FileAdapter;
import com.realjt.meizu.passwordmanager.common.Constants;
import com.realjt.meizu.passwordmanager.common.Constants.ReturnCode;
import com.realjt.meizu.passwordmanager.task.RecoveryDataTask;
import com.realjt.meizu.passwordmanager.task.RecoveryDataTask.RecoveryDataTaskListener;
import com.realjt.meizu.passwordmanager.utils.EncryptUtils;
import com.realjt.meizu.passwordmanager.utils.SettingsUtils;
import com.realjt.meizu.passwordmanager.view.InputDialog;
import com.realjt.meizu.passwordmanager.view.InputDialog.OnInputDialogOnClickListener;
import com.realjt.meizu.passwordmanager.view.ProgressDialog;

/**
 * 数据恢复Activity
 * 
 * @author Administrator
 */
public class RecoveryActivity extends BaseActivity implements
		OnItemClickListener, OnInputDialogOnClickListener, OnClickListener
{
	/**
	 * 当前路径
	 */
	private TextView pathTextView;

	private ListView fileListView;

	private FileAdapter fileAdapter;

	private String path;

	private static final String rootPath = Environment
			.getExternalStorageDirectory().getAbsolutePath();

	private boolean choosePath = false;

	private ProgressDialog progressDialog;

	private InputDialog inputDialog;

	private String encryptFilePassword, inputFilePassword;

	private JsonReader recoveryJsonReader;

	private Button operateButton;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		Intent intent = getIntent();
		path = intent.getStringExtra("path");
		choosePath = intent.getBooleanExtra("choosepath", false);

		setContentView(R.layout.activity_recovery);

		pathTextView = (TextView) findViewById(R.id.tv_recovery_path);
		fileListView = (ListView) findViewById(R.id.lv_recovery_file_browser);
		operateButton = (Button) findViewById(R.id.bt_recovery_operate);

		initView();
	}

	private void initView()
	{
		fileAdapter = new FileAdapter(this);

		if (choosePath)
		{
			setTitle(R.string.backup_savepath);

			File file = new File(path);
			if (!file.exists())
			{
				file.mkdirs();
			}

			operateButton.setText(R.string.use_path);
		} else
		{
			operateButton.setText(R.string.back_root);
		}

		if (null == path || path.length() == 0)
		{
			path = rootPath;
		}

		fileAdapter.setFileList(new File(path).listFiles(),
				SettingsUtils.getDisplayHiddenFile());

		if (path.length() == rootPath.length())
		{
			pathTextView.setText("/" + getString(R.string.sd_card));
		} else
		{
			pathTextView.setText("/" + getString(R.string.sd_card)
					+ path.substring(rootPath.length(), path.length()));
		}

		fileListView.setAdapter(fileAdapter);
		fileListView.setOnItemClickListener(this);
		operateButton.setOnClickListener(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		getMenuInflater().inflate(R.menu.menu_recovery, menu);

		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu)
	{
		MenuItem menuItem = menu.findItem(R.id.action_recovery_display_hidden);
		if (SettingsUtils.getDisplayHiddenFile())
		{
			menuItem.setTitle("☑  " + getString(R.string.display_hidden_file));
		} else
		{
			menuItem.setTitle("       "
					+ getString(R.string.display_hidden_file));
		}

		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		if (R.id.action_recovery_display_hidden == item.getItemId())
		{
			SettingsUtils.setDisplayHiddenFile(!SettingsUtils
					.getDisplayHiddenFile());

			fileAdapter.setFileList(new File(path).listFiles(),
					SettingsUtils.getDisplayHiddenFile());
			fileAdapter.notifyDataSetChanged();
		}

		return super.onOptionsItemSelected(item);
	}

	// @Override
	// public boolean onPrepareOptionsMenu(Menu menu)
	// {
	// if (choosePath)
	// {
	// menu.setGroupVisible(R.id.menu_group_select_path, true);
	// menu.setGroupVisible(R.id.menu_group_open_file, false);
	// } else
	// {
	// menu.setGroupVisible(R.id.menu_group_select_path, false);
	// menu.setGroupVisible(R.id.menu_group_open_file, true);
	// }
	//
	// return super.onPrepareOptionsMenu(menu);
	// }

	// @Override
	// public boolean onOptionsItemSelected(MenuItem item)
	// {
	// switch (item.getItemId())
	// {
	// case R.id.action_recovery_use_path:
	// if ((FileUtil.handleRootFile().length == 1 && path.length() >= rootPath
	// .length()) || (path.length() > rootPath.length()))
	// {
	// Intent intent = new Intent("COMPLETE");
	// intent.putExtra("path", path);
	// setResult(0, intent);
	//
	// super.onBackPressed();
	//
	// overridePendingTransition(R.anim.push_left_in,
	// R.anim.push_right_out);
	// }
	// break;
	//
	// case R.id.action_recovery_back_root:
	// backRoot();
	// break;
	//
	// default:
	// break;
	// }
	//
	// return super.onOptionsItemSelected(item);
	// }

	/**
	 * 返回根目录
	 */
	private void backRootPath()
	{
		if (!rootPath.equals(path))
		{
			path = rootPath;
			fileAdapter.setFileList(Environment.getExternalStorageDirectory()
					.listFiles(), SettingsUtils.getDisplayHiddenFile());
			fileAdapter.notifyDataSetChanged();

			pathTextView.setText("/" + getString(R.string.sd_card));
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id)
	{
		File file = fileAdapter.getFileList().get(position);
		if (file.isDirectory())
		{
			path = file.getAbsolutePath();
			fileAdapter.setFileList(file.listFiles(),
					SettingsUtils.getDisplayHiddenFile());
			fileAdapter.notifyDataSetChanged();

			fileListView.setSelection(0);

			if (path.length() == rootPath.length())
			{
				pathTextView.setText("/" + getString(R.string.sd_card));
			} else
			{
				pathTextView.setText("/" + getString(R.string.sd_card)
						+ path.substring(rootPath.length(), path.length()));
			}
		} else if (!choosePath)
		{
			// String filePath = fileAdapter.getFileList().get(position)
			// .getAbsolutePath();
			String filePath = file.getAbsolutePath();
			if (filePath.toLowerCase(Locale.getDefault()).endsWith(
					Constants.FILE_SUFFIX))
			{
				readFile(filePath);
			}
		}
	}

	private void readFile(String filePath)
	{
		try
		{
			recoveryJsonReader = new JsonReader(new FileReader(new File(
					filePath)));
			recoveryJsonReader.beginObject();
			recoveryJsonReader.nextName();
			encryptFilePassword = recoveryJsonReader.nextString();

			inputDialog = new InputDialog(RecoveryActivity.this, 0,
					"请输入文件恢复密码", R.string.sure, R.string.cancel);
			inputDialog.setInputDialogOnClickListener(this);
			inputDialog.show();

			recoveryJsonReader.nextName();
		} catch (FileNotFoundException e)
		{
			toastMakeText(getString(R.string.recovery_failed));
		} catch (IOException e)
		{
			toastMakeText(getString(R.string.recovery_failed));
		} catch (Exception e)
		{
			toastMakeText(getString(R.string.recovery_failed));
		}
	}

	@Override
	public void onInputDialogClick(AlertDialog alertDialog, int requestCode,
			boolean positive, String inputText)
	{
		if (positive)
		{
			if (inputText.length() >= 6 && inputText.length() <= 16)
			{
				inputFilePassword = inputText;
				if (EncryptUtils.encryptSHA256(inputFilePassword).equals(
						encryptFilePassword))
				{
					inputDialog.dismiss();
					progressDialog = new ProgressDialog(RecoveryActivity.this);
					progressDialog.setMessage("恢复中...");
					progressDialog.setCancelable(false);
					progressDialog.show();

					RecoveryDataTask recoveryDataTask = new RecoveryDataTask(
							this, recoveryJsonReader, inputFilePassword);
					recoveryDataTask
							.setRecoveryDataTaskListener(new RecoveryDataTaskListener()
							{
								@Override
								public void onRecoveryComplete(boolean success,
										String message)
								{
									progressDialog.dismiss();

									toastMakeText(message);

									if (success)
									{
										Intent intent = new Intent(
												ReturnCode.COMPLETE);
										setResult(ReturnCode.SUCCESS, intent);

										onFinish();
									}
								}

							});

					recoveryDataTask.execute();
				} else
				{
					toastMakeText(R.string.wrong_password);
				}
			} else
			{
				toastMakeText(R.string.password_at_least_6_pleace);
			}
		}
	}

	private void onFinish()
	{
		super.onBackPressed();

		overridePendingTransition(R.anim.push_left_in, R.anim.push_right_out);
	}

	@Override
	public void onBackPressed()
	{
		if (path.equals(rootPath))
		{
			Intent intent = new Intent(ReturnCode.CANCEL);
			setResult(ReturnCode.SUCCESS, intent);

			super.onBackPressed();

			overridePendingTransition(R.anim.push_left_in,
					R.anim.push_right_out);
		} else
		{
			String temp = path.substring(0, path.lastIndexOf("/"));

			if (temp.equals(rootPath))
			{
				fileAdapter.setFileList(Environment
						.getExternalStorageDirectory().listFiles(),
						SettingsUtils.getDisplayHiddenFile());
			} else
			{
				fileAdapter.setFileList(new File(temp).listFiles(),
						SettingsUtils.getDisplayHiddenFile());
			}

			fileAdapter.notifyDataSetChanged();
			path = temp;

			if (path.length() == rootPath.length())
			{
				pathTextView.setText("/" + getString(R.string.sd_card));
			} else
			{
				pathTextView.setText("/" + getString(R.string.sd_card)
						+ path.substring(rootPath.length(), path.length()));
			}
		}
	}

	@Override
	public void onClick(View view)
	{
		switch (view.getId())
		{
		case R.id.bt_recovery_operate:
			if (choosePath)
			{
				if (path.length() >= rootPath.length())
				{
					Intent intent = new Intent(ReturnCode.COMPLETE);
					intent.putExtra("path", path);
					setResult(ReturnCode.SUCCESS, intent);

					super.onBackPressed();

					overridePendingTransition(R.anim.push_left_in,
							R.anim.push_right_out);
				}
			} else
			{
				backRootPath();
			}
			break;
		default:
			break;
		}
	}

}
