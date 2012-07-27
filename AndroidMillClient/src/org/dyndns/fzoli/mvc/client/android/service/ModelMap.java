package org.dyndns.fzoli.mvc.client.android.service;

import java.util.Iterator;

import org.dyndns.fzoli.mvc.client.android.activity.ConnectionActivity;
import org.dyndns.fzoli.mvc.client.model.CachedModel;
import org.dyndns.fzoli.mvc.common.map.CommonMap;

@SuppressWarnings("unchecked")
public class ModelMap<EventType, PropsType> extends CommonMap<Class<? extends ConnectionActivity>, CachedModel<EventType, PropsType, ?, ?>> {
	
	private static final long serialVersionUID = 1L;
	
	private final ConnectionService<EventType, PropsType> SERVICE;
	
	public ModelMap(ConnectionService<EventType, PropsType> service) {
		SERVICE = service;
	}
	
	protected ConnectionService<EventType, PropsType> getService() {
		return SERVICE;
	}
	
	@Override
	public CachedModel<EventType, PropsType, ?, ?> put(Class<? extends ConnectionActivity> key, CachedModel<EventType, PropsType, ?, ?> value) {
		synchronized (this) {
			if (getService().onModelPut(key, value)) {
				if (value != null) value.addStateListener(getService().getModelStateListener());
				return super.put(key, value);
			}
			else {
				return null;
			}
		}
	}
	
	@Override
	public CachedModel<EventType, PropsType, ?, ?> remove(Object o) {
		try {
			Class<? extends ConnectionActivity> key = (Class<? extends ConnectionActivity>) o;
			synchronized (this) {
				CachedModel<EventType, PropsType, ?, ?> m = get(key);
				getService().onModelRemove(key, m);
				if (m != null) m.removeStateListener(getService().getModelStateListener());
				return super.remove(key);
			}
		}
		catch (Exception ex) {
			return super.remove(o);
		}
	}
	
	public void free(Class<? extends ConnectionActivity> key) {
		synchronized (this) {
			CachedModel<EventType, PropsType, ?, ?> m = get(key);
			if (m != null) {
				m.removeListeners();
				m.getConnection().removeListener(m); //CachedModel nem teszi meg, de a többi model esetén ezt a metódust nem kell meghívni
				remove(key);
			}
		}
	}
	
	public void free() {
		Iterator<Class<? extends ConnectionActivity>> it = keySet().iterator();
		while (it.hasNext()) {
			free(it.next());
			free();
			break;
		}
	}
	
}