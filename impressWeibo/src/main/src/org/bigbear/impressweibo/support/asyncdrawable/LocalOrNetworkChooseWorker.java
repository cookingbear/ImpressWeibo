package org.bigbear.impressweibo.support.asyncdrawable;

import org.bigbear.impressweibo.support.database.DownloadPicturesDBTask;
import org.bigbear.impressweibo.support.debug.AppLogger;
import org.bigbear.impressweibo.support.file.FileLocationMethod;
import org.bigbear.impressweibo.support.file.FileManager;
import org.bigbear.impressweibo.support.imageutility.ImageUtility;

import pl.droidsonroids.gif.GifDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.widget.ImageView;

import java.io.IOException;
import java.lang.ref.WeakReference;

/**
 * User: qii
 * Date: 14-6-10
 */
public class LocalOrNetworkChooseWorker extends AbstractWorker<String, Integer, Boolean> {

    private String data = "";
    private boolean isMultiPictures = false;

    private WeakReference<ImageView> viewWeakReference;

    private FileLocationMethod method;

    private IWeiciyuanDrawable IWeiciyuanDrawable;

    public String getUrl() {
        return data;
    }

    public LocalOrNetworkChooseWorker(ImageView view, String url, FileLocationMethod method,
            boolean isMultiPictures) {

        this.viewWeakReference = new WeakReference<ImageView>(view);
        this.data = url;
        this.method = method;
        this.isMultiPictures = isMultiPictures;
    }

    public LocalOrNetworkChooseWorker(IWeiciyuanDrawable view, String url,
            FileLocationMethod method,
            boolean isMultiPictures) {

        this(view.getImageView(), url, method, false);
        this.IWeiciyuanDrawable = view;
        this.isMultiPictures = isMultiPictures;
    }

    @Override
    protected Boolean doInBackground(String... url) {
        String path = FileManager.getFilePathFromUrl(data, method);
        return ImageUtility.isThisBitmapCanRead(path) && TaskCache.isThisUrlTaskFinished(data);
    }

    @Override
    protected void onCancelled(Boolean aBoolean) {
        super.onCancelled(aBoolean);
        ImageView imageView = viewWeakReference.get();
        if (!isMySelf(imageView)) {
            return;
        }

        imageView.setImageDrawable(
                new ColorDrawable(DebugColor.CHOOSE_CANCEL));
    }
    private void setGifDrawableIfTrue(ImageView view,Drawable drawable) {
        String path=null;
        AppLogger.d("isthispicturegif");
        while (TextUtils.isEmpty(path=DownloadPicturesDBTask.get(this.data)))
			try {
					Thread.sleep(5);
			} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
			}
	    try {
				
			drawable=new GifDrawable(path);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        AppLogger.d("setImageDrawable");
        view.setImageDrawable(drawable);
    }
    @Override
    protected void onPostExecute(Boolean result) {
        super.onPostExecute(result);

        ImageView imageView = viewWeakReference.get();
        PictureBitmapDrawable downloadedDrawable =null;
        if (!isMySelf(imageView)) {
            return;
        }

        if (result) {
            LocalWorker newTask = null;

            if (IWeiciyuanDrawable != null) {
                newTask = new LocalWorker(IWeiciyuanDrawable, getUrl(), method,
                        isMultiPictures);
                downloadedDrawable = new PictureBitmapDrawable(newTask);
                IWeiciyuanDrawable.setImageDrawable(downloadedDrawable);
            } else {
                newTask = new LocalWorker(imageView, getUrl(), method, isMultiPictures);
                downloadedDrawable = new PictureBitmapDrawable(newTask);
                imageView.setImageDrawable(downloadedDrawable);
            }

            newTask.executeOnIO();
            if (ImageUtility.isThisPictureGif(this.data))
                setGifDrawableIfTrue(imageView,downloadedDrawable);
        } else {

            ReadWorker newTask = null;

            if (IWeiciyuanDrawable != null) {
                newTask = new ReadWorker(IWeiciyuanDrawable, getUrl(), method,
                        isMultiPictures);
                downloadedDrawable = new PictureBitmapDrawable(newTask);
                IWeiciyuanDrawable.setImageDrawable(downloadedDrawable);
            } else {
                newTask = new ReadWorker(imageView, getUrl(), method, isMultiPictures);
                downloadedDrawable = new PictureBitmapDrawable(newTask);
                imageView.setImageDrawable(downloadedDrawable);
            }
            newTask.executeOnWaitNetwork();
            if (ImageUtility.isThisPictureGif(this.data)) 
                setGifDrawableIfTrue(imageView,downloadedDrawable);
        }
    }
}

