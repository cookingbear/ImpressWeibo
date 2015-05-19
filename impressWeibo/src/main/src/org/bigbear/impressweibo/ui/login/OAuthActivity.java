package org.bigbear.impressweibo.ui.login;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.bigbear.impressweibo.R;
import org.bigbear.impressweibo.bean.AccountBean;
import org.bigbear.impressweibo.bean.UserBean;
import org.bigbear.impressweibo.dao.URLHelper;
import org.bigbear.impressweibo.dao.login.OAuthDao;
import org.bigbear.impressweibo.support.database.AccountDBTask;
import org.bigbear.impressweibo.support.debug.AppLogger;
import org.bigbear.impressweibo.support.error.WeiboException;
import org.bigbear.impressweibo.support.lib.MyAsyncTask;
import org.bigbear.impressweibo.support.utils.GlobalContext;
import org.bigbear.impressweibo.support.utils.Utility;
import org.bigbear.impressweibo.ui.interfaces.AbstractAppActivity;

import com.google.gson.Gson;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * User: qii
 * Date: 12-7-28
 */
public class OAuthActivity extends AbstractAppActivity {

    private WebView webView;
    private MenuItem refreshItem;
    private boolean pressed=false;
    private Handler handler=new Handler();
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.oauthactivity_layout);
        //ActionBar actionBar = getActionBar();
        //actionBar.setDisplayHomeAsUpEnabled(false);
        //actionBar.setTitle(getString(R.string.login));
        getActionBar().hide();
        webView = (WebView) findViewById(R.id.webView);
        webView.setWebViewClient(new WeiboWebViewClient());

        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setSaveFormData(false);
        settings.setSavePassword(false);
        settings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        settings.setRenderPriority(WebSettings.RenderPriority.HIGH);

        CookieSyncManager.createInstance(this);
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.removeAllCookie();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        webView.clearCache(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.actionbar_menu_oauthactivity, menu);
        refreshItem = menu.findItem(R.id.menu_refresh);
        refresh();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = MainActivity.newIntent();
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                return true;
            case R.id.menu_refresh:
                refresh();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void refresh() {
        webView.clearView();
        webView.loadUrl("about:blank");
        LayoutInflater inflater = (LayoutInflater) getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);
        ImageView iv = (ImageView) inflater.inflate(R.layout.refresh_action_view, null);

        Animation rotation = AnimationUtils.loadAnimation(this, R.anim.refresh);
        iv.startAnimation(rotation);

        refreshItem.setActionView(iv);
        webView.loadUrl(getWeiboOAuthUrl());
    }

    private void completeRefresh() {
    	AppLogger.d("completeRefresh");
        if (refreshItem.getActionView() != null) {
            refreshItem.getActionView().clearAnimation();
            refreshItem.setActionView(null);
        }
    }

    private String getWeiboOAuthUrl() {
        Map<String, String> parameters = new HashMap<String, String>();
        parameters.put("client_id", URLHelper.APP_KEY);
        parameters.put("response_type", "code");
        parameters.put("redirect_uri", URLHelper.DIRECT_URL);
        parameters.put("display", "mobile");
        return URLHelper.URL_OAUTH2_ACCESS_AUTHORIZE + "?" + Utility.encodeUrl(parameters)
                + "&scope=friendships_groups_read,friendships_groups_write";
    }

    private class WeiboWebViewClient extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
        	AppLogger.d("onPageStarted "+url);
            	
            if (url.startsWith(URLHelper.DIRECT_URL)) {
                handleRedirectUrl(view, url);
                view.stopLoading();
                return;
            }
            super.onPageStarted(view, url, favicon);
        }

        @Override
        public void onReceivedError(WebView view, int errorCode, String description,
                String failingUrl) {
            super.onReceivedError(view, errorCode, description, failingUrl);
            new SinaWeiboErrorDialog().show(getSupportFragmentManager(), "");
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            if (!url.equals("about:blank")) {
                completeRefresh();
            }
        }
    }

    private void handleRedirectUrl(WebView view, String url) {
    	AppLogger.d("handleRedirectUrl");
        Bundle values = Utility.parseUrl(url);
        String error = values.getString("error");
        String error_code = values.getString("error_code");  
        String code=values.getString("code");
        if (error == null && error_code == null) {
        	if (code!=null)
				try {
					getAccessToken(code);
				} catch (MalformedURLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
        	
        } else {
            Toast.makeText(OAuthActivity.this, getString(R.string.you_cancel_login),
                    Toast.LENGTH_SHORT).show();
            finish();
        }
    }
    private void getAccessToken(String code) throws MalformedURLException{
    	String urlStr=new String(URLHelper.ACCESS_TOKEN);
    	String paramStr="client_id="+URLHelper.APP_KEY+"&client_secret="+URLHelper.APP_SECRET+"&grant_type=authorization_code&redirect_uri="+URLHelper.DIRECT_URL+"&code="+code;
    	AppLogger.d("access_token from code: "+urlStr);
    	final HttpPost post=new HttpPost(URI.create(urlStr+"?"+paramStr));
    	final HttpClient client=new DefaultHttpClient();
    	
    		new Thread(new Runnable(){

				@Override
				public void run() {
					// TODO Auto-generated method stub
					HttpResponse response=null;
	    			try {
						response=client.execute(post);
						AppLogger.d(response.getStatusLine().getStatusCode()+"");
						if (response.getStatusLine().getStatusCode()==200){
							BufferedReader reader=new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
							AppLogger.d("reader built");
							StringBuilder json=new StringBuilder();
							String line=null;
							while  ((line=reader.readLine())!=null){
								AppLogger.d("line");
								json.append(line);
								
							}
							AppLogger.d(json.toString());
							Gson gson=new Gson();
							AccessToken accessTokenBean=gson.fromJson(json.toString(), AccessToken.class);
							final String access_token = accessTokenBean.getAccess_token();
				            final String expires_time = accessTokenBean.getExpires_in();
				            Bundle values=new Bundle();
				            values.putString("expires_in", expires_time);
				            values.putString("access_token",access_token);
				            AppLogger.d("gson built");
				            final Intent intent = new Intent();
				            intent.putExtras(values);
				            Runnable resultSet=new Runnable(){
				                public void run(){
				            	setResult(RESULT_OK, intent);				            
				                new OAuthTask(OAuthActivity.this).execute(access_token, expires_time);}
				            };
				            handler.post(resultSet);
						}
					} catch (ClientProtocolException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
	    			
				}

    		}).start();

    	
    	try {
			webView.postUrl(urlStr,URLEncoder.encode(paramStr,"utf-8").getBytes());
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
        	if (this.pressed==true){
                super.onBackPressed();
            	this.finish();
            }
            else {
            	this.pressed=true;
            	Toast.makeText(this,getString(R.string.pressed_again),Toast.LENGTH_LONG).show();
            }
        }
    }

    private static class OAuthTask extends MyAsyncTask<String, UserBean, DBResult> {

        private WeiboException e;
        private ProgressFragment progressFragment = ProgressFragment.newInstance();
        private WeakReference<OAuthActivity> oAuthActivityWeakReference;

        private OAuthTask(OAuthActivity activity) {
            oAuthActivityWeakReference = new WeakReference<OAuthActivity>(activity);
        }

        @Override
        protected void onPreExecute() {
            progressFragment.setAsyncTask(this);
            OAuthActivity activity = oAuthActivityWeakReference.get();
            if (activity != null) {
                progressFragment.show(activity.getSupportFragmentManager(), "");
            }
        }

        @Override
        protected DBResult doInBackground(String... params) {
            String token = params[0];
            long expiresInSeconds = Long.valueOf(params[1]);

            try {
            	AppLogger.d("userBean will be built");
                UserBean user = new OAuthDao(token).getOAuthUserInfo();
                AccountBean account = new AccountBean();
                account.setAccess_token(token);
                account.setExpires_time(System.currentTimeMillis() + expiresInSeconds * 1000);
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
        protected void onCancelled(DBResult dbResult) {
            super.onCancelled(dbResult);
            if (progressFragment != null) {
                progressFragment.dismissAllowingStateLoss();
            }

            OAuthActivity activity = oAuthActivityWeakReference.get();
            if (activity == null) {
                return;
            }

            if (e != null) {
                Toast.makeText(activity, e.getError(), Toast.LENGTH_SHORT).show();
            }
            activity.webView.loadUrl(activity.getWeiboOAuthUrl());
        }

        @Override
        protected void onPostExecute(DBResult dbResult) {
            if (progressFragment.isVisible()) {
                progressFragment.dismissAllowingStateLoss();
            }
            OAuthActivity activity = oAuthActivityWeakReference.get();
            if (activity == null) {
                return;
            }
            switch (dbResult) {
                case add_successfuly:
                    Toast.makeText(activity, activity.getString(R.string.login_success),
                            Toast.LENGTH_SHORT).show();
                    break;
                case update_successfully:
                	AppLogger.d("456");  
                    Toast.makeText(activity, activity.getString(R.string.update_account_success),
                            Toast.LENGTH_SHORT).show();
                    break;
            }
            activity.finish();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (isFinishing()) {
            webView.stopLoading();
        }
    }

    public static class ProgressFragment extends DialogFragment {

        MyAsyncTask asyncTask = null;

        public static ProgressFragment newInstance() {
            ProgressFragment frag = new ProgressFragment();
            frag.setRetainInstance(true);
            Bundle args = new Bundle();
            frag.setArguments(args);
            return frag;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            ProgressDialog dialog = new ProgressDialog(getActivity());
            dialog.setMessage(getString(R.string.oauthing));
            dialog.setIndeterminate(false);
            dialog.setCancelable(true);
            return dialog;
        }

        @Override
        public void onCancel(DialogInterface dialog) {
            if (asyncTask != null) {
                asyncTask.cancel(true);
            }
            super.onCancel(dialog);
        }

        void setAsyncTask(MyAsyncTask task) {
            asyncTask = task;
        }
    }

    public static class SinaWeiboErrorDialog extends DialogFragment {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage(R.string.sina_server_error)
                    .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
            return builder.create();
        }
    }

    public static enum DBResult {
        add_successfuly, update_successfully
    }
}
