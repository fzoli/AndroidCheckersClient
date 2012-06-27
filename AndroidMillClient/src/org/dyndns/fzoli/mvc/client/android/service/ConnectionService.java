package org.dyndns.fzoli.mvc.client.android.service;

import org.dyndns.fzoli.android.context.NetworkInfoContext;
import org.dyndns.fzoli.mvc.client.android.activity.ConnectionActivity;
import org.dyndns.fzoli.mvc.client.connection.Connection;
import org.dyndns.fzoli.mvc.client.event.ModelStateEvent;
import org.dyndns.fzoli.mvc.client.event.ModelStateListener;
import org.dyndns.fzoli.mvc.client.model.Model;

import android.content.Intent;
import android.os.IBinder;

public interface ConnectionService<EventType, PropsType> extends NetworkInfoContext {
	
	/* Kiegészítő Service metódusok */
	
	void recreateConnection();
	
	Connection<EventType, PropsType> getConnection();
	
	ModelMap<EventType, PropsType> getModelMap();
	
	ModelStateListener getModelStateListener();
	
	/* Kiegészítő Service inicializáló-metódusok */
	
	Connection<EventType, PropsType> createConnection();
	
	ConnectionBinder<EventType, PropsType> createConnectionBinder();
	
	/* Felüldefiniálandó meghívandó Service eseménykezelő-metódusok */
	
	IBinder onBind(Intent intent);
	
	/* Felüldefiniálandó hívó Service eseménykezelő-metódusok */
	
	int onStartCommand(Intent intent, int flags, int startId);
	
	/* Kiegészítő Service eseménykezelő-metódusok */
	
	void onModelStateChange(ModelStateEvent e);
	
	@SuppressWarnings("unchecked")
	boolean onModelPut(Class<? extends ConnectionActivity> key, Model<EventType, PropsType, ?, ?> value);
	
	@SuppressWarnings("unchecked")
	boolean onModelRemove(Class<? extends ConnectionActivity> key, Model<EventType, PropsType, ?, ?> value);
	
}