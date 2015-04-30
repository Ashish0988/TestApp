package co.notifie.app;

import com.google.gson.annotations.Expose;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.RealmClass;

/**
 * Created by thunder on 30.04.15.
 */
@RealmClass
public class NotifieTemplate extends RealmObject {

    @Expose
    @PrimaryKey
    private String id;

    @Expose
    private String ru_name;

    @Expose
    private String en_name;

    @Expose
    private String url;

    @Expose
    private String params;

    @Expose
    private String client_id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRu_name() {
        return ru_name;
    }

    public void setRu_name(String ru_name) {
        this.ru_name = ru_name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getParams() {
        return params;
    }

    public void setParams(String params) {
        this.params = params;
    }

    public String getEn_name() {
        return en_name;
    }

    public void setEn_name(String en_name) {
        this.en_name = en_name;
    }

    public String getClient_id() {
        return client_id;
    }

    public void setClient_id(String client_id) {
        this.client_id = client_id;
    }
}
