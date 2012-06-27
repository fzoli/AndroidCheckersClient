package org.dyndns.fzoli.mill.client.model;

import java.util.List;
import org.dyndns.fzoli.mill.common.key.ModelKeys;
import org.dyndns.fzoli.mill.common.key.PlayerBuilderKeys;
import org.dyndns.fzoli.mill.common.model.pojo.PlayerBuilderData;
import org.dyndns.fzoli.mill.common.model.pojo.PlayerBuilderEvent;
import org.dyndns.fzoli.mvc.client.connection.Connection;
import org.dyndns.fzoli.mvc.client.event.ModelActionListener;
import org.dyndns.fzoli.mvc.common.request.map.RequestMap;

/**
 *
 * @author zoli
 */
public class PlayerBuilderModel extends AbstractMillModel<PlayerBuilderEvent, PlayerBuilderData> implements PlayerBuilderKeys {

    public PlayerBuilderModel(Connection<Object, Object> connection) {
        super(connection, ModelKeys.PLAYER_BUILDER, PlayerBuilderEvent.class, PlayerBuilderData.class);
    }
    
    public void setUser(String user, ModelActionListener<Integer> callback) {
        RequestMap m = new RequestMap();
        m.setFirst(KEY_REQUEST, REQ_SET_USER);
        m.setFirst(KEY_VALUE, user);
        setProperty(m, callback);
    }
    
    public void setEmail(String email, ModelActionListener<Integer> callback) {
        RequestMap m = new RequestMap();
        m.setFirst(KEY_REQUEST, REQ_SET_EMAIL);
        m.setFirst(KEY_VALUE, email);
        setProperty(m, callback);
    }
    
    public void createUser(String password, boolean hash, ModelActionListener<Integer> callback) {
        RequestMap m = new RequestMap();
        m.setFirst(KEY_REQUEST, hash ? REQ_SAFE_CREATE : REQ_CREATE);
        m.setFirst(KEY_VALUE, password);
        askModel(m, callback);
    }
    
    public void validate() {
        askModel(createValidateMap());
    }
    
    public void validate(ModelActionListener<Integer> callback) {
        askModel(createValidateMap(), callback);
    }
    
    @Override
    protected void updateCache(List<PlayerBuilderEvent> list, PlayerBuilderData cache) {
        try {
            for (PlayerBuilderEvent e : list) {
                if (e.isReset()) cache.reinit();
                cache.setUserCount(e.getUserCount());
            }
        }
        catch (Exception ex) {
            ;
        }
    }
    
    private RequestMap createValidateMap() {
        RequestMap m = new RequestMap();
        m.setFirst(KEY_REQUEST, REQ_VALIDATE);
        return m;
    }
    
}