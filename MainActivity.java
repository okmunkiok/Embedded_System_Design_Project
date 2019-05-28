package com.example.test1;

import android.support.v7.app.ActionBarActivity;
import android.app.Activity;  //
import android.graphics.Bitmap;  //
import android.graphics.BitmapFactory; //
import android.hardware.Camera;
import android.os.Build;
import android.os.Bundle;  //
import android.os.Environment;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;  //
import android.widget.ImageView; //
import android.widget.Button; 

import java.io.InputStream;
import java.io.File; //
import java.io.FileOutputStream; //

import android.widget.Toast;  //
import android.content.Intent; //
import android.content.Context;
import android.net.Uri; //

import java.sql.Date; 
import java.text.SimpleDateFormat;

import com.example.test1.R;

import android.graphics.Color;
import android.provider.MediaStore;

public class MainActivity extends ActionBarActivity {

	
	ImageView imgViewPicture, imgViewBinarizedPicture;
	Button btnTakePicture, btnBinarize;
	
	Bitmap bitmap1; //// original image
	Bitmap bitmap2; //// processed image
	Bitmap bitmap3; //// 2nd processed image
	
    private File mFile;
    private ImageView mViewPicture;
    private Camera mCamera;
    private SurfaceView surfPreview;
    
    String picturename = "";
    

    private String dateName(long dateTaken){
        Date date = new Date(dateTaken);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        return dateFormat.format(date);
    }    
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
	    mViewPicture = (ImageView) findViewById(R.id.imgViewPicture);
		imgViewBinarizedPicture = (ImageView)findViewById(R.id.imgViewBinarizedPicture);
		btnTakePicture = (Button)findViewById(R.id.btnTakePicture); 
		btnBinarize = (Button)findViewById(R.id.btnBinarize);

	    surfPreview = (SurfaceView) findViewById(R.id.surfPreview);
	    SurfaceHolder surfHolder = surfPreview.getHolder();
	    surfHolder.addCallback(mPreviewCallback);
		
