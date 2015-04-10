package co.notifie.testapp;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;

import io.realm.RealmBaseAdapter;
import io.realm.RealmResults;

/**
 * Created by thunder on 09.04.15.
 */
public class ClientAdapter extends RealmBaseAdapter<NotifieClient> implements ListAdapter {

    public ClientAdapter(Context context, int resId,
                       RealmResults<NotifieClient> realmResults,
                       boolean automaticUpdate) {
        super(context, realmResults, automaticUpdate);
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        NotifieClient item = realmResults.get(position);

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.client_cell, parent, false);

        TextView textView = (TextView) rowView.findViewById(R.id.label);
        TextView textSubView = (TextView) rowView.findViewById(R.id.client_legal);
        TextView messageText = (TextView) rowView.findViewById(R.id.message_text);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.icon);

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
        SimpleDateFormat short_format = new SimpleDateFormat("dd MMMM HH:mm");
        String short_date = "?";

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

    public RealmResults<NotifieClient> getRealmResults() {
        return realmResults;
    }

}
