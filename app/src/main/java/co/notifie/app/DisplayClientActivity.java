package co.notifie.app;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListAdapter;

import io.realm.RealmResults;


public class DisplayClientActivity extends ActionBarActivity implements AbsListView.OnItemClickListener  {

    public static NotifieClient client;
    private FeedAdapter mAdapter;
    private AbsListView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_client);

        Intent intent = getIntent();
        String client_id = intent.getStringExtra(ClientFragment.EXTRA_MESSAGE);

        client = MainActivity.realm.where(NotifieClient.class)
                .equalTo("id", client_id)
                .findFirst();

        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setTitle(client.getName());
            //ab.setSubtitle(R.string.client_feed);
        }

        RealmResults<NotifeMessage> messages;

        messages = MainActivity.realm.where(NotifeMessage.class)
                .equalTo("client.id", client.getId())
                .findAll();

        messages.sort("created_at", RealmResults.SORT_ORDER_DESCENDING);

        mAdapter = new FeedAdapter(this, R.layout.message_cell, messages, true);

        // Set the adapter
        mListView = (AbsListView) findViewById(R.id.clients_list_view);
        ((AdapterView<ListAdapter>) mListView).setAdapter(mAdapter);

        mListView.setOnItemClickListener(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(getResources().getColor(R.color.status_bar_color));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        // getMenuInflater().inflate(R.menu.menu_display_client, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public final static String EXTRA_MESSAGE = "co.notifie.test_app.MESSAGE";

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        final NotifeMessage item = mAdapter.getRealmResults().get(position);

        Intent intent = new Intent(this, DisplayMessageActivity.class);
        intent.putExtra(EXTRA_MESSAGE, item.getId());
        startActivity(intent);

    }
}
