package org.dyndns.fzoli.android.context.activity;

import org.dyndns.fzoli.android.context.NetworkInfoContextUtil;
import org.dyndns.fzoli.android.context.NetworkInfoListener;

public class NetworkInfoActivityUtil extends NetworkInfoContextUtil implements NetworkInfoActivity {

	private final NetworkInfoListener NETWORK_INFO_LISTENER = new NetworkInfoListener() {
		
		@Override
		public void onNetworkChanged(final boolean replaced) {
			runOnUiThread(new Runnable() {
				
				@Override
				public void run() {
					NetworkInfoActivityUtil.this.onNetworkChanged(replaced);
				}
				
			});
		}
		
	};
	
	public NetworkInfoActivityUtil(NetworkInfoActivity context) {
		super(context);
	}
	
	@Override
	protected NetworkInfoActivity getContext() {
		return (NetworkInfoActivity) super.getContext();
	}
	
	/* Alapértelmezett Activity metódusok */
	
	@Override
	public void runOnUiThread(Runnable action) {
		getContext().runOnUiThread(action);
	}
	
	/* Felüldefiniálásban ős hívás előtt meghívandó Activity metódusok */
	
	@Override
	public void onPause() {
		setNetworkInfoListener(false);
	}
	
	/* Felüldefiniálásban ős hívás után meghívandó Activity metódusok */

	@Override
	public void onResume() {
		setNetworkInfoListener(true);
	}
	
	/* Kiegészítő hívó Activity eseménykezelő-metódusok */
	
	@Override
	public void onNetworkChanged(boolean replaced) {
		getContext().onNetworkChanged(replaced);
	}
	
	/* Rejtett metódusok */
	
	protected void setNetworkInfoListener(boolean enable) {
    	if (enable) addNetworkInfoListener(NETWORK_INFO_LISTENER);
    	else removeNetworkInfoListener(NETWORK_INFO_LISTENER);
    }
	
}