package org.dyndns.fzoli.mill.common.model.entity;

import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

/**
 *
 * @author zoli
 */
public class BasePlayer {
    
    private boolean online;
    private String playerName;
    protected Long signUp, signIn;
    private Date signUpDate, signInDate;
    private PersonalData personalData;
    private PlayerStatus playerStatus;

    public BasePlayer(String playerName, Date signUpDate, Date signInDate, PersonalData personalData, PlayerStatus playerStatus, boolean online) {
        this.online = online;
        this.playerName = playerName;
        this.signUpDate = signUpDate;
        this.signInDate = signInDate;
        this.personalData = personalData;
        this.playerStatus = playerStatus;
        if (signUpDate != null) signUp = signUpDate.getTime();
        if (signInDate != null) signIn = signInDate.getTime();
    }

    public String getName() {
        if (getPersonalData() == null) return getPlayerName();
        String n = getPersonalData().getName();
        return n == null ? getPlayerName() : n;
    }
    
    public boolean isOnline() {
        return online;
    }

    public PlayerStatus getPlayerStatus() {
        return playerStatus;
    }

    public String getPlayerName() {
        return playerName;
    }

    public Date getSignUpDate() {
        return signUpDate;
    }

    public Date getSignInDate() {
        return signInDate;
    }

    public PersonalData getPersonalData() {
        return personalData;
    }

    public void setPersonalData(PersonalData personalData) {
        this.personalData = personalData;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }

    public void setPlayerStatus(PlayerStatus playerStatus) {
        this.playerStatus = playerStatus;
    }

    public void setSignInDate(Date signInDate) {
        this.signInDate = signInDate;
        if (signInDate != null) this.signIn = signInDate.getTime();
        else signIn = null;
    }

    public void setSignUpDate(Date signUpDate) {
        this.signUpDate = signUpDate;
        if (signUpDate != null) this.signUp = signUpDate.getTime();
        else signUp = null;
    }
    
    public void reload(BasePlayer bp) {
        if (bp == null) return;
        setPersonalData(bp.getPersonalData());
        setOnline(bp.isOnline());
        setPlayerStatus(bp.getPlayerStatus());
        setSignInDate(bp.getSignInDate());
        setSignUpDate(bp.getSignUpDate());
    }
    
    public static void orderList(List<BasePlayer> ls) {
        if (ls == null) return;
        Collections.sort(ls, new Comparator<BasePlayer>() {

            @Override
            public int compare(BasePlayer p1, BasePlayer p2) {
                return p1.getPlayerName().compareToIgnoreCase(p2.getPlayerName());
            }

        });
    }
    
}