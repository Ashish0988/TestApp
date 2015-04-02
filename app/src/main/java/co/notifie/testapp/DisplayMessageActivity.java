package co.notifie.testapp;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

import static co.notifie.testapp.MainActivity.NOTIFIE_HOST;

public class DisplayMessageActivity extends ActionBarActivity {

    private final static String TAG = "TestImageGetter";
    public ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_display_message);

        Intent intent = getIntent();
        String message_id = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);

        NotifeMessage message = MainActivity.realm.where(NotifeMessage.class)
                .equalTo("id", message_id)
                .findFirst();

        TextView textView = (TextView) this.findViewById(R.id.detail_message_text_view);

        if (textView != null && message != null) {
            textView.setTextSize(11);
            String html = message.getText();
            URLImageParser url_parser = new URLImageParser(textView, this);
            Spanned htmlSpan = Html.fromHtml(html, url_parser, null);
            textView.setText(htmlSpan);
        }

        // Display auth_token

        TextView authTokenText = (TextView) this.findViewById(R.id.token);

        imageView = (ImageView) this.findViewById(R.id.user_icon);

        if (authTokenText != null) {

            Notifie app = ((Notifie)getApplicationContext());
            String auth_token = app.getAuth_token();

            authTokenText.setText(auth_token);
        }

    }

    @Override
    protected void onStart() {
        super.onStart();

        Notifie app = ((Notifie)getApplicationContext());
        String auth_token = app.getAuth_token();

        RestClient.get().getMe(auth_token, new Callback<User>() {
            @Override
            public void success(User user, Response response) {
                // success!
                String avatar_url = NOTIFIE_HOST + user.getAvatar_url();
                Log.i("App", avatar_url);

                Picasso.with(imageView.getContext())
                        .load(avatar_url)
                        .transform(new CircleTransform())
                        .resize(100, 100)
                        .centerCrop()
                        .into(imageView);

            }

            @Override
            public void failure(RetrofitError error) {
                // something went wrong
                Log.e("App", "Error" + error);
            }
        });
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
