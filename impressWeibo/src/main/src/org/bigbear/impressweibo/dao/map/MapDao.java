package org.bigbear.impressweibo.dao.map;

import org.bigbear.impressweibo.dao.URLHelper;
import org.bigbear.impressweibo.support.asyncdrawable.TaskCache;
import org.bigbear.impressweibo.support.error.WeiboException;
import org.bigbear.impressweibo.support.file.FileLocationMethod;
import org.bigbear.impressweibo.support.file.FileManager;
import org.bigbear.impressweibo.support.http.HttpMethod;
import org.bigbear.impressweibo.support.http.HttpUtility;
import org.bigbear.impressweibo.support.imageutility.ImageUtility;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.Bitmap;
import android.text.TextUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * User: qii
 * Date: 13-7-22
 */
public class MapDao {

    public Bitmap getMap() throws WeiboException {

        String url = URLHelper.STATIC_MAP;
        Map<String, String> map = new HashMap<String, String>();
        map.put("access_token", access_token);
        String coordinates = String.valueOf(lat) + "," + String.valueOf(lan);
        map.put("center_coordinate", coordinates);
        map.put("zoom", "14");
        map.put("size", "600x380");

        String jsonData = HttpUtility.getInstance().executeNormalTask(HttpMethod.Get, url, map);
        String mapUrl = "";
        try {
            JSONObject jsonObject = new JSONObject(jsonData);
            JSONArray array = jsonObject.optJSONArray("map");
            jsonObject = array.getJSONObject(0);
            mapUrl = jsonObject.getString("image_url");
        } catch (JSONException e) {

        }

        if (TextUtils.isEmpty(mapUrl)) {
            return null;
        }

        String filePath = FileManager.getFilePathFromUrl(mapUrl, FileLocationMethod.map);

        boolean downloaded = TaskCache
                .waitForPictureDownload(mapUrl, null, filePath, FileLocationMethod.map);

        if (!downloaded) {
            return null;
        }

        Bitmap bitmap = ImageUtility
                .readNormalPic(FileManager.getFilePathFromUrl(mapUrl, FileLocationMethod.map), -1,
                        -1);

        return bitmap;
    }

    public MapDao(String token, double lan, double lat) {
        this.access_token = token;
        this.lan = lan;
        this.lat = lat;
    }

    private String access_token;

    private double lan;

    private double lat;
}
