package com.example.dr2;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;

import androidx.annotation.Nullable;

public class PaintView extends View {

    public static int BRUSH_SIZE = 40;
    public static final int BG_COLOR = Color.WHITE;
    public static final int LINE_COLOR = Color.BLACK;
    public static final float TOUCH_TOLERANCE =4;
    public float mx, my;
    public  Paint paint;
    public  Path path;
    public  ArrayList<FingerPath> fps = new ArrayList<>();
    private Bitmap mBitmap;
    private Canvas mCanvas;
    private Paint mBitmapPaint = new Paint(Paint.DITHER_FLAG);



    public PaintView(Context context) {
        this(context,null);
    }

    public PaintView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setDither(true);
        paint.setColor(LINE_COLOR);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setXfermode(null);
        paint.setAlpha(0xff);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        mBitmap =Bitmap.createBitmap(w,h,Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas(mBitmap);

    }
    @Override
    protected void onDraw(Canvas canvas) {
        canvas.save();
        mCanvas.drawColor(BG_COLOR);

        for(FingerPath fp : fps){
            paint.setColor(fp.color);
            paint.setMaskFilter(null);
            paint.setStrokeWidth(fp.strokewidth);

            mCanvas.drawPath(fp.path, paint);
        }

        canvas.drawBitmap(mBitmap,0,0,mBitmapPaint);

        canvas.restore();

    }
    private void touchStart(float x, float y){
        path = new Path();
        FingerPath fp = new FingerPath(LINE_COLOR,BRUSH_SIZE,path);
        fps.add(fp);

        path.reset();
        path.moveTo(x, y);
        mx = x;
        my = y;

    }


    private void touchMove(float x, float y){
        float dx = Math.abs(x-mx);
        float dy = Math.abs(y-my);

        if(dx>=TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE){
            path.quadTo(mx, my, (x+mx)/2,(y+my)/2);
            mx= x;
            my = y;
        }


    }

    private void touchUp(){
        path.lineTo(mx,my);
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                touchStart(x,y);
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                touchMove(x,y);
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                touchUp();
                invalidate();
                break;
        }

        return true;
    }

    public void clear() {
        fps.clear();
        invalidate();
    }

    public Bitmap getmBitmap(){
        return mBitmap;
    }

}
