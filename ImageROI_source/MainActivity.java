package com.hbe.imageroi;
// Android Studio

//package com.example.imageroi;
// Eclipse


import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import android.content.pm.PackageManager;
import android.widget.TextView;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import java.lang.Math;


public class MainActivity extends Activity
{
    ImageView imageView1, imageView2; //// ImageView1은 원래 이미지를 보이고 ImageView2는 이미지의 이진화 결과를 보임
    ImageView [] imageView_each_letters = new ImageView [12];
    Button button1, button2, button4;
    Bitmap bitmap1; //// original image
    Bitmap bitmap_temp;
    Bitmap bitmap_temp_2;
    Bitmap [] each_character_bitmap_array = new Bitmap [12];
    int each_character_bitmap_array_index = 0;

    int filter_width = 0;

    Bitmap bitmap_hit_or_miss_transformed;

    Bitmap bitmap_resized;


    Bitmap bitmap2; //// for detecting and determining area
    Bitmap bitmap3; ////
    Bitmap bitmap_for_actual_processing; // for actual processing

    int height_index_of_number_area = 0;
    int afford_of_height_index_of_number_area = 4;
    int height_of_number_area = 0;
    int x_min = 99999999;
    int y_min = 99999999;
    int x_max = 0;
    int y_max = 0;
    int is_alphabet_combined_or_not = 0;
    int [] index_of_letter_and_number = new int [12];

    int test_index = 0;

    TextView [] text_view_whether_letter_or_number_or_noise = new TextView [12];

    @Override protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageView1 = (ImageView)findViewById(R.id.image1);
        imageView2 = (ImageView)findViewById(R.id.image2);
        imageView_each_letters[0] = (ImageView) findViewById (R.id.ImageView01);
        imageView_each_letters[1] = (ImageView) findViewById (R.id.ImageView02);
        imageView_each_letters[2] = (ImageView) findViewById (R.id.ImageView03);
        imageView_each_letters[3] = (ImageView) findViewById (R.id.ImageView04);
        imageView_each_letters[4] = (ImageView) findViewById (R.id.ImageView05);
        imageView_each_letters[5] = (ImageView) findViewById (R.id.ImageView06);
        imageView_each_letters[6] = (ImageView) findViewById (R.id.ImageView07);
        imageView_each_letters[7] = (ImageView) findViewById (R.id.ImageView08);
        imageView_each_letters[8] = (ImageView) findViewById (R.id.ImageView09);
        imageView_each_letters[9] = (ImageView) findViewById (R.id.ImageView10);
        imageView_each_letters[10] = (ImageView) findViewById (R.id.ImageView11);
        imageView_each_letters[11] = (ImageView) findViewById (R.id.ImageView12);
        button1 = (Button)findViewById(R.id.button1);
//        button for choose image from gallery
        button2 = (Button)findViewById(R.id.button2);
//        button for actual activation
        button4 = (Button)findViewById(R.id.button4);
//        button re-starting app for easy testing


        text_view_whether_letter_or_number_or_noise[0] =  (TextView) findViewById (R.id.textView1);
        text_view_whether_letter_or_number_or_noise[1] =  (TextView) findViewById (R.id.textView2);
        text_view_whether_letter_or_number_or_noise[2] =  (TextView) findViewById (R.id.textView3);
        text_view_whether_letter_or_number_or_noise[3] =  (TextView) findViewById (R.id.textView4);
        text_view_whether_letter_or_number_or_noise[4] =  (TextView) findViewById (R.id.textView5);
        text_view_whether_letter_or_number_or_noise[5] =  (TextView) findViewById (R.id.textView6);
        text_view_whether_letter_or_number_or_noise[6] =  (TextView) findViewById (R.id.textView7);
        text_view_whether_letter_or_number_or_noise[7] =  (TextView) findViewById (R.id.textView8);
        text_view_whether_letter_or_number_or_noise[8] =  (TextView) findViewById (R.id.textView9);
        text_view_whether_letter_or_number_or_noise[9] =  (TextView) findViewById (R.id.textView10);
        text_view_whether_letter_or_number_or_noise[10] =  (TextView) findViewById (R.id.textView11);
        text_view_whether_letter_or_number_or_noise[11] =  (TextView) findViewById (R.id.textView12);
//        text_view_whether_letter_or_number_or_noise[0].setText("sdf");

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
                    bitmap2 = resize_samplesize(bitmap1, 3);
                    bitmap_temp = resize_samplesize(bitmap1, 3);

                    bitmap2 = gray_scaling(bitmap2);
                    bitmap2 = binarization(bitmap2);
                    filter_width = 3;
                    bitmap2 = dilation(bitmap2, filter_width);
                    filter_width = 3;
                    bitmap2 = gaussian_filtering(bitmap2, filter_width);
                    bitmap2 = binarization(bitmap2);
                    filter_width = 3;
                    bitmap2 = dilation(bitmap2, filter_width);
                    bitmap2 = detect_the_number_area(bitmap2);
                    bitmap_temp = gray_scaling(bitmap_temp);
                    bitmap_temp = binarization(bitmap_temp);
                    filter_width = 5;
                    bitmap_temp = gaussian_filtering(bitmap_temp, filter_width);
                    bitmap_temp = binarization(bitmap_temp);
//                    filter_width = 5;
//                    bitmap_temp = dilation(bitmap_temp, filter_width);
//					filter_width = 3;
//					bitmap_temp = erosion(bitmap_temp, filter_width);

                    bitmap_temp = get_image_of_height_number_area(bitmap_temp);
                    bitmap_temp = get_rid_of_border_whose_width_is_1_pixel(bitmap_temp);

                    find_letter_candidate_from_left_to_right(bitmap_temp);
                    judge_candidate_by_width(bitmap_temp);
//                    distinguish_numbers(bitmap_temp);

//                    for(int i = 0; index_of_letter_and_number[i] != 0; i++)
//                        distinguish_numbers(each_character_bitmap_array[index_of_letter_and_number[i]], index_of_letter_and_number[i]);

                    if(is_alphabet_combined_or_not == 0){
                        for(int i = 0; index_of_letter_and_number[i] != 0; i++){
                            if(i == 2){
                                continue;
                            }
                            else if(i == 3){
                                distinguish_vowels(each_character_bitmap_array[index_of_letter_and_number[i]], index_of_letter_and_number[i]);
                                continue;
                            }
                            distinguish_numbers(each_character_bitmap_array[index_of_letter_and_number[i]], index_of_letter_and_number[i]);
                        }
                    }
                    else if(is_alphabet_combined_or_not == 1){
                        for(int i = 0; index_of_letter_and_number[i] != 0; i++){
                            if(i == 2){

                                continue;
                            }
                            distinguish_numbers(each_character_bitmap_array[index_of_letter_and_number[i]], index_of_letter_and_number[i]);
                        }
                    }

                    imageView2.setImageBitmap(bitmap_temp);

