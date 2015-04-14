package co.notifie.testapp;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.pnikosis.materialishprogress.ProgressWheel;

import java.util.List;

import mehdi.sakout.fancybuttons.FancyButton;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class UploadPhotoActivity extends ActionBarActivity {

    EditText userName;
    ProgressWheel progress;
    FancyButton signUpButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_photo);

        signUpButton = (FancyButton) findViewById(R.id.final_button);
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveAction();
            }
        });

        progress = (ProgressWheel) findViewById(R.id.progress_wheel);
        userName = (EditText)  findViewById(R.id.full_name);

        signUpButton.setVisibility(View.INVISIBLE);
        searchForClients();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_upload_photo, menu);
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

    public void saveAction() {

    }

    public void searchForClients() {
        RestClient.get().searchForClient(MainActivity.AUTH_TOKEN, new Callback<SearchClientsResponce>() {
            @Override
            public void success(SearchClientsResponce responce, Response response) {
                // success!
                List <NotifieCustomer> names = responce.getFound();
                Log.i("searchForClients", names.toString());

                if (names != null && names.size() > 0) {
                    NotifieCustomer first = names.get(0);
                    userName.setText(first.getFullname());
                }

                progress.setVisibility(View.GONE);
                signUpButton.setVisibility(View.VISIBLE);

            }

            @Override
            public void failure(RetrofitError error) {
                // something went wrong

                Toast toast = Toast.makeText(getBaseContext(), error.toString(), Toast.LENGTH_LONG);
                toast.setGravity(Gravity.TOP | Gravity.CENTER, 0, 0);
                toast.show();
            }
        });
    }
}
