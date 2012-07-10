package org.dyndns.fzoli.android.widget;

import java.util.Arrays;
import java.util.List;

import android.content.Context;
import android.preference.EditTextPreference;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

public class AutoCompletePreference extends EditTextPreference {

	private final AutoCompleteTextView EDIT_TEXT;
	
	private List<String> items;
	
	public AutoCompletePreference(Context context) {
		this(context, null);
	}
	
	public AutoCompletePreference(Context context, AttributeSet attrs) {
	    super(context, attrs);
	    EDIT_TEXT = new AutoCompleteTextView(context, attrs);
	    EDIT_TEXT.setThreshold(0);
	}
	
	public List<String> getItems() {
		return items;
	}
	
	public void setItems(String[] items) {
		setItems(Arrays.asList(items));
	}
	
	public void setItems(List<String> items) {
		this.items = items;
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_dropdown_item_1line, items);
	    EDIT_TEXT.setAdapter(adapter);
	}
	
	@Override
	protected void onBindDialogView(View view) {
	    AutoCompleteTextView editText = EDIT_TEXT;
	    editText.setText(getText());

	    ViewParent oldParent = editText.getParent();
	    if (oldParent != view) {
	        if (oldParent != null) {
	            ((ViewGroup) oldParent).removeView(editText);
	        }
	        onAddEditTextToDialogView(view, editText);
	    }
	}

	@Override
	protected void onDialogClosed(boolean positiveResult) {
	    if (positiveResult) {
	        String value = EDIT_TEXT.getText().toString();
	        if (callChangeListener(value)) {
	            setText(value);
	        }
	    }
	}
	
}