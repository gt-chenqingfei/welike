package com.redefine.richtext.span;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.controller.AbstractDraweeController;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.interfaces.DraweeHierarchy;
import com.facebook.drawee.view.DraweeHolder;
import com.facebook.imagepipeline.image.ImageInfo;
import com.redefine.richtext.RichItem;
import com.redefine.richtext.block.Block;
import com.redefine.richtext.drawee.IDraweeDelegateView;

import java.io.File;
import java.lang.ref.WeakReference;

/**
 * Span that contains a Drawee.
 * <p>
 * <p>The containing view must also call {@link #onDetach(IDraweeDelegateView)} from its
 * {@link View#onStartTemporaryDetach()} and {@link View#onDetachedFromWindow()}
 * methods.
 * Similarly, it must call {@link #onAttach} from its
 * {@link View#onFinishTemporaryDetach()} and {@link View#onAttachedToWindow()}
 * methods.
 * <p>
 * {@see DraweeHolder}
 */
public class DraweeSpan extends RichImageSpan implements RichSpan {

    private final DraweeHolder mDraweeHolder;
    private final Block mBlock;
    private final OnRichItemClickListener mListener;
    private int mWidgetWidth;
    private IDraweeDelegateView mDraweeSpanView;

    private final DrawableCallback mDrawableCallback;
    private boolean isPressed;

    public DraweeSpan(Context context, Block b, int widgetWidth, OnRichItemClickListener mListener) {
        this(context, b, mListener);
        mWidgetWidth = widgetWidth;
    }

    public DraweeSpan(Context context, Block block, OnRichItemClickListener l) {
        this(block, initDrawableHolder(context, block), l);
    }

    public DraweeSpan(Block block,
                      DraweeHolder draweeHolder, OnRichItemClickListener l) {
        this(block, draweeHolder, l, ALIGN_BASELINE);
    }

    public DraweeSpan(Block block,
                      DraweeHolder draweeHolder, OnRichItemClickListener l,
                      @BetterImageSpanAlignment int verticalAlignment) {
        super(draweeHolder.getTopLevelDrawable(), verticalAlignment);
        mBlock = block;
        mListener = l;
        mDraweeHolder = draweeHolder;
        final DraweeController controller = mDraweeHolder.getController();
        Drawable topLevelDrawable = mDraweeHolder.getTopLevelDrawable();
        mDrawableCallback = new DrawableCallback(this);
        if (topLevelDrawable != null) {
            topLevelDrawable.setCallback(mDrawableCallback);
        }
        if (controller instanceof AbstractDraweeController) {
            ((AbstractDraweeController) controller).addControllerListener(new DraweeControllerListener(this));
        }
    }

    @Override
    public int getSize(Paint paint, CharSequence text, int start, int end, Paint.FontMetricsInt fontMetrics) {
        super.getSize(paint, text, start, end, fontMetrics);
        return mWidgetWidth;
    }

    private static boolean isHttp(String path) {
        return !TextUtils.isEmpty(path) && (path.startsWith("http") || path.startsWith("https"));
    }

    private static DraweeHolder initDrawableHolder(Context context, Block block) {
        Uri iconUri;
        String target = TextUtils.isEmpty(block.richItem.icon) ? block.richItem.source: block.richItem.icon;
        if (isHttp(target)) {
            iconUri = Uri.parse(target);
        } else {
            iconUri = Uri.fromFile(new File(target));
        }

        DraweeHierarchy draweeAnimatedHierarchy =
                GenericDraweeHierarchyBuilder.newInstance(context.getResources())
                        .setPlaceholderImage(new ColorDrawable(Color.GRAY))
                        .setActualImageScaleType(ScalingUtils.ScaleType.FIT_CENTER)
                        .build();
        DraweeController animatedController =
                Fresco.newDraweeControllerBuilder()
                        .setUri(iconUri)
                        .setAutoPlayAnimations(true)
                        .build();
        DraweeHolder draweeHolder = DraweeHolder.create(draweeAnimatedHierarchy, context);
        draweeHolder.setController(animatedController);
        return draweeHolder;
    }

