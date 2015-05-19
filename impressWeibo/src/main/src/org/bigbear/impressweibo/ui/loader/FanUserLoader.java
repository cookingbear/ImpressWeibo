package org.bigbear.impressweibo.ui.loader;

import org.bigbear.impressweibo.bean.UserListBean;
import org.bigbear.impressweibo.dao.user.FanListDao;
import org.bigbear.impressweibo.support.error.WeiboException;

import android.content.Context;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * User: qii
 * Date: 13-5-12
 */
public class FanUserLoader extends AbstractAsyncNetRequestTaskLoader<UserListBean> {

    private static Lock lock = new ReentrantLock();

    private String token;
    private String uid;
    private String cursor;

    public FanUserLoader(Context context, String token, String uid, String cursor) {
        super(context);
        this.token = token;
        this.uid = uid;
        this.cursor = cursor;
    }

    public UserListBean loadData() throws WeiboException {
        FanListDao dao = new FanListDao(token, uid);
        dao.setCursor(cursor);

        UserListBean result = null;
        lock.lock();

        try {
            result = dao.getGSONMsgList();
        } finally {
            lock.unlock();
        }
        return result;
    }
}

