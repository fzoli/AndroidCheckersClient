package org.dyndns.fzoli.android.context;

import java.util.ArrayList;
import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetworkInfoContextUtil implements NetworkInfoContext {

	private NetworkInfo lastNetworkInfo;
	private boolean connectivityReceiverEnabled = false;
	
	private final NetworkInfoContext CONTEXT;
	private final List<NetworkInfoListener> NETWORK_INFO_LISTENERS = new ArrayList<NetworkInfoListener>();
	
	private final BroadcastReceiver CONNECTIVITY_RECEIVER = new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context c, Intent i) {
			NetworkInfo ni = getActiveNetworkInfo();
			boolean replaced = isNetworkReplaced(ni);
			synchronized (NETWORK_INFO_LISTENERS) {
				for (NetworkInfoListener l : NETWORK_INFO_LISTENERS) {
					l.onNetworkChanged(replaced);
				}
			}
			if (replaced) lastNetworkInfo = ni;
		}
		
    };
	
	private static final IntentFilter CONNECTIVITY_INTENT_FILTER = new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE");
	
	public NetworkInfoContextUtil(NetworkInfoContext context) {
		CONTEXT = context;
	}
	
	protected NetworkInfoContext getContext() {
		return CONTEXT;
	}
	
	@Override
	public Object getSystemService(String name) {
		return getContext().getSystemService(name);
	}
	
	@Override
	public Intent registerReceiver(BroadcastReceiver receiver, IntentFilter filter) {
		return getContext().registerReceiver(receiver, filter);
	}

	@Override
	public void unregisterReceiver(BroadcastReceiver receiver) {
		getContext().unregisterReceiver(receiver);
	}
	
	@Override
	public boolean isNetworkAvailable() {
		try {
			final NetworkInfo activeNetwork = getActiveNetworkInfo(true);
			return activeNetwork != null && activeNetwork.getState() == NetworkInfo.State.CONNECTED;
		}
		catch (Exception ex) {
			return true;
		}
	}
	
	@Override
	public NetworkInfo getActiveNetworkInfo() {
		return getActiveNetworkInfo(false);
	}
	
	@Override
	public void addNetworkInfoListener(NetworkInfoListener l) {
		synchronized (NETWORK_INFO_LISTENERS) {
			if (!NETWORK_INFO_LISTENERS.contains(l)) NETWORK_INFO_LISTENERS.add(l);
			if (NETWORK_INFO_LISTENERS.size() == 1) setConnectivityReceiver(true);
		}
	}

	@Override
	public void removeNetworkInfoListener(NetworkInfoListener l) {
		synchronized (NETWORK_INFO_LISTENERS) {
			if (NETWORK_INFO_LISTENERS.contains(l)) NETWORK_INFO_LISTENERS.remove(l);
			if (NETWORK_INFO_LISTENERS.isEmpty()) setConnectivityReceiver(false);
		}
	}

	protected boolean isNetworkReplaced() {
		return isNetworkReplaced(getActiveNetworkInfo());
	}
	
	protected boolean isNetworkReplaced(NetworkInfo networkInfo) {
		if (lastNetworkInfo == null && networkInfo == null) return false;
		if (lastNetworkInfo == null && networkInfo != null) return false;
		if (lastNetworkInfo != null && networkInfo == null) return false;
		return networkInfo.getType() != lastNetworkInfo.getType();
	}
	
	protected NetworkInfo getActiveNetworkInfo(boolean throwEx) {
		try {
			final ConnectivityManager conMgr =  (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
			return conMgr.getActiveNetworkInfo();
		}
		catch (RuntimeException ex) {
			/* Null or Permission Denied */
			if (throwEx) throw ex;
			return null;
		}
	}

	protected void setConnectivityReceiver(boolean enable) {
		if (enable && !isConnectivityReceiverEnabled()) {
			lastNetworkInfo = getActiveNetworkInfo();
			registerReceiver(CONNECTIVITY_RECEIVER, CONNECTIVITY_INTENT_FILTER);
		}
		if (!enable && isConnectivityReceiverEnabled()) {
			lastNetworkInfo = null;
			unregisterReceiver(CONNECTIVITY_RECEIVER);
		}
		connectivityReceiverEnabled = enable;
	}

	protected boolean isConnectivityReceiverEnabled() {
		return connectivityReceiverEnabled;
	}
	
}