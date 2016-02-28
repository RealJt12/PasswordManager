package com.realjt.meizu.passwordmanager.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.realjt.meizu.passwordmanager.R;
import com.realjt.meizu.passwordmanager.common.Constants;
import com.realjt.meizu.passwordmanager.utils.LogUtils;
import com.realjt.meizu.passwordmanager.view.PromptDialog;

/**
 * 关于Activity
 * 
 * @author Administrator
 * 
 */
public class AboutActivity extends BaseActivity implements OnClickListener
{
	/**
	 * 提示对话框
	 */
	private PromptDialog promptDialog;

	/**
	 * 版本描述
	 */
	private TextView editionDescription;

	/**
	 * 使用说明,软件订制,支持作者
	 */
	private RelativeLayout useExplainLayout, privateOrderLayout,
			supportAuthorLayout;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_about);

		editionDescription = (TextView) findViewById(R.id.tv_about_edition_description);
		useExplainLayout = (RelativeLayout) findViewById(R.id.rl_about_use_explain);
		useExplainLayout.setOnClickListener(this);
		privateOrderLayout = (RelativeLayout) findViewById(R.id.rl_about_private_order);
		privateOrderLayout.setOnClickListener(this);
		supportAuthorLayout = (RelativeLayout) findViewById(R.id.rl_about_support_author);
		supportAuthorLayout.setOnClickListener(this);

		initView();
	}

	private void initView()
	{
		String editionText;
		switch (Constants.EDITION)
		{
		case 0:
			editionText = getString(R.string.no_authentication);
			break;

		case 1:
			editionText = getString(R.string.trial_version);
			break;

		case 3:
			editionText = getString(R.string.authentication_failed);
			break;

		default:
			editionText = "";
			break;
		}

		if (null != editionText && !"".equals(editionText))
		{
			editionDescription.setText(getString(R.string.edition_content)
					+ "\n(" + editionText + ")");
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		getMenuInflater().inflate(R.menu.menu_about, menu);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{
		case R.id.action_about_share:
			share();
			break;

		default:
			break;
		}

		return super.onOptionsItemSelected(item);
	}

	private void share()
	{
		Intent intent = new Intent(Intent.ACTION_SEND);
		intent.setType("text/plain");
		intent.putExtra(Intent.EXTRA_TEXT, getString(R.string.share_content));
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(Intent.createChooser(intent, getString(R.string.share)));
	}

	@Override
	public void onClick(View view)
	{
		switch (view.getId())
		{
		case R.id.rl_about_use_explain:
			// 使用说明
			showUseExplain();
			break;

		case R.id.rl_about_private_order:
			// 软件订制
			showOrderSoftware();
			break;

		case R.id.rl_about_support_author:
			enterStore();
			break;

		default:
			break;
		}
	}

	/**
	 * 展示使用说明提示框
	 */
	private void showUseExplain()
	{
		promptDialog = new PromptDialog(this, 0, R.string.use_explain,
				R.string.use_explain_content, R.string.sure, 0);
		promptDialog.setCancelable(false);
		promptDialog.show();
	}

	/**
	 * 私人定制
	 */
	private void showOrderSoftware()
	{
		promptDialog = new PromptDialog(this, 0, R.string.private_order,
				R.string.private_order_content, R.string.sure, 0);
		promptDialog.setCancelable(true);
		promptDialog.show();
	}

	/**
	 * 进入应用商店展示应用详情
	 */
	private void enterStore()
	{
		try
		{
			startActivity(new Intent("android.intent.action.VIEW",
					Uri.parse(Constants.APK_IN_STORE_ADDRESS)));

			overridePendingTransition(R.anim.push_right_in,
					R.anim.push_left_out);

			LogUtils.debug("enter meizu appstore, application exit");

			Intent intent = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
			intent.putExtra("reason", "switch");

			sendBroadcast(intent);
		} catch (Exception e)
		{
			// 未安装魅族应用商店
			toastMakeText(R.string.uninstall_meizuz_app_store);
		}
	}

	@Override
	public void onBackPressed()
	{
		super.onBackPressed();

		overridePendingTransition(R.anim.push_left_in, R.anim.push_right_out);
	}

}