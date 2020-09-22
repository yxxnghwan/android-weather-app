package com.example.weather;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

import java.util.ArrayList;
import java.util.List;


public class AddLocationActivity extends AppCompatActivity {


    EditText editAddr;
    Button searchBtn, addBtn;
    ListView addrListView;
    TextView tvInfo;


    ArrayList<String> addrList;
    Geocoder geocoder;
    Address selectedAddr;
    ArrayAdapter<String> adapter;
    List<Address> location;
    double lat, lon;
    Weather weather;

    MyDBHelper dbHelper;
    SQLiteDatabase sqlDB;
    InputMethodManager imm;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_location);

        imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);

        dbHelper = new MyDBHelper(AddLocationActivity.this);
        sqlDB = dbHelper.getWritableDatabase();

        editAddr = findViewById(R.id.editAddress);
        searchBtn = findViewById(R.id.searchBtn);
        addBtn = findViewById(R.id.addBtn);

        tvInfo = findViewById(R.id.tvInfo);
        addrListView = findViewById(R.id.addrListView);

        weather = new Weather(AddLocationActivity.this);

        addrList = new ArrayList<>();
        adapter = new ArrayAdapter<String>(AddLocationActivity.this, android.R.layout.simple_list_item_1, addrList);
        addrListView.setAdapter(adapter);

        geocoder = new Geocoder(AddLocationActivity.this);

        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                imm.hideSoftInputFromWindow(view.getWindowToken(),0);
                try {
                    addrList = new ArrayList<>();

                    location = geocoder.getFromLocationName(editAddr.getText().toString(),5);
                    addrList = new ArrayList<>();


                    for(Address addr : location) {
                        addrList.add(addr.getAddressLine(0));

                    }
                    adapter = new ArrayAdapter<String>(AddLocationActivity.this, android.R.layout.simple_list_item_1, addrList) {
                        @Override
                        public View getView(int position, View convertView, ViewGroup parent)
                        {
                            View view = super.getView(position, convertView, parent);
                            TextView tv = view.findViewById(android.R.id.text1);
                            tv.setTextColor(Color.rgb(0,192,192));
                            return view;
                        }
                    };
                    addrListView.setAdapter(adapter);


                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(AddLocationActivity.this, "주소를 읽지 못했습니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        addrListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                selectedAddr = location.get(i);

                String infotxt = selectedAddr.getAddressLine(0) + " 선택됨";

                tvInfo.setText(infotxt);
            }
        });

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(selectedAddr != null) {
                    lat = selectedAddr.getLatitude();
                    lon = selectedAddr.getLongitude();

                    sqlDB.execSQL("insert into tbl_weather(location_name, lat, lon)" +
                            " values('"+ selectedAddr.getAddressLine(0) + "', " + lat +", "+ lon +")");

                    Toast.makeText(AddLocationActivity.this, "해당 지역이 추가되었습니다.", Toast.LENGTH_SHORT).show();

                    dbHelper.close();
                    sqlDB.close();
                    Intent intent = new Intent(AddLocationActivity.this, WeatherMainActivity.class);
                    finish();
                    startActivity(intent);

                } else {
                    Toast.makeText(AddLocationActivity.this, "지역을 선택하세요", Toast.LENGTH_SHORT).show();
                }
            }
        });

        editAddr.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_DONE){
                    imm.hideSoftInputFromWindow(editAddr.getWindowToken(),0);
                }

                try {
                    addrList = new ArrayList<>();

                    location = geocoder.getFromLocationName(editAddr.getText().toString(),5);
                    addrList = new ArrayList<>();


                    for(Address addr : location) {
                        addrList.add(addr.getAddressLine(0));

                    }
                    adapter = new ArrayAdapter<String>(AddLocationActivity.this, android.R.layout.simple_list_item_1, addrList);
                    addrListView.setAdapter(adapter);


                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(AddLocationActivity.this, "주소를 읽지 못했습니다.", Toast.LENGTH_SHORT).show();
                }


                return false;
            }
        });
    }

    @Override
    public void onBackPressed() {
        sqlDB.close();
        dbHelper.close();
        Intent intent = new Intent(AddLocationActivity.this, WeatherMainActivity.class);
        finish();
        startActivity(intent);
    }
}
