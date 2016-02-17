package com.realjt.meizu.passwordmanager.activity;

import android.app.ActionBar;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import com.realjt.meizu.passwordmanager.R;
import com.realjt.meizu.passwordmanager.adapter.PasswordAdapter;
import com.realjt.meizu.passwordmanager.db.PasswordDatabaseHelper;
import com.realjt.meizu.passwordmanager.model.Classification;
import com.realjt.meizu.passwordmanager.model.Password;

/**
 * 搜索Activity
 * 
 * @author Administrator
 * 
 */
public class SearchActivity extends BaseActivity implements OnClickListener,
		OnItemClickListener, OnItemLongClickListener
{
	private ListView listView;

	private PasswordAdapter homeAdapter;

	private PasswordDatabaseHelper databaseHelper;

	private EditText searchEditText;

	private ImageView searchInputClearimageView;

	// private MenuItem menuItem;

	private int classification = -1;

	private boolean isItemClickable = true;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search);

		listView = (ListView) findViewById(R.id.lv_search);
		databaseHelper = PasswordDatabaseHelper.getInstance(this);
		homeAdapter = new PasswordAdapter(this);
		listView.setAdapter(homeAdapter);

		listView.setOnItemClickListener(this);
		listView.setOnItemLongClickListener(this);

		initActionbarView();

		search(null);
	}

	private void initActionbarView()
	{
		getActionBar().setCustomView(R.layout.actionbar_search);

		getActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		getActionBar().setDisplayShowCustomEnabled(true);

		View view = getActionBar().getCustomView();

		searchEditText = (EditText) view.findViewById(R.id.et_search_input);
		searchEditText.addTextChangedListener(textWatcher);
		searchInputClearimageView = (ImageView) view
				.findViewById(R.id.iv_actionbar_search_clear);
		searchInputClearimageView.setOnClickListener(this);

		searchEditText.setFocusable(true);
		searchEditText.setFocusableInTouchMode(true);
		searchEditText.requestFocus();
	}

	private TextWatcher textWatcher = new TextWatcher()
	{
		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count)
		{
			search(s.toString());
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after)
		{
		}

		@Override
		public void afterTextChanged(Editable s)
		{
		}

	};

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		getMenuInflater().inflate(R.menu.menu_search, menu);

		// if (null == menuItem)
		// {
		// menuItem = menu.findItem(R.id.action_search_classification);
		// }

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		String menuTitle = item.getTitle().toString();

		int classificationTemp = -2;

		if (R.id.action_search_all == item.getItemId())
		{
			classificationTemp = -1;
		} else
		{
			classificationTemp = Classification.initClassification(this,
					menuTitle).getClassification();
		}

		if (classificationTemp != classification)
		{
			classification = classificationTemp;

			search(searchEditText.getText().toString());
		}

		return super.onOptionsItemSelected(item);
	}

	/**
	 * 模式匹配查找
	 * 
	 * @param name
	 *            账号名称
	 */
	private void search(String name)
	{
		new SearchTask().execute(name);
	}

	private class SearchTask extends AsyncTask<String, Void, Void>
	{
		@Override
		protected Void doInBackground(String... params)
		{
			if (null == params[0] || "".equals(params[0]))
			{
				homeAdapter.setPasswordList(databaseHelper
						.querySimplePasswordByClassification(classification));
			} else
			{
				homeAdapter.setPasswordList(databaseHelper.queryPasswordLike(
						params[0].toString(), classification));
			}

			return null;
		}

		@Override
		protected void onPostExecute(Void result)
		{
			super.onPostExecute(result);
			homeAdapter.notifyDataSetChanged();

			isItemClickable = true;
		}

	}

	@Override
	public void onClick(View view)
	{
		switch (view.getId())
		{
		case R.id.iv_actionbar_search_clear:
			if ("".equals(searchEditText.getText().toString()))
			{
				onBackPressed();
			} else
			{
				searchEditText.setText("");
			}
			break;

		default:
			break;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id)
	{
		if (isItemClickable)
		{
			Password password = homeAdapter.getPasswordList().get(position);

			Intent intent = new Intent(this, DetailActivity.class);
			intent.putExtra("id", password.getId());

			startActivityForResult(intent, 0);

			overridePendingTransition(R.anim.zoom_in_enter, R.anim.zoom_in_exit);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode,
			Intent intent)
	{
		super.onActivityResult(requestCode, resultCode, intent);
		searchEditText.setFocusable(true);
		searchEditText.setFocusableInTouchMode(true);
		searchEditText.requestFocus();

		isItemClickable = false;

		search(searchEditText.getText().toString());

		// InputMethodManager inputMethodManager = (InputMethodManager)
		// getSystemService(Context.INPUT_METHOD_SERVICE);
		// inputMethodManager.toggleSoftInput(0,
		// InputMethodManager.HIDE_NOT_ALWAYS);
	}

	@Override
	public void onBackPressed()
	{
		super.onBackPressed();

		overridePendingTransition(R.anim.push_top_in, R.anim.push_top_out);
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view,
			int position, long id)
	{
		if (isItemClickable)
		{
			Password password = homeAdapter.getPasswordList().get(position);

			password = databaseHelper.queryPasswordById(password.getId());

			NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

			NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
			inboxStyle.setBigContentTitle(password.getName());

			inboxStyle.addLine(password.getAccount());
			inboxStyle.addLine(password.getPassword());

			Notification notification = new NotificationCompat.Builder(this)
					.setSmallIcon(R.drawable.icon)
					.setTicker(password.getName()).setAutoCancel(true)
					.setStyle(inboxStyle)
					.setDefaults(Notification.DEFAULT_LIGHTS).build();
			notificationManager.notify(0, notification);

			password = null;
			return true;
		} else
		{
			return false;
		}
	}

}
