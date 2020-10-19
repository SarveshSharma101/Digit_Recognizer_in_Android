package com.example.dr2;

import android.graphics.Path;

public class FingerPath {

    public int color;
    public int strokewidth;
    public Path path;

    public FingerPath(int color, int strokewidth, Path path) {
        this.color = color;
        this.strokewidth = strokewidth;
        this.path = path;
    }
}
