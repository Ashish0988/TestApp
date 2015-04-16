package co.notifie.app;

import com.google.gson.annotations.Expose;

/**
 * Created by thunder on 12.04.15.
 */
public class UserRequestWrapper {
    @Expose
    private NotifieUser user;

    public NotifieUser getUser() {
        return user;
    }

    public void setUser(NotifieUser user) {
        this.user = user;
    }
}
