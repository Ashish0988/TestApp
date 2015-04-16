package co.notifie.app;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.okhttp.OkHttpClient;

import retrofit.RestAdapter;
import retrofit.client.OkClient;
import retrofit.converter.GsonConverter;

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

        Gson gson = new GsonBuilder()
                .excludeFieldsWithoutExposeAnnotation()
                .create();

        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(ROOT)
                .setConverter(new GsonConverter(gson))
                .setClient(new OkClient(new OkHttpClient()))
                .build();

        restAdapter.setLogLevel(RestAdapter.LogLevel.FULL);

        REST_CLIENT = restAdapter.create(Api.class);
    }
}
