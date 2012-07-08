package org.dyndns.fzoli.mill.android.activity;

import java.util.Arrays;

import org.dyndns.fzoli.android.widget.ConfirmDialog;
import org.dyndns.fzoli.android.widget.ProgressEditTextLayout;
import org.dyndns.fzoli.mill.android.ConnectivityActivity;
import org.dyndns.fzoli.mill.android.HomeActivity;
import org.dyndns.fzoli.mill.android.MillConnectionService;
import org.dyndns.fzoli.mill.android.R;
import org.dyndns.fzoli.mill.android.SignInActivity;
import org.dyndns.fzoli.mill.android.service.MillConnectionBinder;
import org.dyndns.fzoli.mill.android.service.MillConnectionBinder.LoginMode;
import org.dyndns.fzoli.mvc.client.android.activity.ConnectionActivityUtil;
import org.dyndns.fzoli.mvc.client.event.ModelActionEvent;
import org.dyndns.fzoli.mvc.client.event.ModelChangeEvent;
import org.dyndns.fzoli.mvc.client.event.ModelEvent;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.text.InputFilter;
import android.widget.TextView;

public class MillModelActivityUtil<EventObj, PropsObj> extends ConnectionActivityUtil<Object, Object, EventObj, PropsObj> implements MillModelActivity<EventObj, PropsObj> {
	
	private AlertDialog alertDialog;
	private ProgressDialog progressDialog;
	
//	public static final String INTENT_FILTER_CLOSE = "MillOnlineActivityFinish";
//	
//	private final BroadcastReceiver CLOSE_RECEIVER = new BroadcastReceiver() {
//    
//		@Override
//        public void onReceive(Context context, Intent intent) {
//			unregisterReceiver(CLOSE_RECEIVER);
//			finish();                                   
//        }
//		
//	};
	
	public MillModelActivityUtil(MillModelActivity<EventObj, PropsObj> activity) {
		super(activity);
	}
	
	@Override
	protected MillModelActivity<EventObj, PropsObj> getContext() {
		return (MillModelActivity<EventObj, PropsObj>) super.getContext();
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		registerReceiver(CLOSE_RECEIVER, new IntentFilter(INTENT_FILTER_CLOSE));
		setVisible(false);
	}
	
	@Override
	public void onPause() {
		setProgressDialog(false);
		closeAlertDialog(false);
		super.onPause();
	}
	
	@Override
	public boolean onConnectionBinded() {
		boolean b = super.onConnectionBinded();
		if (!b) return false;
		b = isNetworkAvailable();
		processNetworkChange(b);
		return b;
	}
	
	@Override
	public void onNetworkChanged(boolean replaced) {
		processNetworkChange(isNetworkAvailable());
	}
	
	@Override
	public void onModelPrepare() {
		setProgressDialog(true);
	}
	
	@Override
	public boolean onModelCreated(ModelActionEvent<PropsObj> e) {
		boolean b = getContext().onModelCreated(e);
		if (b) {
			processModelEvent(e);
			setProgressDialog(false);
			if (e.getType() == TYPE_EVENT) setVisible(true);
		}
		return b;
	}
	
	@Override
	public void onModelChanged(ModelChangeEvent<EventObj> e) {
		processModelEvent(e);
	}
	
	@Override
	public boolean processModelChange(EventObj e) {
		return getContext().processModelChange(e);
	}
	
	@Override
	public boolean processModelData(PropsObj e) {
		return getContext().processModelData(e);
	}
	
	@SuppressWarnings("unchecked")
	private void processModelEvent(ModelEvent e) {
		switch (e.getType()) {
			case TYPE_EVENT:
				if (e instanceof ModelChangeEvent) processModelChange(((ModelChangeEvent<EventObj>)e).getEvent());
				if (e instanceof ModelActionEvent) processModelData(((ModelActionEvent<PropsObj>)e).getEvent());
				break;
			case TYPE_SERVER_RECONNECT:
				processModelData(getModel().getCache(false, false));
				setProgressDialog(false);
				break;
			case TYPE_SERVER_LOST:
				setProgressDialog(true);
				break;
			case TYPE_CONTROLLER_CLOSE_EXCEPTION:
			case TYPE_CONNECTION_EXCEPTION:
				showAlertDialog(e.getType());
				setProgressDialog(false);
		}
	}
	
	@Override
	public void setVisible(boolean visible) {
		try {
			getContext().setVisible(visible);
		}
		catch (NullPointerException ex) {
			;
		}
	}
	
	@Override
	public void finish() {
		getContext().finish();
	}
	
	@Override
	public CharSequence getText(int resId) {
		return getContext().getText(resId);
	}
	
	@Override
	public void startActivity(Intent intent) {
		getContext().startActivity(intent);
	}
	
