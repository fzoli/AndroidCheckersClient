package org.dyndns.fzoli.mill.android.service;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;

public abstract class MillServiceConnection implements ServiceConnection {
	
	public abstract void onServiceConnected(ComponentName cn, MillConnectionBinder mcb);

	@Override
	public final void onServiceConnected(ComponentName paramComponentName, IBinder paramIBinder) {
		onServiceConnected(paramComponentName, (MillConnectionBinder)paramIBinder);
	}
	
	@Override
	public void onServiceDisconnected(ComponentName paramComponentName) {
		;
	}
	
}