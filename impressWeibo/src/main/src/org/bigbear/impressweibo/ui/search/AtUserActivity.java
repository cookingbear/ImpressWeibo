package org.bigbear.impressweibo.ui.search;

import org.bigbear.impressweibo.R;
import org.bigbear.impressweibo.ui.interfaces.AbstractAppActivity;

import android.app.ActionBar;
import android.os.Bundle;
import android.view.MenuItem;

/**
 * User: qii
 * Date: 12-10-8
 */
public class AtUserActivity extends AbstractAppActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(R.string.at_other);

        String token = getIntent().getStringExtra("token");
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .replace(android.R.id.content, AtUserFragment.newInstance(token))
                    .commit();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return true;
    }
}
