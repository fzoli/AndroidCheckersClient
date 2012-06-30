package org.dyndns.fzoli.mill.android;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootIntentFilter extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		Intent serviceIntent = new Intent();
		serviceIntent.setAction("org.dyndns.fzoli.mill.UPDATE_URL");
		context.startService(serviceIntent);
	}

}