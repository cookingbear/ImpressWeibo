package org.bigbear.impressweibo.unreadnotification;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import org.bigbear.impressweibo.R;
import org.bigbear.impressweibo.bean.AccountBean;
import org.bigbear.impressweibo.bean.CommentListBean;
import org.bigbear.impressweibo.bean.MessageListBean;
import org.bigbear.impressweibo.bean.UnreadBean;
import org.bigbear.impressweibo.dao.unread.ClearUnreadDao;
import org.bigbear.impressweibo.support.error.WeiboException;
import org.bigbear.impressweibo.support.utils.GlobalContext;
import org.bigbear.impressweibo.support.utils.NotificationUtility;
import org.bigbear.impressweibo.support.utils.Utility;
import org.bigbear.impressweibo.ui.main.MainTimeLineActivity;
import org.bigbear.impressweibo.ui.send.WriteCommentActivity;
import org.bigbear.impressweibo.ui.send.WriteReplyToCommentActivity;

/**
 * User: qii
 * Date: 12-12-5
 */
@Deprecated
public class JBBigTextNotification {
    private Context context;

    private AccountBean accountBean;

    private CommentListBean comment;
    private MessageListBean repost;
    private CommentListBean mentionCommentsResult;

    private UnreadBean unreadBean;

    //only leave one broadcast receiver
    private static BroadcastReceiver clearNotificationEventReceiver;

    public JBBigTextNotification(Context context,
                                 AccountBean accountBean,
                                 CommentListBean comment,
                                 MessageListBean repost,
                                 CommentListBean mentionCommentsResult, UnreadBean unreadBean) {
        this.context = context;
        this.accountBean = accountBean;
        this.comment = comment;
        this.repost = repost;
        this.mentionCommentsResult = mentionCommentsResult;
        this.unreadBean = unreadBean;
    }


    private PendingIntent getPendingIntent() {
        Intent i = new Intent(context, MainTimeLineActivity.class);
        i.putExtra("account", accountBean);
        i.putExtra("comment", comment);
        i.putExtra("repost", repost);
        i.putExtra("mentionsComment", mentionCommentsResult);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, Long.valueOf(accountBean.getUid()).intValue(), i, PendingIntent.FLAG_UPDATE_CURRENT);
        return pendingIntent;
    }

    public Notification get() {
        Notification.Builder builder = new Notification.Builder(context)
                .setTicker(NotificationUtility.getTicker(unreadBean, null, null, null))
                .setContentText(accountBean.getUsernick())
                .setSmallIcon(R.drawable.ic_notification)
                .setAutoCancel(true)
                .setContentIntent(getPendingIntent())
                .setOnlyAlertOnce(true);

        builder.setContentTitle(NotificationUtility.getTicker(unreadBean, null, null, null));

        builder.setNumber(1);

        if (clearNotificationEventReceiver != null) {
            GlobalContext.getInstance().unregisterReceiver(clearNotificationEventReceiver);
            JBBigTextNotification.clearNotificationEventReceiver = null;
        }

        clearNotificationEventReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            new ClearUnreadDao(accountBean.getAccess_token()).clearMentionStatusUnread(unreadBean, accountBean.getUid());
                            new ClearUnreadDao(accountBean.getAccess_token()).clearMentionCommentUnread(unreadBean, accountBean.getUid());
                            new ClearUnreadDao(accountBean.getAccess_token()).clearCommentUnread(unreadBean, accountBean.getUid());
                        } catch (WeiboException ignored) {

                        } finally {
                            GlobalContext.getInstance().unregisterReceiver(clearNotificationEventReceiver);
                            JBBigTextNotification.clearNotificationEventReceiver = null;
                        }

                    }
                }).start();
            }
        };

        IntentFilter intentFilter = new IntentFilter("org.bigbear.impressweibo.Notification.unread");

        GlobalContext.getInstance().registerReceiver(clearNotificationEventReceiver, intentFilter);

        Intent broadcastIntent = new Intent("org.bigbear.impressweibo.Notification.unread");

        PendingIntent deletedPendingIntent = PendingIntent.getBroadcast(GlobalContext.getInstance(), 0, broadcastIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setDeleteIntent(deletedPendingIntent);


        Notification.BigTextStyle bigTextStyle = new Notification.BigTextStyle(builder);

        if (unreadBean.getMention_status() == 1) {
            bigTextStyle.setBigContentTitle(repost.getItem(0).getUser().getScreen_name() + "�?");
            bigTextStyle.bigText(repost.getItem(0).getText());


            Intent intent = new Intent(context, WriteCommentActivity.class);
            intent.putExtra("token", accountBean.getAccess_token());
            intent.putExtra("msg", repost.getItem(0));

            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            builder.addAction(R.drawable.comment_light, context.getString(R.string.comments), pendingIntent);


        }

        if (unreadBean.getCmt() == 1) {
            bigTextStyle.setBigContentTitle(comment.getItem(0).getUser().getScreen_name() + "�?");
            bigTextStyle.bigText(comment.getItem(0).getText());

            Intent intent = new Intent(context, WriteReplyToCommentActivity.class);
            intent.putExtra("token", accountBean.getAccess_token());
            intent.putExtra("msg", comment.getItem(0));
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            builder.addAction(R.drawable.reply_to_comment_light, context.getString(R.string.reply_to_comment), pendingIntent);

        }

        if (unreadBean.getMention_cmt() == 1) {
            bigTextStyle.setBigContentTitle(mentionCommentsResult.getItem(0).getUser().getScreen_name() + "�?");
            bigTextStyle.bigText(mentionCommentsResult.getItem(0).getText());

            Intent intent = new Intent(context, WriteReplyToCommentActivity.class);
            intent.putExtra("token", accountBean.getAccess_token());
            intent.putExtra("msg", mentionCommentsResult.getItem(0));
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            builder.addAction(R.drawable.reply_to_comment_light, context.getString(R.string.reply_to_comment), pendingIntent);
        }


        bigTextStyle.setSummaryText(accountBean.getUsernick());

        builder.setStyle(bigTextStyle);
        Utility.configVibrateLedRingTone(builder);

        return builder.build();
    }

}
