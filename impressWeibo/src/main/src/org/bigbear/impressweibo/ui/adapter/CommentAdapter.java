package org.bigbear.impressweibo.ui.adapter;

import java.util.List;

import org.bigbear.impressweibo.bean.CommentBean;
import org.bigbear.impressweibo.bean.MessageBean;
import org.bigbear.impressweibo.bean.UserBean;
import org.bigbear.impressweibo.support.settinghelper.SettingUtility;
import org.bigbear.impressweibo.support.utils.GlobalContext;
import org.bigbear.impressweibo.ui.adapter.AbstractAppListAdapter.ViewHolder;
import org.bigbear.impressweibo.ui.send.WriteReplyToCommentActivity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

public class CommentAdapter extends AbsAdapter<CommentBean>{

	public CommentAdapter(Fragment fragment, List<CommentBean> bean) {
		super(fragment, bean);
		// TODO Auto-generated constructor stub
	}


	@Override
	public void bindViewData(MainViewHolder holder,int position) {
		// TODO Auto-generated method stub



        final CommentBean comment = bean.get(position);

        UserBean user = comment.getUser();
        if (user != null) {
            holder.username.setVisibility(View.VISIBLE);
            if (!TextUtils.isEmpty(user.getRemark())) {
                holder.username.setText(new StringBuilder(user.getScreen_name()).append("(")
                        .append(user.getRemark()).append(")").toString());
            } else {
                holder.username.setText(user.getScreen_name());
            }
            buildAvatar(holder.avatar, position, user);
            
        } else {
            holder.username.setVisibility(View.INVISIBLE);
            holder.avatar.setVisibility(View.INVISIBLE);
        }

        holder.content.setText(comment.getListViewSpannableString());

        holder.time.setTime(comment.getMills());
        if (holder.source != null) {
            holder.source.setText(comment.getSourceString());
        }

        holder.repost_content.setVisibility(View.GONE);
        holder.repost_content_pic.setVisibility(View.GONE);

        CommentBean reply = comment.getReply_comment();

        if (reply != null ) {

            holder.repost_flag.setVisibility(View.VISIBLE);
            holder.repost_content.setVisibility(View.VISIBLE);
            holder.repost_content.setText(reply.getListViewSpannableString());
            holder.repost_content.setTag(reply.getId());
        } else {

            MessageBean repost_msg = comment.getStatus();

            if (repost_msg != null ) {
                buildOriWeiboContent(repost_msg, holder, position);
            } else {
                if (holder.repost_layout != null) {
                    holder.repost_layout.setVisibility(View.GONE);
                }
                holder.repost_flag.setVisibility(View.GONE);

            }
        }		
	}
    protected void buildOriWeiboContent(final MessageBean oriWeibo, MainViewHolder holder,
            int position) {
        holder.repost_content.setVisibility(View.VISIBLE);
        holder.repost_content_pic.setVisibility(View.GONE);
        holder.repost_content__pic_multi.setVisibility(View.GONE);
        holder.content_pic.setVisibility(View.GONE);
        holder.content_pic_multi.setVisibility(View.GONE);
        holder.repost_content.setText(oriWeibo.getListViewSpannableString());

        if (oriWeibo.havePicture()) {
            if (oriWeibo.isMultiPics()) {
                buildMultiPic(oriWeibo, holder.repost_content__pic_multi);
            } else {
                buildPic(oriWeibo, holder.repost_content_pic, position);
            }
        }
    }

}
