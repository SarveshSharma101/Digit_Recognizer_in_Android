package com.example.dr2;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class MainActivity extends AppCompatActivity {

    private LinearLayout ll;
    private PaintView paintView;
    private Button clear, predict;
    private TextView prediction;
    private static Bitmap mBitmap;
    private static int[] pid={R.id.p0,R.id.p1,R.id.p2,R.id.p3,R.id.p4,R.id.p5,R.id.p6,R.id.p7,R.id.p8,R.id.p9};
    private static int i=-1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ll = findViewById(R.id.ll);
        paintView = findViewById(R.id.draw);
        clear = findViewById(R.id.clear);
        predict = findViewById(R.id.predict);

        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.e("LOg",String.valueOf(i));
                if(i!=-1){
                    Log.e("LOg","-->"+String.valueOf(i));
                    View view= findViewById(pid[i]);
                    float desiredPercentage = 1f;
                    TranslateAnimation anim = new TranslateAnimation(Animation.RELATIVE_TO_PARENT, 0,
                            Animation.RELATIVE_TO_PARENT, 0, Animation.RELATIVE_TO_PARENT,
                            0.2f, Animation.RELATIVE_TO_PARENT, desiredPercentage);
                    anim.setDuration(1100);
                    anim.setFillAfter(true);
                    view.startAnimation(anim);
                    view.setVisibility(View.INVISIBLE);
                    i=-1;
                }
                paintView.clear();
            }
        });

        predict.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int imgSize = Math.min(paintView.getmBitmap().getWidth(), paintView.getmBitmap().getHeight());
                Bitmap centerCroppedBitmap = centerCrop(paintView.getmBitmap());
                if(centerCroppedBitmap!= null) {
                    mBitmap = Bitmap.createScaledBitmap(centerCroppedBitmap, 28, 28, true);


                    DigitDetector digitDetector = new DigitDetector(MainActivity.this);
                    i = digitDetector.detectDigit(mBitmap);

                    if(i==-1){
                        Toast.makeText(MainActivity.this,"Please draw again!!!", Toast.LENGTH_SHORT).show();
                    }else {
                        Animate(findViewById(pid[i]));
                    }
                }
            }
        });
    }

    private void Animate(View v) {
        v.setBackground(getDrawable(R.drawable.pred_bg1));
        float desiredPercentage = 0.08f;
        TranslateAnimation anim = new TranslateAnimation(Animation.RELATIVE_TO_PARENT, 0,
                Animation.RELATIVE_TO_PARENT, 0, Animation.RELATIVE_TO_PARENT,
                1, Animation.RELATIVE_TO_PARENT, desiredPercentage);
        anim.setDuration(1050);
        anim.setFillAfter(true);
        v.startAnimation(anim);
        v.setVisibility(View.VISIBLE);
    }

    public static Bitmap centerCrop(Bitmap srcBmp){
        Bitmap dstBmp = null;
        if (srcBmp.getWidth() >= srcBmp.getHeight()){

            dstBmp = Bitmap.createBitmap(
                    srcBmp,
                    srcBmp.getWidth()/2 - srcBmp.getHeight()/2,
                    0,
                    srcBmp.getHeight(),
                    srcBmp.getHeight()
            );

        }else{

            dstBmp = Bitmap.createBitmap(
                    srcBmp,
                    0,
                    srcBmp.getHeight()/2 - srcBmp.getWidth()/2,
                    srcBmp.getWidth(),
                    srcBmp.getWidth()
            );
        }
        return dstBmp;
    }
}