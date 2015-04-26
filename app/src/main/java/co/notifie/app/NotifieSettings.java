package co.notifie.app;

import com.google.gson.annotations.Expose;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.RealmClass;

/**
 * Created by thunder on 26.04.15.
 */

@RealmClass
public class NotifieSettings extends RealmObject {

    @Expose
    @PrimaryKey
    private String id;
    @Expose
    private String client_id;
    @Expose
    private String user_id;
    @Expose
    private String allow_notification;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getClient_id() {
        return client_id;
    }

    public void setClient_id(String client_id) {
        this.client_id = client_id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getAllow_notification() {
        return allow_notification;
    }

    public void setAllow_notification(String allow_notification) {
        this.allow_notification = allow_notification;
    }
}
