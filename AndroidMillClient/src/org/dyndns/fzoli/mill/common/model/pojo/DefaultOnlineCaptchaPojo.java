package org.dyndns.fzoli.mill.common.model.pojo;

import org.dyndns.fzoli.mill.common.model.entity.Player;

/**
 *
 * @author zoli
 */
public class DefaultOnlineCaptchaPojo extends BaseOnlinePojo implements BaseCaptchaPojo {

    private int captchaWidth;
    private boolean captchaValidated;
    
    public DefaultOnlineCaptchaPojo(Player player, boolean captchaValidated, int captchaWidth) {
        this(getPlayerName(player), captchaValidated, captchaWidth);
    }

    public DefaultOnlineCaptchaPojo(String playerName, boolean captchaValidated, int captchaWidth) {
        super(playerName);
        this.captchaWidth = captchaWidth;
        this.captchaValidated = captchaValidated;
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
