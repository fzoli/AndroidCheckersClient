package org.dyndns.fzoli.mill.common.model.entity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 *
 * @author zoli
 */
public class Player extends BasePlayer {
    
    private String email;
    private boolean validated;
    private int permission, activePermission;
    private List<BasePlayer> friendList, friendWishList, blockedUserList, possibleFriends;

    public Player(String playerName, String email, boolean validated, int permission, int activePermission, Date signUpDate, Date signInDate, PersonalData personalData, List<BasePlayer> friendList, List<BasePlayer> friendWishList, List<BasePlayer> blockedUserList, List<BasePlayer> possibleFriends, boolean online) {
        super(playerName, signUpDate, signInDate, personalData, online);
        this.email = email;
        this.validated = validated;
        this.permission = permission;
        this.activePermission = activePermission;
        this.friendList = friendList;
        this.friendWishList = friendWishList;
        this.blockedUserList = blockedUserList;
        this.possibleFriends = possibleFriends;
    }
    
    public boolean isValidated() {
        return validated;
    }

    public int getPermissionMask(boolean active) {
        return active ? activePermission : permission;
    }

    public String getEmail() {
        return email;
    }

    public List<BasePlayer> getFriendList() {
        return friendList;
    }

    public List<BasePlayer> getFriendWishList() {
        return friendWishList;
    }

    public List<BasePlayer> getBlockedUserList() {
        return blockedUserList;
    }

    public List<BasePlayer> getPossibleFriends() {
        return possibleFriends;
    }

    public List<BasePlayer> createMergedPlayerList() {
        List<BasePlayer> l = new ArrayList<BasePlayer>();
        l.addAll(getFriendList());
        l.addAll(getFriendWishList());
        l.addAll(getBlockedUserList());
        l.addAll(getPossibleFriends());
        return l;
    }
    
    public BasePlayer findPlayer(String playerName) {
        if (playerName == null) return null;
        for (BasePlayer p : getFriendList()) {
            if (p.getPlayerName().equals(playerName)) return p;
        }
        return null;
    }
    
}