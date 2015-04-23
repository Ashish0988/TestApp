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

import org.joda.time.DateTime;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import io.realm.RealmBaseAdapter;
import io.realm.RealmResults;

/**
 * Created by thunder on 08.04.15.
 */
public class FeedAdapter extends RealmBaseAdapter<NotifeMessage> implements ListAdapter {

    public FeedAdapter(Context context, int resId,
                     RealmResults<NotifeMessage> realmResults,
                     boolean automaticUpdate) {
        super(context, realmResults, automaticUpdate);
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        NotifeMessage item = realmResults.get(position);

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.message_cell, parent, false);

        TextView textView = (TextView) rowView.findViewById(R.id.label);
        TextView textSubView = (TextView) rowView.findViewById(R.id.sub_label);
        TextView messageText = (TextView) rowView.findViewById(R.id.message_text);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.icon);

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
        SimpleDateFormat short_format = new SimpleDateFormat("dd MMMM HH:mm");
        String short_date = "?";

        //try {
            //Date date = format.parse(item.getCreated_at());
            Date date = item.getCreated_at();
            short_date = short_format.format(date);

            TextView createdAtTextView = (TextView) rowView.findViewById(R.id.created_at);

            DateTime date2 = DateTime.now();

            String frenchShortName = date2.monthOfYear().getAsShortText(Locale.US);

            createdAtTextView.setText(short_date);

        //} catch (ParseException e) {
        //    e.printStackTrace();
        //}

        textView.setText(item.getClient().getName());
        messageText.setText(item.getShort_title());
        textSubView.setText(item.getIn_reply_to_screen_name());

        if (item.getOpen_at().length() == 0) {
            // Not opeded
            imageView.setVisibility(View.VISIBLE);
        } else {
            imageView.setVisibility(View.INVISIBLE);

            Log.v("getUnread_comments_sum", " = " + item.getUnread_comments_sum());
            if (item.getUnread_comments_sum() > 0) {
                imageView.setImageResource(R.drawable.unread_comment);
                imageView.setVisibility(View.VISIBLE);
            }
        }

        RealmResults<NotifieComment> comments = MainActivity.realm.where(NotifieComment.class)
                .equalTo("message_id", item.getId())
                .findAll();

        //comments.sort("id", RealmResults.SORT_ORDER_DESCENDING);

        NotifieComment last_comment = null;

        for ( NotifieComment comment : comments) {
            if (comment.getReaded_at().getTime() == 0) {
                last_comment = comment;
            }
        }

        if (last_comment != null) {
            Log.v("Message id:", item.getId() + " last comment:" + last_comment.toString() + " total comments:" + comments.size());
        }

        if (last_comment != null && last_comment.getReaded_at().getTime() == 0) {

            TextView last_user_name = (TextView) rowView.findViewById(R.id.unread_comment_user_name);
            TextView last_comment_text = (TextView) rowView.findViewById(R.id.unread_comment_text);
            TextView last_created_at = (TextView) rowView.findViewById(R.id.unread_comment_created_at);
            ImageView last_user_avatar = (ImageView) rowView.findViewById(R.id.comment_user_icon);

            date = last_comment.getCreated_at();
            short_date = short_format.format(date);
            last_created_at.setText(short_date);

            last_user_name.setText(last_comment.getFrom_user_name() + " –");
            last_comment_text.setText(last_comment.getText());
            String image_url = last_comment.getFrom_user_avatar();

            try {
                if (image_url != null && image_url.length() != 0) {
                    Picasso.with(context) // getBaseContext()
                            .load(image_url)
                            .transform(new CircleTransform())
                            .resize(last_user_avatar.getLayoutParams().width, last_user_avatar.getLayoutParams().height)
                            .centerCrop()
                            .into(last_user_avatar);
                }
            } catch (IllegalArgumentException e) {
                Log.v("Path", image_url);
            }


        } else {
            rowView.findViewById(R.id.has_unread_comments).setVisibility(View.GONE);
        }

        /*
        String image_url = item.getClient().getImage();

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
        */

        return rowView;
    }

    public RealmResults<NotifeMessage> getRealmResults() {
        return realmResults;
    }

}
