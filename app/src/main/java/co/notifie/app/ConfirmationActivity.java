package co.notifie.app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import mehdi.sakout.fancybuttons.FancyButton;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;


public class ConfirmationActivity extends ActionBarActivity {

    EditText codeText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirmation);

        FancyButton signUpButton = (FancyButton) findViewById(R.id.confirmation_button);
        codeText = (EditText)  findViewById(R.id.confirmation_code);

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                confirmAction(codeText.getText().toString());
            }
        });
    }

    public void confirmAction(String code) {
        if (code.length() > 0) {

            /*Intent intent = new Intent(getBaseContext(), UploadPhotoActivity.class);
            intent.putExtra("confirmation_code_message", "1234");
            startActivity(intent);*/
            confirmCode();
        }
    }

    public void confirmCode() {
        RestClient.get().postCode(codeText.getText().toString(), new Callback<AuthResponce>() {
            @Override
            public void success(AuthResponce authResponce, Response response) {
                // success!
                String auth_token = authResponce.getAuthentication_token();
                Log.i("App", auth_token);

                Notifie app = ((Notifie) getApplicationContext());
                app.setAuth_token(auth_token);
                // you get the point...

                MainActivity.AUTH_TOKEN = auth_token;

                SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
                SharedPreferences.Editor ed = pref.edit();
                ed.putString(MainActivity.AUTH_TOKEN_STRING, auth_token);
                ed.apply();

                Intent intent = new Intent(getBaseContext(), UploadPhotoActivity.class);
                intent.putExtra("confirmation_code_message", auth_token);
                startActivity(intent);

            }

            @Override
            public void failure(RetrofitError error) {
                // something went wrong

                String json = new String(((TypedByteArray) error.getResponse().getBody()).getBytes());
                codeText.setError(json);

            }
        });
    }

    @Override
    public void onBackPressed() {
        // your code.
        codeText.setError(getString(R.string.cant_be_blank));
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        // getMenuInflater().inflate(R.menu.menu_confirmation, menu);
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
}
