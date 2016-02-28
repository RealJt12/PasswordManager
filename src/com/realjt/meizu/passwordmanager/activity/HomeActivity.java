package com.realjt.meizu.passwordmanager.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.realjt.meizu.passwordmanager.R;
import com.realjt.meizu.passwordmanager.adapter.PasswordAdapter;
import com.realjt.meizu.passwordmanager.adapter.ViewPagerAdapter;
import com.realjt.meizu.passwordmanager.common.Constants;
import com.realjt.meizu.passwordmanager.db.PasswordDatabaseHelper;
import com.realjt.meizu.passwordmanager.model.Classification;
import com.realjt.meizu.passwordmanager.model.Password;
import com.realjt.meizu.passwordmanager.task.BackupDataTask;
import com.realjt.meizu.passwordmanager.task.BackupDataTask.BackupDataTaskListener;
import com.realjt.meizu.passwordmanager.utils.LogUtils;
import com.realjt.meizu.passwordmanager.utils.SettingsUtils;
import com.realjt.meizu.passwordmanager.view.InputDialog;
import com.realjt.meizu.passwordmanager.view.InputDialog.OnInputDialogOnClickListener;
import com.realjt.meizu.passwordmanager.view.ProgressDialog;
import com.realjt.meizu.passwordmanager.view.ScrollTabView;

/**
 * 首页Activity
 * 
 * @author Administrator
 */
