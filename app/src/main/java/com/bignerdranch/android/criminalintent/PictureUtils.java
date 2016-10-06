package com.bignerdranch.android.criminalintent;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Point;

/**
 * Created by sredorta on 10/6/2016.
 */
public class PictureUtils {
    public static Bitmap getScaledBitmap(String path, int destWidth, int destHeight) {
        //Read the actual dimensions of the Bitmap
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);

        float srcWidth = options.outWidth;
        float srcHeight = options.outHeight;

        //Determine reduction
        int inSampleSize = 1;
        if ((srcHeight > destHeight) || (srcWidth > destWidth)) {
            inSampleSize = Math.round(srcHeight/destHeight);
        } else {
            inSampleSize = Math.round(srcWidth/destWidth);
        }
        options = new BitmapFactory.Options();
        options.inSampleSize = inSampleSize;

        //Reread the bitmap with right resolution
        return BitmapFactory.decodeFile(path, options);
    }
    // Generated scaled bitmap when we don't know destination size (define size as activity window size)
    public static Bitmap getScaledBitmap(String path, Activity activity) {
        Point size = new Point();
        activity.getWindowManager().getDefaultDisplay().getSize(size);
        return getScaledBitmap(path, size.x, size.y);
    }
}
