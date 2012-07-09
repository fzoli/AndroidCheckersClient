package org.dyndns.fzoli.mill.client.model;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import org.dyndns.fzoli.http.CountingListener;
import org.dyndns.fzoli.mill.common.key.ModelKeys;
import org.dyndns.fzoli.mill.common.key.PlayerAvatarKeys;
import org.dyndns.fzoli.mill.common.model.pojo.PlayerAvatarData;
import org.dyndns.fzoli.mill.common.model.pojo.PlayerAvatarEvent;
import org.dyndns.fzoli.mvc.client.connection.Connection;
import org.dyndns.fzoli.mvc.client.event.ModelActionListener;
import org.dyndns.fzoli.mvc.common.request.map.RequestMap;

/**
 *
 * @author zoli
 */
public class PlayerAvatarModel extends AbstractOnlineModel<PlayerAvatarEvent, PlayerAvatarData> implements PlayerAvatarKeys {

    public PlayerAvatarModel(Connection<Object, Object> connection) {
        super(connection, ModelKeys.PLAYER_AVATAR, PlayerAvatarEvent.class, PlayerAvatarData.class);
    }
    
    public InputStream createAvatarImage(int scale) {
        return createAvatarImage(null, scale);
    }
    
    public InputStream createAvatarImage(String user, int scale) {
        return getImage(createCreateAvatarRequest(user, scale));
    }
    
    public void createAvatarImage(int scale, ModelActionListener<InputStream> callback) {
        createAvatarImage(null, scale, callback);
    }
    
    public void createAvatarImage(String user, int scale, ModelActionListener<InputStream> callback) {
        getImage(createCreateAvatarRequest(user, scale), callback);
    }
    
    public InputStream getAvatarImage() {
        return getImage(createGetAvatarRequest());
    }
    
    public void getAvatarImage(ModelActionListener<InputStream> callback) {
        getImage(createGetAvatarRequest(), callback);
    }
    
    public int setAvatar(ByteArrayOutputStream stream, CountingListener l) {
        return setImage(stream, createSetAvatarRequest(), l);
    }
    
    public void setAvatar(ByteArrayOutputStream stream, ModelActionListener<Integer> callback, CountingListener l) {
        setImage(stream, createSetAvatarRequest(), callback, l);
    }
    
    public int setAvatarEnabled(boolean enable) {
        return setProperty(createSetAvatarEnableRequest(enable));
    }
    
    public void setAvatarEnabled(boolean enable, ModelActionListener<Integer> callback) {
        setProperty(createSetAvatarEnableRequest(enable), callback);
    }
    
    public int setAvatarAttrs(int x, int y, int scale) {
        return setProperty(createSetAvatarAttrsRequest(x, y, scale));
    }
    
    public void setAvatarAttrs(int x, int y, int scale, ModelActionListener<Integer> callback) {
        setProperty(createSetAvatarAttrsRequest(x, y, scale), callback);
    }
    
    public int removeAvatar() {
        return askModel(createRemoveAvatarRequest());
    }
    
    public void removeAvatar(ModelActionListener<Integer> callback) {
        askModel(createRemoveAvatarRequest(), callback);
    }
    
    private RequestMap createCreateAvatarRequest(String user, int scale) {
        return new RequestMap()
        .setFirst(KEY_REQUEST, REQ_GET_AVATAR)
        .setFirst(KEY_USER, user)
        .setFirst(KEY_SCALE, Integer.toString(scale));
    }
    
    private RequestMap createGetAvatarRequest() {
        return new RequestMap().setFirst(KEY_REQUEST, REQ_GET_AVATAR);
    }
    
    private RequestMap createSetAvatarRequest() {
        return new RequestMap().setFirst(KEY_REQUEST, REQ_SET_AVATAR);
    }
    
    private RequestMap createSetAvatarAttrsRequest(int x, int y, int scale) {
        return new RequestMap()
        .setFirst(KEY_REQUEST, REQ_SET_AVATAR_ATTRS)
        .setFirst(KEY_X, Integer.toString(x))
        .setFirst(KEY_Y, Integer.toString(y))
        .setFirst(KEY_SCALE, Integer.toString(scale));
    }
    
    private RequestMap createSetAvatarEnableRequest(boolean enabled) {
        return new RequestMap()
        .setFirst(KEY_REQUEST, REQ_SET_AVATAR)
        .setFirst(KEY_ENABLE, Boolean.toString(enabled));
    }
    
    private RequestMap createRemoveAvatarRequest() {
        return new RequestMap()
        .setFirst(KEY_REQUEST, REQ_REMOVE_AVATAR);
    }
    
}