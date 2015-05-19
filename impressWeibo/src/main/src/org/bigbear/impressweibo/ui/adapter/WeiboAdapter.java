package org.bigbear.impressweibo.ui.adapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bigbear.impressweibo.R;
import org.bigbear.impressweibo.bean.CommentBean;
import org.bigbear.impressweibo.bean.MessageBean;
import org.bigbear.impressweibo.bean.UserBean;
import org.bigbear.impressweibo.support.asyncdrawable.IPictureWorker;
import org.bigbear.impressweibo.support.asyncdrawable.IWeiciyuanDrawable;
import org.bigbear.impressweibo.support.asyncdrawable.MsgDetailReadWorker;
import org.bigbear.impressweibo.support.asyncdrawable.PictureBitmapDrawable;
import org.bigbear.impressweibo.support.asyncdrawable.TimeLineBitmapDownloader;
import org.bigbear.impressweibo.support.debug.AppLogger;
import org.bigbear.impressweibo.support.file.FileLocationMethod;
import org.bigbear.impressweibo.support.gallery.GalleryAnimationActivity;
import org.bigbear.impressweibo.support.lib.AnimationRect;
import org.bigbear.impressweibo.support.lib.MyAsyncTask;
import org.bigbear.impressweibo.support.lib.ProfileTopAvatarImageView;
import org.bigbear.impressweibo.support.lib.TimeTextView;
import org.bigbear.impressweibo.support.lib.WeiboDetailImageView;
import org.bigbear.impressweibo.support.settinghelper.SettingUtility;
import org.bigbear.impressweibo.support.utils.GlobalContext;
import org.bigbear.impressweibo.support.utils.TextViewFixTouchConsume;
import org.bigbear.impressweibo.support.utils.TimeLineUtility;
import org.bigbear.impressweibo.support.utils.Utility;
import org.bigbear.impressweibo.ui.basefragment.AbstractTimeLineFragment;
import org.bigbear.impressweibo.ui.userinfo.UserInfoActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.util.LongSparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class WeiboAdapter extends AbsAdapter<MessageBean>{

    private LongSparseArray<Integer> msgHeights = new LongSparseArray<Integer>();
    private LongSparseArray<Integer> msgWidths = new LongSparseArray<Integer>();
    private LongSparseArray<Integer> oriMsgHeights = new LongSparseArray<Integer>();
    private LongSparseArray<Integer> oriMsgWidths = new LongSparseArray<Integer>();
    
	public WeiboAdapter(Fragment fragment,List<MessageBean> bean){
		super(fragment,bean);
	}


	@Override
	public void bindViewData(MainViewHolder holder, int position) {
		// TODO Auto-generated method stub
        final MessageBean msg = bean.get(position);

        UserBean user = msg.getUser();
        if (user != null) {

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

        
        if (!TextUtils.isEmpty(msg.getListViewSpannableString())) {
        	AppLogger.d(msg.getListViewSpannableString().toString());
            boolean haveCachedHeight = msgHeights.get(msg.getIdLong()) != null;
            ViewGroup.LayoutParams layoutParams = holder.content.getLayoutParams();
            if (haveCachedHeight) {
                layoutParams.height = msgHeights.get(msg.getIdLong());
            } else {
                layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            }

            boolean haveCachedWidth = msgWidths.get(msg.getIdLong()) != null;
            if (haveCachedWidth) {
                layoutParams.width = msgWidths.get(msg.getIdLong());
            } else {
                layoutParams.width = ViewGroup.LayoutParams.WRAP_CONTENT;
            }

            holder.content.requestLayout();
            holder.content.setText(msg.getListViewSpannableString());
            holder.content.setClickable(true);
            holder.content.setFocusable(true);
           // holder.content.setMovementMethod(TextViewFixTouchConsume.LocalLinkMovementMethod.getInstance());
            holder.content.setMovementMethod(LinkMovementMethod.getInstance());           
            if (!haveCachedHeight) {
                msgHeights.append(msg.getIdLong(), layoutParams.height);
            }

            if (!haveCachedWidth) {
                msgWidths.append(msg.getIdLong(), layoutParams.width);
            }
        } else {
            TimeLineUtility.addJustHighLightLinks(msg);
            holder.content.setText(msg.getListViewSpannableString());
            holder.content.setClickable(true);
            holder.content.setFocusable(true);
            holder.content.setMovementMethod(TextViewFixTouchConsume.LocalLinkMovementMethod.getInstance());
        }

        holder.time.setTime(msg.getMills());
        if (holder.source != null) {
            holder.source.setText(msg.getSourceString());
        }

        boolean checkRepostsCount = (msg.getReposts_count() != 0);
        boolean checkCommentsCount = (msg.getComments_count() != 0);
        boolean checkPic = msg.havePicture()
                    || (msg.getRetweeted_status() != null
                    && msg.getRetweeted_status().havePicture());
        checkPic = (checkPic && !SettingUtility.isEnablePic());
        boolean checkGps = (msg.getGeo() != null);

        if (!checkRepostsCount && !checkCommentsCount && !checkPic && !checkGps) {
                holder.count_layout.setVisibility(View.INVISIBLE);
        } else {
                holder.count_layout.setVisibility(View.VISIBLE);

           if (checkPic) {
                    holder.timeline_pic_iv.setVisibility(View.VISIBLE);
            } else {
                    holder.timeline_pic_iv.setVisibility(View.GONE);
            }

           if (checkGps) {
                    holder.timeline_gps_iv.setVisibility(View.VISIBLE);
            } else {
                    holder.timeline_gps_iv.setVisibility(View.INVISIBLE);
                }

           if (checkRepostsCount) {
                    holder.repost_count.setText(String.valueOf(msg.getReposts_count()));
                    holder.repost_count.setVisibility(View.GONE);
            } else {
                    holder.repost_count.setVisibility(View.GONE);
                }

           if (checkCommentsCount) {
                    holder.comment_count.setText(String.valueOf(msg.getComments_count()));
                    holder.comment_count.setVisibility(View.GONE);
            } else {
                    holder.comment_count.setVisibility(View.GONE);
                }
            }


        holder.repost_content.setVisibility(View.GONE);
        holder.repost_content_pic.setVisibility(View.GONE);
        holder.repost_content__pic_multi.setVisibility(View.GONE);

        holder.content_pic.setVisibility(View.GONE);
        holder.content_pic_multi.setVisibility(View.GONE);
        //test true

        if (msg.havePicture()) {
            if (msg.isMultiPics()) {
                buildMultiPic(msg, holder.content_pic_multi);
            } else {
                buildPic(msg, holder.content_pic, position);
            }
        }

        MessageBean repost_msg = msg.getRetweeted_status();

        if (repost_msg != null ) {

            holder.repost_flag.setVisibility(View.VISIBLE);
            //sina weibo official account can send repost message with picture, fuck sina weibo
            if (holder.content_pic.getVisibility() != View.GONE) {
                holder.content_pic.setVisibility(View.GONE);
            }
            buildRepostContent(msg, repost_msg, holder, position);

            if (holder.content_pic_multi != holder.repost_content__pic_multi) {
                interruptPicDownload(holder.content_pic_multi);
            }
            if (holder.repost_content_pic != holder.content_pic) {
                interruptPicDownload(holder.repost_content_pic);
            }
        } else {
            if (holder.content_pic_multi != holder.repost_content__pic_multi) {
                interruptPicDownload(holder.repost_content__pic_multi);
            }
            if (holder.repost_content_pic != holder.content_pic) {
                interruptPicDownload(holder.repost_content_pic);
            }

            holder.repost_flag.setVisibility(View.GONE);
        }

        boolean interruptPic = false;
        boolean interruptMultiPic = false;
        boolean interruptRepostPic = false;
        boolean interruptRepostMultiPic = false;

        if (msg.havePicture()) {
            if (msg.isMultiPics()) {
                interruptPic = true;
            } else {
                interruptMultiPic = true;
            }
        }

        if (repost_msg != null ) {

            if (repost_msg.havePicture()) {
                if (repost_msg.isMultiPics()) {
                    interruptRepostPic = true;
                } else {
                    interruptRepostMultiPic = true;
                }
            }
        }

        if (interruptPic && interruptRepostPic) {
            interruptPicDownload(holder.content_pic);
            interruptPicDownload(holder.repost_content_pic);
        }

        if (interruptMultiPic && interruptRepostMultiPic) {
            interruptPicDownload(holder.content_pic_multi);
            interruptPicDownload(holder.repost_content__pic_multi);
        }

        if (interruptPic && !interruptRepostPic) {
            if (holder.content_pic != holder.repost_content_pic) {
                interruptPicDownload(holder.content_pic);
            }
        }

        if (!interruptPic && interruptRepostPic) {
            if (holder.content_pic != holder.repost_content_pic) {
                interruptPicDownload(holder.repost_content_pic);
            }
        }

        if (interruptMultiPic && !interruptRepostMultiPic) {
            if (holder.content_pic_multi != holder.repost_content__pic_multi) {
                interruptPicDownload(holder.content_pic_multi);
            }
        }

        if (!interruptMultiPic && interruptRepostMultiPic) {
            if (holder.content_pic_multi != holder.repost_content__pic_multi) {
                interruptPicDownload(holder.repost_content__pic_multi);
            }
        }
	}
    private void interruptPicDownload(GridLayout gridLayout) {
        for (int i = 0; i < gridLayout.getChildCount(); i++) {
            ImageView iv = (ImageView) gridLayout.getChildAt(i);
            if (iv != null) {
                Drawable drawable = iv.getDrawable();
                if (drawable instanceof PictureBitmapDrawable) {
                    PictureBitmapDrawable downloadedDrawable
                            = (PictureBitmapDrawable) drawable;
                    IPictureWorker worker = downloadedDrawable.getBitmapDownloaderTask();
                    if (worker != null) {
                        ((MyAsyncTask) worker).cancel(true);
                    }
                    iv.setImageDrawable(null);
                }
            }
        }
    }
    private void interruptPicDownload(IWeiciyuanDrawable view) {
        Drawable drawable = view.getImageView().getDrawable();
        if (drawable instanceof PictureBitmapDrawable) {
            PictureBitmapDrawable downloadedDrawable
                    = (PictureBitmapDrawable) drawable;
            IPictureWorker worker = downloadedDrawable.getBitmapDownloaderTask();
            if (worker != null) {
                ((MyAsyncTask) worker).cancel(true);
            }
        }
        view.getImageView().setImageDrawable(null);
    }	
    private void buildRepostContent(MessageBean msg, final MessageBean repost_msg,
            MainViewHolder holder, int position) {
        holder.repost_content.setVisibility(View.VISIBLE);
        if (!repost_msg.getId().equals((String) holder.repost_content.getTag())) {
            boolean haveCachedHeight = oriMsgHeights.get(msg.getIdLong()) != null;
            ViewGroup.LayoutParams layoutParams = holder.repost_content.getLayoutParams();
            if (haveCachedHeight) {
                layoutParams.height = oriMsgHeights.get(msg.getIdLong());
            } else {
                layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            }

            boolean haveCachedWidth = oriMsgWidths.get(msg.getIdLong()) != null;
            if (haveCachedWidth) {
                layoutParams.width = oriMsgWidths.get(msg.getIdLong());
            } else {
                layoutParams.width = ViewGroup.LayoutParams.WRAP_CONTENT;
            }

            holder.repost_content.requestLayout();
            holder.repost_content.setText(repost_msg.getListViewSpannableString());

            if (!haveCachedHeight) {
                oriMsgHeights.append(msg.getIdLong(), layoutParams.height);
            }

            if (!haveCachedWidth) {
                oriMsgWidths.append(msg.getIdLong(), layoutParams.width);
            }

            holder.repost_content.setText(repost_msg.getListViewSpannableString());
            holder.repost_content.setTag(repost_msg.getId());
        }

        if (repost_msg.havePicture()) {
            if (repost_msg.isMultiPics()) {
                buildMultiPic(repost_msg, holder.repost_content__pic_multi);
            } else {
                buildPic(repost_msg, holder.repost_content_pic, position);
            }
        }
    }


}
