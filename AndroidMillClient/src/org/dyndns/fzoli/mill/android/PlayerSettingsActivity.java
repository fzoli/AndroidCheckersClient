package org.dyndns.fzoli.mill.android;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import org.dyndns.fzoli.android.widget.AutoCompletePreference;
import org.dyndns.fzoli.android.widget.TextWatcherAdapter;
import org.dyndns.fzoli.mill.android.activity.AbstractMillOnlineBundlePreferenceActivity;
import org.dyndns.fzoli.mill.android.activity.IntegerMillModelActivityAdapter;
import org.dyndns.fzoli.mill.android.activity.MillModelActivityAdapter;
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

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceScreen;
import android.preference.Preference.OnPreferenceChangeListener;
import android.text.Editable;
import android.widget.DatePicker;
import android.widget.Toast;

public class PlayerSettingsActivity extends AbstractMillOnlineBundlePreferenceActivity<PlayerEvent, PlayerData> {
	
	private static final int ID_DATE_DIALOG = 0;
	private static final String KEY_YEAR = "1", KEY_MONTH = "2", KEY_DAY = "3";
	
	private Preference birthdayPref;
	private AutoCompletePreference regionPref, cityPref;
	
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
	
	private Timer timer;
	
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
		//avatarPref.setEnabled(false); //TODO: bug javítása után törölni az utasítást
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
		final CheckBoxPreference inverseNamePref = new CheckBoxPreference(this);
		
