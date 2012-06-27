package org.dyndns.fzoli.mvc.client.android.service;

import org.dyndns.fzoli.android.context.service.AbstractNetworkInfoService;
import org.dyndns.fzoli.mvc.client.android.activity.ConnectionActivity;
import org.dyndns.fzoli.mvc.client.connection.Connection;
import org.dyndns.fzoli.mvc.client.event.ModelStateEvent;
import org.dyndns.fzoli.mvc.client.event.ModelStateListener;
import org.dyndns.fzoli.mvc.client.event.type.ModelStateType;
import org.dyndns.fzoli.mvc.client.model.Model;

import android.content.Intent;
import android.os.IBinder;

public abstract class AbstractConnectionService<EventType, PropsType> extends AbstractNetworkInfoService implements ConnectionService<EventType, PropsType>, ModelStateType {
	
	private final ConnectionServiceUtil<EventType, PropsType> UTIL;
	
	public AbstractConnectionService() {
		UTIL = createServiceUtil();
	}
	
	protected ConnectionServiceUtil<EventType, PropsType> createServiceUtil() {
		return new ConnectionServiceUtil<EventType, PropsType>(this);
	}
	
	protected ConnectionServiceUtil<EventType, PropsType> getServiceUtil() {
		return UTIL;
	}
	
	/* Kiegészítő Service metódusok */
	
	@Override
	public void recreateConnection() {
		getServiceUtil().recreateConnection();
	}
	
	@Override
	public Connection<EventType, PropsType> getConnection() {
		return getServiceUtil().getConnection();
	}
	
	@Override
	public ModelMap<EventType, PropsType> getModelMap() {
		return getServiceUtil().getModelMap();
	}
	
	@Override
	public ModelStateListener getModelStateListener() {
		return getServiceUtil().getModelStateListener();
	}
	
	/* Kiegészítő Service inicializáló-metódusok */
	
	@Override
	public abstract Connection<EventType, PropsType> createConnection();
	
	@Override
	public ConnectionBinder<EventType, PropsType> createConnectionBinder() {
		return new ConnectionBinder<EventType, PropsType>(this);
	}
	
	/* Service eseménykezelő-metódusok */
	
	@Override
	public IBinder onBind(Intent intent) {
		return getServiceUtil().onBind(intent);
	}
	
	@SuppressWarnings("unchecked")
	public ConnectionBinder<EventType, PropsType> getConnectionBinder() {
		return (ConnectionBinder<EventType, PropsType>) onBind(null);
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		return START_STICKY;
	}
	
	/* Kiegészítő Service eseménykezelő-metódusok */
	
	@Override
	public void onModelStateChange(ModelStateEvent e) {
		;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public boolean onModelPut(Class<? extends ConnectionActivity> key, Model<EventType, PropsType, ?, ?> value) {
		return true;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public boolean onModelRemove(Class<? extends ConnectionActivity> key, Model<EventType, PropsType, ?, ?> value) {
		return true;
	}
	
}