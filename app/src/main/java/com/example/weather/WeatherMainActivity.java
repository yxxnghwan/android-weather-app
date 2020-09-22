package com.example.weather;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class WeatherMainActivity extends AppCompatActivity {

    MyDBHelper dbHelper;
    SQLiteDatabase sqlDB;
    ListView cityList;
    Button init;
    CircleImageView addCityBtn;
    ArrayList<String> nameList = new ArrayList<>();
    Cursor cur;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cityList = findViewById(R.id.cityList);
        addCityBtn = findViewById(R.id.addCityBtn);
        init = findViewById(R.id.init);

        dbHelper = new MyDBHelper(this);
        sqlDB = dbHelper.getReadableDatabase();
        cur = sqlDB.rawQuery("select * from tbl_weather;", null);

        while(cur.moveToNext()) {
            nameList.add(cur.getString(cur.getColumnIndex("location_name")));
        }


        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, nameList) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent)
            {
                View view = super.getView(position, convertView, parent);
                TextView tv = view.findViewById(android.R.id.text1);
                tv.setTextColor(Color.rgb(0,192,192));
                return view;
            }
        };

        cityList.setAdapter(adapter);

        cityList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String locationName = nameList.get(i);
                cur.close();
                sqlDB.close();
                dbHelper.close();
                Intent intent = new Intent(WeatherMainActivity.this, WeatherInfoActivity.class);
                intent.putExtra("locationName", locationName);
                finish();
                startActivity(intent);
            }
        });

        addCityBtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();

                if(action==MotionEvent.ACTION_DOWN) {
                    addCityBtn.setImageResource(R.drawable.plus_btn_pressed);
                }
                else if(action==MotionEvent.ACTION_UP){
                    addCityBtn.setImageResource(R.drawable.plus_btn);
                    cur.close();
                    sqlDB.close();
                    dbHelper.close();
                    Intent intent = new Intent(WeatherMainActivity.this, AddLocationActivity.class);
                    finish();
                    startActivity(intent);
                }

                return false;
            }

        });

        init.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dbHelper.onUpgrade(sqlDB, 0, 0);
                Toast.makeText(WeatherMainActivity.this, "테이블이 초기화되었습니다.", Toast.LENGTH_SHORT).show();
                adapter.notifyDataSetChanged();
            }
        });
    }
}
