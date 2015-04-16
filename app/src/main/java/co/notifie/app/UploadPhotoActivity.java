package co.notifie.app;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.facebook.Request;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.model.GraphUser;
import com.pnikosis.materialishprogress.ProgressWheel;
import com.squareup.picasso.Picasso;

import java.util.List;

import mehdi.sakout.fancybuttons.FancyButton;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class UploadPhotoActivity extends ActionBarActivity {

    EditText userName;
    ProgressWheel progress;
    FancyButton signUpButton;
    FancyButton faceBookButton;
    ImageButton avatarButton;

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

        faceBookButton = (FancyButton) findViewById(R.id.facebook_button);
        faceBookButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openFacebookSession();
            }
        });

        progress = (ProgressWheel) findViewById(R.id.progress_wheel);
        userName = (EditText)  findViewById(R.id.full_name);
        avatarButton = (ImageButton) findViewById(R.id.avatar);

        avatarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(pickPhoto, 1);//one can be replaced with any action code
            }
        });

        signUpButton.setVisibility(View.INVISIBLE);
        searchForClients();
    }

    private void openFacebookSession(){

        Session.openActiveSession(this, true, new Session.StatusCallback() {
            @Override
            public void call(Session session, SessionState state, Exception exception) {
                if (exception != null) {
                    Log.d("Facebook", exception.getMessage());
                }
                Log.d("Facebook", "Session State: " + session.getState());
                // you can make request to the /me API or do other stuff like post, etc. here
                if (session.isOpened()) {

                    Request.newMeRequest(session, new Request.GraphUserCallback() {
                        @Override
                        public void onCompleted(GraphUser graphUser, com.facebook.Response response) {
                            try {
                                String imgUrl = "http://graph.facebook.com/" + graphUser.getId() + "/picture?type=large";

                                Picasso.with(getBaseContext())
                                        .load(imgUrl)
                                        .transform(new CircleTransform())
                                        .resize(160, 160)
                                        .centerCrop()
                                        .into(avatarButton);

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }).executeAsync();
                }
            }
        });
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

    Uri outputUri;
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

        Session.getActiveSession().onActivityResult(this, requestCode, resultCode, imageReturnedIntent);

        if (resultCode == RESULT_OK)
        {
            Uri imageUri = imageReturnedIntent.getData();
            try {
                //Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);

                Picasso.with(this) // getBaseContext()
                        .load(imageUri)
                        .transform(new CircleTransform())
                        .resize(160, 160)
                        .centerCrop()
                        .into(avatarButton);

            } catch (Exception e) {

            }
        }
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
