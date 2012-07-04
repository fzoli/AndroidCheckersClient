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
    private Date signUpDate, signInDate;
    private PersonalData personalData;

    public BasePlayer(String playerName, Date signUpDate, Date signInDate, PersonalData personalData, boolean online) {
        this.online = online;
        this.playerName = playerName;
        this.signUpDate = signUpDate;
        this.signInDate = signInDate;
        this.personalData = personalData;
    }

    public String getName() {
        if (getPersonalData() == null) return getPlayerName();
        String n = getPersonalData().getName();
        return n == null ? getPlayerName() : n;
    }
    
    public boolean isOnline() {
        return online;
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

    public void setOnline(boolean online) {
        this.online = online;
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