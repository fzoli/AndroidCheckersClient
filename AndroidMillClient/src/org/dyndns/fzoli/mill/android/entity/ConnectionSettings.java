package org.dyndns.fzoli.mill.android.entity;

import org.dyndns.fzoli.http.HttpUrl;

public class ConnectionSettings {
	
	private HttpUrl url;
	private boolean acceptInvalidCert;
	
	public ConnectionSettings(HttpUrl url, boolean acceptInvalidCert) {
		this.url = url;
		this.acceptInvalidCert = acceptInvalidCert;
	}
	
	public HttpUrl getUrl() {
		return url;
	}
	
	public void setUrl(HttpUrl url) {
		this.url = url;
	}
	
	public boolean isAcceptInvalidCert() {
		return acceptInvalidCert;
	}
	
	public void setAcceptInvalidCert(boolean acceptInvalidCert) {
		this.acceptInvalidCert = acceptInvalidCert;
	}
	
}