		final OnPreferenceChangeListener onNameChange = new Preference.OnPreferenceChangeListener() {
			
			@Override
			public boolean onPreferenceChange(final Preference preference, Object newValue) {
				final String value = newValue.toString();
				boolean ret = InputValidator.isNameValid(value);
				if (ret) {
					preference.setSummary(value);
					preference.setEnabled(false);
					getModel().setPersonalData(preference == firstNamePref ? PersonalDataType.FIRST_NAME : PersonalDataType.LAST_NAME, value, new ModelActionListener<Integer>() {
						
						@Override
						public void modelActionPerformed(ModelActionEvent<Integer> e) {
							new IntegerMillModelActivityAdapter(PlayerSettingsActivity.this, e) {
								
								@Override
								public void onEvent(int e) {
									preference.setEnabled(true);
									switch (getReturn(e)) {
										case OK:
											if (preference == firstNamePref) personalData.setFirstName(value);
											else personalData.setLastName(value);
											setInverseNameEnabled(inverseNamePref, personalData);
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
		firstNamePref.getEditText().setSingleLine();
		firstNamePref.setText(personalData.getFirstName());
		firstNamePref.setSummary(personalData.getFirstName());
		firstNamePref.setOnPreferenceChangeListener(onNameChange);
		
		nameCat.addPreference(firstNamePref);
		
		lastNamePref.setTitle(R.string.last_name);
		lastNamePref.getEditText().setSingleLine();
		lastNamePref.setText(personalData.getLastName());
		lastNamePref.setSummary(personalData.getLastName());
		lastNamePref.setOnPreferenceChangeListener(onNameChange);
		nameCat.addPreference(lastNamePref);
		
		inverseNamePref.setTitle(R.string.inverse_name);
		setInverseNameEnabled(inverseNamePref, personalData);
		inverseNamePref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
			
			@Override
			public boolean onPreferenceChange(Preference paramPreference, Object paramObject) {
				inverseNamePref.setEnabled(false);
				final boolean val = !inverseNamePref.isChecked();
				getModel().setPersonalData(PersonalDataType.INVERSE_NAME, Boolean.toString(val), new ModelActionListener<Integer>() {
					
					@Override
					public void modelActionPerformed(ModelActionEvent<Integer> e) {
						new IntegerMillModelActivityAdapter(PlayerSettingsActivity.this, e) {
							
							@Override
							public void onEvent(int e) {
								inverseNamePref.setEnabled(true);
								switch (getReturn(e)) {
									case OK:
										personalData.setInverseName(val);
										break;
									default:
										inverseNamePref.setChecked(!val);
								}
							}
							
						};
					}
					
				});
				return true;
			}
			
		});
		nameCat.addPreference(inverseNamePref);
		
		final PreferenceCategory locationCat = new PreferenceCategory(this);
		locationCat.setTitle(R.string.location);
		personalPref.addPreference(locationCat);
		
		final AutoCompletePreference countryPref = new AutoCompletePreference(this);
		countryPref.setTitle(R.string.country);
		countryPref.setText(personalData.getCountry());
		countryPref.setSummary(personalData.getCountry());
		countryPref.getEditText().addTextChangedListener(new TextWatcherAdapter() {
			
			@Override
			public void afterTextChanged(Editable paramEditable) {
				initAutoComplette(countryPref, new Runnable() {
					
					@Override
					public void run() {
						getModel().loadCountries(countryPref.getText(), createAutoCompletteHandler(countryPref));
					}
					
				});
			}
			
		});
		countryPref.setOnPreferenceChangeListener(createLocationSetter(countryPref, PersonalDataType.COUNTRY, personalData, personalData.getCountry()));
		locationCat.addPreference(countryPref);
		
		regionPref = new AutoCompletePreference(this);
		regionPref.setTitle(R.string.region);
		regionPref.setText(personalData.getRegion());
		regionPref.setSummary(personalData.getRegion());
		regionPref.getEditText().addTextChangedListener(new TextWatcherAdapter() {
			
			@Override
			public void afterTextChanged(Editable paramEditable) {
				initAutoComplette(regionPref, new Runnable() {
					
					@Override
					public void run() {
						getModel().loadRegions(regionPref.getText(), createAutoCompletteHandler(regionPref));
					}
					
				});
			}
			
		});
		regionPref.setOnPreferenceChangeListener(createLocationSetter(regionPref, PersonalDataType.REGION, personalData, personalData.getRegion()));
		locationCat.addPreference(regionPref);
		
		cityPref = new AutoCompletePreference(this);
		cityPref.setTitle(R.string.city);
		cityPref.setText(personalData.getCity());
		cityPref.setSummary(personalData.getCity());
		cityPref.getEditText().addTextChangedListener(new TextWatcherAdapter() {
			
			@Override
			public void afterTextChanged(Editable paramEditable) {
				initAutoComplette(cityPref, new Runnable() {
					
					@Override
					public void run() {
						getModel().loadCities(cityPref.getText(), createAutoCompletteHandler(cityPref));
					}
					
				});
			}
			
		});
		cityPref.setOnPreferenceChangeListener(createLocationSetter(cityPref, PersonalDataType.CITY, personalData, personalData.getCity()));
		locationCat.addPreference(cityPref);
		
		setLocationsEnabled();
		
		final PreferenceCategory othersCat = new PreferenceCategory(this);
		othersCat.setTitle(R.string.others);
		personalPref.addPreference(othersCat);
		
		birthdayPref = new Preference(this);
		birthdayPref.setTitle(R.string.birthday);
		setBirthDate(personalData.getBirthDate());
		birthdayPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
			
			@Override
			public boolean onPreferenceClick(Preference paramPreference) {
				final Calendar c = Calendar.getInstance();
				if (personalData.getBirthDate() != null) c.setTime(personalData.getBirthDate());
			    Bundle bundle = new Bundle();
			    bundle.putInt(KEY_YEAR, c.get(Calendar.YEAR));
			    bundle.putInt(KEY_MONTH, c.get(Calendar.MONTH));
			    bundle.putInt(KEY_DAY, c.get(Calendar.DAY_OF_MONTH));
				showDialog(ID_DATE_DIALOG, bundle);
				return true;
			}
			
		});
		othersCat.addPreference(birthdayPref);
		
		final Preference sexPref = new Preference(this);
		sexPref.setTitle(R.string.sex);
		sexPref.setSummary(getSex(this, personalData.getSex()));
		sexPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
			
			@Override
			public boolean onPreferenceClick(Preference paramPreference) {
				final PlayerSettingsActivity c = PlayerSettingsActivity.this;
				new AlertDialog.Builder(c)
				.setTitle(R.string.sex)
				.setItems(new String[]{getSex(c, Sex.MALE), getSex(c, Sex.FEMALE), getSex(c, Sex.OTHER)}, new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						final Sex sex;
						switch (which) {
							case 0:
								sex = Sex.MALE;
								break;
							case 1:
								sex = Sex.FEMALE;
								break;
							default:
								sex = Sex.OTHER;
						}
						if (sex.equals(personalData.getSex())) return;
						sexPref.setEnabled(false);
						getModel().setPersonalData(PersonalDataType.SEX, sex.name(), new ModelActionListener<Integer>() {
							
							@Override
							public void modelActionPerformed(ModelActionEvent<Integer> e) {
								new IntegerMillModelActivityAdapter(c, e) {
									
									@Override
									public void onEvent(int e) {
										sexPref.setEnabled(true);
										personalData.setSex(sex);
										sexPref.setSummary(getSex(c, sex));
									}
									
								};
							}
							
						});
					}
					
				})
				.create().show();
				return true;
			}
			
		});
		othersCat.addPreference(sexPref);
	}
	
