package co.notifie.testapp;

import java.util.List;

/**
 * Created by thunder on 02.04.15.
 */
public class MessagesResponce {

    //private Pagitation pagitation;
    private List<NotifeMessage> messages;

    /*
    public Pagitation getPagitation() {
        return pagitation;
    }

    public void setPagitation(Pagitation pagitation) {
        this.pagitation = pagitation;
    }
    */

    public List<NotifeMessage> getMessages() {
        return messages;
    }

    public void setMessages(List<NotifeMessage> messages) {
        this.messages = messages;
    }
}
