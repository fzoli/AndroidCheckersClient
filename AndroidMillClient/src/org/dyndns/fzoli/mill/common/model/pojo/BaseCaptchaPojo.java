package org.dyndns.fzoli.mill.common.model.pojo;

/**
 *
 * @author zoli
 */
public interface BaseCaptchaPojo {
    
    int getCaptchaWidth();
    
    boolean isCaptchaValidated();
    
    void setCaptchaValidated(boolean validated);
    
    void setCaptchaWidth(int captchaWidth);
    
}