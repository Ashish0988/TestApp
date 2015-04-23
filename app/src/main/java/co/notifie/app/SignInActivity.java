package co.notifie.app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import mehdi.sakout.fancybuttons.FancyButton;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;


public class SignInActivity extends ActionBarActivity {

    EditText phoneText;
    EditText emailText;
    EditText passwordText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        emailText = (EditText) findViewById(R.id.email);
        passwordText = (EditText) findViewById(R.id.password);

        FancyButton signUpButton = (FancyButton) findViewById(R.id.sign_in_button);
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_sign_in, menu);
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

    public void signIn() {
        RestClient.get().singIn(emailText.getText().toString(), passwordText.getText().toString(), new Callback<AuthResponce>() {
            @Override
            public void success(AuthResponce authResponce, Response response) {
                // success!
                String auth_token = authResponce.getAuthentication_token();
                Log.i("App", auth_token);

                Notifie app = ((Notifie) getApplicationContext());
                app.setAuth_token(auth_token);
                app.setCurrentUser(authResponce.getUser());
                // you get the point...

                MainActivity.AUTH_TOKEN = auth_token;

                SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
                SharedPreferences.Editor ed = pref.edit();
                ed.putString(MainActivity.AUTH_TOKEN_STRING, auth_token);
                ed.apply();

                Intent intent = new Intent(getBaseContext(), SwipeActivity.class);
                startActivity(intent);

            }

            @Override
            public void failure(RetrofitError error) {
                // something went wrong

                String json =  new String(((TypedByteArray)error.getResponse().getBody()).getBytes());
                Log.e("App", "Error" + json);

                Toast toast = Toast.makeText(getApplicationContext(), json, Toast.LENGTH_LONG);
                toast.setGravity(Gravity.TOP | Gravity.CENTER, 0, 0);
                toast.show();
            }
        });
    }
}
