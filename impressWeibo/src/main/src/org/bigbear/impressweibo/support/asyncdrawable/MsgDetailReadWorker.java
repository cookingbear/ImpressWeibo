package org.bigbear.impressweibo.support.asyncdrawable;

import org.bigbear.impressweibo.bean.MessageBean;
import org.bigbear.impressweibo.support.debug.AppLogger;
import org.bigbear.impressweibo.support.file.FileDownloaderHttpHelper;
import org.bigbear.impressweibo.support.file.FileLocationMethod;
import org.bigbear.impressweibo.support.file.FileManager;
import org.bigbear.impressweibo.support.gallery.GalleryAnimationActivity;
import org.bigbear.impressweibo.support.imageutility.ImageUtility;
import org.bigbear.impressweibo.support.lib.AnimationRect;
import org.bigbear.impressweibo.support.lib.MyAsyncTask;
import org.bigbear.impressweibo.support.lib.WeiboDetailImageView;
import org.bigbear.impressweibo.support.settinghelper.SettingUtility;
import org.bigbear.impressweibo.support.utils.GlobalContext;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import java.util.ArrayList;

/**
 * User: qii
 * Date: 13-2-8
 * insert progress update listener into  download worker if it exists
 * or create a new download worker
 */
public class MsgDetailReadWorker extends MyAsyncTask<Void, Integer, String> {

    private WeiboDetailImageView view;
    private ProgressBar pb;
    private Button retry;

    private MessageBean msg;

    public MsgDetailReadWorker(WeiboDetailImageView view, MessageBean msg) {
        this.view = view;
        this.pb = this.view.getProgressBar();
        this.msg = msg;
        this.retry = view.getRetryButton();
        retry.setVisibility(View.INVISIBLE);

        String oriPath = FileManager
                .getFilePathFromUrl(msg.getOriginal_pic(), FileLocationMethod.picture_large);

        if (ImageUtility.isThisBitmapCanRead(oriPath)
                && TaskCache.isThisUrlTaskFinished(msg.getOriginal_pic())) {

            onPostExecute(oriPath);
            cancel(true);
            return;
        }

        String middlePath = FileManager
                .getFilePathFromUrl(msg.getBmiddle_pic(), FileLocationMethod.picture_bmiddle);

        if (ImageUtility.isThisBitmapCanRead(middlePath)
                && TaskCache.isThisUrlTaskFinished(msg.getBmiddle_pic())) {
            onPostExecute(middlePath);
            cancel(true);
            return;
        }

        pb.setVisibility(View.VISIBLE);
        pb.setIndeterminate(true);
    }

    public void setView(WeiboDetailImageView view) {
        this.view = view;
        this.pb = this.view.getProgressBar();
        this.retry = view.getRetryButton();
        retry.setVisibility(View.INVISIBLE);
    }

    @Override
    protected String doInBackground(Void... arg) {
    	AppLogger.d("doinbackground");
        if (isCancelled()) {
            return null;
        }

        if (SettingUtility.getEnableBigPic()) {
            boolean result = TaskCache
                    .waitForPictureDownload(msg.getOriginal_pic(), downloadListener,
                            FileManager.generateDownloadFileName(msg.getOriginal_pic()),
                            FileLocationMethod.picture_large);

            return result ? FileManager
                    .getFilePathFromUrl(msg.getOriginal_pic(), FileLocationMethod.picture_large)
                    : null;
        } else {
            boolean result = TaskCache
                    .waitForPictureDownload(msg.getBmiddle_pic(), downloadListener,
                            FileManager.generateDownloadFileName(msg.getBmiddle_pic()),
                            FileLocationMethod.picture_bmiddle);

            return result ? FileManager
                    .getFilePathFromUrl(msg.getBmiddle_pic(), FileLocationMethod.picture_bmiddle)
                    : null;
        }
    }

    FileDownloaderHttpHelper.DownloadListener downloadListener
            = new FileDownloaderHttpHelper.DownloadListener() {

        @Override
        public void pushProgress(int progress, int max) {
            onProgressUpdate(progress, max);
        }
    };

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        if (this.getStatus() == Status.RUNNING) {
            pb.setVisibility(View.VISIBLE);
            pb.setIndeterminate(false);

            Integer progress = values[0];
            Integer max = values[1];

            pb.setMax(max);
            pb.setProgress(progress);
        }
    }

    @Override
    protected void onCancelled(String bitmap) {
        pb.setVisibility(View.INVISIBLE);
        super.onCancelled(bitmap);
    }

    @Override
    protected void onPostExecute(String path) {
    	AppLogger.d("msgdetail onpostExecute");
        retry.setVisibility(View.INVISIBLE);
        pb.setIndeterminate(true);

        if (!TextUtils.isEmpty(path)) {
            AppLogger.d("is not empty");
            if (!path.endsWith(".gif")) {
                readNormalPic(path);
            } else {
                view.setGif(path);
            }
            pb.setVisibility(View.INVISIBLE);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    AnimationRect rect = AnimationRect.buildFromImageView(view.getImageView());
                    ArrayList<AnimationRect> animationRectArrayList
                            = new ArrayList<AnimationRect>();
                    animationRectArrayList.add(rect);
                    Intent intent = GalleryAnimationActivity
                            .newIntent(msg, animationRectArrayList, 0);
                    GlobalContext.getInstance().getActivity().startActivity(intent);
                }
            });
        } else {
            AppLogger.d("is empty");
            pb.setVisibility(View.INVISIBLE);
            view.setImageDrawable(new ColorDrawable(Color.TRANSPARENT));
            retry.setVisibility(View.VISIBLE);
            retry.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MsgDetailReadWorker picTask = new MsgDetailReadWorker(view, msg);
                    picTask.executeOnExecutor(MyAsyncTask.THREAD_POOL_EXECUTOR);
                }
            });
        }
    }

    private void readNormalPic(String path) {

        Bitmap bitmap = ImageUtility.readNormalPic(path, 2000, 2000);

        view.setTag(true);
        view.getImageView().setTag(true);
        view.setVisibility(View.VISIBLE);
        view.setImageBitmap(bitmap);
        view.setAlpha(0.0f);
        view.animate().alpha(1.0f).setDuration(200);
    }
}