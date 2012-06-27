package org.dyndns.fzoli.android.context.activity;

import org.dyndns.fzoli.android.context.NetworkInfoListener;

import android.app.ExpandableListActivity;
import android.net.NetworkInfo;

public abstract class AbstractNetworkInfoExpandableListActivity extends ExpandableListActivity implements NetworkInfoActivity {

	private final NetworkInfoActivityUtil UTIL = createContextUtil();
	
	protected NetworkInfoActivityUtil getContextUtil() {
		return UTIL;
	}
	
	protected NetworkInfoActivityUtil createContextUtil() {
		return new NetworkInfoActivityUtil(this);
	}
	
	/* Felüldefiniált Activity metódusok */
	
	@Override
	public void onPause() {
		getContextUtil().onPause();
		super.onPause();
	}
	
	@Override
	public void onResume() {
		super.onResume();
		getContextUtil().onResume();
	}
	
	/* Kiegészítő Context metódusok */
	
	@Override
	public NetworkInfo getActiveNetworkInfo() {
		return getContextUtil().getActiveNetworkInfo();
	}

	@Override
	public boolean isNetworkAvailable() {
		return getContextUtil().isNetworkAvailable();
	}
	
	@Override
	public void addNetworkInfoListener(NetworkInfoListener l) {
		getContextUtil().addNetworkInfoListener(l);
	}
	
	@Override
	public void removeNetworkInfoListener(NetworkInfoListener l) {
		getContextUtil().removeNetworkInfoListener(l);
	}
	
	/* Kiegészítő Activity eseménykezelő-metódusok */
	
	@Override
	public void onNetworkChanged(boolean replaced) {
		;
	}
	
}