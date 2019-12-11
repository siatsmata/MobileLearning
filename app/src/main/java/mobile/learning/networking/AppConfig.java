package mobile.learning.networking;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class AppConfig {

    //public static String BASE_URL = "https://classroom-trilogi.000webhostapp.com/Android/";
    public static String BASE_URL = "https://hyperintegrated.co.id/Android/";

    public static Retrofit getRetrofit() {

        return new Retrofit.Builder()
                .baseUrl(AppConfig.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }
}
