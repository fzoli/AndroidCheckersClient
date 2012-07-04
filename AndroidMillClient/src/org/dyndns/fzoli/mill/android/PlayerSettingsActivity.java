package org.dyndns.fzoli.mill.android;

import java.io.FileNotFoundException;

import org.dyndns.fzoli.mill.android.activity.AbstractMillOnlineBundlePreferenceActivity;
import org.dyndns.fzoli.mill.client.model.PlayerModel;
import org.dyndns.fzoli.mill.common.model.pojo.PlayerData;
import org.dyndns.fzoli.mill.common.model.pojo.PlayerEvent;
import org.dyndns.fzoli.mvc.client.android.activity.ConnectionActivity;
import org.dyndns.fzoli.mvc.client.connection.Connection;
import org.dyndns.fzoli.mvc.client.model.CachedModel;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceScreen;
import android.util.Log;
import android.view.Display;

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
	
	private static final int REQ_PICK = 1;
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
			case REQ_PICK:
				Log.i("test", "pick " + data);
				try {
					Bitmap bitmap = decodeUri(data.getData());
					Log.i("test", "img: "+bitmap.getHeight()+";"+bitmap.getWidth());
				} catch (Exception e) {
					Log.i("test","ex",e);
				}
				break;
		}
	}
	
	private void openPictureSelect() {
		Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        i.putExtra("crop", false);
        startActivityForResult(i, REQ_PICK);
	}
	
	private Bitmap decodeUri(Uri selectedImage) throws FileNotFoundException {
		
        // Decode image size
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(getContentResolver().openInputStream(selectedImage), null, o);

        // The new size we want to scale to
        Display d = getWindowManager().getDefaultDisplay();
        final int REQUIRED_SIZE = Math.max(d.getHeight(), d.getWidth());

        // Find the correct scale value. It should be the power of 2.
        int width_tmp = o.outWidth, height_tmp = o.outHeight;
        int scale = 1;
        while (true) {
            if (width_tmp / 2 < REQUIRED_SIZE
               || height_tmp / 2 < REQUIRED_SIZE) {
                break;
            }
            width_tmp /= 2;
            height_tmp /= 2;
            scale *= 2;
        }

        // Decode with inSampleSize
        BitmapFactory.Options o2 = new BitmapFactory.Options();
        o2.inSampleSize = scale;
        return BitmapFactory.decodeStream(getContentResolver().openInputStream(selectedImage), null, o2);

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
				openPictureSelect();
				return true;
			}
			
		});
		optionsPref.addPreference(avatarPref);
	}
	
}