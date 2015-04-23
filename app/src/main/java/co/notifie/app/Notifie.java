package co.notifie.app;

import android.app.Application;
import net.danlew.android.joda.JodaTimeAndroid;

/**
 * Created by thunder on 02.04.15.
 */
public class Notifie extends Application {

    private String auth_token;
    private User currentUser;

    public String getAuth_token() {
        return auth_token;
    }

    public void setAuth_token(String auth_token) {
        this.auth_token = auth_token;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        JodaTimeAndroid.init(this);
    }

}
