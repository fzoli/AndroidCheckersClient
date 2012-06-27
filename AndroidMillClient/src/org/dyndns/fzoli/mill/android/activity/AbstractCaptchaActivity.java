package org.dyndns.fzoli.mill.android.activity;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.dyndns.fzoli.android.widget.TextWatcherAdapter;
import org.dyndns.fzoli.mill.android.R;
import org.dyndns.fzoli.mill.android.SignUpActivity;
import org.dyndns.fzoli.mill.client.model.AbstractMillModel;
import org.dyndns.fzoli.mill.common.InputValidator;
import org.dyndns.fzoli.mill.common.model.pojo.BaseCaptchaPojo;
import org.dyndns.fzoli.mvc.client.android.activity.ModelBundleActivity;
import org.dyndns.fzoli.mvc.client.connection.Connection;
import org.dyndns.fzoli.mvc.client.event.ModelActionEvent;
import org.dyndns.fzoli.mvc.client.event.ModelActionListener;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.Editable;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public abstract class AbstractCaptchaActivity<EventObj, PropsObj extends BaseCaptchaPojo> extends ModelBundleActivity<EventObj, PropsObj> {

	private ImageView ivCaptcha, warnCaptcha;
	private EditText etCaptcha;
	private Button btOk;
	
	@Override
	public abstract AbstractMillModel<EventObj, PropsObj> createModel(Connection<Object, Object> connection);
	
	@Override
	public AbstractMillModel<EventObj, PropsObj> getModel() {
		return (AbstractMillModel<EventObj, PropsObj>) super.getModel();
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.captcha);
		warnCaptcha = (ImageView) findViewById(R.id.warnCaptcha);
		ivCaptcha = (ImageView) findViewById(R.id.ivCaptcha);
		etCaptcha = (EditText) findViewById(R.id.etCaptcha);
		btOk = (Button) findViewById(R.id.btOk);
		
		MillModelActivityUtil.addLengthFilter(etCaptcha, 6);
		etCaptcha.addTextChangedListener(new TextWatcherAdapter() {
			
			@Override
			public void afterTextChanged(Editable paramEditable) {
				warnCaptcha.setVisibility(View.INVISIBLE);
			}
			
		});
		
		etCaptcha.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				return sendCaptcha();
			}
			
		});
		
		btOk.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				sendCaptcha();
			}
			
		});
		
		beforeAction();
	}
	
	private boolean sendCaptcha() {
		String s = etCaptcha.getText().toString().trim();
		etCaptcha.setText(s);
		if (!InputValidator.isCaptchaValid(s)) {
			warnCaptcha.setVisibility(View.VISIBLE);
			return true;
		}
		beforeAction();
		getModel().validateCaptcha(s, new ModelActionListener<Integer>() {
			
			@Override
			public void modelActionPerformed(ModelActionEvent<Integer> e) {
				if (e.getType() == TYPE_EVENT) {
					new IntegerMillModelActivityAdapter(AbstractCaptchaActivity.this, e) {
						
						@Override
						public void onEvent(int i) {
							if (i != 0) {
								drawCaptcha(getModel().getCaptcha(false));
								onCaptchaValidate(false);
							}
							else {
								onCaptchaValidate(true);
							}
						}
						
					};
				}
			}
			
		});
		return false;
	}
	
	private void beforeAction() {
		btOk.setEnabled(false);
		setProgressBarIndeterminateVisibility(true);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.captcha, menu);
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch (item.getItemId()) {
		case R.id.entryNewImg:
			beforeAction();
			getModel().getCaptcha(true, new ModelActionListener<InputStream>() {
				
				@Override
				public void modelActionPerformed(ModelActionEvent<InputStream> e) {
					new MillModelActivityAdapter<InputStream>(AbstractCaptchaActivity.this, e) {
						
						@Override
						public void onEvent(InputStream e) {
							drawCaptcha(e);
						}
						
					};
					
				}
				
			});
			break;
		}
		return super.onMenuItemSelected(featureId, item);
	}
	
	@Override
	public boolean onPrepareModelCreate(ModelActionEvent<PropsObj> e) {
		if (e.getType() == TYPE_EVENT) {
			PropsObj evt = e.getEvent();
			if (getModel().hasCaptchaInfo(evt) && getModel().isCaptchaValidated(evt)) {
				finish();
				return false;
			}
			final int size = createWidth();
			runOnUiThread(new Runnable() {
				
				@Override
				public void run() {
					etCaptcha.setWidth(size);
					btOk.setWidth(size);
				}
			});
			setCaptchaSize(evt, size);
			drawCaptcha(getModel().getCaptcha(false));
		}
		return super.onPrepareModelCreate(e);
	}
	
	private int createWidth() {
		DisplayMetrics tmp = getResources().getDisplayMetrics();
		return Math.min(tmp.widthPixels, tmp.heightPixels) - (int)(tmp.density * 10);
	}
	
	private void setCaptchaSize(PropsObj e, int size) {
		if (getModel().hasCaptchaInfo(e) && getModel().getCaptchaWidth(e) != size) {
			if (getModel().setCaptchaSize(size) == 0) getModel().getCache().setCaptchaWidth(size);
		}
	}
	
	private void drawCaptcha(final InputStream captchaStream) {
		try {
			final BufferedInputStream buffer = new BufferedInputStream(captchaStream);
			final Bitmap captchaBitmap = BitmapFactory.decodeStream(buffer);
			runOnUiThread(new Runnable() {
				
				@Override
				public void run() {
					etCaptcha.setText("");
					btOk.setEnabled(true);
					setProgressBarIndeterminateVisibility(false);
					try {
						ivCaptcha.setImageBitmap(captchaBitmap);
						if (buffer != null) buffer.close();
						if (captchaStream != null) captchaStream.close();
					}
					catch (IOException e) {
						Toast.makeText(AbstractCaptchaActivity.this, "Captcha download error.", Toast.LENGTH_LONG).show();
					}
				}
				
			});
			
		}
		catch (Exception ex) {}
	}
	
	private int loadCounter = 0;
	
	public boolean processModelData(PropsObj e) {
		loadCounter++;
		if (loadCounter > 1 && !e.isCaptchaValidated()) { // if server restart
			getModelMap().remove(SignUpActivity.class); // remove model
			finish(); // close activity to reopening it with new model
			return false;
		}
		return super.processModelData(e);
	};
	
	protected void onCaptchaValidate(boolean ok) {
		if (ok) {
			getModel().getCache().setCaptchaValidated(true);
			finish();
		}
		else {
			float d = getResources().getDisplayMetrics().density;
			Toast t = Toast.makeText(this, R.string.captcha_failed, Toast.LENGTH_SHORT);
			t.setGravity(Gravity.CENTER, 0, (int)(42*d));
			t.show();
		}
	}
	
}