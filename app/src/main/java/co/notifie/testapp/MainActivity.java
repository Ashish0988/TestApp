package co.notifie.testapp;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.io.IOException;

import io.realm.Realm;
import io.realm.RealmResults;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class MainActivity extends ActionBarActivity {

    public final static String EXTRA_MESSAGE = "co.notifie.test_app.MESSAGE";
    public final static String NOTIFIE_HOST = "http://192.168.1.52:3000"; //192.168.1.39:3000
    public final static String TAG = "Notifie";
    public final static String PROJECT_NUMBER = "981231673984";
    public static String AUTH_TOKEN;

    public static Realm realm;
    //private Context context;

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

        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });


        Button signUpButton = (Button) findViewById(R.id.singup_button);
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goSignUp();
            }
        });


        //final ListView listview = (ListView) findViewById(R.id.listview);

        getRegId();

        /*
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                        .setDefaultFontPath("fonts/MuseoSansCyrl-300.ttf")
                        .setFontAttrId(R.attr.fontPath)
                        .build()
        );
        */

        //realm = Realm.getInstance(this);
        realm = Realm.getInstance(this, "test10.realm");

        // Sign in and get Token
        signIn();

        /*
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
            });*/

        }

    @Override
    protected void onStart() {
        super.onStart();

        // new SignInTask().execute();

        //new HttpRequestTask().execute();
    }

    public void signIn() {
        RestClient.get().singIn("stas.bedunkevich@gmail.com", "123456", new Callback<AuthResponce>() {
            @Override
            public void success(AuthResponce authResponce, Response response) {
                // success!
                String auth_token = authResponce.getAuthentication_token();
                Log.i("App", auth_token);

                Notifie app = ((Notifie) getApplicationContext());
                app.setAuth_token(auth_token);
                // you get the point...

                AUTH_TOKEN = auth_token;

            }

            @Override
            public void failure(RetrofitError error) {
                // something went wrong
                Log.e("App", "Error" + error);
            }
        });
    }

    public void attemptLogin() {
        Intent intent = new Intent(this, SwipeActivity.class);
        startActivity(intent);
    }

    public void goSignUp() {
        Intent intent = new Intent(this, SignUpActivity.class);
        startActivity(intent);
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
            attemptLogin();
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



}
