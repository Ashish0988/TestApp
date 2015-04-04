package co.notifie.testapp;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import io.realm.RealmBaseAdapter;
import io.realm.RealmResults;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

import static co.notifie.testapp.MainActivity.NOTIFIE_HOST;

public class DisplayMessageActivity extends ActionBarActivity {

    private final static String TAG = "TestImageGetter";
    public ImageView imageView;
    private static SwipeRefreshLayout mSwipeRefreshLayout;
    NotifeMessage message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_display_message);

        Intent intent = getIntent();
        String message_id = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);

        message = MainActivity.realm.where(NotifeMessage.class)
                .equalTo("id", message_id)
                .findFirst();

        if (message != null) {

            //
            // Setup Refresh
            //
            mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.message_swipe_refresh_layout);

            if (mSwipeRefreshLayout != null) {
                mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        loadComments(message.getId());
                    }
                });
            }


            //
            // Loading message comments
            //
            loadComments(message.getId());

            final ListView listview = (ListView) findViewById(R.id.comments_list_view);

            LayoutInflater inflater = getLayoutInflater();
            final ViewGroup header = (ViewGroup)inflater.inflate(R.layout.message_list_header, listview, false);
            listview.addHeaderView(header, null, false);

            RealmResults<NotifieComment> comments = MainActivity.realm
                            .where(NotifieComment.class)
                            .equalTo("message_id", message_id)
                            .findAll();

            comments.sort("id", RealmResults.SORT_ORDER_DESCENDING);

            final commentAdapter my_adapter = new commentAdapter(this, R.layout.comment_cell,
                    comments, true);

            listview.setAdapter(my_adapter);

        }

        TextView textView = (TextView) this.findViewById(R.id.detail_message_text_view);

        if (textView != null && message != null) {
            textView.setTextSize(11);
            String html = message.getText();
            URLImageParser url_parser = new URLImageParser(textView, this);
            Spanned htmlSpan = Html.fromHtml(html, url_parser, null);
            textView.setText(htmlSpan);
        }

        // Display auth_token

        TextView authTokenText = (TextView) this.findViewById(R.id.token);

        imageView = (ImageView) this.findViewById(R.id.user_icon);

        if (authTokenText != null) {

            Notifie app = ((Notifie)getApplicationContext());
            String auth_token = app.getAuth_token();

            authTokenText.setText(auth_token);
        }

    }

    //
    // Load Messages From Server
    //
    public void loadComments(String comment_id) {

        RestClient.get().getMessageComments(MainActivity.AUTH_TOKEN, comment_id, new Callback<CommentsResponce>() {
            @Override
            public void success(CommentsResponce messagesResponce, Response response) {
                // success!
                List<NotifieComment> comments = messagesResponce.getComments();

                //
                // Store At Database
                //

                MainActivity.realm.beginTransaction();
                MainActivity.realm.copyToRealmOrUpdate(comments);
                MainActivity.realm.commitTransaction();

                if (mSwipeRefreshLayout != null) {
                    mSwipeRefreshLayout.setRefreshing(false);
                }

            }

            @Override
            public void failure(RetrofitError error) {
                // something went wrong
                Log.e("App", "Error" + error);
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

        Notifie app = ((Notifie)getApplicationContext());
        String auth_token = app.getAuth_token();

        RestClient.get().getMe(auth_token, new Callback<User>() {
            @Override
            public void success(User user, Response response) {
                // success!
                String avatar_url = NOTIFIE_HOST + user.getAvatar_url();
                Log.i("App", avatar_url);

                Picasso.with(imageView.getContext())
                        .load(avatar_url)
                        .transform(new CircleTransform())
                        .resize(100, 100)
                        .centerCrop()
                        .into(imageView);

            }

            @Override
            public void failure(RetrofitError error) {
                // something went wrong
                Log.e("App", "Error" + error);
            }
        });
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

    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }

        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }

    private class commentAdapter extends RealmBaseAdapter<NotifieComment> implements ListAdapter {

        public commentAdapter(Context context, int resId,
                         RealmResults<NotifieComment> realmResults,
                         boolean automaticUpdate) {
            super(context, realmResults, automaticUpdate);
        }


        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            NotifieComment item = realmResults.get(position);

            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View rowView = inflater.inflate(R.layout.comment_cell, parent, false);

            TextView textView = (TextView) rowView.findViewById(R.id.comment_text_label);
            ImageView imageView = (ImageView) rowView.findViewById(R.id.comment_user_icon);

            textView.setText(item.getText());

            String image_url = item.getFrom_user_avatar();

            try {
                if (image_url != null && image_url.length() != 0) {
                    Picasso.with(getBaseContext())
                            .load(image_url)
                            .transform(new CircleTransform())
                            .resize(60, 60)
                            .centerCrop()
                            .into(imageView);
                }
            } catch (IllegalArgumentException e) {
                Log.v("Path", image_url);
            }

            return rowView;
        }

        public RealmResults<NotifieComment> getRealmResults() {
            return realmResults;
        }
    }

}
