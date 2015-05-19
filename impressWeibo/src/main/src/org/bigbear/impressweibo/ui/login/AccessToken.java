package org.bigbear.impressweibo.ui.login;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

public class AccessToken implements Parcelable{
	private String access_token;
	private String expires_in;
	private String remind_in;
	private String uid;
	public String getAccess_token(){
		return this.access_token;
	}
	public void setAccess_token(String access_token){
		this.access_token=access_token;
	}
	public String getExpires_in(){
		return this.expires_in;
	}
	public void setExpires_in(String expires_in){
		this.expires_in=expires_in;
	}
	public String getRemind_in(){
		return this.remind_in;
	}
	public void setRemind_in(String remind_in){
		this.remind_in=remind_in;
	}
	public String getUid(){
		return this.uid;
	}
	public void setUid(String uid){
		this.uid=uid;
	}
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		// TODO Auto-generated method stub
		Bundle bundle=new Bundle();
		bundle.putString("access_token",this.access_token);
		bundle.putString("expires_in", this.expires_in);
		bundle.putString("remind_in", this.remind_in);
		bundle.putString("uid",this.uid);
		dest.writeBundle(bundle);
	}
    public static  Parcelable.Creator<AccessToken> CREATOR =new Parcelable.Creator<AccessToken>(){
		public AccessToken createFromParcel(Parcel in){
			Bundle bundle=in.readBundle();
			AccessToken accessToken=new AccessToken();
			accessToken.setAccess_token(bundle.getString("access_token"));
			accessToken.setExpires_in(bundle.getString("expires_in"));
			accessToken.setRemind_in(bundle.getString("remind_in"));
			accessToken.setUid(bundle.getString("uid"));
			return accessToken;
		}

		@Override
		public AccessToken[] newArray(int size) {
			// TODO Auto-generated method stub
			return new AccessToken[size];
		}
	};
	
}   
