package org.bigbear.impressweibo.ui.login;

import org.bigbear.impressweibo.R;
import org.bigbear.impressweibo.bean.AccountBean;
import org.bigbear.impressweibo.support.database.AccountDBTask;
import org.bigbear.impressweibo.support.debug.AppLogger;
import org.bigbear.impressweibo.support.lib.changelogdialog.ChangeLogDialog;
import org.bigbear.impressweibo.support.settinghelper.SettingUtility;
import org.bigbear.impressweibo.support.utils.GlobalContext;
import org.bigbear.impressweibo.support.utils.ThemeUtility;
import org.bigbear.impressweibo.support.utils.Utility;
import org.bigbear.impressweibo.ui.blackmagic.BlackMagicActivity;
import org.bigbear.impressweibo.ui.interfaces.AbstractAppActivity;
import org.bigbear.impressweibo.ui.main.MainTimeLineActivity;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.app.WallpaperManager;
import android.content.AsyncTaskLoader;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AbstractAppActivity implements LoaderManager.LoaderCallbacks<List<AccountBean>>
         {

    private static final String ACTION_OPEN_FROM_APP_INNER = "org.bigbear.impressweibo:accountactivity";
    private static final String ACTION_OPEN_FROM_APP_INNER_REFRESH_TOKEN = "org.bigbear.impressweibo:accountactivity_refresh_token";

    private static final String REFRESH_ACTION_EXTRA = "refresh_account";

    private final int ADD_ACCOUNT_REQUEST_CODE = 0;
    private final int LOADER_ID = 0;
    
    private ListView listView = null;
    private AccountAdapter listAdapter = null;
    private List<AccountBean> accountList = new ArrayList<AccountBean>();
    private boolean pressed=false;
    private AnimationListener animationListener=null;
    private boolean jumpEnabled=false;
    
    public static Intent newIntent() {
        Intent intent = new Intent(GlobalContext.getInstance(), MainActivity.class);
        intent.setAction(ACTION_OPEN_FROM_APP_INNER);
        return intent;
    }

    public static Intent newIntent(AccountBean refreshAccount) {
        Intent intent = new Intent(GlobalContext.getInstance(), MainActivity.class);
        intent.setAction(ACTION_OPEN_FROM_APP_INNER_REFRESH_TOKEN);
        intent.putExtra(REFRESH_ACTION_EXTRA, refreshAccount);
        return intent;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
    	
        //String action = getIntent() != null ? getIntent().getAction() : null;

        //if (ACTION_OPEN_FROM_APP_INNER.equals(action)) {
            //empty
        //} else if (ACTION_OPEN_FROM_APP_INNER_REFRESH_TOKEN.equals(action)) {
            //empty
        //} else {
            //finish current Activity
    	AppLogger.d("mainActivity begin");
        //}
        super.onCreate(savedInstanceState);   
        setContentView(R.layout.main_layout);
        getActionBar().hide();
        View backgroundView=findViewById(R.id.main_view);
        backgroundView.setAlpha((float) 0.3);
        animationListener=new AnimationListener(){

			@Override
			public void onAnimationEnd(Animation animation) {
				// TODO Auto-generated method stub
				try {
					Thread.sleep(300);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		        jumpToMainTimeLineActivity();
		        AppLogger.d("TimeLineActivity jumped");
		        if (!jumpEnabled){
		        if (Utility.isCertificateFingerprintCorrect(MainActivity.this) && Utility
		               .isSinaWeiboSafe(MainActivity.this)){		                	
		                    Intent intent =new Intent(MainActivity.this,SSOActivity.class);		                    
		                    startActivityForResult(intent,ADD_ACCOUNT_REQUEST_CODE);}
		        else {
		        	Intent intent=new Intent(MainActivity.this,OAuthActivity.class);
		        	startActivityForResult(intent,ADD_ACCOUNT_REQUEST_CODE);
		        }}
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onAnimationStart(Animation animation) {
				// TODO Auto-generated method stub
				AppLogger.d("onAnimationStart");
			}
        	
        };
        setAnimation();
        
        
        //Intent intent = new Intent(this,OAuthActivity.class);
        //startActivityForResult(intent,ADD_ACCOUNT_REQUEST_CODE);

        //setContentView(R.layout.accountactivity_layout);
        //getActionBar().setTitle(getString(R.string.app_name));
        listAdapter = new AccountAdapter();
       // listView = (ListView) findViewById(R.id.listView);
       // listView.setOnItemClickListener(new AccountListItemClickListener());
       // listView.setAdapter(listAdapter);
       // listView.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE_MODAL);
       // listView.setMultiChoiceModeListener(new AccountMultiChoiceModeListener());
        getLoaderManager().initLoader(LOADER_ID, null, this);
        AppLogger.d("mainActivity initloader");
        //if (SettingUtility.firstStart()) {
       //     showChangeLogDialog();
       // }

    //    if (ACTION_OPEN_FROM_APP_INNER_REFRESH_TOKEN.equals(action)) {
    //        showAddAccountDialog();
    //        AccountBean accountBean = getIntent().getParcelableExtra(REFRESH_ACTION_EXTRA);
    //        Toast.makeText(this, String.format(getString(R.string.account_token_has_expired),
    //                accountBean.getUsernick()), Toast.LENGTH_SHORT).show();
    //    }
    
    }

    @Override
    public void onBackPressed() {    	
        if (this.pressed==true){
            super.onBackPressed();
        	this.finish();
        }
        else {
        	this.pressed=true;
        	Toast.makeText(this,getString(R.string.pressed_again),Toast.LENGTH_LONG).show();
        }
    }

    private void showChangeLogDialog() {
        ChangeLogDialog changeLogDialog = new ChangeLogDialog(this);
        changeLogDialog.show();
    }

    private void jumpToMainTimeLineActivity() {
        String id = SettingUtility.getDefaultAccountId();
        AppLogger.d("jump id "+id);
        if (!TextUtils.isEmpty(id)) {
            AccountBean bean = AccountDBTask.getAccount(id);
            AppLogger.d(bean.toString());
            if (bean != null) {
                Intent start = MainTimeLineActivity.newIntent(bean);
                jumpEnabled=true;
                startActivity(start);
                finish();
            }
        }
    }

    private void showAddAccountDialog() {
        final ArrayList<Class> activityList = new ArrayList<Class>();
        ArrayList<String> itemValueList = new ArrayList<String>();

        activityList.add(OAuthActivity.class);
        itemValueList.add(getString(R.string.oauth_login));



        if (SettingUtility.isBlackMagicEnabled()) {
            activityList.add(BlackMagicActivity.class);
            itemValueList.add(getString(R.string.hack_login));
        }

        new AlertDialog.Builder(this)
                .setItems(itemValueList.toArray(new String[0]),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(MainActivity.this,
                                        activityList.get(which));
                                startActivityForResult(intent, ADD_ACCOUNT_REQUEST_CODE);
                            }
                        }).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == ADD_ACCOUNT_REQUEST_CODE && resultCode == RESULT_OK) {
        	AppLogger.d("MainActivity result ok");
            refresh();
            AppLogger.d("refresh() has passed");
            if (data == null) {
                return;
            }

            String expires_time = data.getExtras().getString("expires_in");
            long expiresDays = TimeUnit.SECONDS.toDays(Long.valueOf(expires_time));
            AppLogger.d(".get(0) ");
            while (GlobalContext.firstUser==null);           
            Intent intent = MainTimeLineActivity.newIntent(GlobalContext.firstUser);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        }
        
        if (resultCode ==RESULT_CANCELED ){
        	AppLogger.d("MainActivity result canceled");
            Intent intent=new Intent(MainActivity.this,OAuthActivity.class);
            startActivityForResult(intent,ADD_ACCOUNT_REQUEST_CODE);
            }
        
        AppLogger.d("MainActiviy onActivityResult end");
    }

    private void refresh() {
        getLoaderManager().getLoader(LOADER_ID).forceLoad();
    }

    private void remove() {
        Set<String> set = new HashSet<String>();
        long[] ids = listView.getCheckedItemIds();
        for (long id : ids) {
            set.add(String.valueOf(id));
        }
        accountList = AccountDBTask.removeAndGetNewAccountList(set);
        listAdapter.notifyDataSetChanged();
    }
    

    private static  class AccountDBLoader extends AsyncTaskLoader<List<AccountBean>> {

        public AccountDBLoader(Context context, Bundle args) {
            super(context);
        }

        @Override
        protected void onStartLoading() {
            super.onStartLoading();
            forceLoad();
        }

        public List<AccountBean> loadInBackground() {  
            return AccountDBTask.getAccountList();         
        }
    }
    
    private class AccountListItemClickListener implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            if (!Utility.isTokenValid(accountList.get(i))) {
                showAddAccountDialog();
                return;
            }

            Intent intent = MainTimeLineActivity.newIntent(accountList.get(i));
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        }
    }

    private class AccountMultiChoiceModeListener implements AbsListView.MultiChoiceModeListener {

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mode.getMenuInflater().inflate(R.menu.contextual_menu_accountactivity, menu);
            mode.setTitle(getString(R.string.account_management));
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.menu_remove_account:
                    remove();
                    mode.finish();
                    return true;
            }
            return false;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {

        }

        @Override
        public void onItemCheckedStateChanged(ActionMode mode, int position, long id,
                boolean checked) {
            listAdapter.notifyDataSetChanged();
        }
    }
    @Override
    public Loader<List<AccountBean>> onCreateLoader(int id, Bundle args) {
        return new AccountDBLoader(MainActivity.this, args);
    }

    @Override
    public void onLoadFinished(Loader<List<AccountBean>> loader, List<AccountBean> data) {
    	accountList=data;
    }

    @Override
    public void onLoaderReset(Loader<List<AccountBean>> loader) {
        accountList = new ArrayList<AccountBean>();
        listAdapter.notifyDataSetChanged();
    }

    private class AccountAdapter extends BaseAdapter {
        private int checkedBG;
        private int defaultBG;

        public AccountAdapter() {
            defaultBG = getResources().getColor(R.color.transparent);
            checkedBG = ThemeUtility
                    .getColor(MainActivity.this, R.attr.listview_checked_color);
        }

        @Override
        public int getCount() {
            return accountList.size();
        }

        @Override
        public Object getItem(int i) {
            return accountList.get(i);
        }

        @Override
        public long getItemId(int i) {
            return Long.valueOf(accountList.get(i).getUid());
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

        @Override
        public View getView(final int i, View view, ViewGroup viewGroup) {
            ViewHolder holder;
            if (view == null || view.getTag() == null) {
                LayoutInflater layoutInflater = getLayoutInflater();
                View mView = layoutInflater
                        .inflate(R.layout.accountactivity_listview_item_layout, viewGroup, false);
                holder = new ViewHolder();
                holder.root = mView.findViewById(R.id.listview_root);
                holder.name = (TextView) mView.findViewById(R.id.account_name);
                holder.avatar = (ImageView) mView.findViewById(R.id.imageView_avatar);
                holder.tokenInvalid = (TextView) mView.findViewById(R.id.token_expired);
                view = mView;
            } else {
                holder = (ViewHolder) view.getTag();
            }

            holder.root.setBackgroundColor(defaultBG);
            if (listView.getCheckedItemPositions().get(i)) {
                holder.root.setBackgroundColor(checkedBG);
            }

            if (accountList.get(i).getInfo() != null) {
                holder.name.setText(accountList.get(i).getInfo().getScreen_name());
            } else {
                holder.name.setText(accountList.get(i).getUsernick());
            }

            if (!TextUtils.isEmpty(accountList.get(i).getAvatar_url())) {
                getBitmapDownloader()
                        .downloadAvatar(holder.avatar, accountList.get(i).getInfo(), false);
            }

            holder.tokenInvalid.setVisibility(!Utility.isTokenValid(accountList.get(i)) ? View.VISIBLE : View.GONE);
            return view;
        }
    }

    class ViewHolder {
        View root;
        TextView name;
        ImageView avatar;
        TextView tokenInvalid;
    }
    
    private void setAnimation(){
    	ImageView impression=(ImageView) findViewById(R.id.main_title);
    	AnimationSet animationSet=new AnimationSet(true);
        Animation ani =new AlphaAnimation(0,1);
        ani.setDuration(400);
        impression.setAnimation(ani);
        animationSet.addAnimation(ani);
 
        List<View> text=new ArrayList<View>();
        text.add(findViewById(R.id.xin));
        text.add(findViewById(R.id.lang));
        text.add(findViewById(R.id.wei));
        text.add(findViewById(R.id.bo));
        text.add(findViewById(R.id.di));
        text.add(findViewById(R.id.san));
        text.add(findViewById(R.id.fang));
        for (int i=0;i<7;i++){
        	Animation animation=new AlphaAnimation(0,1);
        	animation.setStartOffset(i*150);
        	animation.setDuration(400);
        	text.get(i).setAnimation(animation);
        	animationSet.addAnimation(animation);
        	if (i==6) {
        		animation.setAnimationListener(animationListener);
        	}
        }
        animationSet.setStartOffset(500);
        animationSet.start();

    }
}
