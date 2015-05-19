package org.bigbear.impressweibo.ui.login;

import org.bigbear.impressweibo.R;
import org.bigbear.impressweibo.bean.AccountBean;
import org.bigbear.impressweibo.bean.UserBean;
import org.bigbear.impressweibo.dao.login.OAuthDao;
import org.bigbear.impressweibo.support.database.AccountDBTask;
import org.bigbear.impressweibo.support.debug.AppLogger;
import org.bigbear.impressweibo.support.error.WeiboException;
import org.bigbear.impressweibo.support.lib.MyAsyncTask;
import org.bigbear.impressweibo.support.lib.sinasso.SsoHandler;
import org.bigbear.impressweibo.support.utils.GlobalContext;
import org.bigbear.impressweibo.support.utils.Utility;
import org.bigbear.impressweibo.ui.interfaces.AbstractAppActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import java.lang.ref.WeakReference;

/**
 * User: qii
 * Date: 13-6-18
 */
public class SSOActivity extends AbstractAppActivity {

    private SSOTask task;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //getActionBar().setTitle(R.string.official_app_login);
        //getActionBar().setDisplayHomeAsUpEnabled(true);
        //getActionBar().setDisplayShowHomeEnabled(false);
        getActionBar().hide();
        SsoHandler ssoHandler = new SsoHandler(SSOActivity.this);
        ssoHandler.authorize();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Utility.cancelTasks(task);
    }

    private static class SSOTask extends MyAsyncTask<String, UserBean, OAuthActivity.DBResult> {

        private WeakReference<SSOActivity> sSOActivityWeakReference;
        private WeiboException e;
        private OAuthActivity.ProgressFragment progressFragment = OAuthActivity.ProgressFragment
                .newInstance();
        private String token;
        private String expiresIn;

        public SSOTask(SSOActivity ssoActivity, String token, String expiresIn) {
            this.sSOActivityWeakReference = new WeakReference<SSOActivity>(ssoActivity);
            this.token = token;
            this.expiresIn = expiresIn;
        }

        @Override
        protected void onPreExecute() {
            progressFragment.setAsyncTask(this);

            SSOActivity activity = sSOActivityWeakReference.get();
            if (activity != null) {
                progressFragment.show(activity.getSupportFragmentManager(), "");
            }
        }

        @Override
        protected OAuthActivity.DBResult doInBackground(String... params) {
            try {
                UserBean user = new OAuthDao(token).getOAuthUserInfo();
                AccountBean account = new AccountBean();
                account.setAccess_token(token);
                account.setExpires_time(
                        System.currentTimeMillis() + Long.valueOf(expiresIn) * 1000);
                account.setInfo(user);
                AppLogger
                        .e("token expires in " + Utility.calcTokenExpiresInDays(account) + " days");
                GlobalContext.firstUser=account;
                return AccountDBTask.addOrUpdateAccount(account, false);
            } catch (WeiboException e) {
                AppLogger.e(e.getError());
                this.e = e;
                cancel(true);
                return null;
            }
        }

        @Override
        protected void onCancelled(OAuthActivity.DBResult dbResult) {
            super.onCancelled(dbResult);
            if (progressFragment != null) {
                progressFragment.dismissAllowingStateLoss();
            }

            SSOActivity activity = sSOActivityWeakReference.get();
            if (activity == null) {
                return;
            }

            if (e != null) {
                Toast.makeText(activity, e.getError(), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(activity, R.string.you_cancel_ssologin, Toast.LENGTH_SHORT).show();
            }
            activity.finish();
        }

        @Override
        protected void onPostExecute(OAuthActivity.DBResult dbResult) {
        	
            if (progressFragment.isVisible()) {
                progressFragment.dismissAllowingStateLoss();
            }
            
            SSOActivity activity = sSOActivityWeakReference.get();
            if (activity == null) {
                return;
            }
            Bundle values = new Bundle();
            values.putString("expires_in", expiresIn);
            Intent intent = new Intent();
            intent.putExtras(values);

            switch (dbResult) {

                case add_successfuly:    
                    activity.setResult(RESULT_OK, intent);
                    activity.finish();
                    Toast.makeText(activity, activity.getString(R.string.login_success),
                            Toast.LENGTH_SHORT).show();
                    break;
                case update_successfully:
                	activity.setResult(RESULT_OK,intent);
                    Toast.makeText(activity, activity.getString(R.string.update_account_success),
                            Toast.LENGTH_SHORT).show();
                    break;
            }
            AppLogger.d("onPostExecute end");
            activity.finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {  	
    	AppLogger.e("resultCode "+resultCode);
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_CANCELED || data == null) {
        	if (resultCode==Activity.RESULT_CANCELED) AppLogger.d("result canceled");
        	if (data==null) AppLogger.d("data null");
            Toast.makeText(this, R.string.you_cancel_ssologin, Toast.LENGTH_SHORT).show();
            Intent resultInfo=new Intent();
            
            SSOActivity.this.setResult(RESULT_CANCELED, resultInfo);
            finish();
            return;
        }
    	AppLogger.d("result ok");
        // Check OAuth 2.0/2.10 error code.
        String error = data.getStringExtra("error");
        if (error == null) {
            error = data.getStringExtra("error_type");
        }

        // error occurred.
        if (error != null) {
            if (error.equals("access_denied")
                    || error.equals("OAuthAccessDeniedException")) {
            	AppLogger.d("access denied");
                Log.d("Weibo-authorize", "Login canceled by user.");
                Toast.makeText(this, R.string.you_cancel_ssologin, Toast.LENGTH_SHORT).show();
                finish();
            } else {
                String description = data
                        .getStringExtra("error_description");
                if (description != null) {
                    error = error + ":" + description;
                }
                Log.d("Weibo-authorize", "Login failed: " + error);
                Toast.makeText(this, getString(R.string.login_failed) + error, Toast.LENGTH_SHORT)
                        .show();
            	AppLogger.d("fffff");
                finish();
            }
            return;
        }

        final String KEY_TOKEN = "access_token";
        final String KEY_EXPIRES = "expires_in";
        final String KEY_REFRESHTOKEN = "refresh_token";
    	AppLogger.d("check point");
        String token = data.getStringExtra(KEY_TOKEN);
        String expires = data
                .getStringExtra(KEY_EXPIRES);
        String refreshToken = data.getStringExtra(KEY_REFRESHTOKEN);

        if (Utility.isTaskStopped(task)) {
            task = new SSOTask(SSOActivity.this, token, expires);
            task.executeOnExecutor(MyAsyncTask.THREAD_POOL_EXECUTOR);
        }
    }
}
