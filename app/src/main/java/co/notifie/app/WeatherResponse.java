package co.notifie.app;

/**
 * Created by thunder on 01.04.15.
 */
public class WeatherResponse {
    private int cod;
    private String base;
    private Weather weather;

    public int getCod() {
        return cod;
    }

    public void setCod(int cod) {
        this.cod = cod;
    }

    public String getBase() {
        return base;
    }

    public void setBase(String base) {
        this.base = base;
    }

    public Weather getWeather() {
        return weather;
    }

    public void setWeather(Weather weather) {
        this.weather = weather;
    }
}
