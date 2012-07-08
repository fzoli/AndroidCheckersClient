package org.dyndns.fzoli.mill.client.model;

import org.dyndns.fzoli.mill.common.key.ModelKeys;
import org.dyndns.fzoli.mill.common.key.PlayerAvatarKeys;
import org.dyndns.fzoli.mill.common.model.pojo.PlayerAvatarData;
import org.dyndns.fzoli.mill.common.model.pojo.PlayerAvatarEvent;
import org.dyndns.fzoli.mvc.client.connection.Connection;

/**
 *
 * @author zoli
 */
public class PlayerAvatarModel extends AbstractOnlineModel<PlayerAvatarEvent, PlayerAvatarData> implements PlayerAvatarKeys {

    public PlayerAvatarModel(Connection<Object, Object> connection) {
        super(connection, ModelKeys.PLAYER_AVATAR, PlayerAvatarEvent.class, PlayerAvatarData.class);
    }
    
}