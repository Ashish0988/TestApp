package co.notifie.testapp;

import com.google.gson.annotations.Expose;

/**
 * Created by thunder on 13.04.15.
 */
public class CommentPostWrapper {

    @Expose
    private NotifieComment comment;

    public NotifieComment getComment() {
        return comment;
    }

    public void setComment(NotifieComment comment) {
        this.comment = comment;
    }
}