                    for(int i = 0; i < imageView_each_letters.length; i++)
                        imageView_each_letters[i].setImageBitmap(each_character_bitmap_array[i]);

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
                else if(v == button4)
                    restart();

            }
        };

        button1.setOnClickListener(listener);
        button2.setOnClickListener(listener);
        button4.setOnClickListener(listener);
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



    //// 이미지 resize by ratio, 출처: https://it77.tistory.com/99
    private Bitmap resize_samplesize (Bitmap original, int samplesize) {

        Bitmap resized = Bitmap.createScaledBitmap(original, (int)((double) original.getWidth()/samplesize), (int)((double) original.getHeight()/samplesize), true);

        return resized;
    }


    public Bitmap gray_scaling(final Bitmap before_binarization_bitmap_image){
        int threshold = 150 * 100;

        int width = before_binarization_bitmap_image.getWidth();
        int height = before_binarization_bitmap_image.getHeight();

        int [] pixels_array = new int [width * height];
        Bitmap bitmap_sketch_book = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_4444);
        before_binarization_bitmap_image.getPixels(pixels_array, 0, width, 0, 0, width, height);


        // color information
        int A, R, G, B;
        int index;
        int gray_scaled_rgb;

        for(int y = height - 1; y >= 0; y--){
            for(int x = width - 1; x >= 0; x--){
                index = y * width + x;
                int temp_pixel = pixels_array[index];
                R = (temp_pixel >> 16) & 0xff;
                G = (temp_pixel >> 8) & 0xff;
                B = (temp_pixel) & 0xff;
                A = (temp_pixel >> 24) & 0xff;

                gray_scaled_rgb = ((R << 5) - (R << 1)) + ((G << 6) - (G << 2) - G) + ((B << 3) + (B << 1) + B);
                gray_scaled_rgb /= 100;

                pixels_array[index] = (A << 24) | (gray_scaled_rgb << 16) | (gray_scaled_rgb << 8) | (gray_scaled_rgb);
            }
        }
        bitmap_sketch_book.setPixels(pixels_array, 0, width, 0, 0, width, height);

        return bitmap_sketch_book;
    }


    public Bitmap binarization(final Bitmap before_binarization_bitmap_image){
        int threshold = 150 * 100;

        int width = before_binarization_bitmap_image.getWidth();
        int height = before_binarization_bitmap_image.getHeight();

        int [] pixels_array = new int [width * height];
        Bitmap bitmap_sketch_book = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_4444);

        before_binarization_bitmap_image.getPixels(pixels_array, 0, width, 0, 0, width, height);


        // color information
        int A, R, G, B;
        int index;
        int gray_scaled_rgb;

        for(int y = height - 1; y >= 0; y--){
            for(int x = width - 1; x >= 0; x--){
                index = y * width + x;
                int temp_pixel = pixels_array[index];
                R = (temp_pixel >> 16) & 0xff;
                G = (temp_pixel >> 8) & 0xff;
                B = (temp_pixel) & 0xff;
                A = (temp_pixel >> 24) & 0xff;

                gray_scaled_rgb = ((R << 5) - (R << 1)) + ((G << 6) - (G << 2) - G) + ((B << 3) + (B << 1) + B);

                if(gray_scaled_rgb > threshold)
                    pixels_array[index] = 0xffffffff;
                else
                    pixels_array[index] = 0xff000000;
            }
        }
        bitmap_sketch_book.setPixels(pixels_array, 0, width, 0, 0, width, height);

        return bitmap_sketch_book;
    }


    public Bitmap pad (final Bitmap before_padding_bitmap_image, int[] pixels_array, Bitmap bitmap_sketch_book, int pad_width){
        int width = before_padding_bitmap_image.getWidth();
        int height = before_padding_bitmap_image.getHeight();

        int index;


        for(int y = pad_width - 1; y >= 0; y--){
            for(int x = width - 1; x >= 0; x--){
                index = y * width + x;
                pixels_array[index] = pixels_array[index] | 0xffffffff;
            }
        }

        for(int y = height - 1; y >= height - pad_width; y--){
            for(int x = width - 1; x >= 0; x--){
                index = y * width + x;
                pixels_array[index] = pixels_array[index] | 0xffffffff;
            }
        }
        for(int y = height - 1; y >= 0; y--){
            for(int x = pad_width - 1; x >= 0; x--){
                index = y * width + x;
                pixels_array[index] = pixels_array[index] | 0xffffffff;
            }
        }
        for(int y = height - 1; y >= 0; y--){
            for(int x = width - 1; x >= width - pad_width; x--){
                index = y * width + x;
                pixels_array[index] = pixels_array[index] | 0xffffffff;
            }
        }

        bitmap_sketch_book.setPixels(pixels_array, 0, width, 0, 0, width, height);

        return bitmap_sketch_book;
    }


    public Bitmap dilation (final Bitmap before_dilation_bitmap_image, int dilation_width){


        int width = before_dilation_bitmap_image.getWidth();
        int height = before_dilation_bitmap_image.getHeight();

        int [] pixels_array = new int [width * height];
        int [] pixels_array_for_process = new int [width * height];
        Bitmap bitmap_sketch_book = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_4444);

        for(int i = pixels_array_for_process.length - 1; i >= 0 ; i--)
            pixels_array_for_process[i] = 0xffffffff;

        int [] dilation_matrix_array = new int [dilation_width * dilation_width];
        int index;
        int index_reference;
        int square_point;
        int is_dilation = 0;

        before_dilation_bitmap_image.getPixels(pixels_array, 0, width, 0, 0, width, height);

        for(index = dilation_matrix_array.length - 1; index >= 0; index--){
            dilation_matrix_array[index] = 1;
        }

        for(int y = dilation_width / 2; y < height - dilation_width / 2; y++){
            for(int x = dilation_width / 2; x < width - dilation_width / 2; x++){
                is_dilation = 0;
                square_point = y * width + x;

                for(int z = - (dilation_width / 2); z <= dilation_width / 2; z++){
                    index_reference = (y + z) * width + x;

                    for(int w = - (dilation_width / 2); w <= dilation_width / 2 ; w++){
                        index = index_reference + w;
                        if((pixels_array[index] | 0xff000000) == 0xff000000){
                            is_dilation = 1;
                            break;
                        }
                    }
                    if(is_dilation != 0)
                        break;
                }

                if(is_dilation != 0)
                    pixels_array_for_process[square_point] = 0xff000000;

            }
        }


        for(int i = pixels_array.length - 1; i >= 0 ; i--)
            pixels_array[i] = pixels_array_for_process[i];

        bitmap_sketch_book.setPixels(pixels_array, 0, width, 0, 0, width, height);

        return bitmap_sketch_book;
    }

    public Bitmap erosion (final Bitmap before_erosion_bitmap_image, int erosion_width){


        int width = before_erosion_bitmap_image.getWidth();
        int height = before_erosion_bitmap_image.getHeight();

        int [] pixels_array = new int [width * height];
        int [] pixels_array_for_process = new int [width * height];
        Bitmap bitmap_sketch_book = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_4444);
        for(int i = pixels_array_for_process.length - 1; i >= 0 ; i--)
            pixels_array_for_process[i] = 0xffffffff;

        int [] erosion_matrix_array = new int [erosion_width * erosion_width];
        int index;
        int index_reference;
        int square_point;
        int is_erosion = 0;
        int erosion_width_square = erosion_width * erosion_width;
        int escape_for_double_for = 0;

        before_erosion_bitmap_image.getPixels(pixels_array, 0, width, 0, 0, width, height);

        for(index = erosion_matrix_array.length - 1; index >= 0; index--){
            erosion_matrix_array[index] = 1;
        }

        for(int y = erosion_width / 2; y < height - erosion_width / 2; y++){
            for(int x = erosion_width / 2; x < width - erosion_width / 2; x++){
                is_erosion = 0;
                escape_for_double_for = 0;
                square_point = y * width + x;

                for(int z = - (erosion_width / 2); z <= erosion_width / 2; z++){
                    index_reference = (y + z) * width + x;

                    for(int w = - (erosion_width / 2); w <= erosion_width / 2 ; w++){
                        index = index_reference + w;
                        if((pixels_array[index] | 0xff000000) != 0xff000000){
                            escape_for_double_for = 1;
                            break;
                        }
                        is_erosion ++;
                    }

                    if(escape_for_double_for != 0)
                        break;
                }

                if(is_erosion == erosion_width_square)
                    pixels_array_for_process[square_point] = 0xff000000;

            }
        }


        for(int i = pixels_array.length - 1; i >= 0 ; i--)
            pixels_array[i] = pixels_array_for_process[i];


        bitmap_sketch_book.setPixels(pixels_array, 0, width, 0, 0, width, height);

        return bitmap_sketch_book;
    }

    public Bitmap hit_or_miss_transfrom (final Bitmap before_hit_or_miss_transform_bitmap_image, int[] pixels_array, Bitmap bitmap_sketch_book, int hit_or_miss_transform_width){
        int width = before_hit_or_miss_transform_bitmap_image.getWidth();
        int height = before_hit_or_miss_transform_bitmap_image.getHeight();

        int [] pixels_array_for_erosion = new int [width * height];
        Bitmap bitmap_for_temp_erosion = erosion(before_hit_or_miss_transform_bitmap_image, hit_or_miss_transform_width);
        bitmap_for_temp_erosion.getPixels(pixels_array_for_erosion, 0, width, 0, 0, width, height);

        int [] pixels_array_for_dilation = new int [width * height];
        for(int i = pixels_array_for_dilation.length - 1; i >= 0; i--)
            pixels_array_for_dilation[i] = 0xffffff;


        bitmap_sketch_book.setPixels(pixels_array_for_erosion, 0, width, 0, 0, width, height);

        return bitmap_sketch_book;
    }



    public Bitmap detect_the_number_area(final Bitmap before_binarization_bitmap_image){
        int threshold_of_number_of_color_changed = 9;
        int number_of_color_changed = 0;

        int width = before_binarization_bitmap_image.getWidth();
        int height = before_binarization_bitmap_image.getHeight();
        Bitmap bitmap_sketch_book = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_4444);

        int [] pixels_array = new int [width * height];

        before_binarization_bitmap_image.getPixels(pixels_array, 0, width, 0, 0, width, height);

        int [] height_index_array_for_detect_longest_height = new int [width * width];
        int current_max_height_index = 0;
        int current_max_height = 0;

        int temp_index = 0;
        int calculate_height = 0;

        before_binarization_bitmap_image.getPixels(pixels_array, 0, width, 0, 0, width, height);


        int A, R, G, B;
        int index;
        int gray_scaled_rgb;

        for(int y = 1; y < height; y++){
            number_of_color_changed = 0;
            for(int x = 0; x < width - 1; x ++){
                index = y * width + x;
                if(pixels_array[index] != pixels_array[index + 1])
                    number_of_color_changed += 1;
                if(number_of_color_changed >= threshold_of_number_of_color_changed){
                    height_index_array_for_detect_longest_height[y] = 1;
                    if(height_index_array_for_detect_longest_height[y - 1] == 0){
                        temp_index = y;
                        calculate_height = 0;
                    }
                    else{
                        calculate_height += 1;
                        if(calculate_height > current_max_height){
                            current_max_height_index = temp_index;
                            current_max_height = calculate_height;
                        }
                    }

                    break;
                }
            }

        }

        height_index_of_number_area = current_max_height_index;
        height_of_number_area = current_max_height;
        if(height_index_of_number_area > afford_of_height_index_of_number_area){
            height_index_of_number_area -= afford_of_height_index_of_number_area;
            height_of_number_area += 2 * afford_of_height_index_of_number_area;
        }



        bitmap_sketch_book.setPixels(pixels_array, 0, width, 0, 0, width, height);

        return bitmap_sketch_book;
    }



    public Bitmap gaussian_filtering (final Bitmap before_dilation_bitmap_image, int filter_width){


        int width = before_dilation_bitmap_image.getWidth();
        int height = before_dilation_bitmap_image.getHeight();

        int [] pixels_array = new int [width * height];
        int [] pixels_array_for_process = new int [width * height];
        Bitmap bitmap_sketch_book = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_4444);
        for(int i = pixels_array_for_process.length - 1; i >= 0 ; i--)
            pixels_array_for_process[i] = 0xffffffff;

        int A, R, G, B;
        float R_sum, G_sum, B_sum;
        int R_sum_int, G_sum_int, B_sum_int;
        int temp_pixel;
        int gray_scaled_rgb;

        int index;
        int index_reference;
        int index_for_filter = 0;
        int square_point;
        int filter_image_element_sum = 0;
        int is_dilation = 0;

        int filter_element_sum_for_remaking_sum_to_1 = 0;
        float [] filter_matrix_array = new float [filter_width * filter_width];

        before_dilation_bitmap_image.getPixels(pixels_array, 0, width, 0, 0, width, height);


        for(int i = 0; i < filter_matrix_array.length / 2; i ++){
            filter_matrix_array[i] = (i + 1);
            filter_matrix_array[(filter_matrix_array.length - 1) - i] = (i + 1);
        }
        filter_matrix_array[filter_matrix_array.length / 2 + 1] = filter_matrix_array[filter_matrix_array.length / 2] + 1;
        for(int i = 0; i < filter_matrix_array.length; i++)
            filter_element_sum_for_remaking_sum_to_1 += filter_matrix_array[i];
        for(int i = 0; i < filter_matrix_array.length; i++)
            filter_matrix_array[i] /= filter_element_sum_for_remaking_sum_to_1;

        for(int y = filter_width / 2; y < height - filter_width / 2; y++){
            for(int x = filter_width / 2; x < width - filter_width / 2; x++){
                square_point = y * width + x;
                index_for_filter = 0;
                gray_scaled_rgb = 0;
                R_sum = 0;
                G_sum = 0;
                B_sum = 0;

                for(int z = - (filter_width / 2); z <= filter_width / 2; z++){
                    index_reference = (y + z) * width + x;

                    for(int w = - (filter_width / 2); w <= filter_width / 2 ; w++){
                        index = index_reference + w;
//						pixels_array_for_process[square_point] += (pixels_array[index] * filter_matrix_array[index_for_filter]);


                        temp_pixel = pixels_array[index];
                        R = (temp_pixel >> 16) & 0xff;
                        G = (temp_pixel >> 8) & 0xff;
                        B = (temp_pixel) & 0xff;
                        A = (temp_pixel >> 24) & 0xff;

                        R_sum += (R * filter_matrix_array[index_for_filter]);
                        G_sum += (G * filter_matrix_array[index_for_filter]);
                        B_sum += (B * filter_matrix_array[index_for_filter]);

                        index_for_filter += 1;
                    }
                }

                R_sum_int = (int) R_sum;
                G_sum_int = (int) G_sum;
                B_sum_int = (int) B_sum;
                pixels_array_for_process[square_point] = 0xff000000 | (R_sum_int << 16) | (G_sum_int << 8) | (B_sum_int);

            }
        }


        for(int i = pixels_array.length - 1; i >= 0 ; i--)
            pixels_array[i] = pixels_array_for_process[i];

        bitmap_sketch_book.setPixels(pixels_array, 0, width, 0, 0, width, height);

        return bitmap_sketch_book;
    }

    public Bitmap get_image_of_height_number_area(final Bitmap image_which_will_be_cropped){

        int width = image_which_will_be_cropped.getWidth();
        int height = image_which_will_be_cropped.getHeight();


        Bitmap image_of_height_number_area = Bitmap.createBitmap(image_which_will_be_cropped
                , 0
                , height_index_of_number_area
                , width
                , height_of_number_area);


        return image_of_height_number_area;
    }



    public void find_letter_candidate_from_left_to_right(final Bitmap before_bitmap_image){

        int width = before_bitmap_image.getWidth();
        int height = before_bitmap_image.getHeight();

        int [] pixels_array = new int [width * height];
        Bitmap bitmap_sketch_book = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_4444);
        before_bitmap_image.getPixels(pixels_array, 0, width, 0, 0, width, height);

        int y = 0;
        int index = 0;
        int temp_pixel = 0;
        int i = 0;
        int if_there_was_in_high_part = 0;
        for(int x = 0; x < width; x++){
            if_there_was_in_high_part = 0;
            y = height * 1 / 3;
            index = y * width + x;
            temp_pixel = pixels_array[index];

            if(temp_pixel == 0xff000000){
                x_min = 99999999;
                y_min = 99999999;
                x_max = 0;
                y_max = 0;
                find_letter(pixels_array, width, height, index, x, y);
                bitmap_sketch_book.setPixels(pixels_array, 0, width, 0, 0, width, height);
                bitmap_temp = bitmap_sketch_book;
                each_character_bitmap_array[each_character_bitmap_array_index] = get_a_letter(bitmap_temp);
                each_character_bitmap_array_index += 1;
                if_there_was_in_high_part = 1;
            }

            if(if_there_was_in_high_part == 1 && (x_max - x_min) < width / 20){
                int x_temp = x;
                for(x = x_min; x < x_max; x++){
                    y = height * 2 / 3;
                    index = y * width + x;
                    temp_pixel = pixels_array[index];

                    if(temp_pixel == 0xff000000){
                        x_min = 99999999;
                        y_min = 99999999;
                        x_max = 0;
                        y_max = 0;
                        find_letter(pixels_array, width, height, index, x, y);
                        bitmap_sketch_book.setPixels(pixels_array, 0, width, 0, 0, width, height);
                        bitmap_temp = bitmap_sketch_book;
                        each_character_bitmap_array[each_character_bitmap_array_index] = get_a_letter(bitmap_temp);
                        each_character_bitmap_array_index += 1;

//                        is_alphabet_combined_or_not = 1;
                    }

                }

                x = x_temp;
            }

        }
    }



    public void find_letter(int [] pixels_array, int width, int height, int reference_index, int x, int y){

//		if(y > height || y < 0 || x < width)
//			return;


//		Toast.makeText(getApplicationContext(), "asdf", Toast.LENGTH_LONG).show();
        pixels_array[reference_index] = 0xffff0000;
        int index = 0;


//		오른쪽 픽셀 탐색
        index = reference_index + 1;
        if(pixels_array[index] == 0xff000000){
            if(x > x_max)
                x_max = x;
            if(y > y_max)
                y_max = y;
            if(x < x_min)
                x_min = x;
            if(y < y_min)
                y_min = y;

            find_letter(pixels_array, width, height, index, x + 1, y);
        }

//		오른쪽 위 픽셀 탐색
        index = reference_index - width + 1;
        if(pixels_array[index] == 0xff000000){
            if(x > x_max)
                x_max = x;
            if(y > y_max)
                y_max = y;
            if(x < x_min)
                x_min = x;
            if(y < y_min)
                y_min = y;

            find_letter(pixels_array, width, height, index, x + 1, y - 1);
        }

//		위 픽셀 탐색
        index = reference_index - width;
        if(pixels_array[index] == 0xff000000){
            if(x > x_max)
                x_max = x;
            if(y > y_max)
                y_max = y;
            if(x < x_min)
                x_min = x;
            if(y < y_min)
                y_min = y;

            find_letter(pixels_array, width, height, index, x, y - 1);
        }

//		왼쪽 위 픽셀 탐색
        index = reference_index - width - 1;
        if(pixels_array[index] == 0xff000000){
            if(x > x_max)
                x_max = x;
            if(y > y_max)
                y_max = y;
            if(x < x_min)
                x_min = x;
            if(y < y_min)
                y_min = y;

            find_letter(pixels_array, width, height, index, x - 1, y - 1);
        }

//		왼쪽 픽셀 탐색
        index = reference_index - 1;
        if(pixels_array[index] == 0xff000000){
            if(x > x_max)
                x_max = x;
            if(y > y_max)
                y_max = y;
            if(x < x_min)
                x_min = x;
            if(y < y_min)
                y_min = y;

            find_letter(pixels_array, width, height, index, x - 1, y);
        }

//		왼쪽 아래 픽셀 탐색
        index = reference_index + width - 1;
        if(pixels_array[index] == 0xff000000){
            if(x > x_max)
                x_max = x;
            if(y > y_max)
                y_max = y;
            if(x < x_min)
                x_min = x;
            if(y < y_min)
                y_min = y;

            find_letter(pixels_array, width, height, index, x - 1, y + 1);
        }

//		아래 픽셀 탐색
        index = reference_index + width;
        if(pixels_array[index] == 0xff000000){
            if(x > x_max)
                x_max = x;
            if(y > y_max)
                y_max = y;
            if(x < x_min)
                x_min = x;
            if(y < y_min)
                y_min = y;

            find_letter(pixels_array, width, height, index, x, y + 1);
        }

//		오른쪽 아래 픽셀 탐색
        index = reference_index + width + 1;
        if(pixels_array[index] == 0xff000000){
            if(x > x_max)
                x_max = x;
            if(y > y_max)
                y_max = y;
            if(x < x_min)
                x_min = x;
            if(y < y_min)
                y_min = y;

            find_letter(pixels_array, width, height, index, x + 1, y + 1);
        }



        return;
    }


    public Bitmap get_a_letter(final Bitmap image_which_will_be_re_created){

        int width_of_image_which_would_be_extracted = image_which_will_be_re_created.getWidth();
        int height_of_image_which_would_be_extracted = image_which_will_be_re_created.getHeight();
        int index_for_extract = 0;
        int [] original_pixels_array = new int [width_of_image_which_would_be_extracted * height_of_image_which_would_be_extracted];
        image_which_will_be_re_created.getPixels(original_pixels_array, 0, width_of_image_which_would_be_extracted, 0, 0, width_of_image_which_would_be_extracted, height_of_image_which_would_be_extracted);

        int width = x_max - x_min + 1;
        int height = y_max - y_min + 1;
        int x = 0;
        int y = 0;
        int index = 0;
        int [] pixels_array = new int [width * height];

        Bitmap bitmap_sketch_book = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_4444);

        for(y = 0; y < height; y++){
            for(x = 0; x < width; x++){
                index = width * y + x;
                index_for_extract = width_of_image_which_would_be_extracted * (y_min + y) + (x_min + x);
                if(original_pixels_array[index_for_extract] == 0xffff0000)
                    pixels_array[index] = 0xff000000;
                else
                    pixels_array[index] = 0xffffffff;
            }
        }

        bitmap_sketch_book.setPixels(pixels_array, 0, width, 0, 0, width, height);

        return bitmap_sketch_book;


    }



    public static Bitmap RotateBitmap(Bitmap source, float angle)
    {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }

    public Bitmap get_rid_of_border_whose_width_is_1_pixel(final Bitmap before_bitmap_image){
        int threshold = 150 * 100;

        int width = before_bitmap_image.getWidth();
        int height = before_bitmap_image.getHeight();

        int [] pixels_array = new int [width * height];
        Bitmap bitmap_sketch_book = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_4444);
        before_bitmap_image.getPixels(pixels_array, 0, width, 0, 0, width, height);

        int x = 0;
        int y = 0;
        int index = 0;

        y = 0;
        for(x = 0; x < width; x++){
            index = y * width + x;
            pixels_array[index] = 0xffff0000;
        }
        y = height - 1;
        for(x = 0; x < width; x++){
            index = y * width + x;
            pixels_array[index] = 0xffff0000;
        }
        x = 0;
        for(y = 0; y < height; y++){
            index = y * width + x;
            pixels_array[index] = 0xffff0000;
        }
        x = width - 1;
        for(y = 0; y < height; y++){
            index = y * width + x;
            pixels_array[index] = 0xffff0000;
        }

        bitmap_sketch_book.setPixels(pixels_array, 0, width, 0, 0, width, height);

        return bitmap_sketch_book;
    }



    public Bitmap pseudo_erase_image(final Bitmap before_bitmap_image){

        int width = before_bitmap_image.getWidth();
        int height = before_bitmap_image.getHeight();

        int [] pixels_array = new int [width * height];
        Bitmap bitmap_sketch_book = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_4444);

        before_bitmap_image.getPixels(pixels_array, 0, width, 0, 0, width, height);

        int index = 0;

        for(index = 0; index < width * height; index++)
            pixels_array[index] = 0xffffffff;

        bitmap_sketch_book.setPixels(pixels_array, 0, width, 0, 0, width, height);

        return bitmap_sketch_book;
    }


    public void judge_candidate_by_width(final Bitmap before_bitmap_image){

        int width = before_bitmap_image.getWidth();
        int height = before_bitmap_image.getHeight();
        int erase_or_not = 0;

        int index = 0;

        for(int i = 0; i < each_character_bitmap_array_index; i++){
            int each_candidate_width = each_character_bitmap_array[i].getWidth();
            int each_candidate_height = each_character_bitmap_array[i].getHeight();
            erase_or_not = 0;

            int [] pixels_array = new int [each_candidate_width * each_candidate_height];
            before_bitmap_image.getPixels(pixels_array, 0, each_candidate_width, 0, 0, each_candidate_width, each_candidate_height);

            if(each_candidate_width > width / 20)
                erase_or_not = 1;

            else if(each_candidate_height < height / 6)
                erase_or_not = 1;

            else if(pixels_array[0] == 0xff000000){
                if(pixels_array[each_candidate_width * (each_candidate_height - 1) + 1] == 0xff000000)
                    if(pixels_array[each_candidate_width - 1] == 0xffffffff)
                        if(pixels_array[each_candidate_width * each_candidate_height - 1] == 0xffffffff)
                            erase_or_not = 1;
            }
            else if(pixels_array[0] == 0xffffffff){
                if(pixels_array[each_candidate_width * (each_candidate_height - 1) + 1] == 0xffffffff)
                    if(pixels_array[each_candidate_width - 1] == 0xff000000)
                        if(pixels_array[each_candidate_width * each_candidate_height - 1] == 0xff000000)
                            erase_or_not = 1;
            }


            if(erase_or_not == 1){
//                each_character_bitmap_array[i] = pseudo_erase_image(each_character_bitmap_array[i]);
                text_view_whether_letter_or_number_or_noise[i].setText("노이즈");
            }
            else{
                index_of_letter_and_number[index] = i;
                index += 1;
            }
        }


        if(index == 8)
            is_alphabet_combined_or_not = 0;
        else
            is_alphabet_combined_or_not = 1;


        for(int i = 0; index_of_letter_and_number[i] != 0; i++){
            if(is_alphabet_combined_or_not == 0){
                if(i == 0 || i == 1)
                    text_view_whether_letter_or_number_or_noise[index_of_letter_and_number[i]].setText("숫자");
                else if(i == 2)
                    text_view_whether_letter_or_number_or_noise[index_of_letter_and_number[i]].setText("자음");
                else if(i == 3)
                    text_view_whether_letter_or_number_or_noise[index_of_letter_and_number[i]].setText("모음");
                else
                    text_view_whether_letter_or_number_or_noise[index_of_letter_and_number[i]].setText("숫자");
            }
            else if(is_alphabet_combined_or_not == 1){
                if(i == 0 || i == 1)
                    text_view_whether_letter_or_number_or_noise[index_of_letter_and_number[i]].setText("숫자");
                else if(i == 2)
                    text_view_whether_letter_or_number_or_noise[index_of_letter_and_number[i]].setText("자모음");
                else
                    text_view_whether_letter_or_number_or_noise[index_of_letter_and_number[i]].setText("숫자");
            }
        }
    }


    //    source: http://www.masterqna.com/android/81347/%EC%95%88%EB%93%9C%EB%A1%9C%EC%9D%B4%EB%93%9C-app-%EC%9E%AC%EC%8B%9C%EC%9E%91-%EC%97%90-%EB%8C%80%ED%95%B4-%EC%A7%88%EB%AC%B8%EB%93%9C%EB%A6%BD%EB%8B%88%EB%8B%A4
    public void restart(){

        Intent i = getBaseContext().getPackageManager().
                getLaunchIntentForPackage(getBaseContext().getPackageName());
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);

    }


    public void distinguish_numbers (final Bitmap before_bitmap_image, int image_index) {

        int black = 0xff000000;
        int width = before_bitmap_image.getWidth();
        int[] width_divided_by_12 = new int[12];
        for (int i = 0; i < width_divided_by_12.length; i++)
            width_divided_by_12[i] = (width * (i + 1) / 12);
        int[] width_divided_by_24 = new int[24];
        for (int i = 0; i < width_divided_by_24.length; i++)
            width_divided_by_24[i] = (width * (i + 1) / 24);
        int height = before_bitmap_image.getHeight();
        int[] height_divided_by_12 = new int[12];
        for (int i = 0; i < height_divided_by_12.length; i++)
            height_divided_by_12[i] = (height * (i + 1) / 12);
        int[] height_divided_by_24 = new int[24];
        for (int i = 0; i < height_divided_by_24.length; i++)
            height_divided_by_24[i] = (height * (i + 1) / 24);

        int[] pixels_array = new int[width * height];
        Bitmap bitmap_sketch_book = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_4444);

        before_bitmap_image.getPixels(pixels_array, 0, width, 0, 0, width, height);


        int index;
        int index_temp_for_less_multiplying = 0;
        int count_x_symmetric_of_left_and_right = 0;

        int[] detecting_all_part_left_of_each_height_divided_by_24 = new int[24];
        int[] detecting_all_part_right_of_each_height_divided_by_24 = new int[detecting_all_part_left_of_each_height_divided_by_24.length];
        int[] detecting_all_part_top_of_each_width_divided_by_24 = new int[24];
        int[] detecting_all_part_bottom_of_each_width_divided_by_24 = new int[detecting_all_part_top_of_each_width_divided_by_24.length];


        int[] detecting_0_at_each_case = new int[4];

        int[] detecting_1_at_each_case_count_x_symmetric_of_left_and_right = new int[3];
        int[] detecting_1_at_each_case_count_y_symmetric_of_top_and_bottom = new int[1];

        int[] detecting_2_at_each_case_count_x_of_left_and_right = new int[3];

        int[] detecting_3_at_each_case = new int[2];

        int[] detecting_4_at_each_case_count_x_of_left_and_right = new int[8];

        int[] detecting_6_at_each_case_count_x_of_left_and_right = new int[2];
        int[] detecting_6_high_part_left_of_each_height_divided_by_24 = new int[5];
        int[] detecting_6_high_part_right_of_each_height_divided_by_24 = new int[detecting_6_high_part_left_of_each_height_divided_by_24.length];
        int[] detecting_6_low_part_left_of_each_height_divided_by_24 = new int[5];
        int[] detecting_6_low_part_right_of_each_height_divided_by_24 = new int[detecting_6_low_part_left_of_each_height_divided_by_24.length];


        int[] detecting_7_at_each_case_count_x_symmetric_of_left_and_right = new int[1];
        int[] detecting_7_left_of_each_height_divided_by_12 = new int[height_divided_by_12.length - 3];
        int[] detecting_7_right_of_each_height_divided_by_12 = new int[detecting_7_left_of_each_height_divided_by_12.length];
        int[] detecting_7_at_each_case_count_y_of_top_and_bottom = new int[1];

        int[] detecting_8_at_each_case_count_x_of_left_and_right = new int[4];


        int count_y_symmetric_of_left_and_right = 0;
        int[] left = new int[2];
        int[] left_validity = new int[2];
        int[] right = new int[2];
        int[] right_validity = new int[2];
        int[] top = new int[2];
        int[] top_validity = new int[2];
        int[] bottom = new int[2];
        int[] bottom_validity = new int[2];



        int[] detecting_9_at_each_case_count_x_of_left_and_right = new int[3];



        int temp_for_swap;


