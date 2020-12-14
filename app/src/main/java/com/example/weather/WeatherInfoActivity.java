package com.example.weather;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.Calendar;

public class WeatherInfoActivity extends AppCompatActivity {

    TextView tv_location, tv_temp, tv_feelsLike, tv_humidity, tv_windSpeed, currentTime;
    ImageView deleteBtn;
    ImageView weather_main;

    Intent intent;

    MyDBHelper dbHelper;
    SQLiteDatabase sqlDB;
    Cursor cur;

    double lat, lon;


    Weather weather;
    String forecastURL;
    String weatherURL;
    int id;

    Calendar c = Calendar.getInstance();

    RecyclerView forecastList;
    RecyclerForecastAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather_info);

        tv_location = findViewById(R.id.tv_location);
        weather_main = findViewById(R.id.weather_main);
        tv_temp = findViewById(R.id.tv_temp);
        tv_feelsLike = findViewById(R.id.tv_feelsLike);
        tv_humidity = findViewById(R.id.tv_humidity);
        tv_windSpeed = findViewById(R.id.tv_windSpeed);
        deleteBtn = findViewById(R.id.deleteBtn);
        forecastList = findViewById(R.id.forecast_bar);
        currentTime = findViewById(R.id.current_time);

        weather = new Weather(WeatherInfoActivity.this);

        intent = getIntent();
        String locationName = intent.getStringExtra("locationName");

        dbHelper = new MyDBHelper(WeatherInfoActivity.this);
        sqlDB = dbHelper.getReadableDatabase();

        cur = sqlDB.rawQuery("select * from tbl_weather where location_name = '"+ locationName +"';", null);

        if(cur.moveToNext()) {
            id = cur.getInt(cur.getColumnIndex("id"));
            lat = cur.getDouble(cur.getColumnIndex("lat"));
            Log.d("lat : ", lat+"");
            lon = cur.getDouble(cur.getColumnIndex("lon"));
            Log.d("lon : ", lon+"");
            tv_location.setText(cur.getString(cur.getColumnIndex("location_name")));
        }
        forecastURL = "http://api.openweathermap.org/data/2.5/forecast?lat="+ lat +"&lon="+ lon +"&units=metric&appid=" + SecretInfo.OPENWEATHERMAP_API_KEY;

        ArrayList<Weather> forecast = Weather.getForecast(forecastURL, WeatherInfoActivity.this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(WeatherInfoActivity.this);
        forecastList.setLayoutManager(linearLayoutManager);

        adapter = new RecyclerForecastAdapter(WeatherInfoActivity.this);
        forecastList.setAdapter(adapter);

        for(Weather w : forecast) {
            adapter.addItem(w);
        }

        weatherURL = "http://api.openweathermap.org/data/2.5/weather?lat="+ lat +"&lon="+ lon +"&units=metric&appid=" + SecretInfo.OPENWEATHERMAP_API_KEY;



        weather.setInfoFromURL(weatherURL);


        c = Calendar.getInstance();
        c.setTimeInMillis(System.currentTimeMillis());
        currentTime.setText((c.get(Calendar.AM_PM)== 0? "AM" : "PM") + " " + (c.get(Calendar.HOUR)==0? 12:c.get(Calendar.HOUR)) + ":" + (c.get(Calendar.MINUTE) < 10? "0" + c.get(Calendar.MINUTE) :c.get(Calendar.MINUTE)));
        (new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                while (!Thread.interrupted())
                    try
                    {
                        Thread.sleep(1000); //1초 간격으로 실행
                        runOnUiThread(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                c.setTimeInMillis(System.currentTimeMillis());
                                currentTime.setText((c.get(Calendar.AM_PM)== 0? "AM" : "PM") + " " + (c.get(Calendar.HOUR)==0? 12:c.get(Calendar.HOUR)) + ":" + (c.get(Calendar.MINUTE) < 10? "0" + c.get(Calendar.MINUTE) :c.get(Calendar.MINUTE)));
                            }
                        });
                    }
                    catch (InterruptedException e)
                    {
                        e.printStackTrace();
                    }
            }
        })).start();
        Uri imgUri = Uri.parse("http://openweathermap.org/img/wn/"+ weather.getIcon() +"@2x.png");
        Glide.with(WeatherInfoActivity.this).load(imgUri).into(weather_main);
        tv_temp.setText(weather.getTemp()+"℃");
        tv_feelsLike.setText(weather.getFeelsLike() + "℃");
        tv_humidity.setText(weather.getHumidity()+"%");
        tv_windSpeed.setText(weather.getWindSpeed()+ "m/s");

        deleteBtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                int action = event.getAction();


                if(action==MotionEvent.ACTION_DOWN) {
                    deleteBtn.setImageResource(R.drawable.delete_location_btn_pressed);
                }
                else if(action==MotionEvent.ACTION_UP){
                    deleteBtn.setImageResource(R.drawable.delete_location_btn);
                    cur.close();
                    sqlDB.close();
                    sqlDB = dbHelper.getReadableDatabase();
                    sqlDB.execSQL("delete from tbl_weather where id = " + id + ";");
                    sqlDB.close();
                    dbHelper.close();
                    Intent intent = new Intent(WeatherInfoActivity.this, WeatherMainActivity.class);
                    finish();
                    startActivity(intent);
                }

                return false;
            }
        });
    }
    @Override
    public void onBackPressed() {
        cur.close();
        sqlDB.close();
        dbHelper.close();
        Intent intent = new Intent(WeatherInfoActivity.this, WeatherMainActivity.class);
        finish();
        startActivity(intent);
    }
}
