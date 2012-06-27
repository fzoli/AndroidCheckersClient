package org.dyndns.fzoli.mill.android.activity;

import org.dyndns.fzoli.mvc.client.android.activity.ConnectionActivity;

import android.content.Intent;

public interface MillModelActivity<EventObj, PropsObj> extends ConnectionActivity<Object, Object, EventObj, PropsObj> {
	
	void setVisible(boolean visible);
	
	CharSequence getText(int resId);
	
	void finish();
	
	void startActivity(Intent intent);
	
	boolean processModelChange(EventObj e);
	
	boolean processModelData(PropsObj e);
	
	void setProgressDialog(boolean visible);
	
	void showAlertDialog(int type);
	
	void closeAlertDialog(boolean callOnCancel);
	
}