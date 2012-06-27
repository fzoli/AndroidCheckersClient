package org.dyndns.fzoli.android.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class ProgressEditTextLayout extends LinearLayout {
	
	private EditableEditText editText;
	private TextView textView;
	private ProgressBar progressBar;
	private ImageView warningView;
	
	private boolean lastDetailsVisible = false;
	
	private static final LayoutParams deflp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
	
	public ProgressEditTextLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(attrs);
	}

	public ProgressEditTextLayout(Context context) {
		super(context);
		init(null);
	}

	private void init(AttributeSet attrs) {
		setOrientation(VERTICAL);
		
		RelativeLayout etl = new RelativeLayout(getContext());
		addView(etl, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		
		editText = new EditableEditText(getContext());
		editText.setSingleLine();
		editText.setImeOptions(EditorInfo.IME_ACTION_SEND);
		if (attrs != null) {
			editText.setInputType(attrs.getAttributeIntValue("http://schemas.android.com/apk/res/android", "inputType", editText.getInputType()));
		}
		etl.addView(editText, deflp);
		
		final float d = getContext().getResources().getDisplayMetrics().density;
		
		progressBar = new ProgressBar(getContext(), null, android.R.attr.progressBarStyleSmall);
		setProgress(false);
		etl.addView(progressBar, createParams(d));
		
		warningView = new ImageView(getContext());
		warningView.setImageResource(android.R.drawable.stat_sys_warning);
		setWarning(false);
		etl.addView(warningView, createParams(d));
		
		textView = new TextView(getContext());
	}
	
	public EditableEditText getEditText() {
		return editText;
	}
	
	public TextView getDetails() {
		return textView;
	}
	
	public void setProgress(boolean visible) {
		editText.setEditable(!visible);
		setVisible(progressBar, visible);
	}
	
	public void setWarning(boolean visible) {
		setVisible(warningView, visible);
	}
	
	public void setDetails(boolean visible) {
		if (visible && !lastDetailsVisible) addView(textView, deflp);
		if (!visible && lastDetailsVisible) removeView(textView);
		lastDetailsVisible = visible;
	}
	
	public boolean isDetailsVisible() {
		return lastDetailsVisible;
	}
	
	private void setVisible(View view, boolean visible) {
		view.setVisibility(visible ? View.VISIBLE : View.INVISIBLE);
	}
	
	private RelativeLayout.LayoutParams createParams(float d) {
		RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		lp.addRule(RelativeLayout.CENTER_VERTICAL);
		lp.setMargins(0, 0, (int)(10 * d), 0);
		return lp;
	}
	
}