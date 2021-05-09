package com.example.lab2_1;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.provider.BaseColumns;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import static com.example.lab2_1.RowAdapter.RECREATE_VIEW;
import static com.example.lab2_1.RowAdapter.uri;

public class MainActivity extends AppCompatActivity {
    private final ArrayList<Row> rows = new ArrayList<>();
    private final String TAG = "Provider Ops";
    private final BroadcastReceiver br = new MyBroadcastReceiver();
    private RecyclerView rvRow;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ContentResolver contentResolver = getContentResolver();

        IntentFilter filter = new IntentFilter();
        filter.addAction(RECREATE_VIEW);
        registerReceiver(br, filter);

        Cursor cursor = contentResolver.query(uri, null, null, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    int id = cursor.getInt(cursor.getColumnIndex("id"));
                    String name = cursor.getString(cursor.getColumnIndex("name"));
                    String gender = cursor.getString(cursor.getColumnIndex("gender"));
                    String department = cursor.getString(cursor.getColumnIndex("department"));
                    String salary = cursor.getString(cursor.getColumnIndex("salary"));
                    rows.add(new Row(id, name, gender, department, salary));
                } while (cursor.moveToNext());
            }
            cursor.close();
        }

        LinearLayoutManager staffInfoLayoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        RowAdapter rowAdapter = new RowAdapter(rows);
        rvRow = findViewById(R.id.rv_row);
        rvRow.setLayoutManager(staffInfoLayoutManager);
        rvRow.setAdapter(rowAdapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(br);
    }

    public static class InfoEntry implements BaseColumns {
        public static final String COLUMN_ID = "id";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_GENDER = "gender";
        public static final String COLUMN_DEPARTMENT = "department";
        public static final String COLUMN_SALARY = "salary";
    }

    private class MyBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            rvRow.setAdapter(new RowAdapter(rows));
        }
    }

}