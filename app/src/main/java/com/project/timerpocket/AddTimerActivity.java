package com.project.timerpocket;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;

public class AddTimerActivity extends AppCompatActivity {

    private EditText editTitle = null;
    private NumberPicker pickerHour = null;
    private NumberPicker pickerMinute = null;
    private NumberPicker pickerSecond = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_timer);

        editTitle = findViewById(R.id.editTitle);
        pickerHour = findViewById(R.id.pickerHour);
        pickerMinute = findViewById(R.id.pickerMinute);
        pickerSecond = findViewById(R.id.pickerSecond);
        Button btnCancel = findViewById(R.id.btnCancel);
        Button btnSave = findViewById(R.id.btnSave);

        pickerHour.setMinValue(0);
        pickerHour.setMaxValue(99);
        pickerMinute.setMinValue(0);
        pickerMinute.setMaxValue(59);
        pickerSecond.setMinValue(0);
        pickerSecond.setMaxValue(59);

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra("title", editTitle.getText().toString());
                intent.putExtra("hour", pickerHour.getValue());
                intent.putExtra("minute", pickerMinute.getValue());
                intent.putExtra("second", pickerSecond.getValue());
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }
}