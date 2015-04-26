package co.notifie.app;

import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;

import io.realm.RealmResults;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by thunder on 16.04.15.
 */
public class ClientCompactAdapter extends ClientAdapter {

    public ClientCompactAdapter(Context context, int resId, RealmResults<NotifieClient> realmResults, boolean automaticUpdate) {
        super(context, resId, realmResults, automaticUpdate);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        NotifieClient item = realmResults.get(position);

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.client_cell_compact, parent, false);

        TextView textView = (TextView) rowView.findViewById(R.id.label);
        TextView textSubView = (TextView) rowView.findViewById(R.id.client_legal);
        TextView messageText = (TextView) rowView.findViewById(R.id.message_text);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.icon);

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
        SimpleDateFormat short_format = new SimpleDateFormat("dd MMMM HH:mm");
        String short_date = "?";

        CheckBox check_box =  (CheckBox) rowView.findViewById(R.id.checkBox);

        check_box.setTag(position);

        if (item.getCheck_for_notifie().equals("1")) {
            check_box.setChecked(true);
        } else {
            check_box.setChecked(false);
        }

        check_box.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                                 @Override
                                                 public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                                    int position = (int) buttonView.getTag();
                                                     NotifieClient item = realmResults.get(position);

                                                     MainActivity.realm.beginTransaction();
                                                     if (isChecked) {
                                                        item.setCheck_for_notifie("1");
                                                     } else {
                                                        item.setCheck_for_notifie("0");
                                                     }
                                                     MainActivity.realm.commitTransaction();

                                                     postSettings(item.getId(), item.getCheck_for_notifie());
                                                 }
                                             }
        );

        /*
        Notifie app = ((Notifie) context.getApplicationContext());
        User currentUser = app.getCurrentUser();

        Boolean allow_notification = true;
        for (NotifieSettings set : currentUser.getSettings()) {

            if (set.getClient_id().equals(item.getId())) {
                if (set.getAllow_notification().equals("false")) {
                    allow_notification = false;
                }
            }

        }*/


        /*
        try {
            Date date = format.parse(item.getCreated_at());
            short_date = short_format.format(date);

            TextView createdAtTextView = (TextView) rowView.findViewById(R.id.created_at);

            DateTime date2 = DateTime.now();

            String frenchShortName = date2.monthOfYear().getAsShortText(Locale.US);

            createdAtTextView.setText(short_date);

        } catch (ParseException e) {
            e.printStackTrace();
        }
        */

        textView.setText(item.getName());
        //messageText.setText(item.getLegal());
        textSubView.setText(item.getLegal());

        String image_url = item.getImage();

        try {
            if (image_url != null && image_url.length() != 0) {
                Picasso.with(context) // getBaseContext()
                        .load(image_url)
                        .transform(new CircleTransform())
                        .resize(80, 80)
                        .centerCrop()
                        .into(imageView);
            }
        } catch (IllegalArgumentException e) {
            Log.v("Path", image_url);
        }

        return rowView;
    }

    //
    // Post Change Settings Allow For Notifie
    //
    public void postSettings(String client_id, String allow) {


        RestClient.get().postSettings(MainActivity.AUTH_TOKEN, client_id, allow, new Callback<SettingsResponce>() {
            @Override
            public void success(SettingsResponce settingsResponce, Response response) {
                // success!
                // you get the point...

            }

            @Override
            public void failure(RetrofitError error) {
                // something went wrong
                Log.e("App", "Error" + error);

                Toast toast = Toast.makeText(context.getApplicationContext(), error.toString(), Toast.LENGTH_LONG);
                toast.setGravity(Gravity.TOP | Gravity.CENTER, 0, 0);
                toast.show();
            }
        });

    }

    public RealmResults<NotifieClient> getRealmResults() {
        return realmResults;
    }
}
