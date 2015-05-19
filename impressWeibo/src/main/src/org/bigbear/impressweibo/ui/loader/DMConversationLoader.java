package org.bigbear.impressweibo.ui.loader;

import org.bigbear.impressweibo.bean.DMListBean;
import org.bigbear.impressweibo.dao.dm.DMConversationDao;
import org.bigbear.impressweibo.support.error.WeiboException;

import android.content.Context;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * User: qii
 * Date: 13-5-15
 */
public class DMConversationLoader extends AbstractAsyncNetRequestTaskLoader<DMListBean> {

    private static Lock lock = new ReentrantLock();

    private String token;
    private String uid;
    private String page;

    public DMConversationLoader(Context context, String token, String uid, String page) {
        super(context);
        this.token = token;
        this.uid = uid;
        this.page = page;
    }

    public DMListBean loadData() throws WeiboException {
        DMConversationDao dao = new DMConversationDao(token);
        dao.setPage(Integer.valueOf(page));
        dao.setUid(uid);

        DMListBean result = null;
        lock.lock();

        try {
            result = dao.getConversationList();
        } finally {
            lock.unlock();
        }
        return result;
    }
}
