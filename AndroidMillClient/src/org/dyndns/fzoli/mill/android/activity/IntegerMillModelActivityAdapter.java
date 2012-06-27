package org.dyndns.fzoli.mill.android.activity;

import org.dyndns.fzoli.mvc.client.event.ModelActionEvent;

public class IntegerMillModelActivityAdapter extends MillModelActivityAdapter<Integer> {

	@SuppressWarnings("unchecked")
	public IntegerMillModelActivityAdapter(MillModelActivity activity, ModelActionEvent<Integer> e) {
		super(activity, e);
	}
	
	public void onEvent(int e) {
		;
	}
	
	@Override
	public final void onEvent(Integer e) {
		onEvent((int)e);
	}
	
}