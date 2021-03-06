package ru.gdgkazan.simpleweather.network;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import java.io.IOException;
import java.util.List;

import ru.arturvasilov.sqlite.core.SQLite;
import ru.arturvasilov.sqlite.core.Where;
import ru.gdgkazan.simpleweather.data.GsonHolder;
import ru.gdgkazan.simpleweather.data.model.City;
import ru.gdgkazan.simpleweather.data.tables.CityTable;
import ru.gdgkazan.simpleweather.data.tables.RequestTable;
import ru.gdgkazan.simpleweather.network.model.NetworkRequest;
import ru.gdgkazan.simpleweather.network.model.Request;
import ru.gdgkazan.simpleweather.network.model.RequestStatus;
import ru.gdgkazan.simpleweather.utils.WeatherListGenerator;

/**
 * @author Artur Vasilov
 */
public class NetworkService extends IntentService {

    private static final String REQUEST_KEY = "request";
    private static final String CITY_NAME_KEY = "city_name";
    private static final String DOWNLOAD_CITY_WEATHER = "download_city_weathe";
    private static final String WEATHERS_CITY_KEY = "weathers_city_key";


    public static void startDownloadCityWeather(@NonNull Context context, @NonNull Request request, @NonNull String cityName) {
        Intent intent = new Intent(context, NetworkService.class);
        intent.putExtra(REQUEST_KEY, GsonHolder.getGson().toJson(request));
        intent.putExtra(CITY_NAME_KEY, cityName);
        context.startService(intent);
    }

    public static void startDownloadCityesIDs(@NonNull Context context, @NonNull Request request) {
        Intent intent = new Intent(context, NetworkService.class);
        intent.putExtra(REQUEST_KEY, GsonHolder.getGson().toJson(request));
        context.startService(intent);
    }

    public static void startDownloadCityesWeather(@NonNull Context context, @NonNull Request request) {
        Intent intent = new Intent(context, NetworkService.class);
        intent.putExtra(REQUEST_KEY, GsonHolder.getGson().toJson(request));
        context.startService(intent);
    }


    @SuppressWarnings("unused")
    public NetworkService() {
        super(NetworkService.class.getName());
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        Request request = GsonHolder.getGson().fromJson(intent.getStringExtra(REQUEST_KEY), Request.class);
        Request savedRequest = SQLite.get().querySingle(RequestTable.TABLE,
                Where.create().equalTo(RequestTable.REQUEST, request.getRequest()));

        if (savedRequest != null && request.getStatus() == RequestStatus.IN_PROGRESS) {
            return;
        }
        request.setStatus(RequestStatus.IN_PROGRESS);
        // SQLite.get().delete(RequestTable.TABLE);
        SQLite.get().insert(RequestTable.TABLE, request);
        SQLite.get().notifyTableChanged(RequestTable.TABLE);

        if (TextUtils.equals(NetworkRequest.CITY_WEATHER, request.getRequest())) {
            String cityName = intent.getStringExtra(CITY_NAME_KEY);
            executeCityRequest(request, cityName);
        }

        if (TextUtils.equals(NetworkRequest.CITYES_WEATHERS, request.getRequest())) {
            executeCityesWeathersRequest(request);
        }
    }

    private void executeCityRequest(@NonNull Request request, @NonNull String cityName) {
        try {
            City city = ApiFactory.getWeatherService()
                    .getWeather(cityName)
                    .execute()
                    .body();

            SQLite.get().delete(CityTable.TABLE);
            SQLite.get().insert(CityTable.TABLE, city);
            request.setStatus(RequestStatus.SUCCESS);
        } catch (IOException e) {
            request.setStatus(RequestStatus.ERROR);
            request.setError(e.getMessage());
        } finally {
            SQLite.get().insert(RequestTable.TABLE, request);
            SQLite.get().notifyTableChanged(RequestTable.TABLE);
        }
    }

    //for download all cityes - set in while "!WLG.isEmpty()"
    private void executeCityesWeathersRequest(@NonNull Request request) {
        try {
            //загружаем ID городов
            String text = ApiFactory.getWeatherService()
                    .getTextWithIDsAndNames().execute().body().string();
            //Объект WLG хранит список WeatherCity, а также класс содержит метод извлечения объектов WeatherCity в количестве 20 шт.
            //20-максимальное количество City который может передать сервер за один запрос. WeatherListGenerator позволяет задать
            //число генераций порционнальных списков (20 городов в списке) WeatherList
            WeatherListGenerator WLG = new WeatherListGenerator(text);
            WLG.setTimesToExtract(500);

            SQLite.get().delete(CityTable.TABLE);
            while (WLG.getTimesToExtract() != 0) {

                String weatherCitiesURL = WLG.Next20WeatherCities();
                List<City> c = ApiFactory.getWeatherService()
                        .getWC(weatherCitiesURL)
                        .execute()
                        .body()
                        .getCities();

                SQLite.get().delete(CityTable.TABLE);
                for (City city : c) {
                    SQLite.get().insert(CityTable.TABLE, city);
                }

                request.setStatus(RequestStatus.SUCCESS);
                SQLite.get().insert(RequestTable.TABLE, request);
                SQLite.get().notifyTableChanged(RequestTable.TABLE);
            }
        } catch (Exception e) {
            request.setStatus(RequestStatus.ERROR);
            request.setError(e.getMessage());
            SQLite.get().delete(RequestTable.TABLE);
            SQLite.get().insert(RequestTable.TABLE, request);
            SQLite.get().notifyTableChanged(RequestTable.TABLE);
        }
    }



    }



