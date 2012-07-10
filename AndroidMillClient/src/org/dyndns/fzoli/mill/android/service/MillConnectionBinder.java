package org.dyndns.fzoli.mill.android.service;

import java.util.HashMap;
import java.util.Map;

import org.dyndns.fzoli.mill.android.MillConnectionService;
import org.dyndns.fzoli.mill.android.entity.UserInfo;
import org.dyndns.fzoli.mvc.client.android.service.ConnectionBinder;

import android.graphics.Bitmap;

public class MillConnectionBinder extends ConnectionBinder<Object, Object> {

	public enum LoginMode {
		SIGNED_OUT, SIGN_IN_FAILED, SIGNED_IN, SIGNING_IN, SIGNING_OUT, ERROR, KICKED, INVISIBLE_ERROR
	}
	
	private LoginMode loginMode = LoginMode.SIGNED_OUT;
	private UserInfo userInfo = new UserInfo();
	
	private final Map<String, String> VARS = new HashMap<String, String>();
	public final Map<String, Bitmap> BITMAPS = new HashMap<String, Bitmap>();
	
	private Bitmap avatarImage;
	
	public MillConnectionBinder(MillConnectionService service) {
		super(service);
	}
	
	@Override
	protected MillConnectionService getService() {
		return (MillConnectionService) super.getService();
	}
	
	public Map<String, String> getVars() {
		return VARS;
	}
	
	public Bitmap getAvatarImage() {
		return avatarImage;
	}
	
	public void setAvatarImage(Bitmap avatarImage) {
		this.avatarImage = avatarImage;
	}
	
	public MillDatabaseHelper getDatabaseHelper() {
		return getService().getDatabaseHelper();
	}
	
	public void reinitDatabaseHelper() {
		getService().closeHelper();
		getService().initHelper();
	}
	
	public UserInfo getUserInfo() {
		return userInfo;
	}
	
	public void setUserInfo(UserInfo userInfo) {
		this.userInfo = userInfo;
	}
	
	public LoginMode getLoginMode() {
		return loginMode;
	}
	
	public void setLoginMode(LoginMode loginMode) {
		this.loginMode = loginMode;
	}
	
}