	protected void processNetworkChange(boolean networkAvailable) {
    	if (!networkAvailable) startActivity(new Intent((Context) getContext(), ConnectivityActivity.class));
    }
	
	@Override
	public void setProgressDialog(boolean visible) {
		try {
			if (visible && progressDialog == null) {
				closeAlertDialog(false);
				progressDialog = ProgressDialog.show((Context) getContext(), getText(R.string.wait), getText(R.string.connecting), true, false);
			}
			else if (!visible && progressDialog != null) {
				progressDialog.dismiss();
				progressDialog = null;
			}
		}
		catch (Exception ex) {
			;
		}
	}
	
	@Override
	public void showAlertDialog(int type) {
		if (alertDialog != null) return;
		try {
			AlertDialog.Builder builder = new AlertDialog.Builder((Context) getContext());
			builder.setIcon(android.R.drawable.ic_dialog_alert);
			if (type == TYPE_CONNECTION_EXCEPTION) {
				builder.setTitle(getText(R.string.connection_error))
				.setPositiveButton(getText(R.string.retry), new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int id) {
						closeAlertDialog(false);
						rebindConnectionService();
					}
					
				})
				.setNegativeButton(getText(R.string.exit), new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int id) {
						closeAlertDialog(true);
					}
					
				});
			}
			else {
				builder.setTitle(getText(R.string.controller_error))
				.setNeutralButton(getText(R.string.ok), new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int id) {
						closeAlertDialog(false);
					}
					
				});
			}
			alertDialog = builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
				
				@Override
				public void onCancel(DialogInterface dialog) {
					getConnectionBinder().setLoginMode(LoginMode.INVISIBLE_ERROR);
					openHome();
				}
				
			}).setCancelable(true).show();
		}
		catch (Exception ex) {
			;
		}
	}
	
	@Override
	public void closeAlertDialog(boolean callOnCancel) {
		if (alertDialog == null) return;
		if (callOnCancel) alertDialog.cancel();
		else alertDialog.dismiss();
		alertDialog = null;
	}
	
	public void openSignIn() {
		openSignIn((Context)getContext());
	}
	
	public void openHome() {
		openHome((Context)getContext());
	}
	
	@Override
	public MillConnectionBinder getConnectionBinder() {
		return (MillConnectionBinder) super.getConnectionBinder();
	}
	
	public ProgressDialog getProgressDialog() {
		return progressDialog;
	}
	
//	public static void closeMillModelActivities(Context context) {
//		context.sendBroadcast(new Intent(MillModelActivityUtil.INTENT_FILTER_CLOSE));
//	}
	
	public static void openSignIn(Context context) {
		openActivity(context, SignInActivity.class);
	}
	
	public static void openHome(Context context) {
		openActivity(context, HomeActivity.class);
	}
	
	public static void openActivity(Context context, Class<? extends Activity> c) {
		Intent intent = new Intent(context, c);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		if (context instanceof Service) intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(intent);
	}
	
	public static void bindMillConnectionService(ContextWrapper context, ServiceConnection conn) {
		Intent intent = new Intent(context, MillConnectionService.class);
		context.startService(intent);
		context.bindService(intent, conn, ContextWrapper.BIND_AUTO_CREATE);
	}
	
	public static InputFilter[] createLengthFilter(InputFilter[] filter, int length) {
		InputFilter[] f = Arrays.copyOf(filter, filter.length + 1);
		f[f.length - 1] = new InputFilter.LengthFilter(length);
		return f;
	}
	
	public static void addLengthFilter(TextView tv, int length) {
		tv.setFilters(createLengthFilter(tv.getFilters(), length));
	}
	
	public static void showAlertDialog(Context context, int title, Integer msg, DialogInterface.OnClickListener l) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		if (msg != null) builder.setMessage(msg);
		builder.setIcon(android.R.drawable.ic_dialog_alert)
		.setTitle(title)
		.setNeutralButton(R.string.ok, l == null ? ConfirmDialog.dummy : l)
		.setCancelable(false)
		.show();
	}
	
	public static void resetEtl(ProgressEditTextLayout etl) {
		etl.setProgress(false);
		etl.setWarning(false);
		etl.setDetails(false);
	}
	
	public static void setEtlProgress(ProgressEditTextLayout etl) {
		setEtl(etl, false, "");
	}
	
	public static void setEtlDetails(ProgressEditTextLayout etl, CharSequence c) {
		setEtl(etl, true, c);
	}
	
	private static void setEtl(ProgressEditTextLayout etl, boolean v, CharSequence c) {
		etl.setProgress(!v);
		etl.setWarning(v);
		etl.setDetails(v);
		etl.getDetails().setText(c);
	}
	
}