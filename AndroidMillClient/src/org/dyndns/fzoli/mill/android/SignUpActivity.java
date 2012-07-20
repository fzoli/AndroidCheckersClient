package org.dyndns.fzoli.mill.android;

import java.text.DecimalFormat;
import java.util.Date;

import org.dyndns.fzoli.android.widget.EditTextAction;
import org.dyndns.fzoli.android.widget.ProgressEditTextLayout;
import org.dyndns.fzoli.android.widget.TextWatcherAdapter;
import org.dyndns.fzoli.mill.android.activity.AbstractMillModelActivity;
import org.dyndns.fzoli.mill.android.activity.IntegerMillModelActivityAdapter;
import org.dyndns.fzoli.mill.android.activity.TimeCounter;
import org.dyndns.fzoli.mill.android.entity.UserInfo;
import org.dyndns.fzoli.mill.android.entity.UserInfoSettings;
import org.dyndns.fzoli.mill.android.service.MillDatabaseHelper;
import org.dyndns.fzoli.mill.android.service.MillConnectionBinder.LoginMode;
import org.dyndns.fzoli.mill.client.model.PlayerBuilderModel;
import org.dyndns.fzoli.mill.common.InputValidator;
import org.dyndns.fzoli.mill.common.key.PlayerBuilderKeys;
import org.dyndns.fzoli.mill.common.key.PlayerBuilderReturn;
import org.dyndns.fzoli.mill.common.model.pojo.PlayerBuilderData;
import org.dyndns.fzoli.mill.common.model.pojo.PlayerBuilderEvent;
import org.dyndns.fzoli.mvc.client.connection.Connection;
import org.dyndns.fzoli.mvc.client.event.ModelActionEvent;
import org.dyndns.fzoli.mvc.client.event.ModelActionListener;
import org.dyndns.fzoli.mvc.client.model.CachedModel;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class SignUpActivity extends AbstractMillModelActivity<PlayerBuilderEvent, PlayerBuilderData> implements PlayerBuilderKeys {
	
	private static final String PASSWORD = "password";
	private static final String PASSWORD_AGAIN = "password2";
	
	private String prevUser, prevEmail;
	private boolean userNotFree = false, emailNotFree = false;
	private ProgressEditTextLayout etlUser, etlPassword, etlPasswordAgain, etlEmail;
	private TextView tvUserCount, tvCountdown;
	private Button btSignUp;
	
	@Override
	public boolean onPrepareModelCreate(ModelActionEvent<PlayerBuilderData> e) {
		if (e.getType() == ModelActionEvent.TYPE_EVENT) {
			if (prepare(e.getEvent())) return false;
		}
		return super.onPrepareModelCreate(e);
	}
	
	private TimeCounter counter = new TimeCounter(new TimeCounter.TimeCounterTask() {
		
		@Override
		public void hit(final int hour, final int minute, final int second, final int time) {
			runOnUiThread(new Runnable() {

				@Override
				public void run() {
					if (time == 0) reset();
					int timeout = getCache().getTimeout() / 1000;
					if (time == timeout) setProgress(10000 - 2);
					else setProgress((int)(10000 - (100 * (100 - (100 * (time / (double)timeout))))));
					tvCountdown.setText(minute + ":" + (second < 10 ? "0" + second : second + ""));
				}
				
			});
		}
		
	});
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		requestWindowFeature(Window.FEATURE_PROGRESS);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sign_up);
		setProgressBarIndeterminateVisibility(false);
		etlUser = (ProgressEditTextLayout) findViewById(R.id.etlUser);
		etlPassword = (ProgressEditTextLayout) findViewById(R.id.etlPassword);
		etlPasswordAgain = (ProgressEditTextLayout) findViewById(R.id.etlPasswordAgain);
		etlEmail = (ProgressEditTextLayout) findViewById(R.id.etlEmail);
		tvUserCount = (TextView) findViewById(R.id.tvUserCount);
		tvCountdown = (TextView) findViewById(R.id.tvCountdown);
		btSignUp = (Button) findViewById(R.id.btSignUp);
		
		addLengthFilter(etlUser.getEditText(), InputValidator.MAX_USER_LENGTH);
		addLengthFilter(etlPassword.getEditText(), InputValidator.MAX_PASSWORD_LENGTH);
		addLengthFilter(etlPasswordAgain.getEditText(), InputValidator.MAX_PASSWORD_LENGTH);
		
		btSignUp.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				createUser();
			}
			
		});
		
		etlUser.getEditText().setAction(new EditTextAction() {
			
			@Override
			public boolean actionPerformed(boolean focus) {
				trim(etlUser);
				setUser();
				return true;
			}
			
		});
		
		etlPassword.getEditText().setAction(new EditTextAction() {
			
			@Override
			public boolean actionPerformed(boolean focus) {
				trim(etlPassword);
				return setPassword(true);
			}
			
		});
		
		etlPassword.getEditText().addTextChangedListener(new TextWatcherAdapter() {
			
			@Override
			public void afterTextChanged(Editable paramEditable) {
				setPassword(false);
			}
			
		});
		
		etlPasswordAgain.getEditText().addTextChangedListener(new TextWatcherAdapter() {
			
			@Override
			public void afterTextChanged(Editable paramEditable) {
				setPasswordAgain(false);
			}
			
		});
		
		etlPasswordAgain.getEditText().setAction(new EditTextAction() {
			
			@Override
			public boolean actionPerformed(boolean focus) {
				trim(etlPasswordAgain);
				return setPasswordAgain(true);
			}
			
		});
		
		etlEmail.getEditText().setAction(new EditTextAction() {
			
			@Override
			public boolean actionPerformed(boolean focus) {
				trim(etlEmail);
				setEmail();
				return true;
			}
			
		});
	}
	
	private void trim(ProgressEditTextLayout etl) {
		String d = etl.getEditText().getText().toString();
		String s = d.trim();
		if (!s.equals(d)) etl.getEditText().setText(s);
	}
	
	@Override
	public void onPause() {
		try {
			setPassword(false);
			setPasswordAgain(false);
			setUser();
			setEmail();
			counter.stop();
		}
		catch (Exception ex) {
			;
		}
		super.onPause();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.sign_up, menu);
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch (item.getItemId()) {
			case R.id.entryReset:
				reset();
				break;
		}
		return super.onMenuItemSelected(featureId, item);
	}
	
	@Override
	public PlayerBuilderModel getModel() {
		return (PlayerBuilderModel) super.getModel();
	}
	
	@Override
	public CachedModel<Object, Object, PlayerBuilderEvent, PlayerBuilderData> createModel(Connection<Object, Object> connection) {
		return new PlayerBuilderModel(connection);
	}

	@Override
	public boolean processModelChange(PlayerBuilderEvent e) {
		if (e.isReset()) {resetGUI(true); processModelData(getCache());}
		else setUserCount(e.getUserCount());
		return true;
	}
	
	@Override
	public boolean processModelData(PlayerBuilderData e) {
		if (prepare(e)) {
			Toast.makeText(this, R.string.server_restart, Toast.LENGTH_LONG).show();
			return true;
		}
		resetGUI(false);
		etlUser.getEditText().setText(e.getUser());
		prevUser = e.getUser();
		etlEmail.getEditText().setText(e.getEmail());
		prevEmail = e.getEmail();
		etlPassword.getEditText().setText(getVar(PASSWORD));
		etlPasswordAgain.getEditText().setText(getVar(PASSWORD_AGAIN));
		setUserCount(e.getUserCount());
		setTimeCounter(e);
		return true;
	}
	
	private boolean prepare(PlayerBuilderData e) {
		if (!e.isCaptchaValidated()) {startActivity(new Intent(this, SignUpCaptchaActivity.class));}
		return !e.isCaptchaValidated();
	}
	
	private void setUserCount(long c) {
		tvUserCount.setText(getString(R.string.usercount_1) + ' ' + new DecimalFormat().format(c) + ' ' + getString(c > 1 ? R.string.usercount_3 : R.string.usercount_2));
	}
	
	private void setTimeCounter(PlayerBuilderData e) {
		long time = new Date().getTime();
		if (getCache().delay == null) getCache().delay = time - e.getTime();
		long now = time - getCache().delay;
		long end = e.getInitTime() + e.getTimeout();
		counter.start((int)((end - now) / 1000));
	}
	
	private PlayerBuilderData getCache() {
		return getModel().getCache(false, false);
	}
	
	private void resetGUI(final boolean removePass) {
		runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				resetEtl(etlUser);
				resetEtl(etlEmail);
				resetEtl(etlPassword);
				resetEtl(etlPasswordAgain);
				prevUser = prevEmail = "";
				userNotFree = emailNotFree = false;
				etlPassword.getEditText().setText("");
				etlPasswordAgain.getEditText().setText("");
				if (removePass) {
					removeVar(PASSWORD);
					removeVar(PASSWORD_AGAIN);
				}
			}
			
		});
	}
	
	private void reset() {
		setTitleProgress(true);
		getModel().validate(new ModelActionListener<Integer>() {
			
			@Override
			public void modelActionPerformed(ModelActionEvent<Integer> e) {
				setTitleProgress(false);
				new IntegerMillModelActivityAdapter(SignUpActivity.this, e) {
					
					@Override
					public void onEvent(int i) {
						resetGUI(true);
					}
					
				};
			}
			
		});
	}
	
	private boolean setPassword(boolean show) {
		String password = etlPassword.getEditText().getText().toString();
		if (InputValidator.isPasswordValid(password)) {
			resetEtl(etlPassword);
			setVar(PASSWORD, password);
			return false;
		}
		else if (show && !password.isEmpty()) {
			setEtlDetails(etlPassword, getString(R.string.password_format));
		}
		return true;
	}
	
	private boolean setPasswordAgain(boolean show) {
		String password = etlPassword.getEditText().getText().toString();
		String password2 = etlPasswordAgain.getEditText().getText().toString();
		boolean valid = InputValidator.isPasswordValid(password2);
		boolean equal = password.equals(password2);
		if (valid && equal) {
			resetEtl(etlPasswordAgain);
			setVar(PASSWORD_AGAIN, password);
			return false;
		}
		else if (show && !password.isEmpty()) {
			if (!valid) setEtlDetails(etlPasswordAgain, getString(R.string.password_format));
			if (!equal) setEtlDetails(etlPasswordAgain, getString(R.string.password_not_match));
		}
		return true;
	}
	
	private void setUser() {
		setUser(null, null);
	}
	
	private void setUser(final Runnable onEvent, final Runnable onWrongEvent) {
		final String user = etlUser.getEditText().getText().toString();
		if (user.equalsIgnoreCase(prevUser) || user.equalsIgnoreCase(getModel().getCache().getUser())) {
			if (onEvent != null) onEvent.run();
			return;
		}
		prevUser = user;
		if (!InputValidator.isUserIdValid(user)) {
			setEtlDetails(etlUser, getText(R.string.user_format));
		}
		else {
			setEtlProgress(etlUser);
			getModel().setUser(user, new ModelActionListener<Integer>() {
				
				@Override
				public void modelActionPerformed(ModelActionEvent<Integer> e) {
					new IntegerMillModelActivityAdapter(SignUpActivity.this, e) {
						
						@Override
						public void onEvent(final int i) {
							userNotFree = false;
							etlUser.setProgress(false);
							switch (getReturn(i)) {
								case OK:
									getCache().setUser(user);
									if (onEvent != null) onEvent.run();
									break;
								case USER_EXISTS:
									userNotFree = true;
									setEtlDetails(etlUser, getText(R.string.user_not_free));
								default:
									if (onWrongEvent != null) onWrongEvent.run();
							}
						}
						
					};
				}
				
			});
		}
	}
	
	private void setEmail() {
		setEmail(null, null);
	}
	
	private void setEmail(final Runnable onEvent, final Runnable onWrongEvent) {
		final String email = etlEmail.getEditText().getText().toString();
		if (email.isEmpty()) resetEtl(etlEmail);
		if (email.equalsIgnoreCase(prevEmail) || email.equalsIgnoreCase(getModel().getCache().getEmail())) {
			if (onEvent != null) onEvent.run();
			return;
		}
		prevEmail = email;
		if (!InputValidator.isEmailValid(email)) {
			setEtlDetails(etlEmail, getText(R.string.email_format));
		}
		else {
			setEtlProgress(etlEmail);
			getModel().setEmail(email, new ModelActionListener<Integer>() {
				
				@Override
				public void modelActionPerformed(ModelActionEvent<Integer> e) {
					new IntegerMillModelActivityAdapter(SignUpActivity.this, e) {
						
						@Override
						public void onEvent(final int i) {
							emailNotFree = false;
							etlEmail.setProgress(false);
							switch (getReturn(i)) {
								case OK:
									getCache().setEmail(email);
									if (onEvent != null) onEvent.run();
									break;
								case EMAIL_EXISTS:
									emailNotFree = true;
									setEtlDetails(etlEmail, getText(R.string.email_not_free));
								default:
									if (onWrongEvent != null) onWrongEvent.run();
							}
						}
						
					};
				}
				
			});
		}
	}
	
	private void createUser() {
		if (userNotFree) setUser();
		if (emailNotFree) setEmail();
		if (userNotFree || emailNotFree) return;
		final String user = etlUser.getEditText().getText().toString();
		String email = etlEmail.getEditText().getText().toString();
		final String password = etlPassword.getEditText().getText().toString();
		String password2 = etlPasswordAgain.getEditText().getText().toString();
		boolean userValid = InputValidator.isUserIdValid(user);
		boolean passValid = InputValidator.isPasswordValid(password);
		boolean pass2Valid = InputValidator.isPasswordValid(password2);
		boolean passEqual = password.equals(password2);
		boolean userPassEqual = password.equals(user) && !user.isEmpty();
		boolean emailValid = InputValidator.isEmailValid(email);
		if (userValid && passValid && passEqual && emailValid && !userPassEqual) {
			setTitleProgress(true);
			
			final Runnable wrongEvent = new Runnable() {
				
				@Override
				public void run() {
					setTitleProgress(false);
				}
				
			};
			
			setUser(new Runnable() {
				
				@Override
				public void run() {
					setEmail(new Runnable() {
						
						@Override
						public void run() {
							final String passhash = InputValidator.md5Hex(password);
							getModel().createUser(passhash, true, new ModelActionListener<Integer>() {
								
								@Override
								public void modelActionPerformed(ModelActionEvent<Integer> e) {
									new IntegerMillModelActivityAdapter(SignUpActivity.this, e) {
										
										@Override
										public void onEvent(int i) {
											setTitleProgress(false);
											switch (getReturn(i)) {
												case OK:
													resetGUI(true);
													getConnectionBinder().getModelMap().free(HomeActivity.class);
													getConnectionBinder().setLoginMode(LoginMode.SIGNED_IN);
													UserInfoSettings s = getConnectionBinder().getDatabaseHelper().getUserInfoSettings();
													UserInfo ui = getConnectionBinder().getUserInfo();
													if (s.isSaveUser()) ui.setUser(user);
													if (s.isSavePassword()) ui.setPassword(password);
													if (s.isSaveUser()) {
														MillDatabaseHelper db = getConnectionBinder().getDatabaseHelper();
														UserInfo newUser = db.findUserInfo(user);
														if (newUser == null) { // ha nincs még tárolva, létrehozás és beállítás
															newUser = new UserInfo();
															newUser.setUser(user);
															newUser.setServer(getConnectionBinder().getDatabaseHelper().getConnectionSettings().getUrl());
														}
														if (s.isSavePassword()) { // ha a jelszót is tároljuk, jelszó beállítás
															newUser.setPassword(password);
														}
														db.store(newUser);
														getConnectionBinder().reinitDatabaseHelper();
													}
													getModelMap().remove(SignUpActivity.class);
													finish();
													break;
												default:
													rebindConnectionService();
											}
										}
										
									};
								}
								
							});
						}
						
					}, wrongEvent);
				}
				
			}, wrongEvent);
			
		}
		else {
			if (!userValid) setEtlDetails(etlUser, getText(R.string.user_format));
			if (!passValid) setEtlDetails(etlPassword, getText(R.string.password_format));
			if (!pass2Valid) setEtlDetails(etlPasswordAgain, getText(R.string.password_format));
			if (!passEqual) setEtlDetails(etlPasswordAgain, getText(R.string.password_not_match));
			if (!emailValid) setEtlDetails(etlEmail, getText(R.string.email_format));
			if (userPassEqual) setEtlDetails(etlPassword, getText(R.string.password_not_user));
		}
	}
	
	
	private void setTitleProgress(final boolean visible) {
		runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				btSignUp.setEnabled(!visible);
				etlUser.getEditText().setEditable(!visible);
				etlPassword.getEditText().setEditable(!visible);
				etlPasswordAgain.getEditText().setEditable(!visible);
				etlEmail.getEditText().setEditable(!visible);
				setProgressBarIndeterminateVisibility(visible);
			}
			
		});
	}
	
	private PlayerBuilderReturn getReturn(int i) {
		return getEnumValue(PlayerBuilderReturn.class, i);
	}
	
}