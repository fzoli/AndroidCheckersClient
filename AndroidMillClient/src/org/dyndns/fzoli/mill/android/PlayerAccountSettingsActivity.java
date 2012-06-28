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
import android.preference.PreferenceCategory;
import android.preference.PreferenceScreen;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.TextView;

public class PlayerAccountSettingsActivity extends AbstractMillOnlineBundlePreferenceActivity<PlayerEvent, PlayerData> {
	
	private TextView tvWarning;
	private EditTextPreference emailPref;
	
	private interface PasswordDialogEvent {
		void onClick(EditText input);
	}
	
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
			if (e.isCaptchaValidated()) {
				if (!e.getPlayer().isValidated()) {
					tvWarning.setText(R.string.validate_warning);
					tvWarning.setVisibility(View.VISIBLE);
				}
				if (getEmail().isEmpty()) {
					tvWarning.setText(R.string.empty_email_warning);
					tvWarning.setVisibility(View.VISIBLE);
				}
				initScreen(e);
			}
			else {
				startActivity(new Intent(this, PlayerSettingsCaptchaActivity.class));
			}
			return true;
		}
		return false;
	}
	
	@Override
	public void onBackPressed() {
		if (isModified()) {
			showClosePasswordDialog();
		}
		else {
			super.onBackPressed();
		}
	}
	
	private boolean isModified() {
		return emailPref == null ? false : !emailPref.getText().equalsIgnoreCase(getEmail());
	}
	
	private void initScreen(PlayerData e) {
		final PreferenceScreen root = getPreferenceManager().createPreferenceScreen(this);
		setPreferenceScreen(root);
		final PreferenceCategory userDatas = new PreferenceCategory(this);
		userDatas.setTitle(R.string.user_datas);
		root.addPreference(userDatas);
		final Preference userPref = new Preference(this);
		userPref.setEnabled(false);
		userPref.setTitle(R.string.username);
		userPref.setSummary(e.getPlayerName());
		userDatas.addPreference(userPref);
		emailPref = new EditTextPreference(this);
		emailPref.getEditText().setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
		emailPref.setTitle(R.string.email);
		emailPref.setSummary(getEmail());
		emailPref.setText(getEmail());
		emailPref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
			
			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				CharSequence val = (CharSequence)newValue;
				emailPref.setSummary(val); //TODO: InputValidator
				return true;
			}
			
		});
		userDatas.addPreference(emailPref);
		final PreferenceCategory userActions = new PreferenceCategory(this);
		userActions.setTitle(R.string.user_actions);
		root.addPreference(userActions);
		final Preference validatePref = new Preference(this);
		validatePref.setTitle(R.string.validate_email);
		validatePref.setSummary(R.string.validate_email_sum);
		validatePref.setEnabled(!getEmail().isEmpty());
		userActions.addPreference(validatePref);
		final Preference suspendPref = new Preference(this);
		suspendPref.setTitle(R.string.account_suspend);
		userActions.addPreference(suspendPref);
	}
	
	private void showClosePasswordDialog() {
		showPasswordDialog(new PasswordDialogEvent() {
			
			@Override
			public void onClick(EditText input) {
				/*String password = input.getText().toString();*/
				PlayerAccountSettingsActivity.super.onBackPressed();
			}
			
		}, new PasswordDialogEvent() {
			
			@Override
			public void onClick(EditText input) {
				PlayerAccountSettingsActivity.super.onBackPressed();
			}
			
		});
	}
	
	private void showPasswordDialog(final PasswordDialogEvent ok, final PasswordDialogEvent cancel) { //TODO
		AlertDialog.Builder alert = new AlertDialog.Builder(this);
		alert.setTitle(R.string.password);
		alert.setMessage(R.string.password_input); 
		final EditText input = new EditText(this);
		input.setSingleLine();
		input.setTransformationMethod(PasswordTransformationMethod.getInstance());
		alert.setView(input);
		
		alert.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
			
			public void onClick(DialogInterface dialog, int whichButton) {
				if (ok != null) ok.onClick(input);
			}
			
		});
		
		alert.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
			
			public void onClick(DialogInterface dialog, int whichButton) {
				if (cancel != null) cancel.onClick(input);
			}
			
		});

		alert.show();
	}
	
	private String getEmail() {
		try {
			return getModel().getCache().getPlayer().getEmail();
		}
		catch (Exception e) {
			return "";
		}
	}
	
}