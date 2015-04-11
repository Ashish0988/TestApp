package co.notifie.testapp;

import com.google.gson.annotations.Expose;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.RealmClass;

/**
 * Created by thunder on 02.04.15.
 */

@RealmClass
public class NotifeMessage extends RealmObject {

    @Expose
    @PrimaryKey
    private String id;

    @Expose
    private String client_id;
    @Expose
    private String created_at;
    @Expose
    private String favorited;
    @Expose
    private String in_reply_to_screen_name;
    @Expose
    private String open_at;
    @Expose
    private String read;
    @Expose
    private String request;
    @Expose
    private String short_title;
    @Expose
    private String text;
    private String updated_at;
    @Expose
    private String unread_comments_sum;

    @Expose
    private NotifieClient client;
    @Expose
    private RealmList<NotifieComment> comments;

    public String getClient_id() {
        return client_id;
    }

    public void setClient_id(String client_id) {
        this.client_id = client_id;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getFavorited() {
        return favorited;
    }

    public void setFavorited(String favorited) {
        this.favorited = favorited;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIn_reply_to_screen_name() {
        return in_reply_to_screen_name;
    }

    public void setIn_reply_to_screen_name(String in_reply_to_screen_name) {
        this.in_reply_to_screen_name = in_reply_to_screen_name;
    }

    public String getOpen_at() {
        return open_at;
    }

    public void setOpen_at(String open_at) {
        this.open_at = open_at;
    }

    public String getRead() {
        return read;
    }

    public void setRead(String read) {
        this.read = read;
    }

    public String getRequest() {
        return request;
    }

    public void setRequest(String request) {
        this.request = request;
    }

    public String getShort_title() {
        return short_title;
    }

    public void setShort_title(String short_title) {
        this.short_title = short_title;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }

    public String getUnread_comments_sum() {
        return unread_comments_sum;
    }

    public void setUnread_comments_sum(String unread_comments_sum) {
        this.unread_comments_sum = unread_comments_sum;
    }

    public NotifieClient getClient() {
        return client;
    }

    public void setClient(NotifieClient client) {
        this.client = client;
    }

    public RealmList<NotifieComment> getComments() {
        return comments;
    }

    public void setComments(RealmList<NotifieComment> comments) {
        this.comments = comments;
    }
}
