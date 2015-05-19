package org.bigbear.impressweibo.ui.adapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bigbear.impressweibo.R;
import org.bigbear.impressweibo.bean.MessageBean;
import org.bigbear.impressweibo.bean.UserBean;
import org.bigbear.impressweibo.support.asyncdrawable.IWeiciyuanDrawable;
import org.bigbear.impressweibo.support.asyncdrawable.TimeLineBitmapDownloader;
import org.bigbear.impressweibo.support.debug.AppLogger;
import org.bigbear.impressweibo.support.file.FileLocationMethod;
import org.bigbear.impressweibo.support.gallery.GalleryAnimationActivity;
import org.bigbear.impressweibo.support.lib.AnimationRect;
import org.bigbear.impressweibo.support.lib.ProfileTopAvatarImageView;
import org.bigbear.impressweibo.support.lib.TimeTextView;
import org.bigbear.impressweibo.support.lib.WeiboDetailImageView;
import org.bigbear.impressweibo.support.settinghelper.SettingUtility;
import org.bigbear.impressweibo.support.utils.GlobalContext;
import org.bigbear.impressweibo.ui.basefragment.AbstractTimeLineFragment;
import org.bigbear.impressweibo.ui.userinfo.UserInfoActivity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public abstract class AbsAdapter<T> extends RecyclerView.Adapter<AbsAdapter.MainViewHolder>{
	protected Fragment fragment;
	protected List<T> bean;
	public AbsAdapter (Fragment fragment,List<T> bean){
		this.fragment=fragment;
		this.bean=bean;
	}
	@Override
	public int getItemCount(){
		return bean.size();
	}
    
	@Override
	public MainViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		// TODO Auto-generated method stub
		AppLogger.d("oncreateviewholder");
		View view =LayoutInflater.from(fragment.getActivity()).inflate(R.layout.timeline_listview_item_layout	, parent,false);
		MainViewHolder viewHolder =new MainViewHolder(view);
		bindViewHolderHelp(viewHolder,view);
		return viewHolder;
	}
	public void bindViewHolderHelp(MainViewHolder viewHolder,View view){
		viewHolder.avatar=(ProfileTopAvatarImageView)view.findViewById(R.id.avatar);
		viewHolder.comment_count=(TextView)view.findViewById(R.id.comment_count);
		viewHolder.content=(TextView)view.findViewById(R.id.content);
		viewHolder.content_pic=(WeiboDetailImageView)view.findViewById(R.id.content_pic);
		viewHolder.content_pic_multi=(GridLayout)view.findViewById(R.id.content_pic_multi);
		viewHolder.count_layout=(LinearLayout)view.findViewById(R.id.count_layout);
		viewHolder.listview_root=(LinearLayout)view.findViewById(R.id.listview_root);
		viewHolder.location=(TextView)view.findViewById(R.id.location);
		viewHolder.map=(ImageView)view.findViewById(R.id.map);
		viewHolder.repost_content=(TextView)view.findViewById(R.id.repost_content);
		viewHolder.repost_content__pic_multi=(GridLayout)view.findViewById(R.id.repost_content__pic_multi);
		viewHolder.repost_content_pic=(WeiboDetailImageView)view.findViewById(R.id.repost_content_pic);
		viewHolder.repost_count=(TextView)view.findViewById(R.id.repost_count);
		viewHolder.repost_flag=(View)view.findViewById(R.id.repost_flag);
		viewHolder.repost_layout=(LinearLayout)view.findViewById(R.id.repost_layout);
		viewHolder.source=(TextView)view.findViewById(R.id.source);
		viewHolder.time=(TimeTextView)view.findViewById(R.id.time);
		viewHolder.timeline_gps_iv=(ImageView)view.findViewById(R.id.timeline_gps_iv);
		viewHolder.timeline_pic_iv=(ImageView)view.findViewById(R.id.timeline_pic_iv);
		viewHolder.username=(TextView)view.findViewById(R.id.username);

	}
	@Override
	public void onBindViewHolder(MainViewHolder holder,int position){

        bindViewData(holder,position);
	}
    protected void buildAvatar(IWeiciyuanDrawable view, int position, final UserBean user) {
        view.setVisibility(View.VISIBLE);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(fragment.getActivity(), UserInfoActivity.class);
                intent.putExtra("token", GlobalContext.getInstance().getSpecialToken());
                intent.putExtra("user", user);
                fragment.getActivity().startActivity(intent);
            }
        });
        view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                UserDialog dialog = new UserDialog(user);
                dialog.show(fragment.getFragmentManager(), "");
                return true;
            }
        });
        view.checkVerified(user);
        buildAvatarHelp(view.getImageView(), position, user);
    }

    private void buildAvatarHelp(ImageView view, int position, final UserBean user) {
        String image_url = user.getProfile_image_url();
        if (!TextUtils.isEmpty(image_url)) {
            view.setVisibility(View.VISIBLE);
            TimeLineBitmapDownloader.getInstance()
                    .downloadAvatar(view, user, (AbstractTimeLineFragment) fragment);
        } else {
            view.setVisibility(View.GONE);
        }
    }
    protected void buildPic(final MessageBean msg, ImageView view) {
        view.setVisibility(View.VISIBLE);
        TimeLineBitmapDownloader.getInstance()
                .downContentPic(view, msg, (AbstractTimeLineFragment) fragment);
    }

    protected void buildPic(final MessageBean msg, IWeiciyuanDrawable view) {
        view.setVisibility(View.VISIBLE);
        TimeLineBitmapDownloader.getInstance()
                .downContentPic(view, msg, (AbstractTimeLineFragment) fragment);
    }
    protected void buildPic(final MessageBean msg, final IWeiciyuanDrawable view, int position) {
        if (SettingUtility.isEnablePic()) {
            view.setVisibility(View.VISIBLE);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ImageView imageView = view.getImageView();
                    AnimationRect rect = AnimationRect.buildFromImageView(imageView);
                    ArrayList<AnimationRect> animationRectArrayList
                            = new ArrayList<AnimationRect>();
                    animationRectArrayList.add(rect);

                    Intent intent = GalleryAnimationActivity
                            .newIntent(msg, animationRectArrayList, 0);
                    fragment.getActivity().startActivity(intent);
                }
            });
            buildPic(msg, view);
        } else {
            view.setVisibility(View.GONE);
        }
    }
    protected void buildMultiPic(final MessageBean msg, final GridLayout gridLayout) {
        if (SettingUtility.isEnablePic()) {
            gridLayout.setVisibility(View.VISIBLE);

            final int count = msg.getPicCount();
            for (int i = 0; i < count; i++) {
                final IWeiciyuanDrawable pic = (IWeiciyuanDrawable) gridLayout.getChildAt(i);
                pic.setVisibility(View.VISIBLE);
                if (SettingUtility.getEnableBigPic()) {
                    TimeLineBitmapDownloader.getInstance()
                            .displayMultiPicture(pic, msg.getHighPicUrls().get(i),
                                    FileLocationMethod.picture_large,
                                    (AbstractTimeLineFragment) fragment);
                } else {
                    TimeLineBitmapDownloader.getInstance()
                            .displayMultiPicture(pic, msg.getThumbnailPicUrls().get(i),
                                    FileLocationMethod.picture_thumbnail,
                                    (AbstractTimeLineFragment) fragment);
                }

                final int finalI = i;
                pic.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ArrayList<AnimationRect> animationRectArrayList
                                = new ArrayList<AnimationRect>();
                        for (int i = 0; i < count; i++) {
                            final IWeiciyuanDrawable pic = (IWeiciyuanDrawable) gridLayout
                                    .getChildAt(i);
                            ImageView imageView = (ImageView) pic;
                            if (imageView.getVisibility() == View.VISIBLE) {
                                AnimationRect rect = AnimationRect.buildFromImageView(imageView);
                                animationRectArrayList.add(rect);
                            }
                        }

                        Intent intent = GalleryAnimationActivity
                                .newIntent(msg, animationRectArrayList, finalI);
                        fragment.getActivity().startActivity(intent);
                    }
                });
            }

            if (count < 9) {
                ImageView pic;
                switch (count) {
                    case 8:
                        pic = (ImageView) gridLayout.getChildAt(8);
                        pic.setVisibility(View.INVISIBLE);
                        break;
                    case 7:
                        for (int i = 8; i > 6; i--) {
                            pic = (ImageView) gridLayout.getChildAt(i);
                            pic.setVisibility(View.INVISIBLE);
                        }
                        break;
                    case 6:
                        for (int i = 8; i > 5; i--) {
                            pic = (ImageView) gridLayout.getChildAt(i);
                            pic.setVisibility(View.GONE);
                        }

                        break;
                    case 5:
                        for (int i = 8; i > 5; i--) {
                            pic = (ImageView) gridLayout.getChildAt(i);
                            pic.setVisibility(View.GONE);
                        }
                        pic = (ImageView) gridLayout.getChildAt(5);
                        pic.setVisibility(View.INVISIBLE);
                        break;
                    case 4:
                        for (int i = 8; i > 5; i--) {
                            pic = (ImageView) gridLayout.getChildAt(i);
                            pic.setVisibility(View.GONE);
                        }
                        pic = (ImageView) gridLayout.getChildAt(5);
                        pic.setVisibility(View.INVISIBLE);
                        pic = (ImageView) gridLayout.getChildAt(4);
                        pic.setVisibility(View.INVISIBLE);
                        break;
                    case 3:
                        for (int i = 8; i > 2; i--) {
                            pic = (ImageView) gridLayout.getChildAt(i);
                            pic.setVisibility(View.GONE);
                        }
                        break;
                    case 2:
                        for (int i = 8; i > 2; i--) {
                            pic = (ImageView) gridLayout.getChildAt(i);
                            pic.setVisibility(View.GONE);
                        }
                        pic = (ImageView) gridLayout.getChildAt(2);
                        pic.setVisibility(View.INVISIBLE);
                        break;
                }
            }
        } else {
            gridLayout.setVisibility(View.GONE);
        }
    }
    public abstract void bindViewData(MainViewHolder holder,int position);
    static class MainViewHolder extends RecyclerView.ViewHolder{
    	public MainViewHolder(View v) {
    		super(v);
    	}
    	public LinearLayout listview_root;
    	public ProfileTopAvatarImageView avatar;
    	public TextView username;
    	public TimeTextView time;
    	public TextView source;
        public TextView content;
        public WeiboDetailImageView content_pic;
        public GridLayout content_pic_multi;
        public View repost_flag;
        public TextView repost_content;
        public WeiboDetailImageView repost_content_pic;
        public GridLayout repost_content__pic_multi;
        public ImageView timeline_gps_iv;
        public ImageView timeline_pic_iv;
        public TextView repost_count;
        public LinearLayout count_layout;
        public TextView comment_count;
        public TextView location;
        public ImageView map;
        public LinearLayout repost_layout;
    }
}
