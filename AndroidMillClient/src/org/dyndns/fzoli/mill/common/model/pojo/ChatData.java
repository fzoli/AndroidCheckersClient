package org.dyndns.fzoli.mill.common.model.pojo;

import java.util.List;
import java.util.Map;
import org.dyndns.fzoli.mill.common.model.entity.Message;

/**
 *
 * @author zoli
 */
public class ChatData extends BaseOnlinePojo {

    private Integer sync;
    private List<Message> messages;
    private Map<String, Integer> unreadedCount;

    public ChatData(String playerName) {
        super(playerName);
    }
    
    public ChatData(String playerName, Map<String, Integer> unreadedCount) {
        super(playerName);
        this.unreadedCount = unreadedCount;
    }
    
    public ChatData(String playerName, List<Message> messages) {
        super(playerName);
        this.messages = messages;
    }

    public Integer getSync() {
        return sync;
    }

    public void setSync(Integer sync) {
        this.sync = sync;
    }

    public Map<String, Integer> getUnreadedCount() {
        return unreadedCount;
    }

    public List<Message> getMessages() {
        return messages;
    }
    
}