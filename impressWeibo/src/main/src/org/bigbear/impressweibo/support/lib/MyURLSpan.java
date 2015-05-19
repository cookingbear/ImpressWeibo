package org.bigbear.impressweibo.support.lib;

import org.bigbear.impressweibo.R;
import org.bigbear.impressweibo.support.debug.AppLogger;
import org.bigbear.impressweibo.support.utils.GlobalContext;
import org.bigbear.impressweibo.support.utils.ThemeUtility;
import org.bigbear.impressweibo.support.utils.Utility;
import org.bigbear.impressweibo.support.utils.WebBrowserSelector;
import org.bigbear.impressweibo.ui.adapter.LongClickLinkDialog;
import org.bigbear.impressweibo.ui.userinfo.UserInfoActivity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Parcel;
import android.provider.Browser;
import android.support.v4.app.FragmentActivity;
import android.text.ParcelableSpan;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.style.ClickableSpan;
import android.view.View;

/**
 * User: qii
 * Date: 12-8-20
 */
public class MyURLSpan extends ClickableSpan implements ParcelableSpan {

    private final String mURL;

    public MyURLSpan(String url) {
        mURL = url;
    }

    public MyURLSpan(Parcel src) {
        mURL = src.readString();
    }

    public int getSpanTypeId() {
        return 11;
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mURL);
    }

    public String getURL() {
        return mURL;
    }
    @Override
    public void onClick(View widget) {
    	AppLogger.d("onclick myurlspan");
        Uri uri = Uri.parse(getURL());
        Context context = widget.getContext();
        if (uri.getScheme().startsWith("http")) {
            String url = uri.toString();
            if (Utility.isWeiboAccountIdLink(url)) {
                Intent intent = new Intent(context, UserInfoActivity.class);
                intent.putExtra("id", Utility.getIdFromWeiboAccountLink(url));
                context.startActivity(intent);
            } else if (Utility.isWeiboAccountDomainLink(url)) {
                Intent intent = new Intent(context, UserInfoActivity.class);
                intent.putExtra("domain", Utility.getDomainFromWeiboAccountLink(url));
                context.startActivity(intent);
            } else {
                //otherwise some urls cant be opened, will be redirected to sina error page
                String openUrl = url;
                if (openUrl.endsWith("/")) {
                    openUrl = openUrl.substring(0, openUrl.lastIndexOf("/"));
                }
                WebBrowserSelector.openLink(context, Uri.parse(openUrl));
            }
        } else {
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            intent.putExtra(Browser.EXTRA_APPLICATION_ID, context.getPackageName());
            context.startActivity(intent);
        }
    }

    public void onLongClick(View widget) {
        Uri data = Uri.parse(getURL());
        if (data != null) {
            String d = data.toString();
            String newValue = "";
            if (d.startsWith("org.bigbear.impressweibo")) {
                int index = d.lastIndexOf("/");
                newValue = d.substring(index + 1);
            } else if (d.startsWith("http")) {
                newValue = d;
            }
            if (!TextUtils.isEmpty(newValue)) {
                Utility.vibrate(widget.getContext(), widget);
                LongClickLinkDialog dialog = new LongClickLinkDialog(data);
                Utility.forceShowDialog((FragmentActivity) widget.getContext(), dialog);
            }
        }
    }

    @Override
    public void updateDrawState(TextPaint tp) {    	
    
        tp.setColor(GlobalContext.getInstance().getActivity().getResources().getColor((R.color.textlink)));
        
//        tp.setUnderlineText(true);
    }
}
