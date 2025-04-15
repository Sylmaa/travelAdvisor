package api;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class APIFetcher {
    private final String weatherApiKey;
    private final String mapsApiKey;
    private final String defaultCity;
    private final String defaultOrigin;
    private final String defaultDestination;

    public APIFetcher() {
        Properties prop = new Properties();
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("config.properties")) {
            prop.load(input);
            weatherApiKey = prop.getProperty("weather.api.key");
            mapsApiKey = prop.getProperty("maps.api.key");
            defaultCity = prop.getProperty("default.city");
            defaultOrigin = prop.getProperty("default.origin");
            defaultDestination = prop.getProperty("default.destination");
        } catch (IOException ex) {
            throw new RuntimeException("Config файл унших үед алдаа гарлаа", ex);
        }
    }

    public JSONObject getWeatherData() throws Exception {
        return getWeatherData(defaultCity);
    }

    public JSONObject getWeatherData(String city) throws Exception {
        String url = String.format("http://api.openweathermap.org/data/2.5/weather?q=%s&appid=%s&units=metric",
                city, weatherApiKey);
        return fetchDataFromAPI(url);
    }

    public JSONObject getTrafficData() throws Exception {
        return getTrafficData(defaultOrigin, defaultDestination);
    }

    public JSONObject getTrafficData(String origin, String destination) throws Exception {
        String url = String.format("https://maps.googleapis.com/maps/api/directions/json?origin=%s&destination=%s" +
                        "&departure_time=now&traffic_model=best_guess&key=%s",
                origin, destination, mapsApiKey);
        return fetchDataFromAPI(url);
    }

    private JSONObject fetchDataFromAPI(String url) throws Exception {
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpGet request = new HttpGet(url);
            return client.execute(request, httpResponse -> {
                String response = EntityUtils.toString(httpResponse.getEntity());
                return new JSONObject(response);
            });
        }
    }
}