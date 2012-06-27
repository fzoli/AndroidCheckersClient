package org.dyndns.fzoli.mvc.client.android.activity;

import org.dyndns.fzoli.android.context.activity.NetworkInfoActivity;
import org.dyndns.fzoli.mvc.client.android.service.ConnectionService;
import org.dyndns.fzoli.mvc.client.android.service.ModelMap;
import org.dyndns.fzoli.mvc.client.connection.Connection;
import org.dyndns.fzoli.mvc.client.event.ModelActionEvent;
import org.dyndns.fzoli.mvc.client.event.ModelChangeEvent;
import org.dyndns.fzoli.mvc.client.event.type.ModelActionEventType;
import org.dyndns.fzoli.mvc.client.event.type.ModelChangeEventType;
import org.dyndns.fzoli.mvc.client.model.CachedModel;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;

public interface ConnectionActivity<EventType, PropsType, EventObj, PropsObj> extends NetworkInfoActivity, ModelChangeEventType, ModelActionEventType {
	
	/* Activity metódusok */
	
	Context getApplicationContext();
	
	ComponentName startService(Intent service);
	
	boolean stopService(Intent name);
	
	boolean bindService(Intent service, ServiceConnection conn, int flags);
	
	void unbindService(ServiceConnection conn);
	
	/* Felüldefiniálandó Activity metódusok */
	
	void onCreate(Bundle savedInstanceState);
	
	/* Kiegészítő Activity metódusok */
	
	CachedModel<EventType, PropsType, EventObj, PropsObj> getModel();
	
	ModelMap<EventType, PropsType> getModelMap();
	
	void setModelListener(boolean enable);
	
	void startConnectionService();
    
    void stopConnectionService();
    
    void rebindConnectionService();
    
    void recreateConnection();
    
    /* Kiegészítő Activity inicializáló-metódusok */
    
    CachedModel<EventType, PropsType, EventObj, PropsObj> createModel(Connection<EventType, PropsType> connection);
    
    Class<? extends ConnectionService<EventType, PropsType>> getConnectionServiceClass();
    
    /* Kiegészítő Activity eseménykezelő-metódusok */
    
    boolean onPrepareModelCreate(ModelActionEvent<PropsObj> e);
    
    boolean onConnectionBinded();
    
    void onModelPrepare();
    
    boolean onModelCreated(ModelActionEvent<PropsObj> e);
    
    void onModelChanged(ModelChangeEvent<EventObj> e);
    
    void onModelCacheReinit();
    
    void onModelListenerSet(boolean enable, boolean success);
    
}