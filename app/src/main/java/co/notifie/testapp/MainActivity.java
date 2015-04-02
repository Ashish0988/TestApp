package co.notifie.testapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmBaseAdapter;
import io.realm.RealmResults;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/*
import org.springframework.http.HttpAuthentication;
import org.springframework.http.HttpBasicAuthentication;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;
*/

public class MainActivity extends ActionBarActivity {

    public final static String EXTRA_MESSAGE = "co.notifie.test_app.MESSAGE";
    public final static String NOTIFIE_HOST = "http://notifie.ru"; //192.168.1.39:3000
    public static String AUTH_TOKEN;

    ArrayList<String> list = new ArrayList<String>();
    public static Realm realm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final ListView listview = (ListView) findViewById(R.id.listview);

        //realm = Realm.getInstance(this);
        realm = Realm.getInstance(this, "test4.realm");

        // Sign in and get Token
        signIn();

        RealmResults<NotifeMessage> messages = realm.where(NotifeMessage.class)
                .findAll();


        final MyAdapter my_adapter = new MyAdapter(this, R.layout.message_cell,
                messages, true);

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

                RealmResults<NotifeMessage> result2 = realm.where(NotifeMessage.class).findAll();
                Log.i("RealmResults = ", result2.toString());

                for (NotifeMessage message : messages) {
                    list.add(message.getShort_title());
                    Log.i("App message = ", message.getShort_title());
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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

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
            ImageView imageView = (ImageView) rowView.findViewById(R.id.icon);
            textView.setText(item.getShort_title());

            TextView messageText = (TextView) rowView.findViewById(R.id.message_text);
            messageText.setText(item.getText());

            String image_url = item.getClient().getImage();



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

        public RealmResults<NotifeMessage> getRealmResults() {
            return realmResults;
        }
    }

}
