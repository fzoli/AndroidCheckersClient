package org.dyndns.fzoli.mill.common.model.pojo;

import java.util.Date;

/**
 *
 * @author zoli
 */
public class PlayerBuilderData extends PlayerBuilderPojo implements BaseCaptchaPojo {
    
    public Long delay;
    private boolean validated;
    private long initTime, time;
    private int timeout;
    private String user, email;
    private int captchaWidth;

    public PlayerBuilderData(String user, String email, long time, long initTime, int timeout, long userCount, boolean validated, int captchaWidth) {
        super(userCount);
        this.user = user;
        this.time = time;
        this.initTime = initTime;
        this.email = email;
        this.timeout = timeout;
        this.validated = validated;
        this.captchaWidth = captchaWidth;
    }

    @Override
    public int getCaptchaWidth() {
        return captchaWidth;
    }

    @Override
    public boolean isCaptchaValidated() {
        return validated;
    }

    public long getInitTime() {
        return initTime;
    }
    
    public int getTimeout() {
        return timeout;
    }

    public long getTime() {
        return time;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void setInitTime(long initTime) {
        this.initTime = initTime;
    }

    @Override
    public void setUserCount(long userCount) {
        super.setUserCount(userCount);
    }

    @Override
    public void setCaptchaValidated(boolean validated) {
        this.validated = validated;
    }

    @Override
    public void setCaptchaWidth(int captchaWidth) {
        this.captchaWidth = captchaWidth;
    }
    
    public void reinit() {
        initTime = new Date().getTime() - (delay == null ? 0 : delay);
        user = "";
        email = "";
    }
    
}