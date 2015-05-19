package org.bigbear.impressweibo.ui.loader;

import org.bigbear.impressweibo.bean.android.CommentTimeLineData;
import org.bigbear.impressweibo.support.database.CommentToMeTimeLineDBTask;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

/**
 * User: qii
 * Date: 13-4-10
 */
public class CommentsToMeDBLoader extends AsyncTaskLoader<CommentTimeLineData> {

    private String accountId;
    private CommentTimeLineData result;

    public CommentsToMeDBLoader(Context context, String accountId) {
        super(context);
        this.accountId = accountId;
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        if (result == null) {
            forceLoad();
        } else {
            deliverResult(result);
        }
    }

    public CommentTimeLineData loadInBackground() {
        result = CommentToMeTimeLineDBTask.getCommentLineMsgList(accountId);
        return result;
    }
}