package ru.gdgkazan.simpleweather.network;

import android.support.annotation.NonNull;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.Url;
import ru.gdgkazan.simpleweather.data.model.CitiesWeathersResponse;
import ru.gdgkazan.simpleweather.data.model.City;

/**
 * @author Artur Vasilov
 */
public interface WeatherService {

    @GET("data/2.5/weather?units=metric")
    Call<City> getWeather(@NonNull @Query("q") String query);

    @GET
    Call<CitiesWeathersResponse> getWC(@Url String url);

    @GET("http://openweathermap.org/help/city_list.txt")
    Call<ResponseBody> getTextWithIDsAndNames();

}
