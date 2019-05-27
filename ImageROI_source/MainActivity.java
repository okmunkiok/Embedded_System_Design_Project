package com.example.imageroi;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
//	import to handle bmp by Matrix
//	import to handle Arrays
//	mainly for checking Arrays' data right


public class MainActivity extends Activity 
{ 
	ImageView imageView1, imageView2; //// ImageView1은 원래 이미지를 보이고 ImageView2는 이미지의 이진화 결과를 보임 
	Button button1, button2; 
	Bitmap bitmap1; //// original image
	Bitmap bitmap2; //// processed image
	Bitmap bitmap3; //// 2nd processed image
	
	@Override protected void onCreate(Bundle savedInstanceState) 
	{ 
		super.onCreate(savedInstanceState); 
		setContentView(R.layout.activity_main); 
		imageView1 = (ImageView)findViewById(R.id.image1); 
		imageView2 = (ImageView)findViewById(R.id.image2);
		button1 = (Button)findViewById(R.id.button1); 
		button2 = (Button)findViewById(R.id.button2);
		
		Button.OnClickListener listener = new Button.OnClickListener(){
			
			@Override
			public void onClick(View v) {
				if(v==button1){
					Intent intent = new Intent(); 
					intent.setType("image/*"); 
					intent.setAction(Intent.ACTION_GET_CONTENT); 
					startActivityForResult(intent, 1);
				}
				else if (v==button2) {
										
					// 이진화 호출입니다
					bitmap2 = grayScale(bitmap1);			//// 흑백 사진(grayscale)로 변경) + 이진화
					
					//// resize, 가로폭이 200 픽셀로 설정
					////bitmap3 = resizeBitmap(bitmap2, 200);        
					
					//// resize, 비율이 1/8로 축소
					bitmap3 = resize_samplesize(bitmap2, 8);
					
					imageView2.setImageBitmap(bitmap3); 	//// resize된 이진화된 이미지를 ImageView에 보임
				
					imageView2.setDrawingCacheEnabled(true);  //화면에 뿌릴때 캐시를 사용하게 한다

					bitmap3 = imageView2.getDrawingCache();   //캐시를 비트맵으로 변환
					
					//// 이진화 이미지 SDcard에 저장
					FileOutputStream outStream = null;
			        String extStorageDirectory = Environment.getExternalStorageDirectory().toString();
		
					File file = new File(extStorageDirectory, "binarized.jpg");
			        try {
			        	outStream = new FileOutputStream(file);
			            bitmap3.compress(Bitmap.CompressFormat.JPEG, 80, outStream);   //// 숫자가 적을 수록 압축률이 높음 JEPG에서 100일때 100~150KB, 80일때 16KB 정도임
			            outStream.flush();
			            outStream.close();
			            
			            //// 브로드캐스팅해서 갤러리에 보이도록 함
		                sendBroadcast(new Intent( Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file)));
		                
		                imageView2.setDrawingCacheEnabled(false);
			 
			            Toast.makeText(getApplicationContext(), "Saved", Toast.LENGTH_LONG).show();
			        } catch (Exception e) {
			            e.printStackTrace();
			            Toast.makeText(getApplicationContext(), "Save Error", Toast.LENGTH_SHORT).show();
			        }
				}
					
			}
		};
	
		button1.setOnClickListener(listener);
		button2.setOnClickListener(listener);
	} 
	
	@Override protected void onActivityResult(int requestCode, int resultCode, Intent data) 
	{ 
		// Check which request we're responding to 
		if (requestCode == 1) { 
			// Make sure the request was successful 
			if (resultCode == RESULT_OK) { 
				try { 
					// 선택한 이미지에서 비트맵 생성 
					InputStream in = getContentResolver().openInputStream(data.getData()); 

					bitmap1 = BitmapFactory.decodeStream(in); //// 이진화 함수에 넘겨주기 위함
					in.close(); 
					// 이미지 표시 
					imageView1.setImageBitmap(bitmap1);
				} catch (Exception e) { 
					e.printStackTrace(); 
				} 
			} 
		} 
	} 
	
	
//	여기는 이진화 작업 (gray-scaling + binarization) 구역입니다
//	gray-scaling
//	참고
//	https://developer.android.com/reference/android/graphics/Bitmap
//	https://developer.android.com/reference/android/graphics/Bitmap.Config
// 	https://developer.android.com/reference/android/graphics/ColorMatrix
// 	https://developer.android.com/reference/android/graphics/ColorFilter
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
        		
        		gray_rgb = ((R << 5) - (R << 1)) + ((G << 6) - (G << 2) - G) + ((B << 3) + (B << 1) + B);
        		
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
