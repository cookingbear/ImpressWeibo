package org.bigbear.impressweibo.ui.adapter;

import java.util.List;

import org.bigbear.impressweibo.R;
import org.bigbear.impressweibo.bean.MessageBean;
import org.bigbear.impressweibo.support.debug.AppLogger;
import org.bigbear.impressweibo.ui.adapter.AbsAdapter.MainViewHolder;

import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class TestAdapter extends AbsAdapter<MessageBean>{

	public TestAdapter(Fragment fragment, List bean) {
		super(fragment, bean);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onBindViewHolder(MainViewHolder holder,int position){
		AppLogger.d("onbindviewholder");
		holder.username.setText(bean.get(position).getUser().getName());
	}
	@Override
	public MainViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		// TODO Auto-generated method stub
		View view =LayoutInflater.from(fragment.getActivity()).inflate(R.layout.test_item_layout, parent,false);
		MainViewHolder viewHolder =new MainViewHolder(view);
		viewHolder.username=(TextView)view.findViewById(R.id.username);
		AppLogger.d("username: "+viewHolder.username.getText());
		return viewHolder;
	}

	@Override
	public void bindViewData(
			org.bigbear.impressweibo.ui.adapter.AbsAdapter.MainViewHolder holder,
			int position) {
		// TODO Auto-generated method stub
		
	}
}
