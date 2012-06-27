package org.dyndns.fzoli.mill.android.activity;

import org.dyndns.fzoli.mill.common.model.pojo.BaseOnlinePojo;
import org.dyndns.fzoli.mvc.client.android.activity.ModelBundlePreferenceActivity;

public abstract class AbstractMillOnlineBundlePreferenceActivity<EventObj extends BaseOnlinePojo, PropsObj extends BaseOnlinePojo> extends ModelBundlePreferenceActivity<EventObj, PropsObj> {

	@Override
	public boolean processModelChange(EventObj e) {
		return AbstractMillOnlineActivity.processModel(getContextUtil(), e);
	}

	@Override
	public boolean processModelData(PropsObj e) {
		return AbstractMillOnlineActivity.processModel(getContextUtil(), e);
	}

}