package org.bigbear.impressweibo.slidingmenufragment;

import java.util.HashMap;
import java.util.Map;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class GroupFragment extends Fragment{
	public static Map<String,Fragment> fragmentMap=new HashMap<String,Fragment>();
	public static void initGroupFragment(String[] idstr){
		for (String id:idstr){
			GroupFragment groupFragment=new GroupFragment();
			
		}
	}
	@Override
	public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState){
		View view =null;
		
		return view;
	}
}
