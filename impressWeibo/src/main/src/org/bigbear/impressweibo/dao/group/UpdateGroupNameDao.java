package org.bigbear.impressweibo.dao.group;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import org.bigbear.impressweibo.bean.GroupBean;
import org.bigbear.impressweibo.dao.URLHelper;
import org.bigbear.impressweibo.support.debug.AppLogger;
import org.bigbear.impressweibo.support.error.WeiboException;
import org.bigbear.impressweibo.support.http.HttpMethod;
import org.bigbear.impressweibo.support.http.HttpUtility;

import java.util.HashMap;
import java.util.Map;

/**
 * User: qii
 * Date: 13-2-16
 * http://open.weibo.com/wiki/2/friendships/groups/update
 */
public class UpdateGroupNameDao {

    public GroupBean update() throws WeiboException {

        String url = URLHelper.GROUP_UPDATE;

        Map<String, String> map = new HashMap<String, String>();
        map.put("access_token", access_token);
        map.put("name", name);
        map.put("list_id", list_id);

        String jsonData = HttpUtility.getInstance().executeNormalTask(HttpMethod.Post, url, map);

        Gson gson = new Gson();

        GroupBean value = null;
        try {
            value = gson.fromJson(jsonData, GroupBean.class);
        } catch (JsonSyntaxException e) {
            AppLogger.e(e.getMessage());
        }

        return value;
    }

    public UpdateGroupNameDao(String token, String list_id, String name) {
        this.access_token = token;
        this.name = name;
        this.list_id = list_id;
    }

    private String access_token;
    private String name;
    private String list_id;
}