//        detect left and right
        for (int y = 0; y < height - 1; y++) {

            if (y == 0) {
                index_temp_for_less_multiplying = y * width;
                for (int x = 0; x < width; x++) {
                    index = index_temp_for_less_multiplying + x;

                    if (pixels_array[index] == black) {
                        left[0] = x;
                        left_validity[0] = 1;
                        break;
                    }
                }

                for (int x = width - 1; x > 0; x--) {
                    index = index_temp_for_less_multiplying + x;

                    if (pixels_array[index] == black) {
                        right[0] = x;
                        right_validity[0] = 1;
                        break;
                    }
                }
            }

            index_temp_for_less_multiplying = (y + 1) * width;
            for (int x = 0; x < width; x++) {
                index = index_temp_for_less_multiplying + x;

                if (pixels_array[index] == black) {
                    left[1] = x;
                    left_validity[1] = 1;
                    break;
                }
            }

            for (int x = width - 1; x > 0; x--) {
                index = index_temp_for_less_multiplying + x;

                if (pixels_array[index] == black) {
                    right[1] = x;
                    right_validity[1] = 1;
                    break;
                }
            }

            if (left_validity[0] == 1 && right_validity[0] == 1 && left_validity[1] == 1 && right_validity[1] == 1) {
//                if((right[1] - right[0]) + (left[1] - left[0]) == 0)
//                if((right[1] - left[1]) + (left[1] - left[0]) == 0)
//                if((right[1] - right[0]) >= 0 && (left[1] - left[0] >= 0) || (right[1] - right[0]) <= 0 && (left[1] - left[0]) <= 0)
//                    count_x_symmetric_of_left_and_right += 1;


//                detecting 1
//                if(y > height){
                if (y > height_divided_by_12[0]) {
                    if ((right[0] - left[0]) - (right[1] - left[1]) == 0)
                        detecting_1_at_each_case_count_x_symmetric_of_left_and_right[0] += 1;
                }
//                if(y > height * 1 / 2){
                if (y > height_divided_by_12[5]) {
                    if ((right[0] - left[0]) - (right[1] - left[1]) == 0)
                        detecting_1_at_each_case_count_x_symmetric_of_left_and_right[1] += 1;
                }
//                if(y > height * 2 / 3){
                if (y > height_divided_by_12[7]) {
                    if ((right[0] - left[0]) - (right[1] - left[1]) == 0)
                        detecting_1_at_each_case_count_x_symmetric_of_left_and_right[2] += 1;
                }



//                detecting 6
                for (int j = 0; j < detecting_6_high_part_left_of_each_height_divided_by_24.length; j++) {
                    if (y == height_divided_by_24[j]) {
                        detecting_6_high_part_left_of_each_height_divided_by_24[j] = left[1];
                        detecting_6_high_part_right_of_each_height_divided_by_24[j] = right[1];
                    }
                }
                for (int j = 0; j < detecting_6_low_part_left_of_each_height_divided_by_24.length; j++) {
                    if (y == height_divided_by_24[height_divided_by_24.length - 1 - j]) {
                        detecting_6_low_part_left_of_each_height_divided_by_24[j] = left[1];
                        detecting_6_low_part_right_of_each_height_divided_by_24[j] = right[1];
                    }
                }


//                detecting 7
                for (int j = 3; j < height_divided_by_12.length; j++) {
                    if (y == height_divided_by_12[j]) {
                        detecting_7_left_of_each_height_divided_by_12[j - 3] = left[0];
                        detecting_7_right_of_each_height_divided_by_12[j - 3] = right[0];
                    }
                }




//                detecting left and right
                for(int j = 0; j < detecting_all_part_left_of_each_height_divided_by_24.length; j++){
                    if(y == height_divided_by_24[j]){
                        detecting_all_part_left_of_each_height_divided_by_24[j] = left[0];
                        detecting_all_part_right_of_each_height_divided_by_24[j] = right[0];
                    }
                }
            }


            left[0] = left[1];
            left_validity[0] = left_validity[1];
            right[0] = right[1];
            right_validity[0] = right_validity[1];

//            left[1] = 0;
            left_validity[1] = 0;
//            right[1] = 0;
            right_validity[1] = 0;
        }

        for (int i = 0; i < left.length; i++) {
            left[i] = 0;
            left_validity[i] = 0;
            right[i] = 0;
            right_validity[i] = 0;
        }

