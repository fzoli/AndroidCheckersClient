package org.dyndns.fzoli.mill.android.activity;

import org.dyndns.fzoli.mill.common.model.pojo.BaseOnlinePojo;

public abstract class AbstractMillOnlineExpandableListActivity<EventObj extends BaseOnlinePojo, PropsObj extends BaseOnlinePojo> extends AbstractMillModelExpandableListActivity<EventObj, PropsObj>{

	@Override
	public boolean processModelChange(EventObj e) {
		return AbstractMillOnlineActivity.processModel(getContextUtil(), e);
	}

	@Override
	public boolean processModelData(PropsObj e) {
		return AbstractMillOnlineActivity.processModel(getContextUtil(), e);
	}

}