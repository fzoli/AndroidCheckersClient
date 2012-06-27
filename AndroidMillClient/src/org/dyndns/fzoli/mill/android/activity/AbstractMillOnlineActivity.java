package org.dyndns.fzoli.mill.android.activity;

import org.dyndns.fzoli.mill.android.service.MillConnectionBinder.LoginMode;
import org.dyndns.fzoli.mill.common.model.pojo.BaseOnlinePojo;

public abstract class AbstractMillOnlineActivity<EventObj extends BaseOnlinePojo, PropsObj extends BaseOnlinePojo> extends AbstractMillModelActivity<EventObj, PropsObj> {
	
	@Override
	public boolean processModelChange(EventObj e) {
		return processModel(getContextUtil(), e);
	}

	@Override
	public boolean processModelData(PropsObj e) {
		return processModel(getContextUtil(), e);
	}
	
	public static <EventObj extends BaseOnlinePojo, PropsObj extends BaseOnlinePojo> boolean processModel(MillModelActivityUtil<EventObj, PropsObj> util, BaseOnlinePojo e) {
		if (e.getPlayerName() == null) { // ha valami miatt nincs bejelentkezve a felhasználó (pl. szerver restart vagy másik munkamenetben való bejelentkezés)
			if (util.getConnectionBinder() != null) util.getConnectionBinder().setLoginMode(LoginMode.KICKED); //kidobás detektálás közlése
			util.openHome(); // kezdőablak megnyitása, hogy pucoljon és bejelentkezőt nyisson ami jelzi az eseményt
			return false;
		}
		return true;
	}
	
}