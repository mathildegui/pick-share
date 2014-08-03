package com.mathilde.customcam.image;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;

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
}
