package org.dyndns.fzoli.android.context.service;

import org.dyndns.fzoli.android.context.NetworkInfoContext;
import org.dyndns.fzoli.android.context.NetworkInfoContextUtil;
import org.dyndns.fzoli.android.context.NetworkInfoListener;

import android.app.Service;
import android.net.NetworkInfo;

public abstract class AbstractNetworkInfoService extends Service implements NetworkInfoContext {

	private final NetworkInfoContextUtil UTIL = createContextUtil();
	
	protected NetworkInfoContextUtil getContextUtil() {
		return UTIL;
	}
	
	protected NetworkInfoContextUtil createContextUtil() {
		return new NetworkInfoContextUtil(this);
	}
	
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

}