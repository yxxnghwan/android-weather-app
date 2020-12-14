package com.example.weather;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Weather {

    private String main = "";
    private String desc = "";
    private String icon = "";
    private double temp;            // 온도
    private double feelsLike;       // 체감온도
    private double humidity;        // 습도%
    private double windSpeed;       // 풍속m/s
    private long timeMillis = System.currentTimeMillis();       // forecast를 호출할 때 시간별 정보 지정 안하면  현재시간
    Activity activity;
    public Weather(Activity activity) {
        this.activity = activity;
    }



    public String getMain() {
        return main;
    }

    public void setMain(String main) {
        this.main = main;
    }

    public String getDesc() { return desc; }

    public void setDesc(String desc) { this.desc = desc; }

    public String getIcon() { return icon; }

    public void setIcon(String icon) { this.icon = icon; }

    public double getTemp() {
        return temp;
    }

    public void setTemp(double temp) {
        this.temp = temp;
    }

    public double getFeelsLike() {
        return feelsLike;
    }

    public void setFeelsLike(double feelsLike) {
        this.feelsLike = feelsLike;
    }

    public double getHumidity() {
        return humidity;
    }

    public void setHumidity(double humidity) {
        this.humidity = humidity;
    }

    public double getWindSpeed() {
        return windSpeed;
    }

    public void setWindSpeed(double windSpeed) {
        this.windSpeed = windSpeed;
    }

    public long getTimeMillis() { return timeMillis; }

    public void setTimeMillis(long timeMillis) {
        this.timeMillis = timeMillis;
    }


    public void setInfoFromURL(String url){

        try {
            ReadJsonFromURL asyncTask =  new ReadJsonFromURL(activity);
            String receiveJSON = asyncTask.execute(new URL(url)).get();

            JSONObject weatherJSON = new JSONObject(receiveJSON);

            // 이상하게 weather라는 배열에 한개의 객체만 들어가있다. 어차피 한개 객체만 들어 있으면서, 그래서 쓰기가 복잡하다

            setMain(weatherJSON.getJSONArray("weather").getJSONObject(0).getString("main"));
            setDesc(weatherJSON.getJSONArray("weather").getJSONObject(0).getString("description"));
            setIcon(weatherJSON.getJSONArray("weather").getJSONObject(0).getString("icon"));
            setTemp(weatherJSON.getJSONObject("main").getDouble("temp"));
            setFeelsLike(weatherJSON.getJSONObject("main").getDouble("feels_like"));
            setHumidity(weatherJSON.getJSONObject("main").getDouble("humidity"));
            setWindSpeed(weatherJSON.getJSONObject("wind").getDouble("speed"));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static ArrayList<Weather> getForecast(String url, Activity activity) {
        ArrayList<Weather> forecast = new ArrayList<>();
        try {
            ReadJsonFromURL asyncTask =  new ReadJsonFromURL(activity);
            String receiveJSON = asyncTask.execute(new URL(url)).get();

            JSONObject forecastJSON = new JSONObject(receiveJSON);
            JSONArray array = forecastJSON.getJSONArray("list");
            for(int i = 0; i < 16; ++i) {
                Weather w = new Weather(activity);
                JSONObject jo = array.getJSONObject(i);
                w.setMain(jo.getJSONArray("weather").getJSONObject(0).getString("main"));
                w.setIcon(jo.getJSONArray("weather").getJSONObject(0).getString("icon"));
                w.setTemp(jo.getJSONObject("main").getDouble("temp"));
                w.setFeelsLike(jo.getJSONObject("main").getDouble("feels_like"));
                w.setHumidity(jo.getJSONObject("main").getDouble("humidity"));
                w.setWindSpeed(jo.getJSONObject("wind").getDouble("speed"));
                String tx_utc = jo.getString("dt_txt");
                Date d = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(tx_utc);
                w.setTimeMillis(d.getTime()+(9*60*60*1000)); // 한국시간은 utc시간보다 9시간 빠르다 따라서 9시간 더해서 저장해야함+(9*60*60*1000)

                forecast.add(w);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("Weather : ", "url불러오기 에러");
        }

        return forecast;
    }

    static class ReadJsonFromURL extends AsyncTask<URL, String, String> {
        Activity activity;
        LoadingDialog dialog;
        public ReadJsonFromURL(Activity activity) {
            this.activity = activity;
        }
        @Override
        protected void onPreExecute() {
            dialog = new LoadingDialog(activity);
            dialog.show();
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            super.onPreExecute();
        }
        @Override
        protected String doInBackground(URL... urls) {
            InputStream is = null;
            String jsonStr = "";
            try {
                is = urls[0].openStream();
                BufferedReader rd = new BufferedReader(new InputStreamReader(is, "UTF-8"));
                String str;
                StringBuffer buffer = new StringBuffer();
                while ((str = rd.readLine()) != null) {
                    buffer.append(str);
                }
                jsonStr = buffer.toString();
            } catch (IOException e) {
                e.printStackTrace();
                Log.d("AsyncTask : ", "url 읽기 에러");
            }
            return jsonStr;
        }
        protected void onPostExecute(String jsonStr) {
            dialog.dismiss();
            super.onPostExecute(jsonStr);
        }
    }
}
