package co.notifie.testapp;

/**
 * Created by thunder on 02.04.15.
 */
public class Pagitation {
    private String per_page;
    private String total_pages;
    private String total_objects;

    public String getPer_page() {
        return per_page;
    }

    public void setPer_page(String per_page) {
        this.per_page = per_page;
    }

    public String getTotal_pages() {
        return total_pages;
    }

    public void setTotal_pages(String total_pages) {
        this.total_pages = total_pages;
    }

    public String getTotal_objects() {
        return total_objects;
    }

    public void setTotal_objects(String total_objects) {
        this.total_objects = total_objects;
    }
}
