package org.dyndns.fzoli.android.context.activity;

import org.dyndns.fzoli.android.context.NetworkInfoContext;

public interface NetworkInfoActivity extends NetworkInfoContext {
	
	/* Activity metódusok */
	
	void runOnUiThread(Runnable action);
	
	/* Felüldefiniálandó Activity metódusok */
	
	void onPause();
	
	void onResume();
	
	/* Kiegészítő Activity eseménykezelő-metódus */
	
	void onNetworkChanged(boolean replaced);
	
}