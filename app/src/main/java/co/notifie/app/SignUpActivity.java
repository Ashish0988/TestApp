package co.notifie.app;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import mehdi.sakout.fancybuttons.FancyButton;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;


public class SignUpActivity extends ActionBarActivity {
    EditText phoneText;
    EditText emailText;
    EditText passwordText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        emailText = (EditText) findViewById(R.id.email_text);
        passwordText = (EditText) findViewById(R.id.password);
        phoneText = (EditText) findViewById(R.id.phone);
        //phoneText.setText("+7");
        phoneText.addTextChangedListener(new PhoneNumberFormattingTextWatcher());

        Button terms = (Button) findViewById(R.id.terms);
        Button privacy = (Button) findViewById(R.id.privacy);

        terms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = "http://notifie.ru/legal";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });

        privacy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = "http://notifie.ru/privacy";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });

        FancyButton signUpButton = (FancyButton) findViewById(R.id.sign_up_button);
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signUpAction();
            }
        });
    }

    private void signUpAction() {

        UserRequestWrapper params = new UserRequestWrapper();
        NotifieUser user = new NotifieUser();

        user.setPhone(phoneText.getText().toString());
        user.setEmail(emailText.getText().toString());
        user.setPassword(passwordText.getText().toString());

        params.setUser(user);

        RestClient.get().singUp(params, new Callback<AuthResponce>() {
            @Override
            public void success(AuthResponce authResponce, Response response) {
                // success!
                String confirmation_code = authResponce.getPhone_confirm_code();
                Log.i("confirmation_code = ", confirmation_code);

                // you get the point...
                Intent intent = new Intent(getBaseContext(), ConfirmationActivity.class);
                intent.putExtra("confirmation_code_message", "1234");
                startActivity(intent);
            }

            private String getStringResourceByName(String aString) {
                String packageName = getPackageName();

                String error_id = aString.replaceAll("\\s+", "_").replace(" ", "_")
                        .replace("'", "").replace(".", "_").replace(",", "_").replace("(", "").replace(")", "");
                int resId = getResources().getIdentifier(error_id, "string", packageName);
                if (resId == 0) {
                    return aString;
                }
                return getString(resId);
            }

            @Override
            public void failure(RetrofitError error) {
                // something went wrong

                String return_string = null;

                if (error.getResponse() != null) {
                    return_string = new String(((TypedByteArray) error.getResponse().getBody()).getBytes());
                } else {

                    Toast toast = Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.BOTTOM | Gravity.CENTER, 0, 0);
                    toast.show();
                    return;
                }

                JSONObject jsnobject = null;
                try {
                    jsnobject = new JSONObject(return_string);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                JSONObject errorsObject = null;
                try {
                    errorsObject = jsnobject.getJSONObject("errors");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if (errorsObject != null)
                for(int i = 0; i < errorsObject.names().length(); i++) {
                    String key = "";
                    try {
                        key = errorsObject.names().getString(i);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    JSONArray valueArray = null;
                    try {
                        valueArray = errorsObject.getJSONArray(errorsObject.names().getString(i));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    String value = "";
                    if (valueArray != null)
                        for (int j = 0; j < valueArray.length(); j++) {
                            try {
                                value += getStringResourceByName(valueArray.getString(j));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }


                    if (key.equals("email")) {
                        emailText.setError(value);
                    }

                    if (key.equals("phone")) {
                        phoneText.setError(value);
                    }

                    if (key.equals("password")) {
                        passwordText.setError(value);
                    }
                }


                //emailText.setError(json);
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_sign_up, menu);
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
