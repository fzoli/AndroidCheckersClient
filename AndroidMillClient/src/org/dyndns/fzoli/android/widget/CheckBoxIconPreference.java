package org.dyndns.fzoli.android.widget;

import android.app.Activity;
import android.preference.CheckBoxPreference;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class CheckBoxIconPreference extends CheckBoxPreference {
	
	private final ImageView iconView;
	private final int margin;
	
	private LinearLayout layout;
	private View lastView;
	
	public CheckBoxIconPreference(Activity context, int resourceIcon) {
		super(context);
		DisplayMetrics metrics = new DisplayMetrics();
		context.getWindowManager().getDefaultDisplay().getMetrics(metrics);
		this.margin = (int)(metrics.density * 12);
		this.iconView = new ImageView(getContext());
		this.iconView.setImageResource(resourceIcon);
	}
	
	public View getPreferenceView() {
		return lastView;
	}
	
	public ImageView getIconView() {
		return iconView;
	}
	
	@Override
	public View getView(View convertView, ViewGroup parent) {
		lastView = super.getView(convertView, parent);
		if (layout != null) layout.removeAllViews();
		layout = new LinearLayout(getContext());
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		params.leftMargin = margin;
		params.gravity = Gravity.CENTER;
		layout.addView(iconView, params);
		layout.addView(lastView);
		return layout;
	}
	
}