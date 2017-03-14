package com.example.seemore;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;

//Activity behind first page of application
//choosing starting and ending stations
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
		
		//Image button leading to main activity
        ImageButton tut1=(ImageButton) findViewById(R.id.go);
        tut1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
				//Taking information from Spinners to next activity
                Global mApp=Global.getInstance();
                String string;
                Spinner spinner1 = (Spinner) findViewById(R.id.begintrip);
                string=spinner1.getSelectedItem().toString();
                mApp.setValue(string,0);
                Spinner spinner2 = (Spinner) findViewById(R.id.endtrip);
                string=spinner2.getSelectedItem().toString();
                mApp.setValue(string,1);
                startActivity(new Intent("com.example.seemore.travel"));
            }
        });

    }
}
