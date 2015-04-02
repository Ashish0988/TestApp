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
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.realm.Realm;
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
    public final static String NOTIFIE_HOST = "http://192.168.1.39:3000"; //192.168.1.39:3000
    public static String AUTH_TOKEN;

    ArrayList<String> list = new ArrayList<String>();
    MySimpleArrayAdapter adapter;
    Realm realm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final ListView listview = (ListView) findViewById(R.id.listview);

        /*
        String[] values = new String[] { "Android порождённый русскоязычной версией Windows (вернее, программой fontview.exe, которая входит в дефолтную поставку, начиная аж с незапамятных времён Windows 95). Таким хитроумным способом Винда демонстрирует юзеру", "iPhone", "WindowsMobile",
                "BlackberryСъешь ещё этих мягких французских булок, да выпей чаю",
                "WebOS", "Ubuntu", "Windows7", "Max OS X"
        };

        for (int i = 0; i < values.length; ++i) {
            list.add(values[i]);
        }*/

        //realm = Realm.getInstance(this);
        realm = Realm.getInstance(this, "test1.realm");

        // Sign in and get Token
        signIn();

        adapter = new MySimpleArrayAdapter(this, list);

        //final StableArrayAdapter adapter = new StableArrayAdapter(this,
        //        R.layout.message_cell, R.id.label, list);

        //final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.message_cell, R.id.label, list);
        //setListAdapter(adapter);

        listview.setAdapter(adapter);

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
                final String item = (String) parent.getItemAtPosition(position);

                Intent intent = new Intent(MainActivity.this, DisplayMessageActivity.class);
                intent.putExtra(EXTRA_MESSAGE, item);
                startActivity(intent);

                /*view.animate().setDuration(2000).alpha(0)
                        .withEndAction(new Runnable() {
                            @Override
                            public void run() {
                                list.remove(item);
                                adapter.notifyDataSetChanged();
                                view.setAlpha(1);
                            }
                        });*/
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

        RestClient.get().getMessages(AUTH_TOKEN, 1, new Callback<MessagesResponce>() {
            @Override
            public void success(MessagesResponce messagesResponce, Response response) {
                // success!
                List<NotifeMessage> messages = messagesResponce.getMessages();

                //
                // Store At Database
                //

                realm.beginTransaction();
                realm.copyToRealm(messages);
                realm.commitTransaction();

                RealmResults<NotifeMessage> result2 = realm.where(NotifeMessage.class).findAll();
                Log.i("RealmResults = ", result2.toString());

                for (NotifeMessage message : messages) {
                    list.add(message.getShort_title());
                    Log.i("App message = ", message.getShort_title());
                }

                adapter.notifyDataSetChanged();
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

    /** Called when the user clicks the Send button */
    public void sendMessage(View view) {
        // Do something in response to button
        /*
        Intent intent = new Intent(this, DisplayMessageActivity.class);
        EditText editText = (EditText) findViewById(R.id.edit_message);
        String message = editText.getText().toString();
        intent.putExtra(EXTRA_MESSAGE, message);
        startActivity(intent);
        */
    }

    public class MySimpleArrayAdapter extends ArrayAdapter<String> {
        private final Context context;
        private final List<String> values;

        public MySimpleArrayAdapter(Context context, List<String> values) {
            super(context, R.layout.message_cell, values);
            this.context = context;
            this.values = values;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View rowView = inflater.inflate(R.layout.message_cell, parent, false);
            TextView textView = (TextView) rowView.findViewById(R.id.label);
            ImageView imageView = (ImageView) rowView.findViewById(R.id.icon);
            textView.setText(values.get(position));

            TextView messageText = (TextView) rowView.findViewById(R.id.message_text);
            messageText.setText(values.get(position));

            // Change the icon for Windows and iPhone
            String s = values.get(position);
            if (s.startsWith("Windows7") || s.startsWith("iPhone")
                    || s.startsWith("Solaris")) {
                imageView.setImageResource(R.drawable.ic_action_call);
            } else {
                imageView.setImageResource(R.drawable.ic_action_refresh);
            }

            return rowView;
        }
    }


    private class StableArrayAdapter extends ArrayAdapter<String> {

        HashMap<String, Integer> mIdMap = new HashMap<String, Integer>();

        public StableArrayAdapter(Context context, int textViewResourceId,
                                  List<String> objects) {
            super(context, textViewResourceId, objects);
            for (int i = 0; i < objects.size(); ++i) {
                mIdMap.put(objects.get(i), i);
            }
        }

        @Override
        public long getItemId(int position) {
            String item = getItem(position);
            return mIdMap.get(item);
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

    }

}
