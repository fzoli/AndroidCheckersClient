package org.dyndns.fzoli.mvc.client.android.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.dyndns.fzoli.mvc.client.android.activity.ConnectionActivity;
import org.dyndns.fzoli.mvc.client.event.ModelChangeListener;
import org.dyndns.fzoli.mvc.client.event.ModelStateEvent;
import org.dyndns.fzoli.mvc.client.event.ModelStateListener;
import org.dyndns.fzoli.mvc.client.model.CachedModel;
import org.dyndns.fzoli.mvc.common.map.CommonMap;

@SuppressWarnings("unchecked")
public class ModelMap<EventType, PropsType> extends CommonMap<Class<? extends ConnectionActivity>, CachedModel<EventType, PropsType, ?, ?>> {
	
	private static final long serialVersionUID = 1L;
	
	private final ConnectionService<EventType, PropsType> SERVICE;
	
	private final Map<Class<? extends ConnectionActivity>, List<ModelChangeListener>> LISTENERS = new HashMap<Class<? extends ConnectionActivity>, List<ModelChangeListener>>();
	
	private final List<ModelChangeListener> LAZY_LISTENERS = new ArrayList<ModelChangeListener>();
	
	private final ModelStateListener STATE_LISTENER = new ModelStateListener() {
		
		@Override
		public void actionListenerChanged(ModelStateEvent ev) {
			try {
				CachedModel m = (CachedModel) ev.getSourceModel();
				Class<? extends ConnectionActivity> key = getKey(m);
				if (key == null) return;
				synchronized (LISTENERS) {
					List<ModelChangeListener> ls = LISTENERS.get(key);
					if (ls == null) return;
					for (ModelChangeListener l : LAZY_LISTENERS) {
						if (!ls.contains(l)) continue;
						switch (ev.getType()) {
							case ModelStateEvent.TYPE_ADD:
								if (!m.getListeners().contains(l)) m.addListener(l);
								break;
							case ModelStateEvent.TYPE_REMOVE:
								m.removeListener(l);
								break;
						}
					}
				}
			}
			catch (ClassCastException ex) {
				;
			}
		}
		
	};
	
	public ModelMap(ConnectionService<EventType, PropsType> service) {
		SERVICE = service;
	}
	
	protected ConnectionService<EventType, PropsType> getService() {
		return SERVICE;
	}
	
	public void addExtraModelChangeListener(Class<? extends ConnectionActivity> key, ModelChangeListener l, boolean lazy) {
		if (key == null || l == null) return;
		synchronized (LISTENERS) {
			List<ModelChangeListener> ls = LISTENERS.get(key);
			if (ls == null) {
				ls = new ArrayList<ModelChangeListener>();
				LISTENERS.put(key, ls);
			}
			if (!ls.contains(l)) ls.add(l);
			if (lazy) LAZY_LISTENERS.add(l);
		}
		synchronized (this) {
			CachedModel<EventType, PropsType, ?, ?> m = get(key);
			if (m != null) {
				if (!m.getListeners().contains(l)) m.addListener(l);
			}
		}
	}
	
	public void removeExtraModelChangeListener(Class<? extends ConnectionActivity> key, ModelChangeListener l) {
		if (key == null) return;
		synchronized (LISTENERS) {
			List<ModelChangeListener> ls = LISTENERS.get(key);
			if (ls == null) return;
			ls.remove(l);
			LAZY_LISTENERS.remove(l);
		}
		synchronized (this) {
			CachedModel<EventType, PropsType, ?, ?> m = get(key);
			if (m != null) {
				m.removeListener(l);
			}
		}
	}
	
	@Override
	public CachedModel<EventType, PropsType, ?, ?> put(Class<? extends ConnectionActivity> key, CachedModel<EventType, PropsType, ?, ?> value) {
		synchronized (this) {
			if (getService().onModelPut(key, value)) {
				if (value != null && key != null) {
					List<ModelChangeListener> ls = LISTENERS.get(key);
					if (ls != null) {
						for (ModelChangeListener l : ls) {
							value.addListener(l);
						}
					}
					value.addStateListener(getService().getModelStateListener());
					value.addStateListener(STATE_LISTENER);
				}
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
				if (m != null) {
					m.removeStateListener(getService().getModelStateListener());
					m.removeStateListener(STATE_LISTENER);
					List<ModelChangeListener> ls = LISTENERS.get(key);
					if (ls != null) {
						for (ModelChangeListener l : ls) {
							m.removeListener(l);
						}
					}
				}
				return super.remove(key);
			}
		}
		catch (Exception ex) {
			return super.remove(o);
		}
	}
	
	public Class<? extends ConnectionActivity> getKey(CachedModel m) {
		synchronized (this) {
			Iterator<Entry<Class<? extends ConnectionActivity>, CachedModel<EventType, PropsType, ?, ?>>> it = entrySet().iterator();
			while (it.hasNext()) {
				Entry<Class<? extends ConnectionActivity>, CachedModel<EventType, PropsType, ?, ?>> e = it.next();
				if (e.getValue().equals(m)) {
					return e.getKey();
				}
			}
		}
		return null;
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