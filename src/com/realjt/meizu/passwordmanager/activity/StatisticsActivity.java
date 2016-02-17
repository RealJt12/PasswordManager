package com.realjt.meizu.passwordmanager.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.realjt.meizu.passwordmanager.R;
import com.realjt.meizu.passwordmanager.db.PasswordDatabaseHelper;
import com.realjt.meizu.passwordmanager.model.Classification;
import com.realjt.meizu.passwordmanager.view.ProgressDialog;
import com.realjt.meizu.passwordmanager.view.PromptDialog;
import com.realjt.meizu.passwordmanager.view.PromptDialog.PromptDialogOnClickListener;

/**
 * 数据统计Activity
 * 
 * @author Administrator
 * 
 */
public class StatisticsActivity extends BaseActivity implements
		OnClickListener, PromptDialogOnClickListener
{
	private PasswordDatabaseHelper databaseHelper;

	private TextView statisticsNames, statisticsCount, statisticsCountAll;

	private List<String> counts = new ArrayList<String>();

	private String countAll;

	private Button clearAllDataButton;

	private ProgressDialog progressDialog;

	private PromptDialog promptDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_statistics);

		databaseHelper = PasswordDatabaseHelper.getInstance(this);

		statisticsNames = (TextView) findViewById(R.id.tv_statistics_names);
		statisticsCount = (TextView) findViewById(R.id.tv_statistics_count);
		statisticsCountAll = (TextView) findViewById(R.id.tv_statistics_count_all);

		clearAllDataButton = (Button) findViewById(R.id.bt_statistics_clear_all_data);

		initView();
	}

	private void initView()
	{
		Classification[] classifications = Classification.values();
		String names = "";
		for (int i = 1; i < classifications.length; i++)
		{
			names += getString(classifications[i].getDescription()) + "\n\n";
		}
		names += getString(classifications[0].getDescription());

		statisticsNames.setText(names);

		clearAllDataButton.setOnClickListener(this);

		new GetCountDataTask().execute();
	}

	private class GetCountDataTask extends AsyncTask<Void, Void, Void>
	{
		@Override
		protected void onPreExecute()
		{
			super.onPreExecute();

			progressDialog = new ProgressDialog(StatisticsActivity.this);
			progressDialog.setMessage("处理中...");
			progressDialog.show();
		}

		@Override
		protected Void doInBackground(Void... params)
		{
			countAll = String.valueOf(databaseHelper.getPasswordCount());

			Classification[] classifications = Classification.values();
			for (int i = 1; i < classifications.length; i++)
			{
				counts.add(String.valueOf(databaseHelper
						.getPasswordCountByClassification(classifications[i]
								.getClassification())));
			}
			counts.add(String.valueOf(databaseHelper
					.getPasswordCountByClassification(Classification.OTHER
							.getClassification())));

			return null;
		}

		@Override
		protected void onPostExecute(Void result)
		{
			super.onPostExecute(result);

			StringBuilder stringBuilder = new StringBuilder();
			for (String string : counts)
			{
				stringBuilder.append(string + "\n\n");
			}
			statisticsCount.setText(stringBuilder);
			statisticsCountAll.setText(countAll);

			progressDialog.dismiss();
		}

	}

	@Override
	public void onClick(View view)
	{
		switch (view.getId())
		{
		case R.id.bt_statistics_clear_all_data:
			promptDialog = new PromptDialog(this, 0, "确定删除全部数据?", "此操作不可恢复!",
					R.string.sure, R.string.cancel);
			promptDialog.setPromptDialogOnClickListener(this);
			promptDialog.show();
			break;

		default:
			break;
		}
	}

	@Override
	public void onBackPressed()
	{
		super.onBackPressed();

		overridePendingTransition(R.anim.push_left_in, R.anim.push_right_out);
	}

	@Override
	public void onPromptDialogClick(AlertDialog alertDialog, int requestCode,
			boolean positive)
	{
		if (positive)
		{
			databaseHelper.deleteAllPassword();
			counts.clear();

			new GetCountDataTask().execute();
		}
	}

}
