package org.dyndns.fzoli.mill.android.activity.home;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class PlayerAdapter extends BaseAdapter {

	private final Context CONTEXT;
	private final List<PlayerInfo> PLAYERS = new ArrayList<PlayerInfo>();
	
	public PlayerAdapter(Context context) {
		this.CONTEXT = context;
	}
	
	public List<PlayerInfo> getPlayerList() {
		return PLAYERS;
	}
	
	@Override
	public int getCount() {
		return PLAYERS.size();
	}

	@Override
	public Object getItem(int paramInt) {
		return PLAYERS.get(paramInt);
	}

	@Override
	public long getItemId(int paramInt) {
		return 0;
	}

	@Override
	public View getView(int index, View convertView, ViewGroup vg) {
		PlayerInfo p = PLAYERS.get(index);
		return PlayerGroupAdapter.getPlayerView(CONTEXT, p, convertView);
	}
	
	public void setStatus(int id, PlayerInfo.Status status) {
		Log.i("test","setplayer "+status);
		PLAYERS.get(id).setStatus(status);
		notifyDataSetChanged();
	}
	
}