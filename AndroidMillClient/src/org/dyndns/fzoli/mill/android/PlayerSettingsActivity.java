package org.dyndns.fzoli.mill.android;

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

import org.dyndns.fzoli.android.widget.AutoCompletePreference;
import org.dyndns.fzoli.mill.android.activity.AbstractMillOnlineBundlePreferenceActivity;
import org.dyndns.fzoli.mill.android.activity.IntegerMillModelActivityAdapter;
import org.dyndns.fzoli.mill.client.model.PlayerModel;
import org.dyndns.fzoli.mill.common.InputValidator;
import org.dyndns.fzoli.mill.common.key.PersonalDataType;
import org.dyndns.fzoli.mill.common.key.PlayerReturn;
import org.dyndns.fzoli.mill.common.model.entity.PersonalData;
import org.dyndns.fzoli.mill.common.model.entity.Sex;
import org.dyndns.fzoli.mill.common.model.pojo.PlayerData;
import org.dyndns.fzoli.mill.common.model.pojo.PlayerEvent;
import org.dyndns.fzoli.mvc.client.android.activity.ConnectionActivity;
import org.dyndns.fzoli.mvc.client.connection.Connection;
import org.dyndns.fzoli.mvc.client.event.ModelActionEvent;
import org.dyndns.fzoli.mvc.client.event.ModelActionListener;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceScreen;
import android.preference.Preference.OnPreferenceChangeListener;
import android.view.Window;
import android.widget.Toast;

public class PlayerSettingsActivity extends AbstractMillOnlineBundlePreferenceActivity<PlayerEvent, PlayerData> {
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		super.onCreate(savedInstanceState);
		setProgressBarIndeterminateVisibility(false);
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public Class<? extends ConnectionActivity> getClassKey() {
		return HomeActivity.class;
	}
	
	@Override
	public PlayerModel getModel() {
		return (PlayerModel) super.getModel();
	}
	
	@Override
	public PlayerModel createModel(Connection<Object, Object> connection) {
		return new PlayerModel(connection);
	}
	
	@Override
	public boolean processModelChange(PlayerEvent e) {
		if(super.processModelChange(e)) {
			//TODO: ha a belépett felhasználónak megváltozott a jog maszkja, a lista frissítése
			return true;
		}
		return false;
	}
	
	@Override
	public boolean processModelData(PlayerData e) {
		if(super.processModelData(e)) {
			if (e.isCaptchaValidated()) initScreen();
			else startActivity(new Intent(this, PlayerSettingsCaptchaActivity.class));
			return true;
		}
		return false;
	}
	
	private void showToast(int r) {
		Toast.makeText(this, r, Toast.LENGTH_LONG).show();
	}
	
