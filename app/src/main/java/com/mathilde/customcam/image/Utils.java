package com.mathilde.customcam.image;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.net.Uri;
import android.widget.Toast;

/**
 * Created by mathilde on 03/08/14.
 */
public class Utils {
    public static final String TAG = "Utils";

    public static Bitmap doBrightness(Bitmap src, int value) {
        Bitmap bmp = Bitmap.createBitmap(src.getWidth(), src.getHeight(),
                Bitmap.Config.ARGB_8888);
        int brightness = value;
        ColorMatrix cMatrix = new ColorMatrix();
        cMatrix.set(new float[] { 1, 0, 0, 0, brightness, 0, 1,
                0, 0, brightness,//
                0, 0, 1, 0, brightness, 0, 0, 0, 1, 0 });
        Paint paint = new Paint();
        paint.setColorFilter(new ColorMatrixColorFilter(cMatrix));
        Canvas canvas = new Canvas(bmp);
        canvas.drawBitmap(src, 0, 0, paint);
        return bmp;
    }

    public static Bitmap doContrast(Bitmap src, int value){
        Bitmap bmp = Bitmap.createBitmap(src.getWidth(), src.getHeight(),
                Bitmap.Config.ARGB_8888);
        int contrast = value;

        float scale = contrast + 1.f;
        float translate = (-.5f * scale + .5f) * 255.f;
        ColorMatrix cMatrix = new ColorMatrix();
        cMatrix.set(new float[] {
                scale, 0, 0, 0, translate,
                0, scale, 0, 0, translate,
                0, 0, scale, 0, translate,
                0, 0, 0, 1, 0});
        Paint paint = new Paint();
        paint.setColorFilter(new ColorMatrixColorFilter(cMatrix));
        Canvas canvas = new Canvas(bmp);
        canvas.drawBitmap(src, 0, 0, paint);
        return bmp;
    }

    public static Bitmap doMonochrome(Bitmap src){
        Bitmap bmpMonochrome = Bitmap.createBitmap(src.getWidth(), src.getHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bmpMonochrome);
        ColorMatrix ma = new ColorMatrix();
        ma.setSaturation(0);
        Paint paint = new Paint();
        paint.setColorFilter(new ColorMatrixColorFilter(ma));
        canvas.drawBitmap(src, 0, 0, paint);
        return bmpMonochrome;
    }

    public static Bitmap doSepia(Bitmap src){
        Bitmap bmpSepia = Bitmap.createBitmap(src.getWidth(), src.getHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bmpSepia);

        ColorMatrix ma = new ColorMatrix();
        ma.setSaturation(0);

        final ColorMatrix mb = new ColorMatrix();
        mb.setScale(1f, .95f, .82f, 1.0f);
        ma.setConcat(mb, ma);

        final ColorMatrixColorFilter filter = new ColorMatrixColorFilter(ma);
//        drawable.setColorFilter(filter);
        Paint paint = new Paint();
        paint.setColorFilter(new ColorMatrixColorFilter(ma));
        canvas.drawBitmap(src, 0, 0, paint);
        return bmpSepia;
    }


    public static Bitmap doNegative(Bitmap src){
        Bitmap bmpNegative = Bitmap.createBitmap(src.getWidth(), src.getHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bmpNegative);

        float[] colorMatrix_Negative = {
                -1.0f, 0, 0, 0, 255, //red
                0, -1.0f, 0, 0, 255, //green
                0, 0, -1.0f, 0, 255, //blue
                0, 0, 0, 1.0f, 0 //alpha
        };

        Paint MyPaint_Negative = new Paint();
        ColorFilter colorFilter_Negative = new ColorMatrixColorFilter(colorMatrix_Negative);
        MyPaint_Negative.setColorFilter(colorFilter_Negative);
        canvas.drawBitmap(src, 0, 0, MyPaint_Negative);
        return bmpNegative;
    }

    public static Bitmap doCustom(Bitmap src, int red, int green, int blue, int alpha){
        Bitmap bmpNegative = Bitmap.createBitmap(src.getWidth(), src.getHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bmpNegative);

        float[] colorMatrix_Negative = {
                -1.0f, 0, 0, 0, 255, //red
                0, -1.0f, 0, 0, 255, //green
                0, 0, -1.0f, 0, 255, //blue
                0, 0, 0, 1.0f, 0 //alpha
        };

        Paint MyPaint_Negative = new Paint();
        ColorFilter colorFilter_Negative = new ColorMatrixColorFilter(colorMatrix_Negative);
        MyPaint_Negative.setColorFilter(colorFilter_Negative);
        canvas.drawBitmap(src, 0, 0, MyPaint_Negative);
        return bmpNegative;
    }

    public static void performCrop(Uri picUri, Activity context, int code){
        try {
//call the standard crop action intent (the user device may not support it)
            Intent cropIntent = new Intent("com.android.camera.action.CROP");
            //indicate image type and Uri
            cropIntent.setDataAndType(picUri, "image/*");
            //set crop properties
            cropIntent.putExtra("crop", "true");
            //indicate aspect of desired crop
            cropIntent.putExtra("aspectX", 1);
            cropIntent.putExtra("aspectY", 1);
            //indicate output X and Y
            cropIntent.putExtra("outputX", 256);
            cropIntent.putExtra("outputY", 256);
            //retrieve data on return
            cropIntent.putExtra("return-data", true);
            //start the activity - we handle returning in onActivityResult
            context.startActivityForResult(cropIntent, code);
        }
        catch(ActivityNotFoundException anfe){
            //display an error message
            String errorMessage = "Whoops - your device doesn't support the crop action!";
            Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show();
        }
    }
}
