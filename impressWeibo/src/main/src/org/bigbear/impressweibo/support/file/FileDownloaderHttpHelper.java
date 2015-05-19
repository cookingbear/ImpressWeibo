package org.bigbear.impressweibo.support.file;

import org.bigbear.impressweibo.support.debug.AppLogger;

public class FileDownloaderHttpHelper {
    public static class DownloadListener {
        public void pushProgress(int progress, int max) {
        	AppLogger.d("orign pushprogress");
        }

        public void completed() {
        }

        public void cancel() {
        }
    }
}
