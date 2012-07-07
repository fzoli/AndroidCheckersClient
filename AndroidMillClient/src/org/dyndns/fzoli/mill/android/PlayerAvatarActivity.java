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
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class PlayerAvatarActivity extends AbstractMillOnlineActivity<BaseOnlinePojo, PlayerAvatarData> {
	
	private static final int REQ_PICK = 1;
	
	private Button btGallery;
	private ImageView ivAvatar;
	private RelativeLayout rlAvatar;
	
	private float mX = 0, mY = 0;
	private Bitmap bmAvatar;
	private int size = 0;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTitle(R.string.avatar);
		setContentView(R.layout.avatar);
		
		btGallery = (Button) findViewById(R.id.btGallery);
		ivAvatar = (ImageView) findViewById(R.id.ivAvatar);
		rlAvatar = (RelativeLayout) findViewById(R.id.rlAvatar);
		
		DisplayMetrics dm = getResources().getDisplayMetrics();
		size = Math.min(dm.widthPixels, dm.heightPixels);
		Log.i("test", "rectangle size: "+size);
		rlAvatar.getLayoutParams().height = size;
		rlAvatar.getLayoutParams().width = size;
		
		ivAvatar.setOnTouchListener(new View.OnTouchListener() {
	    	
			@Override
	        public boolean onTouch(View view, MotionEvent event) {
	            onImageTouch(event);
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
		switch (requestCode) {
			case REQ_PICK:
				onImageLoad(data.getData());
				break;
		}
	}
	
	private void onImageTouch(MotionEvent event) {
		float curX, curY;
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mX = event.getX();
                mY = event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                curX = event.getX();
                curY = event.getY();
                ivAvatar.scrollBy((int) (mX - curX), (int) (mY - curY));
                limitScroll();
                mX = curX;
                mY = curY;
                break;
            case MotionEvent.ACTION_UP:
                curX = event.getX();
                curY = event.getY();
                ivAvatar.scrollBy((int) (mX - curX), (int) (mY - curY));
                limitScroll();
                break;
        }
	}
	
	private void limitScroll() {
		if (bmAvatar == null || ivAvatar == null) return;
		int maxX = bmAvatar.getWidth() - size;
		int maxY = bmAvatar.getHeight() - size;
		if (ivAvatar.getScrollX() < 0) ivAvatar.scrollTo(0, ivAvatar.getScrollY()); //bal szegély
		if (ivAvatar.getScrollX() > maxX) ivAvatar.scrollTo(maxX, ivAvatar.getScrollY()); //jobb szegély
		if (ivAvatar.getScrollY() < 0) ivAvatar.scrollTo(ivAvatar.getScrollX(), 0); //felső szegély
		if (ivAvatar.getScrollY() > maxY) ivAvatar.scrollTo(ivAvatar.getScrollX(), maxY); //alsó szegély
	}
	
	private void onImageLoad(Uri selectedImage) {
		try {
			bmAvatar = decodeUri(selectedImage);
			ivAvatar.setImageBitmap(bmAvatar);
			Log.i("test", "load image "+bmAvatar.getWidth()+" x "+bmAvatar.getHeight());
			mX = mY = 0;
			ivAvatar.scrollTo(0, 0);
		}
		catch (Exception e) {
			;
		}
	}
	
	private Bitmap decodeUri(Uri selectedImage) throws FileNotFoundException {
		
        // Decode image size
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(getContentResolver().openInputStream(selectedImage), null, o);

        // Find the correct scale value. It should be the power of 2.
        int width_tmp = o.outWidth, height_tmp = o.outHeight;

        int scale = 1;
        while (true) {
            if (width_tmp / 2 < size
               || height_tmp / 2 < size) {
                break;
            }
            width_tmp /= 2;
            height_tmp /= 2;
            scale *= 2;
        }

        // Decode with inSampleSize
        BitmapFactory.Options o2 = new BitmapFactory.Options();
        o2.inSampleSize = scale;
        Bitmap minimizedBitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(selectedImage), null, o2);
        
        width_tmp = minimizedBitmap.getWidth();
        height_tmp = minimizedBitmap.getHeight();
        
        float scaleWidth = ((float) size) / width_tmp;
        float scaleHeight = ((float) size) / height_tmp;
        float scaleMax = Math.max(scaleWidth, scaleHeight);
        
        // create matrix for the manipulation
        Matrix matrix = new Matrix();
        // resize the bit map
        matrix.postScale(scaleMax, scaleMax);
        
        return Bitmap.createBitmap(minimizedBitmap, 0, 0, 
        		width_tmp, height_tmp, matrix, true); 

    }

	private void openPictureSelect() {
		Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        i.putExtra("crop", false);
        startActivityForResult(i, REQ_PICK);
	}
	
}