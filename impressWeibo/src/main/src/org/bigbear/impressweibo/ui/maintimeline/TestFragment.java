package org.bigbear.impressweibo.ui.maintimeline;

import org.bigbear.impressweibo.R;
import org.bigbear.impressweibo.bean.AccountBean;
import org.bigbear.impressweibo.bean.UserBean;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public class TestFragment extends Fragment{
    private static final String ARGUMENTS_ACCOUNT_EXTRA = FriendsTimeLineFragment.class.getName() + ":account_extra";
    private static final String ARGUMENTS_USER_EXTRA = FriendsTimeLineFragment.class.getName() + ":userBean_extra";
    private static final String ARGUMENTS_TOKEN_EXTRA = FriendsTimeLineFragment.class.getName() + ":token_extra";
	@Override
	public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState){
		return inflater.inflate(R.layout.test_layout,container,false);
	}
    public static TestFragment newInstance(AccountBean accountBean, UserBean userBean,
                String token) {
    	TestFragment fragment = new TestFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(ARGUMENTS_ACCOUNT_EXTRA, accountBean);
        bundle.putParcelable(ARGUMENTS_USER_EXTRA, userBean);
        bundle.putString(ARGUMENTS_TOKEN_EXTRA, token);
        fragment.setArguments(bundle);
        return fragment;
        }
    
}
