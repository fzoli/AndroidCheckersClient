package org.dyndns.fzoli.mill.android.entity;

import org.dyndns.fzoli.http.HttpUrl;
import org.dyndns.fzoli.mill.common.InputValidator;

public class UserInfo {
	
	private HttpUrl server;
	private int length = 0;
	private String user = "", password = "";
	
	public HttpUrl getServer() {
		return server;
	}
	
	public void setServer(HttpUrl server) {
		this.server = server;
	}
	
	public String getUser() {
		return user;
	}
	
	public int getPasswordLength() {
		return length;
	}
	
	public String getPasswordHash() {
		return password;
	}
	
	public void setUser(String user) {
		this.user = user;
	}
	
	public void setPassword(String passwordHash, int length) {
		this.password = passwordHash;
		this.length = length;
	}
	
	public void setPassword(String password) {
		this.length = password.length();
		if (password.isEmpty()) this.password = "";
		else this.password = InputValidator.md5Hex(password);
	}
	
	@Override
	public String toString() {
		return user + " (length " + length + ')';
	}
	
}