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
        VALIDATE,
        INVALIDATE,
        SUSPEND,
        UNSUSPEND
    }
    
    private PlayerEventType type;
    private String changedPlayer;
    
    public PlayerEvent(Player player) {
        super(player);
        type = PlayerEventType.COMMON;
    }

    public PlayerEvent(Player player, PlayerEventType type) {
        super(player);
        this.type = type;
    }
    
    public PlayerEvent(Player player, String changedPlayer, PlayerEventType type) {
        super(player);
        this.changedPlayer = changedPlayer;
        this.type = type;
    }
    
    public PlayerEvent(Player player, String changedPlayer, boolean signIn) {
        this(player, changedPlayer, signIn ? PlayerEventType.SIGNIN : PlayerEventType.SIGNOUT);
    }

    public PlayerEventType getType() {
        return type;
    }

    public String getChangedPlayer() {
        return changedPlayer;
    }
    
}