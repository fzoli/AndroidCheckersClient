package org.dyndns.fzoli.mill.client.model;

import java.util.List;
import org.dyndns.fzoli.mill.common.model.pojo.BaseOnlinePojo;
import org.dyndns.fzoli.mvc.client.connection.Connection;

/**
 *
 * @author zoli
 */
public abstract class AbstractOnlineModel<EventObj extends BaseOnlinePojo, PropsObj extends BaseOnlinePojo> extends AbstractMillModel<EventObj, PropsObj> {

    public AbstractOnlineModel(Connection<Object, Object> connection, String modelKey, Class<EventObj> eventClass, Class<PropsObj> propsClass) {
        super(connection, modelKey, eventClass, propsClass);
    }

    @Override
    protected void updateCache(List<EventObj> list, PropsObj po) {
        po.setPlayerName(list.get(list.size() - 1).getPlayerName());
    }
    
}
