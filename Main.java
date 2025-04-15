import api.APIFetcher;
import model.TravelAdvisor;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        try {
            // 1. API Fetcher инициализаци
            APIFetcher apiFetcher = new APIFetcher();

            // 2. Travel Advisor инициализаци
            TravelAdvisor advisor = new TravelAdvisor();

            // 3. Жишээ өгөгдөл ашиглан загвар сургах (бодит төсөлд өөр өгөгдөл ашиглах)
            double[][] trainingFeatures = {
                    {20.0, 0.0, 50.0, 1.2, 30.0},   // Сайн нөхцөл
                    {15.0, 5.0, 70.0, 1.5, 45.0},    // Дунд нөхцөл
                    {-5.0, 10.0, 90.0, 2.0, 60.0}    // Муу нөхцөл
            };
            String[] trainingLabels = {"good", "average", "bad"};
            advisor.trainModel(trainingFeatures, trainingLabels);

            // 4. API-аас одоогийн мэдээлэл авах
            System.out.println("Одоогийн цаг агаар болон замын нөхцлийг шалгаж байна...");
            JSONObject weatherData = apiFetcher.getWeatherData();
            JSONObject trafficData = apiFetcher.getTrafficData();

            // 5. Өгөгдөл боловсруулах
            double temperature = weatherData.getJSONObject("main").getDouble("temp");
            double precipitation = weatherData.has("rain") ?
                    weatherData.getJSONObject("rain").getDouble("1h") : 0;
            double humidity = weatherData.getJSONObject("main").getDouble("humidity");

            JSONObject route = trafficData.getJSONArray("routes").getJSONObject(0);
            JSONObject leg = route.getJSONArray("legs").getJSONObject(0);
            double normalTime = leg.getJSONObject("duration").getDouble("value") / 60; // Минут
            double trafficTime = leg.getJSONObject("duration_in_traffic").getDouble("value") / 60;
            double trafficLevel = trafficTime / normalTime;

            // 6. Зөвлөгөө авах
            double[] currentConditions = {
                    temperature,
                    precipitation,
                    humidity,
                    trafficLevel,
                    trafficTime
            };
            String recommendation = advisor.predictBestTime(currentConditions);

            // 7. Үр дүнг харуулах
            System.out.println("\n===== ЯВАХ ЦАГИЙН ЗӨВЛӨМЖ =====");
            System.out.println("Одоогийн цаг: " + new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date()));
            System.out.printf("Температур: %.1f°C\n", temperature);
            System.out.printf("Хур тунадас: %.1f мм\n", precipitation);
            System.out.printf("Чийгшил: %.1f%%\n", humidity);
            System.out.printf("Замын хугацаа: %.1f минут (%.1f%% их)\n", trafficTime, (trafficLevel-1)*100);
            System.out.println("ЗӨВЛӨМЖ: " + getRecommendationMessage(recommendation));

        } catch (Exception e) {
            System.err.println("Алдаа гарлаа: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static String getRecommendationMessage(String recommendation) {
        switch (recommendation) {
            case "good":
                return "Одоо явах хамгийн тохиромжтой цаг!";
            case "average":
                return "Явах боломжтой, гэхдээ арай хүлээх нь дээр";
            case "bad":
                return "Маш муу нөхцөл, явахгүй байхыг зөвлөж байна";
            default:
                return "Тодорхойгүй";
        }
    }
}