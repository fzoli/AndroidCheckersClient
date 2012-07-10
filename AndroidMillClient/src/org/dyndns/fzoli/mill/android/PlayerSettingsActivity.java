package org.dyndns.fzoli.mill.android;

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

import org.dyndns.fzoli.android.widget.AutoCompletePreference;
import org.dyndns.fzoli.mill.android.activity.AbstractMillOnlineBundlePreferenceActivity;
import org.dyndns.fzoli.mill.client.model.PlayerModel;
import org.dyndns.fzoli.mill.common.model.entity.PersonalData;
import org.dyndns.fzoli.mill.common.model.entity.Sex;
import org.dyndns.fzoli.mill.common.model.pojo.PlayerData;
import org.dyndns.fzoli.mill.common.model.pojo.PlayerEvent;
import org.dyndns.fzoli.mvc.client.android.activity.ConnectionActivity;
import org.dyndns.fzoli.mvc.client.connection.Connection;
import org.dyndns.fzoli.mvc.client.model.CachedModel;

import android.content.Context;
import android.content.Intent;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceScreen;

public class PlayerSettingsActivity extends AbstractMillOnlineBundlePreferenceActivity<PlayerEvent, PlayerData> {
	
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
		firstNamePref.setTitle(R.string.first_name);
		firstNamePref.setText(personalData.getFirstName());
		firstNamePref.setSummary(personalData.getFirstName());
		firstNamePref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
			
			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				return true;
			}
			
		});
		nameCat.addPreference(firstNamePref);
		
		final EditTextPreference lastNamePref = new EditTextPreference(this);
		lastNamePref.setTitle(R.string.last_name);
		lastNamePref.setText(personalData.getLastName());
		lastNamePref.setSummary(personalData.getLastName());
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