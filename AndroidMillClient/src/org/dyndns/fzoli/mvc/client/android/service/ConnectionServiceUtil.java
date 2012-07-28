package org.dyndns.fzoli.mvc.client.android.service;

import java.util.ArrayList;
import java.util.List;

import org.dyndns.fzoli.android.context.NetworkInfoContextUtil;
import org.dyndns.fzoli.mvc.client.android.activity.ConnectionActivity;
import org.dyndns.fzoli.mvc.client.connection.Connection;
import org.dyndns.fzoli.mvc.client.event.ModelStateEvent;
import org.dyndns.fzoli.mvc.client.event.ModelStateListener;
import org.dyndns.fzoli.mvc.client.model.Model;

import android.content.Intent;
import android.os.IBinder;

public class ConnectionServiceUtil<EventType, PropsType> extends NetworkInfoContextUtil implements ConnectionService<EventType, PropsType> {

	private Connection<EventType, PropsType> connection;
	
	private final ConnectionBinder<EventType, PropsType> BINDER = createConnectionBinder();
	private final ModelMap<EventType, PropsType> MODELS = new ModelMap<EventType, PropsType>(this);
	
	@SuppressWarnings("unchecked")
	private final List<ConnectionActivity> ACTIVITIES = new ArrayList<ConnectionActivity>();
	
	private final ModelStateListener STATE_LISTENER = new ModelStateListener() {

		@Override
		public void actionListenerChanged(ModelStateEvent e) {
			onModelStateChange(e);
		}
		
	};
	
	public ConnectionServiceUtil(ConnectionService<EventType, PropsType> context) {
		super(context);
	}
	
	@Override
	@SuppressWarnings("unchecked")
	protected ConnectionService<EventType, PropsType> getContext() {
		return (ConnectionService<EventType, PropsType>) super.getContext();
	}
	
	/* Kiegészítő Service metódusok */
	
	@Override
	public void recreateConnection() {
		connection.close();
		connection = createConnection();
		getModelMap().clear();
		rebindActivities();
	}

	@Override
	public Connection<EventType, PropsType> getConnection() {
		return connection;
	}

	@Override
	public ModelMap<EventType, PropsType> getModelMap() {
		return MODELS;
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<ConnectionActivity> getActivityList() {
		return ACTIVITIES;
	}

	@Override
	public ModelStateListener getModelStateListener() {
		return STATE_LISTENER;
	}

	/* Kiegészítő Service inicializáló-metódusok */
	
	@Override
	public Connection<EventType, PropsType> createConnection() {
		return getContext().createConnection();
	}
	
	@Override
	public ConnectionBinder<EventType, PropsType> createConnectionBinder() {
		return getContext().createConnectionBinder();
	}
	
	/* Felüldefiniálandó hívandó Service eseménykezelő-metódusok */
	
	@Override
	public IBinder onBind(Intent intent) {
		if (connection == null) connection = createConnection();
		return BINDER;
	}

	/* Felüldefiniálandó hívó Service eseménykezelő-metódusok */
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if (connection == null) connection = createConnection();
		return getContext().onStartCommand(intent, flags, startId);
	}
	
	/* Kiegészítő Service eseménykezelő-metódusok */
	
	@Override
	public void onModelStateChange(ModelStateEvent e) {
		getContext().onModelStateChange(e);
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public boolean onModelPut(Class<? extends ConnectionActivity> key, Model<EventType, PropsType, ?, ?> value) {
		return getContext().onModelPut(key, value);
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public boolean onModelRemove(Class<? extends ConnectionActivity> key, Model<EventType, PropsType, ?, ?> value) {
		return getContext().onModelRemove(key, value);
	}
	
	/* Rejtett metódusok */
	
	@SuppressWarnings("unchecked")
	protected void rebindActivities() {
		List<ConnectionActivity> l = new ArrayList<ConnectionActivity>(getActivityList());
		for (ConnectionActivity a : l) {
			a.rebindConnectionService();
		}
	}
	
}