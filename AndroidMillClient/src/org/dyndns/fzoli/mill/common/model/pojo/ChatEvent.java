package org.dyndns.fzoli.mill.common.model.pojo;

import org.dyndns.fzoli.mill.common.model.entity.Message;

/**
 *
 * @author zoli
 */
public class ChatEvent extends BaseOnlinePojo {

    private Message message;
    private String clearPlayer;
    private Boolean clear;
    
    public ChatEvent(String playerName) {
        super(playerName);
    }
    
    public ChatEvent(String playerName, String clearPlayer) {
        super(playerName);
        this.clear = true;
        this.clearPlayer = clearPlayer;
    }

    public ChatEvent(String playerName, Message message) {
        super(playerName);
        this.message = message;
    }

    public boolean isClear() {
        if (clear == null) return false;
        return clear;
    }

    public String getClearPlayer() {
        return clearPlayer;
    }
    
    public Message getMessage() {
        return message;
    }
    
}
