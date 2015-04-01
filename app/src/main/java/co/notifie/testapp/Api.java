package co.notifie.testapp;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Header;
import retrofit.http.POST;
import retrofit.http.Query;

/**
 * Created by thunder on 01.04.15.
 */
public interface Api {

    @POST("/users/sign_in.json")
    void singIn(@Query("email") String userEmail, @Query("password") String userPassword,
                    Callback<AuthResponce> callback);

    @GET("/api/v1/me")
    void getMe(@Header("Authorization") String authorization,
                Callback<User> callback);

}
