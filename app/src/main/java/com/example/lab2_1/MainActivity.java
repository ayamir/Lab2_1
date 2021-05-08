package com.example.lab2_1;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Map;

import static com.example.lab2_1.Row.ADD;
import static com.example.lab2_1.Row.DEL;
import static com.example.lab2_1.Row.UPD;
import static com.example.lab2_1.RowAdapter.actionMap;

public class MainActivity extends AppCompatActivity {
    private final ArrayList<Row> rows = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ContentResolver contentResolver = getContentResolver();
        Uri uri = Uri.parse("content://com.example.lab2_0.helper.MyContentProvider/staff");

        Cursor cursor = contentResolver.query(uri, null, null, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    String id = cursor.getString(cursor.getColumnIndex("id"));
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
        RecyclerView rvRow = findViewById(R.id.rv_row);
        rvRow.setLayoutManager(staffInfoLayoutManager);
        rvRow.setAdapter(rowAdapter);

        Button btnApply = findViewById(R.id.btn_apply);
        btnApply.setOnClickListener(view -> {
            int index = 0;
            for (Map.Entry<Integer, Integer> entry : actionMap.entrySet()) {
                Row element = rows.get(entry.getKey());
                if (element.isModified()) {
                    int op = entry.getValue();
                    switch (op) {
                        case ADD:
                            ContentValues cvAdd = getContentValues(element);
                            contentResolver.insert(uri, cvAdd);
                            break;
                        case UPD:
                            ContentValues cvUpd = getContentValues(element);
                            contentResolver.update(uri, cvUpd, InfoEntry.COLUMN_PK, new String[]{String.valueOf(index)});
                            break;
                        case DEL:
                            contentResolver.delete(uri, InfoEntry.COLUMN_PK, new String[]{String.valueOf(index)});
                            break;
                        default:
                            break;
                    }
                }
                index++;
            }
        });
    }

    private ContentValues getContentValues(Row element) {
        ContentValues cv = new ContentValues();
        cv.put(InfoEntry.COLUMN_ID, element.getId());
        cv.put(InfoEntry.COLUMN_NAME, element.getName());
        cv.put(InfoEntry.COLUMN_GENDER, element.getGender());
        cv.put(InfoEntry.COLUMN_DEPARTMENT, element.getDepartment());
        cv.put(InfoEntry.COLUMN_SALARY, element.getSalary());

        return cv;
    }

    public static class InfoEntry implements BaseColumns {
        public static final String COLUMN_PK = "pk";
        public static final String COLUMN_ID = "id";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_GENDER = "gender";
        public static final String COLUMN_DEPARTMENT = "department";
        public static final String COLUMN_SALARY = "salary";
    }

}