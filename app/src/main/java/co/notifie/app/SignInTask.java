package co.notifie.app;

import android.os.AsyncTask;
import android.util.Log;

/*
import org.springframework.http.HttpAuthentication;
import org.springframework.http.HttpBasicAuthentication;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;
*/

/**
 * Created by thunder on 01.04.15.
 */

public class SignInTask extends AsyncTask<Void, Void, AuthResponce> {
    @Override
    protected AuthResponce doInBackground(Void... params) {
        try {
            final String url = "http://192.168.1.39:3000/users/sign_in?email=s.iv@notifie.ru&password=123456";

            // Set the Accept header

            /*
            HttpHeaders requestHeaders = new HttpHeaders();
            requestHeaders.setAccept(Collections.singletonList(new MediaType("application", "json")));
            HttpAuthentication authHeader = new HttpBasicAuthentication("s.iv@notifie.ru", "123456");
            requestHeaders.setAuthorization(authHeader);
            HttpEntity<?> requestEntity = new HttpEntity<Object>(requestHeaders);

            RestTemplate restTemplate = new RestTemplate();

            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

            ResponseEntity<AuthResponce> responseEntity = restTemplate.exchange(url, POST, requestEntity, AuthResponce.class);

            AuthResponce responce = responseEntity.getBody();
            Log.w("SignInTask", "body:"  + responce);

            return responce; */

            return null;

        } catch (Exception e) {
            Log.e("MainActivity", e.getMessage(), e);
        }

        return null;
    }

    @Override
    protected void onPostExecute(AuthResponce responce) {
        //TextView greetingIdText = (TextView) findViewById(R.id.id_value);
        //TextView greetingContentText = (TextView) findViewById(R.id.content_value);
        //greetingIdText.setText(greeting.getId());
        //greetingContentText.setText(greeting.getContent());
        if (responce != null) {
            Log.w("onPostExecute", "id:" + responce.getUser_id());
            Log.w("onPostExecute", "content:" + responce.getAuthentication_token());
        }
    }

}
