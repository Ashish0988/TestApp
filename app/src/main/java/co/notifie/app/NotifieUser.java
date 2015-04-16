package co.notifie.app;

import com.google.gson.annotations.Expose;

/**
 * Created by thunder on 12.04.15.
 */
public class NotifieUser {
    @Expose
    private String email;
    @Expose
    private String phone;
    @Expose
    private String password;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
