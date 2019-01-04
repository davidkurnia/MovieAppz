package me.davidkurniawan.MovieAppz;

import android.app.Application;

import me.davidkurniawan.MovieAppz.utils.FontOverride;

public class MovieAppz extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        new Thread(new Runnable() {
            @Override
            public void run() {
                FontOverride.setDefaultFont(MovieAppz.this, "DEFAULT", "Comfortaa-Light.ttf");
                FontOverride.setDefaultFont(MovieAppz.this, "MONOSPACE", "Comfortaa-Regular.ttf");
                FontOverride.setDefaultFont(MovieAppz.this, "SERIF", "Comfortaa-Bold.ttf");
                FontOverride.setDefaultFont(MovieAppz.this, "SANS_SERIF", "Comfortaa-Bold.ttf");
            }
        }).start();
    }
}
