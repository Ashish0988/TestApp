package co.notifie.testapp;

import com.google.gson.annotations.Expose;

/**
 * Created by thunder on 13.04.15.
 */
public class MessagePostWrapper {
    @Expose
    private NotifeMessage message;

    public NotifeMessage getMessage() {
        return message;
    }

    public void setMessage(NotifeMessage message) {
        this.message = message;
    }
}
