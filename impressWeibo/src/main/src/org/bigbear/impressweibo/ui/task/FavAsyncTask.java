package org.bigbear.impressweibo.ui.task;

import org.bigbear.impressweibo.R;
import org.bigbear.impressweibo.bean.FavBean;
import org.bigbear.impressweibo.dao.fav.FavDao;
import org.bigbear.impressweibo.support.error.WeiboException;
import org.bigbear.impressweibo.support.lib.MyAsyncTask;
import org.bigbear.impressweibo.support.utils.GlobalContext;

import android.widget.Toast;

/**
 * User: qii
 * Date: 12-9-12
 */
public class FavAsyncTask extends MyAsyncTask<Void, FavBean, FavBean> {

    private String token;
    private String id;
    private WeiboException e;

    public FavAsyncTask(String token, String id) {
        this.token = token;
        this.id = id;
    }

    @Override
    protected FavBean doInBackground(Void... params) {
        FavDao dao = new FavDao(token, id);
        try {
            return dao.favIt();
        } catch (WeiboException e) {
            this.e = e;
            cancel(true);
            return null;
        }
    }

    @Override
    protected void onCancelled(FavBean favBean) {
        super.onCancelled(favBean);
        if (favBean == null && this.e != null) {
            Toast.makeText(GlobalContext.getInstance(), e.getError(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onPostExecute(FavBean favBean) {
        super.onPostExecute(favBean);
        if (favBean != null) {
            Toast.makeText(GlobalContext.getInstance(),
                    GlobalContext.getInstance().getString(R.string.fav_successfully),
                    Toast.LENGTH_SHORT).show();
        }
    }
}
