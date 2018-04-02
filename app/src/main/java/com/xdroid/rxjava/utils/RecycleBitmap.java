package com.xdroid.rxjava.utils;


import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

/**
 * 类描述:回收ImageView占用的图像内存
 * 参考内容:http://blog.csdn.net/intbird/article/details/19905549
 */

public class RecycleBitmap {
    private RecycleBitmap() {
    }

    /**
     * 回收ImageView占用的图像内存;
     *
     * @param imageView
     */
    public static void recycleImageView(ImageView imageView) {
        if (imageView == null) {
            return;
        }

        Drawable drawable = imageView.getDrawable();
        if (drawable != null && drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            Bitmap bitmap = bitmapDrawable.getBitmap();
            if (bitmap != null && !bitmap.isRecycled()) {
                bitmap.recycle();
                bitmap = null;
                imageView.setImageBitmap(null);
            }
        }
    }
}