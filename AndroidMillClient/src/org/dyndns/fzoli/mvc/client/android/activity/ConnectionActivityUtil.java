package org.dyndns.fzoli.mvc.client.android.activity;

import java.util.List;

import org.dyndns.fzoli.android.context.activity.NetworkInfoActivity;
import org.dyndns.fzoli.android.context.activity.NetworkInfoActivityUtil;
import org.dyndns.fzoli.mvc.client.android.service.ConnectionBinder;
import org.dyndns.fzoli.mvc.client.android.service.ConnectionService;
import org.dyndns.fzoli.mvc.client.android.service.ModelMap;
import org.dyndns.fzoli.mvc.client.connection.Connection;
import org.dyndns.fzoli.mvc.client.event.CachedModelChangeListener;
import org.dyndns.fzoli.mvc.client.event.ModelActionEvent;
import org.dyndns.fzoli.mvc.client.event.ModelActionListener;
import org.dyndns.fzoli.mvc.client.event.ModelChangeEvent;
import org.dyndns.fzoli.mvc.client.model.CachedModel;
import org.dyndns.fzoli.mvc.client.model.Model;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;

public class ConnectionActivityUtil<EventType, PropsType, EventObj, PropsObj> extends NetworkInfoActivityUtil implements NetworkInfoActivity, ConnectionActivity<EventType, PropsType, EventObj, PropsObj> {
	
	private boolean rebind, prepareCalled;
	private Intent connIntent;
	private ServiceConnection serviceConn;
	private ConnectionBinder<EventType, PropsType> connectionBinder;
	
	@SuppressWarnings("unchecked")
	private List<ConnectionActivity> activities;
	
	private CachedModel<EventType, PropsType, EventObj, PropsObj> model;
	
