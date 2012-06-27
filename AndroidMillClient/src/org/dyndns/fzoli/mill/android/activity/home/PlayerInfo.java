package org.dyndns.fzoli.mill.android.activity.home;

import android.graphics.Bitmap;

public class PlayerInfo {
	
	public enum Status {
		ONLINE, OFFLINE, BLOCKED, INVISIBLE
	}
	
	private String playerName, name, group;
	private Bitmap avatar;
	private Status status;
	
	public PlayerInfo(Status status, String playerName, String name, String group, Bitmap avatar) {
		this.playerName = playerName;
		this.name = name;
		this.group = group;
		this.avatar = avatar;
		this.status = status;
	}
	
	public String getGroup() {
		return group;
	}
	
	public String getName() {
		return name;
	}
	
	public String getPlayerName() {
		return playerName;
	}
	
	public Status getStatus() {
		return status;
	}
	
	public Bitmap getAvatar() {
		return avatar;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public void setAvatar(Bitmap avatar) {
		this.avatar = avatar;
	}
	
	public void setGroup(String group) {
		this.group = group;
	}
	
	public void setStatus(Status status) {
		this.status = status;
	}
	
}