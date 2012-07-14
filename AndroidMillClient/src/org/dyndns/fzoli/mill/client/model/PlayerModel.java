package org.dyndns.fzoli.mill.client.model;

import java.util.List;
import org.dyndns.fzoli.mill.common.key.ModelKeys;
import org.dyndns.fzoli.mill.common.key.PersonalDataType;
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

    public void loadCountries(String value, ModelActionListener<PlayerData> callback) {
        getProperties(new RequestMap().setFirst(KEY_REQUEST, REQ_GET_COUNTRIES).setFirst(KEY_VALUE, value), callback);
    }
    
    public void loadRegions(String value, ModelActionListener<PlayerData> callback) {
        getProperties(new RequestMap().setFirst(KEY_REQUEST, REQ_GET_REGIONS).setFirst(KEY_VALUE, value), callback);
    }
    
    public void loadCities(String value, ModelActionListener<PlayerData> callback) {
        getProperties(new RequestMap().setFirst(KEY_REQUEST, REQ_GET_CITIES).setFirst(KEY_VALUE, value), callback);
    }
    
    public PlayerData loadPlayer(String playerName) {
        return getProperties(new RequestMap()
            .setFirst(KEY_USER, playerName));
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
        return askModel(new RequestMap()
            .setFirst(KEY_REQUEST, REQ_IS_EMAIL_FREE)
            .setFirst(KEY_VALUE, email)) == 1 ? true : false;
    }
    
    public void revalidateEmail(String password, boolean safe, ModelActionListener<Integer> callback) {
        askModel(new RequestMap()
            .setFirst(KEY_REQUEST, safe ? REQ_SAFE_REVALIDATE_EMAIL : REQ_REVALIDATE_EMAIL)
            .setFirst(KEY_PASSWORD, password), callback);
    }
    
    public void suspendAccount(String password, boolean safe, ModelActionListener<Integer> callback) {
        askModel(new RequestMap()
            .setFirst(KEY_REQUEST, safe ? REQ_SAFE_SUSPEND_ACCOUNT : REQ_SUSPEND_ACCOUNT)
            .setFirst(KEY_PASSWORD, password), callback);
    }
    
    public void setEmail(String password, String email, boolean safe, ModelActionListener<Integer> callback) {
        setProperty(new RequestMap()
            .setFirst(KEY_REQUEST, safe ? REQ_SAFE_SET_EMAIL : REQ_SET_EMAIL)
            .setFirst(KEY_PASSWORD, password)
            .setFirst(KEY_VALUE, email), callback);
    }
    
    public void setPassword(String oldPassword, String newPassword, boolean safe, ModelActionListener<Integer> callback) {
        setProperty(new RequestMap()
            .setFirst(KEY_REQUEST, safe ? REQ_SET_SAFE_PASSWORD : REQ_SET_PASSWORD)
            .setFirst(KEY_PASSWORD, oldPassword)
            .setFirst(KEY_VALUE, newPassword), callback);
    }
    
    public void setOnlineStatus(OnlineStatus state, ModelActionListener<Integer> callback) {
        setProperty(new RequestMap()
            .setFirst(KEY_REQUEST, REQ_SET_ONLINE_STATUS)
            .setFirst(KEY_VALUE, state.name()), callback);
    }
    
    public int setPersonalData(PersonalDataType request, String value) {
        return setProperty(createPersonalDataRequest(request, value));
    }
    
    public void setPersonalData(PersonalDataType request, String value, ModelActionListener<Integer> callback) {
        setProperty(createPersonalDataRequest(request, value), callback);
    }
    
    private List<BasePlayer> findPlayerList(PlayerData.PlayerList type) {
        switch (type) {
            case BLOCKED_PLAYERS:
                return getCache().getPlayer().getBlockedUserList();
            case WISHED_FRIENDS:
                return getCache().getPlayer().getFriendWishList();
            case POSSIBLE_FRIENDS:
                return getCache().getPlayer().getPossibleFriends();
            default:
                return getCache().getPlayer().getFriendList();
        }
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
                            if (p != null) {
                                p.setOnline(true);
                            }
                            else {
                                PlayerData data = loadPlayer(e.getChangedPlayer());
                                List<BasePlayer> ls = findPlayerList(data.getAskedPlayerList());
                                ls.add(data.getAskedPlayer());
                                BasePlayer.orderList(ls);
                            }
                            break;
                        case SIGNOUT:
                            p = findPlayer(l, e.getChangedPlayer());
                            if (p != null) p.setOnline(false);
                            break;
                        case VALIDATE:
                            po.getPlayer().setValidated(true);
                            break;
                        case INVALIDATE:
                            po.getPlayer().setEmail("");
                            po.getPlayer().setValidated(false);
                            break;
                        case SUSPEND:
                            p = findPlayer(l, e.getChangedPlayer());
                            if (p != null) {
                                po.getPlayer().getFriendList().remove(p);
                                po.getPlayer().getFriendWishList().remove(p);
                                po.getPlayer().getBlockedUserList().remove(p);
                                po.getPlayer().getPossibleFriends().remove(p);
                            }
                            break;
                        case AVATAR_ENABLE:
                            po.getPlayer().setAvatarEnabled(true);
                            break;
                        case AVATAR_DISABLE:
                            po.getPlayer().setAvatarEnabled(false);
                            break;
                        case RELOAD:
                            PlayerData data = loadPlayer(e.getChangedPlayer());
                            p = findPlayer(l, e.getChangedPlayer());
                            if (p != null) p.reload(data.getAskedPlayer());
                            break;
                        case PERSONAL_DATA_CHANGE:
                            data = loadPlayer(e.getChangedPlayer());
                            p = findPlayer(l, e.getChangedPlayer());
                            if (p != null) p.setPersonalData(data.getAskedPlayer().getPersonalData());
                            break;
                    }
                }
            }
        }
        catch (Exception ex) {
            ;
        }
    }
    
    public BasePlayer findPlayer(String playerName) {
        return findPlayer(getCache().getPlayer().createMergedPlayerList(), playerName);
    }
    
    private BasePlayer findPlayer(List<BasePlayer> l, String name) {
        for (BasePlayer p : l) {
            if (p.getPlayerName().equals(name)) return p;
        }
        return null;
    }
    
    private RequestMap createPersonalDataRequest(PersonalDataType request, String value) {
        return new RequestMap()
            .setFirst(KEY_REQUEST, request.name())
            .setFirst(KEY_VALUE, value);
    }
    
    private RequestMap createSignInRequest(String user, String password, boolean hash) {
        return new RequestMap()
        .setFirst(KEY_REQUEST, hash ? REQ_SAFE_SIGN_IN : REQ_SIGN_IN)
        .setFirst(KEY_USER, user)
        .setFirst(KEY_PASSWORD, password);
    }

    private RequestMap createSignOutRequest() {
        return new RequestMap()
        .setFirst(KEY_REQUEST, REQ_SIGN_OUT);
    }
    
}