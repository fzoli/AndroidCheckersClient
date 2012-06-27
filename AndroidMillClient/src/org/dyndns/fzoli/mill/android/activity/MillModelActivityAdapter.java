package org.dyndns.fzoli.mill.android.activity;

import org.dyndns.fzoli.mvc.client.event.ModelActionEvent;

public class MillModelActivityAdapter<T> {
	
	@SuppressWarnings("unchecked")
	public MillModelActivityAdapter(final MillModelActivity activity, final ModelActionEvent<T> e) {
		switch (e.getType()) {
			case ModelActionEvent.TYPE_EVENT:
				activity.runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						onEvent(e.getEvent());
					}
					
				});
				break;
			default:
				activity.onModelCreated(e);
		}
	}
	
	public void onEvent(T e) {
		;
	}
	
}