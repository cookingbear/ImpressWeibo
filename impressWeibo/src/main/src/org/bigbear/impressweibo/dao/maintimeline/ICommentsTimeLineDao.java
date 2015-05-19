package org.bigbear.impressweibo.dao.maintimeline;

import org.bigbear.impressweibo.bean.CommentListBean;
import org.bigbear.impressweibo.support.error.WeiboException;

/**
 * User: qii
 * Date: 12-12-16
 */
public interface ICommentsTimeLineDao {
    public CommentListBean getGSONMsgList() throws WeiboException;

    public void setSince_id(String since_id);

    public void setMax_id(String max_id);
}
