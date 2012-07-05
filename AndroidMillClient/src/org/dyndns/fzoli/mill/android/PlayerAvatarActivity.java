package org.dyndns.fzoli.mill.android;

import java.io.FileNotFoundException;

import org.dyndns.fzoli.mill.android.activity.AbstractMillOnlineActivity;
import org.dyndns.fzoli.mill.client.model.PlayerAvatarModel;
import org.dyndns.fzoli.mill.common.model.pojo.BaseOnlinePojo;
import org.dyndns.fzoli.mill.common.model.pojo.PlayerAvatarData;
import org.dyndns.fzoli.mvc.client.connection.Connection;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class PlayerAvatarActivity extends AbstractMillOnlineActivity<BaseOnlinePojo, PlayerAvatarData> {
	
	private static final int REQ_PICK = 1;
	
	private Button btGallery;
	private ImageView ivAvatar;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTitle(R.string.avatar);
		setContentView(R.layout.avatar);
		
		btGallery = (Button) findViewById(R.id.btGallery);
		ivAvatar = (ImageView) findViewById(R.id.ivAvatar);
		

		DisplayMetrics dm = getResources().getDisplayMetrics();
		
		int size = Math.min(dm.widthPixels, dm.heightPixels) - ((int) dm.density * 10);
		
		ivAvatar.setMaxWidth(size);
		ivAvatar.setMaxHeight(size);
		ivAvatar.setMinimumWidth(size);
		ivAvatar.setMinimumHeight(size);
		
//		RelativeLayout.LayoutParams rl = new RelativeLayout.LayoutParams(size, size);
//		svAvatar.setLayoutParams(rl);
		
		final ImageView switcherView = ivAvatar;
	    switcherView.setOnTouchListener(new View.OnTouchListener() {

	        public boolean onTouch(View arg0, MotionEvent event) {

	        	float mx = 0,my = 0;
	            float curX, curY;

	            switch (event.getAction()) {

	                case MotionEvent.ACTION_DOWN:
	                    mx = event.getX();
	                    my = event.getY();
	                    break;
	                case MotionEvent.ACTION_MOVE:
	                    curX = event.getX();
	                    curY = event.getY();
	                    switcherView.scrollBy((int) (mx - curX), (int) (my - curY));
	                    mx = curX;
	                    my = curY;
	                    break;
	                case MotionEvent.ACTION_UP:
	                    curX = event.getX();
	                    curY = event.getY();
	                    switcherView.scrollBy((int) (mx - curX), (int) (my - curY));
	                    break;
	            }

	            return true;
	        }
	    });
		
		((Button)findViewById(R.id.btCancel)).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View paramView) {
				finish();
			}
			
		});
		
		btGallery.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View paramView) {
				openPictureSelect();
			}
			
		});
		
	}
	
	@Override
	public PlayerAvatarModel createModel(Connection<Object, Object> connection) {
		return new PlayerAvatarModel(connection);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
			case REQ_PICK:
				Log.i("test", "pick " + data);
				try {
					Bitmap bitmap = decodeUri(data.getData());
					Log.i("test", "img: "+bitmap.getHeight()+";"+bitmap.getWidth());
					ivAvatar.setImageBitmap(bitmap);
				} catch (Exception e) {
					Log.i("test","ex",e);
				}
				break;
		}
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

	private void openPictureSelect() {
		Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        i.putExtra("crop", false);
        startActivityForResult(i, REQ_PICK);
	}
	
}