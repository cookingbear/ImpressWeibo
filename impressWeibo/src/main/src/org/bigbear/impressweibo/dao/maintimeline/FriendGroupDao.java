package org.bigbear.impressweibo.dao.maintimeline;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import org.bigbear.impressweibo.bean.GroupListBean;
import org.bigbear.impressweibo.dao.URLHelper;
import org.bigbear.impressweibo.support.debug.AppLogger;
import org.bigbear.impressweibo.support.error.WeiboException;
import org.bigbear.impressweibo.support.http.HttpMethod;
import org.bigbear.impressweibo.support.http.HttpUtility;

import java.util.HashMap;
import java.util.Map;

/**
 * User: qii
 * Date: 12-10-17
 */
public class FriendGroupDao {
	public GroupListBean getGroup() throws WeiboException {

        String url = URLHelper.FRIENDSGROUP_INFO;
        String jsonData = HttpUtility.getInstance().executeNormalTask(HttpMethod.Get, url, null);

        Gson gson = new Gson();

        GroupListBean value = null;
        try {
            value = gson.fromJson(jsonData, GroupListBean.class);
        } catch (JsonSyntaxException e) {
            AppLogger.e(e.getMessage());
        }

        return value;
    }

    public GroupListBean getGroupWithAccesstoken() throws WeiboException {

        String url = URLHelper.FRIENDSGROUP_INFO;

        Map<String, String> map = new HashMap<String, String>();
        map.put("access_token", access_token);

        String jsonData = HttpUtility.getInstance().executeNormalTask(HttpMethod.Get, url, map);

        Gson gson = new Gson();

        GroupListBean value = null;
        try {
            value = gson.fromJson(jsonData, GroupListBean.class);
        } catch (JsonSyntaxException e) {
            AppLogger.e(e.getMessage());
        }

        return value;
    }

    public FriendGroupDao(String token) {
        this.access_token = token;
    }

    private String access_token;
}
