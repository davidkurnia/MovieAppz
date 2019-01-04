package me.davidkurniawan.MovieAppz.utils;

import android.content.Context;
import android.view.Gravity;

import com.fxn.cue.Cue;
import com.fxn.cue.enums.Duration;
import com.fxn.cue.enums.Type;

public class Toaster {
    public static void makeToast(String text, Type type, Context context) {
        Cue.init()
                .with(context)
                .setMessage(text)
                .setType(type)
                .setDuration(Duration.LONG)
                .setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL)
                .show();

    }
}
