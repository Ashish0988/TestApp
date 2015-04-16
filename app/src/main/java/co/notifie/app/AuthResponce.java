package co.notifie.app;

import com.google.gson.annotations.Expose;

/**
 * Created by thunder on 01.04.15.
 */
public class AuthResponce {

    @Expose
    private String authentication_token;

    @Expose
    private String user_id;
    @Expose
    private User user;

    @Expose
    private String phone_confirm_code;

    public String getAuthentication_token() {
        return authentication_token;
    }

    public void setAuthentication_token(String authentication_token) {
        this.authentication_token = authentication_token;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getPhone_confirm_code() {
        return phone_confirm_code;
    }

    public void setPhone_confirm_code(String phone_confirm_code) {
        this.phone_confirm_code = phone_confirm_code;
    }
}