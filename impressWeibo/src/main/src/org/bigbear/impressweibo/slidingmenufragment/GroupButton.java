package org.bigbear.impressweibo.slidingmenufragment;

import java.util.Iterator;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;

import com.slidingmenu.lib.app.SlidingFragmentActivity;

public class GroupButton extends Button{
	private String name;
	public GroupButton newInstance(Context context,String name){
		return new GroupButton(context,name);
	}
	private GroupButton(final Context context,String name){
		super(context);
		this.name=name;
		this.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				FragmentActivity fragment=(FragmentActivity)context;
				Iterator iterator=GroupFragment.fragmentMap.values().iterator();
				FragmentTransaction transaction=fragment.getFragmentManager().beginTransaction();
				while (iterator.hasNext())
					transaction.hide((Fragment)iterator.next());
		        transaction.show(GroupFragment.fragmentMap.get(GroupButton.this.name));				
			}
			
		});
	}
}
