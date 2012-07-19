package org.dyndns.fzoli.mvc.client.android.activity;

import org.dyndns.fzoli.android.context.activity.AbstractNetworkInfoExpandableListActivity;
import org.dyndns.fzoli.android.context.activity.NetworkInfoActivityUtil;
import org.dyndns.fzoli.mvc.client.android.service.ConnectionService;
import org.dyndns.fzoli.mvc.client.android.service.ModelMap;
import org.dyndns.fzoli.mvc.client.connection.Connection;
import org.dyndns.fzoli.mvc.client.event.ModelActionEvent;
import org.dyndns.fzoli.mvc.client.event.ModelChangeEvent;
import org.dyndns.fzoli.mvc.client.model.CachedModel;

import android.os.Bundle;

public abstract class AbstractConnectionExpandableListActivity<EventType, PropsType, EventObj, PropsObj> extends AbstractNetworkInfoExpandableListActivity implements ConnectionActivity<EventType, PropsType, EventObj, PropsObj> {
	
	@Override
	protected NetworkInfoActivityUtil createContextUtil() {
		return new ConnectionActivityUtil<EventType, PropsType, EventObj, PropsObj>(this);
	}
	
	@Override
	@SuppressWarnings("unchecked")
	protected ConnectionActivityUtil<EventType, PropsType, EventObj, PropsObj> getContextUtil() {
		return (ConnectionActivityUtil<EventType, PropsType, EventObj, PropsObj>) super.getContextUtil();
	}
	
	/* Felüldefiniált Activity metódusok */
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getContextUtil().onCreate(savedInstanceState);
	}
	
	/* Kiegészítő Activity metódusok */
	
	@Override
	public CachedModel<EventType, PropsType, EventObj, PropsObj> getModel() {
		return getContextUtil().getModel();
	}
	
	@Override
	public ModelMap<EventType, PropsType> getModelMap() {
		return getContextUtil().getModelMap();
	}
	
	@Override
	public void setModelListener(boolean enable) {
		getContextUtil().setModelListener(enable);
	}

	@Override
	public void startConnectionService() {
		getContextUtil().startConnectionService();
	}

	@Override
	public void stopConnectionService() {
		getContextUtil().stopConnectionService();
	}
	
	@Override
	public void rebindConnectionService() {
		getContextUtil().rebindConnectionService();
	}
	
	@Override
	public void recreateConnection() {
		getContextUtil().recreateConnection();
	}
	
	@Override
	public void reinitModel() {
		getContextUtil().reinitModel();
	}
	
	/* Kiegészítő Activity inicializáló-metódusok */
	
	@Override
	public abstract CachedModel<EventType, PropsType, EventObj, PropsObj> createModel(Connection<EventType, PropsType> connection);

	@Override
	public abstract Class<? extends ConnectionService<EventType, PropsType>> getConnectionServiceClass();
	
	/* Kiegészítő Activity eseménykezelő-metódusok */
	
	@Override
	public boolean onPrepareModelCreate(ModelActionEvent<PropsObj> e) {
		return true;
	}
	
	@Override
	public boolean onConnectionBinded() {
		return true;
	}
	
	@Override
	public void onModelPrepare() {
		;
	}
	
	@Override
	public boolean onModelCreated(ModelActionEvent<PropsObj> e) {
		return true;
	}
	
	@Override
	public void onModelChanged(ModelChangeEvent<EventObj> e) {
		;
	}
	
	@Override
	public void onModelCacheReinit() {
		;
	}
	
	@Override
	public void onModelListenerSet(boolean enable, boolean success) {
		;
	}
	
	@Override
	public void onNetworkChanged(boolean replaced) {
		;
	}
	
}