package org.dyndns.fzoli.mill.client.model;

import java.util.List;
import org.dyndns.fzoli.mill.common.key.ModelKeys;
import org.dyndns.fzoli.mill.common.key.PlayerKeys;
import org.dyndns.fzoli.mill.common.model.entity.BasePlayer;
import org.dyndns.fzoli.mill.common.model.entity.OnlineStatus;
import org.dyndns.fzoli.mill.common.model.pojo.PlayerData;
import org.dyndns.fzoli.mill.common.model.pojo.PlayerEvent;
import org.dyndns.fzoli.mvc.client.connection.Connection;
import org.dyndns.fzoli.mvc.client.event.ModelActionListener;
import org.dyndns.fzoli.mvc.common.request.map.RequestMap;

/**
 *
 * @author zoli
 */
public class PlayerModel extends AbstractOnlineModel<PlayerEvent, PlayerData> implements PlayerKeys {

    public PlayerModel(Connection<Object, Object> connection) {
        super(connection, ModelKeys.PLAYER, PlayerEvent.class, PlayerData.class);
    }

    public BasePlayer loadPlayer(String playerName) {
        RequestMap m = new RequestMap();
        m.setFirst(KEY_USER, playerName);
        return getProperties(m).getAskedPlayer();
    }
    
    public int signIn(String user, String password, boolean hash) {
        return askModel(createSignInRequest(user, password, hash));
    }
    
    public void signIn(String user, String password, boolean hash, ModelActionListener<Integer> callback) {
        askModel(createSignInRequest(user, password, hash), callback);
    }
    
    public int signOut() {
        return askModel(createSignOutRequest());
    }
    
    public void signOut(ModelActionListener<Integer> callback) {
        askModel(createSignOutRequest(), callback);
    }
    
    public boolean isEmailFree(String email) {
        RequestMap m = new RequestMap();
        m.setFirst(KEY_REQUEST, REQ_IS_EMAIL_FREE);
        m.setFirst(KEY_VALUE, email);
        return askModel(m) == 1 ? true : false;
    }
    
    public void revalidateEmail(String password, boolean safe, ModelActionListener<Integer> callback) {
        RequestMap m = new RequestMap();
        m.setFirst(KEY_REQUEST, safe ? REQ_SAFE_REVALIDATE_EMAIL : REQ_REVALIDATE_EMAIL);
        m.setFirst(KEY_PASSWORD, password);
        askModel(m, callback);
    }
    
    public void suspendAccount(String password, boolean safe, ModelActionListener<Integer> callback) {
        RequestMap m = new RequestMap();
        m.setFirst(KEY_REQUEST, safe ? REQ_SAFE_SUSPEND_ACCOUNT : REQ_SUSPEND_ACCOUNT);
        m.setFirst(KEY_PASSWORD, password);
        askModel(m, callback);
    }
    
    public void setEmail(String password, String email, boolean safe, ModelActionListener<Integer> callback) {
        RequestMap m = new RequestMap();
        m.setFirst(KEY_REQUEST, safe ? REQ_SAFE_SET_EMAIL : REQ_SET_EMAIL);
        m.setFirst(KEY_PASSWORD, password);
        m.setFirst(KEY_VALUE, email);
        setProperty(m, callback);
    }
    
    public void setPassword(String oldPassword, String newPassword, boolean safe, ModelActionListener<Integer> callback) {
        RequestMap m = new RequestMap();
        m.setFirst(KEY_REQUEST, safe ? REQ_SET_SAFE_PASSWORD : REQ_SET_PASSWORD);
        m.setFirst(KEY_PASSWORD, oldPassword);
        m.setFirst(KEY_VALUE, newPassword);
        setProperty(m, callback);
    }
    
    public void setOnlineStatus(OnlineStatus state, ModelActionListener<Integer> callback) {
        RequestMap m = new RequestMap();
        m.setFirst(KEY_REQUEST, REQ_SET_ONLINE_STATUS);
        m.setFirst(KEY_VALUE, state.name());
        setProperty(m, callback);
    }
    
    @Override
    protected void updateCache(List<PlayerEvent> list, PlayerData po) {
        try {
            super.updateCache(list, po);
            if (po.getPlayerName() != null) {
                BasePlayer p;
                List<BasePlayer> l = po.getPlayer().createMergedPlayerList();
                for (PlayerEvent e : list) {
                    switch (e.getType()) {
                        case SIGNIN:
                            p = findPlayer(l, e.getChangedPlayer());
                            if (p != null) p.setOnline(true);
                            break;
                        case SIGNOUT:
                            p = findPlayer(l, e.getChangedPlayer());
                            if (p != null) p.setOnline(false);
                            break;
                        case VALIDATE:
                            if(po.getPlayer() != null) po.getPlayer().setValidated(true);
                            break;
                        case INVALIDATE:
                            if(po.getPlayer() != null) {
                                po.getPlayer().setEmail("");
                                po.getPlayer().setValidated(false);
                            }
                            break;
                        case SUSPEND:
                            //TODO
                            break;
                    }
                }
            }
        }
        catch (Exception ex) {
            ;
        }
    }
    
    private BasePlayer findPlayer(List<BasePlayer> l, String name) {
        for (BasePlayer p : l) {
            if (p.getPlayerName().equals(name)) return p;
        }
        return null;
    }
    
    private RequestMap createSignInRequest(String user, String password, boolean hash) {
        RequestMap m = new RequestMap();
        m.setFirst(KEY_REQUEST, hash ? REQ_SAFE_SIGN_IN : REQ_SIGN_IN);
        m.setFirst(KEY_USER, user);
        m.setFirst(KEY_PASSWORD, password);
        return m;
    }

    private RequestMap createSignOutRequest() {
        RequestMap m = new RequestMap();
        m.setFirst(KEY_REQUEST, REQ_SIGN_OUT);
        return m;
    }
    
}