	@Override
	protected Dialog onCreateDialog(int id, Bundle args) {
		switch (id) {
			case ID_DATE_DIALOG:
				return new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
					
					@Override
					public void onDateSet(DatePicker picker, int year, int month, int day) {
						final Calendar c = Calendar.getInstance();
						c.set(year, month, day);
						final Date d = c.getTime();
						if (InputValidator.isBirthDateValid(d)) {
							birthdayPref.setEnabled(false);
							getModel().setPersonalData(PersonalDataType.BIRTH_DATE, Long.toString(d.getTime()), new ModelActionListener<Integer>() {
								
								@Override
								public void modelActionPerformed(ModelActionEvent<Integer> e) {
									new IntegerMillModelActivityAdapter(PlayerSettingsActivity.this, e) {
										
										@Override
										public void onEvent(int e) {
											birthdayPref.setEnabled(true);
											getModel().getCache().getPlayer().getPersonalData().setBirthDate(d);
										};
											
									};
								}
								
							});
							setBirthDate(d);
						}
						else {
							showToast(R.string.wrong_value);
						}
					}
					
				}, args.getInt(KEY_YEAR), args.getInt(KEY_MONTH), args.getInt(KEY_DAY));
		}
		return super.onCreateDialog(id, args);
	}
	
	private void setInverseNameEnabled(CheckBoxPreference pref, PersonalData personalData) {
		if (InputValidator.isNameValid(personalData.getFirstName()) && InputValidator.isNameValid(personalData.getLastName())) {
			pref.setChecked(personalData.isInverseName());
			pref.setEnabled(true);
		}
		else {
			pref.setChecked(false);
			pref.setEnabled(false);
		}
	}
	
	private void setLocationsEnabled() {
		PersonalData data = getModel().getCache().getPlayer().getPersonalData();
		regionPref.setEnabled(data.getCountry() != null);
		cityPref.setEnabled(data.getRegion() != null);
	}
	
	private void initAutoComplette(final AutoCompletePreference pref, final Runnable method) {
		if (timer != null) timer.cancel();
		timer = new Timer();
		timer.schedule(new TimerTask() {
			
			@Override
			public void run() {
				runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						method.run();
					}
					
				});
			}
			
		}, 500);
	}
	
	private Preference.OnPreferenceChangeListener createLocationSetter(final AutoCompletePreference pref, final PersonalDataType type, final PersonalData personalData, final String oldVal) {
		return new Preference.OnPreferenceChangeListener() {
			
			@Override
			public boolean onPreferenceChange(Preference paramPreference, Object paramObject) {
				if (pref.getText().equals(oldVal)) return true;
				pref.setEnabled(false);
				regionPref.setEnabled(false);
				cityPref.setEnabled(false);
				getModel().setPersonalData(type, pref.getText(), new ModelActionListener<Integer>() {
					
					@Override
					public void modelActionPerformed(ModelActionEvent<Integer> e) {
						new IntegerMillModelActivityAdapter(PlayerSettingsActivity.this, e) {
							
							@Override
							public void onEvent(int e) {
								pref.setEnabled(true);
								regionPref.setEnabled(true);
								cityPref.setEnabled(true);
								String val;
								boolean ok;
								switch (getReturn(e)) {
									case OK:
										ok = true;
										val = pref.getText();
										break;
									default:
										ok = false;
										val = oldVal;
										showToast(R.string.wrong_value);
								}
								switch (type) {
									case COUNTRY:
										personalData.setCountry(val);
										if (ok) {
											personalData.setRegion(null);
											personalData.setCity(null);
											regionPref.setText(null);
											regionPref.setSummary(null);
											cityPref.setText(null);
											cityPref.setSummary(null);
										}
										break;
									case REGION:
										personalData.setRegion(val);
										if (ok) {
											personalData.setCity(null);
											cityPref.setText(null);
											cityPref.setSummary(null);
										}
										break;
									case CITY:
										personalData.setCity(val);
								}
								pref.setText(val);
								pref.setSummary(val);
								setLocationsEnabled();
							}
							
						};
					}
					
				});
				return true;
			}
			
		};
	}
	
	private ModelActionListener<PlayerData> createAutoCompletteHandler(final AutoCompletePreference pref) {
		return new ModelActionListener<PlayerData>() {
			
			@Override
			public void modelActionPerformed(ModelActionEvent<PlayerData> e) {
				new MillModelActivityAdapter<PlayerData>(PlayerSettingsActivity.this, e) {
					
					@Override
					public void onEvent(PlayerData e) {
						pref.setItems(e.getPlaces());
					}
					
				};
			}
			
		};
	}
	
	private PlayerReturn getReturn(int i) {
		return getEnumValue(PlayerReturn.class, i);
	}
	
	public void setBirthDate(Date date) {
		String val = date == null ? "" : DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.getDefault()).format(date);
		birthdayPref.setSummary(val);
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