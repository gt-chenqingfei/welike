package com.redefine.commonui.util;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.media.ThumbnailUtils;

/**
 * Created by mengnan on 2018/4/25.
 **/
public class RoundBitmapUtils {
        public static Bitmap createCircleImage(Bitmap source, int min) {
                Bitmap resizeBmp = ThumbnailUtils.extractThumbnail(source, 200, 200);

                final Paint paint = new Paint();

        paint.setAntiAlias(true);

        Bitmap target = Bitmap.createBitmap(min, min, Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(target);

        canvas.drawCircle(min / 2, min / 2, min / 2, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));

        canvas.drawBitmap(resizeBmp, 0, 0, paint);

        return target;
    }

}
