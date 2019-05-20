package com.example.imageroi;

import android.app.Activity;
import android.content.Intent; 
import android.graphics.Bitmap; 
import android.graphics.BitmapFactory; 
import android.net.Uri;
import android.os.Bundle; 
import android.view.View; 
import android.widget.Button; 
import android.widget.ImageView; 
import android.widget.Toast;

import java.io.InputStream;
import java.io.File;
import java.io.FileOutputStream;

import android.graphics.Color;
import android.provider.MediaStore;
import android.os.Environment;

import android.content.Context;

public class MainActivity extends Activity 
{ 
	ImageView imageView1, imageView2; //// ImageView1�� ���� �̹����� ���̰� ImageView2�� �̹����� ����ȭ ����� ���� 
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
					
					bitmap2 = grayScale(bitmap1);			//// ��� ����(grayscale)�� ����) + ����ȭ
					
					//// resize, �������� 200 �ȼ��� ����
					////bitmap3 = resizeBitmap(bitmap2, 200);        
					
					//// resize, ������ 1/8�� ���
					bitmap3 = resize_samplesize(bitmap2, 8);
					
					imageView2.setImageBitmap(bitmap3); 	//// resize�� ����ȭ�� �̹����� ImageView�� ����
				
					imageView2.setDrawingCacheEnabled(true);  //ȭ�鿡 �Ѹ��� ĳ�ø� ����ϰ� �Ѵ�

					bitmap3 = imageView2.getDrawingCache();   //ĳ�ø� ��Ʈ������ ��ȯ
					
					//// ����ȭ �̹��� SDcard�� ����
					FileOutputStream outStream = null;
			        String extStorageDirectory = Environment.getExternalStorageDirectory().toString();
		
					File file = new File(extStorageDirectory, "binarized.jpg");
			        try {
			        	outStream = new FileOutputStream(file);
			            bitmap3.compress(Bitmap.CompressFormat.JPEG, 80, outStream);   //// ���ڰ� ���� ���� ������� ���� JEPG���� 100�϶� 100~150KB, 80�϶� 16KB ������
			            outStream.flush();
			            outStream.close();
			            
			            //// ��ε�ĳ�����ؼ� �������� ���̵��� ��
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
					// ������ �̹������� ��Ʈ�� ���� 
					InputStream in = getContentResolver().openInputStream(data.getData()); 

					bitmap1 = BitmapFactory.decodeStream(in); //// ����ȭ �Լ��� �Ѱ��ֱ� ����
					in.close(); 
					// �̹��� ǥ�� 
					imageView1.setImageBitmap(bitmap1);
				} catch (Exception e) { 
					e.printStackTrace(); 
				} 
			} 
		} 
	} 
	
	//// ��� (grayscale) �̹����� ����� �ڵ���, ��ó: https://devms.tistory.com/37
	private Bitmap grayScale(final Bitmap orgBitmap){
        int width, height;
        width = orgBitmap.getWidth();
        height = orgBitmap.getHeight();
 
        Bitmap bmpGrayScale = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_4444);
 
        // color information
        int A, R, G, B;
        int pixel;
 
        // scan through all pixels
        for (int x = 0; x < width; ++x) {
            for (int y = 0; y < height; ++y) {
                // get pixel color
                pixel = orgBitmap.getPixel(x, y);
                A = Color.alpha(pixel);
                R = Color.red(pixel);
                G = Color.green(pixel);
                B = Color.blue(pixel);
                int gray = (int) (0.2989 * R + 0.5870 * G + 0.1140 * B);
 
                
                //// ����ȭ�� ����� �ڵ���
                // use 128 as threshold, above -> white, below -> black
                ////
                if (gray > 128)
                    gray = 255;
                else
                    gray = 0;
                ////
                // set new pixel color to output bitmap
                bmpGrayScale.setPixel(x, y, Color.argb(A, gray, gray, gray));
            }
        }
        return bmpGrayScale;
    }

	//// �̹��� resize with ������ �ȼ�, ��ó: https://dwfox.tistory.com/37  
	private Bitmap resizeBitmap(Bitmap original, int resizeWidth) {
		 
	    ////int resizeWidth = 200;
	 
	    double aspectRatio = (double) original.getHeight() / (double) original.getWidth();
	    int targetHeight = (int) (resizeWidth * aspectRatio);
	    Bitmap result = Bitmap.createScaledBitmap(original, resizeWidth, targetHeight, false);
	    if (result != original) {
	        original.recycle();
	    }
	    return result;
	}
	
	//// �̹��� resize by ratio, ��ó: https://it77.tistory.com/99
	private Bitmap resize_samplesize (Bitmap original, int samplesize) {

		Bitmap resized = Bitmap.createScaledBitmap(original, (int)((double) original.getWidth()/samplesize), (int)((double) original.getHeight()/samplesize), true);
		
		return resized;
	}
	

}