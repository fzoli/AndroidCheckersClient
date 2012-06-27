package org.dyndns.fzoli.mill.common.model.pojo;

/**
 *
 * @author zoli
 */
public class PlayerBuilderEvent extends PlayerBuilderPojo {
    
    private boolean reset;
    
    public PlayerBuilderEvent(boolean reset, long userCount) {
        super(userCount);
        this.reset = reset;
    }

    public boolean isReset() {
        return reset;
    }
    
}