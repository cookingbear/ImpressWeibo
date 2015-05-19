package org.bigbear.impressweibo.ui.loader;

import org.bigbear.impressweibo.bean.android.MentionTimeLineData;
import org.bigbear.impressweibo.support.database.MentionWeiboTimeLineDBTask;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

/**
 * User: qii
 * Date: 13-4-10
 */
public class MentionsWeiboTimeDBLoader extends AsyncTaskLoader<MentionTimeLineData> {

    private String accountId;
    private MentionTimeLineData result;

    public MentionsWeiboTimeDBLoader(Context context, String accountId) {
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

    public MentionTimeLineData loadInBackground() {
        result = MentionWeiboTimeLineDBTask.getRepostLineMsgList(accountId);
        return result;
    }
}
