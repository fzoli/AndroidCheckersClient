package org.dyndns.fzoli.mill.common.model.pojo;

import org.dyndns.fzoli.mill.common.model.entity.BasePlayer;
import org.dyndns.fzoli.mill.common.model.entity.Player;

/**
 *
 * @author zoli
 */
public class PlayerData extends BaseOnlinePojo implements BaseCaptchaPojo {

    private Player player;
    private int captchaWidth;
    private boolean captchaValidated;
    
    private BasePlayer askedPlayer;
    private PlayerList askedPlayerList;
    
    public static enum PlayerList {
        FRIENDS,
        WISHED_FRIENDS,
        POSSIBLE_FRIENDS,
        BLOCKED_PLAYERS
    }
    
    public PlayerData(BasePlayer askedPlayer, PlayerList askedPlayerList) {
        super("");
        this.askedPlayer = askedPlayer;
        this.askedPlayerList = askedPlayerList;
    }
    
    public PlayerData(Player player, boolean captchaValidated, int captchaWidth) {
        super(player);
        this.player = player;
        this.captchaWidth = captchaWidth;
        this.captchaValidated = captchaValidated;
    }
    
    public BasePlayer getAskedPlayer() {
        return askedPlayer;
    }

    public PlayerList getAskedPlayerList() {
        return askedPlayerList;
    }
    
    public Player getPlayer() {
        return player;
    }
    
    @Override
    public int getCaptchaWidth() {
        return captchaWidth;
    }

    @Override
    public boolean isCaptchaValidated() {
        return captchaValidated;
    }

    @Override
    public void setCaptchaValidated(boolean captchaValidated) {
        this.captchaValidated = captchaValidated;
    }

    @Override
    public void setCaptchaWidth(int captchaWidth) {
        this.captchaWidth = captchaWidth;
    }
    
}