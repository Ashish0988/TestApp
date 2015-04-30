package co.notifie.app;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import java.util.List;

import fr.ganfra.materialspinner.MaterialSpinner;
import io.realm.Realm;
import io.realm.RealmResults;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class AskActivity extends ActionBarActivity {

    MaterialSpinner spinner;
    MaterialSpinner template_spinner;
    RealmResults <NotifieClient> clients;
    String[] subjects;
    ArrayAdapter<String> adapter2;
    public static Realm realm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ask);

        if (MainActivity.realm == null) {
            realm = Realm.getInstance(this, MainActivity.REALM_DATABASE);
        } else {
            realm = MainActivity.realm;
        }

        loadClients();

        clients = realm.where(NotifieClient.class)
                .greaterThan("templates_count", 0)
                .equalTo("check_for_notifie", "1")
                .findAll();

        String[] client_names = new String[clients.size()];

        for(int i = 0; i < clients.size(); i++) {
            client_names[i] = clients.get(i).getName();
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, client_names);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner = (MaterialSpinner) findViewById(R.id.spinner);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // your code here
                NotifieClient client = null;

                if (position >= 0) {
                    client = clients.get(position);
                }

                if (client != null) {

                    /*
                    RealmResults <NotifeMessage> messages = realm.where(NotifeMessage.class)
                            .equalTo("client_id", client.getId())
                            .findAll();

                    String[] client_subject = new String[messages.size()];

                    for (int i = 0; i < messages.size(); i++) {
                        client_subject[i] = messages.get(i).getShort_title();
                    }
                    */

                    RealmResults <NotifieTemplate> messages = realm.where(NotifieTemplate.class)
                            .equalTo("client_id", client.getId())
                            .findAll();

                    String[] client_subject = new String[messages.size()];

                    for (int i = 0; i < messages.size(); i++) {
                        client_subject[i] = messages.get(i).getRu_name();
                    }


                    adapter2 = new ArrayAdapter<String>(getBaseContext(), android.R.layout.simple_spinner_item, client_subject);
                    adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    //template_spinner = (MaterialSpinner) findViewById(R.id.template_spinner);
                    template_spinner.setAdapter(adapter2);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });

        subjects = new String[]{};

        adapter2 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, subjects);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        template_spinner = (MaterialSpinner) findViewById(R.id.template_spinner);
        template_spinner.setAdapter(adapter2);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_ask, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //
    // Load Messages From Server
    //
    public void loadClients() {

        RestClient.get().getClients(MainActivity.AUTH_TOKEN, new Callback<ClientsResponce>() {
            @Override
            public void success(ClientsResponce clientsResponce, Response response) {
                // success!
                List<NotifieClient> clients = clientsResponce.getClients();

                realm.beginTransaction();
                realm.copyToRealmOrUpdate(clients);
                realm.commitTransaction();

            }

            @Override
            public void failure(RetrofitError error) {
                // something went wrong
                Log.e("App", "Error body:" + error.getBody());
            }
        });

    }
}
