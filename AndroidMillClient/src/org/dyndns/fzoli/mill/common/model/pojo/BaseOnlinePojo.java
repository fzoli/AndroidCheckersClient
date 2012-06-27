package org.dyndns.fzoli.mill.common.model.pojo;

import org.dyndns.fzoli.mill.common.model.entity.Player;

/**
 *
 * @author zoli
 */
public class BaseOnlinePojo {
    
    private String playerName;

    public BaseOnlinePojo(Player player) {
        this(getPlayerName(player));
    }
    
    public BaseOnlinePojo(String playerName) {
        this.playerName = playerName;
    }
    
    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }
    
    protected static String getPlayerName(Player player) {
        return player == null ? null : player.getPlayerName();
    }
    
}