package org.bigbear.impressweibo.ui.basefragment;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import org.bigbear.impressweibo.R;
import org.bigbear.impressweibo.bean.ListBean;
import org.bigbear.impressweibo.bean.MessageBean;
import org.bigbear.impressweibo.bean.MessageListBean;
import org.bigbear.impressweibo.dao.destroy.DestroyStatusDao;
import org.bigbear.impressweibo.support.debug.AppLogger;
import org.bigbear.impressweibo.support.error.WeiboException;
import org.bigbear.impressweibo.support.lib.MyAsyncTask;
import org.bigbear.impressweibo.support.utils.GlobalContext;
import org.bigbear.impressweibo.support.utils.Utility;
import org.bigbear.impressweibo.ui.actionmenu.StatusSingleChoiceModeListener;
import org.bigbear.impressweibo.ui.adapter.StatusListAdapter;
import org.bigbear.impressweibo.ui.interfaces.IRemoveItem;

import com.google.gson.Gson;

import android.os.Bundle;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Toast;

/**
 * User: qii
 * Date: 12-7-29
 */
public abstract class AbstractMessageTimeLineFragment<T extends ListBean<MessageBean, ?>>
        extends AbstractTimeLineFragment<T> implements IRemoveItem {

    private RemoveTask removeTask;

    protected void showNewMsgToastMessage(ListBean<MessageBean, ?> newValue) {
        if (newValue != null && getActivity() != null) {
            if (newValue.getSize() == 0) {
                Toast.makeText(getActivity(), getString(R.string.no_new_message),
                        Toast.LENGTH_SHORT).show();
            } else if (newValue.getSize() > 0) {
                Toast.makeText(getActivity(),
                        getString(R.string.total) + newValue.getSize() + getString(
                                R.string.new_messages), Toast.LENGTH_SHORT).show();
            }
        }
    }

    protected void clearAndReplaceValue(ListBean<MessageBean, ?> value) {
        getList().getItemList().clear();
        getList().getItemList().addAll(value.getItemList());
        getList().setTotal_number(value.getTotal_number());
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //getListView().setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
        //getListView().setOnItemLongClickListener(onItemLongClickListener);
    }

    /*private AdapterView.OnItemLongClickListener onItemLongClickListener
            = new AdapterView.OnItemLongClickListener() {
        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
            if (position - getListView().getHeaderViewsCount() < getList().getSize()
                    && position - getListView().getHeaderViewsCount() >= 0
                    && timeLineAdapter.getItem(position - getListView().getHeaderViewsCount())
                    != null) {
                MessageBean msg = getList().getItemList()
                        .get(position - getListView().getHeaderViewsCount());
                StatusSingleChoiceModeListener choiceModeListener
                        = new StatusSingleChoiceModeListener(getListView(),
                        (StatusListAdapter) timeLineAdapter, AbstractMessageTimeLineFragment.this,
                        msg);
                if (actionMode != null) {
                    actionMode.finish();
                    actionMode = null;
                }

                getListView().setItemChecked(position, true);
                getAdapter().notifyDataSetChanged();
                actionMode = getActivity().startActionMode(choiceModeListener);
                return true;
            }
            return false;
        }
    };*/

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }

    /**
     * fix android bug,long press a item in the first tab's listview, rotate screen, the item
     * background is still blue(it is checked),
     * but if you test on other tabs' lstview, the item is not checked
     */
    @Override
    public void onResume() {
        super.onResume();
        clearActionMode();
    }

    @Override
    protected void buildListAdapter() {
    	PrintWriter writer=null;
		try {
			writer = new PrintWriter(new FileWriter(new File("C:\\Users\\Administrator\\Desktop\\abc.txt")));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	Gson gson=new Gson();
    	String json=gson.toJson(getList(),MessageListBean.class);
    	writer.print(json);
    	writer.close();
    	AppLogger.d("json has bean writen");
        //timeLineAdapter = new StatusListAdapter(this, getList().getItemList(), getListView(), true);
        
        //getListView().setAdapter(timeLineAdapter);
    }

    @Override
    public void removeItem(int position) {
        clearActionMode();
        if (removeTask == null || removeTask.getStatus() == MyAsyncTask.Status.FINISHED) {
            removeTask = new RemoveTask(GlobalContext.getInstance().getSpecialToken(),
                    getList().getItemList().get(position).getId(), position);
            removeTask.executeOnExecutor(MyAsyncTask.THREAD_POOL_EXECUTOR);
        }
    }

    @Override
    public void removeCancel() {
        clearActionMode();
    }

    class RemoveTask extends MyAsyncTask<Void, Void, Boolean> {

        String token;
        String id;
        int positon;
        WeiboException e;

        public RemoveTask(String token, String id, int positon) {
            this.token = token;
            this.id = id;
            this.positon = positon;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            DestroyStatusDao dao = new DestroyStatusDao(token, id);
            try {
                return dao.destroy();
            } catch (WeiboException e) {
                this.e = e;
                cancel(true);
                return false;
            }
        }

        @Override
        protected void onCancelled(Boolean aBoolean) {
            super.onCancelled(aBoolean);
            if (Utility.isAllNotNull(this.e, getActivity())) {
                Toast.makeText(getActivity(), e.getError(), Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
           /* if (aBoolean) {
                ((StatusListAdapter) timeLineAdapter).removeItem(positon);
            }*/
        }
    }
}
