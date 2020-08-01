package com.example.weather;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MyDBHelper extends SQLiteOpenHelper {

    public MyDBHelper(Context context) {
        super(context, "weatherDB", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("create table tbl_weather" +
                "(" +
                "   id  integer primary key autoincrement not null, " +
                "   location_name  varchar(30)  not null," +                         // 지역이름
                "   lat  double not null," +                                // 위도
                "   lon double  not null" +                                 // 경도
                ");");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("drop table if exists tbl_weather");
        onCreate(sqLiteDatabase);
    }
}
