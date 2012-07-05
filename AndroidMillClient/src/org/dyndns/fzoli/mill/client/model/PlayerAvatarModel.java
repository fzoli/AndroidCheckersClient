package org.dyndns.fzoli.mill.client.model;

import org.dyndns.fzoli.mill.common.key.ModelKeys;
import org.dyndns.fzoli.mill.common.model.pojo.BaseOnlinePojo;
import org.dyndns.fzoli.mill.common.model.pojo.PlayerAvatarData;
import org.dyndns.fzoli.mvc.client.connection.Connection;

/**
 *
 * @author zoli
 */
public class PlayerAvatarModel extends AbstractOnlineModel<BaseOnlinePojo, PlayerAvatarData> {

    public PlayerAvatarModel(Connection<Object, Object> connection) {
        super(connection, ModelKeys.PLAYER_AVATAR, BaseOnlinePojo.class, PlayerAvatarData.class);
    }
    
}