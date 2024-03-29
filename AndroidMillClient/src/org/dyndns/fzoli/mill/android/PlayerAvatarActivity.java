package org.dyndns.fzoli.mill.android;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import org.dyndns.fzoli.http.CountingListener;
import org.dyndns.fzoli.mill.android.activity.AbstractMillOnlineActivity;
import org.dyndns.fzoli.mill.android.activity.IntegerMillModelActivityAdapter;
import org.dyndns.fzoli.mill.android.activity.MillModelActivityAdapter;
import org.dyndns.fzoli.mill.client.model.PlayerAvatarModel;
import org.dyndns.fzoli.mill.common.key.PlayerAvatarReturn;
import org.dyndns.fzoli.mill.common.model.pojo.PlayerAvatarData;
import org.dyndns.fzoli.mill.common.model.pojo.PlayerAvatarEvent;
import org.dyndns.fzoli.mvc.client.connection.Connection;
import org.dyndns.fzoli.mvc.client.event.ModelActionEvent;
import org.dyndns.fzoli.mvc.client.event.ModelActionListener;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Bitmap.CompressFormat;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class PlayerAvatarActivity extends AbstractMillOnlineActivity<PlayerAvatarEvent, PlayerAvatarData> {
	
	//TODO: szerver oldalon a képfeltöltés mégse zárolja a munkamenetet, hogy működjön a kijelentkezés és bejelentkezés közben
	// majd befejezni ezt a szerencsétlen kódot ha nem lesz 40 fok és fogni fog az agyam a metekhoz!!!
	
	private static final int REQ_PICK = 1;
	
	private Button btGallery, btOk;
	private TextView tvAvatar;
	private ImageView ivAvatar;
	private ProgressBar pbAvatar;
	private RelativeLayout rlAvatar;
	
	private float mX = 0, mY = 0;
	private Bitmap bmAvatar, loaded;
	private int size = 0;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTitle(R.string.avatar);
		setContentView(R.layout.avatar);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
		
		btOk = (Button)findViewById(R.id.btOk);
		btGallery = (Button) findViewById(R.id.btGallery);
		tvAvatar = (TextView) findViewById(R.id.tvAvatar);
		ivAvatar = (ImageView) findViewById(R.id.ivAvatar);
		pbAvatar = (ProgressBar) findViewById(R.id.pbAvatar);
		rlAvatar = (RelativeLayout) findViewById(R.id.rlAvatar);
		
		DisplayMetrics dm = getResources().getDisplayMetrics();
		size = Math.min(dm.widthPixels, dm.heightPixels);

		rlAvatar.getLayoutParams().height = size;
		rlAvatar.getLayoutParams().width = size;
		
		ivAvatar.setOnTouchListener(new View.OnTouchListener() {
	    	
			@Override
	        public boolean onTouch(View view, MotionEvent event) {
	            onImageTouch(event);
	            return true;
	        }
	        
	    });
		
		btGallery.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View paramView) {
				openPictureSelect();
			}
			
		});
		
		btOk.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View paramView) {
				onUpload();
			}
			
		});
		
		((Button)findViewById(R.id.btCancel)).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View paramView) {
				finish();
			}
			
		});

	}
	
	@Override
	public PlayerAvatarModel createModel(Connection<Object, Object> connection) {
		return new PlayerAvatarModel(connection);
	}
	
	@Override
	public PlayerAvatarModel getModel() {
		return (PlayerAvatarModel) super.getModel();
	}
	
	@Override
	public boolean processModelData(PlayerAvatarData e) {
		boolean ret = super.processModelData(e);
		if (ret && loaded == null) {
			Bitmap avatar = getConnectionBinder().getAvatarImage();
			if (avatar == null) {
				pbAvatar.setVisibility(View.VISIBLE);
				getModel().getAvatarImage(new ModelActionListener<InputStream>() {
					
					@Override
					public void modelActionPerformed(ModelActionEvent<InputStream> e) {
						new MillModelActivityAdapter<InputStream>(PlayerAvatarActivity.this, e) {
							
							@Override
							public void onEvent(InputStream s) {
								pbAvatar.setVisibility(View.GONE);
								onImageLoad(createBitmap(s), false);
							}
							
						};
					}
				});
			}
			else {
				onImageLoad(avatar, false);
			}
		}
		return ret;
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
			case REQ_PICK:
				if (data != null) onImageLoad(data.getData());
				break;
		}
	}
	
	private void onUpload() { //TODO
		if (isImageEmpty(bmAvatar)) return;
//		btOk.setEnabled(false);
		Bitmap img = getConnectionBinder().getAvatarImage();
		final float zoom = loaded == null ? createScale(img == null ? bmAvatar : img, false) : createScale(loaded, true);
		final int x = (int)(ivAvatar.getScrollX() / zoom);
		final int y = (int)(ivAvatar.getScrollY() / zoom);
		final int scale = loaded == null ? getModel().getCache().getScale() : (int)(size * zoom);
		Log.i("test", "image upload. x: "+x+";y: "+y+";scale: "+scale+";zoom: "+zoom);
		getModel().setAvatarAttrs(x, y, scale, new ModelActionListener<Integer>() {
			
			@Override
			public void modelActionPerformed(ModelActionEvent<Integer> e) {
				new IntegerMillModelActivityAdapter(PlayerAvatarActivity.this, e) {
					
					@Override
					public void onEvent(int e) {
//						btOk.setEnabled(true);
						switch (getReturn(e)) {
							case OK:
								if (loaded != null) {
									getModel().setAvatar(createStream(loaded), new ModelActionListener<Integer>() {
										
										@Override
										public void modelActionPerformed(ModelActionEvent<Integer> e) {
											Log.i("test","uploaded");
										}
										
									}, new CountingListener() {
										
										@Override
										public void onWrite(long length, long completted) {
											Log.i("test","onWrite: "+length + " ; "+completted);
										}
										
									});
								}
								getConnectionBinder().setAvatarImage(null);
								finish();
								break;
						}
					}
					
				};
			}
		});
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
        Log.i("test", "image event. mx: "+mX+";my: "+mY);
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
			onImageLoad(decodeUri(selectedImage), true);
		}
		catch (Exception e) {
			;
		}
	}
	
	private boolean isImageEmpty(Bitmap selectedImage) {
		return selectedImage == null || (selectedImage.getWidth() == 1 && selectedImage.getHeight() == 1);
	}
	
	private void onImageLoad(Bitmap selectedImage, boolean local) {
		if (isImageEmpty(selectedImage)) {
			tvAvatar.setVisibility(View.VISIBLE);
		}
		else {
			tvAvatar.setVisibility(View.GONE);
		}
		if (local) loaded = selectedImage;
		else getConnectionBinder().setAvatarImage(selectedImage);
		bmAvatar = createResizedBitmap(selectedImage, local);
		ivAvatar.setImageBitmap(bmAvatar);
		if (!local && getModel().getCache().getX() != null && getModel().getCache().getY() != null) {
			float scale = createScale(selectedImage, local);	
			mX = getModel().getCache().getX() * scale;
			mY = getModel().getCache().getY() * scale;
			Log.i("test", "image loaded. mx: "+mX+";my: "+mY+";scale: "+scale);
		}
		else {
			mX = (bmAvatar.getWidth() - size) / 2;
			mY = (bmAvatar.getHeight() - size) / 2;
			Log.i("test", "image loaded. mx: "+mX+";my: "+mY);
		}
		ivAvatar.scrollTo((int)mX, (int)mY);
	}
	
	private Bitmap decodeUri(Uri selectedImage) throws FileNotFoundException {
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(getContentResolver().openInputStream(selectedImage), null, o);

        int width_tmp = o.outWidth, height_tmp = o.outHeight;

        int decodeScale = 1;
        while (true) {
            if (width_tmp / 2 < size
               || height_tmp / 2 < size) {
                break;
            }
            width_tmp /= 2;
            height_tmp /= 2;
            decodeScale *= 2;
        }

        BitmapFactory.Options o2 = new BitmapFactory.Options();
        o2.inSampleSize = decodeScale;
        return BitmapFactory.decodeStream(getContentResolver().openInputStream(selectedImage), null, o2);
    }
	
	private float createScale(Bitmap bitmap, boolean local) {
		float ret;
		if (!local && getModel().getCache().getScale() != null) {
			ret = size / getModel().getCache().getScale();
		}
		else {
			float scaleWidth = ((float) size) / bitmap.getWidth();
        	float scaleHeight = ((float) size) / bitmap.getHeight();
        	ret = Math.max(scaleWidth, scaleHeight);
		}
		Log.i("test", "created scale: " + ret + " ("+bitmap+";"+local+")");
		return ret;
	}
	
	private Bitmap createResizedBitmap(Bitmap bitmap, boolean local) {
        float scaleMax = createScale(bitmap, local);
        
        Matrix matrix = new Matrix();
        matrix.postScale(scaleMax, scaleMax);
        
        return Bitmap.createBitmap(bitmap, 0, 0, 
        		bitmap.getWidth(), bitmap.getHeight(), matrix, true);
	}
	
	private void openPictureSelect() {
		Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        i.putExtra("crop", false);
        startActivityForResult(i, REQ_PICK);
	}
	
	private PlayerAvatarReturn getReturn(int i) {
		return getEnumValue(PlayerAvatarReturn.class, i);
	}
	
	public static ByteArrayOutputStream createStream(Bitmap bitmap) {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		bitmap.compress(CompressFormat.PNG, 0 /*ignored for PNG*/, bos);
		return bos;
	}
	
	public static Bitmap createBitmap(InputStream s) {
		return BitmapFactory.decodeStream(s);
	}
	
}