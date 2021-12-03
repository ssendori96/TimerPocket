package com.project.timerpocket;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

public class TimerActivity extends AppCompatActivity {

    private TextView textTitle = null;
    private TextView textHour = null;
    private TextView textMinute = null;
    private TextView textSecond = null;

    private int hour = 0;
    private int minute = 0;
    private int second = 0;

    private int hour_default = 0;
    private int minute_default = 0;
    private int second_default = 0;

    private Timer timer = null;
    private TimerTask timerTask = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);

        Intent get_intent = getIntent();
        hour_default = get_intent.getIntExtra("hour", 0);
        minute_default = get_intent.getIntExtra("minute", 0);
        second_default = get_intent.getIntExtra("second", 0);

        hour = hour_default;
        minute = minute_default;
        second = second_default;

        textTitle = findViewById(R.id.textTitle);
        textHour = findViewById(R.id.textHour);
        textMinute = findViewById(R.id.textMinute);
        textSecond = findViewById(R.id.textSecond);
        Button btnBack = findViewById(R.id.btnBack);
        Button btnStart = findViewById(R.id.btnStart);
        Button btnPause = findViewById(R.id.btnPause);
        Button btnReset = findViewById(R.id.btnReset);

        textTitle.setText(get_intent.getStringExtra("title"));
        defaultData();

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timer = new Timer();
                timerTask = new TimerTask() {
                    @Override
                    public void run() {
                        if(second != 0) {
                            second--;
                        } else if(minute != 0) {
                            second = 60;
                            second--;
                            minute--;
                        } else if(hour != 0) {
                            second = 60;
                            minute = 60;
                            second--;
                            minute--;
                            hour--;
                        }

                        if(second <= 9){
                            textSecond.setText("0" + second);
                        } else {
                            textSecond.setText(String.valueOf(second));
                        }

                        if(minute <= 9){
                            textMinute.setText("0" + minute);
                        } else {
                            textMinute.setText(String.valueOf(minute));
                        }

                        if(hour <= 9){
                            textHour.setText("0" + hour);
                        } else {
                            textHour.setText(String.valueOf(hour));
                        }

                        if(hour == 0 && minute == 0 && second == 0) {
                            timer.cancel();
                            Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                            vibrator.vibrate(3000);
                        }

                    }
                };
                timer.schedule(timerTask, 1000, 1000);
            }
        });

        btnPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (timer != null) timer.cancel();
            }
        });

        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (timer != null) timer.cancel();
                defaultData();
            }
        });
    }

    private void defaultData() {
        if(hour_default <= 9){
            textHour.setText("0" + hour_default);
        } else {
            textHour.setText(String.valueOf(hour_default));
        }
        if(minute_default <= 9){
            textMinute.setText("0" + minute_default);
        } else {
            textMinute.setText(String.valueOf(minute_default));
        }
        if(second_default <= 9){
            textSecond.setText("0" + second_default);
        } else {
            textSecond.setText(String.valueOf(second_default));
        }
    }
}