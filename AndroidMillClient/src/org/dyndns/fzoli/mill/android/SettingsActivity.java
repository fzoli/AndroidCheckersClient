package org.dyndns.fzoli.mill.android;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.dyndns.fzoli.android.widget.ConfirmDialog;
import org.dyndns.fzoli.http.HttpUrl;
import org.dyndns.fzoli.mill.android.activity.MillModelActivityUtil;
import org.dyndns.fzoli.mill.android.entity.ConnectionSettings;
import org.dyndns.fzoli.mill.android.entity.UserInfo;
import org.dyndns.fzoli.mill.android.entity.UserInfoSettings;
import org.dyndns.fzoli.mill.android.service.MillConnectionBinder;
import org.dyndns.fzoli.mill.android.service.MillDatabaseHelper;
import org.dyndns.fzoli.mill.android.service.MillServiceConnection;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceScreen;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.text.Editable;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class SettingsActivity extends PreferenceActivity {
	
	private Menu menu;
	private MillConnectionBinder mcb;
	private PreferenceScreen tmp;
	private PreferenceCategory serversCat, connSettingsCat, usrSettingsCat;
	private MillDatabaseHelper db;
	private List<HttpUrl> urls;
	
	private final Map<PreferenceScreen, HttpUrl> URL_SCREENS = new HashMap<PreferenceScreen, HttpUrl>();
	private final Pattern DOMAIN_PATTERN = Pattern.compile("^[a-z0-9]+[a-z0-9\\.\\-\\_]*\\.[a-z]{2,10}$");
	private final Pattern IP_PATTERN = Pattern.compile("^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
														"([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
														"([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
														"([01]?\\d\\d?|2[0-4]\\d|25[0-5])$");
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		MillModelActivityUtil.bindMillConnectionService(this, new MillServiceConnection() {

			@Override
			public void onServiceConnected(ComponentName cn, MillConnectionBinder b) {
				mcb = b;
				db = mcb.getDatabaseHelper();
				urls = db.getServerList();
				setPreferenceScreen(createPreferenceHierarchy());
				final CharSequence[] options = {getString(R.string.edit), getString(R.string.delete)};
				getListView().setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

					@Override
					public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, final long id) {
						Object obj = ((ListView) parent).getAdapter().getItem(position);
						if (obj instanceof PreferenceScreen) {
							final PreferenceScreen urlScreen = (PreferenceScreen) obj;
							if (!urlScreen.isEnabled()) return false;
							AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivity.this);
					    	builder.setTitle(urlScreen.getTitle());
					    	builder.setItems(options, new DialogInterface.OnClickListener() {
								
								@Override
								public void onClick(DialogInterface dialog, int which) {
									switch (which) {
										case 0:
											openPreferenceScreen(urlScreen);
											break;
										case 1:
											new ConfirmDialog(SettingsActivity.this, android.R.drawable.ic_dialog_alert, getString(R.string.delete), getString(R.string.delete_confirm), getString(R.string.yes), getString(R.string.no), new DialogInterface.OnClickListener() {
												
												@Override
												public void onClick(DialogInterface dialog, int which) {
													removeUrlScreen(urlScreen);
												}
												
											}).show();
											break;
									}
								}
								
							});
							builder.show();
							return true;
						}
						return false;
					}
					
				});
			}
			
		});
	}
	
	@Override
	public void onBackPressed() {
		if (tmp == null) super.onBackPressed();
		else closePreferenceScreen();
	}
	
	@Override
	public boolean onSearchRequested() {
		if (tmp == null) return super.onSearchRequested();
		else closePreferenceScreen();
		return false;
	}
	
	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch (item.getItemId()) {
			case R.id.entryNewServer:
				createNewServer();
				break;
		}
		return super.onMenuItemSelected(featureId, item);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		this.menu = menu;
		getMenuInflater().inflate(R.menu.settings, menu);
		return true;
	}
	
	private void openPreferenceScreen(PreferenceScreen screen) {
		if (screen != null) {
			if (menu != null) menu.getItem(0).setVisible(false);
			tmp = getPreferenceScreen();
			setTitle(screen.getTitle());
			setPreferenceScreen(screen);
		}
	}
	
	private void closePreferenceScreen() {
		if (tmp != null) {
			if (menu != null) menu.getItem(0).setVisible(true);
			setTitle(R.string.app_name);
			PreferenceScreen oldScreen = getPreferenceScreen();
			setPreferenceScreen(tmp);
			refresh(tmp, oldScreen, URL_SCREENS.get(oldScreen));
			tmp = null;
		}
	}
	
	private void createNewServer() {
		final HttpUrl url = new HttpUrl("");
		urls.add(url);
		final PreferenceScreen urlScreen = createUrlScreen(getPreferenceScreen(), url, false);
		openPreferenceScreen(urlScreen);
		addUrlScreen(urlScreen, url);
		setTitle(R.string.new_server);
	}
	
	private void showToast() {
		Toast.makeText(SettingsActivity.this, R.string.wrong_value, Toast.LENGTH_SHORT).show();
	}
	
	private void setUrlScreen(PreferenceScreen urlScreen, HttpUrl url) {
		String s = url.toString();
		urlScreen.setTitle(url.getHost());
		urlScreen.setSummary(s);
		urlScreen.setKey(s);
	}
	
	private void refresh(PreferenceScreen root, PreferenceScreen urlScreen, HttpUrl url) {
		HttpUrl u = db.getLastClone(url, urls);
		if (u == null && isPortValid(Integer.toString(url.getPort())) && isHostValid(url.getHost())) {
			db.store(url);
			setUrlScreen(urlScreen, url);
		}
		else {
			removeUrlScreen(urlScreen);
		}
		setConnectionSettings(connSettingsCat);
		((BaseAdapter)root.getRootAdapter()).notifyDataSetChanged();
	}
	
	private void addUrlScreen(PreferenceScreen urlScreen, HttpUrl url) {
		URL_SCREENS.put(urlScreen, url);
		serversCat.addPreference(urlScreen);
	}
	
	private void removeUrlScreen(PreferenceScreen urlScreen) {
		HttpUrl url = URL_SCREENS.get(urlScreen);
		if (url != null) {
			serversCat.removePreference(urlScreen);
			urls.remove(url);
			db.delete(url);
			URL_SCREENS.remove(urlScreen);
			setConnectionSettings(connSettingsCat);
		}
	}
	
	private boolean isHostValid(String text) {
		Matcher dm = DOMAIN_PATTERN.matcher(text);
		Matcher im = IP_PATTERN.matcher(text);
		return dm.matches() || im.matches();
	}
	
	private boolean isPortValid(String text) {
		try {
			int i = Integer.parseInt(text);
			return !(i <= 0 || i > 65535);
		}
		catch (Exception ex) {
			return false;
		}
	}
	
	private PreferenceScreen createUrlScreen(final PreferenceScreen root, final HttpUrl url, boolean add) {
		final PreferenceScreen urlScreen = getPreferenceManager().createPreferenceScreen(this);
		if (add) {
			setUrlScreen(urlScreen, url);
			addUrlScreen(urlScreen, url);
		}
		if (db.equalsDefaultUrl(url)) {
			urlScreen.setEnabled(false);
			return urlScreen;
		}
		urlScreen.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			
			@Override
			public boolean onPreferenceClick(Preference preference) {
				urlScreen.getDialog().setOnCancelListener(new DialogInterface.OnCancelListener() {
					
					@Override
					public void onCancel(DialogInterface dialog) {
						refresh(root, urlScreen, url);
					}
					
				});
				return true;
			}
			
		});
		final EditTextPreference hostPref = new EditTextPreference(this);
		hostPref.setTitle(R.string.domain);
		hostPref.setSummary(R.string.domain_sum);
		hostPref.setText(url.getHost());
		hostPref.getEditText().setSingleLine();
		hostPref.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
			
			@Override
			public boolean onPreferenceChange(Preference paramPreference, Object paramObject) {
				Editable text = hostPref.getEditText().getText();
				boolean ok = isHostValid(text.toString());
				if (!ok) {
					showToast();
				}
				else {
					if (tmp == null) urlScreen.getDialog().setTitle(text);
					else setTitle(text);
					url.setHost(text.toString());
				}
				return ok;
			}
			
		});
		urlScreen.addPreference(hostPref);
		final EditTextPreference portPref = new EditTextPreference(this);
		portPref.getEditText().setInputType(InputType.TYPE_CLASS_NUMBER);
		portPref.setTitle(R.string.port);
		portPref.setSummary(R.string.port_sum);
		portPref.setText(Integer.toString(url.getPort()));
		portPref.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
			
			@Override
			public boolean onPreferenceChange(Preference paramPreference, Object paramObject) {
				String s = portPref.getEditText().getText().toString();
				boolean ok = isPortValid(s);
				if (!ok) {
					showToast();
				}
				else {
					url.setPort(Integer.parseInt(s));
				}
				return ok;
			}
			
		});
		urlScreen.addPreference(portPref);
		final CheckBoxPreference securePref = new CheckBoxPreference(this);
		securePref.setTitle(R.string.secure);
		securePref.setSummary(R.string.secure_sum);
		securePref.setChecked(url.isSsl());
		securePref.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
			
			@Override
			public boolean onPreferenceChange(Preference paramPreference, Object paramObject) {
				url.setSsl(!securePref.isChecked());
				urlScreen.setSummary(url.toString());
				return true;
			}
			
		});
		urlScreen.addPreference(securePref);
		return urlScreen;
	}
	
	private void setConnectionSettings(PreferenceCategory connSettingsCat) {
		final ConnectionSettings connSettings = db.getConnectionSettings();
		connSettingsCat.removeAll();
		final ListPreference urlPreference = new ListPreference(this);
		urlPreference.setTitle(R.string.server);
		urlPreference.setSummary(R.string.server_sum);
		CharSequence[] names = new CharSequence[urls.size()];
		for (int i = 0; i < urls.size(); i++) {
			names[i] = urls.get(i).getHost();
		}
		CharSequence[] addresses = new CharSequence[urls.size()];
		int selected = 0;
		for (int i = 0; i < urls.size(); i++) {
			HttpUrl url = urls.get(i);
			addresses[i] = url.toString();
			if (url.equals(connSettings.getUrl())) {
				selected = i;
			}
		}
		urlPreference.setEntries(names);
		urlPreference.setEntryValues(addresses);
		urlPreference.setDefaultValue(addresses[selected]);
		connSettingsCat.addPreference(urlPreference);
		urlPreference.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
			
			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				int i = urlPreference.findIndexOfValue(newValue.toString());
				if (i != -1) {
					connSettings.setUrl(urls.get(i));
					refreshConnectionSettings(connSettings);
					return true;
				}
				return false;
			}
			
		});
		final CheckBoxPreference acceptInvalidCertsPreference = new CheckBoxPreference(this);
		acceptInvalidCertsPreference.setTitle(R.string.selfsigned);
		acceptInvalidCertsPreference.setSummary(R.string.selfsigned_sum);
		acceptInvalidCertsPreference.setChecked(connSettings.isAcceptInvalidCert());
		connSettingsCat.addPreference(acceptInvalidCertsPreference);
		acceptInvalidCertsPreference.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
			
			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				connSettings.setAcceptInvalidCert(!acceptInvalidCertsPreference.isChecked());
				refreshConnectionSettings(connSettings);
				return true;
			}
		});
	}
	
	private void refreshConnectionSettings(ConnectionSettings connSettings) {
		db.setConnectionSettings(connSettings);
		mcb.recreateConnection();
	}
	
	private void initUsrSettingsCategory(PreferenceCategory usrSettingsCat) {
		final UserInfoSettings s = db.getUserInfoSettings();
		final CheckBoxPreference saveUsrPref = new CheckBoxPreference(this);
		final CheckBoxPreference savePasswdPref = new CheckBoxPreference(this);
		saveUsrPref.setTitle(R.string.save_usr);
		saveUsrPref.setChecked(s.isSaveUser());
		saveUsrPref.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
			
			@Override
			public boolean onPreferenceChange(Preference paramPreference, Object paramObject) {
				if (saveUsrPref.isChecked()) { // not checked
					final List<UserInfo> ls = db.getUserInfoList(false);
					if (ls.isEmpty()) {
						applyUserDisable(ls, s, savePasswdPref);
					}
					else {
						new ConfirmDialog(SettingsActivity.this, android.R.drawable.ic_dialog_alert, getString(R.string.delete_user), getString(R.string.delete_all_user_confirm), getString(R.string.yes), getString(R.string.no), new DialogInterface.OnClickListener() {
							
							@Override
							public void onClick(DialogInterface dialog, int which) {
								applyUserDisable(ls, s, savePasswdPref);
							}
							
						}, new DialogInterface.OnClickListener() {
	
							@Override
							public void onClick(DialogInterface dialog, int which) {
								saveUsrPref.setChecked(true);
							}
							
						}).show();
					}
				}
				else { // checked
					s.setSaveUser(true);
					db.setUserInfoSettings(s);
				}
				return true;
			}
			
		});
		usrSettingsCat.addPreference(saveUsrPref);
		savePasswdPref.setTitle(R.string.save_passwd);
		savePasswdPref.setSummary(R.string.save_passwd_sum);
		savePasswdPref.setChecked(s.isSavePassword());
		savePasswdPref.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
			
			@Override
			public boolean onPreferenceChange(Preference paramPreference, Object paramObject) {
				if (!savePasswdPref.isChecked()) { // checked
					s.setSaveUser(true);
					s.setSavePassword(true);
					saveUsrPref.setChecked(true);
					db.setUserInfoSettings(s);
				}
				else { // not checked
					final List<UserInfo> ls = db.getUserInfoList(false);
					if (ls.isEmpty()) {
						applyPasswordDisable(ls, s);
					}
					else {
						new ConfirmDialog(SettingsActivity.this, android.R.drawable.ic_dialog_alert, getString(R.string.delete_password), getString(R.string.delete_all_password_confirm), getString(R.string.yes), getString(R.string.no), new DialogInterface.OnClickListener() {
							
							@Override
							public void onClick(DialogInterface dialog, int which) {
								applyPasswordDisable(ls, s);
							}
							
						}, new DialogInterface.OnClickListener() {
	
							@Override
							public void onClick(DialogInterface dialog, int which) {
								savePasswdPref.setChecked(true);
							}
							
						}).show();
					}
				}
				return true;
			}
			
		});
		usrSettingsCat.addPreference(savePasswdPref);
	}
	
	private void applyUserDisable(List<UserInfo> ls, UserInfoSettings s, CheckBoxPreference savePasswdPref) {
		s.setLastName("");
		s.setSaveUser(false);
		s.setSavePassword(false);
		savePasswdPref.setChecked(false);
		db.setUserInfoSettings(s);
		for (UserInfo i : ls) {
			db.delete(i);
		}
	}
	
	private void applyPasswordDisable(List<UserInfo> ls,UserInfoSettings s) {
		s.setSavePassword(false);
		db.setUserInfoSettings(s);
		for (UserInfo i : ls) {
			i.setPassword("");
			db.store(i);
		}
	}
	
	private PreferenceScreen createPreferenceHierarchy() {
		final PreferenceScreen root = getPreferenceManager().createPreferenceScreen(this);
		usrSettingsCat = new PreferenceCategory(this);
		usrSettingsCat.setTitle(R.string.usr_settings);
		root.addPreference(usrSettingsCat);
		initUsrSettingsCategory(usrSettingsCat);
		connSettingsCat = new PreferenceCategory(this);
		connSettingsCat.setTitle(R.string.conn_settings);
		root.addPreference(connSettingsCat);
		setConnectionSettings(connSettingsCat);
		serversCat = new PreferenceCategory(this);
		serversCat.setTitle(R.string.server_list);
		root.addPreference(serversCat);
		for (final HttpUrl url : urls) {
			createUrlScreen(root, url, true);
		}
		return root;
	}

}