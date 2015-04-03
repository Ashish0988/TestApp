package co.notifie.testapp;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.RealmClass;

/**
 * Created by thunder on 03.04.15.
 */
@RealmClass
public class NotifieComment extends RealmObject {

    @PrimaryKey
    private String id;

    private String created_at;
    private String from_user_id;
    private String message_id;
    private String readed;
    private String readed_at;
    private String reply;
    private String text;
    private String updated_at;
    private String user_id;
    private String uuid;
    private String has_date_header;
    private String from_user_name;
    private String from_user_avatar;

    private NotifeMessage message;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getFrom_user_id() {
        return from_user_id;
    }

    public void setFrom_user_id(String from_user_id) {
        this.from_user_id = from_user_id;
    }

    public String getMessage_id() {
        return message_id;
    }

    public void setMessage_id(String message_id) {
        this.message_id = message_id;
    }

    public String getReaded() {
        return readed;
    }

    public void setReaded(String readed) {
        this.readed = readed;
    }

    public String getReaded_at() {
        return readed_at;
    }

    public void setReaded_at(String readed_at) {
        this.readed_at = readed_at;
    }

    public String getReply() {
        return reply;
    }

    public void setReply(String reply) {
        this.reply = reply;
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

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getHas_date_header() {
        return has_date_header;
    }

    public void setHas_date_header(String has_date_header) {
        this.has_date_header = has_date_header;
    }

    public String getFrom_user_name() {
        return from_user_name;
    }

    public void setFrom_user_name(String from_user_name) {
        this.from_user_name = from_user_name;
    }

    public String getFrom_user_avatar() {
        return from_user_avatar;
    }

    public void setFrom_user_avatar(String from_user_avatar) {
        this.from_user_avatar = from_user_avatar;
    }

    public NotifeMessage getMessage() {
        return message;
    }

    public void setMessage(NotifeMessage message) {
        this.message = message;
    }
}
