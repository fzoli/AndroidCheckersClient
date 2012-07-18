package org.dyndns.fzoli.mill.android;

import org.dyndns.fzoli.mill.android.activity.AbstractMillOnlineExpandableListActivity;
import org.dyndns.fzoli.mill.client.model.ChatModel;
import org.dyndns.fzoli.mill.common.model.pojo.ChatData;
import org.dyndns.fzoli.mill.common.model.pojo.ChatEvent;
import org.dyndns.fzoli.mvc.client.connection.Connection;

public class ChatActivity extends AbstractMillOnlineExpandableListActivity<ChatEvent, ChatData> {

	@Override
	public ChatModel createModel(Connection<Object, Object> connection) {
		return new ChatModel(connection);
	}

}