public class HomeActivity extends BaseActivity implements OnClickListener,
		OnItemClickListener, OnInputDialogOnClickListener,
		BackupDataTaskListener, OnPageChangeListener, OnItemLongClickListener,
		OnLongClickListener
{
	private ScrollTabView scrollTabView;

	private ViewPager viewPager;

	private ViewPagerAdapter viewPagerAdapter;

	private TextView tabScrollText0, tabScrollText1, tabScrollText2,
			tabScrollText3;

	private ListView listView0, listView1, listView2, listView3;

	private PasswordAdapter listAdapter0, listAdapter1, listAdapter2,
			listAdapter3;

	private PasswordDatabaseHelper databaseHelper;

	private ProgressDialog progressDialog;

	private InputDialog inputDialog;

	private boolean firstLoading = true;

	private boolean isItemClickable = true;

	private Button addButton;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);

		initActionbarView();

		viewPager = (ViewPager) findViewById(R.id.vp_home_pager);
		// 缓存个数
		viewPager.setOffscreenPageLimit(4);
		databaseHelper = PasswordDatabaseHelper.getInstance(this);

		// 分割竖线
		viewPager.setPageMargin(2);
		viewPager
				.setPageMarginDrawable(android.R.drawable.divider_horizontal_bright);

		initView();
	}

	private void initActionbarView()
	{
		ActionBar actionBar = getActionBar();
		actionBar.setCustomView(R.layout.actionbar_home);

		actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		actionBar.setDisplayShowCustomEnabled(true);

		View customView = actionBar.getCustomView();

		tabScrollText0 = (TextView) customView
				.findViewById(R.id.tv_actionbar_home_tab_0);
		tabScrollText1 = (TextView) customView
				.findViewById(R.id.tv_actionbar_home_tab_1);
		tabScrollText2 = (TextView) customView
				.findViewById(R.id.tv_actionbar_home_tab_2);
		tabScrollText3 = (TextView) customView
				.findViewById(R.id.tv_actionbar_home_tab_3);

		tabScrollText0.setOnClickListener(this);
		tabScrollText1.setOnClickListener(this);
		tabScrollText2.setOnClickListener(this);
		tabScrollText3.setOnClickListener(this);

		scrollTabView = (ScrollTabView) customView
				.findViewById(R.id.st_actionbar_home_view);
		scrollTabView.setTotalNumber(4);
		scrollTabView.setColor(getResources().getColor(R.color.blue_actionbar));

		int width = View.MeasureSpec.makeMeasureSpec(0,
				View.MeasureSpec.UNSPECIFIED);
		int height = View.MeasureSpec.makeMeasureSpec(0,
				View.MeasureSpec.UNSPECIFIED);
		tabScrollText0.measure(width, height);

		scrollTabView.setWidth(tabScrollText0.getMeasuredWidth() * 4);
	}

	private void initView()
	{
		View tabView0 = getLayoutInflater().inflate(R.layout.viewpager_content,
				new RelativeLayout(this), false);
		View tabView1 = getLayoutInflater().inflate(R.layout.viewpager_content,
				new RelativeLayout(this), false);
		View tabView2 = getLayoutInflater().inflate(R.layout.viewpager_content,
				new RelativeLayout(this), false);
		View tabView3 = getLayoutInflater().inflate(R.layout.viewpager_content,
				new RelativeLayout(this), false);

		List<View> pagerViews = new ArrayList<View>();
		pagerViews.add(tabView0);
		pagerViews.add(tabView1);
		pagerViews.add(tabView2);
		pagerViews.add(tabView3);

		viewPagerAdapter = new ViewPagerAdapter();
		viewPagerAdapter.setPageViews(pagerViews);

		viewPager.setAdapter(viewPagerAdapter);
		viewPager.addOnPageChangeListener(this);

		listView0 = (ListView) tabView0
				.findViewById(R.id.lv_view_pager_content);
		listAdapter0 = new PasswordAdapter(this);
		listView0.setAdapter(listAdapter0);
		listView0.setTag(0);

		listView1 = (ListView) tabView1
				.findViewById(R.id.lv_view_pager_content);
		listAdapter1 = new PasswordAdapter(this);
		listView1.setAdapter(listAdapter1);
		listView1.setTag(1);

		listView2 = (ListView) tabView2
				.findViewById(R.id.lv_view_pager_content);
		listAdapter2 = new PasswordAdapter(this);
		listView2.setAdapter(listAdapter2);
		listView2.setTag(2);

		listView3 = (ListView) tabView3
				.findViewById(R.id.lv_view_pager_content);
		listAdapter3 = new PasswordAdapter(this);
		listView3.setAdapter(listAdapter3);
		listView3.setTag(3);

		new LoadDataTask().execute();

		listView0.setOnItemClickListener(this);
		listView0.setOnItemLongClickListener(this);
		listView1.setOnItemClickListener(this);
		listView1.setOnItemLongClickListener(this);
		listView2.setOnItemClickListener(this);
		listView2.setOnItemLongClickListener(this);
		listView3.setOnItemClickListener(this);
		listView3.setOnItemLongClickListener(this);

		addButton = (Button) findViewById(R.id.bt_home_add);
		addButton.setOnClickListener(this);
		addButton.setOnLongClickListener(this);
	}

	@Override
	public void onClick(View view)
	{
		switch (view.getId())
		{
		case R.id.tv_actionbar_home_tab_0:
			viewPager.setCurrentItem(0, true);
			break;

		case R.id.tv_actionbar_home_tab_1:
			viewPager.setCurrentItem(1, true);
			break;

		case R.id.tv_actionbar_home_tab_2:
			viewPager.setCurrentItem(2, true);
			break;

		case R.id.tv_actionbar_home_tab_3:
			viewPager.setCurrentItem(3, true);
			break;

		case R.id.bt_home_add:
			Intent intent = new Intent();
			intent.setClass(this, AddActivity.class);
			startActivityForResult(intent, 0);

			overridePendingTransition(R.anim.zoom_in_enter, R.anim.zoom_in_exit);
			break;

		default:
			break;
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		getMenuInflater().inflate(R.menu.menu_home, menu);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		Intent intent = new Intent();

		switch (item.getItemId())
		{
		case R.id.action_home_add:
			intent.setClass(this, AddActivity.class);
			startActivityForResult(intent, 0);

			overridePendingTransition(R.anim.push_right_in,
					R.anim.push_left_out);
			break;

		case R.id.action_home_search:
			intent.setClass(this, SearchActivity.class);
			startActivityForResult(intent, 0);

			overridePendingTransition(R.anim.push_bottom_in,
					R.anim.push_bottom_out);
			break;

		case R.id.action_home_settings:
			intent.setClass(this, SettingsActivity.class);
			startActivityForResult(intent, 0);

			overridePendingTransition(R.anim.push_right_in,
					R.anim.push_left_out);
			break;

		case R.id.action_home_recovery:
			intent.setClass(this, RecoveryActivity.class);
			intent.putExtra("path", SettingsUtils.getBackupSavePath());
			intent.putExtra("choosepath", false);
			startActivityForResult(intent, 0);

			overridePendingTransition(R.anim.push_right_in,
					R.anim.push_left_out);
			break;

		case R.id.action_home_backup:
			backup();
			break;

		default:
			break;
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode,
			Intent intent)
	{
		super.onActivityResult(requestCode, resultCode, intent);

		new LoadDataTask().execute();
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id)
	{
		if (isItemClickable)
		{
			Password password = null;

			switch ((Integer) parent.getTag())
			{
			case 0:
				password = listAdapter0.getPasswordList().get(position);
				break;

			case 1:
				password = listAdapter1.getPasswordList().get(position);
				break;

			case 2:
				password = listAdapter2.getPasswordList().get(position);
				break;

			case 3:
				password = listAdapter3.getPasswordList().get(position);
				break;

			default:
				return;
			}

			if (null != password && isItemClickable)
			{
				LogUtils.debug("click item " + password.getId()
						+ ", enter detailactivity");

				Intent intent = new Intent(this, DetailActivity.class);
				intent.putExtra("id", password.getId());
				startActivityForResult(intent, 0);
				overridePendingTransition(R.anim.zoom_in_enter,
						R.anim.zoom_in_exit);
			}
		}
	}

	private class LoadDataTask extends AsyncTask<String, Void, Void>
	{
		@Override
		protected void onPreExecute()
		{
			super.onPreExecute();

			if (firstLoading)
			{
				showProgressDialog("数据加载中...");
			}

			isItemClickable = false;
		}

		@Override
		protected Void doInBackground(String... params)
		{
			listAdapter0.setPasswordList(databaseHelper
					.queryAllSimplePassword());

			if (!firstLoading)
			{
				listAdapter1
						.setPasswordList(databaseHelper
								.querySimplePasswordByClassification(Classification.LIFE
										.getClassification()));
				listAdapter2
						.setPasswordList(databaseHelper
								.querySimplePasswordByClassification(Classification.WEBSITE
										.getClassification()));
				listAdapter3
						.setPasswordList(databaseHelper
								.querySimplePasswordByClassification(Classification.BANK
										.getClassification()));
			}

			return null;
		}

		@Override
		protected void onPostExecute(Void result)
		{
			super.onPostExecute(result);

			listAdapter0.notifyDataSetChanged();

			if (firstLoading)
			{
				progressDialog.dismiss();

				firstLoading = false;
				new LoadDataTask().execute();
			} else
			{
				listAdapter1.notifyDataSetChanged();
				listAdapter2.notifyDataSetChanged();
				listAdapter3.notifyDataSetChanged();
			}

			isItemClickable = true;
		}

	}

	private void showProgressDialog(String message)
	{
		progressDialog = new ProgressDialog(this);

		progressDialog.setMessage(message);
		progressDialog.show();
	}

	private void backup()
	{
		if (SettingsUtils.isUseLoginPasswordToBackup())
		{
			showProgressDialog("备份中...");

			BackupDataTask backupDataTask = new BackupDataTask(this,
					Constants.LOGIN_PASSWORD);
			backupDataTask.setBackupDataTaskListener(this);
			backupDataTask.execute();
		} else
		{
			inputDialog = new InputDialog(this, 0, "请输入备份密码", R.string.sure,
					R.string.cancel);
			inputDialog.setInputDialogOnClickListener(this);
			inputDialog.show();
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
				inputDialog.dismiss();

				showProgressDialog("备份中...");

				BackupDataTask backupDataTask = new BackupDataTask(this,
						inputText);
				backupDataTask.setBackupDataTaskListener(this);
				backupDataTask.execute();
			} else
			{
				toastMakeText("请输入6-16位密码");
			}
		}
	}

	@Override
	public void onBackupComplete(boolean success, String message)
	{
		progressDialog.dismiss();
		if (success)
		{
			toastMakeTextLong(message);
		} else
		{
			toastMakeText(message);
		}
	}

	@Override
	public void onPageScrollStateChanged(int state)
	{

	}

	@Override
	public void onPageScrolled(int position, float positionOffset,
			int positionOffsetPixels)
	{
		scrollTabView.notifyChanged(position, positionOffset);
	}

	@Override
	public void onPageSelected(int position)
	{
		setCurrentScroll(position);
	}

	private void setCurrentScroll(int selection)
	{
		tabScrollText0.setTextColor(selection == 0 ? getResources().getColor(
				R.color.blue_actionbar) : getResources().getColor(
				android.R.color.black));
		tabScrollText1.setTextColor(selection == 1 ? getResources().getColor(
				R.color.blue_actionbar) : getResources().getColor(
				android.R.color.black));
		tabScrollText2.setTextColor(selection == 2 ? getResources().getColor(
				R.color.blue_actionbar) : getResources().getColor(
				android.R.color.black));
		tabScrollText3.setTextColor(selection == 3 ? getResources().getColor(
				R.color.blue_actionbar) : getResources().getColor(
				android.R.color.black));
	}

	@Override
	public void onBackPressed()
	{
		super.onBackPressed();

		Constants.LOGIN_PASSWORD = null;
	}

	@Override
	protected void onDestroy()
	{
		super.onDestroy();

		Constants.LOGIN_PASSWORD = null;
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view,
			int position, long id)
	{
		if (isItemClickable)
		{
			Password password = null;

			switch ((Integer) parent.getTag())
			{
			case 0:
				password = listAdapter0.getPasswordList().get(position);
				break;

			case 1:
				password = listAdapter1.getPasswordList().get(position);
				break;

			case 2:
				password = listAdapter2.getPasswordList().get(position);
				break;

			case 3:
				password = listAdapter3.getPasswordList().get(position);
				break;

			default:
				return false;
			}

			if (null != password)
			{
				password = databaseHelper.queryPasswordById(password.getId());
				if (null != password)
				{
					NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

					NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
					inboxStyle.setBigContentTitle(password.getName());

					inboxStyle.addLine(password.getAccount());
					inboxStyle.addLine(password.getPassword());

					Notification notification = new NotificationCompat.Builder(
							this).setSmallIcon(R.drawable.icon)
							.setTicker(password.getName()).setAutoCancel(true)
							.setStyle(inboxStyle)
							.setDefaults(Notification.DEFAULT_LIGHTS).build();
					notificationManager.notify(0, notification);

					password = null;
				}
			}

			return true;
		} else
		{
			return false;
		}
	}

	@Override
	public boolean onLongClick(View view)
	{
		if (R.id.bt_home_add == view.getId())
		{
			Intent intent = new Intent();

			intent.setClass(this, SearchActivity.class);
			startActivityForResult(intent, 0);

			overridePendingTransition(R.anim.push_bottom_in,
					R.anim.push_bottom_out);
		}

		return true;
	}
}