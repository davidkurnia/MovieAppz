package me.davidkurniawan.MovieAppz.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import butterknife.ButterKnife;
import me.davidkurniawan.MovieAppz.R;

/**
 * Created by Admin on 1/7/2019.
 */
public class Alarm extends AppCompatActivity {

    TextView tvOneTimeDate, tvOneTimeTime ;
    TextView tvRepeatingTime;
    EditText edtOneTimeMessage , edtRepeatingMessage;
    Button btnOneTimeDate, btnOneTimeTime, btnOneTime ,btnRepeatingTime ,btnRepeating,  btnCancelRepeatingAlarm;

    //private Calendar calOneTimeDate, calOneTimeTime,calRepeatTimeTime;

    //private AlarmReceiver alarmReceiver;
    //private AlarmPreference alarmPreference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("MyAlarmManager");
        ButterKnife.bind(this);
    }
}
