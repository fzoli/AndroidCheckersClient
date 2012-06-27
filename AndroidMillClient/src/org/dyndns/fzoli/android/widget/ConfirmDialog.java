package org.dyndns.fzoli.android.widget;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

public class ConfirmDialog extends AlertDialog.Builder {
	
	public static final DialogInterface.OnClickListener dummy = new DialogInterface.OnClickListener() {
		
		@Override
		public void onClick(DialogInterface dialog, int which) {
			;
		}
		
	};
	
	public ConfirmDialog(Context context, int icon, String title, String message, String yes, String no, DialogInterface.OnClickListener event) {
		this(context, icon, title, message, yes, no, event, null);
	}
	
	public ConfirmDialog(Context context, int icon, String title, String message, String yes, String no, DialogInterface.OnClickListener yesEvent, DialogInterface.OnClickListener noEvent) {
		super(context);
		setIcon(icon);
    	setTitle(title);
    	setMessage(message);
    	setPositiveButton(yes, yesEvent == null ? dummy : yesEvent);
    	setNegativeButton(no, noEvent == null ? dummy : noEvent);
	}
	
}