//        from here
//        right -> bottom
//        left -> top

//        detect top and bottom
        for (int x = 0; x < width - 1; x++) {

            if (x == 0) {
                for (int y = 0; y < height; y++) {
                    index = y * width + x;

                    if (pixels_array[index] == black) {
                        top[0] = y;
                        top_validity[0] = 1;
                        break;
                    }
                }

                for (int y = height - 1; y > 0; y--) {
                    index = y * width + x;

                    if (pixels_array[index] == black) {
                        bottom[0] = y;
                        bottom_validity[0] = 1;
                        break;
                    }
                }
            }


            for (int y = 0; y < height; y++) {
                index = width * y + x;

                if (pixels_array[index] == black) {
                    top[1] = y;
                    top_validity[1] = 1;
                    break;
                }
            }

            for (int y = height - 1; y > 0; y--) {
                index = width * y + x;

                if (pixels_array[index] == black) {
                    bottom[1] = y;
                    bottom_validity[1] = 1;
                    break;
                }
            }

            if (top_validity[0] == 1 && bottom_validity[0] == 1 && top_validity[1] == 1 && bottom_validity[1] == 1) {
//                if((right[1] - right[0]) * (left[1] - left[0]) < 0)
//                if((bottom[1] - right[0]) + (left[1] - left[0]) == 0)
//                    count_y_symmetric_of_left_and_right += 1;


//                detecting 7
//                if((bottom[1] - bottom[0]) != (top[1] - top[0]))
//                    detecting_7_at_each_case_count_y_of_top_and_bottom[0] += 1;
                if (x > width_divided_by_12[5])
                    if ((bottom[1] - top[1]) < (bottom[0] - top[0]))
                        detecting_7_at_each_case_count_y_of_top_and_bottom[0] += 1;


                //                detecting for 0 ~ 9
                for(int j = 0; j < detecting_all_part_left_of_each_height_divided_by_24.length; j++){
                    if(x == width_divided_by_24[j]){
                        detecting_all_part_top_of_each_width_divided_by_24[j] = top[0];
                        detecting_all_part_bottom_of_each_width_divided_by_24[j] = bottom[0];
                    }
                }
            }


            top[0] = top[1];
            top_validity[0] = top_validity[1];
            bottom[0] = bottom[1];
            bottom_validity[0] = bottom_validity[1];

//            left[1] = 0;
            top_validity[1] = 0;
//            right[1] = 0;
            bottom_validity[1] = 0;
        }

