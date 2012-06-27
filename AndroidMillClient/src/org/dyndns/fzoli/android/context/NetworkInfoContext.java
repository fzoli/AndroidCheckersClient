package org.dyndns.fzoli.android.context;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.NetworkInfo;

public interface NetworkInfoContext {
	
	/* ContextWrapper metódusok */
	
	Object getSystemService(String name);
	
	Intent registerReceiver(BroadcastReceiver receiver, IntentFilter filter);
	
	void unregisterReceiver(BroadcastReceiver receiver);
	
	/* Kiegészítő metódusok */
	
	boolean isNetworkAvailable();
	
	NetworkInfo getActiveNetworkInfo();
	
	void addNetworkInfoListener(NetworkInfoListener l);
	
	void removeNetworkInfoListener(NetworkInfoListener l);
	
}