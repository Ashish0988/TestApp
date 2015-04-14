package co.notifie.testapp;

import com.google.gson.annotations.Expose;

import java.util.List;

/**
 * Created by thunder on 15.04.15.
 */
public class SearchClientsResponce {

    @Expose
    private List<NotifieCustomer> found;

    private List<NotifieClient> clients;

    public List<NotifieClient> getClients() {
        return clients;
    }

    public void setClients(List<NotifieClient> clients) {
        this.clients = clients;
    }

    public List<NotifieCustomer> getFound() {
        return found;
    }

    public void setFound(List<NotifieCustomer> found) {
        this.found = found;
    }
}
