package org.dyndns.fzoli.mill.common.model.pojo;

/**
 *
 * @author zoli
 */
public class PlayerAvatarData extends BaseOnlinePojo {

    private boolean avatarEnabled;
    private Integer x, y, scale;

    public PlayerAvatarData(String playerName, boolean avatarEnabled) {
        super(playerName);
        this.avatarEnabled = avatarEnabled;
    }
    
    public PlayerAvatarData(String playerName, Integer x, Integer y, Integer scale, boolean avatarEnabled) {
        super(playerName);
        this.x = x;
        this.y = y;
        this.scale = scale;
        this.avatarEnabled = avatarEnabled;
    }

    public boolean isAvatarEnabled() {
        return avatarEnabled;
    }

    public Integer getX() {
        return x;
    }

    public Integer getY() {
        return y;
    }

    public Integer getScale() {
        return scale;
    }

    public void setX(Integer x) {
        this.x = x;
    }

    public void setY(Integer y) {
        this.y = y;
    }

    public void setScale(Integer scale) {
        this.scale = scale;
    }
    
}