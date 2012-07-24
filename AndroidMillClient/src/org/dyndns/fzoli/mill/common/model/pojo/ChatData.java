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
    
    public ChatData(String playerName, Map<String, Integer> unreadedCount) { //TODO: majd ha jelzi az olvasatlan üzenetek számát, a kezdőérték ennek segítségével jut el a klienshez ÉS egybe kellene tenni a két konstruktort emiatt + lassan ideje megcsinálni a service üzengetést (amikor bejelentkezik az emberke és chat üzenet jön) + előfordulhat, hogy a barátlistán kívüli ember küld üzenetet, amit jelezni kell valahogy (lehessen küldeni profiloldalról mivel elérhető a nyílvántartásból is és barátlistából is a chatküldő felületet használva)
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