package com.redefine.commonui.widget.progress;

/**
 * Created by honglin on 2018/6/28.
 */

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ProgressBar;

import com.redefine.commonui.R;

public class CircularProgressBar extends ProgressBar {


    private int color;
    private float strokeWidth;
    private float sweepSpeed;
    private float rotationSpeed;
    private int colorsId;
    private int minSweepAngle;
    private int maxSweepAngle;
    private boolean capRound;
    private int[] colors;


    public CircularProgressBar(Context context) {
        this(context, null);
    }

    public CircularProgressBar(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.cpbStyle);
    }

    public CircularProgressBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        if (isInEditMode()) {
            setIndeterminateDrawable(new CircularProgressDrawable.Builder(context).build());
            return;
        }

        Resources res = context.getResources();
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CircularProgressBar, defStyle, 0);
        color = a.getColor(R.styleable.CircularProgressBar_cpb_color, res.getColor(R.color.cpb_default_color));
        strokeWidth = a.getDimension(R.styleable.CircularProgressBar_cpb_stroke_width, res.getDimension(R.dimen.cpb_default_stroke_width));
        sweepSpeed = a.getFloat(R.styleable.CircularProgressBar_cpb_sweep_speed, Float.parseFloat(res.getString(R.string.cpb_default_sweep_speed)));
        rotationSpeed = a.getFloat(R.styleable.CircularProgressBar_cpb_rotation_speed, Float.parseFloat(res.getString(R.string.cpb_default_rotation_speed)));
        colorsId = a.getResourceId(R.styleable.CircularProgressBar_cpb_colors, 0);
        minSweepAngle = a.getInteger(R.styleable.CircularProgressBar_cpb_min_sweep_angle, res.getInteger(R.integer.cpb_default_min_sweep_angle));
        maxSweepAngle = a.getInteger(R.styleable.CircularProgressBar_cpb_max_sweep_angle, res.getInteger(R.integer.cpb_default_max_sweep_angle));
        capRound = a.getBoolean(R.styleable.CircularProgressBar_cpb_cap_round, false);
        a.recycle();

        //colors
        if (colorsId != 0) {
            colors = res.getIntArray(colorsId);
        }

        build();
    }

    private void build() {
        Drawable indeterminateDrawable;
        CircularProgressDrawable.Builder builder = new CircularProgressDrawable.Builder(getContext())
                .sweepSpeed(sweepSpeed)
                .rotationSpeed(rotationSpeed)
                .strokeWidth(strokeWidth)
                .minSweepAngle(minSweepAngle)
                .maxSweepAngle(maxSweepAngle).style(capRound ? CircularProgressDrawable.Style.ROUNDED : CircularProgressDrawable.Style.NORMAL);

        if (colors != null && colors.length > 0)
            builder.colors(colors);
        else
            builder.color(color);

        indeterminateDrawable = builder.build();
        indeterminateDrawable.setBounds(0, 0
                , Math.max(indeterminateDrawable.getIntrinsicWidth(), getMeasuredWidth())
                , Math.max(indeterminateDrawable.getIntrinsicHeight(), getMeasuredHeight()));
        setIndeterminateDrawable(indeterminateDrawable);
    }

    private CircularProgressDrawable checkIndeterminateDrawable() {
        Drawable ret = getIndeterminateDrawable();
        if (ret == null || !(ret instanceof CircularProgressDrawable))
            throw new RuntimeException("The drawable is not a CircularProgressDrawable");
        return (CircularProgressDrawable) ret;
    }

    public void progressiveStop() {
        checkIndeterminateDrawable().progressiveStop();
    }

    public void progressiveStop(CircularProgressDrawable.OnEndListener listener) {
        checkIndeterminateDrawable().progressiveStop(listener);
    }

    public void setColor(int color) {
        this.color = color;
    }

    public void setStrokeWidth(float strokeWidth) {
        this.strokeWidth = strokeWidth;
    }

    public void setSweepSpeed(float sweepSpeed) {
        this.sweepSpeed = sweepSpeed;
    }

    public void setRotationSpeed(float rotationSpeed) {
        this.rotationSpeed = rotationSpeed;
    }

    public void setColorsId(int colorsId) {
        this.colorsId = colorsId;
    }

    public void setMinSweepAngle(int minSweepAngle) {
        this.minSweepAngle = minSweepAngle;
    }

    public void setMaxSweepAngle(int maxSweepAngle) {
        this.maxSweepAngle = maxSweepAngle;
    }

    public void setCapRound(boolean capRound) {
        this.capRound = capRound;
    }

    public void setIndeterminateEnble(boolean enble) {
        if (!enble) clearAnimation();
    }

    public void start() {

        build();

    }


}
