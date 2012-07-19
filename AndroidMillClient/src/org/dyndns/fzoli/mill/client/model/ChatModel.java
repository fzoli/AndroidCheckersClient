package org.dyndns.fzoli.mill.client.model;

import java.util.Date;
import org.dyndns.fzoli.mill.common.DateUtil;
import org.dyndns.fzoli.mill.common.key.ChatKeys;
import org.dyndns.fzoli.mill.common.key.ModelKeys;
import org.dyndns.fzoli.mill.common.model.pojo.ChatData;
import org.dyndns.fzoli.mill.common.model.pojo.ChatEvent;
import org.dyndns.fzoli.mvc.client.connection.Connection;
import org.dyndns.fzoli.mvc.client.event.ModelActionListener;
import org.dyndns.fzoli.mvc.common.request.map.RequestMap;

/**
 *
 * @author zoli
 */
public class ChatModel extends AbstractOnlineModel<ChatEvent, ChatData> implements ChatKeys {
    
    public ChatModel(Connection<Object, Object> connection) {
        super(connection, ModelKeys.CHAT, ChatEvent.class, ChatData.class);
        getCache().setSync(getSync());
    }
    
    public int getSync() {
        return askModel(new RequestMap().setFirst(KEY_REQUEST, REQ_SYNC).setFirst(KEY_DATE, Long.toString(DateUtil.getDateInTimeZone(new Date(), "GMT").getTime())));
    }
    
    public void loadUnreadedMessages(String playerName, ModelActionListener<ChatData> callback) {
        loadMessages(playerName, null, callback);
    }
    
    public void loadMessages(String playerName, Date startDate, ModelActionListener<ChatData> callback) {
        RequestMap map = new RequestMap().setFirst(KEY_REQUEST, REQ_GET_MESSAGES).setFirst(KEY_PLAYER, playerName);
        if (startDate != null) map.setFirst(KEY_DATE, Long.toString(startDate.getTime()));
        getProperties(map, callback);
    }
    
    public void updateReadDate(String playerName, ModelActionListener<Integer> callback) {
        askModel(new RequestMap().setFirst(KEY_REQUEST, REQ_UPDATE_READ_DATE).setFirst(KEY_PLAYER, playerName), callback);
    }
    
    public void sendMessage(String playerName, String text, ModelActionListener<Integer> callback) {
        askModel(new RequestMap().setFirst(KEY_REQUEST, REQ_SEND_MESSAGE).setFirst(KEY_PLAYER, playerName).setFirst(KEY_VALUE, text), callback);
    }
    
}