//        float symmetric_rate = (float)
        float x_symmetric_rate = (float) count_x_symmetric_of_left_and_right / (height / 3);
        String x_symmetric_rate_string = String.format("%.2f", x_symmetric_rate);




        //        calculate rate for detecting 8
        float[] detecting_8_at_each_case_x_rate = new float[detecting_8_at_each_case_count_x_of_left_and_right.length];
        for (int i = 0; i < detecting_all_part_left_of_each_height_divided_by_24.length / 4; i++) {
            if ((detecting_all_part_right_of_each_height_divided_by_24[i + 1] - detecting_all_part_left_of_each_height_divided_by_24[i + 1]) > (detecting_all_part_right_of_each_height_divided_by_24[i] - detecting_all_part_left_of_each_height_divided_by_24[i]))
                detecting_8_at_each_case_count_x_of_left_and_right[0] += 1;
        }
        detecting_8_at_each_case_x_rate[0] = (float) detecting_8_at_each_case_count_x_of_left_and_right[0] / (detecting_all_part_left_of_each_height_divided_by_24.length / 4);
        for (int i = detecting_all_part_left_of_each_height_divided_by_24.length * 1 / 4; i < detecting_all_part_left_of_each_height_divided_by_24.length * 2 / 4; i++) {
            if ((detecting_all_part_right_of_each_height_divided_by_24[i + 1] - detecting_all_part_left_of_each_height_divided_by_24[i + 1]) < (detecting_all_part_right_of_each_height_divided_by_24[i] - detecting_all_part_left_of_each_height_divided_by_24[i]))
                detecting_8_at_each_case_count_x_of_left_and_right[1] += 1;
        }
        detecting_8_at_each_case_x_rate[1] = (float) detecting_8_at_each_case_count_x_of_left_and_right[1] / (detecting_all_part_left_of_each_height_divided_by_24.length / 4);
        for (int i = detecting_all_part_left_of_each_height_divided_by_24.length * 2 / 4; i < detecting_all_part_left_of_each_height_divided_by_24.length * 3 / 4; i++) {
            if ((detecting_all_part_right_of_each_height_divided_by_24[i + 1] - detecting_all_part_left_of_each_height_divided_by_24[i + 1]) > (detecting_all_part_right_of_each_height_divided_by_24[i] - detecting_all_part_left_of_each_height_divided_by_24[i]))
                detecting_8_at_each_case_count_x_of_left_and_right[2] += 1;
        }
        detecting_8_at_each_case_x_rate[2] = (float) detecting_8_at_each_case_count_x_of_left_and_right[2] / (detecting_all_part_left_of_each_height_divided_by_24.length / 4);
        for (int i = detecting_all_part_left_of_each_height_divided_by_24.length * 3 / 4; i < detecting_all_part_left_of_each_height_divided_by_24.length * 4 / 4 - 1; i++) {
            if ((detecting_all_part_right_of_each_height_divided_by_24[i + 1] - detecting_all_part_left_of_each_height_divided_by_24[i + 1]) < (detecting_all_part_right_of_each_height_divided_by_24[i] - detecting_all_part_left_of_each_height_divided_by_24[i]))
                detecting_8_at_each_case_count_x_of_left_and_right[3] += 1;
        }
        detecting_8_at_each_case_x_rate[3] = (float) detecting_8_at_each_case_count_x_of_left_and_right[3] / (detecting_all_part_left_of_each_height_divided_by_24.length / 4 - 1);

        if (detecting_8_at_each_case_x_rate[0] > 0.5)
            if (detecting_8_at_each_case_x_rate[1] > 0.5)
