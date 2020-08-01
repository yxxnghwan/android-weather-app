package com.example.weather;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Calendar;


public class RecyclerForecastAdapter extends RecyclerView.Adapter<RecyclerForecastAdapter.ItemViewHolder> {
    // adapter에 들어갈 list 입니다.
    private ArrayList<Weather> listData = new ArrayList<>();

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // LayoutInflater를 이용하여 전 단계에서 만들었던 item.xml을 inflate 시킵니다.
        // return 인자는 ViewHolder 입니다.
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_forecast, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        // Item을 하나, 하나 보여주는(bind 되는) 함수입니다.
        holder.onBind(listData.get(position));
    }

    @Override
    public int getItemCount() {
        // RecyclerView의 총 개수 입니다.
        return listData.size();
    }

    void addItem(Weather weather) {
        // 외부에서 item을 추가시킬 함수입니다.
        listData.add(weather);
    }

    // RecyclerView의 핵심인 ViewHolder 입니다.
    // 여기서 subView를 setting 해줍니다.
    class ItemViewHolder extends RecyclerView.ViewHolder {

        private ImageView main;
        private TextView temp;
        private TextView time;

        ItemViewHolder(View itemView) {
            super(itemView);

            time = itemView.findViewById(R.id.recycler_time);
            main = itemView.findViewById(R.id.recycler_main);
            temp = itemView.findViewById(R.id.recycler_temp);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        }

        void onBind(final Weather weather) {

            Calendar c = Calendar.getInstance();
            c.setTimeInMillis(weather.getTimeMillis());
                        // (c.get(Calendar.MONTH)+1) + "월 " + c.get(Calendar.DAY_OF_MONTH) + "일 " +
            time.setText((c.get(Calendar.AM_PM)== 0? "오전" : "오후") + " " + (c.get(Calendar.HOUR)==0? 12:c.get(Calendar.HOUR)) + "시");
            switch (weather.getMain()) {
                case "Clear" :
                    if(c.get(Calendar.HOUR_OF_DAY) > 5 && c.get(Calendar.HOUR_OF_DAY) < 18) {
                        main.setImageResource(R.drawable.weather_icon_clear_sun);
                    } else {
                        main.setImageResource(R.drawable.weather_icon_clear_moon);
                    }
                    break;
                case "Drizzle" :
                    if(c.get(Calendar.HOUR_OF_DAY) > 5 && c.get(Calendar.HOUR_OF_DAY) < 18) {
                        main.setImageResource(R.drawable.weather_icon_drizzle_sun);
                    } else {
                        main.setImageResource(R.drawable.weather_icon_drizzle_moon);
                    }
                    break;
                case "Clouds" :
                    main.setImageResource(R.drawable.weather_icon_clouds);
                    break;
                case "Rain" :
                    main.setImageResource(R.drawable.weather_icon_rain);
                    break;
                case "Snow" :
                    main.setImageResource(R.drawable.weather_icon_snow);
                    break;
                case "Thunderstorm":
                    main.setImageResource(R.drawable.weather_icon_thunderstorm);
                    break;
                default:
                    main.setImageResource(R.drawable.weather_icon_other);
                    break;
            }
            temp.setText(weather.getTemp()+"℃");
        }
    }
}
