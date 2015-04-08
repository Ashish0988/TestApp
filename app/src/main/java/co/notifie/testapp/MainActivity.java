package co.notifie.testapp;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class MainActivity extends ActionBarActivity {

    public final static String EXTRA_MESSAGE = "co.notifie.test_app.MESSAGE";
    public final static String NOTIFIE_HOST = "http://notifie.ru"; //192.168.1.39:3000
    public final static String TAG = "Notifie";
    public final static String PROJECT_NUMBER = "981231673984";
    public static String AUTH_TOKEN;

    ArrayList<String> list = new ArrayList<String>();
    public static Realm realm;

    private static SwipeRefreshLayout mSwipeRefreshLayout;
    private Context context;

    GoogleCloudMessaging gcm;
    String regid;

    /*
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }*/

    public void getRegId(){
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String msg = "";
                try {
                    if (gcm == null) {
                        gcm = GoogleCloudMessaging.getInstance(getApplicationContext());
                    }
                    regid = gcm.register(PROJECT_NUMBER);
                    msg = "Device registered, registration ID=" + regid;
                    Log.i("GCM",  msg);

                } catch (IOException ex) {
                    msg = "Error :" + ex.getMessage();

                }
                return msg;
            }

            @Override
            protected void onPostExecute(String msg) {
                Log.i("GCM", msg + "\n");
            }
        }.execute(null, null, null);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        final ListView listview = (ListView) findViewById(R.id.listview);

        getRegId();

        /*
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                        .setDefaultFontPath("fonts/MuseoSansCyrl-300.ttf")
                        .setFontAttrId(R.attr.fontPath)
                        .build()
        );
        */

        //realm = Realm.getInstance(this);
        realm = Realm.getInstance(this, "test7.realm");

        // Create Swipe Refresh
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.activity_main_swipe_refresh_layout);
        mSwipeRefreshLayout.setColorScheme(R.color.actionbar_background);

        // Sign in and get Token
        signIn();

        if (mSwipeRefreshLayout != null) {
            mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    loadMessages();
                }
            });
        }

        RealmResults<NotifeMessage> messages = realm.where(NotifeMessage.class)
                .findAll();

        messages.sort("created_at", RealmResults.SORT_ORDER_DESCENDING);

        //final MyAdapter my_adapter = new MyAdapter(this, R.layout.message_cell, messages, true);

        final FeedAdapter my_adapter = new FeedAdapter(this, R.layout.message_cell, messages, true);

        listview.setAdapter(my_adapter);

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {

                final NotifeMessage item = my_adapter.getRealmResults().get(position);

                Intent intent = new Intent(MainActivity.this, DisplayMessageActivity.class);
                intent.putExtra(EXTRA_MESSAGE, item.getId());
                startActivity(intent);

                }
            });
        }

    @Override
    protected void onStart() {
        super.onStart();

        // new SignInTask().execute();

        //new HttpRequestTask().execute();
    }

    public void signIn() {
        RestClient.get().singIn("s.iv@notifie.ru", "123456", new Callback<AuthResponce>() {
            @Override
            public void success(AuthResponce authResponce, Response response) {
                // success!
                String auth_token = authResponce.getAuthentication_token();
                Log.i("App", auth_token);

                Notifie app = ((Notifie) getApplicationContext());
                app.setAuth_token(auth_token);
                // you get the point...

                AUTH_TOKEN = auth_token;

                loadMessages();
            }

            @Override
            public void failure(RetrofitError error) {
                // something went wrong
                Log.e("App", "Error" + error);
            }
        });
    }

    //
    // Load Messages From Server
    //
    public void loadMessages() {

        RestClient.get().getMessages(AUTH_TOKEN, 1, 100, new Callback<MessagesResponce>() {
            @Override
            public void success(MessagesResponce messagesResponce, Response response) {
                // success!
                List<NotifeMessage> messages = messagesResponce.getMessages();

                //
                // Store At Database
                //

                realm.beginTransaction();
                realm.copyToRealmOrUpdate(messages);
                realm.commitTransaction();

                RealmResults<NotifeMessage> result2 =
                        realm.where(NotifeMessage.class)
                        .findAll();

                result2.sort("id", RealmResults.SORT_ORDER_DESCENDING);

                Log.i("RealmResults = ", result2.toString());

                for (NotifeMessage message : messages) {
                    list.add(message.getShort_title());
                    Log.i("App message = ", message.getShort_title());
                }

                if (mSwipeRefreshLayout != null) {
                    mSwipeRefreshLayout.setRefreshing(false);
                }
                //adapter.notifyDataSetChanged();
            }

            @Override
            public void failure(RetrofitError error) {
                // something went wrong
                Log.e("App", "Error" + error);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }


        if (id == R.id.sing_in_action) {
            Intent intent = new Intent(this, SwipeActivity.class);
            startActivity(intent);
        }

        if (id == R.id.action_delete_all) {
            realm.beginTransaction();
            RealmResults<NotifeMessage> result = realm.where(NotifeMessage.class).findAll();
            result.clear();
            RealmResults<NotifieComment> result2 = realm.where(NotifieComment.class).findAll();
            result2.clear();
            realm.commitTransaction();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /*
    public class MyAdapter extends RealmBaseAdapter<NotifeMessage> implements ListAdapter {

        public MyAdapter(Context context, int resId,
                         RealmResults<NotifeMessage> realmResults,
                         boolean automaticUpdate) {
            super(context, realmResults, automaticUpdate);
        }


        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            NotifeMessage item = realmResults.get(position);

            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View rowView = inflater.inflate(R.layout.message_cell, parent, false);

            TextView textView = (TextView) rowView.findViewById(R.id.label);
            TextView textSubView = (TextView) rowView.findViewById(R.id.sub_label);
            TextView messageText = (TextView) rowView.findViewById(R.id.message_text);
            ImageView imageView = (ImageView) rowView.findViewById(R.id.icon);

            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
            SimpleDateFormat short_format = new SimpleDateFormat("dd MMMM HH:mm");
            String short_date = "?";

            try {
                Date date = format.parse(item.getCreated_at());
                short_date = short_format.format(date);

                TextView createdAtTextView = (TextView) rowView.findViewById(R.id.created_at);

                DateTime date2 = DateTime.now();

                String frenchShortName = date2.monthOfYear().getAsShortText(Locale.US);

                createdAtTextView.setText(short_date);

            } catch (ParseException e) {
                e.printStackTrace();
            }

            textView.setText(item.getClient().getName());
            messageText.setText(item.getShort_title());
            textSubView.setText(item.getIn_reply_to_screen_name());

            String image_url = item.getClient().getImage();

            try {
                if (image_url != null && image_url.length() != 0) {
                    Picasso.with(getBaseContext())
                            .load(image_url)
                            .transform(new CircleTransform())
                            .resize(80, 80)
                            .centerCrop()
                            .into(imageView);
                }
            } catch (IllegalArgumentException e) {
                Log.v("Path", image_url);
            }

            return rowView;
        }

        public RealmResults<NotifeMessage> getRealmResults() {
            return realmResults;
        }
    }*/

}
