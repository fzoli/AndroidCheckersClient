package org.dyndns.fzoli.android.widget;

import android.content.Context;
import android.text.method.KeyListener;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

public class EditableEditText extends EditText {
	
	private KeyListener editTextKeyListener;
	
	public EditableEditText(Context context) {
		super(context);
		init();
	}

	public EditableEditText(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public EditableEditText(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	private void init() {
		editTextKeyListener = getKeyListener();
	}
	
	@Override
	public void setText(CharSequence text, BufferType type) {
		super.setText(text, type);
		setSelection(text.length());
	}
	
	@Override
	public void setKeyListener(KeyListener input) {
		editTextKeyListener = input;
		super.setKeyListener(input);
	}
	
	public void setEditable(boolean enable) {
		super.setKeyListener(enable ? editTextKeyListener : null);
	}
	
	public static boolean isActionSend(int actionId, KeyEvent event) {
		return (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER) || actionId == EditorInfo.IME_ACTION_SEND;
	}
	
	public void setAction(final EditTextAction r) {
		if (r != null) {
			setOnFocusChangeListener(new OnFocusChangeListener() {
				
				@Override
				public void onFocusChange(View v, boolean hasFocus) {
					if (!hasFocus) r.actionPerformed(true);
				}
				
			});
			setOnEditorActionListener(new OnEditorActionListener() {
				
				@Override
				public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
					if (isActionSend(actionId, event)) return r.actionPerformed(false);
					return true;
				}
				
			});
		}
		else {
			setOnFocusChangeListener(null);
			setOnEditorActionListener(null);
		}
	}
	
}