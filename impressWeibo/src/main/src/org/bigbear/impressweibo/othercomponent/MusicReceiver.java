package org.bigbear.impressweibo.othercomponent;

import org.bigbear.impressweibo.bean.android.MusicInfo;
import org.bigbear.impressweibo.support.debug.AppLogger;
import org.bigbear.impressweibo.support.lib.RecordOperationAppBroadcastReceiver;
import org.bigbear.impressweibo.support.utils.GlobalContext;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

/**
 * User: qii
 * Date: 14-2-5
 */
public class MusicReceiver extends RecordOperationAppBroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String artist = intent.getStringExtra("artist");
        String album = intent.getStringExtra("album");
        String track = intent.getStringExtra("track");
        if (!TextUtils.isEmpty(track)) {
            MusicInfo musicInfo = new MusicInfo();
            musicInfo.setArtist(artist);
            musicInfo.setAlbum(album);
            musicInfo.setTrack(track);
            AppLogger.d("Music" + artist + ":" + album + ":" + track);
            GlobalContext.getInstance().updateMusicInfo(musicInfo);
        }
    }
}
