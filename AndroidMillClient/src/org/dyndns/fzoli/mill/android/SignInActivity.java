package org.dyndns.fzoli.mill.android;

import java.util.ArrayList;
import java.util.List;

import org.dyndns.fzoli.android.context.activity.AbstractNetworkInfoActivity;
import org.dyndns.fzoli.android.widget.ConfirmDialog;
import org.dyndns.fzoli.android.widget.TextWatcherAdapter;
import org.dyndns.fzoli.mill.android.activity.MillModelActivityUtil;
import org.dyndns.fzoli.mill.android.entity.UserInfo;
import org.dyndns.fzoli.mill.android.entity.UserInfoSettings;
import org.dyndns.fzoli.mill.android.service.MillConnectionBinder;
import org.dyndns.fzoli.mill.android.service.MillServiceConnection;
import org.dyndns.fzoli.mill.android.service.MillConnectionBinder.LoginMode;
import org.dyndns.fzoli.mill.common.InputValidator;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

public class SignInActivity extends AbstractNetworkInfoActivity {
	
	//TODO:
	// a) - A sign in ablak a kezdőablak.
	//    - Létrejövésekor - ha be van jelentkezve a felhasználó, home activity megnyitása.
	//                     - ha bejelentkezési hibát közöltek vele, hiba kijelzése.
	//    - A bejelentkezés gombra kattintás után is home activity megnyitása.
	// b) - A home activity kapcsolódik a szerverhez és ha nincs beléptetve a felhasználó, megkísérli.
	//    - Ha nem sikerült, sign in ablakot meghívja, és közli vele a hiba tényét, hogy hibát dobjon fel.
	
	private MillConnectionBinder mcb;
	private UserInfoSettings userInfoSettings;
	private EditText etPassword;
	private AutoCompleteTextView etUser;
	private Button btSignUp, btSignIn;
	private ImageView warnUser, warnPassword;
	private List<UserInfo> userInfoList;
	private List<String> userNameList = new ArrayList<String>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setVisible(false);
		setContentView(R.layout.sign_in);
		
		etUser = (AutoCompleteTextView) findViewById(R.id.etUser);
		etPassword = (EditText) findViewById(R.id.etPassword);
		btSignUp = (Button) findViewById(R.id.btSignUp);
		btSignIn = (Button) findViewById(R.id.btSignIn);
		warnUser = (ImageView) findViewById(R.id.warnUser);
		warnPassword = (ImageView) findViewById(R.id.warnPassword);
		
		MillModelActivityUtil.addLengthFilter(etUser, InputValidator.MAX_USER_LENGTH);
		MillModelActivityUtil.addLengthFilter(etPassword, InputValidator.MAX_PASSWORD_LENGTH);
		
		MillModelActivityUtil.bindMillConnectionService(this, new MillServiceConnection() {

			@Override
			public void onServiceConnected(ComponentName cn, final MillConnectionBinder b) {
				mcb = b;
				try {
					setVisible(true);
				}
				catch (NullPointerException ex) {
					return;
				}
				prepare();
				
				etUser.addTextChangedListener(new TextWatcherAdapter() {
					
					boolean wasSelected;
					
					@Override
					public void beforeTextChanged(CharSequence cs, int paramInt1, int paramInt2, int paramInt3) {
						wasSelected = isUserSelected(cs.toString());
					}
					
					@Override
					public void afterTextChanged(Editable e) {
						if (wasSelected && !isUserSelected(e.toString())) {
							etPassword.setText("");
							etPassword.setEnabled(true);
						}
						if (isUserSelected(e.toString()) && userInfoSettings.isSavePassword()) {
							UserInfo selected = findUserInfo();
							if (selected != null && selected.getPasswordLength() > 0) {
								setFakePassword(selected);
								etPassword.setEnabled(false);
								mcb.getUserInfo().setPassword(selected.getPasswordHash(), selected.getPasswordLength());
							}
						}
						if (InputValidator.isUserIdValid(e.toString())) {
							warnUser.setVisibility(View.INVISIBLE);
							mcb.getUserInfo().setUser(e.toString());
						}
					}
					
				});
				
				etPassword.addTextChangedListener(new TextWatcherAdapter() {
					
					@Override
					public void afterTextChanged(Editable e) {
						if (InputValidator.isPasswordValid(e.toString())) {
							warnPassword.setVisibility(View.INVISIBLE);
						}
					}
					
				});
				
				switch (mcb.getLoginMode()) {
					case SIGN_IN_FAILED:
						MillModelActivityUtil.showAlertDialog(SignInActivity.this, R.string.sign_in_error, R.string.sign_in_failed, new DialogInterface.OnClickListener() {
						
							@Override
							public void onClick(DialogInterface paramDialogInterface, int paramInt) {
								mcb.setLoginMode(LoginMode.SIGNED_OUT);
								resetPassword();
							}
						
						});
						break;
					case ERROR:
						MillModelActivityUtil.showAlertDialog(SignInActivity.this, R.string.connection_error, null, new DialogInterface.OnClickListener() {
							
							@Override
							public void onClick(DialogInterface paramDialogInterface, int paramInt) {
								mcb.setLoginMode(LoginMode.SIGNED_OUT);
							}
							
						});
						break;
					case KICKED:
						MillModelActivityUtil.showAlertDialog(SignInActivity.this, R.string.server_kick, null, new DialogInterface.OnClickListener() {
							
							@Override
							public void onClick(DialogInterface paramDialogInterface, int paramInt) {
								mcb.setLoginMode(LoginMode.SIGNED_OUT);
							}
							
						});
						break;
					case INVISIBLE_ERROR:
						mcb.setLoginMode(LoginMode.SIGNED_OUT);
				}
				
			}
			
		});
		
