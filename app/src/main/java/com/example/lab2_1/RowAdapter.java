package com.example.lab2_1;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import static com.example.lab2_1.Row.ADD;
import static com.example.lab2_1.Row.DEL;
import static com.example.lab2_1.Row.NON;
import static com.example.lab2_1.Row.UPD;

public class RowAdapter extends RecyclerView.Adapter<RowAdapter.RowViewHolder> {
    public static final Uri uri = Uri.parse("content://com.example.lab2_0.helper.MyContentProvider/staff");
    public static final String RECREATE_VIEW = "com.example.lab2_1.broadcast.RECREATE_VIEW";
    private static final int NAME = 1;
    private static final int GENDER = 2;
    private static final int DEPARTMENT = 3;
    private static final int SALARY = 4;
    public static List<Row> rows;
    private final String TAG = "RowAdapter";

    public RowAdapter(List<Row> rows) {
        RowAdapter.rows = rows;
    }

    @Override
    public boolean onFailedToRecycleView(@NonNull RowViewHolder holder) {
        return super.onFailedToRecycleView(holder);
    }

    @NonNull
    @Override
    public RowViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.table_row_layout, parent, false);
        return new RowViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RowViewHolder holder, int position) {
        Row row = rows.get(position);
        holder.tvId.setText(String.valueOf(row.getId()));

        holder.etName.setText(row.getName());
        holder.etName.addTextChangedListener(new MyTextWatcher(position, holder.etName, NAME));

        holder.etGender.setText(row.getGender());
        holder.etGender.addTextChangedListener(new MyTextWatcher(position, holder.etGender, GENDER));

        holder.etDepartment.setText(row.getDepartment());
        holder.etDepartment.addTextChangedListener(new MyTextWatcher(position, holder.etDepartment, DEPARTMENT));

        holder.etSalary.setText(row.getSalary());
        holder.etSalary.addTextChangedListener(new MyTextWatcher(position, holder.etSalary, SALARY));

        holder.actionSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int index, long id) {
                String selected = parent.getItemAtPosition(index).toString().trim();
                switch (selected) {
                    case "None":
                        rows.get(position).setAction(NON);
                        Log.e(TAG, "position is " + position + ", op = " + selected);
                        break;
                    case "添加":
                        rows.get(position).setAction(ADD);
                        Log.e(TAG, "position is " + position + ", op = " + selected);
                        break;
                    case "更新":
                        rows.get(position).setAction(UPD);
                        Log.e(TAG, "position is " + position + ", op = " + selected);
                        break;
                    case "删除":
                        rows.get(position).setAction(DEL);
                        Log.e(TAG, "position is " + position + ", op = " + selected);
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        holder.btnApply.setOnClickListener(v -> {
            Row element = rows.get(position);
            int id = element.getId();
            int op = element.getAction();
            ContentResolver contentResolver = v.getContext().getContentResolver();

            if (op == UPD) {
                if (element.isModified()) {
                    ContentValues cvUpd = getContentValues(element);
                    int updCode = contentResolver.update(uri, cvUpd, MainActivity.InfoEntry.COLUMN_ID + "=?", new String[]{String.valueOf(id)});
                    if (updCode == 1) {
                        updateUI(contentResolver);
                        Intent reCreate = new Intent();
                        reCreate.setAction(RECREATE_VIEW);
                        v.getContext().sendBroadcast(reCreate);
                    }
                } else {
                    Toast.makeText(v.getContext(), "您还没有做出修改！", Toast.LENGTH_SHORT).show();
                }
            } else if (op == DEL) {
                int delCode = contentResolver.delete(uri, MainActivity.InfoEntry.COLUMN_ID + "=?", new String[]{String.valueOf(id)});
                if (delCode == 1) {
                    updateUI(contentResolver);
                    Intent reCreate = new Intent();
                    reCreate.setAction(RECREATE_VIEW);
                    v.getContext().sendBroadcast(reCreate);
                }
            } else if (op == ADD) {
                ContentValues cvAdd = getContentValues(element);
                Uri addCode = contentResolver.insert(uri, cvAdd);
                Log.e(TAG, "add uri = " + addCode);
                updateUI(contentResolver);
                Intent reCreate = new Intent();
                reCreate.setAction(RECREATE_VIEW);
                v.getContext().sendBroadcast(reCreate);
            }
        });
    }

    private void updateUI(ContentResolver contentResolver) {
        notifyItemRangeRemoved(0, rows.size());
        rows.clear();
        Cursor cursor = contentResolver.query(uri, null, null, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    int newId = cursor.getInt(cursor.getColumnIndex("id"));
                    String name = cursor.getString(cursor.getColumnIndex("name"));
                    String gender = cursor.getString(cursor.getColumnIndex("gender"));
                    String department = cursor.getString(cursor.getColumnIndex("department"));
                    String salary = cursor.getString(cursor.getColumnIndex("salary"));
                    rows.add(new Row(newId, name, gender, department, salary));
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        notifyItemRangeInserted(0, rows.size());

    }

    private ContentValues getContentValues(Row element) {
        ContentValues cv = new ContentValues();
        cv.put(MainActivity.InfoEntry.COLUMN_NAME, element.getName());
        cv.put(MainActivity.InfoEntry.COLUMN_GENDER, element.getGender());
        cv.put(MainActivity.InfoEntry.COLUMN_DEPARTMENT, element.getDepartment());
        cv.put(MainActivity.InfoEntry.COLUMN_SALARY, element.getSalary());

        return cv;
    }

    @Override
    public int getItemCount() {
        return rows.size();
    }

    public static class RowViewHolder extends RecyclerView.ViewHolder {
        TextView tvId;
        EditText etName;
        EditText etGender;
        EditText etDepartment;
        EditText etSalary;
        Spinner actionSpinner;
        ImageView btnApply;

        public RowViewHolder(@NonNull View itemView) {
            super(itemView);
            tvId = itemView.findViewById(R.id.tv_id);
            etName = itemView.findViewById(R.id.et_name);
            etGender = itemView.findViewById(R.id.et_gender);
            etDepartment = itemView.findViewById(R.id.et_department);
            etSalary = itemView.findViewById(R.id.et_salary);
            actionSpinner = itemView.findViewById(R.id.action_spinner);
            btnApply = itemView.findViewById(R.id.btn_apply);
        }
    }

    private static class MyTextWatcher implements TextWatcher {
        private final int position;
        private final EditText editText;
        private final int TYPE;

        public MyTextWatcher(int position, EditText editText, int TYPE) {
            this.position = position;
            this.editText = editText;
            this.TYPE = TYPE;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            rows.get(position).setModified(true);
            editText.setBackgroundColor(Color.YELLOW);
            switch (TYPE) {
                case NAME:
                    rows.get(position).setName(editText.getText().toString());
                    break;
                case GENDER:
                    rows.get(position).setGender(editText.getText().toString());
                    break;
                case DEPARTMENT:
                    rows.get(position).setDepartment(editText.getText().toString());
                    break;
                case SALARY:
                    rows.get(position).setSalary(editText.getText().toString());
                    break;
                default:
                    break;
            }
        }
    }

}