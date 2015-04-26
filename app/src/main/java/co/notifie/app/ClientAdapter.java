package co.notifie.app;

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

        TextView newMessagesCount = (TextView) rowView.findViewById(R.id.client_new_messages_count);
        TextView newCommentsCount = (TextView) rowView.findViewById(R.id.client_new_comments_count);

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
        SimpleDateFormat short_format = new SimpleDateFormat("dd MMMM HH:mm");
        String short_date = "?";


        RealmResults<NotifeMessage> messages = MainActivity.realm.where(NotifeMessage.class)
                .equalTo("client_id", item.getId())
                .findAll();

        Integer unread_comments = 0;
        Integer unread_messages = 0;
        RealmResults<NotifieComment> comments;

        for ( NotifeMessage message : messages) {

            if (message.getOpen_at().length() == 0) {
                unread_messages += 1;
            }
            comments = MainActivity.realm.where(NotifieComment.class)
                    .equalTo("message_id", message.getId())
                    .findAll();

            for ( NotifieComment comment : comments) {
                if (comment.getReaded_at().getTime() == 0) {
                    unread_comments += 1;
                }
            }

        }

        if (unread_messages == 0) {
            newMessagesCount.setVisibility(View.GONE);
            rowView.findViewById(R.id.client_new_messages_icon).setVisibility(View.GONE);
        } else {
            newMessagesCount.setText("" + unread_messages);
        }

        if (unread_comments == 0) {
            newCommentsCount.setVisibility(View.GONE);
            rowView.findViewById(R.id.client_new_comments_icon).setVisibility(View.GONE);

        } else {
            newCommentsCount.setText("" + unread_comments);
        }

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
                        .resize(imageView.getLayoutParams().width, imageView.getLayoutParams().height)
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
