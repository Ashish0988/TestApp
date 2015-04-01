package co.notifie.testapp;

import com.squareup.okhttp.OkHttpClient;

import retrofit.RestAdapter;
import retrofit.client.OkClient;

/**
 * Created by thunder on 01.04.15.
 */
public class RestClient {
    private static Api REST_CLIENT;
    private static String ROOT = MainActivity.NOTIFIE_HOST;

    static {
        setupRestClient();
    }

    private RestClient() {}

    public static Api get() {
        return REST_CLIENT;
    }

    private static void setupRestClient() {
        /*RestAdapter.Builder builder = new RestAdapter.Builder()
                .setEndpoint(ROOT)
                .setClient(new OkClient(new OkHttpClient()))
                .builder.setLogLevel(RestAdapter.LogLevel.FULL);*/

        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(ROOT)
                .setClient(new OkClient(new OkHttpClient()))
                .build();

        REST_CLIENT = restAdapter.create(Api.class);
    }
}
