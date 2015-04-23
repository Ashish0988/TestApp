package co.notifie.app;

import com.google.gson.annotations.Expose;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.RealmClass;

/**
 * Created by thunder on 03.04.15.
 */
@RealmClass
public class NotifieComment extends RealmObject {

    @Expose
    @PrimaryKey
    private String id;

    @Expose
    private Date created_at;
    @Expose
    private String from_user_id;
    @Expose
    private String message_id;
    @Expose
    private String readed;
    @Expose
    private Date readed_at;
    @Expose
    private String reply;
    @Expose
    private String text;
    @Expose
    private Date updated_at;
    @Expose
    private String user_id;
    @Expose
    private String uuid;
    @Expose
    private String has_date_header;
    @Expose
    private String from_user_name;
    @Expose
    private String from_user_avatar;

    @Expose
    private NotifeMessage message;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date getCreated_at() {
        return created_at;
    }

    public void setCreated_at(Date created_at) {
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

    public Date getReaded_at() {
        return readed_at;
    }

    public void setReaded_at(Date readed_at) {
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

    public Date getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(Date updated_at) {
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
