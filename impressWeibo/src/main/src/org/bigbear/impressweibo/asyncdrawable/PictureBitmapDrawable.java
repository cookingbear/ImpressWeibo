package org.bigbear.impressweibo.asyncdrawable;

import org.bigbear.impressweibo.R;
import org.bigbear.impressweibo.support.utils.ThemeUtility;

import android.graphics.drawable.ColorDrawable;

import java.lang.ref.WeakReference;

/**
 * User: qii
 * Date: 12-9-5
 */
public class PictureBitmapDrawable extends ColorDrawable {
    private final WeakReference<IPictureWorker> bitmapDownloaderTaskReference;

    public PictureBitmapDrawable(IPictureWorker bitmapDownloaderTask) {
        super(ThemeUtility.getColor(R.attr.listview_pic_bg));
        bitmapDownloaderTaskReference =
                new WeakReference<IPictureWorker>(bitmapDownloaderTask);
    }

    public IPictureWorker getBitmapDownloaderTask() {
        return bitmapDownloaderTaskReference.get();
    }
}
