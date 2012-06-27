package org.dyndns.fzoli.mill.android;

import org.dyndns.fzoli.android.context.activity.AbstractNetworkInfoActivity;
import org.dyndns.fzoli.mill.android.R;
import org.dyndns.fzoli.mill.android.activity.MillModelActivityUtil;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class ConnectivityActivity extends AbstractNetworkInfoActivity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.connectivity);
		Button bt = (Button) findViewById(R.id.btNetworkSettings);
		bt.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View paramView) {
				startActivity(new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS));
			}
			
		});
	}
	
	@Override
	public void onResume() {
		super.onResume();
		if (isNetworkAvailable()) finish();
	}
	
	@Override
	public void onBackPressed() {
		MillModelActivityUtil.openSignIn(this);
	}
	
	@Override
	public void onNetworkChanged(boolean replaced) {
		if (isNetworkAvailable()) finish();
	}
	
}