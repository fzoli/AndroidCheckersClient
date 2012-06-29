package org.dyndns.fzoli.mill.android.service;

import java.util.ArrayList;
import java.util.List;

import org.dyndns.fzoli.http.HttpUrl;
import org.dyndns.fzoli.mill.android.entity.ConnectionSettings;
import org.dyndns.fzoli.mill.android.entity.UserInfo;
import org.dyndns.fzoli.mill.android.entity.UserInfoSettings;

import com.db4o.ObjectContainer;
import com.db4o.query.Predicate;

public class MillDatabaseHelper {
	
	private final ObjectContainer DB;
	private final HttpUrl DEFAULT_URL = new HttpUrl(true, "fzoli.dyndns.org", 8443);
	private final ConnectionSettings DEFAULT_CONN_SETTINGS = new ConnectionSettings(DEFAULT_URL, true);
	private final UserInfoSettings DEFAULT_USR_SETTINGS = new UserInfoSettings(true, false);
	
	public MillDatabaseHelper(ObjectContainer db) {
		DB = db;
	}
	
	public void delete(Object o) {
		try {
			DB.delete(o);
			DB.commit();
		}
		catch (Exception ex) {
			DB.rollback();
		}
	}
	
	public void store(Object o) {
		try {
			DB.store(o);
			DB.commit();
		}
		catch (Exception ex) {
			DB.rollback();
		}
	}
	
	public UserInfo findUserInfo(String user, List<UserInfo> ls) {
		for (UserInfo ui : ls) {
			if (ui.getUser().equals(user)) return ui;
		}
		return null;
	}
	
	public UserInfo findUserInfo(String user) {
		return findUserInfo(user, false);
	}
	
	public UserInfo findUserInfo(String user, boolean filter) {
		return findUserInfo(user, getUserInfoList(filter));
	}
	
	public List<UserInfo> getUserInfoList(final boolean filter) {
		final HttpUrl server = getConnectionSettings().getUrl();
		return new ArrayList<UserInfo>(DB.query(new Predicate<UserInfo>() {

			private static final long serialVersionUID = 1L;

			@Override
			public boolean match(UserInfo i) {
				if (!filter) return true;
				return server.equals(i.getServer());
			}
			
		}));
	}
	
	public ConnectionSettings getConnectionSettings() {
		List<ConnectionSettings> ls = DB.query(ConnectionSettings.class);
		ConnectionSettings s = ls.isEmpty() ? DEFAULT_CONN_SETTINGS : ls.get(0);
		if (!getServerList().contains(s.getUrl())) {
			s.setUrl(DEFAULT_URL);
			store(s);
		}
		return s;
	}
	
	public UserInfoSettings getUserInfoSettings() {
		List<UserInfoSettings> ls = DB.query(UserInfoSettings.class);
		UserInfoSettings s = ls.isEmpty() ? DEFAULT_USR_SETTINGS : ls.get(0);
		return s;
	}
	
	public void setConnectionSettings(ConnectionSettings settings) {
		ConnectionSettings s = getConnectionSettings();
		s.setAcceptInvalidCert(settings.isAcceptInvalidCert());
		s.setUrl(settings.getUrl());
		store(s);
	}
	
	public void setUserInfoSettings(UserInfoSettings settings) {
		UserInfoSettings s = getUserInfoSettings();
		s.setSaveUser(settings.isSaveUser());
		s.setSavePassword(settings.isSavePassword());
		s.setLastName(settings.getLastName());
		store(s);
	}
	
	public boolean equalsDefaultUrl(HttpUrl url) {
		return url.equals(DEFAULT_URL);
	}
	
	public HttpUrl getLastClone(HttpUrl url, List<HttpUrl> urls) {
		int count = 0;
		HttpUrl hu = null;
		for (HttpUrl u : urls) {
			if (u.equals(url)) {
				count++;
				hu = u;
			}
		}
		return count > 1 ? hu : null;
	}
	
	public List<HttpUrl> getServerList() {
		List<HttpUrl> urls = new ArrayList<HttpUrl>();
		urls.add(DEFAULT_URL);
		urls.addAll(DB.query(HttpUrl.class));
		removeEqualUrls(urls);
		return urls;
	}
	
	private void removeEqualUrls(List<HttpUrl> urls) {
		for (HttpUrl url : urls) {
			HttpUrl u = getLastClone(url, urls);
			if (u != null) {
				delete(u);
				urls.remove(u);
				removeEqualUrls(urls);
				break;
			}
		}
	}
	
}