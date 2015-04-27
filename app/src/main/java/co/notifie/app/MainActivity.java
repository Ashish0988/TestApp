package co.notifie.app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.facebook.AppEventsLogger;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.io.IOException;

import io.realm.Realm;
import io.realm.RealmResults;
import me.leolin.shortcutbadger.ShortcutBadgeException;
import me.leolin.shortcutbadger.ShortcutBadger;
import mehdi.sakout.fancybuttons.FancyButton;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class MainActivity extends ActionBarActivity {

    public final static String EXTRA_MESSAGE = "co.notifie.test_app.MESSAGE";
    public final static String NOTIFIE_HOST = "http://notifie.ru"; //http://192.168.1.40:3000
    public final static String TAG = "Notifie";
    public final static String PROJECT_NUMBER = "981231673984";
    public final static String AUTH_TOKEN_STRING = "notifie_auth_token";
    public final static String REALM_DATABASE = "test21.realm";
    public static String AUTH_TOKEN;

    public static Realm realm;
    public static int filter_option;
    //private Context context;

    GoogleCloudMessaging gcm;
    String regid;

    /*
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }*/

    public void putUserToken(String token) {

        RestClient.get().putMe(MainActivity.AUTH_TOKEN, "", token, new Callback<User>() {
            @Override
            public void success(User user, Response response) {
                // success!
                // you get the point...

            }

            @Override
            public void failure(RetrofitError error) {
                // something went wrong
                Log.e("App", "Error" + error);

                Toast toast = Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_LONG);
                toast.setGravity(Gravity.TOP | Gravity.CENTER, 0, 0);
                toast.show();
            }
        });
    }

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
                    putUserToken(regid);


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
    protected void onResume() {
        super.onResume();

        // Logs 'install' and 'app activate' App Events.
        AppEventsLogger.activateApp(this);
    }

    @Override
    protected void onPause() {
        super.onPause();

        // Logs 'app deactivate' App Event.
        AppEventsLogger.deactivateApp(this);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        FancyButton mEmailSignInButton = (FancyButton) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });


        FancyButton signUpButton = (FancyButton) findViewById(R.id.singup_button);
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goSignUp();
            }
        });

        //FacebookSdk.sdkInitialize(getApplicationContext());

        //final ListView listview = (ListView) findViewById(R.id.listview);

        getRegId();

        filter_option = 0;

        /*
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                        .setDefaultFontPath("fonts/MuseoSansCyrl-300.ttf")
                        .setFontAttrId(R.attr.fontPath)
                        .build()
        );
        */

        realm = Realm.getInstance(this, REALM_DATABASE);

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        String token = pref.getString(MainActivity.AUTH_TOKEN_STRING, "");

        if (token != null && token.length() > 0) {

            AUTH_TOKEN = token;
            Intent intent = new Intent(this, SwipeActivity.class);
            startActivity(intent);
        }


        }

    @Override
    protected void onStart() {
        super.onStart();

        int badgeCount = 0;
        try {
            ShortcutBadger.setBadge(getApplicationContext(), badgeCount);
        } catch (ShortcutBadgeException e) {
            //handle the Exception
        }
    }

    public void attemptLogin() {
        Intent intent = new Intent(this, SignInActivity.class);
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
            RealmResults<NotifieClient> result3 = realm.where(NotifieClient.class).findAll();
            result3.clear();
            realm.commitTransaction();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }



}
