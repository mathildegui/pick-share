package com.mathilde.customcam.camera;

/**
 * Created by mathilde on 28/07/14.
 */

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;

/**
 * Created by gui on 24/07/2014.
 */
public class GridLines extends View {
    private static final String TAG = "GridLines";

    public GridLines(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
    }

    public GridLines(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
    }

    public GridLines(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        // TODO Auto-generated constructor stu
    }


    //Display grid line from the camera
    @Override
    protected void onDraw(Canvas canvas) {
        // TODO Auto-generated method stub
        super.onDraw(canvas);
        DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
        int screenWidth = metrics.widthPixels;
        int screenHeight = (int) (metrics.heightPixels * 0.9);
        Paint paint = new Paint();
        //  Set paint options
        paint.setAntiAlias(true);
        paint.setStrokeWidth(3);
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.argb(255, 255, 255, 255));

        canvas.drawLine((screenWidth / 3) * 2, 0, (screenWidth / 3) * 2, screenHeight, paint);
        canvas.drawLine((screenWidth / 3), 0, (screenWidth / 3), screenHeight, paint);
        canvas.drawLine(0, (screenHeight / 3) * 2, screenWidth, (screenHeight / 3) * 2, paint);
        canvas.drawLine(0, (screenHeight / 3), screenWidth, (screenHeight / 3), paint);
    }
}

