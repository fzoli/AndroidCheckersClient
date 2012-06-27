package org.dyndns.fzoli.mill.android;

import org.dyndns.fzoli.mill.android.activity.AbstractCaptchaActivity;
import org.dyndns.fzoli.mill.client.model.PlayerModel;
import org.dyndns.fzoli.mill.common.model.entity.Player;
import org.dyndns.fzoli.mill.common.model.pojo.PlayerData;
import org.dyndns.fzoli.mill.common.model.pojo.PlayerEvent;
import org.dyndns.fzoli.mvc.client.android.activity.ConnectionActivity;
import org.dyndns.fzoli.mvc.client.connection.Connection;

public class PlayerSettingsCaptchaActivity extends AbstractCaptchaActivity<PlayerEvent, PlayerData> {
	
	@Override
	public PlayerModel createModel(Connection<Object, Object> connection) {
		return new PlayerModel(connection);
	}
	
	@Override
	public PlayerModel getModel() {
		return (PlayerModel) super.getModel();
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public Class<? extends ConnectionActivity> getClassKey() {
		return HomeActivity.class;
	}
	
	private void close() {
		HomeActivity.setSigningIn(getContextUtil(), false);
		getContextUtil().openHome();
	}
	
	@Override
	public void onBackPressed() {
		Player p = getModel().getCache().getPlayer();
		if (HomeActivity.isSigningIn(getContextUtil())) { // ha éppen bejelentkezés volt
			if (p != null) { // ha be van jelentkezve
				if (p.isValidated()) close(); // és validálva van, akkor kiléphet
			}
			else { // ha nincs bejelentkezve, kilépés
				close();
			}
		}
		else { // ha nem bejelentkezés volt, kilépés
			close();
		}
	}
	
	@Override
	protected void onCaptchaValidate(boolean ok) {
		if (ok) {
			HomeActivity.setSigningIn(getContextUtil(), false);
			getModel().getCache().setCaptchaValidated(true);
		}
		super.onCaptchaValidate(ok);
	}
	
}