package org.dyndns.fzoli.mill.android.entity;

public class UserInfoSettings {
	
	private String lastName = "";
	private boolean saveUser, savePassword;
	
	public UserInfoSettings() {
	}
	
	public UserInfoSettings(boolean saveUser, boolean savePassword) {
		this.saveUser = saveUser;
		this.savePassword = savePassword;
	}
	
	public String getLastName() {
		return lastName;
	}
	
	public boolean isSaveUser() {
		return saveUser;
	}
	
	public boolean isSavePassword() {
		return savePassword;
	}
	
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	
	public void setSaveUser(boolean saveUser) {
		this.saveUser = saveUser;
	}
	
	public void setSavePassword(boolean savePassword) {
		this.savePassword = savePassword;
	}

}