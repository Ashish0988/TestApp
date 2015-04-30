package co.notifie.app;

import com.google.gson.annotations.Expose;

import java.util.List;

/**
 * Created by thunder on 30.04.15.
 */
public class ClientsResponce {

    @Expose
    private List<NotifieClient> clients;

    public List<NotifieClient> getClients() {
        return clients;
    }

    public void setClients(List<NotifieClient> clients) {
        this.clients = clients;
    }
}
