package org.bigbear.impressweibo.dao.search;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import org.bigbear.impressweibo.bean.MessageBean;
import org.bigbear.impressweibo.bean.SearchStatusListBean;
import org.bigbear.impressweibo.bean.UserListBean;
import org.bigbear.impressweibo.dao.URLHelper;
import org.bigbear.impressweibo.support.debug.AppLogger;
import org.bigbear.impressweibo.support.error.WeiboException;
import org.bigbear.impressweibo.support.http.HttpMethod;
import org.bigbear.impressweibo.support.http.HttpUtility;
import org.bigbear.impressweibo.support.settinghelper.SettingUtility;
import org.bigbear.impressweibo.support.utils.TimeUtility;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * User: qii
 * Date: 12-9-8
 */
public class SearchDao {
    public UserListBean getUserList() throws WeiboException {
        String url = URLHelper.USERS_SEARCH;

        Map<String, String> map = new HashMap<String, String>();
        map.put("access_token", access_token);
        map.put("count", count);
        map.put("page", page);
        map.put("q", q);

        String jsonData = null;

        jsonData = HttpUtility.getInstance().executeNormalTask(HttpMethod.Get, url, map);

        Gson gson = new Gson();

        UserListBean value = null;
        try {
            value = gson.fromJson(jsonData, UserListBean.class);
        } catch (JsonSyntaxException e) {

            AppLogger.e(e.getMessage());
        }
        return value;
    }

    public SearchStatusListBean getStatusList() throws WeiboException {
        String url = URLHelper.STATUSES_SEARCH;

        Map<String, String> map = new HashMap<String, String>();
        map.put("access_token", access_token);
        map.put("count", count);
        map.put("page", page);
        map.put("q", q);

        String jsonData = null;

        jsonData = HttpUtility.getInstance().executeNormalTask(HttpMethod.Get, url, map);

        Gson gson = new Gson();

        SearchStatusListBean value = null;
        try {
            value = gson.fromJson(jsonData, SearchStatusListBean.class);
            List<MessageBean> list = value.getItemList();
            Iterator<MessageBean> iterator = list.iterator();
            while (iterator.hasNext()) {
                MessageBean msg = iterator.next();
                //message is deleted by sina
                if (msg.getUser() == null) {
                    iterator.remove();
                } else {
                    msg.getListViewSpannableString();
                    TimeUtility.dealMills(msg);
                }
            }
        } catch (JsonSyntaxException e) {

            AppLogger.e(e.getMessage());
        }

        return value;
    }

    private String access_token;
    private String q;
    private String count;
    private String page;

    public SearchDao(String access_token, String q) {
        this.access_token = access_token;
        this.q = q;
        this.count = SettingUtility.getMsgCount();
    }

    public SearchDao setCount(String count) {
        this.count = count;
        return this;
    }

    public SearchDao setPage(String page) {
        this.page = page;
        return this;
    }
}
