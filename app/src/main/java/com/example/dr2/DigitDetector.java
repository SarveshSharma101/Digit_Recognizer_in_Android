package com.example.dr2;

import android.app.Activity;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.util.Log;

import org.tensorflow.lite.Interpreter;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

public class DigitDetector {
    private static final String MODEL_PATH = "model.tflite";
    private Interpreter tflite;
    private float[][] mnistOutput = null;
    private ByteBuffer inputBuffer = null;

    // Specify the output size
    private static final int NUMBER_LENGTH = 10;

    // Specify the input size
    private static final int DIM_BATCH_SIZE = 1;
    private static final int DIM_IMG_SIZE_X = 28;
    private static final int DIM_IMG_SIZE_Y = 28;
    private static final int DIM_PIXEL_SIZE = 1;

    // Number of bytes to hold a float (32 bits / float) / (8 bits / byte) = 4 bytes / float
    private static final int BYTE_SIZE_OF_FLOAT = 4;

    public DigitDetector(Activity activity) {

        try{
            tflite = new Interpreter(loadModel(activity));
            inputBuffer =
                    ByteBuffer.allocateDirect(
                            BYTE_SIZE_OF_FLOAT * DIM_BATCH_SIZE * DIM_IMG_SIZE_X * DIM_IMG_SIZE_Y * DIM_PIXEL_SIZE);
            inputBuffer.order(ByteOrder.nativeOrder());
            mnistOutput = new float[DIM_BATCH_SIZE][NUMBER_LENGTH];
        }catch (Exception e){
            Log.e("Tensorflow","IOEXception ");
        }
    }

    private MappedByteBuffer loadModel(Activity activity) throws IOException {
        AssetFileDescriptor fileDescriptor = activity.getAssets().openFd(MODEL_PATH);
        FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel = inputStream.getChannel();
        long startOffset = fileDescriptor.getStartOffset();
        long declaredLength = fileDescriptor.getDeclaredLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
    }
    public int detectDigit(Bitmap bitmap) {
        preprocess(bitmap);
        runInference();
        int predictedNumber = postprocess();
        return predictedNumber;
    }

    private void preprocess(Bitmap bitmap) {
        int width=28,height=28;
        int[] pixels = new int[width * height];

        // Load bitmap pixels into the temporary pixels variable
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height);

        for (int i = 0; i < pixels.length; ++i) {
            // Set 0 for white and 255 for black pixels
            int pixel = pixels[i];
            int channel = pixel & 0xff;
            inputBuffer.putFloat(0xff - channel);
        }



    }

    protected void runInference() {
        tflite.run(inputBuffer, mnistOutput);
    }
    private int postprocess() {
        for (int i = 0; i < mnistOutput[0].length; i++) {
            float value = mnistOutput[0][i];
            Log.d("Tensorflow", "Output for " + Integer.toString(i) + ": " + Float.toString(value));
//             Check if this number is the one we care about. If yes, return the index
            if (value == 1f) {
                return i;
            }
        }
        return -1;
    }


}
