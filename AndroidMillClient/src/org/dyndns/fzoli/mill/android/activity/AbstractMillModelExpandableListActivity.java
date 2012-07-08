package org.dyndns.fzoli.mill.android.activity;

import org.dyndns.fzoli.android.context.activity.NetworkInfoActivityUtil;
import org.dyndns.fzoli.android.widget.ProgressEditTextLayout;
import org.dyndns.fzoli.mill.android.MillConnectionService;
import org.dyndns.fzoli.mill.android.service.MillConnectionBinder;
import org.dyndns.fzoli.mill.client.model.AbstractMillModel;
import org.dyndns.fzoli.mvc.client.android.activity.AbstractConnectionExpandableListActivity;
import org.dyndns.fzoli.mvc.client.android.service.ConnectionService;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.widget.EditText;

public abstract class AbstractMillModelExpandableListActivity<EventObj, PropsObj> extends AbstractConnectionExpandableListActivity<Object, Object, EventObj, PropsObj> implements MillModelActivity<EventObj, PropsObj> {
	
	@Override
	protected NetworkInfoActivityUtil createContextUtil() {
		return new MillModelActivityUtil<EventObj, PropsObj>(this);
	}
	
	@Override
	protected MillModelActivityUtil<EventObj, PropsObj> getContextUtil() {
		return (MillModelActivityUtil<EventObj, PropsObj>) super.getContextUtil();
	}
	
	@Override
	public Class<? extends ConnectionService<Object, Object>> getConnectionServiceClass() {
		return MillConnectionService.class;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getContextUtil().onCreate(savedInstanceState);
	}
	
	@Override
	public void onPause() {
		getContextUtil().onPause();
		super.onPause();
	}
	
	@Override
	public void setProgressDialog(boolean visible) {
		getContextUtil().setProgressDialog(visible);
	}
	
	@Override
	public void showAlertDialog(int type) {
		getContextUtil().showAlertDialog(type);
	}
	
	@Override
	public void closeAlertDialog(boolean callOnCancel) {
		getContextUtil().closeAlertDialog(callOnCancel);
	}
	
	protected String getVar(String key) {
		String s = getConnectionBinder().getVars().get(key);
		return s == null ? "" : s;
	}
	
	protected void setVar(String key, String val) {
		getConnectionBinder().getVars().put(key, val);
	}
	
	protected void removeVar(String key) {
		getConnectionBinder().getVars().remove(key);
	}
	
	protected MillConnectionBinder getConnectionBinder() {
		return getContextUtil().getConnectionBinder();
	}
	
	protected <T> T getEnumValue(Class<T> clazz, int i) {
		return AbstractMillModel.getEnumValue(clazz, i);
	}
	
	protected void setEtlProgress(final ProgressEditTextLayout etl) {
		runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				MillModelActivityUtil.setEtlProgress(etl);
			}
			
		});
	}
	
	protected void setEtlDetails(final ProgressEditTextLayout etl, final CharSequence c) {
		runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				MillModelActivityUtil.setEtlDetails(etl, c);
			}
			
		});
	}
	
	protected void resetEtl(final ProgressEditTextLayout etl) {
		runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				MillModelActivityUtil.resetEtl(etl);
			}
			
		});
	}
	
	protected void addLengthFilter(EditText et, int length) {
		MillModelActivityUtil.addLengthFilter(et, length);
	}
	
	protected ProgressDialog getProgressDialog() {
		return getContextUtil().getProgressDialog();
	}
	
}