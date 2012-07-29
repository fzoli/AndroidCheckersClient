package org.dyndns.fzoli.mill.android.activity.home;

public class PlayerInfo {
	
	public enum Status {
		ONLINE, OFFLINE, BLOCKED, INVISIBLE
	}
	
	private int count;
	private Status status;
	private String playerName, name, group;
	
	public PlayerInfo(Status status, String playerName, String name, String group, int count) {
		this.playerName = playerName;
		this.name = name;
		this.group = group;
		this.count = count;
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
	
	public int getCount() {
		return count;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public void setCount(int count) {
		this.count = count;
	}
	
	public void setGroup(String group) {
		this.group = group;
	}
	
	public void setStatus(Status status) {
		this.status = status;
	}
	
	public void set(PlayerInfo pi) {
		if (pi == null) return;
		setName(pi.getName());
		setCount(pi.getCount());
		setGroup(pi.getGroup());
		setStatus(pi.getStatus());
	}
	
}