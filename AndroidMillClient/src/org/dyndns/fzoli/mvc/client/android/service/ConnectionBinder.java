package org.dyndns.fzoli.mvc.client.android.service;

import java.util.List;

import org.dyndns.fzoli.mvc.client.android.activity.ConnectionActivity;
import org.dyndns.fzoli.mvc.client.connection.Connection;

import android.os.Binder;

public class ConnectionBinder<EventType, PropsType> extends Binder {
	
	private final ConnectionService<EventType, PropsType> SERVICE;
	
	public ConnectionBinder(ConnectionService<EventType, PropsType> service) {
		SERVICE = service;
	}
	
	public Connection<EventType, PropsType> getConnection() {
		return getService().getConnection();
	}
	
	public void recreateConnection() {
		getService().recreateConnection();
	}
	
	public ModelMap<EventType, PropsType> getModelMap() {
		return getService().getModelMap();
	}
	
	@SuppressWarnings("unchecked")
	public List<ConnectionActivity> getActivityList() {
		return getService().getActivityList();
	}
	
	protected ConnectionService<EventType, PropsType> getService() {
		return SERVICE;
	}
	
}