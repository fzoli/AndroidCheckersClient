package org.dyndns.fzoli.mill.android;

import org.dyndns.fzoli.android.widget.ConfirmDialog;
import org.dyndns.fzoli.mill.android.activity.AbstractMillOnlineBundlePreferenceActivity;
import org.dyndns.fzoli.mill.android.activity.IntegerMillModelActivityAdapter;
import org.dyndns.fzoli.mill.android.activity.MillModelActivityUtil;
import org.dyndns.fzoli.mill.android.entity.UserInfo;
import org.dyndns.fzoli.mill.android.service.MillDatabaseHelper;
import org.dyndns.fzoli.mill.client.model.PlayerModel;
import org.dyndns.fzoli.mill.common.InputValidator;
import org.dyndns.fzoli.mill.common.key.PlayerReturn;
import org.dyndns.fzoli.mill.common.model.entity.Player;
import org.dyndns.fzoli.mill.common.model.pojo.PlayerData;
import org.dyndns.fzoli.mill.common.model.pojo.PlayerEvent;
import org.dyndns.fzoli.mvc.client.android.activity.ConnectionActivity;
import org.dyndns.fzoli.mvc.client.connection.Connection;
import org.dyndns.fzoli.mvc.client.event.ModelActionEvent;
import org.dyndns.fzoli.mvc.client.event.ModelActionListener;
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
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class PlayerAccountSettingsActivity extends AbstractMillOnlineBundlePreferenceActivity<PlayerEvent, PlayerData> {
	
	private TextView tvWarning;
	private EditTextPreference emailPref, passwd1Pref, passwd2Pref;
	
	private interface PasswordDialogEvent {
		void onClick(String password);
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
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
	public PlayerModel getModel() {
		return (PlayerModel) super.getModel();
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
		try {
			return !emailPref.getText().equalsIgnoreCase(getEmail()) || (!passwd1Pref.getText().isEmpty() && isPasswordsOk());
		}
		catch (NullPointerException ex) {
			return false;
		}
	}
	
	private boolean isPasswordsOk() {
		if (passwd1Pref != null && passwd2Pref != null) {
			String p = passwd1Pref.getText();
			return p.equals(passwd2Pref.getText()) && InputValidator.isPasswordValid(p);
		}
		return false;
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
		PreferenceScreen passwordScreen = getPreferenceManager().createPreferenceScreen(this);
		passwordScreen.setTitle(R.string.password);
		userDatas.addPreference(passwordScreen);
		passwd1Pref = new EditTextPreference(this);
		setPasswordType(passwd1Pref.getEditText(), InputValidator.MAX_PASSWORD_LENGTH);
		passwd1Pref.setTitle(R.string.password);
		passwd1Pref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
			
			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				if (InputValidator.isPasswordValid(newValue.toString())) {
					passwd2Pref.setEnabled(true);
					return true;
				}
				else {
					showToast(R.string.password_format);
					return false;
				}
			}
			
		});
		passwordScreen.addPreference(passwd1Pref);
		passwd2Pref = new EditTextPreference(this);
		setPasswordType(passwd2Pref.getEditText(), InputValidator.MAX_PASSWORD_LENGTH);
		passwd2Pref.setTitle(R.string.password_again);
		passwd2Pref.setEnabled(false);
		passwd2Pref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
			
			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				if (!passwd1Pref.getText().equals(newValue.toString())) {
					showToast(R.string.password_not_match);
					return false;
				}
				return true;
			}
			
		});
		passwordScreen.addPreference(passwd2Pref);
		final Preference validatePref = new Preference(this);
		emailPref = new EditTextPreference(this);
		emailPref.getEditText().setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
		emailPref.setTitle(R.string.email);
		emailPref.setSummary(getEmail());
		emailPref.setText(getEmail());
		emailPref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
			
			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				String email = newValue.toString();
				if (InputValidator.isEmailValid(email)) {
					if (!email.isEmpty()) {
						if (getModel().isEmailFree(email)) {
							emailPref.setSummary(email);
							tvWarning.setVisibility(View.GONE);
							validatePref.setEnabled(false);
							return true;
						}
						else {
							showToast(R.string.email_not_free);
							return false;
						}
					}
					else {
						if (getPlayer().isValidated()) {
							showToast(R.string.email_not_changed);
							return false;
						}
						else {
							emailPref.setSummary(email);
							tvWarning.setVisibility(View.VISIBLE);
							return true;
						}
					}
				}
				else {
					showToast(R.string.email_format);
					return false;
				}
			}
			
		});
		userDatas.addPreference(emailPref);
		final PreferenceCategory userActions = new PreferenceCategory(this);
		userActions.setTitle(R.string.user_actions);
		root.addPreference(userActions);
		validatePref.setTitle(R.string.validate_email);
		validatePref.setSummary(R.string.validate_email_sum);
		validatePref.setEnabled(!getEmail().isEmpty() && !getPlayer().isValidated());
		validatePref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
			
			@Override
			public boolean onPreferenceClick(Preference preference) {
				showPasswordDialog(new PasswordDialogEvent() {
					
					@Override
					public void onClick(String password) {
						setIndicator(true);
						getModel().revalidateEmail(InputValidator.md5Hex(password), true, new ModelActionListener<Integer>() {
							
							@Override
							public void modelActionPerformed(ModelActionEvent<Integer> e) { //TODO
								setIndicator(false);
							}
							
						});
					}
					
				}, null);
				return true;
			}
			
		});
		userActions.addPreference(validatePref);
		final Preference suspendPref = new Preference(this);
		suspendPref.setTitle(R.string.account_suspend);
		suspendPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
			
			@Override
			public boolean onPreferenceClick(Preference preference) {
				new ConfirmDialog(PlayerAccountSettingsActivity.this, android.R.drawable.ic_dialog_alert, getString(R.string.account_suspend), getString(R.string.account_suspend_warning), getString(R.string.yes), getString(R.string.no), new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						showPasswordDialog(new PasswordDialogEvent() {
							
							@Override
							public void onClick(String password) {
								setIndicator(true);
								getModel().suspendAccount(InputValidator.md5Hex(password), true, new ModelActionListener<Integer>() {
									
									@Override
									public void modelActionPerformed(ModelActionEvent<Integer> e) { //TODO
										setIndicator(false);
									}
									
								});
							}
							
						}, null);
					}
					
				}).show();
				return true;
			}
			
		});
		userActions.addPreference(suspendPref);
	}
	
	private void showToast(int res) {
		Toast.makeText(PlayerAccountSettingsActivity.this, res, Toast.LENGTH_SHORT).show();
	}
	
	private void setPasswordType(EditText et) {
		setPasswordType(et, 0);
	}
	
	private void setPasswordType(EditText et, int max) {
		et.setSingleLine();
		if (max > 0) MillModelActivityUtil.addLengthFilter(et, max);
		et.setTransformationMethod(PasswordTransformationMethod.getInstance());
	}
	
	private void sendPassword(String oldPassword, String newPassword, final boolean finish) {
		final int newLength = newPassword.length();
		final String newPasswordHash = InputValidator.md5Hex(newPassword);
		setIndicator(true);
		getModel().setPassword(InputValidator.md5Hex(oldPassword), newPasswordHash, true, new ModelActionListener<Integer>() {
			
			@Override
			public void modelActionPerformed(ModelActionEvent<Integer> e) {
				new IntegerMillModelActivityAdapter(PlayerAccountSettingsActivity.this, e) {
					
					@Override
					public void onEvent(int e) {
						setIndicator(false);
						switch(getReturn(e)) {
							case OK:
								MillDatabaseHelper db = getConnectionBinder().getDatabaseHelper();
								UserInfo ui = db.findUserInfo(getPlayer().getPlayerName(), true);
								if (ui != null && db.getUserInfoSettings().isSavePassword()) {
									ui.setPassword(newPasswordHash, newLength);
									db.store(ui);
								}
								if (finish) finish();
								break;
							case NO_CHANGE:
								showToast(R.string.password_not_changed);
								if (finish) finish();
								else setIndicator(false);
								break;
							default:
								showToast(R.string.wrong_password);
						}
					}
					
				};
			}
			
		});
	}
	
	private void showClosePasswordDialog() {
		showPasswordDialog(new PasswordDialogEvent() {
			
			@Override
			public void onClick(String password) { //TODO
				if (isPasswordsOk()) sendPassword(password, passwd1Pref.getText(), true);
				else PlayerAccountSettingsActivity.super.onBackPressed();
			}
			
		}, new Runnable() {
			
			@Override
			public void run() {
				PlayerAccountSettingsActivity.super.onBackPressed();
			}
			
		});
	}
	
	private void showPasswordDialog(final PasswordDialogEvent ok, final Runnable cancel) {
		AlertDialog.Builder alert = new AlertDialog.Builder(this);
		alert.setTitle(R.string.password);
		alert.setMessage(R.string.password_input); 
		final EditText input = new EditText(this);
		setPasswordType(input);
		alert.setView(input);
		
		alert.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
			
			public void onClick(DialogInterface dialog, int whichButton) {
				String password = input.getText().toString();
				if (InputValidator.isPasswordValid(password)) {
					if (ok != null) ok.onClick(password);
				}
				else {
					showToast(R.string.wrong_password);
				}
			}
			
		});
		
		alert.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
			
			public void onClick(DialogInterface dialog, int whichButton) {
				if (cancel != null) cancel.run();
			}
			
		});

		alert.setOnCancelListener(new DialogInterface.OnCancelListener() {
			
			@Override
			public void onCancel(DialogInterface arg0) {
				if (cancel != null) cancel.run();
			}
			
		});
		
		alert.show();
	}
	
	private PlayerReturn getReturn(int i) {
		return getEnumValue(PlayerReturn.class, i);
	}
	
	private Player getPlayer() {
		try {
			return getModel().getCache().getPlayer();
		}
		catch (Exception e) {
			return null;
		}
	}
	
	private String getEmail() {
		try {
			return getPlayer().getEmail();
		}
		catch (Exception e) {
			return "";
		}
	}
	
	private void setIndicator(final boolean visible) {
		runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				setProgressBarIndeterminateVisibility(visible);
			}
			
		});
	}
	
}