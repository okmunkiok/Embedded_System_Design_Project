package com.example.cameratest;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
////import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import java.io.File;
import android.widget.Toast;

////public class MainActivity extends AppCompatActivity {
public class MainActivity extends Activity {

    private File mFile;
    private ImageView mViewPicture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mViewPicture = (ImageView) findViewById(R.id.imgViewPicture);
    }

    public void mOnClick(View v) {
        switch (v.getId()) {
        case R.id.btnTakePicture:
            File sdcard = Environment.getExternalStorageDirectory();
            mFile = new File(sdcard.getAbsolutePath() + "/test.jpg");
            ////
            ////Toast.makeText(getApplicationContext(), "Write OK", Toast.LENGTH_SHORT).show();
            Toast.makeText(getApplicationContext(), sdcard.getAbsolutePath(), Toast.LENGTH_LONG).show();
            ////            
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mFile));
            //// 저장된 사진을 갤러리에 추가
            sendBroadcast(new Intent( Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(mFile)));
            startActivityForResult(intent, 0);
                       
            break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0 && resultCode == RESULT_OK) {
            int width = mViewPicture.getWidth();
            int height = mViewPicture.getHeight();

            BitmapFactory.Options bmpFactoryOptions = new BitmapFactory.Options();
            bmpFactoryOptions.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(mFile.getAbsolutePath(), bmpFactoryOptions);

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
            Bitmap bmp = BitmapFactory.decodeFile(mFile.getAbsolutePath(), bmpFactoryOptions);
            mViewPicture.setImageBitmap(bmp);
        }
    }
}
