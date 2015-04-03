package co.notifie.testapp;

import java.util.List;

/**
 * Created by thunder on 03.04.15.
 */
public class CommentsResponce {

    private List<NotifieComment> comments;

    public List<NotifieComment> getComments() {
        return comments;
    }

    public void setComments(List<NotifieComment> comments) {
        this.comments = comments;
    }
}
