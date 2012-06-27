package org.dyndns.fzoli.mill.common.model.pojo;

/**
 *
 * @author zoli
 */
abstract class PlayerBuilderPojo {
    
    private long userCount;

    PlayerBuilderPojo(long userCount) {
        this.userCount = userCount;
    }

    public long getUserCount() {
        return userCount;
    }
    
    protected void setUserCount(long userCount) {
        this.userCount = userCount;
    }
    
}