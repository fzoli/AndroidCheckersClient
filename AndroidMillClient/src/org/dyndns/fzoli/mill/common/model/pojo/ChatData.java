package org.dyndns.fzoli.mill.common.model.pojo;

import java.util.List;
import org.dyndns.fzoli.mill.common.model.entity.Message;

/**
 *
 * @author zoli
 */
public class ChatData extends BaseOnlinePojo {

    private Integer unreadedCount;
    private List<Message> messages;

    public ChatData(String playerName) {
        super(playerName);
    }
    
    public ChatData(String playerName, int unreadedCount) {
        super(playerName);
        this.unreadedCount = unreadedCount;
    }
    
    public ChatData(String playerName, List<Message> messages) {
        super(playerName);
        this.messages = messages;
    }

    public Integer getUnreadedCount() {
        return unreadedCount;
    }

    public List<Message> getMessages() {
        return messages;
    }
    
}