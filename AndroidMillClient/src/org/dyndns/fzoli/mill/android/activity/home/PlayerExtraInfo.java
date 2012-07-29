package org.dyndns.fzoli.mill.android.activity.home;

public class PlayerExtraInfo extends PlayerInfo {

	private String extra;
	
	public PlayerExtraInfo(Status status, String playerName, String name, String extra, int count) {
		super(status, playerName, name, null, count);
		this.extra = extra;
	}
	
	public String getExtra() {
		return extra;
	}
	
	public void setExtra(String extra) {
		this.extra = extra;
	}
	
	@Override
	public void set(PlayerInfo pi) {
		super.set(pi);
		if (pi == null) return;
		if (pi instanceof PlayerExtraInfo) {
			setExtra(((PlayerExtraInfo) pi).getExtra());
		}
	}
	
}