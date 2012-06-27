package org.dyndns.fzoli.mvc.client.android.activity;

import org.dyndns.fzoli.mill.android.activity.AbstractMillModelActivity;
import org.dyndns.fzoli.mill.android.activity.MillModelActivity;
import org.dyndns.fzoli.mill.android.activity.MillModelActivityUtil;

interface ModelBundleActivityHelper<EventObj, PropsObj> extends MillModelActivity<EventObj, PropsObj>{
	
	@SuppressWarnings("unchecked")
	Class<? extends ConnectionActivity> getClassKey();
	
}

public abstract class ModelBundleActivity<EventObj, PropsObj> extends AbstractMillModelActivity<EventObj, PropsObj> implements ModelBundleActivityHelper<EventObj, PropsObj> {
	
	public static <EventObj, PropsObj> MillModelActivityUtil<EventObj, PropsObj> createContextUtil(final ModelBundleActivityHelper<EventObj, PropsObj> helper) {
		return new MillModelActivityUtil<EventObj, PropsObj>(helper) {
			
			@Override
			@SuppressWarnings("unchecked")
			protected Class<? extends ConnectionActivity> getClassKey() {
				return helper.getClassKey();
			}
			
		};
	}
	
	@Override
	protected MillModelActivityUtil<EventObj, PropsObj> createContextUtil() {
		return createContextUtil(this);
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