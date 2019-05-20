package com.example.cameraapitest;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
////import android.support.v7.app.AppCompatActivity;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;

import java.io.File;
import java.io.FileOutputStream;

import android.widget.Toast;

import android.content.Intent;

import android.net.Uri;

import java.sql.Date; 
import java.text.SimpleDateFormat;

@SuppressWarnings("deprecation")
////public class MainActivity extends AppCompatActivity {
public class MainActivity extends Activity {

    private File mFile;
    private ImageView mViewPicture;
    private Camera mCamera;
    private SurfaceView surfPreview;

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

        ////SurfaceView surfPreview = (SurfaceView) findViewById(R.id.surfPreview);
        surfPreview = (SurfaceView) findViewById(R.id.surfPreview);
        SurfaceHolder surfHolder = surfPreview.getHolder();
        surfHolder.addCallback(mPreviewCallback);
    }

    private SurfaceHolder.Callback mPreviewCallback = new SurfaceHolder.Callback() {

        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            try {
                mCamera = Camera.open(0); // 0: 후면 카메라 1: 전면 카메라
                if (Build.MODEL.equals("Nexus 5X")) { // 넥서스 5x 예외 처리
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

    public void mOnClick(View v) {
    	
        /* 이미지 파일을 외부 저장장치의 기본 디렉토리에 저장하는 방법임. 안드로이드 폴더에서 보이지 않으며 adb shell로 들어가면 /storage/emulated/0에서 확인 가능 */ 
        File sdcard = Environment.getExternalStorageDirectory();
        ////File sdcard = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);  ////Environment.DIRECTORY_DCIM : 카메라로 촬영한 사진 저장 (/mnt/sdcard/DCIM)
        ////mFile = new File(sdcard.getAbsolutePath() + "/picture2.jpg");
        
        //// 현재 연도 날짜 시간의 포맷으로 이미지 파일 이름을 저장함
        String date = dateName(System.currentTimeMillis());
        mFile = new File(sdcard.getAbsolutePath() + "/" +  date +".jpg");
        
        Toast.makeText(getApplicationContext(), sdcard.getAbsolutePath() + "/" + date, Toast.LENGTH_LONG).show();
        
        //// for debugging
        ////String message = sdcard.getAbsolutePath().toString();
        ////Toast.makeText(getApplicationContext(), "SD path:" + message, Toast.LENGTH_LONG).show();
        ////
        
        switch (v.getId()) {
        case R.id.btnTakePicture:
            mCamera.autoFocus(new Camera.AutoFocusCallback() {
                @Override
                public void onAutoFocus(boolean success, Camera camera) {
                    if (success) {
                        mCamera.takePicture(null, null, mPictureCallback);
                    }
                }
            });
            break;
        }
    }

    private Camera.PictureCallback mPictureCallback = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            try {
            	
                FileOutputStream fos = new FileOutputStream(mFile);
                fos.write(data);
                fos.flush();
                fos.close();
                
                //// 브로드캐스팅해서 갤러리에 보이도록 함
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
}
