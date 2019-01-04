package me.davidkurniawan.MovieAppz.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

final public class NetworkUtil {
    public static boolean isNetworkAvailable(Context mContext) {
        ConnectivityManager connectivityManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null;
    }
}
