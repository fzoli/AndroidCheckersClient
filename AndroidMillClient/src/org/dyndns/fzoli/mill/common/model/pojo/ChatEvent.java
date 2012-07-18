package org.dyndns.fzoli.mill.common.model.pojo;

import org.dyndns.fzoli.mill.common.model.entity.Message;

/**
 *
 * @author zoli
 */
public class ChatEvent extends BaseOnlinePojo {

    private Message message;
    
    public ChatEvent(String playerName) {
        super(playerName);
    }

    public ChatEvent(String playerName, Message message) {
        super(playerName);
        this.message = message;
    }

    public Message getMessage() {
        return message;
    }
    
}
