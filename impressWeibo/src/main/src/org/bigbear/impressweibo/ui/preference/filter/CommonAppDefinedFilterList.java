package org.bigbear.impressweibo.ui.preference.filter;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * User: qii
 * Date: 13-6-17
 */
public class CommonAppDefinedFilterList {
    public static Set<String> getDefinedFilterKeywordAndUserList() {
        Set<String> result = new LinkedHashSet<String>();
        Collections.addAll(result, "十二星座", "成功�?", "不转不是中国�?", "经典语录");
        Collections.addAll(result, "白羊座女�?", "白羊座男�?", "金牛座女�?", "金牛座男�?",
                "双子座女�?", "双子座男�?", "巨蟹座女�?", "巨蟹座男�?", "狮子座女�?", "狮子座男�?",
                "处女座女�?", "处女座男�?", "天秤座女�?", "天秤座男�?", "摩羯座女�?", "摩羯座男�?",
                "水瓶座女�?", "水瓶座男�?", "双鱼座女�?", "双鱼座男�?", "天蝎座女�?", "天蝎座男�?",
                "射手座女�?", "射手座男�?");
        Collections.addAll(result, "白羊�?", "白羊�?", "金牛�?", "金牛�?",
                "双子�?", "双子�?", "巨蟹�?", "巨蟹�?", "狮子�?", "狮子�?",
                "处女�?", "处女�?", "天秤�?", "天秤�?", "摩羯�?", "摩羯�?",
                "水瓶�?", "水瓶�?", "双鱼�?", "双鱼�?", "天蝎�?", "天蝎�?",
                "射手�?", "射手�?");
        return result;
    }

    public static Set<String> getDefinedFilterSourceList() {
        Set<String> result = new LinkedHashSet<String>();
        Collections.addAll(result, "皮皮时光�?", "脉搏�?", "在线求签�?");
        return result;
    }
}