//                if (detecting_8_at_each_case_x_rate[2] > 0.5)
                if (detecting_8_at_each_case_x_rate[3] > 0.5)
                {
                    text_view_whether_letter_or_number_or_noise[image_index].setText("8");
                    return;
                }


        //        calculate rate for detecting 2
        float[] detecting_2_at_each_case_x_rate = new float[detecting_2_at_each_case_count_x_of_left_and_right.length];
        for (int i = 0; i < detecting_all_part_left_of_each_height_divided_by_24.length * 1 / 12 - 1; i++) {
            if ((detecting_all_part_left_of_each_height_divided_by_24[i] > detecting_all_part_left_of_each_height_divided_by_24[i + 1]) && (detecting_all_part_right_of_each_height_divided_by_24[i] < detecting_all_part_right_of_each_height_divided_by_24[i + 1]))
                detecting_2_at_each_case_count_x_of_left_and_right[0] += 1;
        }
        detecting_2_at_each_case_x_rate[0] = (float) detecting_2_at_each_case_count_x_of_left_and_right[0] / (detecting_all_part_left_of_each_height_divided_by_24.length * 1 / 12 - 1);
        for (int i = detecting_all_part_left_of_each_height_divided_by_24.length * 1 / 3; i < detecting_all_part_left_of_each_height_divided_by_24.length * 2 / 3; i++) {
            if ((detecting_all_part_left_of_each_height_divided_by_24[i] > detecting_all_part_left_of_each_height_divided_by_24[i + 1]) && (detecting_all_part_right_of_each_height_divided_by_24[i] > detecting_all_part_right_of_each_height_divided_by_24[i + 1]))
                detecting_2_at_each_case_count_x_of_left_and_right[1] += 1;
        }
        detecting_2_at_each_case_x_rate[1] = (float) detecting_2_at_each_case_count_x_of_left_and_right[1] / (detecting_all_part_left_of_each_height_divided_by_24.length * 1 / 3);
        for (int i = detecting_all_part_left_of_each_height_divided_by_24.length * 1 / 3; i < detecting_all_part_left_of_each_height_divided_by_24.length * 2 / 3; i++) {
            if ((detecting_all_part_right_of_each_height_divided_by_24[i] - detecting_all_part_left_of_each_height_divided_by_24[i]) < (detecting_all_part_right_of_each_height_divided_by_24[detecting_all_part_left_of_each_height_divided_by_24.length - 3] - detecting_all_part_left_of_each_height_divided_by_24[detecting_all_part_left_of_each_height_divided_by_24.length - 3]))
                detecting_2_at_each_case_count_x_of_left_and_right[2] += 1;
        }
        detecting_2_at_each_case_x_rate[2] = (float) detecting_2_at_each_case_count_x_of_left_and_right[2] / (detecting_all_part_left_of_each_height_divided_by_24.length * 1 / 3);

        if (detecting_2_at_each_case_x_rate[0] > 0.5)
            if (detecting_2_at_each_case_x_rate[1] > 0.5)
                if(detecting_2_at_each_case_x_rate[2] > 0.5)
                {
                    text_view_whether_letter_or_number_or_noise[image_index].setText("2");
                    return;
                }



        //        calculate rate for detecting 9
        float[] detecting_9_at_each_case_x_rate = new float[detecting_9_at_each_case_count_x_of_left_and_right.length];
        for (int i = 0; i < detecting_all_part_left_of_each_height_divided_by_24.length / 5; i++) {
            if ((detecting_all_part_right_of_each_height_divided_by_24[i + 1] - detecting_all_part_left_of_each_height_divided_by_24[i + 1]) > (detecting_all_part_right_of_each_height_divided_by_24[i] - detecting_all_part_left_of_each_height_divided_by_24[i]))
                detecting_9_at_each_case_count_x_of_left_and_right[0] += 1;
        }
        detecting_9_at_each_case_x_rate[0] = (float) detecting_9_at_each_case_count_x_of_left_and_right[0] / (detecting_all_part_left_of_each_height_divided_by_24.length / 5);
        for (int i = detecting_all_part_left_of_each_height_divided_by_24.length / 3; i < detecting_all_part_left_of_each_height_divided_by_24.length / 2; i++) {
            if ((detecting_all_part_right_of_each_height_divided_by_24[i + 1] - detecting_all_part_left_of_each_height_divided_by_24[i + 1]) < (detecting_all_part_right_of_each_height_divided_by_24[i] - detecting_all_part_left_of_each_height_divided_by_24[i]))
                detecting_9_at_each_case_count_x_of_left_and_right[1] += 1;
        }
        detecting_9_at_each_case_x_rate[1] = (float) detecting_9_at_each_case_count_x_of_left_and_right[1] / (detecting_all_part_left_of_each_height_divided_by_24.length / 6);
        for (int i = detecting_all_part_left_of_each_height_divided_by_24.length * 3 / 4; i < detecting_all_part_left_of_each_height_divided_by_24.length - 1; i++) {
            if ((detecting_all_part_right_of_each_height_divided_by_24[i] - detecting_all_part_right_of_each_height_divided_by_24[i + 1] > 0) && (detecting_all_part_left_of_each_height_divided_by_24[i] - detecting_all_part_left_of_each_height_divided_by_24[i + 1] > 0))
                detecting_9_at_each_case_count_x_of_left_and_right[2] += 1;
        }
        detecting_9_at_each_case_x_rate[2] = (float) detecting_9_at_each_case_count_x_of_left_and_right[2] / (detecting_all_part_left_of_each_height_divided_by_24.length * 1 / 4 - 1);

        if (detecting_9_at_each_case_x_rate[0] > 0.5)
            if (detecting_9_at_each_case_x_rate[1] > 0.5)
                if (detecting_9_at_each_case_x_rate[2] > 0.5)
                {
                    text_view_whether_letter_or_number_or_noise[image_index].setText("9");
                    return;
                }



        //        calculate rate for detecting 4
        float[] detecting_4_at_each_case_x_rate = new float[detecting_4_at_each_case_count_x_of_left_and_right.length];
        for (int i = 0; i < detecting_all_part_left_of_each_height_divided_by_24.length / 4; i++) {
            if ((detecting_all_part_left_of_each_height_divided_by_24[i] > detecting_all_part_left_of_each_height_divided_by_24[i + 1]))
                detecting_4_at_each_case_count_x_of_left_and_right[0] += 1;
        }
        detecting_4_at_each_case_x_rate[0] = (float) detecting_4_at_each_case_count_x_of_left_and_right[0] / (detecting_all_part_left_of_each_height_divided_by_24.length / 4);
        for (int i = detecting_all_part_left_of_each_height_divided_by_24.length / 4; i < detecting_all_part_left_of_each_height_divided_by_24.length * 2 / 4; i++) {
            if ((detecting_all_part_left_of_each_height_divided_by_24[i] > detecting_all_part_left_of_each_height_divided_by_24[i + 1]))
                detecting_4_at_each_case_count_x_of_left_and_right[1] += 1;
        }
        detecting_4_at_each_case_x_rate[1] = (float) detecting_4_at_each_case_count_x_of_left_and_right[1] / (detecting_all_part_left_of_each_height_divided_by_24.length / 4);
        for(int i = detecting_all_part_left_of_each_height_divided_by_24.length * 2 / 4; i < detecting_all_part_left_of_each_height_divided_by_24.length * 3 / 4; i++){
            if ((detecting_all_part_left_of_each_height_divided_by_24[i] > detecting_all_part_left_of_each_height_divided_by_24[i + 1]))
                detecting_4_at_each_case_count_x_of_left_and_right[2] += 1;
        }
        detecting_4_at_each_case_x_rate[2] = (float) detecting_4_at_each_case_count_x_of_left_and_right[2] / (detecting_all_part_left_of_each_height_divided_by_24.length / 4);
        for(int i = 0; i < detecting_all_part_left_of_each_height_divided_by_24.length * 3 / 4; i++){
            if ((detecting_all_part_left_of_each_height_divided_by_24[i] > detecting_all_part_left_of_each_height_divided_by_24[i + 1]))
                detecting_4_at_each_case_count_x_of_left_and_right[3] += 1;
        }
        detecting_4_at_each_case_x_rate[3] = (float) detecting_4_at_each_case_count_x_of_left_and_right[3] / (detecting_all_part_left_of_each_height_divided_by_24.length * 3 / 4);

        for (int i = 0; i < detecting_all_part_left_of_each_height_divided_by_24.length / 4; i++) {
            if ((detecting_all_part_right_of_each_height_divided_by_24[i + 1] - detecting_all_part_left_of_each_height_divided_by_24[i + 1]) > (detecting_all_part_right_of_each_height_divided_by_24[i] - detecting_all_part_left_of_each_height_divided_by_24[i]))
                detecting_4_at_each_case_count_x_of_left_and_right[4] += 1;
        }
        detecting_4_at_each_case_x_rate[4] = (float) detecting_4_at_each_case_count_x_of_left_and_right[4] / (detecting_all_part_left_of_each_height_divided_by_24.length / 4);
        for (int i = detecting_all_part_left_of_each_height_divided_by_24.length / 4; i < detecting_all_part_left_of_each_height_divided_by_24.length * 2 / 4; i++) {
            if ((detecting_all_part_right_of_each_height_divided_by_24[i + 1] - detecting_all_part_left_of_each_height_divided_by_24[i + 1]) > (detecting_all_part_right_of_each_height_divided_by_24[i] - detecting_all_part_left_of_each_height_divided_by_24[i]))
                detecting_4_at_each_case_count_x_of_left_and_right[5] += 1;
        }
        detecting_4_at_each_case_x_rate[5] = (float) detecting_4_at_each_case_count_x_of_left_and_right[5] / (detecting_all_part_left_of_each_height_divided_by_24.length / 4);
        for(int i = detecting_all_part_left_of_each_height_divided_by_24.length * 2 / 4; i < detecting_all_part_left_of_each_height_divided_by_24.length * 3 / 4; i++){
            if ((detecting_all_part_right_of_each_height_divided_by_24[i + 1] - detecting_all_part_left_of_each_height_divided_by_24[i + 1]) > (detecting_all_part_right_of_each_height_divided_by_24[i] - detecting_all_part_left_of_each_height_divided_by_24[i]))
                detecting_4_at_each_case_count_x_of_left_and_right[6] += 1;
        }
        detecting_4_at_each_case_x_rate[6] = (float) detecting_4_at_each_case_count_x_of_left_and_right[6] / (detecting_all_part_left_of_each_height_divided_by_24.length / 4);
        for(int i = 0; i < detecting_all_part_left_of_each_height_divided_by_24.length * 3 / 4; i++){
            if ((detecting_all_part_right_of_each_height_divided_by_24[i + 1] - detecting_all_part_left_of_each_height_divided_by_24[i + 1]) > (detecting_all_part_right_of_each_height_divided_by_24[i] - detecting_all_part_left_of_each_height_divided_by_24[i]))
                detecting_4_at_each_case_count_x_of_left_and_right[7] += 1;
        }
        detecting_4_at_each_case_x_rate[7] = (float) detecting_4_at_each_case_count_x_of_left_and_right[7] / (detecting_all_part_right_of_each_height_divided_by_24.length * 3 / 4);


        if (detecting_4_at_each_case_x_rate[0] > 0.5)
            if (detecting_4_at_each_case_x_rate[1] > 0.5)
                if (detecting_4_at_each_case_x_rate[2] > 0.5)
                    if (detecting_4_at_each_case_x_rate[3] > 0.5)
                        if (detecting_4_at_each_case_x_rate[4] > 0.5)
                            if (detecting_4_at_each_case_x_rate[5] > 0.5)
                                if (detecting_4_at_each_case_x_rate[6] > 0.5)
                                    if (detecting_4_at_each_case_x_rate[7] > 0.5)
                                    {
                                        text_view_whether_letter_or_number_or_noise[image_index].setText("4");
                                        return;
                                    }


