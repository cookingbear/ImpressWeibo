package org.bigbear.impressweibo.bean.android;

import org.bigbear.impressweibo.bean.MessageListBean;

/**
 * User: qii
 * Date: 13-7-7
 */
public class MyStatusTimeLineData {

    public MessageListBean msgList;
    public TimeLinePosition position;

    public MyStatusTimeLineData(MessageListBean msgList, TimeLinePosition position) {
        this.msgList = msgList;
        this.position = position;
    }
}
