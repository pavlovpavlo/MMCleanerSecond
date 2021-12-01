package com.agento.mmcleaner.util;

import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.ProgressBar;
import android.widget.TextView;

import cn.septenary.ui.widget.GradientProgressBar;

public class GradientProgressBarAnimation extends Animation {
    private GradientProgressBar progressBar;
    private TextView progress;
    private float from;
    private float to;

    public GradientProgressBarAnimation(GradientProgressBar progressBar, float from, float to) {
        super();
        this.progressBar = progressBar;
        this.from = from;
        this.to = to;
    }

    public void setProgress(TextView progress) {
        this.progress = progress;
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {
        super.applyTransformation(interpolatedTime, t);
        float value = from + (to - from) * interpolatedTime;
        progressBar.setProgress((int) value, false);
        progress.setText(""+(int) value);
    }

}
