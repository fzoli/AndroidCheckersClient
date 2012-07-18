package org.dyndns.fzoli.mill.client.model;

import org.dyndns.fzoli.mill.common.key.ChatKeys;
import org.dyndns.fzoli.mill.common.key.ModelKeys;
import org.dyndns.fzoli.mill.common.model.pojo.ChatData;
import org.dyndns.fzoli.mill.common.model.pojo.ChatEvent;
import org.dyndns.fzoli.mvc.client.connection.Connection;

/**
 *
 * @author zoli
 */
public class ChatModel extends AbstractOnlineModel<ChatEvent, ChatData> implements ChatKeys {

    public ChatModel(Connection<Object, Object> connection) {
        super(connection, ModelKeys.CHAT, ChatEvent.class, ChatData.class);
    }
    
}
