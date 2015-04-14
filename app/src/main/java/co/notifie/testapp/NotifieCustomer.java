package co.notifie.testapp;

import com.google.gson.annotations.Expose;

/**
 * Created by thunder on 15.04.15.
 */
public class NotifieCustomer {
    @Expose
    private String fullname;

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }
}
