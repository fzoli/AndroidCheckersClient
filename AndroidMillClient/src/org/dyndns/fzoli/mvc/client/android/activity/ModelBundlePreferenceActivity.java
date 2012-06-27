package org.dyndns.fzoli.mvc.client.android.activity;

import org.dyndns.fzoli.mill.android.activity.AbstractMillModelPreferenceActivity;
import org.dyndns.fzoli.mill.android.activity.MillModelActivityUtil;

public abstract class ModelBundlePreferenceActivity<EventObj, PropsObj> extends AbstractMillModelPreferenceActivity<EventObj, PropsObj> implements ModelBundleActivityHelper<EventObj, PropsObj> {

	@Override
	protected MillModelActivityUtil<EventObj, PropsObj> createContextUtil() {
		return ModelBundleActivity.createContextUtil(this);
	}
	
	@SuppressWarnings("unchecked")
	public abstract Class<? extends ConnectionActivity> getClassKey();
	
	@Override
	public boolean processModelChange(EventObj e) {
		return true;
	}

	@Override
	public boolean processModelData(PropsObj e) {
		return true;
	}
	
}