		btSignUp.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View paramView) {
				startActivity(new Intent(SignInActivity.this, SignUpActivity.class));
			}
			
		});
		
		btSignIn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View paramView) {
				String user = etUser.getText().toString();
				String password = etPassword.getText().toString();
				boolean userOk = InputValidator.isUserIdValid(user);
				boolean passwordOk = InputValidator.isPasswordValid(password);
				if (!userOk || !passwordOk) {
					if (!userOk) warnUser.setVisibility(View.VISIBLE);
					if (!passwordOk) warnPassword.setVisibility(View.VISIBLE);
				}
				else {
					if (userInfoSettings.isSaveUser()) { // ha adatbázisba kell menteni
						userInfoSettings.setLastName(user); //utolsó név módosítása, mentése
						mcb.getDatabaseHelper().setUserInfoSettings(userInfoSettings);
						UserInfo ui = findUserInfo();
						if (ui != null) { //felhasználónév tárolva
							if (userInfoSettings.isSavePassword()) { //tárolni kell a jelszót
								if (ui.getPasswordLength() <= 0) { // jelszó nincs tárolva: jelszó módosítása, és ha kell, mentése
									ui.setPassword(password); // jelszó beállítása és mentése
									mcb.getDatabaseHelper().store(ui);
									mcb.getUserInfo().setPassword(password);
								}
								else { // jelszó már tárolva
									mcb.getUserInfo().setPassword(ui.getPasswordHash(), ui.getPasswordLength());
								}
							}
							else { // nem kell tárolni a jelszót
								mcb.getUserInfo().setPassword(password);
							}
						}
						else { // új: nincs se név, se jelszó tárolva
							mcb.getUserInfo().setServer(mcb.getDatabaseHelper().getConnectionSettings().getUrl()); //aktuális szerver
							if (userInfoSettings.isSavePassword()) { //ha tároljuk a jelszót adatbázisban
								mcb.getUserInfo().setPassword(password); //jelszó beállítás
								mcb.getDatabaseHelper().store(mcb.getUserInfo()); // szerver, név és beállított jelszó tárolása
							}
							else { // ha nem tároljuk a jelszót adatbázisban
								mcb.getDatabaseHelper().store(mcb.getUserInfo()); // szerver, név és beállítattlan jelszó tárolása
								mcb.getUserInfo().setPassword(password); //jelszó beállítás
							}
							mcb.reinitDatabaseHelper();
						}
					}
					else { //ha nem kell menteni adatbázisba
						mcb.getUserInfo().setPassword(password); //jelszó beállítás
					}
					mcb.setLoginMode(LoginMode.SIGNING_IN);
					openHome();
				}
			}
			
		});
		
	}
	
	@Override
	public void onBackPressed() {
		stopService(new Intent(this, MillConnectionService.class));
		super.onBackPressed();
	}
	
	@Override
	public void onResume() {
		super.onResume();
		prepare();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.sign_in, menu);
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch (item.getItemId()) {
			case R.id.entrySettings:
				startActivity(new Intent(this, SettingsActivity.class));
				break;
		}
		return super.onMenuItemSelected(featureId, item);
	}
	
	private void prepare() {
		if (mcb != null) {
			if (mcb.getLoginMode().equals(LoginMode.SIGNED_IN) && isNetworkAvailable()) openHome();
			userInfoSettings = mcb.getDatabaseHelper().getUserInfoSettings();
			userInfoList = mcb.getDatabaseHelper().getUserInfoList(true);
			initEt();
		}
	}
	
	private void openHome() {
		MillModelActivityUtil.openActivity(this, HomeActivity.class);
	}
	
	private void resetPassword() {
		etPassword.setText("");
		etPassword.setEnabled(true);
		UserInfo i = findUserInfo();
		if (i != null) {
			i.setPassword("");
			mcb.getDatabaseHelper().store(i);
			initEt();
		}
	}
	
	private UserInfo findUserInfo() {
		return mcb.getDatabaseHelper().findUserInfo(etUser.getText().toString(), userInfoList);
	}
	
	private void setFakePassword(UserInfo selected) {
		StringBuffer s = new StringBuffer();
		for (int i = 0; i < selected.getPasswordLength(); i++) {
			s.append("a");
		}
		etPassword.setText(s.toString());
	}
	
	private void initEt() {
		userNameList.clear();
		String s = mcb.getUserInfo().getUser();
		if (s.isEmpty() && userInfoSettings.isSaveUser()) {
			etUser.setText(userInfoSettings.getLastName());
			mcb.getUserInfo().setUser(etUser.getText().toString());
		}
		else {
			etUser.setText(s);
		}
		etUser.setSelection(etUser.getText().length());
		if (userInfoSettings.isSaveUser()) {
			for (UserInfo ui : userInfoList) {
				userNameList.add(ui.getUser());
			}
			
			etUser.setAdapter(new ArrayAdapter<String>(SignInActivity.this, android.R.layout.simple_dropdown_item_1line, userNameList));
			
			UserInfo i = findUserInfo();
			if (userInfoSettings.isSavePassword() && i != null && i.getPasswordLength() > 0) {
				etPassword.setEnabled(false);
				setFakePassword(i);
			}
			else {
				etPassword.setText("");
				etPassword.setEnabled(true);
			}
			
			etUser.setOnLongClickListener(new View.OnLongClickListener() {
				
				@Override
				public boolean onLongClick(View paramView) {
					if (isUserSelected()) {
						UserInfo i = findUserInfo();
						AlertDialog.Builder builder = new AlertDialog.Builder(SignInActivity.this);
				    	builder.setTitle(R.string.delete)
				    	.setItems((i.getPasswordLength() <= 0 || !userInfoSettings.isSavePassword()) ? new CharSequence[] {getString(R.string.delete_user)} : new CharSequence[] {getString(R.string.delete_user), getString(R.string.delete_password)}, new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface paramDialogInterface, int paramInt) {
								switch (paramInt) {
									case 0:
										new ConfirmDialog(SignInActivity.this, android.R.drawable.ic_dialog_alert, getString(R.string.delete_user), getString(R.string.delete_confirm), getString(R.string.yes), getString(R.string.no), new DialogInterface.OnClickListener() {
											
											@Override
											public void onClick(DialogInterface dialog, int which) {
												mcb.getDatabaseHelper().delete(findUserInfo());
												userInfoList = mcb.getDatabaseHelper().getUserInfoList(true);
												initEt();
											}
											
										}).show();
										break;
									case 1:
										new ConfirmDialog(SignInActivity.this, android.R.drawable.ic_dialog_alert, getString(R.string.delete_password), getString(R.string.delete_password_confirm), getString(R.string.yes), getString(R.string.no), new DialogInterface.OnClickListener() {
											
											@Override
											public void onClick(DialogInterface dialog, int which) {
												resetPassword();
											}
											
										}).show();
										break;
								}
							}
				    		
				    	}).show();
						return true;
					}
					return false;
				}
				
			});
			
		}
		else {
			resetPassword();
			etUser.setAdapter(new ArrayAdapter<String>(SignInActivity.this, android.R.layout.simple_dropdown_item_1line, new String[]{}));
		}
	}
	
	private boolean isUserSelected() {
		return isUserSelected(etUser.getText().toString());
	}
	
	private boolean isUserSelected(String s) {
		return userNameList.contains(s);
	}
	
}