////                if (detecting_6_low_part_left_of_each_height_divided_by_24[1] > detecting_6_low_part_left_of_each_height_divided_by_24[detecting_6_low_part_left_of_each_height_divided_by_24.length - 1])
//                if (detecting_6_low_part_left_of_each_height_divided_by_24[1] < detecting_6_high_part_left_of_each_height_divided_by_24[0])
//                    if(detecting_6_low_part_left_of_each_height_divided_by_24[0] < detecting_6_high_part_left_of_each_height_divided_by_24[0]) {
//                        text_view_whether_letter_or_number_or_noise[image_index].setText("6");
////                        text_view_whether_letter_or_number_or_noise[image_index].setText(Integer.toString(detecting_6_low_part_left_of_each_height_divided_by_24[2]));
//                        return;
//                    }


        //        calculate rate for detecting 7
        float[] detecting_7_at_each_case_count_x_symmetric_of_left_and_right_rate = new float[detecting_7_at_each_case_count_x_symmetric_of_left_and_right.length];
        for (int i = 0; i < detecting_7_left_of_each_height_divided_by_12.length - 1; i++) {
            if ((detecting_7_left_of_each_height_divided_by_12[i] > detecting_7_left_of_each_height_divided_by_12[i + 1]) && (detecting_7_right_of_each_height_divided_by_12[i] > detecting_7_right_of_each_height_divided_by_12[i + 1]))
                detecting_7_at_each_case_count_x_symmetric_of_left_and_right[0] += 1;
        }
        detecting_7_at_each_case_count_x_symmetric_of_left_and_right_rate[0] = (float) detecting_7_at_each_case_count_x_symmetric_of_left_and_right[0] / (detecting_7_left_of_each_height_divided_by_12.length - 1);
        float[] detecting_7_at_each_case_y_rate = new float[detecting_7_at_each_case_count_y_of_top_and_bottom.length];
        detecting_7_at_each_case_y_rate[0] = (float) detecting_7_at_each_case_count_y_of_top_and_bottom[0] / (width * 1 / 2);
//        String detecting_7_at_each_case_y_rate_string = String.format("%.2f", detecting_7_at_each_case_y_rate[0]);
//        text_view_whether_letter_or_number_or_noise[image_index].setText(detecting_7_at_each_case_y_rate_string);

        if (detecting_7_at_each_case_count_x_symmetric_of_left_and_right_rate[0] > 0.5)
            if (detecting_7_at_each_case_y_rate[0] > 0.5) {
                text_view_whether_letter_or_number_or_noise[image_index].setText("7");
                return;
            }


//        calculate rate for detecting 1
        float[] detecting_1_at_each_case_x_symmetric_rate = new float[detecting_1_at_each_case_count_x_symmetric_of_left_and_right.length];
        detecting_1_at_each_case_x_symmetric_rate[0] = (float) detecting_1_at_each_case_count_x_symmetric_of_left_and_right[0] / (height);
        detecting_1_at_each_case_x_symmetric_rate[1] = (float) detecting_1_at_each_case_count_x_symmetric_of_left_and_right[1] / (height * 1 / 2);
        detecting_1_at_each_case_x_symmetric_rate[2] = (float) detecting_1_at_each_case_count_x_symmetric_of_left_and_right[2] / (height * 1 / 3);
//        String at_each_case_x_symmetric_rate_string = String.format("%.2f", at_each_case_x_symmetric_rate[1]);

        if (detecting_1_at_each_case_x_symmetric_rate[0] > 0.5)
            if (detecting_1_at_each_case_x_symmetric_rate[1] > 0.5)
                if (detecting_1_at_each_case_x_symmetric_rate[2] > 0.5) {
                    text_view_whether_letter_or_number_or_noise[image_index].setText("1");
                    return;
                }


        //        calculate rate for detecting 6
        float[] detecting_6_at_each_case_x_rate = new float[detecting_6_at_each_case_count_x_of_left_and_right.length];
        for (int i = 0; i < detecting_6_high_part_left_of_each_height_divided_by_24.length - 1; i++) {
            if ((detecting_6_high_part_left_of_each_height_divided_by_24[i] > detecting_6_high_part_left_of_each_height_divided_by_24[i + 1]) && (detecting_6_high_part_right_of_each_height_divided_by_24[i] > detecting_6_high_part_right_of_each_height_divided_by_24[i + 1]))
                detecting_6_at_each_case_count_x_of_left_and_right[0] += 1;
        }
        detecting_6_at_each_case_x_rate[0] = (float) detecting_6_at_each_case_count_x_of_left_and_right[0] / (detecting_6_high_part_left_of_each_height_divided_by_24.length - 1);
        for (int i = 0; i < detecting_6_low_part_left_of_each_height_divided_by_24.length - 1; i++) {
            if ((detecting_6_low_part_left_of_each_height_divided_by_24[i] > detecting_6_low_part_left_of_each_height_divided_by_24[i + 1]) && (detecting_6_low_part_right_of_each_height_divided_by_24[i] < detecting_6_low_part_right_of_each_height_divided_by_24[i + 1]))
                detecting_6_at_each_case_count_x_of_left_and_right[1] += 1;
        }
        detecting_6_at_each_case_x_rate[1] = (float) detecting_6_at_each_case_count_x_of_left_and_right[1] / (detecting_6_low_part_left_of_each_height_divided_by_24.length - 1);


        if (detecting_6_at_each_case_x_rate[0] > 0.5)
            if (detecting_6_at_each_case_x_rate[1] > 0.5)
