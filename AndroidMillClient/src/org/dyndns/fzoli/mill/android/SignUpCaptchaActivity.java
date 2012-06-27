package org.dyndns.fzoli.mill.android;

import org.dyndns.fzoli.mill.android.activity.AbstractCaptchaActivity;
import org.dyndns.fzoli.mill.client.model.PlayerBuilderModel;
import org.dyndns.fzoli.mill.common.model.pojo.PlayerBuilderData;
import org.dyndns.fzoli.mill.common.model.pojo.PlayerBuilderEvent;
import org.dyndns.fzoli.mvc.client.android.activity.ConnectionActivity;
import org.dyndns.fzoli.mvc.client.connection.Connection;

public class SignUpCaptchaActivity extends AbstractCaptchaActivity<PlayerBuilderEvent, PlayerBuilderData> {

	@Override
	public PlayerBuilderModel createModel(Connection<Object, Object> connection) {
		return new PlayerBuilderModel(connection);
	}
	
	@Override
	public PlayerBuilderModel getModel() {
		return (PlayerBuilderModel) super.getModel();
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public Class<? extends ConnectionActivity> getClassKey() {
		return SignUpActivity.class;
	}
	
	@Override
	public void onBackPressed() {
		getContextUtil().openSignIn();
	}
	
	@Override
	protected void onCaptchaValidate(boolean ok) {
		if (ok) {
			getModel().validate();
			getModelMap().free(getClassKey());
		}
		super.onCaptchaValidate(ok);
	}
	
}