package co.notifie.app;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.Header;
import retrofit.http.Multipart;
import retrofit.http.POST;
import retrofit.http.PUT;
import retrofit.http.Part;
import retrofit.http.Path;
import retrofit.http.Query;
import retrofit.mime.TypedFile;

/**
 * Created by thunder on 01.04.15.
 */
public interface Api {

    @POST("/users/sign_in.json")
    void singIn(@Query("email") String userEmail, @Query("password") String userPassword,
                    Callback<AuthResponce> callback);

    @POST("/api/v1/confirm")
    void postCode(@Query("code") String userPassword,
                Callback<AuthResponce> callback);

    @GET("/api/v1/search_for_clients")
    void searchForClient(@Header("Authorization") String authorization,
               Callback<SearchClientsResponce> callback);

    @GET("/api/v1/clients")
    void getClients(@Header("Authorization") String authorization,
                         Callback<ClientsResponce> callback);

    @POST("/users.json")
    void singUp(@Body UserRequestWrapper user,
                Callback<AuthResponce> callback);

    @Multipart
    @POST("/api/v1/me/avatar")
    void postAvatar(@Header("Authorization") String authorization, @Part("image") TypedFile file,
                    Callback<User> callback);

    @GET("/api/v1/me")
    void getMe(@Header("Authorization") String authorization,
               Callback<User> callback);

    @PUT("/api/v1/me")
    void putMe(@Header("Authorization") String authorization, @Query("new_user_name") String new_user_name, @Query("device_token") String device_token,
               Callback<User> callback);

    @POST("/api/v1/change_settings")
    void postSettings(@Header("Authorization") String authorization, @Query("client_id") String clientId, @Query("allow_for_notifie") String allow,
                  Callback<SettingsResponce> callback);

    @GET("/api/v1/messages.json")
    void getMessages(@Header("Authorization") String authorization, @Query("page") int page, @Query("per_page") int per_page,
               Callback<MessagesResponce> callback);

    @GET("/api/v1/messages/{id}")
    void getMessage(@Header("Authorization") String authorization, @Path("id") int id,
                     Callback<MessagesResponce> callback);

    @POST("/api/v1/messages")
    void postMessage(@Header("Authorization") String authorization, @Body MessagePostWrapper message,
                    Callback<MessagesResponce> callback);

    @GET("/api/v1/messages/{id}/comments")
    void getMessageComments(@Header("Authorization") String authorization, @Path("id") String id,
                    Callback<CommentsResponce> callback);

    @POST("/api/v1/messages/{id}/comments")
    void postComment(@Header("Authorization") String authorization, @Path("id") String id, @Body CommentPostWrapper comment,
                     Callback<CommentsResponce> callback);

}
