package org.dyndns.fzoli.mill.common.model.pojo;

import org.dyndns.fzoli.mill.common.model.entity.Player;

/**
 *
 * @author zoli
 */
public class PlayerEvent extends BaseOnlinePojo {

    public enum PlayerEventType {
        COMMON,
        SIGNIN,
        SIGNOUT,
        VALIDATE
    }
    
    private PlayerEventType type;
    private String changedPlayer;
    
    public PlayerEvent(Player player) {
        super(player);
        type = PlayerEventType.COMMON;
    }

    public PlayerEvent(Player player, PlayerEventType t) {
        super(player);
        type = t;
    }
    
    public PlayerEvent(Player player, String changedPlayer, boolean signIn) {
        super(player);
        this.changedPlayer = changedPlayer;
        type = signIn ? PlayerEventType.SIGNIN : PlayerEventType.SIGNOUT;
    }

    public PlayerEventType getType() {
        return type;
    }

    public String getChangedPlayer() {
        return changedPlayer;
    }
    
}