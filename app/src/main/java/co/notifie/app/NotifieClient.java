package co.notifie.app;

import com.google.gson.annotations.Expose;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.RealmClass;

/**
 * Created by thunder on 02.04.15.
 */

@RealmClass
public class NotifieClient extends RealmObject {

    @Expose
    @PrimaryKey
    private String id;

    @Expose
    private String access_token;
    private String category_id;
    @Expose
    private String created_at;
    private String greetings_message;
    @Expose
    private String image;
    @Expose
    private String legal;
    @Expose
    private String name;
    private String password;
    @Expose
    private String unread_message_count;
    private String updated_at;
    @Expose
    private String url;
    @Expose
    private String username;

    private String check_for_notifie;

    private RealmList<NotifeMessage> messages;

    public String getAccess_token() {
        return access_token;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }

    public String getCategory_id() {
        return category_id;
    }

    public void setCategory_id(String category_id) {
        this.category_id = category_id;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getGreetings_message() {
        return greetings_message;
    }

    public void setGreetings_message(String greetings_message) {
        this.greetings_message = greetings_message;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getLegal() {
        return legal;
    }

    public void setLegal(String legal) {
        this.legal = legal;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUnread_message_count() {
        return unread_message_count;
    }

    public void setUnread_message_count(String unread_message_count) {
        this.unread_message_count = unread_message_count;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getCheck_for_notifie() {
        return check_for_notifie;
    }

    public void setCheck_for_notifie(String check_for_notifie) {
        this.check_for_notifie = check_for_notifie;
    }


    public RealmList<NotifeMessage> getMessages() {
        return messages;
    }

    public void setMessages(RealmList<NotifeMessage> messages) {
        this.messages = messages;
    }

}
