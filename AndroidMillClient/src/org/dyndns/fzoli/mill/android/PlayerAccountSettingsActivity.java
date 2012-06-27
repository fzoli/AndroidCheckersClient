package org.dyndns.fzoli.mill.android;

import org.dyndns.fzoli.mill.android.activity.AbstractMillOnlineBundlePreferenceActivity;
import org.dyndns.fzoli.mill.client.model.PlayerModel;
import org.dyndns.fzoli.mill.common.model.pojo.PlayerData;
import org.dyndns.fzoli.mill.common.model.pojo.PlayerEvent;
import org.dyndns.fzoli.mvc.client.android.activity.ConnectionActivity;
import org.dyndns.fzoli.mvc.client.connection.Connection;
import org.dyndns.fzoli.mvc.client.model.CachedModel;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceScreen;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class PlayerAccountSettingsActivity extends AbstractMillOnlineBundlePreferenceActivity<PlayerEvent, PlayerData> {

	private TextView tvWarning;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		setTitle(R.string.common);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.account_settings);
		tvWarning = (TextView) findViewById(R.id.tvWarning);
	}

	@Override
	@SuppressWarnings("unchecked")
	public Class<? extends ConnectionActivity> getClassKey() {
		return HomeActivity.class;
	}
	
	@Override
	public CachedModel<Object, Object, PlayerEvent, PlayerData> createModel(Connection<Object, Object> connection) {
		return new PlayerModel(connection);
	}
	
	@Override
	public boolean processModelData(PlayerData e) {
		if (super.processModelData(e)) {
			if (e.isCaptchaValidated()) initScreen(e);
			else startActivity(new Intent(this, PlayerSettingsCaptchaActivity.class));
			if (!e.getPlayer().isValidated()) tvWarning.setVisibility(View.VISIBLE);
			return true;
		}
		return false;
	}
	
	@Override
	public void onBackPressed() {
		if (isModified()) {
			showPasswordDialog();
		}
		else {
			super.onBackPressed();
		}
	}
	
	private void initScreen(PlayerData e) {
		final PreferenceScreen root = getPreferenceManager().createPreferenceScreen(this);
		setPreferenceScreen(root);
		Preference userPref = new Preference(this);
		userPref.setEnabled(false);
		userPref.setTitle(R.string.username);
		userPref.setSummary(e.getPlayerName());
		root.addPreference(userPref);
		EditTextPreference emailPref = new EditTextPreference(this);
		emailPref.getEditText().setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
		emailPref.setTitle(R.string.email);
		emailPref.setSummary(e.getPlayer().getEmail());
		root.addPreference(emailPref);
	}
	
	private boolean isModified() { //TODO
		return false;
	}
	
	private void showPasswordDialog() { //TODO
		AlertDialog.Builder alert = new AlertDialog.Builder(this);
		alert.setTitle(R.string.password);
		alert.setMessage(R.string.password_input); 
		final EditText input = new EditText(this);
		input.setSingleLine();
		input.setTransformationMethod(PasswordTransformationMethod.getInstance());
		alert.setView(input);
		
		alert.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
			
			public void onClick(DialogInterface dialog, int whichButton) {
				//OK
				String password = input.getText().toString();
				PlayerAccountSettingsActivity.super.onBackPressed();
			}
			
		});
		
		alert.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
			
			public void onClick(DialogInterface dialog, int whichButton) {
				//Cancel
				PlayerAccountSettingsActivity.super.onBackPressed();
			}
			
		});

		alert.show();
	}
	
}