		Button.OnClickListener listener = new Button.OnClickListener(){
		
			@Override
			public void onClick(View v) {
		        		        
				if (v==btnBinarize) {
					
					bitmap1 = BitmapFactory.decodeFile(mFile.getAbsolutePath());					
					mViewPicture.setImageBitmap(bitmap1);					
					bitmap2 = grayScale(bitmap1);			//// 흑백 사진(grayscale)로 변경) + 이진화     
				
					//// resize, 비율이 1/8로 축소
					bitmap3 = resize_samplesize(bitmap2, 8);				
					imgViewBinarizedPicture.setImageBitmap(bitmap3); 	//// resize된 이진화된 이미지를 ImageView에 보임			
					imgViewBinarizedPicture.setDrawingCacheEnabled(true);  //화면에 뿌릴때 캐시를 사용하게 한다
					bitmap3 = imgViewBinarizedPicture.getDrawingCache();   //캐시를 비트맵으로 변환
				
					//// 이진화 이미지 SDcard에 저장
					FileOutputStream outStream = null;
					String extStorageDirectory = Environment.getExternalStorageDirectory().toString();
	
					File file = new File(extStorageDirectory,  picturename + "binarized.jpg");
					try {
						outStream = new FileOutputStream(file);
						bitmap3.compress(Bitmap.CompressFormat.JPEG, 80, outStream);   //// 숫자가 적을 수록 압축률이 높음 JEPG에서 100일때 100~150KB, 80일때 16KB 정도임
						outStream.flush();
						outStream.close();
		            
						//// 브로드캐스팅해서 갤러리에 보이도록 함
			     		sendBroadcast(new Intent( Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file)));
	                
						imgViewBinarizedPicture.setDrawingCacheEnabled(false);
		 
						Toast.makeText(getApplicationContext(), "Saved", Toast.LENGTH_LONG).show();
					} catch (Exception e) {
						e.printStackTrace();
						Toast.makeText(getApplicationContext(), "Save Error", Toast.LENGTH_SHORT).show();
					}
				}
				else if(v == btnTakePicture){
					
			        File sdcard = Environment.getExternalStorageDirectory();
			        String date = dateName(System.currentTimeMillis());
			        picturename = date;
			        mFile = new File(sdcard.getAbsolutePath() + "/" +  date +".jpg");
			        
			        Toast.makeText(getApplicationContext(), sdcard.getAbsolutePath() + "/" + date, Toast.LENGTH_LONG).show();
			        
					mCamera.autoFocus(new Camera.AutoFocusCallback() {
						@Override
						public void onAutoFocus(boolean success, Camera camera) {
							if (success) {
								mCamera.takePicture(null, null, mPictureCallback);
							}
						}
					});					
				}
			}		
		};

		btnTakePicture.setOnClickListener(listener);
		btnBinarize.setOnClickListener(listener);
	} 

    private SurfaceHolder.Callback mPreviewCallback = new SurfaceHolder.Callback() {

        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            try {
                mCamera = Camera.open(0); // 0: 占쎄쑬��燁삳�李볩옙占�: 占쎄쑬��燁삳�李볩옙占�
                if (Build.MODEL.equals("Nexus 5X")) { // 占싸쇨퐣占쏙옙5x 占쎈뜆��筌ｌ꼶��
                    Camera.Parameters parameters = mCamera.getParameters();
                    parameters.setRotation(180);
                    mCamera.setParameters(parameters);
                }
                mCamera.setPreviewDisplay(holder);
                mCamera.startPreview();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            try {
                mCamera.stopPreview();
                mCamera.setPreviewDisplay(holder);
                mCamera.startPreview();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            mCamera.stopPreview();
            mCamera.release();
        }
    };
    
    private Camera.PictureCallback mPictureCallback = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            try {
            	
               FileOutputStream fos = new FileOutputStream(mFile);
               fos.write(data);
               fos.flush();
               fos.close();
                
                //// �됰슢以덌옙�뽱떐占썬끋�울옙�곴퐣 揶쎼끇��뵳�肉�癰귣똻�좑옙袁⑥쨯 占쏙옙
               sendBroadcast(new Intent( Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(mFile)));
                                
               viewPicture(mFile);
               mCamera.startPreview();
                
                Toast.makeText(getApplicationContext(), "Write OK", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
            	Toast.makeText(getApplicationContext(), "Write Error", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }
    };      

    private void viewPicture(File file) {
        int width = mViewPicture.getWidth();
        int height = mViewPicture.getHeight();

        BitmapFactory.Options bmpFactoryOptions = new BitmapFactory.Options();
        bmpFactoryOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(file.getAbsolutePath(), bmpFactoryOptions);

        int widthRatio = (int) Math.ceil(bmpFactoryOptions.outWidth / (float) width);
        int heightRatio = (int) Math.ceil(bmpFactoryOptions.outHeight / (float) height);
        if (heightRatio > 1 || widthRatio > 1) {
            if (heightRatio > widthRatio) {
                bmpFactoryOptions.inSampleSize = heightRatio;
            } else {
                bmpFactoryOptions.inSampleSize = widthRatio;
            }
        }

        bmpFactoryOptions.inJustDecodeBounds = false;
        Bitmap bmp = BitmapFactory.decodeFile(file.getAbsolutePath(), bmpFactoryOptions);
        mViewPicture.setImageBitmap(bmp);
    }  

	private Bitmap grayScale(final Bitmap orgBitmap){
		int threshold = 30 * 100;
		
        int width = orgBitmap.getWidth();
        int height = orgBitmap.getHeight();
        
        int [] pixels = new int [width * height];
        orgBitmap.getPixels(pixels, 0, width, 0, 0, width, height);
 
        Bitmap bmpGrayScale = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_4444);
 
        // color information
        int A, R, G, B;
        int index;
        int gray_rgb;
        
        for(int y = height - 1; y >= 0; y--){
        	for(int x = width - 1; x >= 0; x--){
        		index = y * width + x;
        		int temp_pixel = pixels[index];
        		R = (temp_pixel >> 16) & 0xff;
        		G = (temp_pixel >> 8) & 0xff;
        		B = (temp_pixel) & 0xff;
//        		A = (pixels[index] >> 24) & 0xff;
        		
        		gray_rgb = ((R << 5) - R - R) + ((G << 6) - G - G - G - G - G) + ((B << 3) + B + B + B);
        		
        		if(gray_rgb > threshold)
        			pixels[index] = temp_pixel | 0xffffff;
        		else
        			pixels[index] = temp_pixel | 0x000000;
        	}
        }
        bmpGrayScale.setPixels(pixels, 0, width, 0, 0, width, height);
        return bmpGrayScale;
 
    }
	
//	여기까지 이진화 작업 구역입니다
	
//	private 
	
	

	//// 이미지 resize with 가로폭 픽셀, 출처: https://dwfox.tistory.com/37  
//	private Bitmap resizeBitmap(Bitmap original, int resizeWidth) {
//		 
//	    ////int resizeWidth = 200;
//	 
//	    double aspectRatio = (double) original.getHeight() / (double) original.getWidth();
//	    int targetHeight = (int) (resizeWidth * aspectRatio);
//	    Bitmap result = Bitmap.createScaledBitmap(original, resizeWidth, targetHeight, false);
//	    if (result != original) {
//	        original.recycle();
//	    }
//	    return result;
//	}
	
	//// 이미지 resize by ratio, 출처: https://it77.tistory.com/99
	private Bitmap resize_samplesize (Bitmap original, int samplesize) {

		Bitmap resized = Bitmap.createScaledBitmap(original, (int)((double) original.getWidth()/samplesize), (int)((double) original.getHeight()/samplesize), true);
		
		return resized;
	}
	

}