	private void initScreen() { //TODO
		final PreferenceScreen root = getPreferenceManager().createPreferenceScreen(this);
		setPreferenceScreen(root);
		final PreferenceCategory optionsPref = new PreferenceCategory(this);
		optionsPref.setTitle(R.string.user_setting_options);
		root.addPreference(optionsPref);
		
		final Preference commonPref = new Preference(this);
		commonPref.setTitle(R.string.common);
		commonPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
			
			@Override
			public boolean onPreferenceClick(Preference paramPreference) {
				startActivity(new Intent(PlayerSettingsActivity.this, PlayerAccountSettingsActivity.class));
				return true;
			}
			
		});
		optionsPref.addPreference(commonPref);
		
		final Preference avatarPref = new Preference(this);
		avatarPref.setTitle(R.string.avatar);
		avatarPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
			
			@Override
			public boolean onPreferenceClick(Preference paramPreference) {
				startActivity(new Intent(PlayerSettingsActivity.this, PlayerAvatarActivity.class));
				return true;
			}
			
		});
		optionsPref.addPreference(avatarPref);
		
		final PersonalData personalData = getModel().getCache().getPlayer().getPersonalData();
		final PreferenceScreen personalPref = getPreferenceManager().createPreferenceScreen(this);
		personalPref.setTitle(R.string.personal_datas);
		optionsPref.addPreference(personalPref);
		
		final PreferenceCategory nameCat = new PreferenceCategory(this);
		nameCat.setTitle(R.string.name);
		personalPref.addPreference(nameCat);
		
		final EditTextPreference firstNamePref = new EditTextPreference(this);
		final EditTextPreference lastNamePref = new EditTextPreference(this);
		
		final OnPreferenceChangeListener onNameChange = new Preference.OnPreferenceChangeListener() {
			
			@Override
			public boolean onPreferenceChange(final Preference preference, Object newValue) {
				final String value = newValue.toString();
				boolean ret = InputValidator.isNameValid(value);
				if (ret) {
					preference.setSummary(value);
					setProgressBarIndeterminateVisibility(true);
					getModel().setPersonalData(preference == firstNamePref ? PersonalDataType.FIRST_NAME : PersonalDataType.LAST_NAME, value, new ModelActionListener<Integer>() {
						
						@Override
						public void modelActionPerformed(ModelActionEvent<Integer> e) {
							new IntegerMillModelActivityAdapter(PlayerSettingsActivity.this, e) {
								
								@Override
								public void onEvent(int e) {
									setProgressBarIndeterminateVisibility(false);
									switch (getReturn(e)) {
										case OK:
											if (preference == firstNamePref) personalData.setFirstName(value);
											else personalData.setLastName(value);
											break;
									}
								}
								
							};
						}
						
					});
				}
				else {
					showToast(R.string.wrong_value);
				}
				return ret;
			}
			
		};
		
		firstNamePref.setTitle(R.string.first_name);
		firstNamePref.setText(personalData.getFirstName());
		firstNamePref.setSummary(personalData.getFirstName());
		firstNamePref.setOnPreferenceChangeListener(onNameChange);
		
		nameCat.addPreference(firstNamePref);
		
		lastNamePref.setTitle(R.string.last_name);
		lastNamePref.setText(personalData.getLastName());
		lastNamePref.setSummary(personalData.getLastName());
		lastNamePref.setOnPreferenceChangeListener(onNameChange);
		nameCat.addPreference(lastNamePref);
		
		final CheckBoxPreference inverseNamePref = new CheckBoxPreference(this);
		inverseNamePref.setTitle(R.string.inverse_name);
		inverseNamePref.setChecked(personalData.isInverseName());
		nameCat.addPreference(inverseNamePref);
		
		final PreferenceCategory locationCat = new PreferenceCategory(this);
		locationCat.setTitle(R.string.location);
		personalPref.addPreference(locationCat);
		
		final AutoCompletePreference countryPref = new AutoCompletePreference(this);
		countryPref.setTitle(R.string.country);
		countryPref.setText(personalData.getCountry());
		countryPref.setSummary(personalData.getCountry());
		locationCat.addPreference(countryPref);
		
		final AutoCompletePreference regionPref = new AutoCompletePreference(this);
		regionPref.setTitle(R.string.region);
		regionPref.setText(personalData.getRegion());
		regionPref.setSummary(personalData.getRegion());
		locationCat.addPreference(regionPref);
		
		final AutoCompletePreference cityPref = new AutoCompletePreference(this);
		cityPref.setTitle(R.string.city);
		cityPref.setText(personalData.getCity());
		cityPref.setSummary(personalData.getCity());
		locationCat.addPreference(cityPref);
		
		final PreferenceCategory othersCat = new PreferenceCategory(this);
		othersCat.setTitle(R.string.others);
		personalPref.addPreference(othersCat);
		
		final Preference birthdayPref = new Preference(this);
		birthdayPref.setTitle(R.string.birthday);
		birthdayPref.setSummary(getDate(personalData.getBirthDate()));
		othersCat.addPreference(birthdayPref);
		
		final Preference sexPref = new Preference(this);
		sexPref.setTitle(R.string.sex);
		sexPref.setSummary(getSex(this, personalData.getSex()));
		othersCat.addPreference(sexPref);
	}
	
	private PlayerReturn getReturn(int i) {
		return getEnumValue(PlayerReturn.class, i);
	}
	
	public static String getDate(Date date) {
		return date == null ? "" : DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.getDefault()).format(date);
	}
	
	public static String getSex(Context context, Sex sex) {
		if (sex == null) return "";
		switch (sex) {
			case MALE:
				return context.getString(R.string.male);
			case FEMALE:
				return context.getString(R.string.female);
			default:
				return context.getString(R.string.other);
		}
	}
	
}