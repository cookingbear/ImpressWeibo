package org.bigbear.impressweibo.dao.relationship;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import org.bigbear.impressweibo.bean.UserBean;
import org.bigbear.impressweibo.dao.URLHelper;
import org.bigbear.impressweibo.support.debug.AppLogger;
import org.bigbear.impressweibo.support.error.WeiboException;
import org.bigbear.impressweibo.support.http.HttpMethod;
import org.bigbear.impressweibo.support.http.HttpUtility;

import java.util.HashMap;
import java.util.Map;

/**
 * User: qii
 * Date: 12-10-12
 */
public class FanDao {

    public FanDao(String token, String uid) {
        this.access_token = token;
        this.uid = uid;
    }

    public UserBean removeFan() throws WeiboException {
        String url = URLHelper.FRIENDSHIPS_FOLLOWERS_DESTROY;

        Map<String, String> map = new HashMap<String, String>();
        map.put("access_token", access_token);
        map.put("uid", uid);

        String jsonData = HttpUtility.getInstance().executeNormalTask(HttpMethod.Post, url, map);
        try {
            UserBean value = new Gson().fromJson(jsonData, UserBean.class);
            if (value != null) {
                return value;
            }
        } catch (JsonSyntaxException e) {
            AppLogger.e(e.getMessage());
        }

        return null;
    }

    private String access_token;
    private String uid;
}
