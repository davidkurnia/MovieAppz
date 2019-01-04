package me.davidkurniawan.MovieAppz.helper;

import android.util.Log;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static me.davidkurniawan.MovieAppz.utils.AppConstants.BASE_URL;

public class ApiClient {

    private final static String TAG = ApiClient.class.getSimpleName();
    private static Retrofit mRetrofit;

    /**
     * This method connects to the API and
     * retrieves the JSON response
     *
     * @return a {@link #mRetrofit} instance
     */
    public static Retrofit getRetrofit() {
        if (mRetrofit == null) {
            mRetrofit = new Retrofit.Builder()
                    .addConverterFactory(GsonConverterFactory.create())
                    .baseUrl(BASE_URL)
                    .build();
        }
        Log.d(TAG, "Retrofit instance created");
        return mRetrofit;
    }
}
