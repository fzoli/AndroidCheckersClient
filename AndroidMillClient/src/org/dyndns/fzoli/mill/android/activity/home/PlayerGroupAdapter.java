package org.dyndns.fzoli.mill.android.activity.home;

import java.util.ArrayList;
import java.util.List;

import org.dyndns.fzoli.mill.android.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

public class PlayerGroupAdapter extends BaseExpandableListAdapter {

	private final Context CONTEXT;
	private final ExpandableListView VIEW;
	private final List<String> GROUPS = new ArrayList<String>();
	private final List<List<PlayerInfo>> CHILDRENS = new ArrayList<List<PlayerInfo>>();
	
	private Integer lastExpandedGroupPosition;
	private boolean avatarEnabled, showAllAvatar;
	
	public PlayerGroupAdapter(Context context, ExpandableListView view) {
		this.CONTEXT = context;
		this.VIEW = view;
	}
	
	public void setShowAllAvatar(boolean showAllAvatar) {
		this.showAllAvatar = showAllAvatar;
	}
	
	public void setAvatarEnabled(boolean avatarEnabled) {
		this.avatarEnabled = avatarEnabled;
		notifyDataSetChanged();
	}
	
	public Integer getLastExpandedGroupPosition() {
		return lastExpandedGroupPosition;
	}
	
	public void setLastExpandedGroupPosition(Integer lastExpandedGroupPosition) {
		this.lastExpandedGroupPosition = lastExpandedGroupPosition;
	}
	
	@Override
	public void onGroupCollapsed(int groupPosition) {
		if (lastExpandedGroupPosition != null && lastExpandedGroupPosition.equals(groupPosition)) lastExpandedGroupPosition = null;
		super.onGroupCollapsed(groupPosition);
	}
	
    @Override
    public void onGroupExpanded(int groupPosition) {
        super.onGroupExpanded(groupPosition);
        try {
        	if (!lastExpandedGroupPosition.equals(groupPosition)) VIEW.collapseGroup(lastExpandedGroupPosition);
        }
        catch (NullPointerException ex) {
        	;
        }
        lastExpandedGroupPosition = groupPosition;
    }
	
	public void addItem(PlayerInfo p) {
        chkGroup(p.getGroup());
        int index = GROUPS.indexOf(p.getGroup());
        if (CHILDRENS.size() < index + 1) {
            CHILDRENS.add(new ArrayList<PlayerInfo>());
        }
        CHILDRENS.get(index).add(p);
    }

	public void setItem(PlayerInfo pi) {
		if (pi == null) return;
		PlayerInfo i = findPlayerInfo(pi);
		if (i == null) {
			addItem(pi);
		}
		else {
			chkGroup(pi.getGroup());
			i.setName(pi.getName());
			i.setAvatar(pi.getAvatar());
			i.setGroup(pi.getGroup());
			i.setStatus(pi.getStatus());
		}
		notifyDataSetChanged();
	}
	
	public void removeItem(String playerName) {
		PlayerInfo pi = findPlayerInfo(playerName);
		if (pi != null) removeItem(pi);
	}
	
	public void removeItem(PlayerInfo pi) {
		if (pi == null) return;
		synchronized (CHILDRENS) {
			for (List<PlayerInfo> ls : CHILDRENS) {
				for (PlayerInfo p : ls) {
					if (pi.equals(p)) {
						ls.remove(p);
						break;
					}
				}
			}
			notifyDataSetChanged();
		}
	}
	
	private void chkGroup(String s) {
		if (!GROUPS.contains(s)) {
            GROUPS.add(s);
        }
	}
	
	public void setStatus(String name, PlayerInfo.Status status) {
		if (name == null || status == null) return;
		PlayerInfo pi = findPlayerInfo(name);
		if (pi == null) return;
		pi.setStatus(status);
		setItem(pi);
	}
	
	public PlayerInfo findPlayerInfo(String name) {
		if (name == null) return null;
		for (List<PlayerInfo> l : CHILDRENS) {
			for (PlayerInfo i : l) {
				if (i.getPlayerName() == null) continue;
				if (i.getPlayerName().equals(name)) return i;
			}
		}
		return null;
	}
	
	public PlayerInfo findPlayerInfo(PlayerInfo p) {
		if (p == null) return null;
		return findPlayerInfo(p.getPlayerName());
	}
	
	@Override
    public Object getChild(int groupPosition, int childPosition) {
		return CHILDRENS.get(groupPosition).get(childPosition);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }
    
    public static View getPlayerView(Context context, PlayerInfo p, View convertView, boolean avatarEnabled) {
    	if (convertView == null) convertView = createView(context, R.layout.player_view);
        ImageView ivStatus = (ImageView) convertView.findViewById(R.id.ivStatus);
        int res;
        switch (p.getStatus()) {
			case ONLINE:
				res = android.R.drawable.presence_online;
				break;
			case OFFLINE:
				res = android.R.drawable.presence_offline;
				break;
			case BLOCKED:
				res = R.drawable.statusicon_block;
				break;
			default:
				res = android.R.drawable.presence_invisible;
		}
        ivStatus.setImageResource(res);
        TextView tvName = (TextView) convertView.findViewById(R.id.tvName);
        tvName.setText(p.getName());
        ImageView ivAvatar = (ImageView) convertView.findViewById(R.id.ivAvatar);
        ivAvatar.setVisibility(avatarEnabled ? View.VISIBLE : View.GONE);
        if (p.getAvatar() != null) ivAvatar.setImageBitmap(p.getAvatar());
		return convertView;
    }
    
	@Override
	public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
		PlayerInfo p = (PlayerInfo) getChild(groupPosition, childPosition);
		return getPlayerView(CONTEXT, p, convertView, avatarEnabled && (getGroup(groupPosition).equals(CONTEXT.getString(R.string.friends)) || showAllAvatar));
	}

	@Override
    public int getChildrenCount(int groupPosition) {
		return CHILDRENS.get(groupPosition).size();
    }

    @Override
    public String getGroup(int groupPosition) {
        return GROUPS.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return GROUPS.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
		String group = (String) getGroup(groupPosition);
		if (convertView == null) convertView = createView(R.layout.player_group_view);
		ImageView iv = (ImageView) convertView.findViewById(R.id.ivStatus);
		iv.setImageResource(isExpanded ? R.drawable.group_expanded : R.drawable.group_collapsed);
		TextView tv = (TextView) convertView.findViewById(R.id.tvName);
        tv.setText(group);
		return convertView;
	}

	@Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public boolean isChildSelectable(int arg0, int arg1) {
        return true;
    }

    private View createView(int res) {
    	return createView(CONTEXT, res);
    }
    
    private static View createView(Context context, int res) {
    	LayoutInflater infalInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        return infalInflater.inflate(res, null);
    }
    
}