	private final ModelActionListener<PropsObj> MODEL_CREATE_LISTENER = new ModelActionListener<PropsObj>() {
		
		@Override
		public void modelActionPerformed(final ModelActionEvent<PropsObj> e) {
			if (onPrepareModelCreate(e)) {
				runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						switch (e.getType()) {
							case TYPE_EVENT:
								onModelCreated(e);
								if (!prepareCalled && model.isCacheOutdated()) {
						    		onModelChanged(new ModelChangeEvent<EventObj>(model, TYPE_SERVER_LOST, false));
						    	}
								setModelListener(true);
								break;
							default:
								onModelCreated(e);
								break;
						}
						activities.add(getContext());
					}
					
				});
			}
		}
		
	};
	
	private final CachedModelChangeListener<EventObj> MODEL_CHANGE_LISTENER = new CachedModelChangeListener<EventObj>() {

		@Override
		public void fireModelChanged(ModelChangeEvent<EventObj> e) {
			callOnModelChange(e);
		}

		@Override
		public void fireCacheReload(int type) {
			callOnModelCacheReinit();
		}
		
	};
    
	public ConnectionActivityUtil(ConnectionActivity<EventType, PropsType, EventObj, PropsObj> activity) {
		super(activity);
	} 
	
	@Override
	@SuppressWarnings("unchecked")
	protected ConnectionActivity<EventType, PropsType, EventObj, PropsObj> getContext() {
		return (ConnectionActivity<EventType, PropsType, EventObj, PropsObj>) super.getContext();
	}
	
	/* Alapértelmezett Activity metódusok */
	
	@Override
	public Context getApplicationContext() {
		return getContext().getApplicationContext();
	}
	
	@Override
	public Object getSystemService(String name) {
		return getContext().getSystemService(name);
	}
	
	@Override
	public Intent registerReceiver(BroadcastReceiver receiver, IntentFilter filter) {
		return getContext().registerReceiver(receiver, filter);
	}

	@Override
	public void unregisterReceiver(BroadcastReceiver receiver) {
		getContext().unregisterReceiver(receiver);
	}
	
	@Override
	public ComponentName startService(Intent service) {
		return getContext().startService(service);
	}
	
	@Override
	public boolean stopService(Intent name) {
		return getContext().stopService(name);
	}
	
	@Override
	public boolean bindService(Intent service, ServiceConnection conn, int flags) {
		return getContext().bindService(service, conn, flags);
	}
	
	@Override
	public void unbindService(ServiceConnection conn) {
		getContext().unbindService(conn);
	}

	@Override
	public void runOnUiThread(Runnable action) {
		getContext().runOnUiThread(action);
	}
	
	/* Felüldefiniálásban ős hívás után meghívandó Activity metódusok */
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		connIntent = new Intent(getApplicationContext(), getConnectionServiceClass());
        startConnectionService();
	}

	@Override
	public void onPause() {
		super.onPause();
		unbindConnectionService();
	}

	@Override
	public void onResume() {
		super.onResume();
		bindConnectionService();
	}
	
	/* Kiegészítő meghívandó Activity metódusok */
	
	@Override
	public CachedModel<EventType, PropsType, EventObj, PropsObj> getModel() {
		return model;
	}
	
	@Override
	public ModelMap<EventType, PropsType> getModelMap() {
    	return getConnectionBinder() == null ? null : getConnectionBinder().getModelMap();
    }
	
	@Override
	public void setModelListener(final boolean enable) {
		if (getModel() != null) {
	    	runOnUiThread(new Runnable() {
				
				@Override
				public void run() {
					boolean contains = getModel().getListeners().contains(MODEL_CHANGE_LISTENER);
					if (enable && !contains) onModelListenerSet(enable, getModel().addListener(MODEL_CHANGE_LISTENER));
					if (!enable && contains) onModelListenerSet(enable, getModel().removeListener(MODEL_CHANGE_LISTENER));
				}
				
			});
	    }
	}
	
	@Override
	public void startConnectionService() {
		startService(connIntent);
	}	

	@Override
	public void stopConnectionService() {
		stopService(connIntent);
	}
	
	@Override
	public void rebindConnectionService() {
		unbindConnectionService();
		bindConnectionService();
	}
	
	@Override
	public void recreateConnection() {
		if (connectionBinder != null) {
			connectionBinder.recreateConnection();
		}
	}
	
	/* Kiegészítő hívó Activity inicializáló-metódusok */
	
	@Override
	public CachedModel<EventType, PropsType, EventObj, PropsObj> createModel(Connection<EventType, PropsType> connection) {
		return getContext().createModel(connection);
	}

	@Override
	public Class<? extends ConnectionService<EventType, PropsType>> getConnectionServiceClass() {
		return getContext().getConnectionServiceClass();
	}
	
	/* Kiegészítő hívó Activity eseménykezelő-metódusok */
	
	@Override
	public boolean onPrepareModelCreate(ModelActionEvent<PropsObj> e) {
		return getContext().onPrepareModelCreate(e);
	}
	
	@Override
	public boolean onConnectionBinded() {
		return getContext().onConnectionBinded();
	}
	
	@Override
	public void onModelPrepare() {
		getContext().onModelPrepare();
	}
	
	@Override
	public boolean onModelCreated(ModelActionEvent<PropsObj> e) {
		return getContext().onModelCreated(e);
	}
	
	@Override
	public void onModelChanged(ModelChangeEvent<EventObj> e) {
		getContext().onModelChanged(e);
	}
	
	@Override
	public void onModelCacheReinit() {
		getContext().onModelCacheReinit();
	}
	
	@Override
	public void onModelListenerSet(boolean enable, boolean success) {
		getContext().onModelListenerSet(enable, success);
	}
	
	@Override
	public void onNetworkChanged(boolean replaced) {
		getContext().onNetworkChanged(replaced);
	}
	
	/* Rejtett metódusok */
	
	protected ConnectionBinder<EventType, PropsType> getConnectionBinder() {
		return connectionBinder;
	}
	
	@SuppressWarnings("unchecked")
	protected Class<? extends ConnectionActivity> getClassKey() {
		return getContext().getClass();
	}
	
    protected void bindConnectionService() {
    	rebind = true;
    	prepareCalled = false;
    	serviceConn = new ServiceConnection() {
    		
			@Override
			@SuppressWarnings("unchecked")
    		public void onServiceConnected(ComponentName name, IBinder binder) {
				connectionBinder = (ConnectionBinder<EventType, PropsType>)binder;
				if (!onConnectionBinded()) return;
				activities = connectionBinder.getActivityList();
				Class<? extends ConnectionActivity> clazz = getClassKey();
				Model<EventType, PropsType, ?, ?> o = connectionBinder.getModelMap().get(clazz);
				if (o != null) {
					model = (CachedModel<EventType, PropsType, EventObj, PropsObj>) o;
					if (model.willCacheReinit(true) || model.isCacheInitializing()) callOnModelPrepare();
				}
				else {
					model = createModel(connectionBinder.getConnection());
					callOnModelPrepare();
					connectionBinder.getModelMap().put(clazz, model);
				}
				callOnModelCreate();
    		}
    		
    		@Override
    		public void onServiceDisconnected(ComponentName name) {
    			clearOnUnbind();
    			if (rebind) bindConnectionService();
    		}
    		
    	};
    	bindService(connIntent, serviceConn, Context.BIND_AUTO_CREATE);
    }
    
    protected void unbindConnectionService() {
    	if (serviceConn != null) {
	    	rebind = false;
	    	unbindService(serviceConn);
	    	clearOnUnbind();
    	}
    }
    
    protected void clearOnUnbind() {
    	setModelListener(false);
    	if (activities != null) activities.remove(getContext());
    	serviceConn = null;
    	connectionBinder = null;
    }
    
    protected void callOnModelCreate() {
    	/* Nem dob esemény hibát, hogy elinduljon az eseményfigyelés.
    	   Ha hiba van, az eseményfigyelés újra kiváltja. */
    	model.getCache(MODEL_CREATE_LISTENER, true, false);
    }
    
    protected void callOnModelPrepare() {
    	prepareCalled = true;
    	runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				onModelPrepare();
			}
			
		});
    }
    
    protected void callOnModelChange(final ModelChangeEvent<EventObj> e) {
    	runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				onModelChanged(e);
			}
			
		});
    }
    
    protected void callOnModelCacheReinit() {
    	runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				onModelCacheReinit();
			}
			
		});
    }
    
}