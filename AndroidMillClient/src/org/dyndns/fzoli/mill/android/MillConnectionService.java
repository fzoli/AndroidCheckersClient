package org.dyndns.fzoli.mill.android;

import java.io.File;
import java.io.IOException;

import org.dyndns.fzoli.http.android.DefaultHttpExecutor;
import org.dyndns.fzoli.mill.android.entity.ConnectionSettings;
import org.dyndns.fzoli.mill.android.service.MillConnectionBinder;
import org.dyndns.fzoli.mill.android.service.MillDatabaseHelper;
import org.dyndns.fzoli.mill.client.model.PlayerModel;
import org.dyndns.fzoli.mill.common.key.MillServletURL;
import org.dyndns.fzoli.mill.common.model.pojo.PlayerEvent;
import org.dyndns.fzoli.mvc.client.android.activity.ConnectionActivity;
import org.dyndns.fzoli.mvc.client.android.service.AbstractConnectionService;
import org.dyndns.fzoli.mvc.client.connection.Connection;
import org.dyndns.fzoli.mvc.client.connection.JSONConnection;
import org.dyndns.fzoli.mvc.client.event.ModelChangeEvent;
import org.dyndns.fzoli.mvc.client.event.ModelChangeListener;
import org.dyndns.fzoli.mvc.client.model.Model;

import android.content.Context;
import android.content.Intent;

import com.db4o.Db4oEmbedded;
import com.db4o.EmbeddedObjectContainer;

public class MillConnectionService extends AbstractConnectionService<Object, Object> {
	
	private EmbeddedObjectContainer db;
	private MillDatabaseHelper helper;
	private boolean started = false;
	
	private PlayerModel playerModel;
	
	private final ModelChangeListener<PlayerEvent> playerEventHandler = new ModelChangeListener<PlayerEvent>() {
		
		@Override
		public void fireModelChanged(ModelChangeEvent<PlayerEvent> e) {
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
		if (helper == null) initHelper();
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
	
}