//                if (detecting_6_low_part_left_of_each_height_divided_by_24[1] > detecting_6_low_part_left_of_each_height_divided_by_24[detecting_6_low_part_left_of_each_height_divided_by_24.length - 1])
                if (detecting_6_low_part_left_of_each_height_divided_by_24[1] < detecting_6_high_part_left_of_each_height_divided_by_24[0])
                    if(detecting_6_low_part_left_of_each_height_divided_by_24[0] < detecting_6_high_part_left_of_each_height_divided_by_24[0]) {
                        text_view_whether_letter_or_number_or_noise[image_index].setText("6");
//                        text_view_whether_letter_or_number_or_noise[image_index].setText(Integer.toString(detecting_6_low_part_left_of_each_height_divided_by_24[2]));
                        return;
                    }



        //        calculate rate for detecting 0
        float[] detecting_0_at_each_case_probability = new float[detecting_0_at_each_case.length];
        for (int i = 0; i < detecting_all_part_left_of_each_height_divided_by_24.length / 9; i++) {
            if ((detecting_all_part_right_of_each_height_divided_by_24[i + 1] - detecting_all_part_left_of_each_height_divided_by_24[i + 1]) > (detecting_all_part_right_of_each_height_divided_by_24[i] - detecting_all_part_left_of_each_height_divided_by_24[i]))
                detecting_0_at_each_case[0] += 1;
        }
        detecting_0_at_each_case_probability[0] = (float) detecting_0_at_each_case[0] / (detecting_all_part_left_of_each_height_divided_by_24.length / 9);
        for (int i = detecting_all_part_left_of_each_height_divided_by_24.length * 8 / 9; i < detecting_all_part_left_of_each_height_divided_by_24.length - 1; i++) {
            if ((detecting_all_part_right_of_each_height_divided_by_24[i + 1] - detecting_all_part_left_of_each_height_divided_by_24[i + 1]) < (detecting_all_part_right_of_each_height_divided_by_24[i] - detecting_all_part_left_of_each_height_divided_by_24[i]))
                detecting_0_at_each_case[1] += 1;
        }
        detecting_0_at_each_case_probability[1] = (float) detecting_0_at_each_case[1] / (detecting_all_part_left_of_each_height_divided_by_24.length / 9 - 1);
        for (int i = 0; i < detecting_all_part_top_of_each_width_divided_by_24.length / 9; i++) {
            if ((detecting_all_part_bottom_of_each_width_divided_by_24[i + 1] - detecting_all_part_top_of_each_width_divided_by_24[i + 1]) > (detecting_all_part_bottom_of_each_width_divided_by_24[i] - detecting_all_part_top_of_each_width_divided_by_24[i]))
                detecting_0_at_each_case[2] += 1;
        }
        detecting_0_at_each_case_probability[2] = (float) detecting_0_at_each_case[2] / (detecting_all_part_left_of_each_height_divided_by_24.length / 9);
        for (int i = detecting_all_part_top_of_each_width_divided_by_24.length * 8 / 9; i < detecting_all_part_top_of_each_width_divided_by_24.length - 1; i++) {
            if ((detecting_all_part_bottom_of_each_width_divided_by_24[i + 1] - detecting_all_part_top_of_each_width_divided_by_24[i + 1]) < (detecting_all_part_bottom_of_each_width_divided_by_24[i] - detecting_all_part_top_of_each_width_divided_by_24[i]))
                detecting_0_at_each_case[3] += 1;
        }
        detecting_0_at_each_case_probability[3] = (float) detecting_0_at_each_case[3] / (detecting_all_part_left_of_each_height_divided_by_24.length / 9 - 1);


        if (detecting_0_at_each_case_probability[0] > 0.5)
            if (detecting_0_at_each_case_probability[1] > 0.5)
                if (detecting_0_at_each_case_probability[2] > 0.5)
                    if (detecting_0_at_each_case_probability[3] > 0.5)
                    {
                        text_view_whether_letter_or_number_or_noise[image_index].setText("0");
                        return;
                    }




        //        calculate rate for detecting 3
        float[] detecting_3_at_each_case_probability = new float[detecting_3_at_each_case.length];
        for (int i = detecting_all_part_left_of_each_height_divided_by_24.length / 8; i < detecting_all_part_left_of_each_height_divided_by_24.length / 3; i++) {
            if ((detecting_all_part_right_of_each_height_divided_by_24[i] - detecting_all_part_right_of_each_height_divided_by_24[i + 1]) > 0)
                detecting_3_at_each_case[0] += 1;
        }
        detecting_3_at_each_case_probability[0] = (float) detecting_3_at_each_case[0] / (detecting_all_part_left_of_each_height_divided_by_24.length * 5 / 24);
        for (int i = detecting_all_part_left_of_each_height_divided_by_24.length * 1 / 8; i < detecting_all_part_left_of_each_height_divided_by_24.length * 3 / 8; i++) {
            if ((detecting_all_part_left_of_each_height_divided_by_24[i] - detecting_all_part_left_of_each_height_divided_by_24[i + 1]) > 0)
                detecting_3_at_each_case[1] += 1;
        }
        detecting_3_at_each_case_probability[1] = (float) detecting_3_at_each_case[1] / (detecting_all_part_left_of_each_height_divided_by_24.length * 2 / 8);


        if (detecting_3_at_each_case_probability[0] > 0.5)
            if (detecting_3_at_each_case_probability[1] > 0.5)
            {
                text_view_whether_letter_or_number_or_noise[image_index].setText("3");
                return;
            }



        //        calculate rate for detecting 5
//        float[] detecting_3_at_each_case_probability = new float[detecting_3_at_each_case.length];
//        for (int i = detecting_all_part_left_of_each_height_divided_by_24.length / 8; i < detecting_all_part_left_of_each_height_divided_by_24.length / 3; i++) {
//            if ((detecting_all_part_right_of_each_height_divided_by_24[i] - detecting_all_part_right_of_each_height_divided_by_24[i + 1]) > 0 && (detecting_all_part_left_of_each_height_divided_by_24[i] - detecting_all_part_left_of_each_height_divided_by_24[i + 1]) > 0)
//                detecting_3_at_each_case[0] += 1;
//        }
//        detecting_3_at_each_case_probability[0] = (float) detecting_3_at_each_case[0] / (detecting_all_part_left_of_each_height_divided_by_24.length * 5 / 24);
//
//
//        if (detecting_3_at_each_case_probability[0] > 0.5)
//        {
        text_view_whether_letter_or_number_or_noise[image_index].setText("5");
        return;
//        }







//        text_view_whether_letter_or_number_or_noise[image_index].setText(at_each_case_x_symmetric_rate_string);
//        text_view_whether_letter_or_number_or_noise[image_index].setText("1");

//                    text_view_whether_letter_or_number_or_noise[image_index].setText("1");


//        if((float) count_x_symmetric_of_left_and_right / height > 0.5)
//            if((float) count_y_symmetric_of_left_and_right / width > 0.5)
//                text_view_whether_letter_or_number_or_noise[image_index].setText("0");

//        text_view_whether_letter_or_number_or_noise[image_index].setText(Float.toString((float) count_x_symmetric_of_left_and_right / height));
//        text_view_whether_letter_or_number_or_noise[image_index].setText(x_symmetric_rate_string);
//        if((float) count_x_symmetric_of_left_and_right / height > 0.55)
//            text_view_whether_letter_or_number_or_noise[image_index].setText("1");
//        text_view_whether_letter_or_number_or_noise[image_index].setText(Float.toString((float) count_y_symmetric_of_left_and_right / width));
//        text_view_whether_letter_or_number_or_noise[image_index].setText(Integer.toString(count_x_symmetric_of_left_and_right));
//        text_view_whether_letter_or_number_or_noise[image_index].setText(Integer.toString(count_y_symmetric_of_left_and_right));
    }



    public void distinguish_vowels (final Bitmap before_bitmap_image, int image_index){

        int black = 0xff000000;
        int width = before_bitmap_image.getWidth();
        int height = before_bitmap_image.getHeight();

        int[] pixels_array = new int[width * height];
        Bitmap bitmap_sketch_book = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_4444);

        before_bitmap_image.getPixels(pixels_array, 0, width, 0, 0, width, height);

        if((float) width / height > 1.3){
            int count_dots_of_high_part = 0;
            int count_dots_of_low_part = 0;
            int index = 0;

            for(int y = 0; y < height / 2; y++){
                for(int x = 0; x < width; x++){
                    index = y * width + x;

                    if(pixels_array[index] == black)
                        count_dots_of_high_part += 1;
                }
            }
            for(int y = height / 2; y < height; y++){
                for(int x = 0; x < width; x++){
                    index = y * width + x;

                    if(pixels_array[index] == black)
                        count_dots_of_low_part += 1;
                }
            }

            if(count_dots_of_high_part > count_dots_of_low_part)
                text_view_whether_letter_or_number_or_noise[image_index].setText("ㅜ");
            else
                text_view_whether_letter_or_number_or_noise[image_index].setText("ㅗ");
        }
        else{
            int count_dots_of_right_part = 0;
            int count_dots_of_left_part = 0;
            int index = 0;

            for(int x = 0; x < width / 2; x++){
                for(int y = 0; y < height; y++){
                    index = y * width + x;

                    if(pixels_array[index] == black)
                        count_dots_of_left_part += 1;
                }
            }
            for(int x = width / 2; x < width; x++){
                for(int y = 0; y < height; y++){
                    index = y * width + x;

                    if(pixels_array[index] == black)
                        count_dots_of_right_part += 1;
                }
            }

            if(count_dots_of_right_part > count_dots_of_left_part * 13 / 10)
                text_view_whether_letter_or_number_or_noise[image_index].setText("ㅓ");
            else if(count_dots_of_left_part > count_dots_of_right_part * 13 / 10)
                text_view_whether_letter_or_number_or_noise[image_index].setText("ㅏ");
            else
                text_view_whether_letter_or_number_or_noise[image_index].setText("ㅐ");
        }


    }
}

