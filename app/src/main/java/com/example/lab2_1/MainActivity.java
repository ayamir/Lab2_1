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
import java.util.List;

import static com.example.lab2_1.RowAdapter.RECREATE_VIEW;
import static com.example.lab2_1.RowAdapter.uri;

public class MainActivity extends AppCompatActivity {
    private final ArrayList<Row> rows = new ArrayList<>();
    private final BroadcastReceiver br = new ResetAdapterReceiver();
    private final Context mContext = this;
    private RecyclerView rvRow;

    public static void queryDB(ContentResolver contentResolver, List<Row> rows) {
        rows.clear();
        Cursor cursor = contentResolver.query(uri, null, null, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    int id = cursor.getInt(cursor.getColumnIndex(InfoEntry.COLUMN_ID));
                    String name = cursor.getString(cursor.getColumnIndex(InfoEntry.COLUMN_NAME));
                    String gender = cursor.getString(cursor.getColumnIndex(InfoEntry.COLUMN_GENDER));
                    String department = cursor.getString(cursor.getColumnIndex(InfoEntry.COLUMN_DEPARTMENT));
                    String salary = cursor.getString(cursor.getColumnIndex(InfoEntry.COLUMN_SALARY));
                    rows.add(new Row(id, name, gender, department, salary));
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
    }

    private void registerReceivers() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(RECREATE_VIEW);
        registerReceiver(br, filter);
    }

    private void bindItems() {
        LinearLayoutManager staffInfoLayoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        RowAdapter rowAdapter = new RowAdapter(rows);
        rvRow = findViewById(R.id.rv_row);
        rvRow.setLayoutManager(staffInfoLayoutManager);
        rvRow.setAdapter(rowAdapter);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final ContentResolver contentResolver = getContentResolver();

        registerReceivers();
        queryDB(contentResolver, rows);
        bindItems();

        Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    while (true) {
                        MyIntentService.startActionCheckNegative(mContext, contentResolver);
                        Thread.sleep(5000);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        thread.start();
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

    private class ResetAdapterReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            rvRow.setAdapter(new RowAdapter(rows));
        }
    }

}