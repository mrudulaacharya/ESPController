package com.example.espcontroller;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;


public class StatsActivity extends AppCompatActivity {

    private String url;
    private TextView value_voltage,value_battery, value_rpm, value_rpm2, value_rpm3, value_rpm4;
    private DatabaseReference reference;

    private Handler mHandler = new Handler();

    //Helper class created for adding data to firebase
    Stats stats;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);

        //This will connect to your firebase instance
        reference = FirebaseDatabase.getInstance().getReference("stats_history");

        Button button_save, button_fetch;

        //Get IP address from previous screen
        Intent intent = getIntent();
        url = "http://"+intent.getStringExtra(MainActivity.EXTRA_URL)+"/";

        //Get elements from screen
        value_voltage = findViewById(R.id.value_voltage);
        value_battery = findViewById(R.id.value_battery);
        value_rpm = findViewById(R.id.value_rpm);
        value_rpm2 = findViewById(R.id.value_rpm2);
        value_rpm3 = findViewById(R.id.value_rpm3);
        value_rpm4 = findViewById(R.id.value_rpm4);


        button_save = findViewById(R.id.button_save);
        button_fetch = findViewById(R.id.button_fetch);

        //Fetch stats
        button_fetch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRunnable.run();
            }
        });

        //Save stats to firebase
        button_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveStats();
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        mRunnable.run();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mHandler.removeCallbacks(mRunnable);
    }

    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            DownloadWebPageTask task = new DownloadWebPageTask();
            task.execute(url);
            mHandler.postDelayed(mRunnable, 1000);
        }
    };

    private class DownloadWebPageTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            String response = "";
            for (String url : urls) {
                DefaultHttpClient client = new DefaultHttpClient();
                HttpGet httpGet = new HttpGet(url);
                try {
                    HttpResponse execute = client.execute(httpGet);
                    InputStream content = execute.getEntity().getContent();

                    BufferedReader buffer = new BufferedReader(
                            new InputStreamReader(content));
                    String s = "";
                    while ((s = buffer.readLine()) != null) {
                        response += s;
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return response;
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                String dataString = Html.fromHtml(result).toString();
                JSONObject data = new JSONObject(dataString);
                value_voltage.setText(data.getString("voltage"));
                value_battery.setText(data.getString("battery"));
                value_rpm.setText(data.getString("rpm"));
                value_rpm2.setText(data.getString("rpm2"));
                value_rpm3.setText(data.getString("rpm3"));
                value_rpm4.setText(data.getString("rpm4"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }


    //This method saves data to firebase
    public void saveStats() {
        String voltage = value_voltage.getText().toString();
        String battery = value_battery.getText().toString();
        String rpm = value_rpm.getText().toString();
        String rpm2 = value_rpm2.getText().toString();
        String rpm3 = value_rpm3.getText().toString();
        String rpm4 = value_rpm4.getText().toString();

        stats = new Stats();
        stats.setVoltage(voltage);
        stats.setBattery(battery);
        stats.setRpm(rpm);
        stats.setRpm(rpm2);
        stats.setRpm(rpm3);
        stats.setRpm(rpm4);

        reference.push().setValue(stats);
        Toast.makeText(this, "Saved successfully", Toast.LENGTH_SHORT).show();
    }

}
