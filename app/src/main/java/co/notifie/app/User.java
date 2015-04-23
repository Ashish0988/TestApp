package co.notifie.app;

import com.google.gson.annotations.Expose;

/**
 * Created by thunder on 01.04.15.
 */
public class User {

    @Expose
    private String id;
    private String avatar_file_name;
    private String avatar_content_type;
    private String avatar_file_size;
    private String avatar_updated_at;
    private String created_at;
    private String device_token;
    @Expose
    private String email;
    @Expose
    private String full_name;
    @Expose
    private String global_phone;
    @Expose
    private String phone;
    private String phone_confirm_code;
    @Expose
    private String role_id;
    private String updated_at;
    @Expose
    private String avatar_url;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAvatar_file_name() {
        return avatar_file_name;
    }

    public void setAvatar_file_name(String avatar_file_name) {
        this.avatar_file_name = avatar_file_name;
    }

    public String getAvatar_content_type() {
        return avatar_content_type;
    }

    public void setAvatar_content_type(String avatar_content_type) {
        this.avatar_content_type = avatar_content_type;
    }

    public String getAvatar_file_size() {
        return avatar_file_size;
    }

    public void setAvatar_file_size(String avatar_file_size) {
        this.avatar_file_size = avatar_file_size;
    }

    public String getAvatar_updated_at() {
        return avatar_updated_at;
    }

    public void setAvatar_updated_at(String avatar_updated_at) {
        this.avatar_updated_at = avatar_updated_at;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getDevice_token() {
        return device_token;
    }

    public void setDevice_token(String device_token) {
        this.device_token = device_token;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFull_name() {
        return full_name;
    }

    public void setFull_name(String full_name) {
        this.full_name = full_name;
    }

    public String getGlobal_phone() {
        return global_phone;
    }

    public void setGlobal_phone(String global_phone) {
        this.global_phone = global_phone;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPhone_confirm_code() {
        return phone_confirm_code;
    }

    public void setPhone_confirm_code(String phone_confirm_code) {
        this.phone_confirm_code = phone_confirm_code;
    }

    public String getRole_id() {
        return role_id;
    }

    public void setRole_id(String role_id) {
        this.role_id = role_id;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }

    public String getAvatar_url() {
        return avatar_url;
    }

    public void setAvatar_url(String avatar_url) {
        this.avatar_url = avatar_url;
    }
}