    /**
     * Gets the drawee span ready to display the image.
     * <p>
     * <p>The containing view must call this method from both {@link View#onFinishTemporaryDetach()}
     * and {@link View#onAttachedToWindow()}.
     */
    public void onAttach(IDraweeDelegateView richDraweeSpanView) {
        mDraweeSpanView = richDraweeSpanView;
        refreshBound(mWidgetWidth, mBlock.richItem.width, mBlock.richItem.height);
        mDraweeHolder.onAttach();
    }

    private void refreshBound(int mWidgetWidth, int width, int height) {
        if (mWidgetWidth == 0 || width == 0 || height == 0) {
            return;
        }
        int realBoundWidth = width;
        int realBoundHeight = height;
        if (width >= mWidgetWidth * 3 / 5F) {
            realBoundWidth = mWidgetWidth;
            realBoundHeight = (int) (height * mWidgetWidth / (width * 1.0F));
        }
        Drawable topLevelDrawable = mDraweeHolder.getTopLevelDrawable();
        if (topLevelDrawable != null) {
            topLevelDrawable.setBounds(0, 0, realBoundWidth, realBoundHeight);
        }

    }

    /**
     * Checks whether the view that uses this holder is currently attached to a window.
     * <p>
     * {@see #onAttach()}
     * {@see #onDetach()}
     *
     * @return true if the holder is currently attached
     */
    public boolean isAttached() {
        return mDraweeHolder.isAttached();
    }

    /**
     * Releases resources used to display the image.
     * <p>
     * <p>The containing view must call this method from both {@link View#onStartTemporaryDetach()}
     * and {@link View#onDetachedFromWindow()}.
     */
    public void onDetach(IDraweeDelegateView richDraweeSpanView) {
        mDraweeHolder.onDetach();
    }

    /**
     * Get the underlying DraweeHolder.
     *
     * @return the DraweeHolder
     */
    public DraweeHolder getDraweeHolder() {
        return mDraweeHolder;
    }

    private IDraweeDelegateView getDraweeView() {
        return mDraweeSpanView;
    }

    @Override
    public Block getBlock() {
        return mBlock;
    }

    @Override
    public RichItem getRichItem() {
        return mBlock.richItem;
    }

    @Override
    public void onClick(View widget) {
        if (mBlock == null || mBlock.richItem == null) {
            return;
        }
        if (mListener != null) {
            mListener.onRichItemClick(mBlock.richItem);
        }
    }

    @Override
    public void setPressed(boolean b) {
        isPressed = b;
    }

    public static class DrawableCallback implements Drawable.Callback {


        private final WeakReference<DraweeSpan> mSpan;

        public DrawableCallback(DraweeSpan draweeSpan) {
            mSpan = new WeakReference<>(draweeSpan);
        }

        @Override
        public void invalidateDrawable(@NonNull Drawable who) {
            DraweeSpan span = mSpan.get();
            if (span != null && span.getDraweeView() != null) {
                span.getDraweeView().invalidate(who);
            }
        }

        @Override
        public void scheduleDrawable(@NonNull Drawable who, @NonNull Runnable what, long when) {
            final long delay = when - SystemClock.uptimeMillis();
            DraweeSpan span = mSpan.get();
            if (span != null && span.getDraweeView() != null) {
                span.getDraweeView().postDelayed(what, delay);
            }
        }

        @Override
        public void unscheduleDrawable(@NonNull Drawable who, @NonNull Runnable what) {
            DraweeSpan span = mSpan.get();
            if (span != null && span.getDraweeView() != null) {
                span.getDraweeView().removeCallbacks(what);
            }
        }
    }

    public static class DraweeControllerListener extends BaseControllerListener<ImageInfo> {

        private final WeakReference<DraweeSpan> mSpan;

        public DraweeControllerListener(DraweeSpan draweeSpan) {
            mSpan = new WeakReference<>(draweeSpan);
        }

        @Override
        public void onFinalImageSet(String id, ImageInfo imageInfo, Animatable animatable) {
            super.onFinalImageSet(id, imageInfo, animatable);
            DraweeSpan span = mSpan.get();
            if (span != null && span.getDraweeView() != null) {
                span.refreshBound(span.mWidgetWidth, imageInfo.getWidth(), imageInfo.getHeight());
                span.getDraweeView().invalidate(span.getDrawable());
            }
        }
    }
}
