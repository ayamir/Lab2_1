package com.example.lab2_1;

import android.app.IntentService;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import static com.example.lab2_1.RowAdapter.RECREATE_VIEW;
import static com.example.lab2_1.RowAdapter.getContentValues;
import static com.example.lab2_1.RowAdapter.rows;
import static com.example.lab2_1.RowAdapter.uri;

public class MyIntentService extends IntentService {

    private static final String ACTION_CHECK_NEGATIVE = "com.example.lab2_1.action.CHECK_NEGATIVE";
    private static final String EXTRA_PARAM2 = "ROW";

    private static final String TAG = "checkNegative";

    public MyIntentService() {
        super("MyIntentService");
    }

    public static void startActionCheckNegative(Context context, ContentResolver contentResolver) {
        Cursor cursor = contentResolver.query(uri, null, null, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    int id = cursor.getInt(cursor.getColumnIndex(MainActivity.InfoEntry.COLUMN_ID));
                    String name = cursor.getString(cursor.getColumnIndex(MainActivity.InfoEntry.COLUMN_NAME));
                    String gender = cursor.getString(cursor.getColumnIndex(MainActivity.InfoEntry.COLUMN_GENDER));
                    String department = cursor.getString(cursor.getColumnIndex(MainActivity.InfoEntry.COLUMN_DEPARTMENT));
                    String salaryString = cursor.getString(cursor.getColumnIndex(MainActivity.InfoEntry.COLUMN_SALARY));
                    Row row = new Row(id, name, gender, department, salaryString);
                    if (!salaryString.isEmpty()) {
                        double salary = Double.parseDouble(salaryString);
                        if (salary < 0) {
                            Intent intent = new Intent(context, MyIntentService.class);
                            intent.setAction(ACTION_CHECK_NEGATIVE);
                            intent.putExtra(EXTRA_PARAM2, row);
                            context.startService(intent);
                        }
                    }
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_CHECK_NEGATIVE.equals(action)) {
                final Row row = (Row) intent.getSerializableExtra(EXTRA_PARAM2);
                handleActionCheckNegative(row);
            }
        }
    }

    private void handleActionCheckNegative(Row row) {
        Log.e(TAG, "handleActionCheckNegative row: " + row.toString());
        String oriSalary = row.getSalary();
        row.setSalary("");
        ContentValues cv = getContentValues(row);
        ContentResolver contentResolver = getContentResolver();
        Log.e(TAG, "record info: "
                + "\n id = " + row.getId()
                + "\n name = " + cv.getAsString(MainActivity.InfoEntry.COLUMN_NAME)
                + "\n gender = " + cv.getAsString(MainActivity.InfoEntry.COLUMN_GENDER)
                + "\n department = " + cv.getAsString(MainActivity.InfoEntry.COLUMN_DEPARTMENT)
                + "\n original salary = " + oriSalary
        );
        int updCode = contentResolver.update(uri, cv, MainActivity.InfoEntry.COLUMN_ID + "=?", new String[]{String.valueOf(row.getId())});
        if (updCode == 1) {
            long timeStamp = System.currentTimeMillis();
            String log = "Unix TimeStamp is " + timeStamp + ", Record Info is: "
                    + "\n id = " + row.getId()
                    + "\n name = " + cv.getAsString(MainActivity.InfoEntry.COLUMN_NAME)
                    + "\n gender = " + cv.getAsString(MainActivity.InfoEntry.COLUMN_GENDER)
                    + "\n department = " + cv.getAsString(MainActivity.InfoEntry.COLUMN_DEPARTMENT)
                    + "\n original salary = " + oriSalary;
            appendLog(log);
            MainActivity.queryDB(contentResolver, rows);
            Intent reBind = new Intent();
            reBind.setAction(RECREATE_VIEW);
            sendBroadcast(reBind);
        } else {
            Log.e(TAG, "handleActionCheckNegative: update error");
        }
    }

    public void appendLog(String text) {
        File logFile = new File(getFilesDir().getAbsolutePath() + "/check.log");
        if (!logFile.exists()) {
            try {
                boolean isCreated = logFile.createNewFile();
                if (!isCreated) {
                    Log.e(TAG, "appendLog failed!");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            BufferedWriter buf = new BufferedWriter(new FileWriter(logFile, true));
            buf.append(text);
            buf.newLine();
            buf.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}