package org.dyndns.fzoli.mill.android;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.dyndns.fzoli.http.android.DefaultHttpExecutor;
import org.dyndns.fzoli.mill.android.entity.ConnectionSettings;
import org.dyndns.fzoli.mill.android.service.MillConnectionBinder;
import org.dyndns.fzoli.mill.android.service.MillDatabaseHelper;
import org.dyndns.fzoli.mill.client.model.ChatModel;
import org.dyndns.fzoli.mill.client.model.PlayerModel;
import org.dyndns.fzoli.mill.common.key.MillServletURL;
import org.dyndns.fzoli.mill.common.model.entity.Message;
import org.dyndns.fzoli.mill.common.model.pojo.ChatEvent;
import org.dyndns.fzoli.mill.common.model.pojo.PlayerEvent;
import org.dyndns.fzoli.mvc.client.android.activity.ConnectionActivity;
import org.dyndns.fzoli.mvc.client.android.service.AbstractConnectionService;
import org.dyndns.fzoli.mvc.client.connection.Connection;
import org.dyndns.fzoli.mvc.client.connection.JSONConnection;
import org.dyndns.fzoli.mvc.client.event.ModelChangeEvent;
import org.dyndns.fzoli.mvc.client.event.ModelChangeListener;
import org.dyndns.fzoli.mvc.client.model.Model;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.db4o.Db4oEmbedded;
import com.db4o.EmbeddedObjectContainer;

public class MillConnectionService extends AbstractConnectionService<Object, Object> {
	
	private static final int MODE_SIGNED_IN = 1, MODE_CHAT_MESSAGE = 2;
	
	private Notification playerNotification;
	private NotificationManager notificationManager;
	
	private final HashMap<String, Notification> notifies = new HashMap<String, Notification>();
	
	private EmbeddedObjectContainer db;
	private MillDatabaseHelper helper;
	private boolean started = false;
	
	private PlayerModel playerModel;
	private ChatModel chatModel;
	
	private final ModelChangeListener<PlayerEvent> playerEventHandler = new ModelChangeListener<PlayerEvent>() {
		
		@Override
		public void fireModelChanged(ModelChangeEvent<PlayerEvent> e) {
			switch (e.getType()) {
				case ModelChangeEvent.TYPE_SERVER_LOST:
					setNotificationVisible(false);
					break;
				case ModelChangeEvent.TYPE_SERVER_RECONNECT:
					setNotificationVisible(true);
			}
		}
		
	};
	
	private final ModelChangeListener<ChatEvent> chatEventHandler = new ModelChangeListener<ChatEvent>() {

		@Override
		public void fireModelChanged(ModelChangeEvent<ChatEvent> e) {
			if (e.getType() == ModelChangeEvent.TYPE_EVENT) {
				Message m = e.getEvent().getMessage();
				addChatNotification(m.getSender());
				m.setSendDate(new Date());
				List<Message> l = getConnectionBinder().getMessages().get(m.getSender());
				if (l != null) {
					l.add(m);
				}
			}
		}
		
	};
	
	@Override
	@SuppressWarnings("unchecked")
	public boolean onModelPut(Class<? extends ConnectionActivity> key, Model<Object, Object, ?, ?> value) {
		if (value instanceof PlayerModel) {
			if (playerModel != null) playerModel.removeListener(playerEventHandler);
			playerModel = (PlayerModel) value;
			playerModel.addListener(playerEventHandler);
		}
		if (value instanceof ChatModel) {
			if (chatModel != null) chatModel.removeListener(chatEventHandler);
			chatModel = (ChatModel) value;
			chatModel.addListener(chatEventHandler);
		}
		return super.onModelPut(key, value);
	}
	
	@Override
	public MillConnectionBinder createConnectionBinder() {
		return new MillConnectionBinder(this);
	}
	
	@Override
	public MillConnectionBinder getConnectionBinder() {
		return (MillConnectionBinder) super.getConnectionBinder();
	}
	
	@Override
	public Connection<Object, Object> createConnection() {
		initHelper();
		ConnectionSettings settings = helper.getConnectionSettings();
		System.setProperty("networkaddress.cache.ttl", "0");
		System.setProperty("networkaddress.cache.negative.ttl", "0");
		Connection<Object, Object> c = new JSONConnection(settings.getUrl(), new DefaultHttpExecutor(),"Mill", MillServletURL.CONTROLLER, MillServletURL.LISTENER);
		if (settings.isAcceptInvalidCert()) c.getHttpExecutor().wrapHttpClient();
		return c;
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		started = true;
		return super.onStartCommand(intent, flags, startId);
	}
	
	@Override
	public void onDestroy() {
		closeHelper();
		getModelMap().free();
		super.onDestroy();
	}
	
	@Override
	public boolean onUnbind(Intent intent) {
		if (!started && db != null) {
			closeHelper();
		}
		return super.onUnbind(intent);
	}
	
	public MillDatabaseHelper getDatabaseHelper() {
		return helper;
	}
	
	public void initHelper() {
		if (helper != null) return;
		File dbFile = new File(getDir("database", Context.MODE_PRIVATE), "settings.odb");
		if (!dbFile.exists()) try {
			dbFile.createNewFile();
		}
		catch (IOException e) {
			throw new RuntimeException(e);
		}
		db = Db4oEmbedded.openFile(Db4oEmbedded.newConfiguration(), dbFile.getAbsolutePath());
		helper = new MillDatabaseHelper(db);
	}
	
	public void closeHelper() {
		db.close();
		db = null;
		helper = null;
	}
	
	public void setNotificationVisible(boolean visible) {
		if (visible) {
			String text = getString(R.string.signed_in) + ": " + playerModel.getCache().getPlayer().getName();
			playerNotification = new Notification(R.drawable.ic_stat_notify, text, System.currentTimeMillis());
			Intent notificationIntent = new Intent(this, SignInActivity.class);
			PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
			playerNotification.setLatestEventInfo(this, getString(R.string.app_name), text, pendingIntent);
			startForeground(MODE_SIGNED_IN, playerNotification);
		}
		else {
			if (playerNotification != null) {
				stopForeground(true);
				playerNotification = null;
			}
		}
	}
	
	private void addChatNotification(String playerName) {
		if (notificationManager == null) {
			notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		}
		if (ChatActivity.ACTIVE_PLAYERS.contains(playerName)) {
			return;
		}
		int count;
		try {
			count = chatModel.getCache().getUnreadedCount().get(playerName);
		}
		catch (Exception ex) {
			count = 0;
		}
		String text = getString(R.string.new_message1) + ' ' + count + ' ' + getString(count > 1 ? R.string.new_message2 : R.string.new_message3);
		Notification notification = notifies.get(playerName);
		if (notification != null) {
			notifies.remove(playerName);
			notificationManager.cancel(playerName, MODE_CHAT_MESSAGE);
		}
		notification = new Notification(R.drawable.ic_stat_notify, text, System.currentTimeMillis());
		notification.flags |= Notification.FLAG_AUTO_CANCEL;
		Intent notificationIntent = new Intent(this, ChatActivity.class);
		notificationIntent.putExtra(ChatActivity.KEY_PLAYER, playerName);
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_ONE_SHOT);
		notification.setLatestEventInfo(getApplicationContext(), getString(R.string.chat) + " - " + ChatActivity.getDisplayName(playerModel, playerName), text, contentIntent);
		notificationManager.notify(playerName, MODE_CHAT_MESSAGE, notification);
		notifies.put(playerName, notification);
	}
	
}